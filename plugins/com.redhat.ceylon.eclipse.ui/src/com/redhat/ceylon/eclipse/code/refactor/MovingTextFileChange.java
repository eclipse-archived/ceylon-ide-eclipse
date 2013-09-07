package com.redhat.ceylon.eclipse.code.refactor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.TextFileChange;

public final class MovingTextFileChange extends TextFileChange {
    private final IFile file;

    public MovingTextFileChange(String name, IFile file, IFile file2) {
        super(name, file);
        this.file = file2;
    }

    public org.eclipse.jface.text.IDocument getPreviewDocument(IProgressMonitor pm) 
            throws CoreException {
        TextFileChange tfc = new TextFileChange(file.getName(), file);
        tfc.setEdit(getEdit().copy());
        return tfc.getPreviewDocument(pm);
    }
}