import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocumentChanges,
    eclipseImportProposals
}
import com.redhat.ceylon.eclipse.code.outline {
    CeylonLabelProvider
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources,
    CeylonPlugin
}
import com.redhat.ceylon.eclipse.util {
    Highlights
}
import com.redhat.ceylon.ide.common.completion {
    FunctionCompletionProposal
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
import org.eclipse.core.runtime {
    NullProgressMonitor
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    DocumentChange
}
import org.eclipse.swt.graphics {
    Image,
    Point
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

class EclipseFunctionCompletionProposal
        (Integer offset, String prefix, String desc, String text, Declaration declaration, Tree.CompilationUnit rootNode)
        extends FunctionCompletionProposal<ICompletionProposal, IFile, IDocument, InsertEdit, TextEdit, TextChange, Point>
        (offset, prefix, desc, text, declaration, rootNode)
        satisfies EclipseDocumentChanges & EclipseCompletionProposal {
    
    shared actual variable String? currentPrefix = prefix;
    shared actual variable Integer length = prefix.size;
    shared actual variable Boolean toggleOverwriteInternal = false;

    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;
    
    shared actual void apply(IDocument doc) {
        value change = DocumentChange("Complete Invocation", doc);
        createChange(change, doc).perform(NullProgressMonitor());
    }
    
    shared actual Image image {
        value img = declaration.shared then CeylonResources.\iCEYLON_FUN else CeylonResources.\iCEYLON_LOCAL_FUN;
        return CeylonLabelProvider.getDecoratedImage(img, CeylonLabelProvider.getDecorationAttributes(declaration), false);
    }
    
    shared actual Boolean autoInsertable => false;
    
    shared actual StyledString styledDisplayString {
        value result = StyledString();
        Highlights.styleFragment(result, 
            displayString, 
            qualifiedNameIsPath, 
            null,
            CeylonPlugin.completionFont);
        return result;
    }

    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
}