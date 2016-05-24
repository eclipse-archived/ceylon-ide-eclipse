import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.platform {
    EclipseTextChange
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    eclipseIcons
}
import com.redhat.ceylon.ide.common.correct {
    QuickFixData,
    QuickFixKind
}
import com.redhat.ceylon.ide.common.doc {
    Icons
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.platform {
    CommonTextChange=TextChange
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

import java.util {
    Collection
}

import org.eclipse.core.resources {
    IProject
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal
}

shared class EclipseQuickFixData(ProblemLocation location,
    shared actual Tree.CompilationUnit rootNode,
    shared actual Node node,
    shared IProject project,
    shared Collection<ICompletionProposal> proposals,
    shared CeylonEditor editor,
    shared actual BaseCeylonProject ceylonProject,
    IDocument doc)
        satisfies QuickFixData {
    
    errorCode => location.problemId;
    problemOffset => location.offset;
    problemLength => location.length;
    
    phasedUnit => editor.parseController.lastPhasedUnit;
    document = EclipseDocument(doc);
    editorSelection => DefaultRegion(editor.selection.offset, editor.selection.length);
    
    shared actual void addQuickFix(String desc, CommonTextChange|Callable<Anything, []> change,
        DefaultRegion? selection, Boolean qualifiedNameIsPath, Icons? icon,
        QuickFixKind kind) {
        
        value myImage = eclipseIcons.fromIcons(icon) else CeylonResources.minorChange;

        if (is EclipseTextChange change) {
            proposals.add(
                proposalsFactory.createProposal(desc, change, selection,
                    qualifiedNameIsPath, myImage, kind)
            );
        } else if (is Callable<Anything, []> callback = change) {
            proposals.add(object extends CorrectionProposal(desc, null, null, myImage, qualifiedNameIsPath) {
                shared actual void apply(IDocument? iDocument) {
                    callback();
                }
            });
        }
    }
    
    shared actual void addConvertToClassProposal(String description, 
        Tree.ObjectDefinition declaration) {
        
        proposals.add(EclipseConvertToClassProposal(description, editor, declaration));
    }
    
    shared actual void addAssignToLocalProposal(String description) {
        proposals.add(EclipseAssignToLocalProposal(this, description));
    }
}
