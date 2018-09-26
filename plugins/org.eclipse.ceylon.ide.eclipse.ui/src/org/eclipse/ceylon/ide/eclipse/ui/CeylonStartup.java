/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.ui;

import static org.eclipse.ceylon.ide.eclipse.core.debug.CeylonDebugElementAdapterFactory.installCeylonDebugElementAdapters;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getFile;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

import org.eclipse.ceylon.ide.eclipse.code.editor.RecentFilesPopup;

public class CeylonStartup implements IStartup {

    private static final class WindowListener implements IWindowListener {
        IContextActivation contextActivation1 = null;
        IContextActivation contextActivation2 = null;

        public void updateContext(IPerspectiveDescriptor perspective) {
            IContextService service = 
                    (IContextService) 
                        getWorkbench()
                            .getActiveWorkbenchWindow()
                            .getService(IContextService.class);
            // in case of previous crash, perspective may be null
            if (perspective != null && 
                    perspective.getId() != null &&
                    perspective.getLabel() != null && 
                    !perspective.getLabel().startsWith("<") &&
                    perspective.getId()
                        .equals(PLUGIN_ID + ".perspective")) {
                contextActivation1 = 
                        service.activateContext(PLUGIN_ID + 
                                ".perspectiveContext");
                contextActivation2 = 
                        service.activateContext(PLUGIN_ID + 
                                ".wizardContext");
            }
            else {
                if (contextActivation1!=null) {
                    service.deactivateContext(contextActivation1);
                }
                if (contextActivation2!=null) {
                    service.deactivateContext(contextActivation2);
                }
            }
        }

        @Override
        public void windowOpened(IWorkbenchWindow window) {
            updateContext(window.getActivePage().getPerspective());
            window.addPerspectiveListener(new IPerspectiveListener() {
                @Override
                public void perspectiveChanged(IWorkbenchPage page,
                        IPerspectiveDescriptor perspective, String changeId) {}
                @Override
                public void perspectiveActivated(IWorkbenchPage page,
                        IPerspectiveDescriptor perspective) {
                    updateContext(perspective);
                }
            });
        }

        @Override
        public void windowDeactivated(IWorkbenchWindow window) {}
        @Override
        public void windowClosed(IWorkbenchWindow window) {}
        @Override
        public void windowActivated(IWorkbenchWindow window) {}
    }

    @Override
    public void earlyStartup() {
        final String version = System.getProperty("java.version");
        if (!version.startsWith("1.7") && 
            !version.startsWith("1.8") && 
            !version.startsWith("9")) {
            final Display display = getWorkbench().getDisplay();
            display.asyncExec(new Runnable() {
                @Override
                public void run() {
                    ErrorDialog.openError(
                            display.getActiveShell(),
                            "Ceylon IDE does not support this JVM",  
                            "Ceylon IDE supports Java versions from 7 to 9.", 
                            new Status(IStatus.ERROR, PLUGIN_ID, 
                                    "Eclipse is running on a Java " 
                                            + version + " VM.", 
                                    null));
                }});
        }
        
        DebugPlugin.getDefault()
                .getLaunchManager()
                .addLaunchListener(new ILaunchListener() {
            Boolean activated = false;
            
            @Override
            public void launchRemoved(ILaunch launch) {
            }
            
            @Override
            public void launchChanged(ILaunch launch) {
                synchronized (activated) {
                    if (activated) {
                        return;
                    }
                    activated = true;
                    installCeylonDebugElementAdapters();
                    DebugPlugin.getDefault()
                        .getLaunchManager()
                        .removeLaunchListener(this);
                }
            }
            
            @Override
            public void launchAdded(ILaunch launch) {
                launchChanged(launch);
            }
        });
        
        getWorkbench()
                .getDisplay()
                .asyncExec(new Runnable() {
            @Override
            public void run() {
                WindowListener listener = new WindowListener();
                getWorkbench().addWindowListener(listener);
                for (IWorkbenchWindow window: 
                        getWorkbench().getWorkbenchWindows()) {
                    listener.windowOpened(window);
                }
            }
        });

        getWorkbench()
                .getDisplay()
                .asyncExec(new Runnable() {
            @Override
            public void run() {
                ICommandService commandService = 
                        (ICommandService) 
                            getWorkbench()
                                .getService(ICommandService.class);
                commandService.addExecutionListener(new IExecutionListener() {
                    public void postExecuteSuccess(final String commandId, 
                            final Object returnValue) {
                        if (commandId.equals("org.eclipse.ui.file.save")) {
                            IEditorPart ed = getCurrentEditor();
                            if (ed!=null) {
                                RecentFilesPopup.addToHistory(
                                        getFile(ed.getEditorInput()));
                            }
                        }
                    }
                    public void postExecuteFailure(final String commandId, 
                            final ExecutionException exception) {}
                    public void preExecute(final String commandId, 
                            final ExecutionEvent event) {}
                    public void notHandled(final String commandId, 
                            final NotHandledException exception) {}
                });
            }
        });
        
        org.eclipse.ceylon.ide.eclipse.core.launch.setDefaultLaunchDelegateToNonCeylonAware_.setDefaultLaunchDelegateToNonCeylonAware();
    }

}
