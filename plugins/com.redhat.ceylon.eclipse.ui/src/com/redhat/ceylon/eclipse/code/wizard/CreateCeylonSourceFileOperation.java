package com.redhat.ceylon.eclipse.code.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ide.undo.CreateFileOperation;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

class CreateCeylonSourceFileOperation extends AbstractOperation {

    private final IPackageFragmentRoot sourceDir;
    private final IPackageFragment packageFragment;
    private final String unitName;
    private final String contents;
    private final boolean includePreamble;    
    private IFile result;
    private CreateFileOperation op;
    
    IFile getResult() {
        return result;
    }
    
    CreateCeylonSourceFileOperation(IPackageFragmentRoot sourceDir,
            IPackageFragment packageFragment, String unitName, boolean includePreamble, 
            String contents, Shell shell) {
        this("New Ceylon Source File", sourceDir, packageFragment, unitName, 
                includePreamble, contents, shell);
    }
    
    CreateCeylonSourceFileOperation(String label, IPackageFragmentRoot sourceDir,
            IPackageFragment packageFragment, String unitName, boolean includePreamble, 
            String contents, Shell shell) {
        super(label);
        this.sourceDir = sourceDir;
        this.packageFragment = packageFragment;
        this.unitName = unitName;
        this.includePreamble = includePreamble;
        this.contents = contents;
    }
    
    private static InputStream getHeader(IProject project, boolean includePreamble) {
        IFile header = project.getFile("header.ceylon");
        InputStream his = new ByteArrayInputStream(new byte[0]);
        if (includePreamble && header.exists() && 
                header.isAccessible()) {
            try {
                his = header.getContents();
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
            
        }
        return his;
    }
    
    
    @Override
    public IStatus execute(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        IPath path = packageFragment.getPath().append(unitName + ".ceylon");
        IProject project = sourceDir.getJavaProject().getProject();
        result = project.getFile(path.makeRelativeTo(project.getFullPath()));
        if (!result.exists()) {
            op = new CreateFileOperation(result, null,
                    getHeader(sourceDir.getJavaProject().getProject(), includePreamble), 
                    "Create Ceylon Source File");
            IStatus status = op.execute(monitor, info);
            if (!status.isOK()) {
                return status;
            }
        }
        try {
            result.appendContents(new ByteArrayInputStream(contents.getBytes()), 
                    false, false, monitor);
        }
        catch (CoreException e) {
            return new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, 
                    "Could not append contents to new file");
        }
        return Status.OK_STATUS;
    }

    @Override
    public IStatus redo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        return execute(monitor, info);
    }

    @Override
    public IStatus undo(IProgressMonitor monitor, IAdaptable info)
            throws ExecutionException {
        if (op == null) {
            return Status.OK_STATUS;
        }
        else {
            return op.undo(monitor, info);
        }
    }
    
}