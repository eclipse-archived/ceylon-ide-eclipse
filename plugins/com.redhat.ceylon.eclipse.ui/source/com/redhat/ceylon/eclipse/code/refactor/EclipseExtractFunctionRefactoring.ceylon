import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree,
    Visitor
}
import com.redhat.ceylon.eclipse.code.correct {
    eclipseImportProposals,
    EclipseDocumentChanges
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.refactoring {
    ExtractFunctionRefactoring,
    FindReturnsVisitor,
    FindResultVisitor,
    FindBodyVisitor,
    getTargetNode
}
import com.redhat.ceylon.ide.common.typechecker {
    ProjectPhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Type,
    TypedDeclaration
}

import java.util {
    JList=List,
    JArrayList=ArrayList
}

import org.eclipse.core.resources {
    IFile,
    IProject,
    IResource,
    IFolder
}
import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.jface.text {
    IRegion,
    IDocument,
    Region
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    RefactoringStatus {
        ...
    },
    TextChange,
    Change,
    CompositeChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

class EclipseExtractFunctionRefactoring(CeylonEditor editorPart, target = null) 
        extends EclipseAbstractRefactoring<TextChange>(editorPart)
        satisfies ExtractFunctionRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, CompositeChange, IRegion>
        & EclipseDocumentChanges
        & EclipseExtractLinkedModeEnabled {
    
    shared actual Tree.Declaration? target;
    
    importProposals => eclipseImportProposals;
    
    shared actual variable String? internalNewName = null;
    shared actual variable Boolean canBeInferred = false;
    shared actual variable Boolean explicitType = false;
    shared actual variable Type? type = null;
    shared actual variable IRegion? typeRegion = null;
    shared actual variable IRegion? decRegion = null;
    shared actual variable IRegion? refRegion = null;
    shared actual variable List<Node->TypedDeclaration> results = [];
    shared actual variable List<Tree.Return> returns = [];
    shared actual variable List<Tree.Statement> statements = [];
    shared actual variable Tree.Body? body = null;
    shared actual JList<IRegion> dupeRegions = JArrayList<IRegion>();
    
    value selection = EditorUtil.getSelection(editorPart);
    function selected(Node node)
            => node.startIndex.intValue() >= selection.offset && 
            node.endIndex.intValue()   <= selection.offset + selection.length;
    
    Tree.CompilationUnit? rootNode 
            = editorPart.parseController
                .typecheckedRootNode;
    if (!exists rootNode) {
        return;
    }
    
    value node = nodes.findNode {
        node = rootNode;
        tokens = editorPart.parseController.tokens;
        startOffset = selection.offset;
        endOffset = selection.offset+selection.length;
    };
    
    //additional initialization for extraction of statements
    //as opposed to extraction of an expression
    
    Tree.Body bodyNode;
    switch (node)
    case (null) {
        return;
    }
    case (is Tree.Term) {
        //we're extracting a single expression
        return;
    }
    case (is Tree.Body) {
        //we're extracting multiple statements
        statements 
                = [ for (s in node.statements) 
                    if (selected(s)) 
                    s ];
        bodyNode = node;
    }
    else {
        value statement 
                = nodes.findStatement(rootNode, node);
        if (!exists statement) {
            return;
        }
        //we're extracting a single statement
        value fbv = FindBodyVisitor(statement);
        fbv.visit(rootNode);
        if (exists found = fbv.body) {
            statements = [statement];
            bodyNode = found;
            //node = body;
        }
        else {
            return;
        }
    }
    body = bodyNode;
    
    value resultsVisitor = FindResultVisitor {
        scope = bodyNode;
        statements = statements;
    };
    for (s in statements) {
        s.visit(resultsVisitor);
    }
    results = resultsVisitor.results;
    
    value returnsVisitor = FindReturnsVisitor();
    for (s in statements) {
        s.visit(returnsVisitor);
    }
    returns = returnsVisitor.returns;
    
    checkFinalConditions(IProgressMonitor? monitor)
            => let(node = editorData.node) 
            if (exists mop = node.scope.getMemberOrParameter(node.unit, newName, null, false))
            then createWarningStatus(
                    "An existing declaration named '``newName``' is already visible this scope")
            else RefactoringStatus();
    
    shared actual RefactoringStatus checkInitialConditions(IProgressMonitor pm) {
        value node = editorData.node;
        if (is Tree.Body|Tree.Statement node, 
            exists body = this.body) {
            for (s in statements) {
                value v = CheckStatementsVisitor(body, statements);
                s.visit(v);
                if (exists msg = v.problem) {
                    return createWarningStatus("Selected statements contain " + msg + " at  " + s.location);
                }
            }
        }
        else if (is Tree.Term node) {
            variable String? problem = null;
            node.visit(object extends Visitor() {
                shared actual void visit(Tree.Body that) {}
                shared actual void visit(Tree.AssignmentOp that) {
                    problem = "an assignment";
                    super.visit(that);
                }
            });
            if (exists msg = problem) {
                return createWarningStatus("Selected expression contains " + msg);
            }
        }
        else {
            return createErrorStatus("No selected statements or expression");
        }
        
        return RefactoringStatus();
    }
    
    shared actual TextChange newFileChange(PhasedUnit pu) {
        assert (is ProjectPhasedUnit<IProject,IResource,IFolder,IFile> pu);
        return newTextFileChange(pu);
    }
    
    addChangeToChange(CompositeChange change, TextChange tc) => change.add(tc);
    
    shared actual Change createChange(IProgressMonitor? monitor) {
        if (is Tree.Term term = editorData.node,
            exists tn = getTargetNode(term, target, editorData.rootNode), 
            tn.declarationModel.toplevel) {
            //if we're extracting a toplevel function, look for
            //replacements in other files in the same package
            value change = CompositeChange(name);
            TextChange tc = newLocalChange();
            extractExpression(tc, term, change);
            if (change.children.size==0) {
                return tc;
            }
            else {
                addChangeToChange(change, tc);
                return change;
            }
        }
        else {
            TextChange tc = newLocalChange();
            extractInFile(tc);
            return tc;
        }
    }
    
    newRegion(Integer start, Integer length) => Region(start, length);
    
    extractInFile(TextChange tfc) => build(tfc);
    
    shared actual String name => (super of ExtractFunctionRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, CompositeChange, IRegion>).name;
}
