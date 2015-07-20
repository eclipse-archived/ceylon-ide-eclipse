import org.antlr.runtime {
    CommonToken
}
import com.redhat.ceylon.eclipse.code.editor {
    CeylonEditor
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree,
    Node
}
import com.redhat.ceylon.ide.common.refactoring {
    CommonRefactoring
}
import org.eclipse.core.resources {
    IFile,
    IProject
}
import org.eclipse.jface.text {
    IDocument
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil,
    Nodes
}
import java.util {
    List,
    ArrayList
}
import org.eclipse.ui {
    IEditorPart,
    IFileEditorInput
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import ceylon.interop.java {
    CeylonIterable
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}
import com.redhat.ceylon.eclipse.core.model {
    CrossProjectSourceFile,
    CrossProjectBinaryUnit,
    IResourceAware,
    EditedSourceFile,
    ProjectSourceFile
}
import org.eclipse.ltk.core.refactoring {
    DocumentChange,
    TextFileChange,
    TextChange
}
import org.eclipse.ui.texteditor {
    ITextEditor
}

abstract class NewAbstractRefactoring(IEditorPart editor) extends Refactoring() satisfies CommonRefactoring {
    
    late IProject? project;
    late IFile? sourceFile;
    late List<CommonToken>? tokens;
    late IDocument? document;
    late CeylonEditor? ceylonEditor;
    shared late Tree.CompilationUnit? rootNode;
    shared late Node? node;

    if (is CeylonEditor ce=editor) {
        assert(is ITextEditor editor);
        ceylonEditor = ce;
        document = editor.documentProvider.getDocument(editor.editorInput);
        project = EditorUtil.getProject(editor);
        value cpc = ce.parseController;
        tokens = cpc.tokens;
        rootNode = cpc.rootNode;
        if (exists rootNode, is IFileEditorInput input = editor.editorInput) {
            sourceFile = EditorUtil.getFile(input);
            node = Nodes.findNode(rootNode, EditorUtil.getSelection(editor));
        } else {
            sourceFile = null;
            node = null;
        }
    } else {
        ceylonEditor = null;
        document = null;
        tokens = null;
        rootNode = null;
        sourceFile = null;
        node = null;
        project = null;
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
    
    shared Boolean isEditable() => rootNode?.unit is EditedSourceFile || rootNode?.unit is ProjectSourceFile;
    
    shared actual String toString(Node term) {
        return Nodes.toString(term, tokens);
    }
    
    shared DocumentChange? newDocumentChange() {
        if (exists ceylonEditor, exists document) {
            value dc = DocumentChange((ceylonEditor.editorInput.name else "") + " - current editor", document);
            dc.textType = "ceylon";
            return dc;
        }
        return null;
    }
    
    shared TextFileChange newTextFileChange(PhasedUnit pu) {
        TextFileChange tfc = TextFileChange(name, CeylonBuilder.getFile(pu));
        tfc.textType = "ceylon";
        return tfc;
    }

    shared actual Boolean searchInEditor()
            => if (exists ceylonEditor, ceylonEditor.dirty) then true else false;
    
    shared actual Boolean searchInFile(PhasedUnit pu) {
        if (exists ceylonEditor) {
            return if (!ceylonEditor.dirty || !pu.unit.equals(ceylonEditor.parseController.rootNode.unit))
            then true else false;
        } else {
            return true;
        }
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