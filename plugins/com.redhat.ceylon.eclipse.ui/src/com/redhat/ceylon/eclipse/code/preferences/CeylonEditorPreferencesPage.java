package com.redhat.ceylon.eclipse.code.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
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
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration;

public class CeylonEditorPreferencesPage 
        extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = "com.redhat.ceylon.eclipse.ui.preferences.editor";
    
    BooleanFieldEditor enclosingBrackets;
    BooleanFieldEditor matchingBracket;
    BooleanFieldEditor currentBracket;
    BooleanFieldEditor autoInsert;
    BooleanFieldEditor autoActivation;
    BooleanFieldEditor linkedMode;
    ScaleFieldEditor autoActivationDelay;
    RadioGroupFieldEditor autoActivationChars;
    BooleanFieldEditor smartCaret;
    BooleanFieldEditor pasteCorrectIndent;
    
    public CeylonEditorPreferencesPage() {
        super(GRID);
    }
    
    @Override
    public boolean performOk() {
        enclosingBrackets.store();
        matchingBracket.store();
        currentBracket.store();
        autoInsert.store();
        autoActivation.store();
        autoActivationDelay.store();
        autoActivationChars.store();
        linkedMode.store();
        smartCaret.store();
        pasteCorrectIndent.store();
        return true;
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        enclosingBrackets.loadDefault();
        matchingBracket.loadDefault();
        currentBracket.loadDefault();
        autoActivation.loadDefault();
        autoInsert.loadDefault();
        autoActivationDelay.loadDefault();
        autoActivationChars.loadDefault();
        linkedMode.loadDefault();
        smartCaret.loadDefault();
        pasteCorrectIndent.loadDefault();
    }
    
    @Override
    public void init(IWorkbench workbench) {
    	//TODO: is it really right that we're 
    	//      storing all our preferences
    	//      in some other plugin's store??
        setPreferenceStore(EditorsPlugin.getDefault().getPreferenceStore());
//        setDescription("Preferences for the Ceylon editor");
    }
    
    
    @Override
    protected Control createContents(Composite parent) {
        Link textEditorsLink = new Link(parent, 0);
        textEditorsLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 6).create());
        textEditorsLink.setText("Ceylon preferences. See '<a>Text Editors</a>' for general editor preferences.");
        textEditorsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferencesUtil.createPreferenceDialogOn(getShell(), 
                        "org.eclipse.ui.preferencePages.GeneralTextEditor", null, null);
            }
        });
        Link colorsAndFontsLink = new Link(parent, 0);
        colorsAndFontsLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 6).create());
        colorsAndFontsLink.setText("See '<a>Colors and Fonts</a>' to customize appearance and syntax highlighting.");
        colorsAndFontsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferencesUtil.createPreferenceDialogOn(getShell(), 
                        "org.eclipse.ui.preferencePages.ColorsAndFonts", null, 
                        "selectFont:com.redhat.ceylon.eclipse.ui.editorFont");
            }
        });
        Group composite = new Group(parent, SWT.SHADOW_ETCHED_IN);
        //composite.setText("Ceylon editor settings");
        GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        composite.setLayout(layout); 
        return super.createContents(composite);
    }

    @Override
    protected void createFieldEditors() {
//        super.createDescriptionLabel(getFieldEditorParent()).setText("Bracket highlighting");
//        addField(new SpacerFieldEditor(getFieldEditorParent()));
        addField(new LabelFieldEditor("Bracket highlighting:",
                getFieldEditorParent()));
        matchingBracket = new BooleanFieldEditor(CeylonEditor.MATCHING_BRACKET, 
                "Highlight matching bracket", 
                getFieldEditorParent());
        matchingBracket.load();
        addField(matchingBracket);
        currentBracket = new BooleanFieldEditor(CeylonEditor.SELECTED_BRACKET, 
                "Highlight selected bracket", 
                getFieldEditorParent());
        currentBracket.load();
        addField(currentBracket);
        enclosingBrackets = new BooleanFieldEditor(CeylonEditor.ENCLOSING_BRACKETS, 
                "Highlight enclosing brackets", 
                getFieldEditorParent());
        enclosingBrackets.load();
        addField(enclosingBrackets);
//        super.createDescriptionLabel(getFieldEditorParent()).setText("Autocompletion");
        addField(new SpacerFieldEditor(getFieldEditorParent()));
        addField(new LabelFieldEditor("Autocompletion:",
                getFieldEditorParent()));
        autoInsert = new BooleanFieldEditor(CeylonSourceViewerConfiguration.AUTO_INSERT, 
                "Auto-insert unique completions", 
                getFieldEditorParent());
        autoInsert.load();
        addField(autoInsert);
        autoActivation = new BooleanFieldEditor(CeylonSourceViewerConfiguration.AUTO_ACTIVATION, 
                "Auto-activate completions list", 
                getFieldEditorParent());
        autoActivation.load();
        addField(autoActivation);
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        autoActivationChars = new RadioGroupFieldEditor(CeylonSourceViewerConfiguration.AUTO_ACTIVATION_CHARS, 
                "Auto-activation characters", 3, 
                new String[][] { new String[] {"period", "."}, 
                                 new String[] {"letters", letters },
                                 new String[] {"both", "." + letters } }, 
                getFieldEditorParent());
        autoActivationChars.load();
        addField(autoActivationChars);
        autoActivationDelay = new ScaleFieldEditor(CeylonSourceViewerConfiguration.AUTO_ACTIVATION_DELAY, 
                "Auto-activation delay", 
                getFieldEditorParent());
        //autoActivationDelay.setValidRange(1, 9999);
        autoActivationDelay.setMinimum(1);
        autoActivationDelay.setMaximum(2000);
        autoActivationDelay.load();
        addField(autoActivationDelay);
        linkedMode = new BooleanFieldEditor(CeylonSourceViewerConfiguration.LINKED_MODE, 
        		"Use linked mode to complete argument lists", 
        		getFieldEditorParent());
        linkedMode.load();
        addField(linkedMode);
        addField(new SpacerFieldEditor(getFieldEditorParent()));
        addField(new LabelFieldEditor("Other:",
                getFieldEditorParent()));
        smartCaret = new BooleanFieldEditor(CeylonEditor.EDITOR_SUB_WORD_NAVIGATION, 
                "Smart caret positioning inside Ceylon identifiers", 
                getFieldEditorParent());
        smartCaret.load();
        addField(smartCaret);
        pasteCorrectIndent = new BooleanFieldEditor(CeylonSourceViewerConfiguration.PASTE_CORRECT_INDENTATION, 
        		"Correct indentation of pasted code", 
        		getFieldEditorParent());
        pasteCorrectIndent.load();
        addField(pasteCorrectIndent);
    }

}

class LabelFieldEditor extends FieldEditor {

    private Label label;

    // All labels can use the same preference name since they don't
    // store any preference.
    public LabelFieldEditor(String value, Composite parent) {
        super("label", value, parent);
    }

    // Adjusts the field editor to be displayed correctly
    // for the given number of columns.
    protected void adjustForNumColumns(int numColumns) {
        ((GridData) label.getLayoutData()).horizontalSpan = numColumns;
    }

    // Fills the field editor's controls into the given parent.
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        label = getLabelControl(parent);
        
        GridData gridData = new GridData();
        gridData.horizontalSpan = numColumns;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.verticalAlignment = GridData.CENTER;
        gridData.grabExcessVerticalSpace = false;
        
        label.setLayoutData(gridData);
    }

    // Returns the number of controls in the field editor.
    public int getNumberOfControls() {
        return 1;
    }

    // Labels do not persist any preferences, so these methods are empty.
    protected void doLoad() {}
    protected void doLoadDefault() {}
    protected void doStore() {}
}

class SpacerFieldEditor extends LabelFieldEditor {
    // Implemented as an empty label field editor.
    public SpacerFieldEditor(Composite parent) {
        super("", parent);
    }
}
