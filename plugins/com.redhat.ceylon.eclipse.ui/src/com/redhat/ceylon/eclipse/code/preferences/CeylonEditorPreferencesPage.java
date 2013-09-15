package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.AUTO_FOLD_COMMENTS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.AUTO_FOLD_IMPORTS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.ENCLOSING_BRACKETS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.MATCHING_BRACKET;
import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.SELECTED_BRACKET;
import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.SUB_WORD_NAVIGATION;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_ACTIVATION;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_ACTIVATION_CHARS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_ACTIVATION_DELAY;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_INSERT;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_ANGLES;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_BACKTICKS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_BRACES;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_BRACKETS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_PARENS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_QUOTES;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE_RENAME;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.PASTE_CORRECT_INDENTATION;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
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
    BooleanFieldEditor linkedModeRename;
    ScaleFieldEditor autoActivationDelay;
    RadioGroupFieldEditor autoActivationChars;
    BooleanFieldEditor smartCaret;
    BooleanFieldEditor pasteCorrectIndent;
    BooleanFieldEditor autoFoldImports;
    BooleanFieldEditor autoFoldComments;
    BooleanFieldEditor closeParens;
    BooleanFieldEditor closeBrackets;
    BooleanFieldEditor closeBraces;
    BooleanFieldEditor closeAngles;
    BooleanFieldEditor closeBackticks;
    BooleanFieldEditor closeQuotes;
    
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
        linkedModeRename.store();
        smartCaret.store();
        pasteCorrectIndent.store();
        autoFoldImports.store();
        autoFoldComments.store();
        closeAngles.store();
        closeBackticks.store();
        closeBraces.store();
        closeBrackets.store();
        closeParens.store();
        closeQuotes.store();
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
        linkedModeRename.loadDefault();
        smartCaret.loadDefault();
        pasteCorrectIndent.loadDefault();
        autoFoldImports.loadDefault();
        autoFoldComments.loadDefault();
        closeAngles.loadDefault();
        closeBackticks.loadDefault();
        closeBraces.loadDefault();
        closeBrackets.loadDefault();
        closeParens.loadDefault();
        closeQuotes.loadDefault();
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
        textEditorsLink.setText("See '<a>Text Editors</a>' for general editor preferences.");
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
        Link annotationsLink = new Link(parent, 0);
        annotationsLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 6).create());
        annotationsLink.setText("See '<a>Annotations</a>' to customize annotation appearance.");
        annotationsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                PreferencesUtil.createPreferenceDialogOn(getShell(), 
                        "org.eclipse.ui.editors.preferencePages.Annotations", null, null);
            }
        });
        
        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sep.setLayoutData(sgd);

        Composite composite = new Composite(parent, SWT.NONE);
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
        otherSection();
        autocompletionSection();
        autocloseSection();
        bracketHighlightingSection();        
        foldingSection();
    }

    private void bracketHighlightingSection() {
//        addField(new LabelFieldEditor("Bracket highlighting:",
//                getFieldEditorParent()));
        Composite group = createGroup(1, "Bracket highlighting");
        matchingBracket = new BooleanFieldEditor(MATCHING_BRACKET, 
                "Highlight matching bracket", 
                getFieldEditorParent(group));
        matchingBracket.load();
        addField(matchingBracket);
        currentBracket = new BooleanFieldEditor(SELECTED_BRACKET, 
                "Highlight selected bracket", 
                getFieldEditorParent(group));
        currentBracket.load();
        addField(currentBracket);
        enclosingBrackets = new BooleanFieldEditor(ENCLOSING_BRACKETS, 
                "Highlight enclosing brackets", 
                getFieldEditorParent(group));
        enclosingBrackets.load();
        addField(enclosingBrackets);
//        super.createDescriptionLabel(getFieldEditorParent()).setText("Autocompletion");
//        addField(new SpacerFieldEditor(getFieldEditorParent()));
    }

    private void autocompletionSection() {
//        addField(new LabelFieldEditor("Autocompletion:",
//                getFieldEditorParent()));
        Composite group = createGroup(1, "Autocompletion");
        autoInsert = new BooleanFieldEditor(AUTO_INSERT, 
                "Auto-insert unique completions", 
                getFieldEditorParent(group));
        autoInsert.load();
        addField(autoInsert);
        autoActivation = new BooleanFieldEditor(AUTO_ACTIVATION, 
                "Auto-activate completions list", 
                getFieldEditorParent(group));
        autoActivation.load();
        addField(autoActivation);
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        autoActivationChars = new RadioGroupFieldEditor(AUTO_ACTIVATION_CHARS, 
                "Auto-activation characters", 3, 
                new String[][] { new String[] {"period", "."}, 
                                 new String[] {"letters", letters },
                                 new String[] {"both", "." + letters } }, 
                getFieldEditorParent(group));
        autoActivationChars.load();
        addField(autoActivationChars);
        autoActivationDelay = new ScaleWithLabelFieldEditor(AUTO_ACTIVATION_DELAY, 
                "Auto-activation delay", 
                getFieldEditorParent(group));
        //autoActivationDelay.setValidRange(1, 9999);
        autoActivationDelay.setMinimum(1);
        autoActivationDelay.setMaximum(2000);
        autoActivationDelay.load();
        addField(autoActivationDelay);
        linkedMode = new BooleanFieldEditor(LINKED_MODE, 
                "Use linked mode to complete argument lists", 
                getFieldEditorParent(group));
        linkedMode.load();
        addField(linkedMode);
//        addField(new SpacerFieldEditor(getFieldEditorParent()));        
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

    private void foldingSection() {
//        addField(new LabelFieldEditor("Folding:",
//                getFieldEditorParent()));
        Composite group = createGroup(2, "Source Folding");
        autoFoldImports = new BooleanFieldEditor(AUTO_FOLD_IMPORTS, 
                "Automatically fold import lists", 
                getFieldEditorParent(group));
        autoFoldImports.load();
        addField(autoFoldImports);
        autoFoldComments = new BooleanFieldEditor(AUTO_FOLD_COMMENTS, 
                "Automatically fold comments", 
                getFieldEditorParent(group));
        autoFoldComments.load();
        addField(autoFoldComments);
//        addField(new SpacerFieldEditor(getFieldEditorParent()));
    }

    private void otherSection() {
//        addField(new LabelFieldEditor("Other:",
//                getFieldEditorParent()));
        Composite group = createGroup(1, "General");
        linkedModeRename = new BooleanFieldEditor(LINKED_MODE_RENAME, 
                "Use linked mode for rename", 
                getFieldEditorParent(group));
        linkedModeRename.load();
        addField(linkedModeRename);
        smartCaret = new BooleanFieldEditor(SUB_WORD_NAVIGATION, 
                "Smart caret positioning inside identifiers", 
                getFieldEditorParent(group));
        smartCaret.load();
        addField(smartCaret);
        pasteCorrectIndent = new BooleanFieldEditor(PASTE_CORRECT_INDENTATION, 
                "Correct indentation of pasted code", 
                getFieldEditorParent(group));
        pasteCorrectIndent.load();
        addField(pasteCorrectIndent);
    }
    
    protected Composite getFieldEditorParent(Composite group) {
        Composite parent = new Composite(group, SWT.NULL);
        parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return parent;
    }

    private void autocloseSection() {
//        addField(new LabelFieldEditor("Automatically close:",
//                getFieldEditorParent()));
        Composite group = createGroup(3, "Automatically close");
        closeParens = new BooleanFieldEditor(CLOSE_PARENS, 
                "Parentheses", 
                getFieldEditorParent(group));
        closeParens.load();
        addField(closeParens);
        closeBrackets = new BooleanFieldEditor(CLOSE_BRACKETS, 
                "Brackets", 
                getFieldEditorParent(group));
        closeBrackets.load();
        addField(closeBrackets);
        closeAngles = new BooleanFieldEditor(CLOSE_ANGLES, 
                "Angle brackets", 
                getFieldEditorParent(group));
        closeAngles.load();
        addField(closeAngles);
        closeBackticks = new BooleanFieldEditor(CLOSE_BACKTICKS, 
                "Backticks", 
                getFieldEditorParent(group));
        closeBackticks.load();
        addField(closeBackticks);
        closeBraces = new BooleanFieldEditor(CLOSE_BRACES, 
                "Braces", 
                getFieldEditorParent(group));
        closeBraces.load();
        addField(closeBraces);
        closeQuotes = new BooleanFieldEditor(CLOSE_QUOTES, 
                "Quotes", 
                getFieldEditorParent(group));
        closeQuotes.load();
        addField(closeQuotes);
//        addField(new SpacerFieldEditor(getFieldEditorParent()));
    }

}

