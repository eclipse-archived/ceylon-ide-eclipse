package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.MATCH_HIGHLIGHTING;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
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

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonOutlinesPreferencePage extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {

    private BooleanFieldEditor displayOutlineTypes;
    private BooleanFieldEditor displayOutlineParameters;
    private BooleanFieldEditor displayOutlineParameterTypes;
    private BooleanFieldEditor displayOutlineTypeParameters;
    private RadioGroupFieldEditor matchHighlighting;
    
    public static final String ID = CeylonPlugin.PLUGIN_ID + ".preferences.outlines";
    
    public CeylonOutlinesPreferencePage() {
        super(GRID);
        setDescription("Customize the appearance of Ceylon outline and hierarchy views.");
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(getPreferences());
    }

    @Override
    protected Control createContents(Composite parent) {
        
        Link colorsAndFontsLink = new Link(parent, 0);
        colorsAndFontsLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 0).create());
        colorsAndFontsLink.setText("See '<a>Colors and Fonts</a>' to customize fonts and label colors.");
        colorsAndFontsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonPlugin.COLORS_AND_FONTS_PAGE_ID, null, 
                        "selectFont:" + 
                                CeylonPlugin.OUTLINE_FONT_PREFERENCE);
            }
        });
                
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
        composite.setLayout(new GridLayout(1, true));
        
        Control contents = super.createContents(composite);
        
        return contents;
    }

    private Group createGroup(int cols, String text) {
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

    @Override
    protected void createFieldEditors() {
        final Composite outlines = createGroup(2, "Outline and Hierarchy Labels");
        displayOutlineTypes = new BooleanFieldEditor(RETURN_TYPES_IN_OUTLINES, 
                "Display return types", 
                getFieldEditorParent(outlines));
        displayOutlineTypes.load();
        addField(displayOutlineTypes);
        displayOutlineTypeParameters = new BooleanFieldEditor(TYPE_PARAMS_IN_OUTLINES, 
                "Display type parameters", 
                getFieldEditorParent(outlines));
        displayOutlineTypeParameters.load();
        addField(displayOutlineTypeParameters);
        displayOutlineParameterTypes = new BooleanFieldEditor(PARAM_TYPES_IN_OUTLINES, 
                "Display parameter types", 
                getFieldEditorParent(outlines));
        displayOutlineParameterTypes.load();
        addField(displayOutlineParameterTypes);
        displayOutlineParameters = new BooleanFieldEditor(PARAMS_IN_OUTLINES, 
                "Display parameter names ", 
                getFieldEditorParent(outlines));
        displayOutlineParameters.load();
        addField(displayOutlineParameters);
        
        final Composite highlighting = createGroup(1, "Match highlighting");
        matchHighlighting = new RadioGroupFieldEditor(MATCH_HIGHLIGHTING, 
                "Emphasize matching text in 'Open' dialogs and proposal lists:", 4, 
                new String[][] { new String[] { "Bold", "bold" }, 
                        new String[] { "Underline", "underline" },
                        new String[] { "Text color", "color" },
//                        new String[] { "Background color", "background" },
                        new String[] { "None", "none" } }, 
                        getFieldEditorParent(highlighting));
        matchHighlighting.load();
        addField(matchHighlighting);
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        matchHighlighting.loadDefault();
        displayOutlineTypes.loadDefault();
        displayOutlineParameters.loadDefault();
        displayOutlineTypeParameters.loadDefault();
        displayOutlineParameterTypes.loadDefault();
    }
    
    @Override
    public boolean performOk() {
        matchHighlighting.store();
        displayOutlineTypes.store();
        displayOutlineParameters.store();
        displayOutlineTypeParameters.store();
        displayOutlineParameterTypes.store();
        return true;
    }

}
