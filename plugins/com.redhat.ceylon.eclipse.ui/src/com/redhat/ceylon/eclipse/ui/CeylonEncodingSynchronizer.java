package com.redhat.ceylon.eclipse.ui;

import static org.eclipse.core.resources.IResourceDelta.CONTENT;
import static org.eclipse.core.resources.IResourceDelta.ENCODING;
import static org.eclipse.core.resources.IResourceDelta.OPEN;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;

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
            unsuspend(old);;
        }
    }

    public void install() {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceChangeListener);
    }

    public void uninstall() {
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceChangeListener);
    }
    
    private void synchronizeEncoding(IProject project, boolean forceEclipseEncoding) {
        if (!(project.isOpen() && CeylonNature.isEnabled(project))) {
            return;
        }
        
        try {
            String eclipseEncoding = project.getDefaultCharset();
            String configEncoding = CeylonProjectConfig.get(project).getProjectEncoding();

            if (forceEclipseEncoding) {
                updateEncoding(project, eclipseEncoding);
                return;
            }
            
            if (configEncoding == null) {
                updateEncoding(project, eclipseEncoding);
            } else if (!configEncoding.equalsIgnoreCase(eclipseEncoding)) {
                showSynchronizationDialog(project, eclipseEncoding, configEncoding);
            }
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    private void showSynchronizationDialog(final IProject project, final String eclipseEncoding, final String configEncoding) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog dialog = new MessageDialog(
                        null, "Encoding settings synchronization?",
                        null, "Encoding settings for project '" + project.getName()
                                + "' is out of sync. Eclipse configuration is set to '"
                                + eclipseEncoding.toLowerCase() +
                                "', but in ceylon configuration file is '"
                                + configEncoding.toLowerCase() +
                                "'. \n\nWhich encoding do you want to use?",
                        MessageDialog.QUESTION,
                        new String[] {
                                "Use '" + eclipseEncoding.toLowerCase() + "'",
                                "Use '" + configEncoding.toLowerCase() + "'" }, 0);
                
                int result = dialog.open();
                if (result == 0) {
                    updateEncoding(project, eclipseEncoding);
                } else {
                    updateEncoding(project, configEncoding);
                }                                
            }
        });
    }
    
    private void updateEncoding(IProject project, String encoding) {
        InternalSynchronizeJob job = new InternalSynchronizeJob(project, encoding);
        job.schedule();
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
                        CeylonProjectConfig.get(project).refresh();
                        synchronizeEncoding(project, false);
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
            CeylonProjectConfig config = CeylonProjectConfig.get(project);
            try {
                isSuspended.set(true);
                
                String originalEclipseEncoding = project.getDefaultCharset();
                if (!isEquals(originalEclipseEncoding, encoding)) {
                    project.setDefaultCharset(encoding, monitor);
                }

                String originalConfigEncoding = config.getProjectEncoding();
                if (!isEquals(originalConfigEncoding, encoding)) {
                    config.setProjectEncoding(encoding);
                    config.save();
                }

                return Status.OK_STATUS;
            } finally {
                isSuspended.set(false);
            }
        }        
    }
    
    private boolean hasFlag(IResourceDelta delta, int flag) {
        return ((delta.getFlags() & flag) == flag);
    }

    private boolean isEquals(String encoding1, String encoding2) {
        return encoding1 != null ? encoding1.equalsIgnoreCase(encoding2) : false;
    }

}
