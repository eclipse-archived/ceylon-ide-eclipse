package org.eclipse.ceylon.ide.eclipse.ui.test;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class CeylonUITestActivator extends Plugin {

	// The shared instance
	private static CeylonUITestActivator plugin;
	
	
	/**
	 * The constructor
	 */
	public CeylonUITestActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static CeylonUITestActivator getDefault() {
		return plugin;
	}

}
