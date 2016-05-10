import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.complete {
    EclipseLinkedModeSupport
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
import com.redhat.ceylon.model.typechecker.model {
    Type
}

import org.eclipse.core.resources {
    IFile
}
import org.eclipse.jface.text {
    Region,
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
import org.eclipse.ltk.core.refactoring {
    TextChange
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

object eclipseSpecifyTypeQuickFix
        satisfies SpecifyTypeQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,EclipseQuickFixData,ICompletionProposal,LinkedModeModel>
                & EclipseAbstractQuickFix 
                & EclipseDocumentChanges
                & EclipseLinkedModeSupport {
    
    shared actual void newSpecifyTypeProposal(String desc, Tree.Type type, 
        Tree.CompilationUnit cu, Type infType, EclipseQuickFixData data) {
        
        data.proposals.add(EclipseSpecifyTypeProposal(desc, type, cu, infType, data));
    }
    
    specifyTypeArgumentsQuickFix => eclipseSpecifyTypeArgumentsQuickFix;
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
        eclipseSpecifyTypeQuickFix.specifyType(doc, type, true, cu, infType);
    }
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => desc;
    
    shared actual Point? getSelection(IDocument? iDocument) => selection;
    
    shared actual Image image => CeylonResources.\iREVEAL;
    
    shared actual StyledString styledDisplayString
            => Highlights.styleProposal(displayString, false);
}
