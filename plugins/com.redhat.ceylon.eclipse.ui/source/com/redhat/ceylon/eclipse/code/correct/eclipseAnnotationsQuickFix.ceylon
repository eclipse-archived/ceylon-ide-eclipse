import ceylon.interop.java {
    CeylonList
}

import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents
}
import com.redhat.ceylon.ide.common.correct {
    AddAnnotationQuickFix,
    RemoveAnnotationQuickFix,
    ImportProposals
}
import com.redhat.ceylon.ide.common.util {
    Indents
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
import org.eclipse.ltk.core.refactoring {
    TextChange,
    TextFileChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}

object eclipseAnnotationsQuickFix
        satisfies AddAnnotationQuickFix<IFile,IDocument, InsertEdit, TextEdit, TextChange, Region, IProject, EclipseQuickFixData,ICompletionProposal>
                & RemoveAnnotationQuickFix<IFile,IDocument, InsertEdit, TextEdit, TextChange, Region, IProject, EclipseQuickFixData,ICompletionProposal>
                & EclipseDocumentChanges {
    
    shared actual Integer getTextEditOffset(TextEdit change) => change.offset;
    
    shared actual List<PhasedUnit> getUnits(IProject p) => CeylonList(CeylonBuilder.getUnits(p));
    
    shared actual Indents<IDocument> indents => eclipseIndents;
    
    shared actual void newAddAnnotationQuickFix(Referenceable dec, String text,
        String desc, Integer offset, TextChange change, Region? selection, EclipseQuickFixData data) {
        
    }

    shared actual void newRemoveAnnotationQuickFix(Declaration dec, String annotation,
        String desc, Integer offset, TextChange change, Region selection, EclipseQuickFixData data) {
        
        assert(is TextFileChange change);
        data.proposals.add(RemoveAnnotionProposal(dec, annotation, offset, change));
    }
    
    shared actual Region newRegion(Integer start, Integer length) => Region(start, length);
    
    shared actual TextChange newTextChange(String desc, PhasedUnit|IFile u) {
        value file = if (is PhasedUnit u) then CeylonBuilder.getFile(u) else u;
        return TextFileChange(desc, file);
    }
    shared actual IdeCompletionManager<out Anything,out Anything,ICompletionProposal,IDocument> completionManager => nothing;
    
    shared actual ImportProposals<out Anything,out Anything,IDocument,InsertEdit,TextEdit,TextChange> importProposals => nothing;
    
}