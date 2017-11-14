/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.ui;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.CHARSET_PROBLEM_MARKER_ID;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.InteropUtils.toCeylonString;
import static org.eclipse.ceylon.ide.eclipse.util.InteropUtils.toJavaString;
import static org.eclipse.core.resources.IResource.DEPTH_ONE;
import static org.eclipse.core.resources.IResourceDelta.CONTENT;
import static org.eclipse.core.resources.IResourceDelta.ENCODING;
import static org.eclipse.core.resources.IResourceDelta.OPEN;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;
import org.eclipse.ceylon.ide.common.model.CeylonProject;
import org.eclipse.ceylon.ide.common.model.CeylonProjectConfig;

public class CeylonEncodingSynchronizer {

    private static final CeylonEncodingSynchronizer instance = new CeylonEncodingSynchronizer();
    
    public static CeylonEncodingSynchronizer getInstance() {
        return instance;
    }

    private final IResourceChangeListener resourceChangeListener = new InternalResourceChangeListener();
    private final IResourceDeltaVisitor resourceDeltaVisitor = new InternalResourceDeltaVisitor();
    private final AtomicBoolean isSuspended = new AtomicBoolean(false);
    
    public boolean suspend() {
        return isSuspended.getAndSet(true);
    }
    
    public void unsuspend(boolean old) {
        isSuspended.set(old);
    }
    
