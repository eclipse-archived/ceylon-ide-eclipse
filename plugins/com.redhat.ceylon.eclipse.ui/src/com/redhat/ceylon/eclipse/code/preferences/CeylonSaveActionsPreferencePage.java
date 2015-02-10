package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.CLEAN_IMPORTS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.FORMAT;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.NORMALIZE_NL;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.NORMALIZE_WS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.STRIP_TRAILING_WS;
import static com.redhat.ceylon.eclipse.util.Indents.getIndentWithSpaces;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonSaveActionsPreferencePage 
        extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = "com.redhat.ceylon.eclipse.ui.preferences.save";
    
    BooleanFieldEditor normalizeWs;
    BooleanFieldEditor normalizeNl;
    BooleanFieldEditor stripTrailingWs;
    BooleanFieldEditor cleanImports;
    BooleanFieldEditor format;
    
    public CeylonSaveActionsPreferencePage() {
        super(GRID);
        setDescription("Save actions are executed each time a Ceylon source file is saved.");
    }
    
    @Override
    public boolean performOk() {
        normalizeWs.store();
        normalizeNl.store();
        stripTrailingWs.store();
        cleanImports.store();
        format.store();
        return true;
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        normalizeWs.loadDefault();
        normalizeNl.loadDefault();
        stripTrailingWs.loadDefault();
        cleanImports.loadDefault();
        format.loadDefault();
    }
    
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(EditorUtil.getPreferences());
    }
    
    @Override
    protected Control createContents(Composite parent) {

//        Composite composite = new Composite(parent, SWT.NONE);
//        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
//        composite.setLayout(new GridLayout(1, true));
        
        Control result = super.createContents(parent);
          
        return result;
    }

    @Override
    protected void createFieldEditors() {
        Composite group = createGroup(1, "On save");
        final Composite parent = getFieldEditorParent(group);
        normalizeWs = new BooleanFieldEditor(NORMALIZE_WS, 
                "Convert tabs to spaces (if insert spaces for tabs enabled)",
                parent);
        normalizeWs.load();
        normalizeWs.setEnabled(getIndentWithSpaces(), parent);
        listener=new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getProperty().equals(EDITOR_SPACES_FOR_TABS)) {
                    normalizeWs.setEnabled(getIndentWithSpaces(), parent);
                }
            }
        };
        EditorsUI.getPreferenceStore().addPropertyChangeListener(listener);
        addField(normalizeWs);
        normalizeNl = new BooleanFieldEditor(NORMALIZE_NL, 
                "Fix line endings",
                parent);
        normalizeNl.load();
        addField(normalizeNl);
        stripTrailingWs = new BooleanFieldEditor(STRIP_TRAILING_WS, 
                "Strip trailing whitespace",
                parent);
        stripTrailingWs.load();
        addField(stripTrailingWs);
        cleanImports = new BooleanFieldEditor(CLEAN_IMPORTS, 
                "Clean imports",
                parent);
        cleanImports.load();
        addField(cleanImports);
        format = new BooleanFieldEditor(FORMAT,
                "Format",
                parent);
        format.load();
        addField(format);
    }

    private Composite createGroup(int cols, String text) {
        Composite parent = getFieldEditorParent();
        Group group = new Group(parent, SWT.NONE);
        group.setText(text);
        group.setLayout(GridLayoutFactory.swtDefaults().equalWidth(true).numColumns(cols).create());
        group.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).grab(true, false).create());
        return group;
    }
    
    protected Composite getFieldEditorParent(Composite group) {
        Composite parent = new Composite(group, SWT.NULL);
        parent.setLayoutData(GridDataFactory.fillDefaults().create());
        return parent;
    }

    private IPropertyChangeListener listener;
    
    @Override
    public void dispose() {
        super.dispose();
        if (listener!=null) {
            EditorUtil.getPreferences().removePropertyChangeListener(listener);
        }
    }

}
