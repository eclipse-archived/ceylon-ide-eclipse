package com.redhat.ceylon.eclipse.code.style;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ceylon.formatter.options.FormattingOptions;

import com.redhat.ceylon.eclipse.code.style.FormatterProfileManager.Profile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * The dialog to create a new profile.
 */
public class FormatterCreateProfileDialog extends StatusDialog {

    private static final String PREF_OPEN_EDIT_DIALOG = CeylonPlugin.PLUGIN_ID
            + ".style.formatter.create_profile_dialog.open_edit";

    private Text fNameText;
    private Combo fProfileCombo;
    private Button fEditCheckbox;

    private final static StatusInfo fOk = new StatusInfo();
    private final static StatusInfo fEmpty = new StatusInfo(IStatus.ERROR,
            "Formatter Profile name is empty");
    private final static StatusInfo fDuplicate = new StatusInfo(IStatus.ERROR,
            "Formatter Profile with this name already exists");

    private final FormatterProfileManager fProfileManager;
    private final List<Profile> fSortedProfiles;
    private final String[] fSortedNames;

    private Profile fCreatedProfile;
    protected boolean fOpenEditDialog;

    private boolean fProjectSpecific;

    public FormatterCreateProfileDialog(Shell parentShell,
            FormatterProfileManager profileManager, boolean projectSpecific) {
        super(parentShell);
        fProfileManager = profileManager;
        fProjectSpecific = projectSpecific;

        fSortedProfiles = fProfileManager.getSortedProfiles();
        fSortedNames = fProfileManager.getSortedDisplayNames();
    }

    @Override
    public void create() {
        super.create();
        setTitle("Create Formatter Profile");
    }

    @Override
    public Control createDialogArea(Composite parent) {

        final int numColumns = 2;

        final Composite composite = (Composite) super.createDialogArea(parent);
        ((GridLayout) composite.getLayout()).numColumns = numColumns;

        // Create "Profile name:" label
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = numColumns;
        gd.widthHint = convertWidthInCharsToPixels(60);
        final Label nameLabel = new Label(composite, SWT.WRAP);
        nameLabel.setText("Create Formatter Profile");
        nameLabel.setLayoutData(gd);

        // Create text field to enter name
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = numColumns;
        fNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        fNameText.setLayoutData(gd);

        fNameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                doValidation();
            }
        });

        if (fProjectSpecific) {
            fNameText.setText("Project Settings");
            fNameText.setEnabled(false);
        }

        // Create "Initialize settings ..." label
        gd = new GridData();
        gd.horizontalSpan = numColumns;
        Label profileLabel = new Label(composite, SWT.WRAP);
        profileLabel.setText("Base Formatter Profile on");
        profileLabel.setLayoutData(gd);

        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = numColumns;
        fProfileCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        fProfileCombo.setLayoutData(gd);

        gd = new GridData();
        gd.horizontalSpan = numColumns;
        fEditCheckbox = new Button(composite, SWT.CHECK);
        fEditCheckbox.setText("Edit Created Profile");
        fEditCheckbox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                fOpenEditDialog = ((Button) e.widget).getSelection();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        final IDialogSettings dialogSettings = CeylonPlugin.getInstance()
                .getDialogSettings();
        if (dialogSettings.get(PREF_OPEN_EDIT_DIALOG) != null) {
            fOpenEditDialog = dialogSettings.getBoolean(PREF_OPEN_EDIT_DIALOG);
        } else {
            fOpenEditDialog = true;
        }
        fEditCheckbox.setSelection(fOpenEditDialog);

        fProfileCombo.setItems(fSortedNames);
        fProfileCombo.setText(fProfileManager.getDefaultProfile().getName());

        if (!fProjectSpecific) {
            fNameText.setFocus();
            updateStatus(fEmpty);
        } else {
            updateStatus(fOk);
        }

        applyDialogFont(composite);

        return composite;
    }

    /**
     * Validate the current settings
     */
    protected void doValidation() {
        final String name = fNameText.getText().trim();

        if (fProfileManager.containsName(name)) {
            updateStatus(fDuplicate);
            return;
        }
        if (name.length() == 0) {
            updateStatus(fEmpty);
            return;
        }
        updateStatus(fOk);
    }

    @Override
    protected void okPressed() {
        if (!getStatus().isOK())
            return;

        final FormattingOptions baseSettings = fSortedProfiles.get(
                fProfileCombo.getSelectionIndex()).getSettings();
        final String profileName = fNameText.getText();

        fCreatedProfile = new Profile(profileName, baseSettings, 1, 0,
                FormatterProfileManager.CEYLON_FORMATTER_VERSION);
        fProfileManager.addProfile(fCreatedProfile);
        super.okPressed();
    }

    public final Profile getCreatedProfile() {
        return fCreatedProfile;
    }

    public final boolean openEditDialog() {
        return fOpenEditDialog;
    }
}
