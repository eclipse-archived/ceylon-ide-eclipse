import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.complete {
    dummyInstance
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.correct {
    specifyTypeQuickFix
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
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Point,
    Image
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
        specifyTypeQuickFix.specifyType {
            document = EclipseDocument(doc);
            typeNode = type;
            inEditor = true;
            rootNode = cu;
            _infType = infType;
            nativeDocument = doc;
            completionManager = dummyInstance;
        };
    }
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => desc;
    
    shared actual Point? getSelection(IDocument? iDocument) => selection;
    
    shared actual Image image => CeylonResources.reveal;
    
    shared actual StyledString styledDisplayString
            => Highlights.styleProposal(displayString, false);
}
