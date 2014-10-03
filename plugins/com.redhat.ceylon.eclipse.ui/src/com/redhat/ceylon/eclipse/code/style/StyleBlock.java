package com.redhat.ceylon.eclipse.code.style;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public abstract class StyleBlock {

    protected Shell shell;
    protected Composite block;
    protected IProject project;
    protected boolean projectSettings;

    protected void setShell(Shell shell) {
        this.shell = shell;
    }

    public void disableProjectSettings() {
        projectSettings = false;
    }

    public void enableProjectSettings() {
        projectSettings = true;
    }

    public boolean hasProjectSpecificOptions(IProject project) {
        // TODO Auto-generated method stub
        return false;
    }

    protected abstract boolean performApply();

    protected abstract void performDefaults();

    public void enableProjectSpecificSettings(boolean useProjectSettings) {
        // TODO Auto-generated method stub

    }

    protected abstract Control createContents(Composite composite);

    protected Composite createComposite(Composite parent, int numColumns) {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());
        final GridLayout layout = new GridLayout(numColumns, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        return composite;
    }

    public abstract void initialize();

    public boolean performOk() {
        return performApply();
    }

    public void dispose() {
        if (block != null) {
            block.dispose();
        }
    }
}
