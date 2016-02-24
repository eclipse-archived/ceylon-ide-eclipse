import com.redhat.ceylon.eclipse.code.complete {
    EclipseLinkedModeSupport
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.code.refactor {
    AbstractLinkedMode
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.link {
    LinkedModeModel
}

abstract class AbstractLinkedModeAdapter(CeylonEditor ceylonEditor)
        extends AbstractLinkedMode(ceylonEditor) 
        satisfies EclipseLinkedModeSupport {
    
    shared actual void installLinkedMode(IDocument doc, LinkedModeModel lm, 
        Object owner, Integer exitSeqNumber, Integer exitPosition) {
        
        enterLinkedMode(doc, exitSeqNumber, exitPosition);
    }
    
    shared actual LinkedModeModel newLinkedMode() => linkedModeModel;
}