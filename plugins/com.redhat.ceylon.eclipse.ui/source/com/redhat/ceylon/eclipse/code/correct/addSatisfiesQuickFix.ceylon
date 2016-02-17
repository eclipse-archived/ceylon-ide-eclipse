import com.redhat.ceylon.ide.common.correct {
    AddSatisfiesQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.model.typechecker.model {
    TypeDeclaration
}

import org.eclipse.core.resources {
    IProject,
    IFile
}
import org.eclipse.jface.text {
    Region,
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    CompositeChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseAddSatisfiesQuickFix
        satisfies AddSatisfiesQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, TypeDeclaration typeParam,
        String description, String missingSatisfiedTypeText, TextChange change, 
        DefaultRegion? region) {
        
        value composite = CompositeChange(change.name);
        composite.add(change);
        
        value reg = if (exists region) then Region(region.start, region.length) else null;
        
        value proposal = AddSatisfiesProposal(typeParam, description, 
            missingSatisfiedTypeText, composite, reg);

        if (!data.proposals.contains(proposal)) {
            data.proposals.add(proposal);
        }
    }
}
