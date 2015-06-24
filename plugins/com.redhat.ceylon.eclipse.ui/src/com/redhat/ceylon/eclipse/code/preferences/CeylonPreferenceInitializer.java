package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.common.Constants.DEFAULT_RESOURCE_DIR;
import static com.redhat.ceylon.common.Constants.DEFAULT_SOURCE_DIR;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.redhat.ceylon.eclipse.util.EditorUtil;

public class CeylonPreferenceInitializer extends AbstractPreferenceInitializer {

    public static final String AUTO_INSERT = "autoInsert";
    public static final String AUTO_INSERT_PREFIX = "autoInsertPrefix";
    public static final String AUTO_ACTIVATION = "autoActivation";
    public static final String AUTO_ACTIVATION_CHARS = "autoActivationChars";
    public static final String AUTO_ACTIVATION_DELAY = "autoActivationDelay";
    public static final String COMPLETION = "completion";
    public static final String COMPLETION_FILTERS = "completionFilters";
    public static final String INACTIVE_COMPLETION_FILTERS = "inactiveCompletionFilters";
    public static final String OPEN_FILTERS = "openFilters";
    public static final String INACTIVE_OPEN_FILTERS = "inactiveOpenFilters";
    public static final String INEXACT_MATCHES = "inexactMatches";
    public static final String LINKED_MODE_ARGUMENTS = "linkedModeCompletion";
    public static final String CHAIN_LINKED_MODE_ARGUMENTS = "linkedModeChainCompletion";
    public static final String LINKED_MODE_RENAME = "linkedModeRename";
    public static final String LINKED_MODE_RENAME_SELECT = "linkedModeRenameSelect";
    public static final String LINKED_MODE_EXTRACT = "linkedModeExtract";
    public static final String PASTE_CORRECT_INDENTATION = "pasteCorrectIndentation";
    public static final String PASTE_ESCAPE_QUOTED = "pasteEscapeQuoted";
    public static final String PASTE_IMPORTS = "pasteImports";
    public static final String RETURN_TYPES_IN_OUTLINES = "displayReturnTypes";
    public static final String PARAMS_IN_OUTLINES = "displayParameters";
    public static final String PARAM_TYPES_IN_OUTLINES = "displayParameterTypes";
    public static final String TYPE_PARAMS_IN_OUTLINES = "displayTypeParameters";
    public static final String PARAMETER_TYPES_IN_COMPLETIONS = "displayParameterTypes";
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
    public static final String SUB_WORD_NAVIGATION = "subWordNavigation";
    public static final String AUTO_FOLD_IMPORTS = "autoFoldImports";
    public static final String AUTO_FOLD_COMMENTS = "autoFoldComments";
    public static final String DEFAULT_PROJECT_TYPE = "defaultProjectType";
    public static final String DEFAULT_SOURCE_FOLDER = "defaultSourceFolder";
    public static final String DEFAULT_RESOURCE_FOLDER = "defaultResourceFolder";
    public static final String PARAMS_IN_DIALOGS = "paramsInDialogs";
    public static final String PARAM_TYPES_IN_DIALOGS = "paramTypesInDialogs";
    public static final String TYPE_PARAMS_IN_DIALOGS = "typeParamsInDialogs";
    public static final String RETURN_TYPES_IN_DIALOGS = "typesInDialogs";
    public static final String FULL_LOC_SEARCH_RESULTS = "fullLocationInSearchResults";
    public static final String MATCH_HIGHLIGHTING = "matchHighlighting";

    public CeylonPreferenceInitializer() {}

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = EditorUtil.getPreferences();
        store.setDefault(AUTO_INSERT, true);
        store.setDefault(AUTO_INSERT_PREFIX, false);
        store.setDefault(AUTO_ACTIVATION, true);
        store.setDefault(AUTO_ACTIVATION_DELAY, 500);
        store.setDefault(AUTO_ACTIVATION_CHARS, ".");
        store.setDefault(COMPLETION, "insert");
        store.setDefault(INEXACT_MATCHES, "positional");
        store.setDefault(LINKED_MODE_ARGUMENTS, true);
        store.setDefault(CHAIN_LINKED_MODE_ARGUMENTS, false);
        store.setDefault(LINKED_MODE_RENAME, true);
        store.setDefault(LINKED_MODE_RENAME_SELECT, true);
        store.setDefault(LINKED_MODE_EXTRACT, true);
        store.setDefault(PASTE_CORRECT_INDENTATION, true);
        store.setDefault(PASTE_ESCAPE_QUOTED, true);
        store.setDefault(PASTE_IMPORTS, true);
        store.setDefault(RETURN_TYPES_IN_OUTLINES, false);
        store.setDefault(TYPE_PARAMS_IN_OUTLINES, true);
        store.setDefault(PARAMS_IN_OUTLINES, false);
        store.setDefault(PARAM_TYPES_IN_OUTLINES, true);
        store.setDefault(PARAMETER_TYPES_IN_COMPLETIONS, true);
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
        store.setDefault(DEFAULT_SOURCE_FOLDER, DEFAULT_SOURCE_DIR);
        store.setDefault(DEFAULT_RESOURCE_FOLDER, DEFAULT_RESOURCE_DIR);
        store.setDefault(DEFAULT_PROJECT_TYPE, "jvm");
        store.setDefault(TYPE_PARAMS_IN_DIALOGS, true);
        store.setDefault(PARAMS_IN_DIALOGS, false);
        store.setDefault(PARAM_TYPES_IN_DIALOGS, true);
        store.setDefault(RETURN_TYPES_IN_DIALOGS, false);
        store.setDefault(FULL_LOC_SEARCH_RESULTS, true);
        store.setDefault(MATCH_HIGHLIGHTING, "bold");
   }
}
