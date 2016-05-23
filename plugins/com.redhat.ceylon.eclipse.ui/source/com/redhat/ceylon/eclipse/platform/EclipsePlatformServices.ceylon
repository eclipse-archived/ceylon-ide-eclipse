import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.eclipse.code.correct {
    EclipseDocument
}
import com.redhat.ceylon.eclipse.code.editor {
    Navigation
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.platform {
    PlatformServices,
    ModelServices,
    VfsServices,
    TextChange,
    TextEdit,
    InsertEdit,
    ReplaceEdit,
    CompositeChange,
    CommonDocument,
    NoopLinkedMode
}
import com.redhat.ceylon.ide.common.typechecker {
    ModifiablePhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast
}
import com.redhat.ceylon.model.typechecker.model {
    Unit
}

import org.eclipse.core.resources {
    IProject,
    IFolder,
    IResource,
    IFile
}
import org.eclipse.jface.preference {
    IPreferenceStore
}
import org.eclipse.ltk.core.refactoring {
    TextFileChange,
    ETextChange=TextChange,
    ECompositeChange=CompositeChange,
    DocumentChange
}
import org.eclipse.text.edits {
    MultiTextEdit,
    ETextEdit=TextEdit,
    EInsertEdit=InsertEdit,
    EReplaceEdit=ReplaceEdit,
    EDeleteEdit=DeleteEdit
}
import org.eclipse.ui.editors.text {
    EditorsUI
}
import org.eclipse.ui.texteditor {
    AbstractDecoratedTextEditorPreferenceConstants {
        editorTabWidth,
        editorSpacesForTabs
    }
}

object eclipsePlatformServices satisfies PlatformServices {
    
    utils() => eclipsePlatformUtils;
    
    shared actual ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>
            model<NativeProject, NativeResource, NativeFolder, NativeFile>() 
            => unsafeCast<ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>>
                    (eclipseModelServices);
    
    shared actual VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile> 
            vfs<NativeProject, NativeResource, NativeFolder, NativeFile>() 
            => unsafeCast<VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile>>
                    (eclipseVfsServices);
    
    createTextChange(String desc, CommonDocument|PhasedUnit input)
            => EclipseTextChange(desc, input);
    
    createCompositeChange(String desc) => EclipseCompositeChange(desc);
    
    gotoLocation(Unit unit, Integer offset, Integer length)
            => Navigation.gotoLocation(unit, offset, length);
    
    indentSpaces 
            =>let(IPreferenceStore? store = EditorsUI.preferenceStore)
                (store?.getInt(editorTabWidth) else 4);

    indentWithSpaces
            => let(IPreferenceStore? store = EditorsUI.preferenceStore)
                (store?.getBoolean(editorSpacesForTabs) else false);
    
    createLinkedMode(CommonDocument document)
            => if (is EclipseDocument document)
               then EclipseLinkedMode(document)
               else NoopLinkedMode(document);
    
    completion => eclipseCompletionServices;
}

shared class EclipseTextChange(String desc, CommonDocument|PhasedUnit|ETextChange input)
        satisfies TextChange {
    
    shared ETextChange nativeChange;
    EclipseDocument doc;
    
    if (is EclipseDocument input) {
        doc = input;
        nativeChange = DocumentChange(desc, input.document);
    }
    else if (is ModifiablePhasedUnit<IProject,IResource,IFolder,IFile> input) {
        nativeChange = TextFileChange(desc, input.resourceFile);
        doc = EclipseDocument(EditorUtil.getDocument(nativeChange));
    }
    else if (is ETextChange input) {
        nativeChange = input;
        doc = EclipseDocument(EditorUtil.getDocument(nativeChange));
    }
    else {
        throw Exception("Unsupported input: ``input``");
    }
    
    ETextEdit toEclipseTextEdit(TextEdit edit) 
            => switch (edit)
            case (is InsertEdit) 
                EInsertEdit(edit.start, edit.text)
            case (is ReplaceEdit) 
                EReplaceEdit(edit.start, edit.length, edit.text)
            else 
                EDeleteEdit(edit.start, edit.length);
    
    shared actual void addEdit(TextEdit edit) {
        value eclipseEdit = toEclipseTextEdit(edit);
        if (is MultiTextEdit me = nativeChange.edit) {
            nativeChange.addEdit(eclipseEdit);
        } else {
            nativeChange.edit = eclipseEdit;            
        }
    }
    
    document = doc;
    
    hasEdits => nativeChange.edit.hasChildren();
    
    initMultiEdit() => nativeChange.edit = MultiTextEdit();
    
    apply() => EditorUtil.performChange(nativeChange);
    
    offset => nativeChange.edit.offset;
    length => nativeChange.edit.length;
}

shared class EclipseCompositeChange(String desc) 
        satisfies CompositeChange {
    shared ECompositeChange nativeChange = ECompositeChange(desc);
    
    shared actual void addTextChange(TextChange change) {
        if (is EclipseTextChange change) {
            nativeChange.add(change.nativeChange);
        }
    }
    
    hasChildren => nativeChange.children.size > 0;
    
}
