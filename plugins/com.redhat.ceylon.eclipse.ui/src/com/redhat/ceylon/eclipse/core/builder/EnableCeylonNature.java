package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.redhat.ceylon.ide.common.model.BaseCeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;

import ceylon.interop.java.CeylonStringIterable;


public class EnableCeylonNature implements IWorkbenchWindowActionDelegate {
    
    private IProject fProject;
    
    public void dispose() {}
    
    public void init(IWorkbenchWindow window) {}
    
    public void run(IAction action) {
        modelJ2C().ceylonModel().addProject(fProject);
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(fProject);
        CeylonProjectConfig config = ceylonProject.getConfiguration();
        List<String> sourceFolders = new ArrayList<>();
        for (IFolder sourceFolder : CeylonBuilder.getSourceFolders(fProject)) {
            if (sourceFolder.isLinked()) {
                sourceFolders.add(sourceFolder.getLocation().toOSString());
            } else {
                sourceFolders.add(sourceFolder.getProjectRelativePath().toString());
            }
        }
        config.setProjectSourceDirectories(new CeylonStringIterable(sourceFolders));
        config.save();
        new CeylonNature().addToProject(fProject);
    }
    
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            Object first = ss.getFirstElement();
            
            if (first instanceof IProject) {
                fProject = (IProject) first;
            } else if (first instanceof IJavaProject) {
                fProject = ((IJavaProject) first).getProject();
            }
        }
    }
}
