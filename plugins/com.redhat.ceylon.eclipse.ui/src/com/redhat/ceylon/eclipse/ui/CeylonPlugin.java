package com.redhat.ceylon.eclipse.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
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
                        IPathEntry.PathEntryType type;
                        IPath path= entry.getPath();

                        switch (entry.getEntryKind()) {
                        case IClasspathEntry.CPE_CONTAINER:
                            type= PathEntryType.CONTAINER;
                            break;
                        case IClasspathEntry.CPE_LIBRARY:
                            type= PathEntryType.ARCHIVE;
                            break;
                        case IClasspathEntry.CPE_PROJECT:
                            type= PathEntryType.PROJECT;
                            break;
                        case IClasspathEntry.CPE_SOURCE:
                            type= PathEntryType.SOURCE_FOLDER;
                            break;
                        default:
                     // case IClasspathEntry.CPE_VARIABLE:
                            throw new IllegalArgumentException("Encountered variable class-path entry: " + 
                                    entry.getPath().toPortableString());
                        }
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
    }

	public static final String kPluginID = "com.redhat.ceylon.eclipse.ui";
	public static final String kLanguageID = "ceylon";

	/**
	 * The unique instance of this plugin class
	 */
	protected static CeylonPlugin sPlugin;

	public static CeylonPlugin getInstance() {
		if (sPlugin == null)
			new CeylonPlugin();
		return sPlugin;
	}

	public CeylonPlugin() {
		sPlugin = this;
	}

	@Override
	public void start(BundleContext context) throws Exception {
	  super.start(context);	  
          ModelFactory.getInstance().installExtender(new CeylonFactoryExtender(), CeylonBuilder.LANGUAGE);

	}

	@Override
	public String getID() {
		return kPluginID;
	}

	@Override
	public String getLanguageID() {
		return kLanguageID;
	}

	public static final org.eclipse.core.runtime.IPath ICONS_PATH = 
			new org.eclipse.core.runtime.Path("icons/"); //$NON-NLS-1$("icons/"); //$NON-NLS-1$

	@Override
	protected void initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry reg) {
		org.osgi.framework.Bundle bundle = getBundle();
		
		reg.put(ICeylonResources.CEYLON_PACKAGE, createImageDescriptor(bundle, 
				ICONS_PATH.append("package_obj.gif")));

		reg.put(ICeylonResources.CEYLON_IMPORT_LIST, createImageDescriptor(bundle, 
				ICONS_PATH.append("impc_obj.gif")));

		reg.put(ICeylonResources.CEYLON_IMPORT, createImageDescriptor(bundle, 
				ICONS_PATH.append("imp_obj.gif")));

		reg.put(ICeylonResources.CEYLON_FILE, createImageDescriptor(bundle, 
				ICONS_PATH.append("template_obj.gif")));

		reg.put(ICeylonResources.CEYLON_FILE_WARNING, createImageDescriptor(bundle, 
				ICONS_PATH.append("file_obj.gif")));

		reg.put(ICeylonResources.CEYLON_FILE_ERROR, createImageDescriptor(bundle, 
				ICONS_PATH.append("file_obj.gif")));

		reg.put(ICeylonResources.CEYLON_CLASS, createImageDescriptor(bundle, 
				ICONS_PATH.append("class_obj.gif")));

		reg.put(ICeylonResources.CEYLON_INTERFACE, createImageDescriptor(bundle, 
				ICONS_PATH.append("int_obj.gif")));

		reg.put(ICeylonResources.CEYLON_LOCAL_CLASS, createImageDescriptor(bundle, 
				ICONS_PATH.append("innerclass_private_obj.gif")));

		reg.put(ICeylonResources.CEYLON_LOCAL_INTERFACE, createImageDescriptor(bundle, 
				ICONS_PATH.append("innerinterface_private_obj.gif")));

		reg.put(ICeylonResources.CEYLON_METHOD, createImageDescriptor(bundle, 
				ICONS_PATH.append("public_co.gif")));

		reg.put(ICeylonResources.CEYLON_ATTRIBUTE, createImageDescriptor(bundle, 
				ICONS_PATH.append("public_co.gif")));

		reg.put(ICeylonResources.CEYLON_LOCAL_METHOD, createImageDescriptor(bundle, 
				ICONS_PATH.append("private_co.gif")));

		reg.put(ICeylonResources.CEYLON_LOCAL_ATTRIBUTE, createImageDescriptor(bundle, 
				ICONS_PATH.append("private_co.gif")));

	    reg.put(ICeylonResources.CEYLON_DEFAULT_REFINEMENT, createImageDescriptor(bundle, 
	                ICONS_PATH.append("over_co.gif")));

        reg.put(ICeylonResources.CEYLON_FORMAL_REFINEMENT, createImageDescriptor(bundle, 
                ICONS_PATH.append("implm_co.gif")));

        reg.put(ICeylonResources.CEYLON_OPEN_DECLARATION, createImageDescriptor(bundle, 
                ICONS_PATH.append("opentype.gif")));

	}

	public static org.eclipse.jface.resource.ImageDescriptor createImageDescriptor(
			org.osgi.framework.Bundle bundle,
			org.eclipse.core.runtime.IPath path) {
		java.net.URL url = org.eclipse.core.runtime.FileLocator.find(bundle, path, null);
		if (url != null) {
			return org.eclipse.jface.resource.ImageDescriptor.createFromURL(url);
		}
		else {
			return null;
		}
	}

}
