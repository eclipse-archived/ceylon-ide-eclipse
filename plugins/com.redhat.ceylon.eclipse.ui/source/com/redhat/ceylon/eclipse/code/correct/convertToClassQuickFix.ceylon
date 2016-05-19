import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.correct {
    AbstractConvertToClassProposal
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}
import org.eclipse.swt.graphics {
    Point,
    Image
}


class EclipseConvertToClassProposal(String desc, CeylonEditor editor,
    Tree.ObjectDefinition declaration)
            extends AbstractLinkedModeAdapter(editor)
            satisfies ICompletionProposal
                    & AbstractConvertToClassProposal<ICompletionProposal,IDocument,LinkedModeModel>{
    
    shared actual String? additionalProposalInfo => null;
    
    shared actual void apply(IDocument doc) {
        applyChanges(EclipseDocument(doc), declaration);
        openPopup();
    }
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => desc;
    
    shared actual Point? getSelection(IDocument doc) => null;
    
    shared actual String hintTemplate => "Enter name for new class {0}";
    
    shared actual Image image => if (declaration.declarationModel.shared)
                                 then CeylonResources.\iCLASS
                                 else CeylonResources.\iLOCAL_CLASS;
    
    shared actual IDocument getNativeDocument(CommonDocument doc) {
        assert(is EclipseDocument doc);
        return doc.document;
    }    
}
