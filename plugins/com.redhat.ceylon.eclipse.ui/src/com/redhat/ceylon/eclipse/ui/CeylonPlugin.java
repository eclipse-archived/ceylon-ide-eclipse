package com.redhat.ceylon.eclipse.ui;

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.awt.Container;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.eclipse.core.cpcontainer.CeylonClasspathContainer;
import com.redhat.ceylon.eclipse.core.cpcontainer.CeylonClasspathUtil;
import com.redhat.ceylon.eclipse.core.cpcontainer.fragmentinfo.IPackageFragmentExtraInfo;
import com.redhat.ceylon.eclipse.core.cpcontainer.fragmentinfo.PreferenceStoreInfo;
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
import org.eclipse.core.runtime.IAdaptable;
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
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
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

    private BundleContext bundleContext;


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
        this.bundleContext = context;
//        copyDefaultRepoIfNecessary();
        addResourceFilterPreference();
        registerProjectOpenCloseListener();
//        runInitialBuild();
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
		reg.put(CEYLON_FILE, image("template_obj.gif"));
		reg.put(CEYLON_FILE_WARNING, image("file_obj.gif"));
		reg.put(CEYLON_FILE_ERROR, image("file_obj.gif"));
		reg.put(CEYLON_CLASS, image("class_obj.gif"));
		reg.put(CEYLON_INTERFACE, image("int_obj.gif"));
		reg.put(CEYLON_LOCAL_CLASS, image("innerclass_private_obj.gif"));
		reg.put(CEYLON_LOCAL_INTERFACE, image("innerinterface_private_obj.gif"));
		reg.put(CEYLON_METHOD, image("public_co.gif"));
		reg.put(CEYLON_ATTRIBUTE, image("public_co.gif"));
		reg.put(CEYLON_LOCAL_METHOD, image("private_co.gif"));
		reg.put(CEYLON_LOCAL_ATTRIBUTE, image("private_co.gif"));
        reg.put(CEYLON_PARAMETER, image("methpro_obj.gif"));
	    reg.put(CEYLON_DEFAULT_REFINEMENT, image("over_co.gif"));
        reg.put(CEYLON_FORMAL_REFINEMENT, image("implm_co.gif"));
        reg.put(CEYLON_OPEN_DECLARATION, image("opentype.gif"));
        reg.put(CEYLON_SEARCH_RESULTS, image("search_ref_obj.gif"));
        reg.put(CEYLON_CORRECTION, image("correction_change.gif"));
        reg.put(CEYLON_NEW_FILE, image("new_wiz.png"));
        reg.put(CEYLON_NEW_MODULE, image("newftrprj_wiz.png"));
        reg.put(CEYLON_NEW_PACKAGE, image("newpack_wiz.png"));
        reg.put(CEYLON_EXPORT_CAR, image("jar_pack_wiz.png"));
        reg.put(ELE32, image("ceylon_icon_32px.png"));
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
                                if (! project.isOpen()) {
                                    CeylonBuilder.removeProject(project);
                                }
                                else {
                                    if (project.hasNature(CeylonNature.NATURE_ID)) {
                                        IJavaProject javaProject = JavaCore.create(project);
                                        if (javaProject != null) {
                                            List<CeylonClasspathContainer> cpContainers = CeylonClasspathUtil.getCeylonClasspathContainers(javaProject);
                                            for (CeylonClasspathContainer container : cpContainers) {
                                                container.launchResolve(false, null);
                                            }
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
    
    private IPackageFragmentExtraInfo packageExtraInfo;
    
    public BundleContext getBundleContext() {
        return this.bundleContext;
    }

    /**
     * Utility class that tries to adapt a non null object to the specified type
     * 
     * @param object
     *            the object to adapt
     * @param type
     *            the class to adapt to
     * @return the adapted object
     */

    public static Object adapt(Object object, Class type) {
        if (type.isInstance(object)) {
            return object;
        } else if (object instanceof IAdaptable) {
            return ((IAdaptable) object).getAdapter(type);
        }
        return Platform.getAdapterManager().getAdapter(object, type);
    }

    public static void log(Exception e) {
        getInstance().logException("Ceylon IDE internal error", e);
    }

    public static void log(IStatus status) {
        getInstance().getLog().log(status);
    }

    public static void log(CoreException e) {
        log(e.getStatus().getSeverity(), "Ceylon IDE internal error", e);
    }

    /**
     * Log the given exception along with the provided message and severity indicator
     */
    public static void log(int severity, String message, Throwable e) {
        log(new Status(severity, PLUGIN_ID, 0, message, e));
    }

    public IPackageFragmentExtraInfo getPackageFragmentExtraInfo() {
        if (packageExtraInfo == null) {
            packageExtraInfo = new PreferenceStoreInfo(getPreferenceStore());
        }
        return packageExtraInfo;
    }
}

