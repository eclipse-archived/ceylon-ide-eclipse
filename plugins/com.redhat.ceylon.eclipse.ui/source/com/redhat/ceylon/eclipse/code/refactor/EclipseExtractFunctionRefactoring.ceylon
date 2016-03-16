import ceylon.collection {
    ArrayList
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
    FindBodyVisitor
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Type,
    TypedDeclaration
}

import org.eclipse.core.resources {
    IFile
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
    RefactoringStatus { ... },
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.ui {
    IEditorPart
}

class EclipseExtractFunctionRefactoring(IEditorPart editorPart, target = null) 
        extends EclipseAbstractRefactoring<TextChange>(editorPart)
        satisfies ExtractFunctionRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion>
        & EclipseDocumentChanges
        & EclipseExtractLinkedModeEnabled {
    
    shared actual Tree.Declaration? target;
    
    importProposals => eclipseImportProposals;
    
    shared actual variable String? internalNewName=null;
    shared actual variable Boolean canBeInferred=false;
    shared actual variable Boolean explicitType=false;
    shared actual variable Type? type=null;
    shared actual variable IRegion? typeRegion=null;
    shared actual variable IRegion? decRegion=null;
    shared actual variable IRegion? refRegion=null;
    shared actual variable Node? result = null;
    shared actual variable TypedDeclaration? resultDeclaration = null;
    shared actual variable Type? returnType = null;
    shared actual variable List<Tree.Return> returns = empty;
    shared actual variable List<Tree.Statement> statements = empty;
    shared actual variable Tree.Body? body = null;
    
    if (!is CeylonEditor editorPart) {
        return;
    }
    
    value selection = EditorUtil.getSelection(editorPart);
    value rootNode = editorPart.parseController.typecheckedRootNode;
    value node = nodes.findNode(rootNode, editorPart.parseController.tokens, 
        selection.offset,
        selection.offset+selection.length);
    
    //additional initialization for extraction of statements
    //as opposed to extraction of an expression
    
    if (is Tree.Body node) {
        body = node;
        value statementsList = ArrayList<Tree.Statement>();
        for (s in node.statements) {
            if (s.startIndex.intValue() >= selection.offset, 
                s.endIndex.intValue() <= selection.offset+selection.length) {
                statementsList.add(s);
            }
        }
        statements = statementsList;
        for (s in statements) {
            value v = FindResultVisitor(node, statements);
            s.visit(v);
            if (v.result exists) {
                result = v.result;
                resultDeclaration = v.resultDeclaration;
                break;
            }
        }
    }
    else if (is Tree.Statement node) {
        value fbv = FindBodyVisitor(node);
        fbv.visit(rootNode);
        if (exists bodyNode = fbv.body) {
            body = bodyNode;
            statements = Singleton(node);
            //node = body;
            //TODO: DUPE CODE!
            for (s in statements) {
                value v = FindResultVisitor(bodyNode, statements);
                s.visit(v);
                if (v.result exists) {
                    result = v.result;
                    resultDeclaration = v.resultDeclaration;
                    break;
                }
            }
        }
        else {
            return;
        }
    }
    else {
        return;
    }
        
    value returnsList = ArrayList<Tree.Return>();
    for (s in statements) {
        value v = FindReturnsVisitor(returnsList);
        s.visit(v);
    }
    returns = returnsList;

    
    checkFinalConditions(IProgressMonitor? monitor)
            => if (exists node = editorData?.node,
                   exists mop = node.scope.getMemberOrParameter(node.unit, newName, null, false))
            then createWarningStatus(
                    "An existing declaration named '``newName``' is already visible this scope")
            else RefactoringStatus();
    
    shared actual RefactoringStatus checkInitialConditions(IProgressMonitor pm) {
        if (exists node = editorData?.node, 
            exists body = this.body) {
            if (is Tree.Body|Tree.Statement node) {
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
        }
        else {
            return createErrorStatus("No selected statements or expression");
        }
        
        return RefactoringStatus();
    }
    
    shared actual TextChange createChange(IProgressMonitor? monitor) {
        TextChange tc = newLocalChange();
        extractInFile(tc);
        return tc;
    }
    
    newRegion(Integer start, Integer length) => Region(start, length);
    
    extractInFile(TextChange tfc) => build(tfc);
    
    shared actual String name => (super of ExtractFunctionRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion>).name;
}
