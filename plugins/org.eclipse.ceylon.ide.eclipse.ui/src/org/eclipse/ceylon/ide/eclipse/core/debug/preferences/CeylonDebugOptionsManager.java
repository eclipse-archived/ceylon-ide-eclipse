/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.preferences;

import static org.eclipse.ceylon.ide.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.ACTIVE_FILTERS_LIST;
import static org.eclipse.ceylon.ide.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.INACTIVE_FILTERS_LIST;
import static org.eclipse.ceylon.ide.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.USE_STEP_FILTERS;
import static org.eclipse.ceylon.ide.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.FILTER_DEFAULT_ARGUMENTS_CODE;
import static org.eclipse.ceylon.ide.eclipse.core.debug.preferences.CeylonDebugPreferenceInitializer.DEBUG_AS_JAVACODE;
import static org.eclipse.jdt.internal.debug.ui.JavaDebugOptionsManager.parseList;

import java.util.Arrays;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchPage;

import org.eclipse.ceylon.ide.eclipse.core.debug.model.CeylonJDIDebugTarget;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

/**
 * Manages options for the Ceylon Debugger
 */
public class CeylonDebugOptionsManager 
        implements IDebugEventSetListener, 
                   IPropertyChangeListener, 
                   ILaunchListener {
    
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
        CeylonPlugin.getPreferences().removePropertyChangeListener(this);
    }   

    /**
     * Notifies the give debug target of filter specifications
     * 
     * @param target Ceylon debug target
     */
    protected void notifyTargetOfPropertyChanges(CeylonJDIDebugTarget target) {
        IPreferenceStore store = CeylonPlugin.getPreferences();
        String[] filters = parseList(store.getString(ACTIVE_FILTERS_LIST));
        target.setCeylonStepFilters(filters);
        target.setCeylonStepFiltersEnabled(store.getBoolean(USE_STEP_FILTERS));
        target.setFiltersDefaultArgumentsCode(store.getBoolean(FILTER_DEFAULT_ARGUMENTS_CODE));
        target.setDebugAsJavaCode(store.getBoolean(DEBUG_AS_JAVACODE));
        
        if (!registeredAsPropertyChangeListener) {
            registeredAsPropertyChangeListener = true;
            store.addPropertyChangeListener(this);
        }
    }   
    
    /**
     * Notifies all targets of current filter specifications.
     */
    protected void notifyTargetsOfPropertyChanges() {
        IDebugTarget[] targets = 
                DebugPlugin.getDefault().getLaunchManager().getDebugTargets();
        for (int i = 0; i < targets.length; i++) {
            if (targets[i] instanceof CeylonJDIDebugTarget) {
                CeylonJDIDebugTarget target = 
                        (CeylonJDIDebugTarget) targets[i];
                notifyTargetOfPropertyChanges(target);
            }
        }   
    }       

    /**
     * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();

        if (property.equals(DEBUG_AS_JAVACODE)) {
            IWorkbenchPage page = DebugUIPlugin.getActiveWorkbenchWindow().getActivePage();
            for (String viewId : Arrays.asList(
                    IDebugUIConstants.ID_VARIABLE_VIEW,
                    IDebugUIConstants.ID_EXPRESSION_VIEW,
                    IDebugUIConstants.ID_DEBUG_VIEW)) {
                IDebugView view = (IDebugView) page.findView(viewId);
                if (view != null) {
                    view.getViewer().refresh();
                }
            }
        }

        if (isPropertyForDebugTargets(property)) {
            notifyTargetsOfPropertyChanges();
        }
    }
    
    /**
     * Returns whether the given property is a property that affects whether
     * or not step filters are used.
     */
    private boolean isPropertyForDebugTargets(String property) {
        return property.equals(ACTIVE_FILTERS_LIST) ||
                property.equals(INACTIVE_FILTERS_LIST) ||
                property.equals(USE_STEP_FILTERS) ||
                property.equals(FILTER_DEFAULT_ARGUMENTS_CODE) ||
                property.equals(DEBUG_AS_JAVACODE);
    }

    private boolean registeredAsPropertyChangeListener = false;
    
    /**
     * When a Ceylon debug target is created, install options in
     * the target.
     */
    public void handleDebugEvents(DebugEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            DebugEvent event = events[i];
            if (event.getKind() == DebugEvent.CREATE) {
                Object source = event.getSource();
                if (source instanceof CeylonJDIDebugTarget) {
                    CeylonJDIDebugTarget ceylonTarget = 
                            (CeylonJDIDebugTarget)source;
                    // step filters
                    notifyTargetOfPropertyChanges(ceylonTarget);
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
        notifyTargetsOfPropertyChanges();
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
    
    public void launchChanged(ILaunch launch) {
        activate();
        DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
    }

    public void launchRemoved(ILaunch launch) {}
}
