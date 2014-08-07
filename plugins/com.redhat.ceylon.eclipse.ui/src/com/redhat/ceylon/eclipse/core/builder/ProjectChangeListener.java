package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathContainers;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;


public class ProjectChangeListener implements IResourceChangeListener {
    
    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        try {
            event.getDelta().accept(new IResourceDeltaVisitor() {                    
                @Override
                public boolean visit(IResourceDelta delta) throws CoreException {
                    final IWorkspaceRoot workspaceRoot = getWorkspace().getRoot();
                    IResource resource = delta.getResource();
                    if (resource.equals(workspaceRoot)) {
                        return true;
                    }
                    if (resource instanceof IProject && delta.getKind()==IResourceDelta.REMOVED) {
                        CeylonBuilder.removeProject((IProject) resource);
                    }
                    else if (resource instanceof IProject && (delta.getFlags() & IResourceDelta.OPEN) != 0) {
                        final IProject project = (IProject) resource;
                        if (!project.isOpen()) {
                            CeylonBuilder.removeProject(project);
                        }
                        else if (CeylonNature.isEnabled(project)) {
                            IJavaProject javaProject = JavaCore.create(project);
                            if (javaProject != null) {
                                //List<CeylonApplicationModulesContainer> cpContainers = 
                                getCeylonClasspathContainers(javaProject);
                                /*for (CeylonApplicationModulesContainer container : cpContainers) {
                                    container.launchResolve(false, null);
                                }*/
                            }
                        }
                    }
                    return false;
                }
            });
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    
}