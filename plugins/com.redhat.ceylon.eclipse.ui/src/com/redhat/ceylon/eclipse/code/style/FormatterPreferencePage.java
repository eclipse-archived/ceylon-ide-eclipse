package com.redhat.ceylon.eclipse.code.style;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/*
 * The page to configure the Ceylon code formatter profiles.
 */
public class FormatterPreferencePage extends StylePage {

    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.style.formatter";

    public FormatterPreferencePage() {
        setTitle("Code Formatter");
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

        Dialog.applyDialogFont(composite);
        return composite;
    }

    @Override
    public void performApply() {
        if (styleBlock != null) {
            styleBlock.performApply();
        }
    }

    @Override
    public boolean performOk() {
        performApply();
        return true;
    }

    @Override
    public void performDefaults() {
        if (styleBlock != null) {
            styleBlock.performDefaults();
        }
    }
    
    private Control createPreferenceContent(Composite composite) {
        if (styleBlock == null) {
            styleBlock = new FormatterConfigurationBlock(project);
        }
        return styleBlock.createContents(composite);
    }
}
