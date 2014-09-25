package com.redhat.ceylon.eclipse.code.style;

import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.FORMATTER_LINE_SPLIT;
import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.FORMATTER_TAB_SIZE;
import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.FORMATTER_indentMode;
import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.FORMATTER_indentMode_Mixed;
import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.FORMATTER_indentMode_Spaces;
import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.FORMATTER_indentMode_Spaces_Size;
import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.FORMATTER_indentMode_Tabs;
import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.FORMATTER_indentMode_Tabs_Size;
import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.FORMATTER_indent_Blank_Lines;
import ceylon.formatter.options.FormattingOptions;
import ceylon.formatter.options.IndentMode;
import ceylon.formatter.options.Mixed;
import ceylon.formatter.options.Spaces;
import ceylon.formatter.options.SparseFormattingOptions;
import ceylon.formatter.options.Tabs;
import ceylon.formatter.options.VariableOptions;
import ceylon.formatter.options.combinedOptions_;
import ceylon.language.Singleton;

/**
 * Wrapper around VariableOptions
 *
 */
public class FormatterPreferences {
    private VariableOptions options;

    public FormatterPreferences(FormattingOptions options) {
        this.options = new VariableOptions(options);
    }

    public String get(String key) {
        String ret = null;
        switch (key) {
        case FORMATTER_indentMode:
            ret = options.getIndentMode().getClass().getSimpleName()
                    .toLowerCase();
            break;
        case FORMATTER_indentMode_Spaces_Size:
        case FORMATTER_indentMode_Tabs_Size:
            ret = new Long(options.getIndentMode().getWidthOfLevel())
                    .toString();
            break;
        case FORMATTER_indent_Blank_Lines:
            ret = options.getIndentBlankLines().booleanValue() ? "true"
                    : "false";
            break;

        // to set up previewer only
        case FORMATTER_LINE_SPLIT:
            ret = "80";
            break;
        case FORMATTER_TAB_SIZE:
            ret = "4";
            break;
        default:
            break;
        }
        return ret;
    }

    public void put(String key, String value) {
        switch (key) {
        case FORMATTER_indentMode:
            IndentMode indentMode = getIndentMode(value, new Long(options
                    .getIndentMode().getWidthOfLevel()).intValue());
            options.setIndentMode(indentMode);
            break;
        case FORMATTER_indentMode_Spaces_Size:
        case FORMATTER_indentMode_Tabs_Size:
            String mode = options.getIndentMode().getClass().getSimpleName()
                    .toLowerCase();
            indentMode = getIndentMode(mode, Integer.parseInt(value));
            options.setIndentMode(indentMode);
            break;
        case FORMATTER_indent_Blank_Lines:
            options.setIndentBlankLines(Boolean.parseBoolean(value) ? new ceylon.language.true_()
                    : new ceylon.language.false_());
            break;
        default:
            break;
        }
    }

    private IndentMode getIndentMode(String mode, int n1) {
        if (FORMATTER_indentMode_Spaces.equalsIgnoreCase(mode)) {
            return new Spaces(n1);
        } else if (FORMATTER_indentMode_Tabs.equalsIgnoreCase(mode)) {
            return new Tabs(n1);
        } else if (FORMATTER_indentMode_Mixed.equalsIgnoreCase(mode)) {
            return new Mixed(new Tabs(n1), new Spaces(n1));
        }
        return options.getIndentMode();
    }

    public FormattingOptions getOptions() {
        return combinedOptions_.combinedOptions(new FormattingOptions(),
                new Singleton<SparseFormattingOptions>(
                        SparseFormattingOptions.$TypeDescriptor$, options));
    }
}
