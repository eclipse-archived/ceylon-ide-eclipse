import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.correct {
    AppendMemberReferenceQuickFix
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
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseAppendMemberReferenceQuickFix
        satisfies AppendMemberReferenceQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        TextChange change, Integer offset, Integer length) {
        
        data.proposals.add(EclipseAppendMemberReferenceProposal(desc, change,
            Region(offset, length)));
    }
}

class EclipseAppendMemberReferenceProposal(String desc, TextChange change, Region region)
        extends CorrectionProposal(desc, change, region, CeylonResources.\iMINOR_CHANGE) {
    
    styledDisplayString => Highlights.styleProposal(displayString, true);
}