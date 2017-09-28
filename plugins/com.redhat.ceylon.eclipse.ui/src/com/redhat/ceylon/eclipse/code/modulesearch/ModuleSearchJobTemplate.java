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