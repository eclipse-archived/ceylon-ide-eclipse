package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createErrorStatus;

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

import org.eclipse.ceylon.ide.eclipse.code.wizard.CeylonSourceStream;

public final class CreateUnitChange extends ResourceChange {
    private final IFile file;
    private final boolean preamble;
    private final String text;
    private final IProject project;
    private String name;

    public CreateUnitChange(IFile file, boolean preamble, String text,
            IProject project, String name) {
        this.file = file;
        this.preamble = preamble;
        this.text = text;
        this.project = project;
        this.name = name;
    }
    
    @Override
    public RefactoringStatus isValid(IProgressMonitor pm) 
            throws CoreException,
            OperationCanceledException {
        if (file.exists()) {
            return createErrorStatus("file already exists");
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
        return name;
    }

    @Override
    protected IResource getModifiedResource() {
        return file;
    }
    
    @Override
    public Object[] getAffectedObjects() {
        return new Object[]{file};
    }
}