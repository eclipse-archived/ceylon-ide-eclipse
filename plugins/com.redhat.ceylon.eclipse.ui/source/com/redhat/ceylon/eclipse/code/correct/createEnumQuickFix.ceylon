import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.correct {
    CreateEnumQuickFix
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}

import org.eclipse.core.resources {
    IProject,
    IFile
}
import org.eclipse.jface.text {
    IDocument,
    Region
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    TextFileChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseCreateEnumQuickFix
        satisfies CreateEnumQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> 
                & EclipseDocumentChanges
                & EclipseAbstractQuickFix {
    
    shared actual void consumeNewQuickFix(String desc, Icons image, Integer offset, TextChange change, EclipseQuickFixData data) {
        assert(is TextFileChange change);
        value img = switch(image)
        case (Icons.classes) CeylonResources.\iCLASS
        case (Icons.interfaces) CeylonResources.\iINTERFACE
        else CeylonResources.\iATTRIBUTE;
        
        data.proposals.add(CreateEnumProposal(null, desc, img, offset, change));
    }
    
    shared actual Integer getDocLength(IDocument doc) => doc.length;
}
