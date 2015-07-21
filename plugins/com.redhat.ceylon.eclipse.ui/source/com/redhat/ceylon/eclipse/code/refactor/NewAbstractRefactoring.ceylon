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
    CommonRefactoring
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
    TextChange
}
import org.eclipse.ui {
    IFileEditorInput
}

abstract class NewAbstractRefactoring(CeylonEditor editor) extends Refactoring() satisfies CommonRefactoring {
    
    late IProject? project;
    late IFile? sourceFile;
    late List<CommonToken>? tokens;
    late IDocument? document;
    shared late Node? node;

    document = editor.documentProvider.getDocument(editor.editorInput);
    project = EditorUtil.getProject(editor);
    value cpc = editor.parseController;
    tokens = cpc.tokens;
    rootNode = cpc.rootNode;
    if (is IFileEditorInput input = editor.editorInput) {
        sourceFile = EditorUtil.getFile(input);
        node = Nodes.findNode(rootNode, EditorUtil.getSelection(editor));
    } else {
        sourceFile = null;
        node = null;
    }
    
    shared Boolean inSameProject(Declaration declaration) {
        value unit = declaration.unit;
        if (unit is CrossProjectSourceFile || unit is CrossProjectBinaryUnit) {
            return false;
        }
        if (is IResourceAware unit) {
            if (exists p = unit.projectResource, exists project) {
                return p.equals(project);
            }
        }
        return false;
    }
    
    shared Boolean isEditable() => rootNode.unit is EditedSourceFile || rootNode.unit is ProjectSourceFile;
    
    shared actual String toString(Node term) {
        return Nodes.toString(term, tokens);
    }
    
    shared DocumentChange? newDocumentChange() {
        value dc = DocumentChange(editor.editorInput.name + " - current editor", document);
        dc.textType = "ceylon";
        return dc;
    }
    
    shared TextFileChange newTextFileChange(PhasedUnit pu) {
        TextFileChange tfc = TextFileChange(name, CeylonBuilder.getFile(pu));
        tfc.textType = "ceylon";
        return tfc;
    }

    shared actual Boolean searchInEditor() => editor.dirty;
    
    shared actual Boolean searchInFile(PhasedUnit pu) {
        return if (!editor.dirty || !pu.unit.equals(editor.parseController.rootNode.unit))
            then true else false;
    }

    shared TextChange newLocalChange() {
        TextChange tc = if (searchInEditor()) 
                then DocumentChange(name, document)
                else TextFileChange(name, sourceFile);
        
        tc.textType = "ceylon";
        return tc;
    }
    
    shared actual List<PhasedUnit> getAllUnits() {
        List<PhasedUnit> units = ArrayList<PhasedUnit>();
        units.addAll(CeylonBuilder.getUnits(project));
        
        if (exists project) {
            for (p in project.referencingProjects.iterable) {
                units.addAll(CeylonBuilder.getUnits(p));
            }
        }
        
        return units;
    }
}