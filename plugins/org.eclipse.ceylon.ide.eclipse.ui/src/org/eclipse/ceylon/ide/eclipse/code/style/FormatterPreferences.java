/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.style;

import static org.eclipse.ceylon.ide.eclipse.code.style.CeylonFormatterConstants.*;

import ceylon.formatter.options.FormattingOptions;
import ceylon.formatter.options.IndentMode;
import ceylon.formatter.options.Mixed;
import ceylon.formatter.options.Spaces;
import ceylon.formatter.options.SparseFormattingOptions;
import ceylon.formatter.options.Tabs;
import ceylon.formatter.options.VariableOptions;
import ceylon.formatter.options.all_;
import ceylon.formatter.options.combinedOptions_;
import ceylon.formatter.options.crlf_;
import ceylon.formatter.options.lf_;
import ceylon.formatter.options.os_;
import ceylon.formatter.options.unlimited_;
import ceylon.language.Range;
import ceylon.language.Singleton;
import ceylon.language.span_;

/**
 * Wrapper around VariableOptions
 *
 */
public class FormatterPreferences {
    private static final String FALSE = "false";
    private static final String TRUE = "true";
    private VariableOptions options;
    private SparseFormattingOptions ideOptions;

    public FormatterPreferences(FormattingOptions options) {
        this.options = new VariableOptions(options);
        this.ideOptions = CeylonStyle.getEclipseWsOptions(null);
    }

