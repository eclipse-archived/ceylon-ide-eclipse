package org.eclipse.ceylon.ide.eclipse.code.editor;

import java.net.URI;

import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.part.FileEditorInput;

import org.eclipse.ceylon.ide.eclipse.core.external.ExternalSourceArchiveManager;

public class SourceArchiveEditorInput extends FileEditorInput {
    public SourceArchiveEditorInput(IFile file) {
        super(file);
    }
    
    @Override
    public IPath getPath() {
        return ExternalSourceArchiveManager.toFullPath(getFile());
    }
    
    @Override
    public URI getURI() {
        return ((Resource) getFile()).getStore().toURI();
    }
    
    @Override
    public String getToolTipText() {
        IPath sourceArchiveFullPath = ExternalSourceArchiveManager.toFullPath(getFile());
        return sourceArchiveFullPath != null ? sourceArchiveFullPath.makeRelative().toOSString() : super.getToolTipText();
    }
}
