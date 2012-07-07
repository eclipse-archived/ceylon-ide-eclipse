package com.redhat.ceylon.eclipse.code.editor;

import static org.eclipse.imp.utils.StreamUtils.readStreamContents;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.runtime.RuntimePlugin;
import org.eclipse.imp.utils.StreamUtils;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.editors.text.StorageDocumentProvider;

public class SourceArchiveDocumentProvider extends StorageDocumentProvider {
    
    public static boolean canHandle(IEditorInput editorInput) {
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
        else if (editorInput instanceof IStorageEditorInput) {
            return true;
        }
        return false;
    }
    
    @Override
    protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
        // If we don't do this, the resulting editor won't permit source folding
        return new AnnotationModel();
    }
    
    @Override
    protected ElementInfo createElementInfo(Object element) throws CoreException {
        ElementInfo ei= super.createElementInfo(element);
        if (element instanceof IURIEditorInput) {
            ei.fDocument= new Document(getZipEntryContents((IURIEditorInput) element));
        } 
        else if (element instanceof IStorageEditorInput) {
            ei.fDocument= new Document(getStorageContents((IStorageEditorInput) element));
        }
        return ei;
    }
    
    @Override
    protected boolean setDocumentContent(IDocument document, IEditorInput editorInput) throws CoreException {
        String contents;
        if (editorInput instanceof IURIEditorInput) {
            IURIEditorInput uriEditorInput = (IURIEditorInput) editorInput;
            contents = getZipEntryContents(uriEditorInput);
        } 
        else if (editorInput instanceof IStorageEditorInput) {
            IStorageEditorInput storageEditorInput= (IStorageEditorInput) editorInput;
            contents = getStorageContents(storageEditorInput);
        } 
        else {
            throw new IllegalArgumentException("Inappropriate type of IEditorInput passed to ZipStorageEditorDocumentProvider: " 
                    + editorInput.getClass());
        }
        document.set(contents);
        return true;
    }
    
    private String getStorageContents(IStorageEditorInput storageEditorInput) throws CoreException {
        return StreamUtils.readStreamContents(storageEditorInput.getStorage().getContents());
    }
    
    private String getZipEntryContents(IURIEditorInput uriEditorInput) throws CoreException {
        try {
            String path = uriEditorInput.getURI().getPath();
            int lastColonIdx = path.lastIndexOf('!');
            String jarPath= path.substring(0, lastColonIdx);
            String entryPath= path.substring(lastColonIdx + 2);
            ZipFile zipFile = new ZipFile(new File(jarPath));
            try {
	            ZipEntry entry= zipFile.getEntry(entryPath);
	            return readStreamContents(zipFile.getInputStream(entry));
            }
            finally {
            	zipFile.close();
            }
        } 
        catch (IOException e) {
            throw new CoreException(new Status(IStatus.ERROR, RuntimePlugin.IMP_RUNTIME, 0, 
                    "Error encountered while obtaining zip file contents", e));
        } 
    }
}
