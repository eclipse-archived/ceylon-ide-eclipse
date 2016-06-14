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
    editorSelection => DefaultRegion {
        start = editor.selection.offset;
        length = editor.selection.length;
    };
    
    shared actual void addQuickFix(String desc, 
        CommonTextChange|Anything() change,
        DefaultRegion? selection, 
        Boolean qualifiedNameIsPath, 
        Icons? icon,
        QuickFixKind kind, 
        String? hint) {
        
        value myImage 
                = eclipseIcons.fromIcons(icon) 
                else CeylonResources.minorChange;

        if (is EclipseTextChange change) {
            proposals.add(
                proposalsFactory.createProposal {
                    description = desc;
                    change = change;
                    selection = selection;
                    qualifiedNameIsPath = qualifiedNameIsPath;
                    myImage = myImage;
                    kind = kind;
                }
            );
        } else if (is Anything() callback = change) {
            proposals.add(object extends CorrectionProposal
                (desc, null, null, myImage, qualifiedNameIsPath) {
                apply(IDocument? doc) => callback();
            });
        }
    }
    
    addConvertToClassProposal(String description, 
        Tree.ObjectDefinition declaration) 
            => proposals.add(EclipseConvertToClassProposal {
                desc = description;
                editor = editor;
                declaration = declaration;
            });
        
    addAssignToLocalProposal(String description) 
            => proposals.add(EclipseAssignToLocalProposal(this, description));
}
