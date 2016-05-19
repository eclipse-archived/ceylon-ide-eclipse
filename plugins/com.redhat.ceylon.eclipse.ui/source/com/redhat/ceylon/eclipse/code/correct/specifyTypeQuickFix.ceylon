import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.complete {
    EclipseLinkedModeSupport,
    dummyInstance
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.correct {
    SpecifyTypeQuickFix
}
import com.redhat.ceylon.ide.common.platform {
    CommonDocument
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
    LinkedModeModel
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Point,
    Image
}

object eclipseSpecifyTypeQuickFix
        satisfies SpecifyTypeQuickFix<IDocument,ICompletionProposal,LinkedModeModel>
                & EclipseLinkedModeSupport {
    
    completionManager => dummyInstance;
    
    shared actual IDocument getNativeDocument(CommonDocument doc) {
        assert(is EclipseDocument doc);
        return doc.document;
    }
}

class EclipseSpecifyTypeProposal(
    String desc, Tree.Type type,
    Tree.CompilationUnit cu, Type infType, EclipseQuickFixData data)
        satisfies ICompletionProposal & ICompletionProposalExtension6 {
    
    variable Point? selection = null;
    
    shared actual String? additionalProposalInfo => null;
    
    shared actual void apply(IDocument doc) {
        // TODO sometimes the editor was null in SpecifyTypeProposal, but here
        // that's never true 
        eclipseSpecifyTypeQuickFix.specifyType(EclipseDocument(doc), type, true, cu, infType);
    }
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => desc;
    
    shared actual Point? getSelection(IDocument? iDocument) => selection;
    
    shared actual Image image => CeylonResources.\iREVEAL;
    
    shared actual StyledString styledDisplayString
            => Highlights.styleProposal(displayString, false);
}
