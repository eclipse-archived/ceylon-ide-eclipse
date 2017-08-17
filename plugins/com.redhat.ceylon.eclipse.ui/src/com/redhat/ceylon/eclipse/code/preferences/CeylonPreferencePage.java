package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_PROJECT_TYPE;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_RESOURCE_FOLDER;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_SOURCE_FOLDER;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.redhat.ceylon.eclipse.core.debug.preferences.CeylonStepFilterPreferencePage;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonPreferencePage 
        extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {

    public static final String VM_PREFERENCE_PAGE = 
            "org.eclipse.jdt.debug.ui.preferences.VMPreferencePage";
    
    private StringFieldEditor sourceFolder;
    private StringFieldEditor resourceFolder;
    private RadioGroupFieldEditor projectType;
    
    public static final String ID = 
            CeylonPlugin.PLUGIN_ID + ".preferences";
    
    public CeylonPreferencePage() {
        super(GRID);
        setDescription("Preferences relating to Ceylon development.");
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(CeylonPlugin.getPreferences());
    }

    @Override
    protected Control createContents(Composite parent) {
        
        Link jreLink = new Link(parent, 0);
        jreLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        jreLink.setText("See Java '<a>Installed JREs</a>' to set up a Java Virtual Machine.");
        jreLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        VM_PREFERENCE_PAGE, 
                        null, null);
            }
        });
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(
                GridDataFactory.fillDefaults()
                    .grab(true, false)
                    .create());
        composite.setLayout(new GridLayout(1, true));
        
        Control contents = super.createContents(composite);
        
        Link textEditorsLink = new Link(parent, 0);
        textEditorsLink.setLayoutData(
                GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        textEditorsLink.setText("See '<a>Editor</a>' for Ceylon editor preferences.");
        textEditorsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonEditorPreferencePage.ID, null, null);
            }
        });
        
        Link completionLink = new Link(parent, 0);
        completionLink.setLayoutData(
                GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        completionLink.setText("See '<a>Completion</a>' for preferences related to content completion.");
        completionLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonCompletionPreferencePage.ID, null, null);
            }
        });
        
        Link refactoringLink = new Link(parent, 0);
        refactoringLink.setLayoutData(
                GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        refactoringLink.setText("See '<a>Refactoring</a>' for preferences related to refactoring.");
        refactoringLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonRefactoringPreferencePage.ID, 
                        null, null);
            }
        });
        
        Link saveLink = new Link(parent, 0);
        saveLink.setLayoutData(
                GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        saveLink.setText("See '<a>Save Actions</a>' to enable save actions.");
        saveLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonSaveActionsPreferencePage.ID, 
                        null, null);
            }
        });
        
        Link outlineLink = new Link(parent, 0);
        outlineLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        outlineLink.setText("See '<a>Outlines, Hierarchies, and Search Results</a>' to customize views.");
        outlineLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonOutlinesPreferencePage.ID, 
                        null, null);
            }
        });
        
        Link openLink = new Link(parent, 0);
        openLink.setLayoutData(
                GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        openLink.setText("See '<a>Open Dialogs</a>' to customize 'Open ...' dialog navigation.");
        openLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonOpenDialogsPreferencePage.ID, 
                        null, null);
            }
        });
        
        Link filtersLink = new Link(parent, 0);
        filtersLink.setLayoutData(
                GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        filtersLink.setText("See '<a>Filtering</a>' to set up filtering.");
        filtersLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonFiltersPreferencePage.ID, 
                        null, null);
            }
        });
        
        Link debugLink = new Link(parent, 0);
        debugLink.setLayoutData(
                GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        debugLink.setText("See '<a>Debugging</a>' to set up step filtering.");
        debugLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonStepFilterPreferencePage.ID, 
                        null, null);
            }
        });
        
        return contents;
    }

    private Group createGroup(int cols, String text) {
        Composite parent = getFieldEditorParent();
        Group group = new Group(parent, SWT.NONE);
        group.setText(text);
        group.setLayout(
                GridLayoutFactory.swtDefaults()
                    .equalWidth(true)
                    .numColumns(cols)
                    .create());
        group.setLayoutData(
                GridDataFactory.fillDefaults()
                    .span(3, 1)
                    .grab(true, false)
                    .create());
        return group;
    }
    
    protected Composite getFieldEditorParent(Composite group) {
        Composite parent = new Composite(group, SWT.NULL);
        parent.setLayoutData(
                GridDataFactory.fillDefaults()
                    .create());
        return parent;
    }

    @Override
    protected void createFieldEditors() {
        final Composite defaultsGroup = 
                createGroup(1, 
                        "Defaults for new Ceylon projects");
        projectType = new RadioGroupFieldEditor(DEFAULT_PROJECT_TYPE, 
                "Default target virtual machine:", 3, 
                new String[][] { new String[] { "JVM", "jvm" }, 
                        new String[] { "JavaScript", "js" },
                        new String[] { "Cross-platform", "jvm,js" } }, 
                        getFieldEditorParent(defaultsGroup));
        sourceFolder = new StringFieldEditor(DEFAULT_SOURCE_FOLDER, 
                "Default source folder name:", 
                getFieldEditorParent(defaultsGroup));
        resourceFolder = new StringFieldEditor(DEFAULT_RESOURCE_FOLDER, 
                "Default resource folder name:", 
                getFieldEditorParent(defaultsGroup));
        projectType.load();
        sourceFolder.load();
        resourceFolder.load();
        addField(projectType);
        addField(sourceFolder);
        addField(resourceFolder);

    }
    
    @Override
    protected void performDefaults() {
        projectType.loadDefault();
        sourceFolder.loadDefault();
        resourceFolder.loadDefault();
        super.performDefaults();
    }
    
    @Override
    public boolean performOk() {
        projectType.store();
        sourceFolder.store();
        resourceFolder.store();
        return super.performOk();
    }

}
