package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.BufferedTokenStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.ReplaceEdit;

import ceylon.formatter.format_;
import ceylon.formatter.options.LineBreak;
import ceylon.formatter.options.Spaces;
import ceylon.formatter.options.SparseFormattingOptions;
import ceylon.formatter.options.Tabs;
import ceylon.formatter.options.combinedOptions_;
import ceylon.formatter.options.crlf_;
import ceylon.formatter.options.lf_;
import ceylon.formatter.options.os_;
import ceylon.formatter.options.loadProfile_;
import ceylon.language.AssertionError;
import ceylon.language.Singleton;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Body;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage;
import com.redhat.ceylon.eclipse.code.style.CeylonStyle;
import com.redhat.ceylon.eclipse.util.Indents;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.eclipse.util.StringBuilderWriter;

final class FormatAction extends Action {
    private final CeylonEditor editor;
    private final boolean respectSelection;

    FormatAction(CeylonEditor editor) {
        this(editor, true);
    }
    
    FormatAction(CeylonEditor editor, boolean respectSelection) {
        super(null);
        this.editor = editor;
        this.respectSelection = respectSelection;
    }
    
    /**
     * Creates {@link SparseFormattingOptions} that respect whitespace-relevant settings:
     * <ul>
     * <li>{@link SparseFormattingOptions#getIndentMode() indentMode} from spaces-for-tabs and editor-tab-width</li>
     * <li>{@link SparseFormattingOptions#getLineBreak() lineBreak} from document newline character</li>
     * </ul>
     */
    private static SparseFormattingOptions getWsOptions(IDocument document) {
        LineBreak lb;
        if (document instanceof IDocumentExtension4) {
            switch(((IDocumentExtension4)document).getDefaultLineDelimiter()){
            case "\n":
                lb = lf_.get_();
                break;
            case "\r\n":
                lb = crlf_.get_();
                break;
            default:
                lb = os_.get_();
                break;
            }
        } else {
            lb = os_.get_();
        }
        return new SparseFormattingOptions(
                /* indentMode = */ Indents.getIndentWithSpaces() ? 
                        new Spaces(Indents.getIndentSpaces()) : 
                        new Tabs(Indents.getIndentSpaces()),
                /* maxLineLength = */ null,
                /* lineBreakStrategy = */ null,
                /* braceOnOwnLine = */ null,
                /* spaceBeforeParamListOpeningParen = */ null,
                /* spaceAfterParamListOpeningParen = */ null,
                /* spaceBeforeParamListClosingParen = */ null,
                /* spaceAfterParamListClosingParen = */ null,
                /* inlineAnnotations = */ null,
                /* spaceBeforeMethodOrClassPositionalArgumentList = */ null,
                /* spaceBeforeAnnotationPositionalArgumentList = */ null,
                /* importStyle = */ null,
                /* spaceAroundImportAliasEqualsSign = */ null,
                /* lineBreaksBeforeLineComment = */ null,
                /* lineBreaksAfterLineComment = */ null,
                /* lineBreaksBeforeSingleComment = */ null,
                /* lineBreaksAfterSingleComment = */ null,
                /* lineBreaksBeforeMultiComment = */ null,
                /* lineBreaksAfterMultiComment = */ null,
                /* lineBreaksInTypeParameterList = */ null,
                /* spaceAfterSequenceEnumerationOpeningBrace = */ null,
                /* spaceBeforeSequenceEnumerationClosingBrace = */ null,
                /* spaceBeforeForOpeningParenthesis = */ null,
                /* spaceAfterValueIteratorOpeningParenthesis = */ null,
                /* spaceBeforeValueIteratorClosingParenthesis = */ null,
                /* spaceBeforeIfOpeningParenthesis = */ null,
                /* failFast = */ null,
                /* spaceBeforeResourceList = */ null,
                /* spaceBeforeCatchVariable = */ null,
                /* spaceBeforeWhileOpeningParenthesis = */ null,
                /* spaceAfterTypeArgOrParamListComma = */ null,
                /* indentBeforeTypeInfo = */ null,
                /* indentationAfterSpecifierExpressionStart = */ null,
                /* indentBlankLines = */ null,
                /* lineBreak = */ lb
                );
    }
    
    @Override
    public boolean isEnabled() {
        CeylonParseController cpc = editor.getParseController();
        return isEnabled(cpc);
    }

    public static boolean isEnabled(CeylonParseController cpc) {
        return cpc!=null && 
                cpc.getStage().ordinal()>=Stage.SYNTACTIC_ANALYSIS.ordinal() &&
                cpc.getRootNode()!=null;
    }
    
    private static class FormattingUnit {
        public final Node node;
        public final CommonToken startToken;
        public final CommonToken endToken;
        public FormattingUnit(final Node node, final CommonToken startToken, final CommonToken endToken) {
            this.node = node;
            this.startToken = startToken;
            this.endToken= endToken;
        }
    }
    
    @Override
    public void run() {
        IDocument document = editor.getCeylonSourceViewer().getDocument();
        final ITextSelection ts = getSelection(editor);
        final boolean selected = respectSelection && ts.getLength() > 0;
        final CeylonParseController pc = editor.getParseController();
        format(pc, document, ts, selected, editor.getSelectionProvider());
    }

