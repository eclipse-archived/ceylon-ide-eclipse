package com.redhat.ceylon.eclipse.code.move;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.resource.DeleteResourceChange;
import org.eclipse.ltk.core.refactoring.resource.ResourceChange;

import com.redhat.ceylon.eclipse.code.wizard.CeylonSourceStream;

public final class CreateUnitChange extends ResourceChange {
    private final IFile file;
    private final boolean preamble;
    private final String text;
    private final IProject project;

    public CreateUnitChange(IFile file, boolean preamble, String text,
            IProject project) {
        this.file = file;
        this.preamble = preamble;
        this.text = text;
        this.project = project;
    }
    
    @Override
    public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,
            OperationCanceledException {
        if (file.exists()) {
            return RefactoringStatus.createErrorStatus("file already exists");
        }
        else {
            return new RefactoringStatus();
        }
    }

    @Override
    public Change perform(IProgressMonitor pm) 
            throws CoreException {
        file.create(new CeylonSourceStream(project, preamble, text), true, pm);
        return new DeleteResourceChange(file.getFullPath(), true);
    }

    @Override
    public String getName() {
        return "Create New Unit";
    }

    @Override
    protected IResource getModifiedResource() {
        return file;
    }
}