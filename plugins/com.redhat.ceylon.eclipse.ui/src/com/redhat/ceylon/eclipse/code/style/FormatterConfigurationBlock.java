package org.eclipse.ceylon.ide.eclipse.code.style;

import static org.eclipse.ceylon.ide.eclipse.code.style.CeylonFormatterConstants.*;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ceylon.formatter.options.loadProfile_;
import ceylon.formatter.options.saveProfile_;

import org.eclipse.ceylon.common.Constants;
import org.eclipse.ceylon.ide.eclipse.code.style.FormatterProfileManager.Profile;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

/**
 * The Ceylon code formatter preference page.
 */

public class FormatterConfigurationBlock extends StyleBlock {

    public static final String FORMATTER_PROFILE_SUFFIX = ".format";

    private Combo fProfileCombo;
    private Button fEditButton;
    private Button fDeleteButton;
    private Button fNewButton;
    private Button fLoadButton;
    private Button fExportButton;
    private PixelConverter fPixConv;

    private FormatterProfileManager fFormatterProfileManager = null;

    /**
     * Some Ceylon source code used for preview, taken from FormatterTabMisc.
     */
    protected final String PREVIEW =
            "import ceylon.collection { HashSet, LinkedList }\n"
            + "\n"
            + "\"Tests the Collatz conjecture for all numbers up to the first argument\n"
            + " (defaulting to 100).\n"
            + " \n"
            + " The Collatz conjecture states that if you start at any number,\n"
            + " and repeatedly\n"
            + " \n"
            + " - divide it by 2 if it’s even, or\n"
            + " - multiply it by 3 and add 1 if it’s odd,\n"
            + " \n"
            + " then you’ll always arrive at the cycle 1 4 2.\"\n"
            + "throws (`class AssertionError`, \"If another cycle is found\")\n"
            + "by (\"Lothar Collatz\")\n"
            + "deprecated (\"Very inefficient\")\n"
            + "shared void run() {\n"
            + "    value max = parseInteger(process.arguments.first else \"100\") else 100;\n"
            + "    HashSet<Integer[]> cycles = HashSet<Integer[]>();\n"
            + "    for (start in 1..max) {\n"
            + "        variable Integer i = start;\n"
            + "        HashSet<Integer> current = HashSet<Integer>();\n"
            + "        while (!i in current) {\n"
            + "            current.add(i);\n"
            + "            if (i % 2 == 0) {\n"
            + "                i /= 2;\n"
            + "            } else {\n"
            + "                i = 3 * i + 1;\n"
            + "            }\n"
            + "        }\n"
            + "        LinkedList<Integer> cycle = LinkedList<Integer>();\n"
            + "        while (!i in cycle) {\n"
            + "            cycle.add(i);\n"
            + "            if (i % 2 == 0) {\n"
            + "                i /= 2;\n"
            + "            } else {\n"
            + "                i = 3 * i + 1;\n"
            + "            }\n"
            + "        }\n"
            + "        assert (exists smallestFromCycle = cycle.sort(byIncreasing(identity<Integer>)).first);\n"
            + "        i = smallestFromCycle;\n"
            + "        LinkedList<Integer> orderedCycle = LinkedList<Integer>();\n"
            + "        while (!i in orderedCycle) {\n"
            + "            orderedCycle.add(i);\n"
            + "            if (i % 2 == 0) {\n"
            + "                i /= 2;\n"
            + "            } else {\n"
            + "                i = 3 * i + 1;\n"
            + "            }\n"
            + "        }\n"
            + "        cycles.add(orderedCycle.sequence());\n"
            + "    }\n"
            + "    \"Conjecture\"\n"
            + "    assert (cycles.size == 1);\n"
            + "}\n";

    private class PreviewController implements Observer {

        public PreviewController(FormatterProfileManager profileManager) {
            profileManager.addObserver(this);
            ceylonPreview.setWorkingValues(new FormatterPreferences(
                    profileManager.getSelected().getSettings()));
            ceylonPreview.update();
        }

