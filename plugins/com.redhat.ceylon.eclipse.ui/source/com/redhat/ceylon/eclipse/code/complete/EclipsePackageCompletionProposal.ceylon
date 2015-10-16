import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges,
    eclipseImportProposals
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.completion {
    ImportedModulePackageProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Declaration
}

import org.eclipse.core.resources {
    IFile
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    IContextInformation
}
import org.eclipse.jface.text.link {
    LinkedModeModel,
    ILinkedModeListener
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

class EclipseImportedModulePackageProposal(Integer offset, String prefix, String memberPackageSubname, Boolean withBody,
                String fullPackageName, CeylonParseController controller, Package candidate)
                extends ImportedModulePackageProposal<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, Point, LinkedModeModel>
                (offset, prefix, memberPackageSubname, withBody, fullPackageName, candidate)
                satisfies EclipseDocumentChanges & EclipseCompletionProposal{

    shared actual variable String? currentPrefix = prefix;
    shared actual variable Integer length = prefix.size;
    shared actual variable Boolean toggleOverwriteInternal = false;
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;

    shared actual Boolean qualifiedNameIsPath => true;
    
    shared actual Image image => CeylonResources.\iPACKAGE;
    
    shared actual ICompletionProposal newPackageMemberCompletionProposal(Declaration d, Point selection, LinkedModeModel lm) {
        return object satisfies ICompletionProposal {
            shared actual Point? getSelection(IDocument document) {
                return null;
            }
            shared actual Image image => CeylonLabelProvider.getImageForDeclaration(d);

            shared actual String displayString => d.name;

            shared actual IContextInformation? contextInformation => null;
            
            shared actual String? additionalProposalInfo => null;
            
            shared actual void apply(IDocument document) {
                document.replace(selection.x, selection.y, d.name);
                lm.exit(ILinkedModeListener.\iUPDATE_CARET);
            }
        };
    }
}