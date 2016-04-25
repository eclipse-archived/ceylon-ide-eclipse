import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.eclipse.code.correct {
    eclipseImportProposals,
    EclipseDocument
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents,
    EditorUtil
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.ide.common.platform {
    PlatformServices,
    ModelServices,
    IdeUtils,
    VfsServices,
    TextChange,
    TextEdit,
    InsertEdit,
    ReplaceEdit,
    CompositeChange,
    CommonDocument
}
import com.redhat.ceylon.ide.common.typechecker {
    ModifiablePhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast,
    Indents
}

import org.eclipse.core.resources {
    IProject,
    IFolder,
    IResource,
    IFile
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

object eclipsePlatformServices satisfies PlatformServices {
    
    shared actual ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>
        model<NativeProject, NativeResource, NativeFolder, NativeFile>() => 
            unsafeCast<ModelServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(eclipseModelServices);
    
    shared actual VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile> vfs<NativeProject, NativeResource, NativeFolder, NativeFile>() => 
            unsafeCast<VfsServices<NativeProject,NativeResource,NativeFolder,NativeFile>>(eclipseVfsServices);

    shared actual IdeUtils utils() => eclipsePlatformUtils;
    
    shared actual ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange>
        importProposals<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange>() =>
            unsafeCast<ImportProposals<IFile,ICompletionProposal,IDocument,InsertEdit,TextEdit,TextChange>>(eclipseImportProposals);
    
    shared actual Indents<IDocument> indents<IDocument>() 
            => unsafeCast<Indents<IDocument>>(eclipseIndents);

    createTextChange(String desc, CommonDocument|PhasedUnit input)
            => EclipseTextChange(desc, input);
    
    createCompositeChange(String desc) => EclipseCompositeChange(desc);
    
}

shared class EclipseTextChange(String desc, CommonDocument|PhasedUnit input)
        satisfies TextChange {
    
    shared ETextChange nativeChange;
    EclipseDocument doc;
    
    if (is EclipseDocument input) {
        doc = input;
        nativeChange = DocumentChange(desc, input.doc);
    } else if (is ModifiablePhasedUnit<IProject,IResource,IFolder,IFile> input) {
        nativeChange = TextFileChange(desc, input.resourceFile);
        doc = EclipseDocument(EditorUtil.getDocument(nativeChange));
    } else {
        throw Exception("Unsupported input: ``input``");
    }
    
    ETextEdit toEclipseTextEdit(TextEdit edit) {
        return switch (edit)
        case (is InsertEdit) EInsertEdit(edit.start, edit.text)
        case (is ReplaceEdit) EReplaceEdit(edit.start, edit.length, edit.text)
        else EDeleteEdit(edit.start, edit.length);
    }
    
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
}

shared class EclipseCompositeChange(String desc) satisfies CompositeChange {
    shared ECompositeChange nativeChange = ECompositeChange(desc);
    
    shared actual void addTextChange(TextChange change) {
        if (is EclipseTextChange change) {
            nativeChange.add(change.nativeChange);
        }
    }
    
    hasChildren => nativeChange.children.size > 0;
    
}
