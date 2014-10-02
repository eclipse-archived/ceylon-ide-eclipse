package com.redhat.ceylon.eclipse.code.style;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CeylonFormatterConstants {

    public static final String FORMATTER_indentMode = "indentMode"; // Spaces(4)
    public static final String FORMATTER_indentMode_Spaces = "Spaces";
    public static final String FORMATTER_indentMode_Tabs = "Tabs";
    public static final String FORMATTER_indentMode_Mixed = "Mixed";

    public static final String FORMATTER_indentMode_Spaces_Size = "indentModeSpacesSize";
    public static final String FORMATTER_indentMode_Tabs_Size = "indentModeTabsSize";
    public static final String FORMATTER_indent_Blank_Lines = "indentBlankLines";
    public static final String FORMATTER_indent_Before_Type_Info = "indentBeforeTypeInfo";
    public static final String FORMATTER_indent_After_Specifier_Expression_Start = "indentationAfterSpecifierExpressionStart";

    public static final String FORMATTER_space_BeforeMethodOrClassPositionalArgumentList = "spaceBeforeMethodOrClassPositionalArgumentList";
    public static final String FORMATTER_space_BeforeResourceList = "spaceBeforeResourceList";
    public static final String FORMATTER_space_BeforeCatchVariable = "spaceBeforeCatchVariable";
    public static final String FORMATTER_space_AroundSatisfiesOf = "spaceAroundSatisfiesOf";
    public static final String FORMATTER_space_AroundImportAliasEqualsSign = "spaceAroundImportAliasEqualsSign";
    public static final String FORMATTER_space_AfterTypeArgOrParamListComma = "spaceAfterTypeArgOrParamListComma";
    public static final String FORMATTER_space_BeforeIfOpeningParenthesis = "spaceBeforeIfOpeningParenthesis";
    public static final String FORMATTER_space_BeforeSequenceEnumerationClosingBrace = "spaceBeforeSequenceEnumerationClosingBrace";
    public static final String FORMATTER_space_BeforeParamListOpeningParen = "spaceBeforeParamListOpeningParen";
    public static final String FORMATTER_space_BeforeParamListClosingParen = "spaceBeforeParamListClosingParen";
    public static final String FORMATTER_space_AfterParamListClosingParen = "spaceAfterParamListClosingParen";

    public static final String FORMATTER_space_AfterParamListClosingParen_Number = "spaceAfterParamListClosingParenNumber";

    public static final String FORMATTER_space_BeforeValueIteratorClosingParenthesis = "spaceBeforeValueIteratorClosingParenthesis";
    public static final String FORMATTER_space_AfterSequenceEnumerationOpeningBrace = "spaceAfterSequenceEnumerationOpeningBrace";
    public static final String FORMATTER_space_BeforeForOpeningParenthesis = "spaceBeforeForOpeningParenthesis";
    public static final String FORMATTER_space_BeforeWhileOpeningParenthesis = "spaceBeforeWhileOpeningParenthesis";
    public static final String FORMATTER_space_BeforeAnnotationPositionalArgumentList = "spaceBeforeAnnotationPositionalArgumentList";
    public static final String FORMATTER_space_AfterValueIteratorOpeningParenthesis = "spaceAfterValueIteratorOpeningParenthesis";
    public static final String FORMATTER_space_AfterParamListOpeningParen = "spaceAfterParamListOpeningParen";

    public static final String FORMATTER_maxLineLength = "maxLineLength";
    public static final String FORMATTER_maxLineLength_Number = "maxLineLengthNumber";
    public static final String FORMATTER_lineBreak = "lineBreak";
    public static final String FORMATTER_lineBreakStrategy = "lineBreakStrategy";
    public static final String FORMATTER_lineBreaksAfterLineComment_First = "lineBreaksAfterLineCommentFirst";
    public static final String FORMATTER_lineBreaksAfterLineComment_Last = "lineBreaksAfterLineCommentLast";
    public static final String FORMATTER_lineBreaksAfterSingleComment_First = "lineBreaksAfterSingleCommentFirst";
    public static final String FORMATTER_lineBreaksAfterSingleComment_Last = "lineBreaksAfterSingleCommentLast";
    public static final String FORMATTER_lineBreaksBeforeMultiComment_First = "lineBreaksBeforeMultiCommentFirst";
    public static final String FORMATTER_lineBreaksBeforeMultiComment_Last = "lineBreaksBeforeMultiCommentLast";
    public static final String FORMATTER_lineBreaksAfterMultiComment_First = "lineBreaksAfterMultiCommentFirst";
    public static final String FORMATTER_lineBreaksAfterMultiComment_Last = "lineBreaksAfterMultiCommentLast";
    public static final String FORMATTER_lineBreaksBeforeSingleComment_First = "lineBreaksBeforeSingleCommentFirst";
    public static final String FORMATTER_lineBreaksBeforeSingleComment_Last = "lineBreaksBeforeSingleCommentLast";
    public static final String FORMATTER_lineBreaksInTypeParameterList_First = "lineBreaksInTypeParameterListFirst";
    public static final String FORMATTER_lineBreaksInTypeParameterList_Last = "lineBreaksInTypeParameterListLast";
    public static final String FORMATTER_lineBreaksBeforeLineComment_First = "lineBreaksBeforeLineCommentFirst";
    public static final String FORMATTER_lineBreaksBeforeLineComment_Last = "lineBreaksBeforeLineCommentLast";

    public static final String FORMATTER_importStyle = "importStyle";
    public static final String FORMATTER_elseOnOwnLine = "elseOnOwnLine";
    public static final String FORMATTER_failFast = "failFast";
    public static final String FORMATTER_braceOnOwnLine = "braceOnOwnLine";
    public static final String FORMATTER_inlineAnnotations = "inlineAnnotations";
    public static final String FORMATTER_inlineAnnotations_List = "inlineAnnotations_List";

    // required for preview setup
    public static final String FORMATTER_LINE_SPLIT = "lineSplit";
    public static final String FORMATTER_TAB_SIZE = "tabSize";

    public static final List<String> acceptedInlineAnnotations = Collections.unmodifiableList(
            Arrays.asList(new String[] { "abstract",
            "actual", "annotation", "default", "final", "formal", "late",
            "native", "optional", "shared", "variable" }));
}
