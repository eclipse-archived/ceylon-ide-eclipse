package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.ide.common.util.toCeylonStringIterable_.toCeylonStringIterable;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.redhat.ceylon.eclipse.core.model.modelJ2C;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;


public class EnableCeylonNature implements IWorkbenchWindowActionDelegate {
    
    private IProject fProject;
    
    public void dispose() {}
    
    public void init(IWorkbenchWindow window) {}
    
    public void run(IAction action) {
        modelJ2C.ceylonModel().addProject(fProject);
        CeylonProject<IProject> ceylonProject = modelJ2C.ceylonModel().getProject(fProject);
        CeylonProjectConfig<IProject> config = ceylonProject.getConfiguration();
        IJavaProject javaProject = JavaCore.create(fProject);
        List<String> sourceFolders = new ArrayList<>();
        IWorkspaceRoot workspaceRoot = fProject.getWorkspace().getRoot();
        try {
            for (IClasspathEntry root : javaProject.getRawClasspath()) {
                if (CeylonBuilder.isCeylonSourceEntry(root)) {
                    IFolder folder = workspaceRoot.getFolder(root.getPath());
                    if (folder.isLinked()) {
                        sourceFolders.add(folder.getLocation().toOSString());
                    } else {
                        sourceFolders.add(folder.getProjectRelativePath().toString());
                    }
                }
            }
        } catch (JavaModelException e) {}
        config.setProjectSourceDirectories(toCeylonStringIterable(sourceFolders));
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
