import com.redhat.ceylon.eclipse.code.complete {
    RefinementCompletionProposal
}
import com.redhat.ceylon.ide.common.correct {
    AddThrowsAnnotationQuickFix,
    RefineEqualsHashQuickFix
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
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
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object addThrowsAnnotationQuickFix
        satisfies AddThrowsAnnotationQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
    
    addAnnotationsQuickFix => eclipseAnnotationsQuickFix;
}

object refineEqualsHashQuickFix
        satisfies RefineEqualsHashQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal>
        & EclipseGenericQuickFix {
    
    shared actual void newProposal(EclipseQuickFixData data, String desc, 
        TextChange _change, DefaultRegion? region) { 
        
        value proposal = object extends CorrectionProposal(desc, _change, 
                null, RefinementCompletionProposal.\iDEFAULT_REFINEMENT) {
             
             styledDisplayString =>
                     let(hint=CorrectionUtil.shortcut("com.redhat.ceylon.eclipse.ui.action.refineEqualsHash"))
                     super.styledDisplayString.append(hint, StyledString.\iQUALIFIER_STYLER);
        };
        data.proposals.add(proposal); 
    }

}
