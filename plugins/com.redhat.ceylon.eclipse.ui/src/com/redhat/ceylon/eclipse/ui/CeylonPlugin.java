package com.redhat.ceylon.eclipse.ui;

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.ModelException;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.builder.CeylonNature;

public class CeylonPlugin extends PluginBase implements ICeylonResources {

	public static final String PLUGIN_ID = "com.redhat.ceylon.eclipse.ui";
	public static final String LANGUAGE_ID = "ceylon";
	public static final String EDITOR_ID = PLUGIN_ID + ".editor";

	/**
	 * The unique instance of this plugin class
	 */
	protected static CeylonPlugin pluginInstance;
	
	private File ceylonRepository = null;

	/**
     * - If the 'ceylon.repo' property exist, returns the corresponding file
     * <br>
     * - Else return the internal defaultRepository folder
	 * 
	 * @return
	 */
	public File getCeylonRepository() {
        return ceylonRepository;
    }

    public static CeylonPlugin getInstance() {
		if (pluginInstance==null) new CeylonPlugin();
		return pluginInstance;
	}

	public CeylonPlugin() {
		pluginInstance = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
        String ceylonRepositoryProperty = System.getProperty("ceylon.repo", "");
        ceylonRepository = getCeylonRepository(ceylonRepositoryProperty);
	    super.start(context);
//        copyDefaultRepoIfNecessary();
        addResourceFilterPreference();
        registerProjectOpenCloseListener();
        runInitialBuild();
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
	    super.stop(context);
	    unregisterProjectOpenCloseListener();
	}

