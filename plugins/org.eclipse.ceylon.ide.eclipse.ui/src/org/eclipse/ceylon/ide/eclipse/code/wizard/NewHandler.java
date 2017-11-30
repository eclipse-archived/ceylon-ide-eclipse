/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.wizard;

import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.Hashtable;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.actions.NewWizardShortcutAction;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class NewHandler extends AbstractHandler implements IExecutableExtension {
    
    private String wizardId;
    
    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        IWorkbench workbench = getWorkbench();
        IWizardDescriptor descriptor = workbench.getNewWizardRegistry().findWizard(wizardId);
        new NewWizardShortcutAction(workbench.getActiveWorkbenchWindow(), descriptor).run();
        return null;
    }
    @Override
    public void setInitializationData(IConfigurationElement config,
            String propertyName, Object data) throws CoreException {
        if (propertyName.equals("class")) {
            wizardId = (String) ((Hashtable) data).get("wizardId");
        }
    }
}