    public void refresh(IResource resource, IProgressMonitor monitor) {
        boolean old = suspend();
        try {
            resource.refreshLocal(IResource.DEPTH_INFINITE, monitor);
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
        finally {
            unsuspend(old);
        }
    }

    public void install() {
        getWorkspace().addResourceChangeListener(resourceChangeListener);
    }

    public void uninstall() {
        getWorkspace().removeResourceChangeListener(resourceChangeListener);
    }
    
    private void synchronizeEncoding(IProject project, boolean forceEclipseEncoding) {
        if (!(project.isAccessible() && CeylonNature.isEnabled(project))) {
            return;
        }
        
        try {
            removeProblemMarker(project);
            
            String eclipseEncoding = project.getDefaultCharset();
            String configEncoding = toJavaString(modelJ2C().ceylonModel().getProject(project)
                    .getConfiguration().getProjectEncoding());

            if (forceEclipseEncoding) {
                updateEncoding(project, eclipseEncoding);
                return;
            }
            
            if (configEncoding == null) {
                updateEncoding(project, eclipseEncoding);
            }
            else if (!configEncoding.equalsIgnoreCase(eclipseEncoding)) {
                createProblemMarker(project, eclipseEncoding, configEncoding);
//                showSynchronizationDialog(project, eclipseEncoding, configEncoding);
            }
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeProblemMarker(final IProject project) {
        Job job = new Job("Remove character encoding problem marker") {                    
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    project.deleteMarkers(CHARSET_PROBLEM_MARKER_ID, false, DEPTH_ONE);
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
                return Status.OK_STATUS;
            }
        };
        job.setRule(project);
        job.schedule();
    }

    private void createProblemMarker(final IProject project,
            final String eclipseEncoding, final String configEncoding) {
        Job job = new Job("Create character encoding problem marker") {                    
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
//                    project.deleteMarkers(CHARSET_PROBLEM_MARKER_ID, false, DEPTH_ONE);
                    IMarker marker = project.createMarker(CHARSET_PROBLEM_MARKER_ID);
                    marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
                    marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
                    marker.setAttribute(IMarker.LOCATION, project.getName());
                    marker.setAttribute(IMarker.SOURCE_ID, CeylonBuilder.SOURCE);
                    marker.setAttribute(IMarker.MESSAGE, getMessage(project, 
                            eclipseEncoding, configEncoding));
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
                return Status.OK_STATUS;
            }
        };
        job.setRule(project);
        job.schedule();
    }

//    private void showSynchronizationDialog(final IProject project, final String eclipseEncoding, final String configEncoding) {
//        Display.getDefault().asyncExec(new Runnable() {
//            @Override
//            public void run() {
//                MessageDialog dialog = new MessageDialog(
//                        null, "Encoding settings synchronization?",
//                        null, getMessage(project, eclipseEncoding, configEncoding) +
//                        ". \n\nWhich encoding do you want to use?",
//                        MessageDialog.QUESTION,
//                        new String[] {
//                                "Use '" + eclipseEncoding.toLowerCase() + "'",
//                                "Use '" + configEncoding.toLowerCase() + "'" }, 0);
//                
//                int result = dialog.open();
//                if (result == 0) {
//                    updateEncoding(project, eclipseEncoding);
//                } else {
//                    updateEncoding(project, configEncoding);
//                }                                
//            }
//
//        });
//    }
    
    private static String getMessage(final IProject project,
            final String eclipseEncoding, final String configEncoding) {
        return "character encoding is out of sync: project " 
                + project.getName() + " is "
                + eclipseEncoding +
                " but " + project.getFullPath() + "/.ceylon/config specifies "
                + configEncoding + "\n" +
                " The project cannot be built if the encoding is not synchronized. Use the Quick Fix to synchronize it."
                ;
    }
    
    public void updateEncoding(IProject project, String encoding) {
        new InternalSynchronizeJob(project, encoding).schedule();
    }

    private class InternalResourceChangeListener implements IResourceChangeListener {

        @Override
        public void resourceChanged(IResourceChangeEvent event) {
            if( !isSuspended.get() ) {
                try {
                    if (event.getDelta() != null) {
                        event.getDelta().accept(resourceDeltaVisitor);
                    }
                } catch (CoreException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private class InternalResourceDeltaVisitor implements IResourceDeltaVisitor {

        @Override
        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource resource = delta.getResource();
            if (resource instanceof IWorkspace || resource instanceof IWorkspaceRoot) {
                return true;
            } else if (resource instanceof IFolder && resource.getName().equals(".ceylon")) {
                return true;
            } else if (resource instanceof IProject) {
                if (hasFlag(delta, OPEN)) {
                    synchronizeEncoding((IProject) resource, false);
                    return false;
                }
                if (hasFlag(delta, ENCODING)) {
                    synchronizeEncoding((IProject) resource, true);
                    return false;
                }
                return true;
            } else if (resource instanceof IFile) {
                if (hasFlag(delta, CONTENT)) {
                    IProject project = resource.getProject();
                    String filePath = resource.getProjectRelativePath().toString();
                    if (filePath.equals(".project") /* handle adding ceylon nature to existing project */) {
                        synchronizeEncoding(project, false);
                    }
                    if (filePath.equals(".ceylon/config")) {
                        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = modelJ2C().ceylonModel().getProject(project);
                        if (ceylonProject != null) {
                            ceylonProject.getConfiguration().refresh();
                            synchronizeEncoding(project, false);
                        }
                    }
                }
            }
            return false;
        }

    }

    private class InternalSynchronizeJob extends WorkspaceJob {

        private final IProject project;
        private final String encoding;

        private InternalSynchronizeJob(IProject project, String encoding) {
            super("Synchronize project encoding configuration");
            this.project = project;
            this.encoding = encoding;
        }

        @Override
        public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
            refresh(project.getFolder(".settings"), monitor);
            refresh(project.getFolder(".ceylon"), monitor);
            
            CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = modelJ2C().ceylonModel().getProject(project);
            if (ceylonProject != null) {
                CeylonProjectConfig config = ceylonProject.getConfiguration();
                config.refresh();
                
                try {
                    isSuspended.set(true);
                    
                    String originalEclipseEncoding = project.getDefaultCharset();
                    if (!isEquals(originalEclipseEncoding, encoding)) {
                        project.setDefaultCharset(encoding, monitor);
                    }

                    String originalConfigEncoding = toJavaString(config.getProjectEncoding());
                    if (!isEquals(originalConfigEncoding, encoding)) {
                        config.setProjectEncoding(toCeylonString(encoding));
                        config.save();
                    }
                } finally {
                    isSuspended.set(false);
                }
            }
            return Status.OK_STATUS;
        }
    }
    
    private boolean hasFlag(IResourceDelta delta, int flag) {
        return ((delta.getFlags() & flag) == flag);
    }

    private boolean isEquals(String encoding1, String encoding2) {
        return encoding1 != null ? encoding1.equalsIgnoreCase(encoding2) : false;
    }

}
