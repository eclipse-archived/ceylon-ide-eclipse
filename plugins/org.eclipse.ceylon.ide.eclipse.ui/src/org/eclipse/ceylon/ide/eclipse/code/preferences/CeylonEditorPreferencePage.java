/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import static org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor.ENCLOSING_BRACKETS;
import static org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor.MATCHING_BRACKET;
import static org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor.SELECTED_BRACKET;
import static org.eclipse.ceylon.ide.eclipse.code.hover.AnnotationHover.ANNOTATION_PREFERENCE_PAGE_ID;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_FOLD_COMMENTS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_FOLD_IMPORTS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_ANGLES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_BACKTICKS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_BRACES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_BRACKETS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_PARENS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.CLOSE_QUOTES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PASTE_CORRECT_INDENTATION;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PASTE_ESCAPE_QUOTED;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PASTE_IMPORTS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.SUB_WORD_NAVIGATION;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.COLORS_AND_FONTS_PAGE_ID;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.EDITOR_FONT_PREFERENCE;
import static org.eclipse.jdt.ui.PreferenceConstants.EDITOR_FOLDING_ENABLED;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.editors.text.EditorsUI;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class CeylonEditorPreferencePage 
        extends FieldEditorPreferencePage 
        implements IWorkbenchPreferencePage {
    
    public static final String TEXTEDITOR_PREFERENCE_PAGE_ID = 
            "org.eclipse.ui.preferencePages.GeneralTextEditor";

    public static final String ID = 
            CeylonPlugin.PLUGIN_ID + ".preferences.editor";
    
    BoolFieldEditor bracketMatching;
    Button oppositeBracket;
    Button matchingBrackets;
    Button enclosingBrackets;
    BooleanFieldEditor smartCaret;
    BooleanFieldEditor pasteCorrectIndent;
    BooleanFieldEditor pasteImports;
    BooleanFieldEditor pasteEscapeQuoted;
    BooleanFieldEditor normalizeWs;
    BooleanFieldEditor normalizeNl;
    BooleanFieldEditor stripTrailingWs;
    BooleanFieldEditor cleanImports;
    BooleanFieldEditor format;
    BooleanFieldEditor autoFoldImports;
    BooleanFieldEditor autoFoldComments;
    BooleanFieldEditor closeParens;
    BooleanFieldEditor closeBrackets;
    BooleanFieldEditor closeBraces;
    BooleanFieldEditor closeAngles;
    BooleanFieldEditor closeBackticks;
    BooleanFieldEditor closeQuotes;
    BoolFieldEditor enableFolding;
    
    public CeylonEditorPreferencePage() {
        super(GRID);
        setDescription("Preferences related to the editor for Ceylon source files.");
    }
    
    @Override
    public boolean performOk() {
        bracketMatching.store();
        IPreferenceStore store = 
                EditorsUI.getPreferenceStore();
        store.setValue(SELECTED_BRACKET, 
                matchingBrackets.getSelection());
        store.setValue(ENCLOSING_BRACKETS, 
                enclosingBrackets.getSelection());
        smartCaret.store();
        pasteCorrectIndent.store();
        pasteEscapeQuoted.store();
        pasteImports.store();
        autoFoldImports.store();
        autoFoldComments.store();
        closeAngles.store();
        closeBackticks.store();
        closeBraces.store();
        closeBrackets.store();
        closeParens.store();
        closeQuotes.store();
        enableFolding.store();
        return true;
    }
    
    @Override
    protected void performDefaults() {
        super.performDefaults();
        bracketMatching.loadDefault();
        IPreferenceStore store = 
                EditorsUI.getPreferenceStore();
        matchingBrackets.setSelection(
                store.getDefaultBoolean(SELECTED_BRACKET));
        enclosingBrackets.setSelection(
                store.getDefaultBoolean(ENCLOSING_BRACKETS));
        oppositeBracket.setSelection(false);
        smartCaret.loadDefault();
        pasteCorrectIndent.loadDefault();
        pasteEscapeQuoted.loadDefault();
        pasteImports.loadDefault();
        autoFoldImports.loadDefault();
        autoFoldComments.loadDefault();
        closeAngles.loadDefault();
        closeBackticks.loadDefault();
        closeBraces.loadDefault();
        closeBrackets.loadDefault();
        closeParens.loadDefault();
        closeQuotes.loadDefault();
        enableFolding.store();
    }
    
    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(CeylonPlugin.getPreferences());
        CeylonEditor.initializeBrackMatcherPreferences();
    }
    
    
    @Override
    protected Control createContents(Composite parent) {
        
        Link textEditorsLink = new Link(parent, 0);
        textEditorsLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        textEditorsLink.setText("See '<a>Text Editors</a>' for general editor preferences.");
        textEditorsLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        TEXTEDITOR_PREFERENCE_PAGE_ID, 
                        null, null);
            }
        });
        
        Link colorsAndFontsLink = new Link(parent, 0);
        colorsAndFontsLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        colorsAndFontsLink.setText("See '<a>Colors and Fonts</a>' to customize appearance and syntax highlighting.");
        colorsAndFontsLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        COLORS_AND_FONTS_PAGE_ID, null, 
                        "selectFont:" 
                                + EDITOR_FONT_PREFERENCE);
            }
        });
        
        Link annotationsLink = new Link(parent, 0);
        annotationsLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        annotationsLink.setText("See '<a>Annotations</a>' to customize annotation appearance.");
        annotationsLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        ANNOTATION_PREFERENCE_PAGE_ID, 
                        null, null);
            }
        });
        
        Composite composite = 
                new Composite(parent, SWT.NONE);
        composite.setLayoutData(
                GridDataFactory.fillDefaults()
                    .grab(true, false)
                    .create());
        composite.setLayout(new GridLayout(1, true));
        
        Control contents = super.createContents(composite);
        
        Link completionLink = new Link(parent, 0);
        completionLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        completionLink.setText("See '<a>Completion</a>' for preferences related to completion.");
        completionLink.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                createPreferenceDialogOn(getShell(), 
                        CeylonCompletionPreferencePage.ID, 
                        null, null);
            }
        });
        
        Link refactoringLink = new Link(parent, 0);
        refactoringLink.setLayoutData(
                GridDataFactory.swtDefaults()
                    .align(SWT.FILL, SWT.CENTER)
                    .create());
        refactoringLink.setText("See '<a>Refactoring</a>' for preferences related to refactoring.");
        refactoringLink.addSelectionListener(
                new SelectionAdapter() {
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
        
        return contents;
    }

    @Override
    protected void createFieldEditors() {
        otherSection();
        autocloseSection();
        bracketHighlightingSection();
        foldingSection();
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
    
    interface Listener {
        void valueChanged(boolean oldValue, boolean newValue);
    }
    
    static class BoolFieldEditor extends BooleanFieldEditor {
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
    
    static class SpecialBoolFieldEditor extends BoolFieldEditor {
        public SpecialBoolFieldEditor(String name, 
                String label, Composite parent) {
            super(name, label, parent);
        }
        @Override
        public IPreferenceStore getPreferenceStore() {
            return EditorsUI.getPreferenceStore();
        }
    }
    
    private void bracketHighlightingSection() {
        Group group = createGroup(1, "Bracket highlighting");
        Composite p = getFieldEditorParent(group);
        GridData gd = new GridData();
        gd.horizontalSpan=1;
        p.setLayoutData(gd);
        bracketMatching = 
                new SpecialBoolFieldEditor(MATCHING_BRACKET, 
                        "Enable matching bracket highlighting", 
                        p);
        bracketMatching.load();
        addField(bracketMatching);
        
        Composite composite = new Composite(group, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
        composite.setLayout(layout);
        GridData gd2 = 
                new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd2.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd2);
        
        oppositeBracket = new Button(composite, SWT.RADIO);
        oppositeBracket.setText("Matching bracket only");
        oppositeBracket.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = oppositeBracket.getSelection();
                matchingBrackets.setSelection(!selected);
                enclosingBrackets.setSelection(!selected);
            }
        });
        matchingBrackets = new Button(composite, SWT.RADIO);
        matchingBrackets.setText("Matching bracket and selected bracket");
        matchingBrackets.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = 
                        matchingBrackets.getSelection();
                oppositeBracket.setSelection(!selected);
                enclosingBrackets.setSelection(!selected);
            }
        });
        enclosingBrackets = new Button(composite, SWT.RADIO);
        enclosingBrackets.setText("Enclosing brackets");
        enclosingBrackets.addSelectionListener(
                new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = 
                        enclosingBrackets.getSelection();
                matchingBrackets.setSelection(!selected);
                oppositeBracket.setSelection(!selected);
            }
        });
        
        IPreferenceStore store = 
                EditorsUI.getPreferenceStore();
        matchingBrackets.setSelection(
                store.getBoolean(SELECTED_BRACKET) &&
                !store.getBoolean(ENCLOSING_BRACKETS));
        enclosingBrackets.setSelection(
                store.getBoolean(ENCLOSING_BRACKETS));
        oppositeBracket.setSelection(
                !store.getBoolean(SELECTED_BRACKET) && 
                !store.getBoolean(ENCLOSING_BRACKETS));
        
        boolean enabled = 
                EditorsUI.getPreferenceStore()
                    .getBoolean(MATCHING_BRACKET);        
        oppositeBracket.setEnabled(enabled);
        matchingBrackets.setEnabled(enabled);
        enclosingBrackets.setEnabled(enabled);
        bracketMatching.setListener(new Listener() {
            @Override
            public void valueChanged(boolean oldValue, boolean newValue) {
                oppositeBracket.setEnabled(newValue);
                matchingBrackets.setEnabled(newValue);
                enclosingBrackets.setEnabled(newValue);
            }
        });
    }

    private void foldingSection() {
        final Composite group = 
                createGroup(2, "Source folding");
        Composite p0 = getFieldEditorParent(group);
        GridData gd = new GridData();
        gd.horizontalSpan=2;
        p0.setLayoutData(gd);
        enableFolding = 
                new SpecialBoolFieldEditor(EDITOR_FOLDING_ENABLED, 
                        "Enable source folding", p0);
        enableFolding.load();
        addField(enableFolding);
        
        final Composite composite = 
                new Composite(group, SWT.NONE);
        GridLayout layout = new GridLayout(1, true);
        composite.setLayout(layout);
        GridData gd2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd2.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd2);
        
        final Composite p1 = 
                getFieldEditorParent(composite);
        autoFoldImports = 
                new BooleanFieldEditor(AUTO_FOLD_IMPORTS, 
                        "Automatically fold import lists", 
                        p1);
        autoFoldImports.load();
        addField(autoFoldImports);
        final Composite p2 = 
                getFieldEditorParent(composite);
        autoFoldComments = 
                new BooleanFieldEditor(AUTO_FOLD_COMMENTS, 
                        "Automatically fold comments", 
                        p2);
        autoFoldComments.load();
        addField(autoFoldComments);
        
        boolean enabled = 
                EditorsUI.getPreferenceStore()
                    .getBoolean(EDITOR_FOLDING_ENABLED);
        autoFoldImports.setEnabled(enabled, p1);
        autoFoldComments.setEnabled(enabled, p2);
