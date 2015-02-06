package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_RESOURCE_FOLDER;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_SOURCE_FOLDER;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.redhat.ceylon.eclipse.core.debug.preferences.CeylonStepFilterPreferencePage;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonPreferencePage extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {

    private StringFieldEditor sourceFolder;
    private StringFieldEditor resourceFolder;

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
        
        Link colorsAndFontsLink = new Link(parent, 0);
        colorsAndFontsLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        colorsAndFontsLink.setText("See '<a>Colors and Fonts</a>' to customize appearance and syntax highlighting.");
        colorsAndFontsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        "org.eclipse.ui.preferencePages.ColorsAndFonts", null, 
                        "selectFont:com.redhat.ceylon.eclipse.ui.editorFont");
            }
        });
        
        Link jreLink = new Link(parent, 0);
        jreLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        jreLink.setText("See Java '<a>Installed JREs</a>' to set up the Java Virtual Machine.");
        jreLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        "org.eclipse.jdt.debug.ui.preferences.VMPreferencePage", 
                        null, null);
            }
        });
        
        Control contents = super.createContents(parent);
        
        return contents;
    }

    @Override
    protected void createFieldEditors() {
        sourceFolder = new StringFieldEditor(DEFAULT_SOURCE_FOLDER, 
                "Default source folder name for new projects:", getFieldEditorParent());
        resourceFolder = new StringFieldEditor(DEFAULT_RESOURCE_FOLDER, 
                "Default resource folder name for new projects:", getFieldEditorParent());
        sourceFolder.load();
        resourceFolder.load();
        addField(sourceFolder);
        addField(resourceFolder);
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        sourceFolder.loadDefault();
        resourceFolder.loadDefault();
    }
    
    @Override
    public boolean performOk() {
        sourceFolder.store();
        resourceFolder.store();
        return true;
    }

}