    private void addResourceFilterPreference() throws BackingStoreException {
        new Job("Add Resource Filter for Ceylon projects") {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IEclipsePreferences instancePreferences = InstanceScope.INSTANCE
                        .getNode(JavaCore.PLUGIN_ID);
                /*IEclipsePreferences defaultPreferences = DefaultScope.INSTANCE
                        .getNode(JavaCore.PLUGIN_ID);*/
                String filter = instancePreferences.get(JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, "");
                if (filter.isEmpty()) {
                    filter = "*.launch, *.ceylon";
                }
                else if (!filter.contains("*.ceylon")) {
                    filter += ", *.ceylon";
                }
                instancePreferences.put(JavaCore.CORE_JAVA_BUILD_RESOURCE_COPY_FILTER, filter);
                try {
                    instancePreferences.flush();
                } 
                catch (BackingStoreException e) {
                    e.printStackTrace();
                }
                return Status.OK_STATUS;
            }
            
        }.schedule();
    }

    
    /**
     * - If the property is not empty, return the corresponding file
     * <br>
     * - Else return the internal defaultRepository folder
     * 
     * @param ceylonRepositoryProperty
     * @return
     * 
     */
    public static File getCeylonRepository(String ceylonRepositoryProperty) {
        File ceylonRepository=null;
        if (! "".equals(ceylonRepositoryProperty)) {
            File ceylonRepositoryPath = new java.io.File(ceylonRepositoryProperty);
            if (ceylonRepositoryPath.exists()) {
                ceylonRepository = ceylonRepositoryPath;
            }
        }
        if (ceylonRepository == null) {
            try {
                Bundle bundle = Platform.getBundle(CeylonPlugin.PLUGIN_ID);
                Path path = new Path("defaultRepository");
                URL eclipseUrl = FileLocator.find(bundle, path, null);
                URL fileURL = FileLocator.resolve(eclipseUrl);
                String urlPath = fileURL.getPath();
                URI fileURI = new URI("file", null, urlPath, null);
                ceylonRepository = new File(fileURI);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ceylonRepository;
    }

	@Override
	public String getID() {
		return PLUGIN_ID;
	}

	@Override
	public String getLanguageID() {
		return LANGUAGE_ID;
	}

    private static IPath iconsPath = new Path("icons/");

    private ImageDescriptor image(String file) {
        URL url = FileLocator.find(getBundle(), 
                iconsPath.append(file), null);
        if (url!=null) {
        	return ImageDescriptor.createFromURL(url);
        }
        else {
        	return null;
        }
	}
	    
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
        reg.put(CEYLON_PROJECT, image("prj_obj.gif"));
		reg.put(CEYLON_PACKAGE, image("package_obj.gif"));
		reg.put(CEYLON_IMPORT_LIST, image("impc_obj.gif"));
		reg.put(CEYLON_IMPORT, image("imp_obj.gif"));
		reg.put(CEYLON_FILE, image("ceylon_unit_16px.png"));
		reg.put(CEYLON_FILE_WARNING, image("ceylon_unit_16px.png"));
		reg.put(CEYLON_FILE_ERROR, image("ceylon_unit_16px.png"));
		reg.put(CEYLON_CLASS, image("ceylon_class_16px.png"));
        reg.put(CEYLON_ABSTRACT_CLASS, image("ceylon_abstractclass_16px.png"));
		reg.put(CEYLON_INTERFACE, image("ceylon_interface_16px.png"));
		reg.put(CEYLON_LOCAL_CLASS, image("ceylon_class_16px.png"));
		reg.put(CEYLON_LOCAL_INTERFACE, image("ceylon_interface_16px.png"));
		reg.put(CEYLON_METHOD, image("ceylon_function_16px.png"));
		reg.put(CEYLON_ATTRIBUTE, image("ceylon_value_16px.png"));
		reg.put(CEYLON_LOCAL_METHOD, image("ceylon_function_16px.png"));
		reg.put(CEYLON_LOCAL_ATTRIBUTE, image("ceylon_value_16px.png"));
        reg.put(CEYLON_PARAMETER, image("ceylon_parameter_16px.png"));
	    reg.put(CEYLON_DEFAULT_REFINEMENT, image("ceylon_decorator_actual.png"));
        reg.put(CEYLON_FORMAL_REFINEMENT, image("ceylon_decorator_actual.png"));
        reg.put(CEYLON_OPEN_DECLARATION, image("ceylon_declaration_16px.png"));
        reg.put(CEYLON_SEARCH_RESULTS, image("ceylon_search_16px.png"));
        reg.put(CEYLON_CORRECTION, image("correction_change.gif"));
        reg.put(CEYLON_NEW_FILE, image("ceylon_unit_75px.png"));
        reg.put(CEYLON_NEW_PROJECT, image("ceylon_project_75px.png"));
        reg.put(CEYLON_NEW_MODULE, image("ceylon_module_75px.png"));
        reg.put(CEYLON_NEW_PACKAGE, image("ceylon_package_75px.png"));
        reg.put(CEYLON_EXPORT_CAR, image("ceylon_export_75px.png"));
        reg.put(ELE32, image("ceylon_icon_32px.png"));
	}
	
	/**
	 * Kick off an initial build at startup time in order
	 * to build the model.
	 */
    private void runInitialBuild() {
        final IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        final Job buildJob = new Job("Building Ceylon Model") {
            @Override
            public IStatus run(IProgressMonitor monitor) {
                try {
                    List<IProject> interestingProjects = new ArrayList<IProject>();
                    for (IProject project : workspaceRoot.getProjects()) {
                        if (project.isOpen() && project.hasNature(CeylonNature.NATURE_ID)) {
                            interestingProjects.add(project);
                        }
                    }
                    
                    monitor.beginTask("Building Ceylon Model", 4 * interestingProjects.size());
/*
                    monitor.subTask("Waiting for JDT to initialize");
                    while (JavaModelManager.getJavaModelManager().batchContainerInitializations != JavaModelManager.BATCH_INITIALIZATION_FINISHED) {
                        yieldRule(monitor);
                    }
*/
                    for (IProject project : interestingProjects) {
                        ISourceProject sourceProject = ModelFactory.open(project);
                        CeylonBuilder.buildCeylonModel(project, sourceProject, null, monitor);
                    }
                    
                    for (IProject project : interestingProjects) {
                        TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
                        if (typeChecker != null) {
                            List<PhasedUnits> phasedUnitsForDependencies = new ArrayList<PhasedUnits>();
                            
                            for (IProject requiredProject : CeylonBuilder.getRequiredProjects(project)) {
                                TypeChecker requiredProjectTypeChecker = CeylonBuilder.getProjectTypeChecker(requiredProject);
                                if (requiredProjectTypeChecker != null) {
                                    phasedUnitsForDependencies.add(requiredProjectTypeChecker.getPhasedUnits());
                                }
                            }
                            
                            for (PhasedUnit pu : typeChecker.getPhasedUnits().getPhasedUnits()) {
                                pu.collectUnitDependencies(typeChecker.getPhasedUnits(), phasedUnitsForDependencies);
                            }
                        }
                    }
                    monitor.done();
                } catch (CoreException e) {
                    return new Status(IStatus.ERROR, getID(), "Job '" + this.getName() + "' failed", e);
                } catch (ModelException e) {
                    return new Status(IStatus.ERROR, getID(), "Job '" + this.getName() + "' failed", e);
                }
                return Status.OK_STATUS;
            }
            
        };
        buildJob.setRule(workspaceRoot);
        buildJob.schedule();
    }
    
    private void registerProjectOpenCloseListener() {
        getWorkspace().addResourceChangeListener(projectOpenCloseListener, 
                IResourceChangeEvent.POST_CHANGE);
    }

    private void unregisterProjectOpenCloseListener() {
        getWorkspace().removeResourceChangeListener(projectOpenCloseListener);
    }

    IResourceChangeListener projectOpenCloseListener = new IResourceChangeListener() {
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
                        if (resource instanceof IProject && (delta.getFlags() & IResourceDelta.OPEN) != 0) {
                            final IProject project = (IProject) resource;
                            try {
                                List<IProject> projectsToBuild = new ArrayList<IProject>();
                                projectsToBuild.add(project); 
                                for (IProject referencingProject : project.getReferencingProjects()) {
                                    projectsToBuild.add(referencingProject);
                                }
                                if (! project.isOpen()) {
                                    CeylonBuilder.removeProjectTypeChecker(project);
                                }
                                for (final IProject projectToBuild : projectsToBuild) {
                                    if (projectToBuild.isOpen() && projectToBuild.hasNature(CeylonNature.NATURE_ID)) {
                                        Job buildJob = new Job("Building Ceylon Model for project " + projectToBuild.getName()) {
                                            @Override
                                            public IStatus run(IProgressMonitor monitor) {
                                                try {
                                                    monitor.beginTask("Building Ceylon Model", 3);
                                                    ISourceProject sourceProject = ModelFactory.open(projectToBuild);
                                                    CeylonBuilder.buildCeylonModel(projectToBuild, sourceProject, null, 
                                                            monitor);
                                                } catch (ModelException e) {
                                                    return new Status(IStatus.ERROR, getID(), "Job '" + this.getName() + "' failed", e);
                                                } catch (CoreException e) {
                                                    return new Status(IStatus.ERROR, getID(), "Job '" + this.getName() + "' failed", e);
                                                }
                                                return Status.OK_STATUS;
                                            }
                                            
                                        };
                                        buildJob.setRule(workspaceRoot);
                                        buildJob.schedule();
                                    }
                                }
                                for (IProject projectToBuild : projectsToBuild) {
                                    TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
                                    if (typeChecker != null) {
                                        List<PhasedUnits> phasedUnitsForDependencies = new ArrayList<PhasedUnits>();
                                        
                                        for (IProject requiredProject : CeylonBuilder.getRequiredProjects(projectToBuild)) {
                                            TypeChecker requiredProjectTypeChecker = CeylonBuilder.getProjectTypeChecker(requiredProject);
                                            if (requiredProjectTypeChecker != null) {
                                                phasedUnitsForDependencies.add(requiredProjectTypeChecker.getPhasedUnits());
                                            }
                                        }
                                        
                                        for (PhasedUnit pu : typeChecker.getPhasedUnits().getPhasedUnits()) {
                                            pu.collectUnitDependencies(typeChecker.getPhasedUnits(), phasedUnitsForDependencies);
                                        }
                                    }
                                }
                            } catch (CoreException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        return false;
                    }
                });
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
    
    /*private void copyDefaultRepoIfNecessary() {
        File home = new File( System.getProperty("user.home") );
        File ceylon = new File( home, ".ceylon" );
        File repo = new File( ceylon, "repo" );
        repo.mkdirs();
        if (repo.list().length == 0) {
            try {
                Bundle bundle = Platform.getBundle(CeylonPlugin.PLUGIN_ID);
                Path path = new Path("defaultRepository");
                URL eclipseUrl = FileLocator.find(bundle, path, null);
                URL fileURL = FileLocator.resolve(eclipseUrl);
                File internalRepoCopy;
                String urlPath = fileURL.getPath();
                URI fileURI = new URI("file", null, urlPath, null);
                internalRepoCopy = new File(fileURI);
                if (internalRepoCopy.exists()) {
                    copyDirectory(internalRepoCopy, repo);
                }
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }*/
    
    /*public void copyDirectory(File sourceLocation , File targetLocation)
    throws IOException {
        
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }
            
            String[] children = sourceLocation.list();
            for (int i=0; i<children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {
            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            
            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }*/
    
}
