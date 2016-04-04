import com.redhat.ceylon.ide.common.platform {
    PlatformServices,
    ModelServices,
    IdeUtils,
    VfsServices
}
import com.redhat.ceylon.ide.common.util {
    unsafeCast,
    Indents
}
import com.redhat.ceylon.ide.common.correct {
    ImportProposals
}
import com.redhat.ceylon.eclipse.code.correct {
    eclipseImportProposals
}
import com.redhat.ceylon.eclipse.util {
    eclipseIndents
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
    
}