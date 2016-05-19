import com.redhat.ceylon.ide.common.correct {
    IdeQuickFixManager
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}

object eclipseQuickFixManager
        extends IdeQuickFixManager<IDocument,ICompletionProposal,LinkedModeModel,EclipseQuickFixData>() {
    
    specifyTypeQuickFix => eclipseSpecifyTypeQuickFix;
    declareLocalQuickFix => eclipseDeclareLocalQuickFix;

}
