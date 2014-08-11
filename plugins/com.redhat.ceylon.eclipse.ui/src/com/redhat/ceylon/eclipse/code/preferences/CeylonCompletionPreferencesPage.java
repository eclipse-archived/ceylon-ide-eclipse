package com.redhat.ceylon.eclipse.code.preferences;

//import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.AUTO_FOLD_COMMENTS;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.AUTO_FOLD_IMPORTS;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.ENCLOSING_BRACKETS;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.MATCHING_BRACKET;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.SELECTED_BRACKET;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.SUB_WORD_NAVIGATION;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_ACTIVATION;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_ACTIVATION_CHARS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_ACTIVATION_DELAY;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_INSERT;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLEAN_IMPORTS;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_ANGLES;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_BACKTICKS;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_BRACES;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_BRACKETS;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_PARENS;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.CLOSE_QUOTES;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.COMPLETION;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.DISPLAY_PARAMETER_TYPES;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.DISPLAY_RETURN_TYPES;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.FORMAT;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.INEXACT_MATCHES;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE_EXTRACT;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE_RENAME;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.NORMALIZE_NL;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.NORMALIZE_WS;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.PASTE_CORRECT_INDENTATION;
//import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.STRIP_TRAILING_WS;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.setPreferenceDefaults;
//import static com.redhat.ceylon.eclipse.util.Indents.getIndentWithSpaces;
//import static org.eclipse.jdt.ui.PreferenceConstants.EDITOR_FOLDING_ENABLED;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;
//import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.ScaleFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
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
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonCompletionPreferencesPage 
        extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String ID = "com.redhat.ceylon.eclipse.ui.preferences.completion";
    
//    BooleanFieldEditor enclosingBrackets;
//    BoolFieldEditor matchingBracket;
//    BooleanFieldEditor currentBracket;
    BooleanFieldEditor autoInsert;
    BoolFieldEditor autoActivation;
    RadioGroupFieldEditor completion;
    RadioGroupFieldEditor inexactMatches;
    BooleanFieldEditor linkedMode;
    ScaleFieldEditor autoActivationDelay;
    RadioGroupFieldEditor autoActivationChars;
//    BooleanFieldEditor linkedModeRename;
//    BooleanFieldEditor linkedModeExtract;
//    BooleanFieldEditor displayOutlineTypes;
    BooleanFieldEditor displayParameterTypes;
//    BooleanFieldEditor smartCaret;
//    BooleanFieldEditor pasteCorrectIndent;
//    BooleanFieldEditor normalizeWs;
//    BooleanFieldEditor normalizeNl;
//    BooleanFieldEditor stripTrailingWs;
//    BooleanFieldEditor cleanImports;
//    BooleanFieldEditor format;
//    BooleanFieldEditor autoFoldImports;
//    BooleanFieldEditor autoFoldComments;
//    BooleanFieldEditor closeParens;
//    BooleanFieldEditor closeBrackets;
//    BooleanFieldEditor closeBraces;
//    BooleanFieldEditor closeAngles;
//    BooleanFieldEditor closeBackticks;
//    BooleanFieldEditor closeQuotes;
//    BoolFieldEditor enableFolding;
    
    public CeylonCompletionPreferencesPage() {
        super(GRID);
        setPreferenceDefaults();
    }
    
    @Override
    public boolean performOk() {
//        enclosingBrackets.store();
//        matchingBracket.store();
//        currentBracket.store();
        autoInsert.store();
        autoActivation.store();
        autoActivationDelay.store();
        autoActivationChars.store();
        completion.store();
        inexactMatches.store();
        linkedMode.store();
//        linkedModeRename.store();
//        linkedModeExtract.store();
//        displayOutlineTypes.store();
        displayParameterTypes.store();
//        smartCaret.store();
//        pasteCorrectIndent.store();
//        normalizeWs.store();
//        normalizeNl.store();
//        stripTrailingWs.store();
//        cleanImports.store();
//        format.store();
//        autoFoldImports.store();
//        autoFoldComments.store();
//        closeAngles.store();
//        closeBackticks.store();
//        closeBraces.store();
//        closeBrackets.store();
//        closeParens.store();
//        closeQuotes.store();
//        enableFolding.store();
        return true;
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
//        enclosingBrackets.loadDefault();
//        matchingBracket.loadDefault();
//        currentBracket.loadDefault();
        autoActivation.loadDefault();
        autoInsert.loadDefault();
        autoActivationDelay.loadDefault();
        autoActivationChars.loadDefault();
        completion.loadDefault();
        inexactMatches.loadDefault();
        linkedMode.loadDefault();
//        linkedModeRename.loadDefault();
//        linkedModeExtract.loadDefault();
//        displayOutlineTypes.loadDefault();
        displayParameterTypes.loadDefault();
//        smartCaret.loadDefault();
//        pasteCorrectIndent.loadDefault();
//        normalizeWs.loadDefault();
//        normalizeNl.loadDefault();
//        stripTrailingWs.loadDefault();
//        cleanImports.loadDefault();
//        format.loadDefault();
//        autoFoldImports.loadDefault();
//        autoFoldComments.loadDefault();
//        closeAngles.loadDefault();
//        closeBackticks.loadDefault();
//        closeBraces.loadDefault();
//        closeBrackets.loadDefault();
//        closeParens.loadDefault();
//        closeQuotes.loadDefault();
//        enableFolding.store();
    }
    
    @Override
    public void init(IWorkbench workbench) {
        //TODO: is it really right that we're 
        //      storing all our preferences
        //      in some other plugin's store??
        setPreferenceStore(EditorsUI.getPreferenceStore());
//        setDescription("Preferences for the Ceylon editor");
    }
    
    
    @Override
    protected Control createContents(Composite parent) {
        Link textEditorsLink = new Link(parent, 0);
        textEditorsLink.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).indent(0, 6).create());
        textEditorsLink.setText("See '<a>Ceylon Editor</a>' for more Ceylon editor preferences.");
        textEditorsLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonEditorPreferencesPage.ID, null, null);
            }
        });
        
        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
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
//        otherSection();
        autocompletionSection();
