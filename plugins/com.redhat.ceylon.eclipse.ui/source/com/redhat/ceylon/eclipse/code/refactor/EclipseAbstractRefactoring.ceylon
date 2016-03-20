import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.eclipse.core.vfs {
    IFileVirtualFile
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.model {
    CrossProjectSourceFile,
    CrossProjectBinaryUnit,
    IResourceAware
}
import com.redhat.ceylon.ide.common.refactoring {
    AbstractRefactoring
}
import com.redhat.ceylon.ide.common.typechecker {
    ProjectPhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.util {
    List,
    ArrayList
}

import org.eclipse.core.resources {
    IProject,
    IResource,
    IFolder,
    IFile
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.ltk.core.refactoring {
    DocumentChange,
    TextFileChange,
    TextChange
}
import org.eclipse.ui {
    IFileEditorInput,
    IEditorPart
}

abstract class EclipseAbstractRefactoring<RefactoringData>
        (IEditorPart editorPart)
        extends Refactoring()
        satisfies AbstractRefactoring<RefactoringData> {
    
    shared class EclipseEditorData(shared CeylonEditor editor) 
            satisfies EditorData {
        
        shared IDocument? document
                = editor.documentProvider
                    .getDocument(editor.editorInput);
        
        shared IProject? project= EditorUtil.getProject(editor);
        
        tokens = editor.parseController.tokens;
        
        rootNode = editor.parseController.typecheckedRootNode;
        
        value selection = EditorUtil.getSelection(editor);
        node = nodes.findNode(rootNode, tokens, 
            selection.offset,
            selection.offset+selection.length);
        
        shared actual IFileVirtualFile? sourceVirtualFile = 
                if (is IFileEditorInput input = editor.editorInput, 
                    exists file=EditorUtil.getFile(input)) 
                    then IFileVirtualFile(file) else null;
    }

    shared actual EclipseEditorData? editorData 
            = if (is CeylonEditor editorPart) 
              then EclipseEditorData(editorPart) else null;

    rootNode => editorData?.rootNode;

    shared Boolean inSameProject(Declaration declaration) {
        value unit = declaration.unit;
        if (unit is CrossProjectSourceFile<IProject,IResource,IFolder,IFile> || 
            unit is CrossProjectBinaryUnit<IProject,IResource,IFolder,IFile, out Anything, out Anything>) {
            return false;
        }
        else if (is IResourceAware<out Anything, out Anything, out Anything> unit, 
            exists p = unit.resourceProject,
            exists editorProject = editorData?.project) {
            return p==editorProject;
        }
        else {
            return false;
        }
    }
    
    shared DocumentChange newDocumentChange() {
        assert (editorData exists);
        value dc = DocumentChange(
            editorPart.editorInput.name 
                    + " \{#2014} current editor", 
            editorData?.document);
        dc.textType = "ceylon";
        return dc;
    }

    shared TextFileChange newTextFileChange(PhasedUnit pu) {
        assert (is ProjectPhasedUnit<IProject,IResource,IFolder,IFile> pu);
        value tfc = TextFileChange(name, pu.resourceFile);
        tfc.textType = "ceylon";
        return tfc;
    }

    searchInEditor()
            => if (exists ceylonEditor=editorData?.editor)
            then ceylonEditor.dirty
            else false;

    searchInFile(PhasedUnit pu)
            => if (exists ceylonEditor=editorData?.editor,
                    exists typecheckedRootNode=rootNode)
            then !ceylonEditor.dirty
            || pu.unit != typecheckedRootNode.unit
            else true;

    shared TextChange newLocalChange() {
        assert (exists editorData);
        TextChange tc;
        if (searchInEditor()) {
            assert (exists doc = editorData.document);
            tc = DocumentChange(name, editorData.document);
        }
        else {
            assert (exists file = editorData.sourceVirtualFile?.nativeResource);
            tc = TextFileChange(name, file);
        }
        tc.textType = "ceylon";
        return tc;
    }

    shared actual List<PhasedUnit> getAllUnits() {
        assert (editorData exists);
        assert (exists project = editorData?.project);
        value units = ArrayList<PhasedUnit>();
        units.addAll(CeylonBuilder.getUnits(project));
        for (p in project.referencingProjects.iterable) {
            units.addAll(CeylonBuilder.getUnits(p));
        }
        return units;
    }
}