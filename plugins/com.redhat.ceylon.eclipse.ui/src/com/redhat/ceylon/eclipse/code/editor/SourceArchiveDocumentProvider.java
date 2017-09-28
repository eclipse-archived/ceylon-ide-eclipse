package org.eclipse.ceylon.ide.eclipse.code.editor;


import static org.eclipse.ceylon.ide.eclipse.code.editor.StreamUtils.readStreamContents;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

import org.eclipse.ceylon.ide.eclipse.core.external.ExternalSourceArchiveManager;

public class SourceArchiveDocumentProvider extends FileDocumentProvider {

    public static boolean isSrcArchive(IEditorInput editorInput) {
        if (editorInput instanceof IURIEditorInput) {
            IURIEditorInput uriEditorInput = (IURIEditorInput) editorInput;
            URI uri= uriEditorInput.getURI();
            if (uri == null) {
                return false;
            }
            else {
                return uri.getPath().contains(".src!");
            }
        }
        else {
            return false;
        }
    }
    
    @Override
    protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
       IAnnotationModel am = super.createAnnotationModel(element);
        if (am == null) {
            if (element instanceof FileStoreEditorInput) {
                URI uri = ((FileStoreEditorInput) element).getURI();
                IResource resource = ExternalSourceArchiveManager.toResource(uri);
                if (resource instanceof IFile) {
                    return new ResourceMarkerAnnotationModel(resource);
                }
            }            
        }
        return am==null ? new AnnotationModel(): am;
    }
    
    @Override
    protected boolean setDocumentContent(IDocument document, IEditorInput editorInput, 
            String encoding) throws CoreException {
        if (isSrcArchive(editorInput)) {
            IURIEditorInput uriEditorInput = (IURIEditorInput) editorInput;
            String contents = getZipEntryContents(uriEditorInput, encoding);
            if (contents!=null) {
                document.set(contents);
                return true;
            }
            else {
                return false;
            }
        } 
        else {
            return super.setDocumentContent(document, editorInput, encoding);
        }
    }
    
    private String getZipEntryContents(IURIEditorInput uriEditorInput, 
            String encoding) {
        String path = uriEditorInput.getURI().getPath();
        int lastColonIdx = path.lastIndexOf('!');
        if (lastColonIdx<0) return null;
        String jarPath= path.substring(0, lastColonIdx);
        String entryPath= path.substring(lastColonIdx + 2);
        try {
            ZipFile zipFile = new ZipFile(new File(jarPath));
            try {
                ZipEntry entry= zipFile.getEntry(entryPath);
                return encoding==null ?
                        readStreamContents(zipFile.getInputStream(entry)) :
                        readStreamContents(zipFile.getInputStream(entry), encoding);
            }
            finally {
                zipFile.close();
            }
        }
        catch (IOException e) {                
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean isModifiable(Object element) {
        if (element instanceof SourceArchiveEditorInput) {
            return false;
        }
        return super.isModifiable(element);
    }
}
