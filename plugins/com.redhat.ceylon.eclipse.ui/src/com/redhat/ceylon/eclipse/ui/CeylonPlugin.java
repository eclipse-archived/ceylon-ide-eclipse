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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.imp.core.ErrorHandler;
import org.eclipse.imp.model.ICompilationUnit;
import org.eclipse.imp.model.IPathEntry;
import org.eclipse.imp.model.IPathEntry.PathEntryType;
import org.eclipse.imp.model.ISourceProject;
import org.eclipse.imp.model.ModelFactory;
import org.eclipse.imp.model.ModelFactory.IFactoryExtender;
import org.eclipse.imp.runtime.PluginBase;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.osgi.framework.BundleContext;

import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;

public class CeylonPlugin extends PluginBase {

	private static final class CeylonFactoryExtender implements
	        IFactoryExtender {
	    
	    public void extend(ISourceProject project) {
            initializeBuildPathFromJavaProject(project);
        }

	    public void extend(ICompilationUnit unit) { }

	   /**
         * Read the IJavaProject classpath configuration and populate the ISourceProject's
         * build path accordingly.
         */
        public void initializeBuildPathFromJavaProject(ISourceProject project) {
            IJavaProject javaProject= JavaCore.create(project.getRawProject());
            if (javaProject.exists()) {
                try {
                    IClasspathEntry[] cpEntries= javaProject.getResolvedClasspath(true);
                    List<IPathEntry> buildPath= new ArrayList<IPathEntry>(cpEntries.length);
                    for(int i= 0; i < cpEntries.length; i++) {
                        IClasspathEntry entry= cpEntries[i];
                        IPath path= entry.getPath();

                        IPathEntry.PathEntryType type = getType(entry);
                        if (type.equals(PathEntryType.SOURCE_FOLDER))
                        {
                            boolean ceylonSourceFolder = false;
                            IPath[] inclusionPatterns = entry.getInclusionPatterns(); 
                            for (int p=0; p<inclusionPatterns.length; p++)
                            {
                                if (inclusionPatterns[p].lastSegment().endsWith(".ceylon"))
                                {
                                    ceylonSourceFolder = true;
                                    break;
                                }                            
                            }
                            if (ceylonSourceFolder)
                            {
                                IPathEntry pathEntry= ModelFactory.createPathEntry(type, path);
                                buildPath.add(pathEntry);
                            }
                        }
                        else
                        {
                            IPathEntry pathEntry= ModelFactory.createPathEntry(type, path);
                            buildPath.add(pathEntry);
                        }
                    }
                    project.setBuildPath(buildPath);
                } catch (JavaModelException e) {
                    ErrorHandler.reportError(e.getMessage(), e);
                }
            }
        }

        private IPathEntry.PathEntryType getType(IClasspathEntry entry) {
            switch (entry.getEntryKind()) {
            case IClasspathEntry.CPE_CONTAINER:
                return PathEntryType.CONTAINER;
            case IClasspathEntry.CPE_LIBRARY:
                return PathEntryType.ARCHIVE;
            case IClasspathEntry.CPE_PROJECT:
                return PathEntryType.PROJECT;
            case IClasspathEntry.CPE_SOURCE:
                return PathEntryType.SOURCE_FOLDER;
            default:
            //case IClasspathEntry.CPE_VARIABLE:
                throw new IllegalArgumentException("Encountered variable class-path entry: " + 
                        entry.getPath().toPortableString());
            }
        }
    }

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
          ModelFactory.getInstance()
              .installExtender(new CeylonFactoryExtender(), 
                      CeylonBuilder.LANGUAGE);

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
