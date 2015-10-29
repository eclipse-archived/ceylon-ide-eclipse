import com.redhat.ceylon.ide.common.correct {
    AddAnnotationQuickFix,
    RemoveAnnotationQuickFix
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable,
    Declaration
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
import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
}
import com.redhat.ceylon.eclipse.code.imports {
    eclipseModuleImportUtils
}

object eclipseAnnotationsQuickFix
        satisfies AddAnnotationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & RemoveAnnotationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseDocumentChanges & EclipseAbstractQuickFix {
    
    shared actual void newAddAnnotationQuickFix(Referenceable dec, String text,
        String desc, Integer offset, TextChange change, Region? selection, EclipseQuickFixData data) {
        
        data.proposals.add(AddAnnotionProposal(dec, text, desc, offset, change, selection));
    }

    shared actual void newRemoveAnnotationQuickFix(Declaration dec, String annotation,
        String desc, Integer offset, TextChange change, Region selection, EclipseQuickFixData data) {
        
        assert(is TextFileChange change);
        data.proposals.add(RemoveAnnotionProposal(dec, annotation, offset, desc, change));
    }
    
    shared actual AbstractModuleImportUtil<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange> moduleImportUtil
            => eclipseModuleImportUtils;
    
    shared actual void newCorrectionQuickFix(String desc, TextChange change,
        Region? selection)
            => CorrectionProposal(desc, change, selection);
    
}