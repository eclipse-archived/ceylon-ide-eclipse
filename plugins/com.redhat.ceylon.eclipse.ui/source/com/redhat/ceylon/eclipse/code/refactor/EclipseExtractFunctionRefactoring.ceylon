import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree,
    Visitor
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.refactoring {
    getTargetNode,
    prepareExtractFunction,
    DeprecatedExtractFunctionRefactoring
}
import com.redhat.ceylon.ide.common.typechecker {
    ProjectPhasedUnit
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
        satisfies DeprecatedExtractFunctionRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, CompositeChange, IRegion>
        & EclipseDocumentChanges
        & EclipseExtractLinkedModeEnabled {
    
    shared actual variable String? internalNewName = null;
    shared actual variable Boolean canBeInferred = false;
    shared actual variable Boolean explicitType = false;
    shared actual variable Type? type = null;
    shared actual variable IRegion? typeRegion = null;
    shared actual variable IRegion? decRegion = null;
    shared actual variable IRegion? refRegion = null;
    shared Tree.Declaration? target;
    shared actual JList<IRegion> dupeRegions = JArrayList<IRegion>();
    
    value selection = EditorUtil.getSelection(editorPart);
    
    Tree.CompilationUnit? rootNode 
            = editorPart.parseController
                .typecheckedRootNode;
    
    value data = if (exists rootNode)
                 then prepareExtractFunction(rootNode, editorPart.parseController.tokens, 
                    selection.offset, selection.offset + selection.length)
                 else [null, [], [], [], null];    
    
    variable <EclipseEditorData&ExtractFunctionData>? lazyData = null;
    
    function createData()
            => object extends EclipseEditorData(editorPart) satisfies ExtractFunctionData {
        shared actual Tree.Body? body => data[4];
        
        shared actual Node node => data[0] else nothing; // "should never happen"
        
        shared actual List<Node->TypedDeclaration> results => data[1];
        
        shared actual List<Tree.Return> returns => data[2];
        
        shared actual List<Tree.Statement> statements => data[3];
        
        shared actual Tree.Declaration? target => outer.target;
    };

    editorData => lazyData else (lazyData = createData());
        
    checkFinalConditions(IProgressMonitor? monitor)
            => let(node = editorData.node) 
            if (exists mop = node.scope.getMemberOrParameter(node.unit, newName, null, false))
            then createWarningStatus(
                    "An existing declaration named '``newName``' is already visible this scope")
            else RefactoringStatus();
    
    shared actual RefactoringStatus checkInitialConditions(IProgressMonitor pm) {
        value node = editorData.node;
        if (is Tree.Body|Tree.Statement node, 
            exists body = editorData.body) {
            for (s in editorData.statements) {
                value v = CheckStatementsVisitor(body, editorData.statements);
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
    
    shared actual String name => (super of DeprecatedExtractFunctionRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, CompositeChange, IRegion>).name;
}
