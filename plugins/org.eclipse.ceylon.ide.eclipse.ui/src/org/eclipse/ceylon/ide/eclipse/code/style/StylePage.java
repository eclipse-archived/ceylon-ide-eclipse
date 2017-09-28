package org.eclipse.ceylon.ide.eclipse.code.style;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public abstract class StylePage extends PropertyPage implements
        IWorkbenchPropertyPage {

    protected IProject project;
    protected StyleBlock styleBlock;
    protected Control styleBlockControl;

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
    }

    @Override
    public IAdaptable getElement() {
        return project;
    }

    @Override
    public void setElement(IAdaptable element) {
        project = (IProject) element.getAdapter(IResource.class);
    }

    protected boolean isProjectPreferencePage() {
        return project != null;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (styleBlock != null) {
            styleBlock.dispose();
        }
        if (styleBlockControl != null) {
            styleBlockControl.dispose();
        }
    }
}
