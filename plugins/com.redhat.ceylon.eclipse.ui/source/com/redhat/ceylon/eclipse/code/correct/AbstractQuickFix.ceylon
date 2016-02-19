import ceylon.interop.java {
    CeylonList
}

import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.eclipse.code.complete {
    EclipseCompletionManager
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents
}
import com.redhat.ceylon.ide.common.completion {
    IdeCompletionManager
}
import com.redhat.ceylon.ide.common.typechecker {
    ModifiablePhasedUnit
}
import com.redhat.ceylon.ide.common.correct {
    AbstractQuickFix,
    ImportProposals
}
import com.redhat.ceylon.ide.common.util {
    Indents
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
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
    ICompletionProposal
}
import org.eclipse.ltk.core.refactoring {
    TextChange,
    TextFileChange,
    DocumentChange
}
import org.eclipse.text.edits {
    InsertEdit,
    TextEdit
}
import com.redhat.ceylon.ide.common.model {
    ModifiableSourceFile,
    IResourceAware
}
import com.redhat.ceylon.ide.common.refactoring {
    DefaultRegion
}

interface EclipseAbstractQuickFix
        satisfies AbstractQuickFix<IFile,IDocument,InsertEdit,TextEdit,TextChange,Region,IProject,EclipseQuickFixData,ICompletionProposal> {
    
    shared actual IdeCompletionManager<out Anything,out ICompletionProposal,IDocument> completionManager 
            => EclipseCompletionManager(CeylonEditor());
    
    shared actual Integer getTextEditOffset(TextEdit change) => change.offset;
    
    shared actual List<PhasedUnit> getUnits(IProject p) => CeylonList(CeylonBuilder.getUnits(p));
    
    shared actual ImportProposals<out Anything,out Anything,IDocument,InsertEdit,TextEdit,TextChange> importProposals
            => eclipseImportProposals;
    
    shared actual Indents<IDocument> indents => eclipseIndents;
    
    shared actual Region newRegion(Integer start, Integer length) => Region(start, length);
    
    shared Region toRegion(DefaultRegion region) => Region(region.start, region.length);
    
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
