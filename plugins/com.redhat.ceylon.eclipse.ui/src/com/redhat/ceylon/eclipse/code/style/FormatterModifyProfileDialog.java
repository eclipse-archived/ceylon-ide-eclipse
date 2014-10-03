package com.redhat.ceylon.eclipse.code.style;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import ceylon.formatter.options.FormattingOptions;

import com.redhat.ceylon.eclipse.code.style.FormatterProfileManager.Profile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class FormatterModifyProfileDialog extends StatusDialog implements
        IModifyDialogTabPage.IModificationListener {

    private static final int APPLY_BUTTON_ID = IDialogConstants.CLIENT_ID;
    private static final int SAVE_BUTTON_ID = IDialogConstants.CLIENT_ID + 1;

    private final boolean newProfile;
    private boolean projectSpecific;

    private Profile profile;
    private FormattingOptions workingValues;
    private final List<IModifyDialogTabPage> tabPages;

    private TabFolder tabFolder;
    private final FormatterProfileManager profileManager;
    private Button applyButton;
    private Button saveButton;
    private StringDialogField profileNameField;

    public FormatterModifyProfileDialog(Shell parentShell, Profile profile,
            FormatterProfileManager profileManager, boolean newProfile,
            boolean projectSpecific) {
        super(parentShell);

        this.profileManager = profileManager;
        this.newProfile = newProfile;
        this.projectSpecific = projectSpecific;

        this.profile = profile;
        setTitle("Modify Formatter Profile - " + profile.getName());
        this.workingValues = profile.getSettings();
        setStatusLineAboveButtons(false);
        this.tabPages = new ArrayList<IModifyDialogTabPage>();
    }

    @Override
    protected boolean isResizable() {
        return false;
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
        // TODO load last focus tab etc.

        if (!newProfile) {
            tabFolder.setSelection(lastFocusNr);
            ((IModifyDialogTabPage) tabFolder.getSelection()[0].getData())
                    .setInitialFocus();
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

        profileNameField.setEnabled(!projectSpecific);
        /*
         * fSaveButton = createButton(nameComposite, SAVE_BUTTON_ID, "Export",
         * false); fSaveButton.setEnabled(false); // !fProjectSpecific);
         */
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
                final IModifyDialogTabPage page = (IModifyDialogTabPage) tabItem
                        .getData();
                // page.fSashForm.setWeights();
                // TODO fDialogSettings.put(fKeyLastFocus,
                // fTabPages.indexOf(page));
                page.makeVisible();
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
        // TODO save dialog settings and active tab
        // final Rectangle shell= getShell().getBounds();
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
            setTitle("Modify Formatter Profile - " + profile.getName());
        } else if (buttonId == SAVE_BUTTON_ID) {
            saveButtonPressed();
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

    private void saveButtonPressed() {
        Profile selected = new Profile(profileNameField.getText(),
                workingValues, 1, 0,
                FormatterProfileManager.CEYLON_FORMATTER_VERSION);

        final FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
        dialog.setText("Save");
        dialog.setFilterExtensions(new String[] { "*.xml" });

        final String lastPath = "/"; // TODO load last
        if (lastPath != null) {
            dialog.setFilterPath(lastPath);
        }
        final String path = dialog.open();
        if (path == null)
            return;

        final File file = new File(path);
        if (file.exists()
                && !MessageDialog.openQuestion(getShell(),
                        "Overwrite Saving Profile", "Overwrite file  - "
                                + BasicElementLabels.getPathLabel(file))) {
            return;
        }

        try {
            CeylonStyle.writeProfileToFile(selected, file);
        } catch (CoreException e) {
            final String title = "Formatter Profile Save Error";
            final String message = "There was an error saving the formatter profile";
            ExceptionHandler.handle(e, getShell(), title, message);
        }
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

    protected final void addTabPage(String title, IModifyDialogTabPage tabPage) {
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
            updateStatus(StatusInfo.OK_STATUS);
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

        updateStatus(StatusInfo.OK_STATUS);
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

        return StatusInfo.OK_STATUS;
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