//        composite.setVisible(enabled);
//        composite.setEnabled(enabled);
//        ((GridData) composite.getLayoutData()).exclude = !enabled;
        enableFolding.setListener(new Listener() {
            @Override
            public void valueChanged(boolean oldValue, boolean newValue) {
                autoFoldImports.setEnabled(newValue, p1);
                autoFoldComments.setEnabled(newValue, p2);
//                composite.setVisible(newValue);
//                composite.setEnabled(newValue);
//                ((GridData) composite.getLayoutData()).exclude = !newValue;
//                group.layout();
            }
        });
    }

    private void otherSection() {
        Composite group = createGroup(1, "General");
        smartCaret = 
                new BooleanFieldEditor(SUB_WORD_NAVIGATION, 
                    "Smart caret positioning inside identifiers", 
                    getFieldEditorParent(group));
        smartCaret.load();
        addField(smartCaret);
        pasteCorrectIndent = 
                new BooleanFieldEditor(PASTE_CORRECT_INDENTATION, 
                    "Correct indentation of pasted code", 
                    getFieldEditorParent(group));
        pasteCorrectIndent.load();
        addField(pasteCorrectIndent);
        pasteImports = 
                new BooleanFieldEditor(PASTE_IMPORTS,
                    "Automatically add missing imports when pasting code", 
                    getFieldEditorParent(group));
        pasteImports.load();
        addField(pasteImports);
        pasteEscapeQuoted = 
                new BooleanFieldEditor(PASTE_ESCAPE_QUOTED, 
                    "Escape text pasted into quoted strings", 
                    getFieldEditorParent(group));
        pasteEscapeQuoted.load();
        addField(pasteEscapeQuoted);
    }
    
    protected Composite getFieldEditorParent(Composite group) {
        Composite parent = new Composite(group, SWT.NULL);
        parent.setLayoutData(GridDataFactory.fillDefaults().create());
        return parent;
    }

    private void autocloseSection() {
        Composite group = createGroup(3, "Automatically close");
        closeParens = 
                new BooleanFieldEditor(CLOSE_PARENS, 
                    "Parentheses", 
                    getFieldEditorParent(group));
        closeParens.load();
        addField(closeParens);
        closeBrackets = 
                new BooleanFieldEditor(CLOSE_BRACKETS, 
                    "Brackets", 
                    getFieldEditorParent(group));
        closeBrackets.load();
        addField(closeBrackets);
        closeAngles = 
                new BooleanFieldEditor(CLOSE_ANGLES, 
                    "Angle brackets", 
                    getFieldEditorParent(group));
        closeAngles.load();
        addField(closeAngles);
        closeBackticks = 
                new BooleanFieldEditor(CLOSE_BACKTICKS, 
                    "Backticks", 
                    getFieldEditorParent(group));
        closeBackticks.load();
        addField(closeBackticks);
        closeBraces = 
                new BooleanFieldEditor(CLOSE_BRACES, 
                    "Braces", 
                    getFieldEditorParent(group));
        closeBraces.load();
        addField(closeBraces);
        closeQuotes = 
                new BooleanFieldEditor(CLOSE_QUOTES, 
                    "Quotes", 
                    getFieldEditorParent(group));
        closeQuotes.load();
        addField(closeQuotes);
    }
    
    private IPropertyChangeListener listener;
    
    @Override
    public void dispose() {
        super.dispose();
        if (listener!=null) {
            CeylonPlugin.getPreferences()
                .removePropertyChangeListener(listener);
        }
    }

}
