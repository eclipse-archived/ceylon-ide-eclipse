package com.redhat.ceylon.eclipse.ui;

import org.eclipse.imp.runtime.PluginBase;
import org.osgi.framework.BundleContext;

public class CeylonPlugin extends PluginBase {

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
		
		reg.put(ICeylonResources.CEYLON_FILE, createImageDescriptor(bundle, 
				ICONS_PATH.append("ceylon_file.gif")));

		reg.put(ICeylonResources.CEYLON_FILE_WARNING, createImageDescriptor(bundle, 
				ICONS_PATH.append("ceylon_file_warning.gif")));

		reg.put(ICeylonResources.CEYLON_FILE_ERROR, createImageDescriptor(bundle, 
				ICONS_PATH.append("ceylon_file_error.gif")));

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

		reg.put(ICeylonResources.CEYLON_DEFAULT_IMAGE, createImageDescriptor(bundle, 
				ICONS_PATH.append("ceylon_default_image.gif")));

		reg.put(ICeylonResources.CEYLON_DEFAULT_OUTLINE_ITEM, createImageDescriptor(bundle, 
				ICONS_PATH.append("ceylon_default_outline_item.gif")));
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
