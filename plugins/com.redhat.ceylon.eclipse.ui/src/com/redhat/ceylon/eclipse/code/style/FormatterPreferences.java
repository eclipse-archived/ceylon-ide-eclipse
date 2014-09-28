package com.redhat.ceylon.eclipse.code.style;

import static com.redhat.ceylon.eclipse.code.style.CeylonFormatterConstants.*;
import ceylon.formatter.options.FormattingOptions;
import ceylon.formatter.options.IndentMode;
import ceylon.formatter.options.Mixed;
import ceylon.formatter.options.Spaces;
import ceylon.formatter.options.SparseFormattingOptions;
import ceylon.formatter.options.Tabs;
import ceylon.formatter.options.VariableOptions;
import ceylon.formatter.options.addIndentBefore_;
import ceylon.formatter.options.combinedOptions_;
import ceylon.formatter.options.stack_;
import ceylon.language.Singleton;

/**
 * Wrapper around VariableOptions
 *
 */
public class FormatterPreferences {
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private VariableOptions options;
    private String space_AfterParamListClosingParen_Number;

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
            ret = booleanString(options.getIndentBlankLines());
            break;
        case FORMATTER_indent_Before_Type_Info:
            ret = options.getIndentBeforeTypeInfo().toString();
            break;
        case FORMATTER_indent_After_Specifier_Expression_Start:
            String iases = options.getIndentationAfterSpecifierExpressionStart()
                    .getClass().getSimpleName().toLowerCase();
            ret = iases.substring(0,iases.length() -1);
            break;
        case FORMATTER_space_BeforeMethodOrClassPositionalArgumentList:
            ret = booleanString(options.getSpaceBeforeMethodOrClassPositionalArgumentList());
            break;            
        case FORMATTER_space_BeforeResourceList:
            ret = booleanString(options.getSpaceBeforeResourceList());
            break;
        case FORMATTER_space_BeforeCatchVariable:
            ret = booleanString(options.getSpaceBeforeCatchVariable());
            break;
        case FORMATTER_space_AroundSatisfiesOf:
            ret = booleanString(options.getSpaceAroundSatisfiesOf());
            break;
        case FORMATTER_space_AroundImportAliasEqualsSign:
            ret = booleanString(options.getSpaceAroundImportAliasEqualsSign());
            break;
        case FORMATTER_space_AfterTypeArgOrParamListComma:
            ret = booleanString(options.getSpaceAfterTypeArgOrParamListComma());
            break;
        case FORMATTER_space_BeforeIfOpeningParenthesis:
            ret = booleanString(options.getSpaceBeforeIfOpeningParenthesis());
            break;
        case FORMATTER_space_BeforeSequenceEnumerationClosingBrace:
            ret = booleanString(options.getSpaceBeforeSequenceEnumerationClosingBrace());
            break;
        case FORMATTER_space_BeforeParamListOpeningParen:
            ret = booleanString(options.getSpaceBeforeParamListOpeningParen());
            break;
        case FORMATTER_space_BeforeParamListClosingParen:
            ret = booleanString(options.getSpaceBeforeParamListClosingParen());
            break;
        case FORMATTER_space_AfterParamListClosingParen:
            if (!(options.getSpaceAfterParamListClosingParen() instanceof ceylon.language.Integer)) {
                ret = booleanString((ceylon.language.Boolean) options.getSpaceAfterParamListClosingParen());
            } else {
                ret = TRUE;
            }
            break;
        case FORMATTER_space_AfterParamListClosingParen_Number: // if queried, only if number
            if (options.getSpaceAfterParamListClosingParen() instanceof ceylon.language.Integer) {
                ret = ((ceylon.language.Integer)options.getSpaceAfterParamListClosingParen()).toString();
                this.space_AfterParamListClosingParen_Number = ret; // save the value in case user enables again
            }
            if (this.space_AfterParamListClosingParen_Number != null) {
                ret = this.space_AfterParamListClosingParen_Number;
            } else {
                ret = "0";
            }
            break;
        case FORMATTER_space_BeforeValueIteratorClosingParenthesis:
            ret = booleanString(options.getSpaceBeforeValueIteratorClosingParenthesis());
            break;
        case FORMATTER_space_AfterSequenceEnumerationOpeningBrace:
            ret = booleanString(options.getSpaceAfterSequenceEnumerationOpeningBrace());
            break;
        case FORMATTER_space_BeforeForOpeningParenthesis:
            ret = booleanString(options.getSpaceBeforeForOpeningParenthesis());
            break;
        case FORMATTER_space_BeforeWhileOpeningParenthesis:
            ret = booleanString(options.getSpaceBeforeWhileOpeningParenthesis());
            break;
        case FORMATTER_space_BeforeAnnotationPositionalArgumentList:
            ret = booleanString(options.getSpaceBeforeAnnotationPositionalArgumentList());
            break;
        case FORMATTER_space_AfterValueIteratorOpeningParenthesis:
            ret = booleanString(options.getSpaceAfterValueIteratorOpeningParenthesis());
            break;
        case FORMATTER_space_AfterParamListOpeningParen:
            ret = booleanString(options.getSpaceAfterParamListOpeningParen());
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

