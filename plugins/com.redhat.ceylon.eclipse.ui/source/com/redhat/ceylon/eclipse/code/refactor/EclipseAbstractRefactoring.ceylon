import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node
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
    IFileEditorInput
}

Boolean inSameProject(Declaration declaration, CeylonEditor editor) {
    value unit = declaration.unit;
    if (unit is CrossProjectSourceFile<IProject,IResource,IFolder,IFile> || 
        unit is CrossProjectBinaryUnit<IProject,IResource,IFolder,IFile, out Anything, out Anything>) {
        return false;
    }
    else if (is IResourceAware<out Anything, out Anything, out Anything> unit, 
        exists p = unit.resourceProject,
        exists editorProject = EditorUtil.getProject(editor)) {
        return p==editorProject;
    }
    else {
        return false;
    }
}

abstract class EclipseAbstractRefactoring<RefactoringData>
        (CeylonEditor editorPart)
        extends Refactoring()
        satisfies AbstractRefactoring<RefactoringData> {
    
    shared class EclipseEditorData(shared CeylonEditor editor) 
            satisfies EditorData {
        
        shared default IDocument? document
                = editor.documentProvider
                    .getDocument(editor.editorInput);
        
        shared IProject? project = EditorUtil.getProject(editor);
        
        tokens = editor.parseController.tokens;
        
        rootNode = editor.parseController.typecheckedRootNode;
        
        value selection = EditorUtil.getSelection(editor);
        value _node = nodes.findNode(
            rootNode, tokens, 
            selection.offset,
            selection.offset+selection.length
        );
        if (!exists _node) {
            throw Exception("Can't refactor if node is null " + 
                "(selection = [``selection.offset``, ``selection.offset+selection.length``]).");
        }
        
        shared actual default Node node = _node;
        
        shared actual IFileVirtualFile? sourceVirtualFile = 
                if (is IFileEditorInput input = editor.editorInput, 
                    exists file=  EditorUtil.getFile(input)) 
                    then IFileVirtualFile(file) else null;
    }

    shared actual default EclipseEditorData editorData 
            = EclipseEditorData(editorPart);

    inSameProject(Declaration declaration)
            => package.inSameProject(declaration, editorPart);
    
    shared DocumentChange newDocumentChange() {
        value dc = DocumentChange(
            editorPart.editorInput.name 
                    + " \{#2014} current editor", 
            editorData.document);
        dc.textType = "ceylon";
        return dc;
    }

    shared TextFileChange newTextFileChange(PhasedUnit pu) {
        assert (is IResourceAware<IProject,IFolder,IFile> pu);
        value tfc = TextFileChange(name, pu.resourceFile);
        tfc.textType = "ceylon";
        return tfc;
    }
    
    shared actual PhasedUnit editorPhasedUnit 
            => editorData.editor.parseController.lastPhasedUnit;

    searchInEditor() => editorData.editor.dirty;

    searchInFile(PhasedUnit pu)
            => !editorData.editor.dirty 
            || pu.unit != rootNode.unit;

    shared TextChange newLocalChange() {
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
        assert (exists project = editorData.project);
        value units = ArrayList<PhasedUnit>();
        units.addAll(CeylonBuilder.getUnits(project));
        for (p in project.referencingProjects.iterable) {
            units.addAll(CeylonBuilder.getUnits(p));
        }
        return units;
    }
    
    shared default small Integer saveMode 
            => affectsOtherFiles 
            then RefactoringSaveHelper.\iSAVE_CEYLON_REFACTORING 
            else RefactoringSaveHelper.\iSAVE_NOTHING;
}