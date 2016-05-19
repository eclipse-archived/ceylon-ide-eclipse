import com.redhat.ceylon.eclipse.code.complete {
    EclipseLinkedModeSupport
}
import com.redhat.ceylon.eclipse.code.refactor {
    AbstractLinkedMode
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.correct {
    AbstractLocalProposal
}
import com.redhat.ceylon.model.typechecker.model {
    Type
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    ICompletionProposalExtension6,
    IContextInformation
}
import org.eclipse.jface.text.link {
    LinkedModeModel,
    LinkedPosition
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Point
}

abstract class EclipseLocalProposal(EclipseQuickFixData data, shared actual String displayString)
        extends AbstractLinkedMode(data.editor)
        satisfies AbstractLocalProposal<IDocument,LinkedModeModel>
                & EclipseLinkedModeSupport
                & ICompletionProposal & ICompletionProposalExtension6 {
    
    shared actual variable Integer currentOffset = -1;
    
    shared actual variable Integer exitPos = 0;
    
    shared actual variable {String*} names = empty;
    
    shared actual variable Integer offset = 0;
    
    shared actual variable Type? type = null;
    
    hintTemplate => "Enter type and name for new local {0}";
    
    newLinkedMode() => linkedModeModel;
    
    shared actual void updatePopupLocation() {
        LinkedPosition? pos = currentLinkedPosition;
        value popup = infoPopup;
        if (!exists pos) {
            popup.setHintTemplate(hintTemplate);
        } else if (pos.sequenceNumber == 1) {
            popup.setHintTemplate("Enter type for new local {0}");
        } else {
            popup.setHintTemplate("Enter name for new local {0}");
        }
    }
    
    shared actual void apply(IDocument doc) {
        currentOffset = data.editor.selection.offset;
        
        value change = performInitialChange(data, currentOffset);
        
        if (exists change) {
            change.apply();
            value unit = data.editor.parseController.lastCompilationUnit.unit;
            if (exists lm = addLinkedPositions(doc, unit)) {
                enterLinkedMode(doc, 2, exitPosition);
                openPopup();
            }
        }
    }
    
    Integer exitPosition => exitPos + initialName.size + 9;
    
    shared actual default StyledString styledDisplayString
            => Highlights.styleProposal(displayString, false, true);
    
    shared actual Point? getSelection(IDocument? doc) => null;
    
    shared actual String? additionalProposalInfo => null;
    
    image => CeylonResources.\iMINOR_CHANGE;
    
    shared actual IContextInformation? contextInformation => null;
}
