package com.redhat.ceylon.eclipse.core.external;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.internal.resources.Resource;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.core.util.Messages;
import org.eclipse.jdt.internal.core.util.Util;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class ExternalSourceArchiveManager implements IResourceChangeListener {
    private static final String EXTERNAL_PROJECT_NAME = "Ceylon Source Archives";
    private static final String LINKED_FOLDER_NAME = "archive-";
    private Map<IPath, IResource> archives;
    private Set<IPath> pendingSourceArchives; // subset of keys of 'archives', for which linked folders haven't been created yet.
    /* Singleton instance */
    private static ExternalSourceArchiveManager MANAGER;

    static {
        MANAGER = new ExternalSourceArchiveManager();
    }
    
    private ExternalSourceArchiveManager() {
    }
    
    public static ExternalSourceArchiveManager getExternalSourceArchiveManager() {
        return MANAGER;
    }
    
    /*
     * Returns a set of external path to external folders referred to on the given classpath.
     * Returns null if none.
     */
    public static Set<IPath> getExternalSourceArchives(Collection<JDTModule> modules) {
        if (modules == null)
            return null;
        Set<IPath> folders = null;
        for (JDTModule  module : modules) {
            if (module.isCeylonArchive()) {
                IPath archivePath = Path.fromOSString(module.getSourceArchivePath());
                if (isExternalSourceArchivePath(archivePath)) {
                    if (folders == null)
                        folders = new HashSet<>();
                    folders.add(archivePath);
                }
            }
        }
        return folders;
    }


    public static boolean isExternalSourceArchivePath(IPath externalPath) {
        if (externalPath == null)
            return false;
        File externalSourceArchive = externalPath.toFile();
        if (! externalSourceArchive.isFile())
            return false;
        if (! externalSourceArchive.getName().endsWith(ArtifactContext.SRC))
            return false;
        if (!externalSourceArchive.exists())
            return false;
        return true;
    }

    public static boolean isInternalPathForExternalSourceArchive(IPath resourcePath) {
        return EXTERNAL_PROJECT_NAME.equals(resourcePath.segment(0));
    }

    public IFolder addSourceArchive(IPath externalSourceArchivePath, boolean scheduleForCreation) {
        return addSourceArchive(externalSourceArchivePath, getExternalSourceArchivesProject(), scheduleForCreation);
    }

    private IFolder addSourceArchive(IPath externalSourceArchivePath, IProject externalSourceArchivesProject, boolean scheduleForCreation) {
        if (archives == null) {
            return null;
        }
        IResource existing = archives.get(externalSourceArchivePath);
        if (existing != null && existing.exists()) {
            return (IFolder) existing;
        }
        IFolder result = externalSourceArchivesProject.getFolder(new Path(externalSourceArchivePath.toString() + "!"));
        if (scheduleForCreation) {
            synchronized(this) {
                if (pendingSourceArchives == null)
                    pendingSourceArchives = Collections.synchronizedSet(new HashSet<IPath>());
            }
            pendingSourceArchives.add(externalSourceArchivePath);
        }
        archives.put(externalSourceArchivePath, result);
        return result;
    }
    
    /** 
     * Try to remove the argument from the list of folders pending for creation.
     * @param externalPath to link to
     * @return true if the argument was found in the list of pending folders and could be removed from it.
     */
    public synchronized boolean removePendingSourceArchive(Object externalPath) {
        if (this.pendingSourceArchives == null)
            return false;
        return this.pendingSourceArchives.remove(externalPath);
    }

    public IFolder createLinkFolder(IPath externalSourceArchivePath, boolean refreshIfExistAlready, IProgressMonitor monitor) throws CoreException {
        IProject externalSourceArchivesProject = createExternalSourceArchivesProject(monitor); // run outside synchronized as this can create a resource
        return createLinkFolder(externalSourceArchivePath, refreshIfExistAlready, externalSourceArchivesProject, monitor);
    }

    private void createVirtualFolderIfNecessary(IContainer container, IProgressMonitor monitor) throws CoreException {
        if (container instanceof IFolder && ! container.exists()) {
            createVirtualFolderIfNecessary(container.getParent(), monitor);
            ((IFolder) container).create(IResource.VIRTUAL, false, monitor);
        }
    }
    
    private IFolder createLinkFolder(IPath externalSourceArchivePath, boolean refreshIfExistAlready,
                                    IProject externalSourceArchivesProject, IProgressMonitor monitor) throws CoreException {
        
        IFolder result = addSourceArchive(externalSourceArchivePath, externalSourceArchivesProject, false);
        URI uri = result.getLocationURI();
        if (uri == null || !CeylonArchiveFileSystem.SCHEME_CEYLON_ARCHIVE.equals(uri.getScheme())) {
            createVirtualFolderIfNecessary(result.getParent(), monitor);
            if (result.exists()) {
                result.delete(true, monitor);
            }
            result.createLink(CeylonArchiveFileSystem.toCeylonArchiveURI(externalSourceArchivePath, Path.EMPTY), IResource.ALLOW_MISSING_LOCAL, monitor);
        }
        else if (refreshIfExistAlready)
            result.refreshLocal(IResource.DEPTH_INFINITE,  monitor);
        return result;
    }

    public void createPendingSourceArchives(IProgressMonitor monitor) throws CoreException{
        synchronized (this) {
            if (pendingSourceArchives == null || pendingSourceArchives.isEmpty()) return;
        }
        
        IProject externalSourceArchivesProject = null;
        externalSourceArchivesProject = createExternalSourceArchivesProject(monitor);
        // To avoid race condition (from addSourceArchive and removeSourceArchive, load the map elements into an array and clear the map immediately.
        // The createLinkFolder being in the synchronized block can cause a deadlock and hence keep it out of the synchronized block. 
        Object[] arrayOfSourceArchives = null;
        synchronized (pendingSourceArchives) {
            arrayOfSourceArchives = pendingSourceArchives.toArray();
            pendingSourceArchives.clear();
        }

        for (int i=0; i < arrayOfSourceArchives.length; i++) {
            try {
                createLinkFolder((IPath) arrayOfSourceArchives[i], false, externalSourceArchivesProject, monitor);
            } catch (CoreException e) {
                Util.log(e, "Error while creating a link for external folder :" + arrayOfSourceArchives[i]); //$NON-NLS-1$
            }
        }
    }
    
    private void deleteVirtualFolderIfPossible(IContainer container, IProgressMonitor monitor) {
        try {
            if (container.exists() 
                    && container instanceof IFolder
                    && container.isVirtual() 
                    && container.members().length == 0) {
                container.delete(false, monitor);
                deleteVirtualFolderIfPossible(container.getParent(), monitor);
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
    

    public void cleanUp(IProgressMonitor monitor) throws CoreException {
        ArrayList<Entry<IPath, IResource>> toDelete = getSourceArchivesToCleanUp(monitor);
        if (toDelete == null)
            return;
        for (Iterator<Entry<IPath, IResource>> iterator = toDelete.iterator(); iterator.hasNext();) {
            Entry<IPath, IResource> entry = iterator.next();
            IFolder folder = (IFolder) entry.getValue();
            folder.delete(true, monitor);
            deleteVirtualFolderIfPossible(folder.getParent(), monitor);
            IPath key = (IPath) entry.getKey();
            archives.remove(key);
        }
        IProject project = getExternalSourceArchivesProject();
        if (project.isAccessible() && project.members().length == 1/*remaining member is .project*/)
            project.delete(true, monitor);
    }

    private ArrayList<Entry<IPath, IResource>> getSourceArchivesToCleanUp(IProgressMonitor monitor) throws CoreException {
        if (archives == null) {
            return null;
        }
        
        Set<IPath> projectSourcePaths = null;
        for (IProject project : CeylonBuilder.getProjects()) {
            for (JDTModule module : CeylonBuilder.getProjectExternalModules(project)) {
                String sourceArchivePathString = module.getSourceArchivePath();
                if (sourceArchivePathString!= null) {
                    if (projectSourcePaths == null) {
                        projectSourcePaths = new HashSet<>();
                    }
                    projectSourcePaths.add(Path.fromOSString(sourceArchivePathString));
                }
            }
        }
        if (projectSourcePaths == null)
            return null;
        ArrayList<Entry<IPath, IResource>> result = null;
        synchronized (archives) {
            Iterator<Entry<IPath, IResource>> iterator = archives.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<IPath, IResource> entry = iterator.next();
                IPath path = entry.getKey();
                if (!projectSourcePaths.contains(path)) {
                    if (entry.getValue() != null) {
                        if (result == null)
                            result = new ArrayList<>();
                        result.add(entry);
                    }
                }
            }
        }
        return result;
    }

    public IProject getExternalSourceArchivesProject() {
        return ResourcesPlugin.getWorkspace().getRoot().getProject(EXTERNAL_PROJECT_NAME);
    }
    public IProject createExternalSourceArchivesProject(IProgressMonitor monitor) throws CoreException {
        IProject project = getExternalSourceArchivesProject();
        if (!project.isAccessible()) {
            if (!project.exists()) {
                createExternalSourceArchivesProject(project, monitor);
            }
            openExternalSourceArchivesProject(project, monitor);
        }
        return project;
    }

    /*
     * Attempt to open the given project (assuming it exists).
     * If failing to open, make all attempts to recreate the missing pieces.
     */
    private void openExternalSourceArchivesProject(IProject project, IProgressMonitor monitor) throws CoreException {
        try {
            project.open(monitor);
        } catch (CoreException e1) {
            if (e1.getStatus().getCode() == IResourceStatus.FAILED_READ_METADATA) {
                // workspace was moved 
                project.delete(false/*don't delete content*/, true/*force*/, monitor);
                createExternalSourceArchivesProject(project, monitor);
            } else {
                // .project or folder on disk have been deleted, recreate them
                IPath stateLocation = CeylonPlugin.getInstance().getStateLocation();
                IPath projectPath = stateLocation.append(EXTERNAL_PROJECT_NAME);
                projectPath.toFile().mkdirs();
                try {
                    FileOutputStream output = new FileOutputStream(projectPath.append(".project").toOSString()); //$NON-NLS-1$
                    try {
                        output.write((
                                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //$NON-NLS-1$
                                "<projectDescription>\n" + //$NON-NLS-1$
                                "   <name>" + EXTERNAL_PROJECT_NAME + "</name>\n" + //$NON-NLS-1$ //$NON-NLS-2$
                                "   <comment></comment>\n" + //$NON-NLS-1$
                                "   <projects>\n" + //$NON-NLS-1$
                                "   </projects>\n" + //$NON-NLS-1$
                                "   <buildSpec>\n" + //$NON-NLS-1$
                                "   </buildSpec>\n" + //$NON-NLS-1$
                                "   <natures>\n" + //$NON-NLS-1$
                                "   </natures>\n" + //$NON-NLS-1$
                                "</projectDescription>").getBytes()); //$NON-NLS-1$
                    } finally {
                        output.close();
                    }
                } catch (IOException e) {
                    // fallback to re-creating the project
                    project.delete(false/*don't delete content*/, true/*force*/, monitor);
                    createExternalSourceArchivesProject(project, monitor);
                }
            }
            project.open(monitor);
        }
    }


    private void createExternalSourceArchivesProject(IProject project, IProgressMonitor monitor) throws CoreException {
        IProjectDescription desc = project.getWorkspace().newProjectDescription(project.getName());
        IPath stateLocation = CeylonPlugin.getInstance().getStateLocation();
        desc.setLocation(stateLocation.append(EXTERNAL_PROJECT_NAME));
        project.create(desc, IResource.HIDDEN, monitor);
    }

    public IFolder getSourceArchive(IPath externalSourceArchivePath) {
        return (IFolder) (archives == null ? null : archives.get(externalSourceArchivePath));
    }
    
    public void initialize() {
        final Map<IPath, IResource> tempSourceArchives = new HashMap<>();
        IProject project = getExternalSourceArchivesProject();
        try {
            doInitialize(tempSourceArchives, project);

            for (IResource value : tempSourceArchives.values()) {
                if (value.getName().startsWith(LINKED_FOLDER_NAME)) {
                    try {
                        project.delete(true, null);
                        openExternalSourceArchivesProject(project, null);
                        doInitialize(tempSourceArchives, project);
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            }
            
        } catch (CoreException e) {
            Util.log(e, "Exception while initializing external folders");
        }
        archives = Collections.synchronizedMap(tempSourceArchives);

        for (final IProject ceylonProject : CeylonBuilder.getProjects()) {
            if (CeylonBuilder.isContainerInitialized(ceylonProject)) {
                // the project container was already initialized
                //    => restarts an update of the source archives
                Job refreshProjectExternalSourceArchive = new Job("Update External Ceylon Sources Archives for project " + ceylonProject.getName()) {
                    protected IStatus run(IProgressMonitor monitor) {
                        ExternalSourceArchiveManager esam = ExternalSourceArchiveManager.getExternalSourceArchiveManager();
                        try {
                            esam.updateProjectSourceArchives(ceylonProject, monitor);
                        } catch (CoreException e) {
                            e.printStackTrace();
                        }
                        return Status.OK_STATUS;
                    };
                };
                refreshProjectExternalSourceArchive.setRule(ResourcesPlugin.getWorkspace().getRoot());
                refreshProjectExternalSourceArchive.schedule();
            }
        }
    }

    protected void doInitialize(final Map<IPath, IResource> tempSourceArchives,
            IProject project) throws CoreException {
        if (!project.isAccessible()) {
            if (project.exists()) {
                // workspace was moved 
                openExternalSourceArchivesProject(project, null/*no progress*/);
            } else {
                // if project doesn't exist, do not open and recreate it as it means that there are no external source archives
                return;
            }
        }
        project.accept(new IResourceVisitor() {
            
            @Override
            public boolean visit(IResource resource) throws CoreException {
                if (resource instanceof IFolder
                        && resource.isLinked()
                        && ! resource.isVirtual()
                        && resource.isSynchronized(IResource.DEPTH_ZERO)
                        && resource.exists()) {
                    URI uri = resource.getLocationURI();
                    if (uri != null && CeylonArchiveFileSystem.SCHEME_CEYLON_ARCHIVE.equals(uri.getScheme())) {
                        String path = uri.getPath();
                        if (path != null) {
                            if (path.endsWith(CeylonArchiveFileSystem.JAR_SUFFIX)) {
                                path = path.substring(0, path.length() - 2);
                            }
                            IPath externalSourceArchivePath = new Path(path);
                            tempSourceArchives.put(externalSourceArchivePath, resource);
                        }
                    }
                    return false;
                }

                return true;
            }
        }, IResource.DEPTH_INFINITE, IContainer.INCLUDE_HIDDEN);
    }

    private void runRefreshJob(Collection<IPath> paths) {
        Job[] jobs = Job.getJobManager().find(ResourcesPlugin.FAMILY_MANUAL_REFRESH);
        RefreshJob refreshJob = null;
        if (jobs != null) {
            for (int index = 0; index < jobs.length; index++) {
                // We are only concerned about ExternalSourceArchiveManager.RefreshJob
                if(jobs[index] instanceof RefreshJob) {
                    refreshJob =  (RefreshJob) jobs[index];
                    refreshJob.addSourceArchivesToRefresh(paths);
                    if (refreshJob.getState() == Job.NONE) {
                        refreshJob.schedule();
                    }
                    break;
                }
            }
        }
        if (refreshJob == null) {
            refreshJob = new RefreshJob(new Vector<>(paths));
            refreshJob.schedule();
        }
    }
    /*
     * Refreshes the external folders referenced on the classpath of the given source project
     */
    public void refreshReferences(final IProject[] sourceProjects, IProgressMonitor monitor) {
        IProject externalProject = getExternalSourceArchivesProject();
        Set<IPath> externalSourceArchives = null;
        for (IProject project : sourceProjects) {
            if (project.equals(externalProject))
                continue;
            if (!CeylonNature.isEnabled(project))
                continue;

            Set<IPath> sourceArchivesInProject = getExternalSourceArchives(CeylonBuilder.getProjectExternalModules(project));
            
            if (sourceArchivesInProject == null || sourceArchivesInProject.size() == 0)
                continue;
            if (externalSourceArchives == null)
                externalSourceArchives = new HashSet<>();
            
            externalSourceArchives.addAll(sourceArchivesInProject);
        }
        if (externalSourceArchives == null) 
            return;

        runRefreshJob(externalSourceArchives);
    }
    
    public void refreshReferences(IProject source, IProgressMonitor monitor) {
        IProject externalProject = getExternalSourceArchivesProject();
        if (source.equals(externalProject))
            return;
        if (!CeylonNature.isEnabled(source))
            return;
        Set<IPath> externalSourceArchives = getExternalSourceArchives(CeylonBuilder.getProjectExternalModules(source));
        if (externalSourceArchives == null)
            return;
        
        runRefreshJob(externalSourceArchives);
        return;
    }

    public IFolder removeSourceArchive(IPath externalSourceArchivePath) {
        return (IFolder) (archives == null ? null : archives.remove(externalSourceArchivePath));
    }

    class RefreshJob extends Job {
        Vector<IPath> externalSourceArchives = null;
        RefreshJob(Vector<IPath> externalSourceArchives){
            super(Messages.refreshing_external_folders);
            this.externalSourceArchives = externalSourceArchives;
        }
        
        public boolean belongsTo(Object family) {
            return family == ResourcesPlugin.FAMILY_MANUAL_REFRESH;
        }
        
        /*
         * Add the collection of paths to be refreshed to the already 
         * existing list of paths.  
         */
        public void addSourceArchivesToRefresh(Collection<IPath> paths) {
            if (!paths.isEmpty() && externalSourceArchives == null) {
                externalSourceArchives = new Vector<IPath>(); 
            }
            Iterator<IPath> it = paths.iterator();
            while(it.hasNext()) {
                IPath path = it.next();
                if (!externalSourceArchives.contains(path)) {
                    externalSourceArchives.add(path);
                }
            }
        }
        
        protected IStatus run(IProgressMonitor pm) {
            try {
                if (externalSourceArchives == null) 
                    return Status.OK_STATUS;
                IPath externalPath = null;
                for (int index = 0; index < externalSourceArchives.size(); index++ ) {
                    if ((externalPath = externalSourceArchives.get(index)) != null) {
                        IFolder sourceArchive = getSourceArchive(externalPath);
                        if (sourceArchive != null)
                            sourceArchive.refreshLocal(IResource.DEPTH_INFINITE, pm);
                    }
                    // Set the processed ones to null instead of removing the element altogether,
                    // so that they will not be considered as duplicates.
                    // This will also avoid elements being shifted to the left every time an element
                    // is removed. However, there is a risk of Collection size to be increased more often.
                    externalSourceArchives.setElementAt(null, index);
                }
            } catch (CoreException e) {
                return e.getStatus();
            }
            return Status.OK_STATUS;
        }
    }

    @Override
    public void resourceChanged(IResourceChangeEvent event) {
        switch(event.getType()) {
        case IResourceChangeEvent.PRE_REFRESH :
            IProject [] projects = null;
            Object o = event.getSource();
            if (o instanceof IProject) {
                projects = new IProject[] { (IProject) o };
            } else if (o instanceof IWorkspace) {
                // The single workspace refresh
                // notification we see, implies that all projects are about to be refreshed.
                 projects = ((IWorkspace) o).getRoot().getProjects(IContainer.INCLUDE_HIDDEN);
            }
            // Refresh all project references together in a single job
            refreshReferences(projects, null);
            
            return;
        }
    }
    
    public static IPath getSourceArchiveFullPath(IFolder sourceArchiveFolder) {
        
        if (MANAGER.archives != null) {
            for (Entry<IPath, IResource> entry : MANAGER.archives.entrySet()) {
                if (entry.getValue().equals(sourceArchiveFolder)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }

    public static boolean isInSourceArchive(IResource resource) {
        return resource != null && resource.getProject().equals(getExternalSourceArchiveManager().getExternalSourceArchivesProject());
    }

    public static boolean isTheSourceArchiveProject(IProject project) {
        return project != null && project.equals(getExternalSourceArchiveManager().getExternalSourceArchivesProject());
    }
    
    public static IPath toFullPath(IResource resource) {
        if (resource == null) {
            return null;
        }
        IProject project = resource.getProject();
        if (! project.equals(MANAGER.getExternalSourceArchivesProject())) {
            return null;
        }
        if (resource.isSynchronized(IResource.DEPTH_ZERO) 
                && resource.exists()) {
            IPath path = new Path(resource.getLocationURI().getPath());
            if (path != null && path.toString().contains(".src!")) {
                return path;
            }
        }
        return null;
    }

    public static IResource toResource(IPath sourceArchiveEntryPath) {
        String entryPathString = sourceArchiveEntryPath.toString();
        int jarSuffixIndex = entryPathString.indexOf(CeylonArchiveFileSystem.JAR_SUFFIX);
        if (jarSuffixIndex > 0) {
            IPath archivePath = new Path(entryPathString.substring(0, jarSuffixIndex));
            IFolder sourceArchiveFolder = getExternalSourceArchiveManager().getSourceArchive(archivePath);
            if (sourceArchiveFolder != null) {
                IPath entryPath = new Path(entryPathString.substring(jarSuffixIndex + 2));
                IResource resource = sourceArchiveFolder.findMember(entryPath);
                return resource;
            }
        }
        return null;
    }

    public static IResource toResource(URI sourceArchiveEntryURI) {
        String scheme = sourceArchiveEntryURI.getScheme();
        if (EFS.SCHEME_FILE.equals(scheme) || 
                CeylonArchiveFileSystem.SCHEME_CEYLON_ARCHIVE.equals(scheme)) {
            return toResource(new Path(sourceArchiveEntryURI.getPath()));
        }
        return null;
    }
    
    public void updateProjectSourceArchives(IProject project, IProgressMonitor monitor) throws CoreException {
        if (archives != null) {
            if (CeylonBuilder.allClasspathContainersInitialized()) {
                cleanUp(monitor);
            }
            Set<IPath> sourceArchives = getExternalSourceArchives(CeylonBuilder.getProjectExternalModules(project));
            if (sourceArchives!=null) {
                for (IPath sourceArchivePath : sourceArchives) {
                    IFolder sourceArchive = getSourceArchive(sourceArchivePath);
                    if (sourceArchive == null || !sourceArchive.exists()) {
                        addSourceArchive(sourceArchivePath, true);
                    }
                }
            }
            createPendingSourceArchives(monitor);
        }
    }

    public static IPath getSourceArchiveEntryPath(IFile file) {
        IPath relativePath = null;
        IFileStore store = ((Resource) file).getStore();
        if (store instanceof CeylonArchiveFileStore) {
            CeylonArchiveFileStore cafs = (CeylonArchiveFileStore) store;
            relativePath = cafs.getEntryPath();
        }
        return relativePath;
    }
}
