import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.eclipse.code.complete {
    EclipseCompletionManager,
    EclipseLinkedModeSupport
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.code.refactor {
    AbstractLinkedMode
}
import com.redhat.ceylon.eclipse.ui {
    CeylonResources
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents,
    EditorUtil,
    Highlights
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import com.redhat.ceylon.ide.common.correct {
    AbstractQuickFix,
    ImportProposals,
    GenericQuickFix,
    AbstractLocalProposal
}
import com.redhat.ceylon.ide.common.model {
    ModifiableSourceFile,
    IResourceAware
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}
import com.redhat.ceylon.ide.common.typechecker {
    ModifiablePhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.redhat.ceylon.model.typechecker.model {
    Unit,
    Type
}

import org.eclipse.core.resources {
    IFile,
    IProject,
    IResource,
    IFolder
}
import org.eclipse.jface.text {
    IDocument,
    Region
}
import org.eclipse.jface.text.contentassist {
    ICompletionProposal,
    ICompletionProposalExtension6,
    IContextInformation
}
import org.eclipse.jface.text.link {
    LinkedModeModel,
    LinkedPosition
}
import org.eclipse.jface.viewers {
    StyledString
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    TextFileChange,
    DocumentChange
}
import org.eclipse.swt.graphics {
    Point
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}

shared interface EclipseAbstractQuickFix
        satisfies AbstractQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> {
    
    shared actual IdeCompletionManager<out Anything,out ICompletionProposal,IDocument> completionManager 
            => EclipseCompletionManager(CeylonEditor());
    
    shared actual Integer getTextEditOffset(TextEdit change) => change.offset;
    
    //shared actual List<PhasedUnit> getUnits(IProject p) => CeylonList(CeylonBuilder.getUnits(p));
    
    shared actual ImportProposals<out Anything,out Anything,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;
    
    shared actual Indents<IDocument> indents => eclipseIndents;
    
    shared actual Region newRegion(Integer start, Integer length) => Region(start, length);
    
    shared Region? toRegion(DefaultRegion? region) => 
            if (exists region) then Region(region.start, region.length) else null;
    
    shared actual TextChange newTextChange(String desc, PhasedUnit|IFile|IDocument u) {
        if (is IDocument u) {
            return DocumentChange(desc, u);
        } else if (is PhasedUnit u){
            assert(is ModifiablePhasedUnit<IProject,IResource,IFolder,IFile> u);
            return TextFileChange(desc, u.resourceFile);
        } else {
            return TextFileChange(desc, u);
        }
    }
    
    shared actual PhasedUnit? getPhasedUnit(Unit? u, EclipseQuickFixData data) {
        if (is ModifiableSourceFile<IProject, IResource, IFolder, IFile> u) {
            return u.phasedUnit;
        }
        return null;
    }
    
    shared actual IFile? getFile<NativeFile>(IResourceAware<out Anything,out Anything,NativeFile> pu, EclipseQuickFixData data) {
        if (is IFile res = pu.resourceFile) {
            return res;
        }
        return null;
    }
}

interface EclipseGenericQuickFix 
        satisfies EclipseAbstractQuickFix
                & GenericQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal>
                & EclipseDocumentChanges {
    
    shared default actual void newProposal(EclipseQuickFixData data, String desc, 
        TextChange change, DefaultRegion? region)
            => data.proposals.add(CorrectionProposal(desc, change, 
                if (exists region) then toRegion(region) else null));
}

abstract class EclipseLocalProposal(EclipseQuickFixData data, shared actual String displayString)
        extends AbstractLinkedMode(data.editor)
        satisfies AbstractLocalProposal<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal,LinkedModeModel>
                & EclipseAbstractQuickFix
                & EclipseDocumentChanges
                & EclipseLinkedModeSupport
                & ICompletionProposal & ICompletionProposalExtension6 {
    
    shared actual variable Integer currentOffset = -1;
    
    shared actual variable Integer exitPos = 0;
    
    shared actual variable {String*} names = empty;
    
    shared actual variable Integer offset = 0;
    
    shared actual variable Type? type = null;
    
    hintTemplate => "Enter type and name for new local {0}";
    
    newLinkedMode() => linkedModeModel;
    
    shared actual void updatePopupLocation() {
        LinkedPosition? pos = currentLinkedPosition;
        value popup = infoPopup;
        if (!exists pos) {
            popup.setHintTemplate(hintTemplate);
        } else if (pos.sequenceNumber == 1) {
            popup.setHintTemplate("Enter type for new local {0}");
        } else {
            popup.setHintTemplate("Enter name for new local {0}");
        }
    }
    
    shared actual void apply(IDocument doc) {
        currentOffset = data.editor.selection.offset;
        
        value change = performInitialChange(data, 
            EditorUtil.getFile(data.editor.editorInput), currentOffset);
        
        if (exists change) {
            EditorUtil.performChange(change);
            value unit = data.editor.parseController.lastCompilationUnit.unit;
            if (exists lm = addLinkedPositions(doc, unit)) {
                enterLinkedMode(doc, 2, exitPosition);
                openPopup();
            }
        }
    }
    
    Integer exitPosition => exitPos + initialName.size + 9;
    
    shared actual default StyledString styledDisplayString
            => Highlights.styleProposal(displayString, false, true);
    
    shared actual Point? getSelection(IDocument? doc) => null;
    
    shared actual String? additionalProposalInfo => null;
    
    image => CeylonResources.\iMINOR_CHANGE;
    
    shared actual IContextInformation? contextInformation => null;
}
