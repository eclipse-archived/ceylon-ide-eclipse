
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocument
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.ide.common.completion {
    ControlStructureProposal
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.swt.graphics {
    Image
}

shared class EclipseControlStructureProposal(Integer offset, String prefix, String desc,
    String text, Declaration declaration, LocalAnalysisResult cpc, Node? node)
        extends ControlStructureProposal
        (offset, prefix, desc, text, node, declaration, cpc)
                satisfies EclipseCompletionProposal {
            
    shared actual variable String? currentPrefix = prefix;
    
    shared actual Image image => CeylonResources.minorChange;
    
    shared actual variable Boolean toggleOverwriteInternal = false;
    
    shared actual Boolean toggleOverwrite => toggleOverwriteInternal;
    
    shared actual void apply(IDocument doc) => applyInternal(EclipseDocument(doc));
}