    private String booleanString(ceylon.language.Boolean b) {
        return b.booleanValue() ? TRUE : FALSE;
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
            options.setIndentBlankLines(ceylonBoolean(value));
            break;
        case FORMATTER_indent_Before_Type_Info:
            options.setIndentBeforeTypeInfo(new ceylon.language.Integer(new Long(value)));
            break;
        case FORMATTER_indent_After_Specifier_Expression_Start:
            if ("stack".equals(value)) {
                options.setIndentationAfterSpecifierExpressionStart(stack_.get_());
            } else { // lower, not camel case
                options.setIndentationAfterSpecifierExpressionStart(addIndentBefore_.get_());
            }
            break;
        case FORMATTER_space_BeforeMethodOrClassPositionalArgumentList:
            options.setSpaceBeforeMethodOrClassPositionalArgumentList(
                ceylonBoolean(value));
            break;
        case FORMATTER_space_BeforeResourceList:
            options.setSpaceBeforeResourceList(ceylonBoolean(value));
            break;
        case FORMATTER_space_BeforeCatchVariable:
            options.setSpaceBeforeCatchVariable( ceylonBoolean(value));
            break;
        case FORMATTER_space_AroundSatisfiesOf:
            options.setSpaceAroundSatisfiesOf(ceylonBoolean(value));
            break;
        case FORMATTER_space_AroundImportAliasEqualsSign:
            options.setSpaceAroundImportAliasEqualsSign(ceylonBoolean(value));
            break;
        case FORMATTER_space_AfterTypeArgOrParamListComma:
            options.setSpaceAfterTypeArgOrParamListComma(ceylonBoolean(value));
            break;
        case FORMATTER_space_BeforeIfOpeningParenthesis:
            options.setSpaceBeforeIfOpeningParenthesis(ceylonBoolean(value));
            break;
        case FORMATTER_space_BeforeSequenceEnumerationClosingBrace:
            options.setSpaceBeforeSequenceEnumerationClosingBrace(ceylonBoolean(value));
            break;
        case FORMATTER_space_BeforeParamListOpeningParen:
            options.setSpaceBeforeParamListOpeningParen(ceylonBoolean(value));
            break;
        case FORMATTER_space_BeforeParamListClosingParen:
            options.setSpaceBeforeParamListClosingParen(ceylonBoolean(value));
            break;
        case FORMATTER_space_AfterParamListClosingParen:
            options.setSpaceAfterParamListClosingParen(ceylonBoolean(value));
            break;
        case FORMATTER_space_AfterParamListClosingParen_Number:
            int num = Integer.parseInt(value);
            if (num != 0) {
                options.setSpaceAfterParamListClosingParen(num);
            }
            break;
        case FORMATTER_space_BeforeValueIteratorClosingParenthesis:
            options.setSpaceBeforeValueIteratorClosingParenthesis(ceylonBoolean(value));
            break;
        case FORMATTER_space_AfterSequenceEnumerationOpeningBrace:
            options.setSpaceAfterSequenceEnumerationOpeningBrace(ceylonBoolean(value));
            break;
        case FORMATTER_space_BeforeForOpeningParenthesis:
            options.setSpaceBeforeForOpeningParenthesis(ceylonBoolean(value));
            break;
        case FORMATTER_space_BeforeWhileOpeningParenthesis:
            options.setSpaceBeforeWhileOpeningParenthesis(ceylonBoolean(value));
            break;
        case FORMATTER_space_BeforeAnnotationPositionalArgumentList:
            options.setSpaceBeforeAnnotationPositionalArgumentList(ceylonBoolean(value));
            break;
        case FORMATTER_space_AfterValueIteratorOpeningParenthesis:
            options.setSpaceAfterValueIteratorOpeningParenthesis(ceylonBoolean(value));
            break;
        case FORMATTER_space_AfterParamListOpeningParen:
            options.setSpaceAfterParamListOpeningParen(ceylonBoolean(value));
            break;            
        default:
            break;
        }
    }

    private ceylon.language.Boolean ceylonBoolean(String value) {
        return Boolean.parseBoolean(value)? 
                new ceylon.language.true_() : new ceylon.language.false_();
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
