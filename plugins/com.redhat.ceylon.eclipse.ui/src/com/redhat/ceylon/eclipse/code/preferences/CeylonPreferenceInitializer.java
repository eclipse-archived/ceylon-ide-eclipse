package com.redhat.ceylon.eclipse.code.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonPreferenceInitializer extends AbstractPreferenceInitializer {

    public static final String AUTO_INSERT = "autoInsert";
    public static final String AUTO_ACTIVATION = "autoActivation";
    public static final String AUTO_ACTIVATION_CHARS = "autoActivationChars";
    public static final String AUTO_ACTIVATION_DELAY = "autoActivationDelay";
    public static final String COMPLETION = "completion";
    public static final String COMPLETION_FILTERS = "completionFilters";
    public static final String INACTIVE_COMPLETION_FILTERS = "inactiveCompletionFilters";
    public static final String INEXACT_MATCHES = "inexactMatches";
    public static final String LINKED_MODE = "linkedModeCompletion";
    public static final String LINKED_MODE_RENAME = "linkedModeRename";
    public static final String LINKED_MODE_RENAME_SELECT = "linkedModeRenameSelect";
    public static final String LINKED_MODE_EXTRACT = "linkedModeExtract";
    public static final String PASTE_CORRECT_INDENTATION = "pasteCorrectIndentation";
    public static final String DISPLAY_RETURN_TYPES = "displayReturnTypes";
    public static final String DISPLAY_PARAMETER_TYPES = "displayParameterTypes";
    public static final String CLOSE_PARENS = "closeParens";
    public static final String CLOSE_BRACKETS = "closeBrackets";
    public static final String CLOSE_ANGLES = "closeAngles";
    public static final String CLOSE_BACKTICKS = "closeBackticks";
    public static final String CLOSE_BRACES = "closeBraces";
    public static final String CLOSE_QUOTES = "closeQuotes";
    public static final String NORMALIZE_WS = "normalizedWs";
    public static final String NORMALIZE_NL = "normalizedNl";
    public static final String STRIP_TRAILING_WS = "stripTrailingWs";
    public static final String CLEAN_IMPORTS = "cleanImports";
    public static final String FORMAT = "format";
    //preference keys
    public final static String MATCHING_BRACKET = "matchingBrackets";
    public final static String MATCHING_BRACKETS_COLOR = "matchingBracketsColor";
    public final static String SELECTED_BRACKET = "highlightBracketAtCaretLocation";
    public final static String ENCLOSING_BRACKETS = "enclosingBrackets";
    public final static String SUB_WORD_NAVIGATION = "subWordNavigation";
    public final static String AUTO_FOLD_IMPORTS = "autoFoldImports";
    public final static String AUTO_FOLD_COMMENTS = "autoFoldComments";

    public CeylonPreferenceInitializer() {}

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = EditorUtil.getPreferences();
        store.setDefault(AUTO_INSERT, true);
        store.setDefault(AUTO_ACTIVATION, true);
        store.setDefault(AUTO_ACTIVATION_DELAY, 500);
        store.setDefault(AUTO_ACTIVATION_CHARS, ".");
        store.setDefault(COMPLETION, "insert");
        store.setDefault(INEXACT_MATCHES, "positional");
        store.setDefault(LINKED_MODE, true);
        store.setDefault(LINKED_MODE_RENAME, true);
        store.setDefault(LINKED_MODE_RENAME_SELECT, true);
        store.setDefault(LINKED_MODE_EXTRACT, true);
        store.setDefault(PASTE_CORRECT_INDENTATION, true);
        store.setDefault(DISPLAY_RETURN_TYPES, false);
        store.setDefault(DISPLAY_PARAMETER_TYPES, true);
        store.setDefault(NORMALIZE_WS, false);
        store.setDefault(NORMALIZE_NL, false);
        store.setDefault(STRIP_TRAILING_WS, false);
        store.setDefault(CLEAN_IMPORTS, false);
        store.setDefault(FORMAT, false);
        store.setDefault(CLOSE_PARENS, true);
        store.setDefault(CLOSE_BRACKETS, true);
        store.setDefault(CLOSE_ANGLES, true);
        store.setDefault(CLOSE_BRACES, true);
        store.setDefault(CLOSE_QUOTES, true);
        store.setDefault(CLOSE_BACKTICKS, true);
        store.setDefault(AUTO_FOLD_IMPORTS, true);
        store.setDefault(AUTO_FOLD_COMMENTS, false);
        store.setDefault(SUB_WORD_NAVIGATION, true);
        store.setDefault(MATCHING_BRACKET, true);
        store.setDefault(MATCHING_BRACKET, true);
        store.setDefault(ENCLOSING_BRACKETS, false);
        store.setDefault(SELECTED_BRACKET, false);
   }
}
