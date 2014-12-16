package com.redhat.ceylon.eclipse.core.debug.preferences;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.redhat.ceylon.eclipse.core.debug.model.CeylonJDIDebugTarget;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * Manages options for the Ceylon Debugger
 */
public class CeylonDebugOptionsManager implements IDebugEventSetListener, IPropertyChangeListener, ILaunchListener {
    
    public static final String PREF_FILTER_LANGUAGE_MODULE = CeylonPlugin.PLUGIN_ID + ".ceylonDebug.FilterCeylonLanguageFrames"; //$NON-NLS-1$
    public static final String PREF_FILTER_MODULE_RUNTIME = CeylonPlugin.PLUGIN_ID + ".ceylonDebug.FilterJBossModulesFrames"; //$NON-NLS-1$
    
    /**
     * Singleton options manager
     */
    private static CeylonDebugOptionsManager optionsManager = null;
    
    /**
     * Whether the manager has been activated
     */
    private boolean fActivated = false;
    
    /**
     * Not to be instantiated
     * 
     * @see CeylonDebugOptionsManager#getDefault();
     */
    private CeylonDebugOptionsManager() {
    }
    
    /**
     * Return the default options manager
     */
    public static CeylonDebugOptionsManager getDefault() {
        if (optionsManager == null) {
            optionsManager = new CeylonDebugOptionsManager();
        }
        return optionsManager;
    }
    
    /**
     * Called at startup by the Java debug ui plug-in
     */
    public void startup() {
        // lazy initialization will occur on the first launch
        DebugPlugin debugPlugin = DebugPlugin.getDefault();
        debugPlugin.getLaunchManager().addLaunchListener(this);
    }
    
    
    /**
     * Called at shutdown by the Ceylon plug-in
     */
    public void shutdown() {
        DebugPlugin debugPlugin = DebugPlugin.getDefault();
        debugPlugin.getLaunchManager().removeLaunchListener(this);
        debugPlugin.removeDebugEventListener(this);
        CeylonPlugin.getInstance().getPreferenceStore().removePropertyChangeListener(this);
    }   

    /**
     * Notifies the give debug target of filter specifications
     * 
     * @param target Ceylon debug target
     */
    protected void notifyTargetOfFilters(CeylonJDIDebugTarget target) {

        IPreferenceStore store = CeylonPlugin.getInstance().getPreferenceStore();

        target.setCeylonFilters(
                store.getBoolean(PREF_FILTER_LANGUAGE_MODULE),
                store.getBoolean(PREF_FILTER_MODULE_RUNTIME));
        if (! registeredAsPropertyChangeListener) {
            registeredAsPropertyChangeListener = true;
            CeylonPlugin.getInstance().getPreferenceStore().addPropertyChangeListener(this);
        }
    }   
    
    /**
     * Notifies all targets of current filter specifications.
     */
    protected void notifyTargetsOfFilters() {
        IDebugTarget[] targets = DebugPlugin.getDefault().getLaunchManager().getDebugTargets();
        for (int i = 0; i < targets.length; i++) {
            if (targets[i] instanceof CeylonJDIDebugTarget) {
                CeylonJDIDebugTarget target = (CeylonJDIDebugTarget)targets[i];
                notifyTargetOfFilters(target);
            }
        }   
    }       

    /**
     * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (isUseFilterProperty(property)) {
            notifyTargetsOfFilters();
        }
    }
    
    /**
     * Returns whether the given property is a property that affects whether
     * or not step filters are used.
     */
    private boolean isUseFilterProperty(String property) {
        return property.equals(PREF_FILTER_LANGUAGE_MODULE) ||
                property.equals(PREF_FILTER_MODULE_RUNTIME);
    }

    private boolean registeredAsPropertyChangeListener = false;
    
    /**
     * When a Ceylon debug target is created, install options in
     * the target.
     * 
     * @see IDebugEventSetListener#handleDebugEvents(DebugEvent[])
     */
    public void handleDebugEvents(DebugEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            DebugEvent event = events[i];
            if (event.getKind() == DebugEvent.CREATE) {
                Object source = event.getSource();
                if (source instanceof CeylonJDIDebugTarget) {
                    CeylonJDIDebugTarget ceylonTarget = (CeylonJDIDebugTarget)source;
                    
                    // step filters
                    notifyTargetOfFilters(ceylonTarget);
                }
            }
        }
    }

    /**
     * Activates this debug options manager. When active, this
     * manager becomes a listener to many notifications and updates
     * running debug targets based on these notifications.
     * 
     * A debug options manager does not need to be activated until
     * there is a running debug target.
     */
    private void activate() {
        if (fActivated) {
            return;
        }
        fActivated = true;
        notifyTargetsOfFilters();
        DebugPlugin.getDefault().addDebugEventListener(this);
    }   

    /**
     * Startup problem handling on the first launch.
     * 
     * @see ILaunchListener#launchAdded(ILaunch)
     */
    public void launchAdded(ILaunch launch) {
        launchChanged(launch);
    }
    /**
     * @see ILaunchListener#launchChanged(ILaunch)
     */
    public void launchChanged(ILaunch launch) {
        activate();
        DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
    }

    /**
     * @see ILaunchListener#launchRemoved(ILaunch)
     */
    public void launchRemoved(ILaunch launch) {
    }
}
