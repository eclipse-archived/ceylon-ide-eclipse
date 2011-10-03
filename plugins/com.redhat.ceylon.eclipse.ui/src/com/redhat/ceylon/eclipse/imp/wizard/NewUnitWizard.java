package com.redhat.ceylon.eclipse.imp.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;

public class NewUnitWizard extends Wizard implements INewWizard {
    
    IStructuredSelection selection;
    IWorkbench workbench;
    NewUnitWizardPage page;
    
    public NewUnitWizard() {}
    
    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        this.workbench = workbench;
    }
    
    @Override
    public boolean performFinish() {
        FileCreationOp op = new FileCreationOp();
        try {
            getContainer().run(true, true, op);
        } 
        catch (InvocationTargetException e) {
            e.printStackTrace();
            return false;
        } 
        catch (InterruptedException e) {
            return false;
        }
        CeylonSourcePositionLocator.gotoLocation(op.result.getFullPath(), 0);
        return true;
    }
    
    @Override
    public void addPages() {
        super.addPages();
        if (page == null) {
            page= new NewUnitWizardPage();
            page.init(selection);
        }
        addPage(page);
    }

    class FileCreationOp implements IRunnableWithProgress {
        IFile result;
        public void run(IProgressMonitor monitor) {
            IPath path = page.getPackageFragment().getPath().append(page.getUnitName()+".ceylon");
            IProject project = page.getSourceDir().getJavaProject().getProject();
            IFile header = project.getFile("header.ceylon");
            InputStream his = new ByteArrayInputStream(new byte[0]);
            if ( page.isIncludePreamble() && 
                    header.exists() && header.isAccessible() ) {
                try {
                    his = header.getContents();
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
            }
            result = project.getFile(path.makeRelativeTo(project.getFullPath()));
            try {
                result.create(his, false, monitor);
            }
            catch (CoreException ce) {
                ce.printStackTrace();
            }
        }
    }
}
