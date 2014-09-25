package com.redhat.ceylon.eclipse.code.style;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.eclipse.code.style.FormatterProfileManager.Profile;

/**
 * The dialog to rename an imported profile.
 */
public class FormatterProfileAlreadyExistsDialog extends StatusDialog {

    private Composite fComposite;
    protected Text fNameText;
    private Button fRenameRadio, fOverwriteRadio;

    private final int NUM_COLUMNS = 2;

    private final StatusInfo fOk;
    private final StatusInfo fEmpty;
    private final StatusInfo fDuplicate;

    private final Profile profile;
    private final FormatterProfileManager fProfileManager;

    public FormatterProfileAlreadyExistsDialog(Shell parentShell,
            Profile profile, FormatterProfileManager profileManager) {
        super(parentShell);
        this.profile = profile;
        fProfileManager = profileManager;
        fOk = new StatusInfo();
        fDuplicate = new StatusInfo(IStatus.ERROR,
                "Formatter Profile already exists");
        fEmpty = new StatusInfo(IStatus.ERROR,
                "Formatter Profile name is empty");

        setHelpAvailable(false);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Formatter Profile Already Exists");
    }

    @Override
    public Control createDialogArea(Composite parent) {

        fComposite = (Composite) super.createDialogArea(parent);
        ((GridLayout) fComposite.getLayout()).numColumns = NUM_COLUMNS;

        createLabel("Formatter Profile " + this.profile.getName()
                + " already exists");

        fRenameRadio = createRadioButton("Rename");
        fNameText = createTextField();

        fOverwriteRadio = createRadioButton("Overwrite");

        if (this.profile.getName().equalsIgnoreCase("default")
                || this.profile.getName().equalsIgnoreCase("unnamed")) {
            fOverwriteRadio.setEnabled(false);
        }

        fRenameRadio.setSelection(true);

        fNameText.setText(this.profile.getName());
        fNameText.setSelection(0, this.profile.getName().length());
        fNameText.setFocus();

        fNameText.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                doValidation();
            }
        });

        fRenameRadio.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                fNameText.setEnabled(true);
                fNameText.setFocus();
                fNameText.setSelection(0, fNameText.getText().length());
                doValidation();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        fOverwriteRadio.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                fNameText.setEnabled(false);
                doValidation();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });

        updateStatus(fDuplicate);

        applyDialogFont(fComposite);

        return fComposite;
    }

    private Label createLabel(String text) {
        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = NUM_COLUMNS;
        gd.widthHint = convertWidthInCharsToPixels(60);
        final Label label = new Label(fComposite, SWT.WRAP);
        label.setText(text);
        label.setLayoutData(gd);
        return label;
    }

    private Button createRadioButton(String text) {
        final GridData gd = new GridData();
        gd.horizontalSpan = NUM_COLUMNS;
        gd.widthHint = convertWidthInCharsToPixels(60);
        final Button radio = new Button(fComposite, SWT.RADIO);
        radio.setLayoutData(gd);
        radio.setText(text);
        return radio;
    }

    private Text createTextField() {
        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = NUM_COLUMNS;
        gd.horizontalIndent = 15;
        final Text text = new Text(fComposite, SWT.SINGLE | SWT.BORDER);
        text.setLayoutData(gd);
        return text;
    }

    /**
     * Validate the current settings
     */
    protected void doValidation() {

        if (fOverwriteRadio.getSelection()) {
            updateStatus(fOk);
            return;
        }

        final String name = fNameText.getText().trim();

        if (name.length() == 0) {
            updateStatus(fEmpty);
            return;
        }

        if (fProfileManager.containsName(name)) {
            updateStatus(fDuplicate);
            return;
        }

        updateStatus(fOk);
    }

    @Override
    protected void okPressed() {
        if (!getStatus().isOK())
            return;
        if (fRenameRadio.getSelection())
            this.profile.rename(fNameText.getText().trim(), fProfileManager);
        super.okPressed();
    }
}
