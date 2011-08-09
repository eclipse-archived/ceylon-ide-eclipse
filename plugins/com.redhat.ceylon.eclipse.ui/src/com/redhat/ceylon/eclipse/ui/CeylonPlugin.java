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
		super();
		sPlugin = this;
	}

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

	// Definitions for image management

	public static final org.eclipse.core.runtime.IPath ICONS_PATH = new org.eclipse.core.runtime.Path(
			"icons/"); //$NON-NLS-1$("icons/"); //$NON-NLS-1$

	protected void initializeImageRegistry(
			org.eclipse.jface.resource.ImageRegistry reg) {
		org.osgi.framework.Bundle bundle = getBundle();
		org.eclipse.core.runtime.IPath path = ICONS_PATH
				.append("ceylon_default_image.gif");//$NON-NLS-1$
		org.eclipse.jface.resource.ImageDescriptor imageDescriptor = createImageDescriptor(
				bundle, path);
		reg.put(ICeylonResources.CEYLON_DEFAULT_IMAGE, imageDescriptor);

		path = ICONS_PATH.append("ceylon_default_outline_item.gif");//$NON-NLS-1$
		imageDescriptor = createImageDescriptor(bundle, path);
		reg.put(ICeylonResources.CEYLON_DEFAULT_OUTLINE_ITEM, imageDescriptor);

		path = ICONS_PATH.append("ceylon_file.gif");//$NON-NLS-1$
		imageDescriptor = createImageDescriptor(bundle, path);
		reg.put(ICeylonResources.CEYLON_FILE, imageDescriptor);

		path = ICONS_PATH.append("ceylon_file_warning.gif");//$NON-NLS-1$
		imageDescriptor = createImageDescriptor(bundle, path);
		reg.put(ICeylonResources.CEYLON_FILE_WARNING, imageDescriptor);

		path = ICONS_PATH.append("ceylon_file_error.gif");//$NON-NLS-1$
		imageDescriptor = createImageDescriptor(bundle, path);
		reg.put(ICeylonResources.CEYLON_FILE_ERROR, imageDescriptor);
	}

	public static org.eclipse.jface.resource.ImageDescriptor createImageDescriptor(
			org.osgi.framework.Bundle bundle,
			org.eclipse.core.runtime.IPath path) {
		java.net.URL url = org.eclipse.core.runtime.FileLocator.find(bundle,
				path, null);
		if (url != null) {
			return org.eclipse.jface.resource.ImageDescriptor
					.createFromURL(url);
		}
		return null;
	}

	// Definitions for image management end

}
