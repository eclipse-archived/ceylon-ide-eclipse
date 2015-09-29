import com.redhat.ceylon.ide.common.correct {
    RefineFormalMembersQuickFix,
    ImportProposals,
    getRefineFormalMembersScope
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    DocumentChange,
    PerformChangeOperation
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    ICompletionProposalExtension6,
    IContextInformation
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import java.lang {
    Character
}
import org.eclipse.swt.graphics {
    Point,
    Image
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import org.eclipse.jface.viewers {
    StyledString
}
import com.redhat.ceylon.eclipse.code.complete {
    EclipseCompletionManager,
    RefinementCompletionProposal
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil,
    eclipseIndents,
    Highlights
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import org.eclipse.jface.bindings {
    TriggerSequence
}
import org.eclipse.core.resources {
    ResourcesPlugin
}
import org.eclipse.core.runtime {
    NullProgressMonitor
}
import java.util {
    Collection
}
import ceylon.interop.java {
    CeylonIterable
}

void addRefineFormalMembersProposal(Collection<ICompletionProposal> proposals, 
    Node node, Tree.CompilationUnit rootNode, Boolean ambiguousError) {
    
    for (p in CeylonIterable(proposals)) {
        if (is EclipseRefineFormalMembersQuickFix p) {
            return;
        }
    }
    
    if (exists scope = getRefineFormalMembersScope(node)) {
        assert(is CeylonEditor editor = EditorUtil.currentEditor);
        proposals.add(EclipseRefineFormalMembersQuickFix(editor, node, rootNode, ambiguousError));
    }
}

class EclipseRefineFormalMembersQuickFix(CeylonEditor editor, Node node, Tree.CompilationUnit rootNode, Boolean ambiguousError) 
        satisfies RefineFormalMembersQuickFix<IDocument,InsertEdit,TextEdit,TextChange>
                & EclipseDocumentChanges
                & ICompletionProposal & ICompletionProposalExtension6 {
    
    shared actual String? additionalProposalInfo {
        //TODO: list the members that will be refined!
        return null;
    }
    
    shared actual void apply(IDocument document) {
        value change = DocumentChange("Refine Members", document);
        refineFormalMembers(document, change, rootNode, node, editor.selection.offset);
        
        change.initializeValidationData(null);
        ResourcesPlugin.workspace.run(PerformChangeOperation(change), NullProgressMonitor());
    }
    
    shared actual IdeCompletionManager<out Object,out Object,out Object,IDocument> completionManager
            => EclipseCompletionManager(editor);
    
    shared actual IContextInformation? contextInformation => null;
    
    shared actual String displayString => getName(node, ambiguousError) else "<error>";
    
    shared actual Character getDocChar(IDocument doc, Integer offset) => Character(doc.getChar(offset));
    
    shared actual Point? getSelection(IDocument? iDocument) => null;
    
    shared actual Image image => RefinementCompletionProposal.\iFORMAL_REFINEMENT;
    
    shared actual ImportProposals<out Object,out Object,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;
    
    shared actual Indents<IDocument> indents => eclipseIndents;
    
    shared actual StyledString styledDisplayString {
        TriggerSequence? binding = EditorUtil.getCommandBinding("com.redhat.ceylon.eclipse.ui.action.refineFormalMembers");
        String hint = if (!exists binding) then "" else " (" + binding.format() + ")";
        return Highlights.styleProposal(displayString, false).append(hint, StyledString.\iQUALIFIER_STYLER);
    }
}