//class LabelFieldEditor extends FieldEditor {
//
//    private Label label;
//
//    // All labels can use the same preference name since they don't
//    // store any preference.
//    public LabelFieldEditor(String value, Composite parent) {
//        super("label", value, parent);
//    }
//
//    // Adjusts the field editor to be displayed correctly
//    // for the given number of columns.
//    protected void adjustForNumColumns(int numColumns) {
//        ((GridData) label.getLayoutData()).horizontalSpan = numColumns;
//    }
//
//    // Fills the field editor's controls into the given parent.
//    protected void doFillIntoGrid(Composite parent, int numColumns) {
//        label = getLabelControl(parent);
//        
//        GridData gridData = new GridData();
//        gridData.horizontalSpan = numColumns;
//        gridData.horizontalAlignment = GridData.FILL;
//        gridData.grabExcessHorizontalSpace = false;
//        gridData.verticalAlignment = GridData.CENTER;
//        gridData.grabExcessVerticalSpace = false;
//        
//        label.setLayoutData(gridData);
//    }
//
//    // Returns the number of controls in the field editor.
//    public int getNumberOfControls() {
//        return 1;
//    }
//
//    // Labels do not persist any preferences, so these methods are empty.
//    protected void doLoad() {}
//    protected void doLoadDefault() {}
//    protected void doStore() {}
//}
//
//class SpacerFieldEditor extends LabelFieldEditor {
//    // Implemented as an empty label field editor.
//    public SpacerFieldEditor(Composite parent) {
//        super("", parent);
//    }
//}
