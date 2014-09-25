package com.redhat.ceylon.eclipse.code.style;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/*
 * The page to configure high-level Ceylon project and module conventions 
 * and acts as a parent page for formatter
 */
public class StyleMainPage extends StylePage {

    public static final String ID = "com.redhat.ceylon.eclipse.ui.preferences.style";

    public StyleMainPage() {
        setTitle("Ceylon Code Style");
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
    }

    private Control createPreferenceContent(Composite composite) {
        if (styleBlock == null) {
            styleBlock = new StyleModuleConventionBlock(project);
        }
        return styleBlock.createContents(composite);
    }

    protected boolean hasProjectSpecificOptions(IProject project) {
        return styleBlock.hasProjectSpecificOptions(project);
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        if (styleBlock != null) {
            styleBlock.performDefaults();
        }
    }

    @Override
    public boolean performOk() {
        if (styleBlock != null && !styleBlock.performOk()) {
            return false;
        }
        return super.performOk();
    }

    @Override
    public void performApply() {
        if (styleBlock != null) {
            styleBlock.performApply();
        }
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        composite.setLayout(layout);
        composite.setFont(parent.getFont());

        GridData data = new GridData(GridData.FILL, GridData.FILL, true, true);
        styleBlockControl = createPreferenceContent(composite);
        styleBlockControl.setLayoutData(data);

        if (isProjectPreferencePage()) {
            boolean useProjectSettings = hasProjectSpecificOptions(project);
            enableProjectSpecificSettings(useProjectSettings);
        }

        Dialog.applyDialogFont(composite);
        return composite;
    }
}
