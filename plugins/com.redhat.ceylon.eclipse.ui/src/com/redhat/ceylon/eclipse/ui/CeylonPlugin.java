package com.redhat.ceylon.eclipse.ui;

import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_CLASS;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_DEFAULT_REFINEMENT;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FILE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FILE_ERROR;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FILE_WARNING;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_FORMAL_REFINEMENT;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_IMPORT;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_IMPORT_LIST;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_INTERFACE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_ATTRIBUTE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_CLASS;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_INTERFACE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_LOCAL_METHOD;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_METHOD;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_OPEN_DECLARATION;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_PACKAGE;
import static com.redhat.ceylon.eclipse.ui.ICeylonResources.CEYLON_SEARCH_RESULTS;
import static org.eclipse.core.resources.IncrementalProjectBuilder.FULL_BUILD;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;

public class CeylonPlugin extends PluginBase {

	public static final String PLUGIN_ID = "com.redhat.ceylon.eclipse.ui";
	public static final String LANGUAGE_ID = "ceylon";

	/**
	 * The unique instance of this plugin class
	 */
	protected static CeylonPlugin pluginInstance;

	public static CeylonPlugin getInstance() {
		if (pluginInstance==null) new CeylonPlugin();
		return pluginInstance;
	}

	public CeylonPlugin() {
		pluginInstance = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
	    super.start(context);
        runInitialBuild();
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
        if (url != null) {
        	return ImageDescriptor.createFromURL(url);
        }
        else {
        	return null;
        }
	}
	
    private void runInitialBuild() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
           public void run() {
             IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
             if (window != null) {
                 try {
                    PlatformUI.getWorkbench().getProgressService()
                     .busyCursorWhile(new IRunnableWithProgress() {
                        @Override
                        public void run(IProgressMonitor monitor) 
                                throws InvocationTargetException, InterruptedException {
                           try {
                               ResourcesPlugin.getWorkspace().build(FULL_BUILD, monitor);
                           }
                           catch (CoreException ce) {
                               throw new InvocationTargetException(ce);
                           }
                        }
                     });
                }
                catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
             }
           }
        });
    }
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
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
	    reg.put(CEYLON_DEFAULT_REFINEMENT, image("over_co.gif"));
        reg.put(CEYLON_FORMAL_REFINEMENT, image("implm_co.gif"));
        reg.put(CEYLON_OPEN_DECLARATION, image("opentype.gif"));
        reg.put(CEYLON_SEARCH_RESULTS, image("search_ref_obj.gif"));
	}

}
