import org.eclipse.ceylon.compiler.typechecker.tree {
    Tree
}
import org.eclipse.ceylon.ide.eclipse.code.correct {
    EclipseDocument
}
import org.eclipse.ceylon.ide.eclipse.code.outline {
    CeylonLabelProvider
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonResources,
    CeylonPlugin
}
import org.eclipse.ceylon.ide.eclipse.util {
    Highlights
}
import org.eclipse.ceylon.ide.common.completion {
    FunctionCompletionProposal
}
import org.eclipse.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.swt.graphics {
    Image
}

shared class EclipseFunctionCompletionProposal
        (Integer offset, String prefix, String desc, String text, Declaration declaration, Tree.CompilationUnit rootNode)
        extends FunctionCompletionProposal(offset, prefix, desc, text, declaration, rootNode)
        satisfies EclipseCompletionProposal {
    
    shared actual variable String? currentPrefix = prefix;
    shared actual variable Boolean toggleOverwriteInternal = false;
    
    shared actual void apply(IDocument doc) {
        createChange(EclipseDocument(doc)).apply();
    }
    
    shared actual Image image {
        value img = declaration.shared then CeylonResources.ceylonFun else CeylonResources.ceylonLocalFun;
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