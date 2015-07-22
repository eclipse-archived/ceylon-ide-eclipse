import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.eclipse.core.model {
    CrossProjectSourceFile,
    CrossProjectBinaryUnit,
    IResourceAware,
    EditedSourceFile,
    ProjectSourceFile
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil,
    Nodes
}
import com.redhat.ceylon.ide.common.refactoring {
    AbstractRefactoring
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.util {
    List,
    ArrayList
}

import org.antlr.runtime {
    CommonToken
}
import org.eclipse.core.resources {
    IFile,
    IProject
}
import org.eclipse.jface.text {
    IDocument
}
import org.eclipse.ltk.core.refactoring {
    DocumentChange,
    TextFileChange,
    TextChange,
    LtkRefactoring = Refactoring

}
import org.eclipse.ui {
    IFileEditorInput,
    IEditorPart
}
import org.eclipse.ui.texteditor {
    ITextEditor
}

abstract class EclipseAbstractRefactoring(IEditorPart editorPart) extends LtkRefactoring() satisfies AbstractRefactoring {

    shared class CeylonEditorData() {
        assert (is CeylonEditor ce=editorPart);
        assert(is ITextEditor editorPart);
        shared CeylonEditor editor=ce;
        shared IDocument? document = editor.documentProvider.getDocument(editorPart.editorInput);
        shared IProject? project = EditorUtil.getProject(editorPart);
        shared List<CommonToken>? tokens = editor.parseController.tokens;
        shared Tree.CompilationUnit? rootNode = editor.parseController.rootNode;
        shared Node? node;
        shared IFile? sourceFile;
        if (exists existingRootNode=rootNode,
            is IFileEditorInput input = editorPart.editorInput) {
            sourceFile = EditorUtil.getFile(input);
            node = Nodes.findNode(rootNode, EditorUtil.getSelection(editorPart));
        } else {
            sourceFile = null;
            node = null;
        }
    }

    shared CeylonEditorData? ceylonEditorData;

    if (is CeylonEditor ce=editorPart) {
        ceylonEditorData = CeylonEditorData();
    } else {
        ceylonEditorData = null;
    }

    shared actual Tree.CompilationUnit? rootNode => ceylonEditorData?.rootNode;

    shared Boolean inSameProject(Declaration declaration) {
        value unit = declaration.unit;
        if (unit is CrossProjectSourceFile || unit is CrossProjectBinaryUnit) {
            return false;
        }
        if (is IResourceAware unit) {
            if (exists p = unit.projectResource,
                exists editorProject=ceylonEditorData?.project) {
                return p.equals(editorProject);
            }
        }
        return false;
    }

    shared Boolean isEditable()
            => rootNode?.unit is EditedSourceFile ||
            rootNode?.unit is ProjectSourceFile;

    shared actual String toString(Node term) {
        assert(ceylonEditorData exists);
        return Nodes.toString(term, ceylonEditorData?.tokens);
    }

    shared DocumentChange? newDocumentChange() {
        assert(ceylonEditorData exists);
        value dc = DocumentChange(editorPart.editorInput.name + " - current editor", ceylonEditorData?.document);
        dc.textType = "ceylon";
        return dc;
    }

    shared TextFileChange newTextFileChange(PhasedUnit pu) {
        TextFileChange tfc = TextFileChange(name, CeylonBuilder.getFile(pu));
        tfc.textType = "ceylon";
        return tfc;
    }

    shared actual Boolean searchInEditor()
            => if (exists ceylonEditor=ceylonEditorData?.editor)
            then ceylonEditor.dirty
            else false;

    shared actual Boolean searchInFile(PhasedUnit pu)
            => if (exists ceylonEditor=ceylonEditorData?.editor)
            then !ceylonEditor.dirty
            || pu.unit != ceylonEditor.parseController.rootNode.unit
            else true;

    shared TextChange newLocalChange() {
        assert(ceylonEditorData exists);
        TextChange tc = if (searchInEditor())
                then DocumentChange(name, ceylonEditorData?.document)
                else TextFileChange(name, ceylonEditorData?.sourceFile);

        tc.textType = "ceylon";
        return tc;
    }

    shared actual List<PhasedUnit> getAllUnits() {
        assert(ceylonEditorData exists);
        assert(exists project = ceylonEditorData?.project);
        List<PhasedUnit> units = ArrayList<PhasedUnit>();
        units.addAll(CeylonBuilder.getUnits(project));
        for (p in project.referencingProjects.iterable) {
            units.addAll(CeylonBuilder.getUnits(p));
        }

        return units;
    }
}