        public void update(Observable o, Object arg) {
            final int value = ((Integer) arg).intValue();
            switch (value) {
            case FormatterProfileManager.PROFILE_CREATED_EVENT:
            case FormatterProfileManager.PROFILE_DELETED_EVENT:
            case FormatterProfileManager.SELECTION_CHANGED_EVENT:
            case FormatterProfileManager.SETTINGS_CHANGED_EVENT:
                ceylonPreview.setWorkingValues(new FormatterPreferences(
                        ((FormatterProfileManager) o).getSelected()
                                .getSettings()));
                ceylonPreview.update();
            }
        }

    }

    /**
     * The CeylonPreview.
     */
    private CeylonPreview ceylonPreview;

    public FormatterConfigurationBlock(IProject project) {
        if (project != null) {
            super.project = project;
            enableProjectSettings();
        }

        initialize();

        List<Profile> profiles = new ArrayList<Profile>();
        File[] profileFiles = null;
        if (project.getLocation().toFile().isDirectory()) {
            File ceylonConfigDir = new File(project.getLocation().toFile(),
                    Constants.CEYLON_CONFIG_DIR);
            if (ceylonConfigDir.isDirectory()) {
                profileFiles = ceylonConfigDir.listFiles(new FileFilter() {

                    @Override
                    public boolean accept(File f) {
                        if (f.isFile()
                                && f.getName().endsWith(
                                        FORMATTER_PROFILE_SUFFIX)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
            }
        }

        if (profileFiles != null) {
            for (File pf : profileFiles) {
                String profileName = pf.getName().substring(
                        0,
                        pf.getName().length()
                                - FORMATTER_PROFILE_SUFFIX.length());
                if (DEFAULT_PROFILE_NAME.equals(profileName)) {
                    continue; // 'default.format' can be copied manually in the
                              // OS FS
                }
                Profile projectProfile = new FormatterProfileManager.Profile(
                        profileName, loadProfile_.loadProfile(profileName,
                                false, project.getLocation().toFile()
                                        .getAbsolutePath()), 1, 0,
                        FormatterProfileManager.CEYLON_FORMATTER_VERSION);
                profiles.add(projectProfile);
            }
        }

        // profiles may have been deleted, cannot rely on selected profile in
        // .style
        String candidateProfile = CeylonStyle.getFormatterProfile(project);
        String activeProfile = DEFAULT_PROFILE_NAME;
        for (Profile cpf : profiles) {
            if (cpf.getName().equals(candidateProfile)) {
                activeProfile = candidateProfile;
            }
        }

        fFormatterProfileManager = createFormatterProfileManager(profiles,
                activeProfile);
    }

    protected FormatterProfileManager createFormatterProfileManager(
            List<Profile> profiles, String activeProfile) {
        return new FormatterProfileManager(profiles, activeProfile);
    }

    protected void configurePreview(Composite composite, int numColumns,
            FormatterProfileManager FormatterProfileManager) {
        createLabel(composite, "Preview", numColumns);
        CeylonPreview result = new CeylonPreview(new FormatterPreferences(
                FormatterProfileManager.getSelected().getSettings()), composite);
        result.setPreviewText(PREVIEW);
        ceylonPreview = result;

        final GridData gd = new GridData(GridData.FILL_VERTICAL
                | GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = numColumns;
        gd.verticalSpan = 7;
        gd.widthHint = 0;
        gd.heightHint = 0;
        ceylonPreview.getControl().setLayoutData(gd);

        new PreviewController(FormatterProfileManager);
    }

    protected static Label createLabel(Composite composite, String text,
            int numColumns) {
        final GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.horizontalSpan = numColumns;
        gd.widthHint = 0;

        final Label label = new Label(composite, SWT.WRAP);
        label.setFont(composite.getFont());
        label.setText(text);
        label.setLayoutData(gd);
        return label;
    }

    protected FormatterModifyProfileDialog createModifyDialog(Shell shell,
            Profile profile, FormatterProfileManager profileManager,
            boolean newProfile, boolean projectSpecific) {

        return new FormatterModifyProfileDialog(shell, profile, profileManager,
                newProfile, projectSpecific);
    }

    @Override
    public void initialize() {
        // all in constructor
    }

    @Override
    protected Control createContents(Composite parent) {
        setShell(parent.getShell());

        final int numColumns = 5;
        fPixConv = new PixelConverter(parent);
        block = createComposite(parent, numColumns);
        Label profileLabel = new Label(block, SWT.NONE);
        profileLabel.setText("A&ctive profile:");
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.horizontalSpan = numColumns;
        profileLabel.setLayoutData(data);
        fProfileCombo = createProfileCombo(block, 3,
                fPixConv.convertWidthInCharsToPixels(20));
        fEditButton = createButton(block, "&Edit...",
                GridData.HORIZONTAL_ALIGN_BEGINNING);
        fDeleteButton = createButton(block, "&Remove",
                GridData.HORIZONTAL_ALIGN_BEGINNING);
        fNewButton = createButton(block, "Ne&w...",
                GridData.HORIZONTAL_ALIGN_BEGINNING);
        fLoadButton = createButton(block, "I&mport...",
                GridData.HORIZONTAL_ALIGN_END);
        fExportButton = createButton(block, "E&xport...",
                GridData.HORIZONTAL_ALIGN_BEGINNING);
        createLabel(block, "", 3);
        configurePreview(block, numColumns, fFormatterProfileManager);

        new ButtonController();
        new ProfileComboController();

        return block;
    }

    private static Combo createProfileCombo(Composite composite, int span,
            int widthHint) {
        final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = span;
        gd.widthHint = widthHint;

        final Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        combo.setFont(composite.getFont());
        combo.setLayoutData(gd);
        return combo;
    }

    private static Button createButton(Composite composite, String text,
            final int style) {
        final Button button = new Button(composite, SWT.PUSH);
        button.setFont(composite.getFont());
        button.setText(text);

        final GridData gd = new GridData(style);
        gd.widthHint = SWTUtil.getButtonWidthHint(button);
        button.setLayoutData(gd);
        return button;
    }

    class ProfileComboController implements Observer, SelectionListener {

        private final List<Profile> fSortedProfiles;

        public ProfileComboController() {
            fSortedProfiles = fFormatterProfileManager.getSortedProfiles();
            fProfileCombo.addSelectionListener(this);
            fFormatterProfileManager.addObserver(this);
            updateProfiles();
            updateSelection();
        }

        public void widgetSelected(SelectionEvent e) {
            final int index = fProfileCombo.getSelectionIndex();
            fFormatterProfileManager.setSelected((Profile) fSortedProfiles
                    .get(index));
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void update(Observable o, Object arg) {
            if (arg == null)
                return;
            final int value = ((Integer) arg).intValue();
            switch (value) {
            case FormatterProfileManager.PROFILE_CREATED_EVENT:
            case FormatterProfileManager.PROFILE_DELETED_EVENT:
            case FormatterProfileManager.PROFILE_RENAMED_EVENT:
                updateProfiles();
                updateSelection();
                break;
            case FormatterProfileManager.SELECTION_CHANGED_EVENT:
                updateSelection();
                break;
            }
        }

        private void updateProfiles() {
            fProfileCombo.setItems(fFormatterProfileManager
                    .getSortedDisplayNames());
        }

        private void updateSelection() {
            fProfileCombo.setText(fFormatterProfileManager.getSelected()
                    .getName());
        }
    }

    class ButtonController implements Observer, SelectionListener {

        public ButtonController() {
            fFormatterProfileManager.addObserver(this);
            fNewButton.addSelectionListener(this);
            fEditButton.addSelectionListener(this);
            fDeleteButton.addSelectionListener(this);
            fLoadButton.addSelectionListener(this);
            fExportButton.addSelectionListener(this);
            update(fFormatterProfileManager, null);
        }

        public void update(Observable o, Object arg) {
            Profile selected = ((FormatterProfileManager) o).getSelected();
            final boolean notBuiltIn = !selected.isBuiltInProfile();
            fDeleteButton.setEnabled(notBuiltIn);
            fEditButton.setEnabled(notBuiltIn);
            fNewButton.setEnabled(true);
            fLoadButton.setEnabled(true);
            fExportButton.setEnabled(true);
        }

        public void widgetSelected(SelectionEvent e) {
            final Button button = (Button) e.widget;
            if (button == fEditButton)
                modifyButtonPressed();
            else if (button == fDeleteButton)
                deleteButtonPressed();
            else if (button == fNewButton)
                newButtonPressed();
            else if (button == fLoadButton)
                loadButtonPressed();
            else if (button == fExportButton)
                exportButtonPressed();
        }

        private void exportButtonPressed() {
            final FileDialog dialog = new FileDialog(block.getShell(), SWT.SAVE);
            dialog.setText("Export Ceylon Formatter profile");
            dialog.setFilterExtensions(new String[] { "*"
                    + FORMATTER_PROFILE_SUFFIX });

            String lastPath = project.getLocation().toFile().getAbsolutePath();
            if (lastPath != null) {
                dialog.setFilterPath(lastPath);
            }
            final String path = dialog.open();
            if (path == null)
                return;

            final File file = new File(path);
            if (file.exists()
                    && !MessageDialog.openQuestion(block.getShell(),
                            "Overwrite Profile?",
                            "Overwrite Profile " + file.getPath() + "?")) {
                return;
            }

            try {
                saveProfile_.saveProfile(fFormatterProfileManager.getSelected()
                        .getSettings(), fFormatterProfileManager.getSelected()
                        .getName(), file.isDirectory() ? file.getAbsolutePath()
                        : file.getParent(), file.getName());
            } catch (Exception e) {
                final String title = "Error exporting profile";
                final String message = "There was an error exporting the profile: "
                        + e.getMessage();
                handleException(
                        new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, message), 
                        block.getShell(), title, message);
            }
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        private void modifyButtonPressed() {
            final StatusDialog modifyDialog = createModifyDialog(
                    block.getShell(), fFormatterProfileManager.getSelected(),
                    fFormatterProfileManager, false, projectSettings);
            modifyDialog.open();
        }

        private void deleteButtonPressed() {
            if (MessageDialog.openQuestion(block.getShell(), "Confirm Remove",
                    "Are you sure you want to remove profile "
                            + fFormatterProfileManager.getSelected().getName()
                            + "?")) {
                if (deleteProfile(project, fFormatterProfileManager
                        .getSelected().getName())) {
                    fFormatterProfileManager.deleteSelected();
                }
            }
        }

        private boolean deleteProfile(IProject project, String profileName) {
            if (project.getLocation().toFile().isDirectory()) {
                File ceylonConfigDir = new File(project.getLocation().toFile(),
                        Constants.CEYLON_CONFIG_DIR);
                if (ceylonConfigDir.isDirectory()) {
                    try {
                        File toBeDeleted = new File(ceylonConfigDir,
                                profileName + FORMATTER_PROFILE_SUFFIX);
                        toBeDeleted.delete();
                        return true;
                    } catch (Exception e) {
                        final String title = "Error deleting profile";
                        final String message = "There was an error deleting profile "
                                + profileName + " : " + e.getMessage();
                        handleException(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, message),
                                block.getShell(), title, message);
                        return false;
                    }
                }
            }
            return false;
        }

        private void newButtonPressed() {
            final FormatterCreateProfileDialog p = new FormatterCreateProfileDialog(
                    block.getShell(), fFormatterProfileManager, false);
            if (p.open() != Window.OK)
                return;
            if (!p.openEditDialog())
                return;
            final StatusDialog modifyDialog = createModifyDialog(
                    block.getShell(), p.getCreatedProfile(),
                    fFormatterProfileManager, true, projectSettings);
            modifyDialog.open();
        }

        private void loadButtonPressed() {
            final FileDialog dialog = new FileDialog(block.getShell(), SWT.OPEN);
            dialog.setText("Load Ceylon Formatter profile");
            dialog.setFilterExtensions(new String[] { "*"
                    + FORMATTER_PROFILE_SUFFIX });
            // TODO find last path
            String lastPath = project.getLocation().toFile().getAbsolutePath();
            if (lastPath != null) {
                dialog.setFilterPath(lastPath);
            }
            final String path = dialog.open();
            if (path == null)
                return;

            final File file = new File(path);
            Profile profile = null;
            String profileName = UNNAMED_PROFILE_NAME;
            String fileName = file.getName();
            if (fileName.length() > FORMATTER_PROFILE_SUFFIX.length()
                    && fileName.endsWith(FORMATTER_PROFILE_SUFFIX)) {
                profileName = fileName.substring(0, fileName.length()
                        - FORMATTER_PROFILE_SUFFIX.length());
            }

            try {
                profile = new Profile(profileName, loadProfile_.loadProfile(
                        profileName, false, file.getParent()), 1, 0,
                        FormatterProfileManager.CEYLON_FORMATTER_VERSION);
            } catch (Exception e) {
                final String title = "Error importing profile";
                final String message = "There was an error importing the profile from file "
                        + file.getName();
                handleException(new Status(
                        IStatus.ERROR, CeylonPlugin.PLUGIN_ID, message), 
                        block.getShell(), title, e.getMessage());
            }
            if (profile == null) {
                return;
            }

            if (fFormatterProfileManager.containsName(profile.getName())
                    || UNNAMED_PROFILE_NAME.equals(profile.getName())) {
                final FormatterProfileAlreadyExistsDialog aeDialog = new FormatterProfileAlreadyExistsDialog(
                        block.getShell(), profile, fFormatterProfileManager);
                if (aeDialog.open() != Window.OK)
                    return;
            }
            try {
                CeylonStyle.writeProfileToFile(profile, project.getLocation()
                        .toFile());
                fFormatterProfileManager.addProfile(profile);
            } catch (CoreException ce) {
                handleException(ce.getStatus(), block.getShell(),
                        "Error importing into prject",
                        "There was an error importing profile to project : "
                                + project.getName());
            }
        }
    }

    @Override
    public boolean performApply() {
        try {
            // the two possible values other than a real profile
            Profile profile = fFormatterProfileManager.getSelected();
            if (profile != null) {
                if (profile.getName() != DEFAULT_PROFILE_NAME
                        && profile.getName() != UNNAMED_PROFILE_NAME) {
                    CeylonStyle.writeProfileToFile(fFormatterProfileManager
                            .getSelected(), project.getLocation().toFile());
                }
                // save the chosen profile name to Ceylon config
                // separate step for clarity
                if (profile.getName() != UNNAMED_PROFILE_NAME) {
                    CeylonStyle.setFormatterProfile(project,
                            fFormatterProfileManager.getSelected()
                                    .getName());
                }
            }
        } catch (CoreException ce) {
            handleException(ce.getStatus(), block.getShell(),
                    "Error applying changes",
                    "There was an error applying changes to project : "
                            + project.getName());
            return false;
        }
        return true;
    }

    @Override
    public void dispose() {
        if (block != null) {
            block.dispose();
            block = null;
        }
    }

    @Override
    protected void performDefaults() {
        // revert to default
        this.fFormatterProfileManager.setSelected(fFormatterProfileManager.getDefaultProfile());
        CeylonStyle.setFormatterProfile(project, this.fFormatterProfileManager.getSelected().getName());
    }
    
    private void handleException(IStatus status, Shell shell, String title, String extMsg) {
        if (status != null) {
            ErrorDialog.openError(shell, title, extMsg, status);
        } else {
            MessageDialog.openError(shell, title, extMsg);
        }
    }
}
