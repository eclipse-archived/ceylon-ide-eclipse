import com.redhat.ceylon.eclipse.code.imports {
    eclipseModuleImportUtils
}
import com.redhat.ceylon.ide.common.correct {
    AddAnnotationQuickFix,
    RemoveAnnotationQuickFix
}
import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
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
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

shared object eclipseAnnotationsQuickFix
        satisfies AddAnnotationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & RemoveAnnotationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseDocumentChanges
                & EclipseAbstractQuickFix {
    
    shared actual void newAddAnnotationQuickFix(Referenceable dec, String text,
        String desc, Integer offset, TextChange change, Region? selection, EclipseQuickFixData data) {
        
        data.proposals.add(AddRemoveAnnotionProposal(dec, text, desc, change, selection));
    }

    shared actual void newRemoveAnnotationQuickFix(Declaration dec, String annotation,
        String desc, Integer offset, TextChange change, Region selection, EclipseQuickFixData data) {
        
        data.proposals.add(AddRemoveAnnotionProposal(dec, annotation, desc, change, selection));
    }
    
    shared actual AbstractModuleImportUtil<IFile,IProject,IDocument,InsertEdit,TextEdit,TextChange> moduleImportUtil
            => eclipseModuleImportUtils;
    
    shared actual void newCorrectionQuickFix(String desc, TextChange change,
        Region? selection)
            => CorrectionProposal(desc, change, selection);
    
}

class AddRemoveAnnotionProposal(dec, annotation, desc, change, region)
        extends CorrectionProposal(desc, change, region) {
    
    Referenceable dec;
    String annotation;
    String desc;
    TextChange change;
    Region? region;
    
    shared actual Boolean equals(Object obj) {
        if (is AddRemoveAnnotionProposal obj) {
            value that = obj;
            return that.dec.equals(dec) && that.annotation.equals(annotation);
        } else {
            return super.equals(obj);
        }
    }
    
    shared actual Integer hash => dec.hash;
}