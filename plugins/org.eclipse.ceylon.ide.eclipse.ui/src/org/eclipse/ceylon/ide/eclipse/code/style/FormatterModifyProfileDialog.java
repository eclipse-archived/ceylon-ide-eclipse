/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.style;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import ceylon.formatter.options.FormattingOptions;

import org.eclipse.ceylon.ide.eclipse.code.style.FormatterProfileManager.Profile;
import org.eclipse.ceylon.ide.eclipse.code.style.FormatterTabPage.ModificationListener;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class FormatterModifyProfileDialog extends StatusDialog implements
        ModificationListener {

    private static final IStatus STATUS_OK = new Status(IStatus.OK, CeylonPlugin.PLUGIN_ID, null);
    
    private final static String LAST_TAB_INDEX = CeylonPlugin.PLUGIN_ID
            + ".style.formatter_page.modify_dialog.last_focus_index";

    private static final int APPLY_BUTTON_ID = IDialogConstants.CLIENT_ID;

    private final boolean newProfile;
    private final boolean projectSpecific;

    private Profile profile;
    private FormattingOptions workingValues;
    private final List<FormatterTabPage> tabPages;

    private TabFolder tabFolder;
    private final FormatterProfileManager profileManager;
    private Button applyButton;
    private Button saveButton;
    private StringDialogField profileNameField;
    
    private int tabIndex;

    public FormatterModifyProfileDialog(Shell parentShell, Profile profile,
            FormatterProfileManager profileManager, boolean newProfile,
            boolean projectSpecific) {
        super(parentShell);

        this.profileManager = profileManager;
        this.newProfile = newProfile;
        this.projectSpecific = projectSpecific;

        this.profile = profile;
        setTitle("Modify Formatter Profile \u2014 " + profile.getName());
        this.workingValues = profile.getSettings();
        setStatusLineAboveButtons(false);
        this.tabPages = new ArrayList<FormatterTabPage>();
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    protected boolean isProjectSpecific() {
        return this.projectSpecific;
    }
    
    protected void addPages(FormatterPreferences workingValues) {
        addTabPage("Indentation", new FormatterTabIndent(this, workingValues));
        addTabPage("Spacing", new FormatterTabSpace(this, workingValues));
        addTabPage("Line and Line Breaks", new FormatterTabLine(this,
                workingValues));
        addTabPage("Miscellaneous", new FormatterTabMisc(this, workingValues));
    }

    @Override
    public void create() {
        super.create();
        int lastFocusNr = 0;
        try {
            lastFocusNr = CeylonPlugin.getInstance().getDialogSettings()
                    .getInt(LAST_TAB_INDEX);
        } catch (NumberFormatException e) {
            // may not exist
        }

        if (!newProfile) {
            tabFolder.setSelection(lastFocusNr);
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        final Composite composite = (Composite) super.createDialogArea(parent);

        Composite nameComposite = new Composite(composite, SWT.NONE);
        nameComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
                false));
        nameComposite.setLayout(new GridLayout(3, false));

        profileNameField = new StringDialogField();
        profileNameField.setLabelText("Profile Name");
        profileNameField.setText(profile.getName());
        profileNameField.getLabelControl(nameComposite).setLayoutData(
                new GridData(SWT.LEFT, SWT.CENTER, false, false));
        profileNameField.getTextControl(nameComposite).setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false));
        profileNameField.setDialogFieldListener(new IDialogFieldListener() {
            public void dialogFieldChanged(DialogField field) {
                doValidate();
            }
        });

        profileNameField.setEnabled(false); // do not edit name when modifying
        
        tabFolder = new TabFolder(composite, SWT.NONE);
        tabFolder.setFont(composite.getFont());
        tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        addPages(new FormatterPreferences(workingValues));

        applyDialogFont(composite);

        tabFolder.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            public void widgetSelected(SelectionEvent e) {
                final TabItem tabItem = (TabItem) e.item;
                final FormatterTabPage page = (FormatterTabPage) tabItem
                        .getData();
                page.makeVisible();
                tabIndex = tabPages.indexOf(page);
            }
        });

        doValidate();

        return composite;
    }

    @Override
    public void updateStatus(IStatus status) {
        if (status == null) {
            doValidate();
        } else {
            super.updateStatus(status);
        }
    }

    @Override
    public boolean close() {
        CeylonPlugin.getInstance().getDialogSettings()
            .put(LAST_TAB_INDEX, tabIndex);
        return super.close();
    }

    @Override
    protected void okPressed() {
        applyPressed();
        super.okPressed();
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == APPLY_BUTTON_ID) {
            applyPressed();
            setTitle("Modify Formatter Profile \u2014 " + profile.getName());
        } else {
            super.buttonPressed(buttonId);
        }
    }

    private void applyPressed() {
        if (!profile.getName().equals(profileNameField.getText())) {
            profile = profile.rename(profileNameField.getText(),
                    this.profileManager);
        }
        profile.setSettings(workingValues, this.profileManager);
        this.profileManager.setSelected(profile);
        doValidate();
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        applyButton = createButton(parent, APPLY_BUTTON_ID, "Apply", false);
        applyButton.setEnabled(false);

        GridLayout layout = (GridLayout) parent.getLayout();
        layout.numColumns++;
        layout.makeColumnsEqualWidth = false;
        Label label = new Label(parent, SWT.NONE);
        GridData data = new GridData();
        data.widthHint = layout.horizontalSpacing;
        label.setLayoutData(data);
        super.createButtonsForButtonBar(parent);
    }

    protected final void addTabPage(String title, FormatterTabPage tabPage) {
        final TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
        applyDialogFont(tabItem.getControl());
        tabItem.setText(title);
        tabItem.setData(tabPage);
        tabItem.setControl(tabPage.createContents(tabFolder));
        tabPages.add(tabPage);
    }

    public void valuesModified(FormatterPreferences prefs) {
        doValidate();
        this.workingValues = prefs.getOptions();
    }

    @Override
    protected void updateButtonsEnableState(IStatus status) {
        super.updateButtonsEnableState(status);
        if (applyButton != null && !applyButton.isDisposed()) {
            applyButton.setEnabled(hasChanges()
                    && !status.matches(IStatus.ERROR));
        }
        if (this.saveButton != null && !this.saveButton.isDisposed()) {
            this.saveButton.setEnabled(!validateProfileName().matches(
                    IStatus.ERROR));
        }
    }

    private void doValidate() {
        String name = profileNameField.getText().trim();
        if (name.equals(profile.getName())
                && profile.hasEqualSettings(workingValues)) {
            updateStatus(STATUS_OK);
            return;
        }

        IStatus status = validateProfileName();
        if (status.matches(IStatus.ERROR)) {
            updateStatus(status);
            return;
        }

        if (!name.equals(profile.getName())
                && this.profileManager.containsName(name)) {
            updateStatus(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID,
                    "Duplicate"));
            return;
        }

        if (profile.isBuiltInProfile()) {
            updateStatus(new Status(IStatus.INFO, CeylonPlugin.PLUGIN_ID,
                    "New Created"));
            return;
        }

        updateStatus(STATUS_OK);
    }

    private IStatus validateProfileName() {
        final String name = profileNameField.getText().trim();

        if (profile.isBuiltInProfile()) {
            if (profile.getName().equals(name)) {
                return new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID,
                        "Built-in");
            }
        }

        if (name.length() == 0) {
            return new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID,
                    "Empty Name");
        }

        return STATUS_OK;
    }

    private boolean hasChanges() {
        if (!profileNameField.getText().trim().equals(profile.getName())) {
            return true;
        }

        if (!workingValues.equals(profile.getSettings())) {
            return true;
        }

        return false;
    }
}
