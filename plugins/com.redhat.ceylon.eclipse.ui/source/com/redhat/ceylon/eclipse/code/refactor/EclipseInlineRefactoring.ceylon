import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
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
    InlineRefactoring,
    isInlineRefactoringAvailable
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    CompositeChange,
    RefactoringStatus
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.ui {
    IEditorPart
}

EclipseInlineRefactoring? newEclipseInlineRefactoring(IEditorPart editor) {
    if (is CeylonEditor editor,
        exists rootNode 
            = editor.parseController.typecheckedRootNode) {
        value selection = EditorUtil.getSelection(editor);
        value node = nodes.findNode {
            node = rootNode;
            tokens = editor.parseController.tokens;
            startOffset = selection.offset;
            endOffset = selection.offset+selection.length;
        };
        if (is Declaration decl = nodes.getReferencedDeclaration(node), 
            isInlineRefactoringAvailable(decl, rootNode, inSameProject(decl, editor))) {
            return EclipseInlineRefactoring(editor, decl).init();
        }
    }
    return null;
}

class EclipseInlineRefactoring(CeylonEditor editorPart, shared Declaration declaration)
        extends EclipseAbstractRefactoring<CompositeChange>(editorPart)
        satisfies InlineRefactoring<ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange,CompositeChange>
                & EclipseDocumentChanges {

    variable Boolean delete = true;
    variable Boolean justOne = false;
    
    shared void toggleDelete() => delete = !delete;
    shared void toggleJustOne() => justOne = !justOne;

    shared class EclipseInlineData(CeylonEditor editor)
            extends EclipseEditorData(editor) satisfies InlineData {
        
        shared actual Declaration declaration => outer.declaration;
        
        shared actual Boolean delete => outer.delete;
        
        shared actual Boolean justOne => outer.justOne;
        
        shared actual IDocument doc {
            if (exists _ = super.document) {
                return _;
            }
            throw Exception("Can't find document");
        }
    }
    
    shared actual late EclipseInlineData editorData;
    
    shared EclipseInlineRefactoring init() {
        editorData = EclipseInlineData(editorPart);
        return this;
    }

    addChangeToChange(CompositeChange change, TextChange tc)
            => change.add(tc);
    
    shared actual RefactoringStatus checkFinalConditions(IProgressMonitor mon)
            => RefactoringStatus();
    
    shared actual RefactoringStatus checkInitialConditions(IProgressMonitor mon) {
        value res = checkAvailability();
        
        if (is String res) {
            return RefactoringStatus.createFatalErrorStatus(res);
        } else {
            value status = RefactoringStatus();
            
            for (warning in res) {
                status.merge(RefactoringStatus.createWarningStatus(warning));
            }
            return status;
        }
    }

    createChange(IProgressMonitor? iProgressMonitor)
            => build(CompositeChange(name));

    newFileChange(PhasedUnit pu) => newTextFileChange(pu);
    
    newDocChange(IDocument doc) => newDocumentChange();
    
    shared actual String name => (super of InlineRefactoring<ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange,CompositeChange>).name;
}