//        autocloseSection();
//        bracketHighlightingSection();        
//        foldingSection();
//        onSaveSection();
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
    
    interface Listener {
        void valueChanged(boolean oldValue, boolean newValue);
    }
    
    class BoolFieldEditor extends BooleanFieldEditor {
        private Listener listener;
        public BoolFieldEditor(String name, String label, Composite parent) {
            super(name, label, parent);
        }
        public BoolFieldEditor(String name, String labelText, int style,
                Composite parent) {
            super(name, labelText, style, parent);
        }
        public void setListener(Listener listener) {
            this.listener = listener;
        }
        @Override
        protected void valueChanged(boolean oldValue, boolean newValue) {
            super.valueChanged(oldValue, newValue);
            if (listener!=null) {
                listener.valueChanged(oldValue, newValue);
            }
        }
        @Override
        protected void doLoadDefault() {
            boolean oldValue = getBooleanValue();
            super.doLoadDefault();
            boolean newValue = getBooleanValue();
            if (listener!=null) {
                listener.valueChanged(oldValue, newValue);
            }
        }
    }
    
    /*private void bracketHighlightingSection() {
//        addField(new LabelFieldEditor("Bracket highlighting:",
//                getFieldEditorParent()));
        Composite group = createGroup(2, "Bracket highlighting");
        Composite p0 = getFieldEditorParent(group);
        GridData gd = new GridData();
        gd.horizontalSpan=2;
        p0.setLayoutData(gd);
        matchingBracket = new BoolFieldEditor(MATCHING_BRACKET, 
                "Enable matching bracket highlighting", p0);
        matchingBracket.load();
        addField(matchingBracket);
        final Composite p1 = getFieldEditorParent(group);
        currentBracket = new BooleanFieldEditor(SELECTED_BRACKET, 
                "Highlight selected bracket", p1);
        currentBracket.load();
        addField(currentBracket);
        final Composite p2 = getFieldEditorParent(group);
        enclosingBrackets = new BooleanFieldEditor(ENCLOSING_BRACKETS, 
                "Highlight enclosing brackets", p2);
        enclosingBrackets.load();
        addField(enclosingBrackets);
        final IPreferenceStore store = EditorsUI.getPreferenceStore();
        boolean enabled = store.getBoolean(MATCHING_BRACKET);
        currentBracket.setEnabled(enabled, p1);
        enclosingBrackets.setEnabled(enabled, p2);
        matchingBracket.setListener(new Listener() {
            @Override
            public void valueChanged(boolean oldValue, boolean newValue) {
                currentBracket.setEnabled(newValue, p1);
                enclosingBrackets.setEnabled(newValue, p2);
            }
        });
//        super.createDescriptionLabel(getFieldEditorParent()).setText("Autocompletion");
//        addField(new SpacerFieldEditor(getFieldEditorParent()));
    }*/

    private void autocompletionSection() {
//        addField(new LabelFieldEditor("Autocompletion:",
//                getFieldEditorParent()));
        Composite group = createGroup(1, "Completion");
        linkedMode = new BooleanFieldEditor(LINKED_MODE, 
                "Use linked mode to complete argument lists", 
                getFieldEditorParent(group));
        linkedMode.load();
        addField(linkedMode);
        displayParameterTypes = new BooleanFieldEditor(DISPLAY_PARAMETER_TYPES, 
                "Display parameter types in proposals", 
                getFieldEditorParent(group));
        displayParameterTypes.load();
        addField(displayParameterTypes);
        final Composite p3 = getFieldEditorParent(group);
        completion = new RadioGroupFieldEditor(COMPLETION, 
                "Completion with trailing identifier characters", 2, 
                new String[][] { new String[] { "inserts", "insert" }, 
                                 new String[] { "overwrites", "overwrite" } }, p3);
        completion.load();
        addField(completion);
        final Composite p4 = getFieldEditorParent(group);
        inexactMatches = new RadioGroupFieldEditor(INEXACT_MATCHES, 
                "For inexact matches propose", 3, 
                new String[][] { new String[] { "no arguments", "none" }, 
                                 new String[] { "positional arguments", "positional" },
                                 new String[] { "positional and named arguments", "both" } }, p4);
        inexactMatches.load();
        addField(inexactMatches);
        
        group = createGroup(1, "Auto-activation and auto-activation");
        autoInsert = new BooleanFieldEditor(AUTO_INSERT, 
                "Auto-insert unique completions", 
                getFieldEditorParent(group));
        autoInsert.load();
        addField(autoInsert);
        autoActivation = new BoolFieldEditor(AUTO_ACTIVATION, 
                "Auto-activate completions list", 
                getFieldEditorParent(group));
        autoActivation.load();
        addField(autoActivation);
        final Composite p1 = getFieldEditorParent(group);
        String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        autoActivationChars = new RadioGroupFieldEditor(AUTO_ACTIVATION_CHARS, 
                "Auto-activation characters", 3, 
                new String[][] { new String[] { "period", "." }, 
                                 new String[] { "letters", letters },
                                 new String[] { "both", "." + letters } }, p1);
        autoActivationChars.load();
        addField(autoActivationChars);
        final Composite p2 = getFieldEditorParent(group);
        autoActivationDelay = new ScaleWithLabelFieldEditor(AUTO_ACTIVATION_DELAY, 
                "Auto-activation delay", p2);
        //autoActivationDelay.setValidRange(1, 9999);
        autoActivationDelay.setMinimum(1);
        autoActivationDelay.setMaximum(2000);
        autoActivationDelay.load();
        addField(autoActivationDelay);
        final IPreferenceStore store = EditorsUI.getPreferenceStore();
        boolean enabled = store.getBoolean(AUTO_ACTIVATION);
        autoActivationChars.setEnabled(enabled, p1);
        autoActivationDelay.setEnabled(enabled, p2);        
        autoActivation.setListener(new Listener() {
            @Override
            public void valueChanged(boolean oldValue, boolean newValue) {
                autoActivationChars.setEnabled(newValue, p1);
                autoActivationDelay.setEnabled(newValue, p2);
            }
        });
//        addField(new SpacerFieldEditor(getFieldEditorParent()));
    }

    /*private void foldingSection() {
//        addField(new LabelFieldEditor("Folding:",
//                getFieldEditorParent()));
        final Composite group = createGroup(2, "Source Folding");
        Composite p0 = getFieldEditorParent(group);
        GridData gd = new GridData();
        gd.horizontalSpan=2;
        p0.setLayoutData(gd);
        enableFolding = new BoolFieldEditor(EDITOR_FOLDING_ENABLED, 
                "Enable source folding", p0);
        enableFolding.load();
        addField(enableFolding);
        final Composite p1 = getFieldEditorParent(group);
        autoFoldImports = new BooleanFieldEditor(AUTO_FOLD_IMPORTS, 
                "Automatically fold import lists", p1);
        autoFoldImports.load();
        addField(autoFoldImports);
        final Composite p2 = getFieldEditorParent(group);
        autoFoldComments = new BooleanFieldEditor(AUTO_FOLD_COMMENTS, 
                "Automatically fold comments", p2);
        autoFoldComments.load();
        addField(autoFoldComments);
        final IPreferenceStore store = EditorsUI.getPreferenceStore();
        boolean enabled = store.getBoolean(EDITOR_FOLDING_ENABLED);
        autoFoldImports.setEnabled(enabled, p1);
        autoFoldComments.setEnabled(enabled, p2);
        enableFolding.setListener(new Listener() {
            @Override
            public void valueChanged(boolean oldValue, boolean newValue) {
                autoFoldImports.setEnabled(newValue, p1);
                autoFoldComments.setEnabled(newValue, p2);
            }
        });
//        addField(new SpacerFieldEditor(getFieldEditorParent()));
    }*/

    /*private void otherSection() {
//        addField(new LabelFieldEditor("Other:",
//                getFieldEditorParent()));
        Composite group = createGroup(1, "General");
        linkedModeRename = new BooleanFieldEditor(LINKED_MODE_RENAME, 
                "Use linked mode for rename", 
                getFieldEditorParent(group));
        linkedModeRename.load();
        addField(linkedModeRename);
        linkedModeExtract = new BooleanFieldEditor(LINKED_MODE_EXTRACT, 
                "Use linked mode for extract refactorings", 
                getFieldEditorParent(group));
        linkedModeExtract.load();
        addField(linkedModeExtract);
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
        displayOutlineTypes = new BooleanFieldEditor(DISPLAY_RETURN_TYPES, 
                "Display return types in outlines", 
                getFieldEditorParent(group));
        displayOutlineTypes.load();
        addField(displayOutlineTypes);
    }*/
    
    /*private void onSaveSection() {
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
        EditorUtil.getPreferences().addPropertyChangeListener(listener);
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
    }*/
    
    protected Composite getFieldEditorParent(Composite group) {
        Composite parent = new Composite(group, SWT.NULL);
        parent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return parent;
    }

    /*private void autocloseSection() {
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
    }*/
    
    private IPropertyChangeListener listener;
    
    @Override
    public void dispose() {
        super.dispose();
        if (listener!=null) {
            EditorUtil.getPreferences().removePropertyChangeListener(listener);
        }
    }

}
