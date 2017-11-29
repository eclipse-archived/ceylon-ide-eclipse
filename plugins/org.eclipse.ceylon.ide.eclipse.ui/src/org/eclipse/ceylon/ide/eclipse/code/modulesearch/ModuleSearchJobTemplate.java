/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.modulesearch;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public abstract class ModuleSearchJobTemplate extends Job {

    public ModuleSearchJobTemplate(String name) {
        super(name);
        setUser(false);
    }

    @Override
    protected final IStatus run(IProgressMonitor monitor) {
        try {
            onRun();
        } catch (Exception e) {
            return new Status(Status.ERROR, CeylonPlugin.PLUGIN_ID, getName() + " failed", e);
        } finally {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    onFinish();
                }
            });
        }
        return Status.OK_STATUS;
    }

    protected abstract void onRun();

    protected abstract void onFinish();

}