    public static void format(final CeylonParseController pc, 
            IDocument document, final ITextSelection ts, 
            final boolean selected, ISelectionProvider selectionProvider) {
        if (!isEnabled(pc)) return;
        final List<CommonToken> tokenList = pc.getTokens();
        final List<FormattingUnit> formattingUnits;
        if (selected) {
            // a node was selected, format only that
            Node selectedRootNode = Nodes.findNode(pc.getRootNode(), ts);
            if (selectedRootNode == null)
                return;
            if (selectedRootNode instanceof Body || selectedRootNode instanceof CompilationUnit) {
                // format only selected statements, not entire body / CU (from now on: body)
                
                Iterator<Statement> it;
                if (selectedRootNode instanceof Body) {
                    it = ((Body)selectedRootNode).getStatements().iterator();
                } else {
                    it = (Iterator<Statement>)(Iterator)((CompilationUnit)selectedRootNode).getDeclarations().iterator();
                }
                Statement stat = null;
                formattingUnits = new ArrayList<FormattingUnit>();
                
                int tokenIndex = -1;
                // find first selected statement
                while (it.hasNext()) {
                    stat = it.next();
                    CommonToken start = (CommonToken)stat.getToken();
                    CommonToken end = (CommonToken)stat.getEndToken();
                    if (end.getStopIndex() >= ts.getOffset()) {
                        formattingUnits.add(new FormattingUnit(stat, start, end));
                        tokenIndex = end.getTokenIndex() + 1;
                        break;
                    }
                }
                // find last selected statement
                while (it.hasNext()) {
                    stat = it.next();
                    CommonToken start = (CommonToken)stat.getToken();
                    CommonToken end = (CommonToken)stat.getEndToken();
                    if (start.getStartIndex() >= ts.getOffset() + ts.getLength()) {
                        break;
                    }
                    formattingUnits.add(new FormattingUnit(stat, tokenList.get(tokenIndex), end));
                    tokenIndex = end.getTokenIndex() + 1;
                }
                
                if (formattingUnits.isEmpty()) {
                    // possible if the selection spanned the entire content of the body,
                    // or if the body is empty, etc.
                    formattingUnits.add(new FormattingUnit(
                            selectedRootNode,
                            (CommonToken)selectedRootNode.getToken(),
                            (CommonToken)selectedRootNode.getEndToken()));
                }
            } else {
                formattingUnits = Collections.singletonList(new FormattingUnit(
                        selectedRootNode,
                        (CommonToken)selectedRootNode.getToken(),
                        (CommonToken)selectedRootNode.getEndToken()));
            }
        } else {
            // format everything
            formattingUnits = Collections.singletonList(new FormattingUnit(
                    pc.getRootNode(),
                    tokenList.get(0),
                    tokenList.get(tokenList.size() - 1)));
        }
        
        final StringBuilder builder = new StringBuilder(document.getLength());
        final SparseFormattingOptions wsOptions = getWsOptions(document);
        try {
            for (FormattingUnit unit : formattingUnits) {
                final int startTokenIndex = unit.startToken.getTokenIndex();
                final int endTokenIndex = unit.endToken.getTokenIndex();
                final int startIndex = unit.startToken.getStartIndex();
                final int stopIndex = unit.endToken.getStopIndex();
                final TokenSource tokens = new TokenSource() {
                    int i = startTokenIndex;
                    @Override
                    public Token nextToken() {
                        if (i <= endTokenIndex)
                            return tokenList.get(i++);
                        else if (i == endTokenIndex + 1)
                            return tokenList.get(tokenList.size() - 1); // EOF token
                        else
                            return null;
                    }
                    @Override
                    public String getSourceName() {
                        throw new IllegalStateException("No one should need this");
                    }
                };
                final int indentLevel = Indents.getIndent(unit.node, document).length() / Indents.getIndentSpaces();
                if (unit != formattingUnits.get(0)) {
                    // add indentation
                    builder.append(wsOptions.getIndentMode().indent(indentLevel));
                }
                format_.format(
                        unit.node,
                        combinedOptions_.combinedOptions(
                                loadProfile_.loadProfile(
                                        CeylonStyle.getFormatterProfile(pc.getProject()),
                                        /* inherit = */ false,
                                        /* baseDir = */ pc.getProject().getLocation().toOSString()),
                                new Singleton<SparseFormattingOptions>
                                    (SparseFormattingOptions.$TypeDescriptor$, wsOptions)),
                        new StringBuilderWriter(builder),
                        new BufferedTokenStream(tokens),
                        indentLevel
                        );
                if (unit == formattingUnits.get(0)) {
                    // trim leading indentation (from formatter's indentBefore)
                    int firstNonWsIndex = 0;
                    while (Character.isWhitespace(builder.charAt(firstNonWsIndex)))
                        firstNonWsIndex++;
                    if (firstNonWsIndex != 0)
                        builder.delete(0, firstNonWsIndex);
                }
            }
        } catch (Exception e) {
            return;
        } catch (AssertionError e) {
            return;
        }
        
        final String text;
        if (selected) {
            // remove the trailing line break
            text = builder.substring(0, 
                    builder.length() - wsOptions.getLineBreak().getText().length());
        } else {
            text = builder.toString();
        }
        try {
            final int startIndex = formattingUnits.get(0).startToken.getStartIndex();
            final int stopIndex = formattingUnits.get(formattingUnits.size() - 1).endToken.getStopIndex();
            if (!document.get().equals(text)) {
                DocumentChange change = 
                        new DocumentChange("Format", document);
                change.setEdit(new ReplaceEdit(
                        selected ? startIndex : 0,
                                selected ? stopIndex - startIndex + 1 : document.getLength(),
                                        text));
                change.perform(new NullProgressMonitor());
                if (selected) {
                    selectionProvider.setSelection(new TextSelection(startIndex, text.length()));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
