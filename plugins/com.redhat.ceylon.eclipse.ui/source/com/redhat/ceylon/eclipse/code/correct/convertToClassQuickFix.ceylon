import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.correct {
    ConvertToClassQuickFix,
    AbstractConvertToClassProposal
}

import org.eclipse.core.resources {
    IFile
}
import org.eclipse.jface.text {
    Region,
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object convertToClassQuickFix
        satisfies ConvertToClassQuickFix<EclipseQuickFixData> {
            
    shared actual void newProposal(EclipseQuickFixData data, String desc,
        Tree.ObjectDefinition declaration) {
        
        data.proposals.add(EclipseConvertToClassProposal(desc, data.editor, declaration));
    }
}

class EclipseConvertToClassProposal(String desc, CeylonEditor editor,
    Tree.ObjectDefinition declaration)
            extends AbstractLinkedModeAdapter(editor)
            satisfies ICompletionProposal
                    & EclipseAbstractQuickFix
                    & EclipseDocumentChanges
                    & AbstractConvertToClassProposal<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal,LinkedModeModel>{
    
    shared actual String? additionalProposalInfo => null;
    
    shared actual void apply(IDocument doc) {
        applyChanges(doc, declaration);
        openPopup();
    }
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => desc;
    
    shared actual Point? getSelection(IDocument doc) => null;
    
    shared actual String hintTemplate => "Enter name for new class {0}";
    
    shared actual Image image => if (declaration.declarationModel.shared)
                                 then CeylonResources.\iCLASS
                                 else CeylonResources.\iLOCAL_CLASS;
    
    shared actual void performChange(TextChange change) {
        EditorUtil.performChange(change);
    }
}