    public String get(String key) {
        String ret = null;
        switch (key) {
        case FORMATTER_indentMode:
            ret = ideOptions.getIndentMode().getClass().getSimpleName()
                    .toLowerCase();
            break;
        case FORMATTER_indentMode_Spaces_Size:
        case FORMATTER_indentMode_Tabs_Size:
            ret = new Long(ideOptions.getIndentMode().getWidthOfLevel())
                    .toString();
            break;
        case FORMATTER_indent_Blank_Lines:
            ret = booleanString(options.getIndentBlankLines());
            break;
        case FORMATTER_indent_Before_Type_Info:
            ret = options.getIndentBeforeTypeInfo().toString();
            break;
        case FORMATTER_space_BeforeMethodOrClassPositionalArgumentList:
            ret = booleanString(options
                    .getSpaceBeforeMethodOrClassPositionalArgumentList());
            break;
        case FORMATTER_space_AroundSatisfiesOf:
            ret = booleanString(options.getSpaceAroundSatisfiesOf());
            break;
        case FORMATTER_space_AroundImportAliasEqualsSign:
            ret = booleanString(options.getSpaceAroundImportAliasEqualsSign());
            break;
        case FORMATTER_space_AfterTypeParamListComma:
            ret = booleanString(options.getSpaceAfterTypeParamListComma());
            break;
        case FORMATTER_space_AfterTypeArgListComma:
            ret = booleanString(options.getSpaceAfterTypeArgListComma());
            break;
        case FORMATTER_space_AroundTypeParamListEqualsSign:
            ret = booleanString(options.getSpaceAroundTypeParamListEqualsSign());
            break;
        case FORMATTER_space_BeforeSequenceEnumerationClosingBrace:
            ret = booleanString(options
                    .getSpaceBeforeSequenceEnumerationClosingBrace());
            break;
        case FORMATTER_space_BeforeParamListOpeningParen:
            ret = booleanString(options.getSpaceBeforeParamListOpeningParen());
            break;
        case FORMATTER_space_BeforeParamListClosingParen:
            ret = booleanString(options.getSpaceBeforeParamListClosingParen());
            break;
        case FORMATTER_space_AfterParamListClosingParen:
            ret = booleanString(options.getSpaceAfterParamListClosingParen());
            break;
        case FORMATTER_space_BeforeValueIteratorClosingParenthesis:
            ret = booleanString(options
                    .getSpaceBeforeValueIteratorClosingParenthesis());
            break;
        case FORMATTER_space_AfterSequenceEnumerationOpeningBrace:
            ret = booleanString(options
                    .getSpaceAfterSequenceEnumerationOpeningBrace());
            break;
        case FORMATTER_space_BeforeAnnotationPositionalArgumentList:
            ret = booleanString(options
                    .getSpaceBeforeAnnotationPositionalArgumentList());
            break;
        case FORMATTER_space_AfterValueIteratorOpeningParenthesis:
            ret = booleanString(options
                    .getSpaceAfterValueIteratorOpeningParenthesis());
            break;
        case FORMATTER_space_AfterParamListOpeningParen:
            ret = booleanString(options.getSpaceAfterParamListOpeningParen());
            break;
        case FORMATTER_space_AfterControlStructureKeyword:
            ret = booleanString(options.getSpaceAfterControlStructureKeyword());
            break;
        case FORMATTER_space_OptionalAroundOperatorLevel:
            ret = options.getSpaceOptionalAroundOperatorLevel().toString();
            break;
        
        case FORMATTER_maxLineLength:
            if ((options.getMaxLineLength() instanceof ceylon.formatter.options.Unlimited)) {
                ret = TRUE; // unlimited
            } else {
                ret = FALSE;
            }
            break;
        case FORMATTER_maxLineLength_Number:
            if (options.getMaxLineLength() instanceof ceylon.formatter.options.Unlimited) {
                ret = "0";
            } else {
                ret = options.getMaxLineLength().toString();
            }
            break;
        case FORMATTER_lineBreakStrategy:
            ret = options.getLineBreakStrategy().toString();
            break;
        case FORMATTER_lineBreaksAfterLineComment_First:
            ret = options.getLineBreaksAfterLineComment().getFirst().toString();
            break;
        case FORMATTER_lineBreaksAfterLineComment_Last:
            ret = options.getLineBreaksAfterLineComment().getLast().toString();
            break;
        case FORMATTER_lineBreaksAfterSingleComment_First:
            ret = options.getLineBreaksAfterSingleComment().getFirst()
                    .toString();
            break;
        case FORMATTER_lineBreaksAfterSingleComment_Last:
            ret = options.getLineBreaksAfterSingleComment().getLast()
                    .toString();
            break;
        case FORMATTER_lineBreaksBeforeMultiComment_First:
            ret = options.getLineBreaksBeforeMultiComment().getFirst()
                    .toString();
            break;
        case FORMATTER_lineBreaksBeforeMultiComment_Last:
            ret = options.getLineBreaksBeforeMultiComment().getLast()
                    .toString();
            break;
        case FORMATTER_lineBreaksAfterMultiComment_First:
            ret = options.getLineBreaksAfterMultiComment().getFirst()
                    .toString();
            break;
        case FORMATTER_lineBreaksAfterMultiComment_Last:
            ret = options.getLineBreaksAfterMultiComment().getLast().toString();
            break;
        case FORMATTER_lineBreaksBeforeSingleComment_First:
            ret = options.getLineBreaksBeforeSingleComment().getFirst()
                    .toString();
            break;
        case FORMATTER_lineBreaksBeforeSingleComment_Last:
            ret = options.getLineBreaksBeforeSingleComment().getLast()
                    .toString();
            break;
        case FORMATTER_lineBreaksInTypeParameterList_First:
            ret = options.getLineBreaksInTypeParameterList().getFirst()
                    .toString();
            break;
        case FORMATTER_lineBreaksInTypeParameterList_Last:
            ret = options.getLineBreaksInTypeParameterList().getLast()
                    .toString();
            break;
        case FORMATTER_lineBreaksBeforeLineComment_First:
            ret = options.getLineBreaksBeforeLineComment().getFirst()
                    .toString();
            break;
        case FORMATTER_lineBreaksBeforeLineComment_Last:
            ret = options.getLineBreaksBeforeLineComment().getLast().toString();
            break;
        case FORMATTER_lineBreaksBetweenImportElements_First:
            ret = options.getLineBreaksBetweenImportElements().getFirst().toString();
            break;
        case FORMATTER_lineBreaksBetweenImportElements_Last:
            ret = options.getLineBreaksBetweenImportElements().getLast().toString();
            break;
            
        case FORMATTER_elseOnOwnLine:
            ret = booleanString(options.getElseOnOwnLine());
            break;
        case FORMATTER_failFast:
            ret = booleanString(options.getFailFast());
            break;
        case FORMATTER_braceOnOwnLine:
            ret = booleanString(options.getBraceOnOwnLine());
            break;
        case FORMATTER_inlineAnnotations:
            if (options.getInlineAnnotations() instanceof ceylon.formatter.options.All) {
                ret = TRUE; // All
            } else {
                ret = FALSE;
            }
            break;
        case FORMATTER_inlineAnnotations_List:
            if (options.getInlineAnnotations() instanceof ceylon.formatter.options.All) {
                ret = "all";
            } else {
                @SuppressWarnings("unchecked") // checked by Ceylon type info
                ceylon.language.Iterable<? extends String, ? extends Object> it = (ceylon.language.Iterable<? extends String, ? extends Object>)options.getInlineAnnotations();
                ret = ceylon.language.String.join(",", it);
            }
            break;
        case FORMATTER_lineBreak:
            ret = ideOptions.getLineBreak().toString();
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
        int num;
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
            options.setIndentBeforeTypeInfo(new ceylon.language.Integer(
                    new Long(value)));
            break;
        case FORMATTER_space_BeforeMethodOrClassPositionalArgumentList:
            options.setSpaceBeforeMethodOrClassPositionalArgumentList(ceylonBoolean(value));
            break;
        case FORMATTER_space_AroundSatisfiesOf:
            options.setSpaceAroundSatisfiesOf(ceylonBoolean(value));
            break;
        case FORMATTER_space_AroundImportAliasEqualsSign:
            options.setSpaceAroundImportAliasEqualsSign(ceylonBoolean(value));
            break;
        case FORMATTER_space_AfterTypeParamListComma:
            options.setSpaceAfterTypeParamListComma(ceylonBoolean(value));
            break;
        case FORMATTER_space_AfterTypeArgListComma:
            options.setSpaceAfterTypeArgListComma(ceylonBoolean(value));
            break;
        case FORMATTER_space_AroundTypeParamListEqualsSign:
            options.setSpaceAroundTypeParamListEqualsSign(ceylonBoolean(value));
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
        case FORMATTER_space_BeforeValueIteratorClosingParenthesis:
            options.setSpaceBeforeValueIteratorClosingParenthesis(ceylonBoolean(value));
            break;
        case FORMATTER_space_AfterSequenceEnumerationOpeningBrace:
            options.setSpaceAfterSequenceEnumerationOpeningBrace(ceylonBoolean(value));
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
        case FORMATTER_space_AfterControlStructureKeyword:
            options.setSpaceAfterControlStructureKeyword(ceylonBoolean(value));
            break;
        case FORMATTER_space_OptionalAroundOperatorLevel:
            options.setSpaceOptionalAroundOperatorLevel(
                new ceylon.language.Integer(new Long(value)));
            break;

        case FORMATTER_maxLineLength:
            if (TRUE.equals(value)) {
                options.setMaxLineLength(unlimited_.get_());
            } else {
                options.setMaxLineLength(ceylon.language.Integer.instance(80L)); // don't use FormattingOptions.$default$maxLineLength(null): that's unlimited!
            }
            break;
        case FORMATTER_maxLineLength_Number:
            if (value == null) {
                options.setMaxLineLength(unlimited_.get_());
            } else {
                if (value.equals("0")) {
                    options.setMaxLineLength(unlimited_.get_());
                } else {
                    options.setMaxLineLength(ceylon.language.Integer.instance(java.lang.Integer.parseInt(value)));
                }
            }
            break;
        case FORMATTER_lineBreakStrategy:
            options.setLineBreakStrategy(options.getLineBreakStrategy()); // default only
            break;
        case FORMATTER_lineBreaksAfterLineComment_First:
            num = Integer.parseInt(value);
            options.setLineBreaksAfterLineComment(setFirst(
                    options.getLineBreaksAfterLineComment(), num));
            break;
        case FORMATTER_lineBreaksAfterLineComment_Last:
            num = Integer.parseInt(value);
            options.setLineBreaksAfterLineComment(setLast(
                    options.getLineBreaksAfterLineComment(), num));
            break;
        case FORMATTER_lineBreaksAfterSingleComment_First:
            num = Integer.parseInt(value);
            options.setLineBreaksAfterSingleComment(setFirst(
                    options.getLineBreaksAfterSingleComment(), num));
            break;
        case FORMATTER_lineBreaksAfterSingleComment_Last:
            num = Integer.parseInt(value);
            options.setLineBreaksAfterSingleComment(setLast(
                    options.getLineBreaksAfterSingleComment(), num));
            break;
        case FORMATTER_lineBreaksBeforeMultiComment_First:
            num = Integer.parseInt(value);
            options.setLineBreaksBeforeMultiComment(setFirst(
                    options.getLineBreaksBeforeMultiComment(), num));
            break;
        case FORMATTER_lineBreaksBeforeMultiComment_Last:
            num = Integer.parseInt(value);
            options.setLineBreaksBeforeMultiComment(setLast(
                    options.getLineBreaksBeforeMultiComment(), num));
            break;
        case FORMATTER_lineBreaksAfterMultiComment_First:
            num = Integer.parseInt(value);
            options.setLineBreaksAfterMultiComment(setFirst(
                    options.getLineBreaksAfterMultiComment(), num));
            break;
        case FORMATTER_lineBreaksAfterMultiComment_Last:
            num = Integer.parseInt(value);
            options.setLineBreaksAfterMultiComment(setLast(
                    options.getLineBreaksAfterMultiComment(), num));
            break;
        case FORMATTER_lineBreaksBeforeSingleComment_First:
            num = Integer.parseInt(value);
            options.setLineBreaksBeforeSingleComment(setFirst(
                    options.getLineBreaksBeforeSingleComment(), num));
            break;
        case FORMATTER_lineBreaksBeforeSingleComment_Last:
            num = Integer.parseInt(value);
            options.setLineBreaksBeforeSingleComment(setLast(
                    options.getLineBreaksBeforeSingleComment(), num));
            break;
        case FORMATTER_lineBreaksInTypeParameterList_First:
            num = Integer.parseInt(value);
            options.setLineBreaksInTypeParameterList(setFirst(
                    options.getLineBreaksInTypeParameterList(), num));
            break;
        case FORMATTER_lineBreaksInTypeParameterList_Last:
            num = Integer.parseInt(value);
            options.setLineBreaksInTypeParameterList(setLast(
                    options.getLineBreaksInTypeParameterList(), num));
            break;
        case FORMATTER_lineBreaksBeforeLineComment_First:
            num = Integer.parseInt(value);
            options.setLineBreaksBeforeLineComment(setFirst(
                    options.getLineBreaksBeforeLineComment(), num));
            break;
        case FORMATTER_lineBreaksBeforeLineComment_Last:
            num = Integer.parseInt(value);
            options.setLineBreaksBeforeLineComment(setLast(
                    options.getLineBreaksBeforeLineComment(), num));
            break;
        case FORMATTER_lineBreaksBetweenImportElements_First:
            num = Integer.parseInt(value);
            options.setLineBreaksBetweenImportElements(setFirst(
                    options.getLineBreaksBetweenImportElements(), num));
            break;
        case FORMATTER_lineBreaksBetweenImportElements_Last:
            num = Integer.parseInt(value);
            options.setLineBreaksBetweenImportElements(setLast(
                    options.getLineBreaksBetweenImportElements(), num));
            break;

        case FORMATTER_elseOnOwnLine:
            options.setElseOnOwnLine(ceylonBoolean(value));
            break;
        case FORMATTER_failFast:
            options.setFailFast(ceylonBoolean(value));
            break;
        case FORMATTER_braceOnOwnLine:
            options.setBraceOnOwnLine(ceylonBoolean(value));
            break;
        case FORMATTER_inlineAnnotations:
            if (TRUE.equals(value)) {
                options.setInlineAnnotations(all_.get_());
            } else {
                options.setInlineAnnotations(FormattingOptions.$default$inlineAnnotations(null, null, null, null, null, null, null, null));
            }
            break;
        case FORMATTER_inlineAnnotations_List:
            if (value == null) {
                options.setInlineAnnotations(all_.get_());
            } else {
                if (value.equals("all")) {
                    options.setInlineAnnotations(all_.get_());
                } else {
                    options.setInlineAnnotations(ceylon.language.String.split(value.replace(',', ' ')));
                }
            }
            break;
        case FORMATTER_lineBreak:
            if (ceylon.formatter.options.lf_.get_().toString().equals(value)) {
                options.setLineBreak(lf_.get_());
            } else if (ceylon.formatter.options.os_.get_().toString()
                    .equals(value)) {
                options.setLineBreak(os_.get_());
            } else if (ceylon.formatter.options.crlf_.get_().toString()
                    .equals(value)) {
                options.setLineBreak(crlf_.get_());
            }
            break;

        default:
            break;
        }
    }

    private Range<ceylon.language.Integer> setFirst(
            Range<ceylon.language.Integer> range, int num) {
        return span_.span(range.getFirst().$getType$(),
                new ceylon.language.Integer(num), range.getLast());
    }

    private Range<ceylon.language.Integer> setLast(
            Range<ceylon.language.Integer> range, int num) {
        return span_.span(range.getFirst().$getType$(), range.getFirst(),
                new ceylon.language.Integer(num));
    }

    private ceylon.language.Boolean ceylonBoolean(String value) {
        return Boolean.parseBoolean(value) ? new ceylon.language.true_()
                : new ceylon.language.false_();
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
