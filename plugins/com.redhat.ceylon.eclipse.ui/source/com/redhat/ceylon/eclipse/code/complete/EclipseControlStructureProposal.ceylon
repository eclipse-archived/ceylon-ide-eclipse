
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges,
    eclipseImportProposals
}
import com.redhat.ceylon.eclipse.code.parse {
    CeylonParseController
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.completion {
    ControlStructureProposal
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.core.resources {
    IFile
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

class EclipseControlStructureProposal(Integer offset, String prefix, String desc,
    String text, Declaration declaration, CeylonParseController cpc, Node? node)
        extends ControlStructureProposal<CeylonParseController,IFile,
        ICompletionProposal,IDocument, InsertEdit, TextEdit, TextChange,Point,LinkedModeModel>
        (offset, prefix, desc, text, node, declaration, cpc)
                satisfies EclipseDocumentChanges & EclipseCompletionProposal {
            
    shared actual variable String? currentPrefix = prefix;
    
    shared actual Image image => CeylonResources.\iMINOR_CHANGE;
    
    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,
        TextEdit,TextChange> importProposals => eclipseImportProposals;
    
    shared actual ICompletionProposal newNameCompletion(String? name)
            => CompletionProposal(offset, prefix, CeylonResources.\iLOCAL_NAME, name, name);
    
    shared actual variable Boolean toggleOverwriteInternal = false;
    
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
    
    shared actual void apply(IDocument doc) {
        applyInternal(doc);
    }
}
