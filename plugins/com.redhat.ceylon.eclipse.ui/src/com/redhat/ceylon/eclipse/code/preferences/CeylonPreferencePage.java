package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_PROJECT_TYPE;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_RESOURCE_FOLDER;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_SOURCE_FOLDER;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.redhat.ceylon.eclipse.core.debug.preferences.CeylonStepFilterPreferencePage;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonPreferencePage extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {

    private StringFieldEditor sourceFolder;
    private StringFieldEditor resourceFolder;
    private RadioGroupFieldEditor projectType;

    public CeylonPreferencePage() {
        super(GRID);
        setDescription("Preferences relating to Ceylon development.");
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(EditorUtil.getPreferences());
    }

    @Override
    protected Control createContents(Composite parent) {
        
        Link jreLink = new Link(parent, 0);
        jreLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        jreLink.setText("See Java '<a>Installed JREs</a>' to set up a Java Virtual Machine.");
        jreLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        "org.eclipse.jdt.debug.ui.preferences.VMPreferencePage", 
                        null, null);
            }
        });
        
        Composite composite = new Composite(parent, SWT.NONE);
        //composite.setText("Ceylon editor settings");
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout);
        
        Control contents = super.createContents(composite);
        
        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sep.setLayoutData(sgd);

        Link textEditorsLink = new Link(parent, 0);
        textEditorsLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        textEditorsLink.setText("See '<a>Editor</a>' for Ceylon editor preferences.");
        textEditorsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonEditorPreferencePage.ID, null, null);
            }
        });
        
        Link completionLink = new Link(parent, 0);
        completionLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        completionLink.setText("See '<a>Completion</a>' for preferences related to content completion.");
        completionLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonCompletionPreferencePage.ID, null, null);
            }
        });
        
        Link saveLink = new Link(parent, 0);
        saveLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        saveLink.setText("See '<a>Save Actions</a>' to enable save actions.");
        saveLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonSaveActionsPreferencePage.ID, null, null);
            }
        });
        
        Link debugLink = new Link(parent, 0);
        debugLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        debugLink.setText("See '<a>Debugging</a>' to set up step filtering.");
        debugLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonStepFilterPreferencePage.ID, null, null);
            }
        });
        
//        Link colorsAndFontsLink = new Link(parent, 0);
//        colorsAndFontsLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
//        colorsAndFontsLink.setText("See '<a>Colors and Fonts</a>' to customize appearance and syntax highlighting.");
//        colorsAndFontsLink.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                createPreferenceDialogOn(getShell(), 
//                        "org.eclipse.ui.preferencePages.ColorsAndFonts", null, 
//                        "selectFont:com.redhat.ceylon.eclipse.ui.editorFont");
//            }
//        });
        
        return contents;
    }

    private Composite createGroup(int cols, String text) {
        Composite parent = getFieldEditorParent();
        Group group = new Group(parent, SWT.NONE);
        group.setText(text);
        GridLayout layout = new GridLayout(cols, true);
        group.setLayout(layout);
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        gd.horizontalSpan=3;
        group.setLayoutData(gd);
        return group;
    }
    
    protected Composite getFieldEditorParent(Composite group) {
        Composite parent = new Composite(group, SWT.NULL);
        parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return parent;
    }

    @Override
    protected void createFieldEditors() {
        final Composite group = createGroup(1, "Defaults for new Ceylon projects");
        projectType = new RadioGroupFieldEditor(DEFAULT_PROJECT_TYPE, 
                "Default target virtual machine:", 3, 
                new String[][] { new String[] { "JVM", "jvm" }, 
                        new String[] { "JavaScript", "js" },
                        new String[] { "Cross-platform", "jvm,js" } }, 
                        getFieldEditorParent(group));
        sourceFolder = new StringFieldEditor(DEFAULT_SOURCE_FOLDER, 
                "Default source folder name:", 
                getFieldEditorParent(group));
        resourceFolder = new StringFieldEditor(DEFAULT_RESOURCE_FOLDER, 
                "Default resource folder name:", 
                getFieldEditorParent(group));
        projectType.load();
        sourceFolder.load();
        resourceFolder.load();
        addField(projectType);
        addField(sourceFolder);
        addField(resourceFolder);
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        projectType.loadDefault();
        sourceFolder.loadDefault();
        resourceFolder.loadDefault();
    }
    
    @Override
    public boolean performOk() {
        projectType.store();
        sourceFolder.store();
        resourceFolder.store();
        return true;
    }

}
