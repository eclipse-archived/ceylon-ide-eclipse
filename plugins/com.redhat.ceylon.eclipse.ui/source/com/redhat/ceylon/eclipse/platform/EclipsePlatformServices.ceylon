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
    ImportProposals,
    CommonDocument
}
import com.redhat.ceylon.ide.common.platform {
    PlatformServices,
    ModelServices,
    IdeUtils,
    VfsServices,
    TextChange,
    TextEdit,
    InsertEdit,
    ReplaceEdit
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
}

shared class EclipseTextChange(String desc, CommonDocument|PhasedUnit input)
        satisfies TextChange {
    
    shared ETextChange change;
    
    if (is EclipseDocument input) {
        change = DocumentChange(desc, input.doc);
    } else if (is ModifiablePhasedUnit<IProject,IResource,IFolder,IFile> input) {
        change = TextFileChange(desc, input.resourceFile);
    } else {
        throw Exception("Unsupported input: ``input``");
    }
    
    shared actual void addChangesFrom(TextChange other) {}
    
    ETextEdit toEclipseTextEdit(TextEdit edit) {
        return switch (edit)
        case (is InsertEdit) EInsertEdit(edit.start, edit.text)
        case (is ReplaceEdit) EReplaceEdit(edit.start, edit.length, edit.text)
        else EDeleteEdit(edit.start, edit.length);
    }
    
    shared actual void addEdit(TextEdit edit) {
        value eclipseEdit = toEclipseTextEdit(edit);
        
        if (is MultiTextEdit me = change.edit) {
            change.addEdit(eclipseEdit);
        } else {
            change.edit = eclipseEdit;            
        }
    }
    
    document = EclipseDocument(EditorUtil.getDocument(change));
    
    hasEdits => change.edit.hasChildren();
    
    initMultiEdit() => change.edit = MultiTextEdit();
}
