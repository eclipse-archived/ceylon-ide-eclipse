import com.redhat.ceylon.eclipse.code.complete {
    EclipseLinkedModeSupport
}
import com.redhat.ceylon.ide.common.correct {
    DeclareLocalQuickFix
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

object eclipseDeclareLocalQuickFix
        satisfies DeclareLocalQuickFix<IDocument,LinkedModeModel,ICompletionProposal>
                & EclipseLinkedModeSupport {
    
}
