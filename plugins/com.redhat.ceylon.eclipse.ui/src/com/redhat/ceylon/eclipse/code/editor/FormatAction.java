package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelection;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.BufferedTokenStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Body;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage;
import com.redhat.ceylon.eclipse.code.style.CeylonStyle;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Indents;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.eclipse.util.StringBuilderWriter;
import com.redhat.ceylon.ide.common.refactoring.DefaultRegion;

import ceylon.formatter.format_;
import ceylon.formatter.options.FormattingOptions;
import ceylon.formatter.options.SparseFormattingOptions;
import ceylon.formatter.options.combinedOptions_;
import ceylon.formatter.options.loadProfile_;
import ceylon.language.AssertionError;
import ceylon.language.Singleton;

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
    
    @Override
    public boolean isEnabled() {
        CeylonParseController cpc = editor.getParseController();
        return isEnabled(cpc);
    }

    public static boolean isEnabled(CeylonParseController cpc) {
        return cpc!=null && 
                cpc.getStage().ordinal()>=Stage.SYNTACTIC_ANALYSIS.ordinal() &&
                cpc.getParsedRootNode()!=null;
    }
    
    @Deprecated
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
        
        if (!isEnabled(pc)) {
            return;
        }
        
        FormattingOptions options = loadProfile_.loadProfile(
                CeylonStyle.getFormatterProfile(pc.getProject()),
                /* inherit = */ false,
                /* baseDir = */ pc.getProject().getLocation().toOSString());
        
        TextChange change = editorJ2C().eclipseFormatAction().format(pc.getParsedRootNode(),
                pc.getTokens(), document, document.getLength(),
                new DefaultRegion(ts.getOffset(), ts.getLength()),
                CeylonStyle.getEclipseWsOptions(document),
                options);
        
        EditorUtil.performChange(change);
        
        selectionProvider.setSelection(new TextSelection(
                change.getEdit().getOffset(),
                change.getEdit().getLength()));
    }
    
    @Deprecated
    void oldformat(final CeylonParseController pc, 
            IDocument document, final ITextSelection ts, 
            final boolean selected, ISelectionProvider selectionProvider) {
        if (!isEnabled(pc)) return;
        final List<CommonToken> tokenList = pc.getTokens();
        final List<FormattingUnit> formattingUnits;
        boolean formatAll = !selected || document.getLength()==ts.getLength();
        if (!formatAll) {
            // a node was selected, format only that
            Node selectedRootNode = Nodes.findNode(pc.getParsedRootNode(), pc.getTokens(), ts);
            if (selectedRootNode == null)
                return;
            if (selectedRootNode instanceof Body || selectedRootNode instanceof CompilationUnit) {
                // format only selected statements, not entire body / CU (from now on: body)
                
                Iterator<? extends Statement> it;
                if (selectedRootNode instanceof Body) {
                    it = ((Body)selectedRootNode).getStatements().iterator();
                } else {
                    it = ((CompilationUnit)selectedRootNode).getDeclarations().iterator();
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
                    pc.getParsedRootNode(),
                    tokenList.get(0),
                    tokenList.get(tokenList.size() - 1)));
        }
        
        final StringBuilder builder = new StringBuilder(document.getLength());
        final SparseFormattingOptions wsOptions = CeylonStyle.getEclipseWsOptions(document);
        try {
            for (FormattingUnit unit : formattingUnits) {
                final int startTokenIndex = unit.startToken.getTokenIndex();
                final int endTokenIndex = unit.endToken.getTokenIndex();
//                final int startIndex = unit.startToken.getStartIndex();
//                final int stopIndex = unit.endToken.getStopIndex();
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
                final int indentLevel = (int) (Indents.indents()
                        .getIndent(unit.node, document)
                        .replace("\t", wsOptions.getIndentMode().indent(1))
                        .length()
                        / Indents.indents().getIndentSpaces());
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
            CeylonPlugin.log(IStatus.ERROR, "Error during code formatting", e);
            return;
        } catch (AssertionError e) {
            CeylonPlugin.log(IStatus.ERROR, "Error during code formatting", e);
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
            final int from = formatAll ? 0 : startIndex;
            final int length = formatAll ? document.getLength() : stopIndex - startIndex + 1;
            if (!document.get(from, length).equals(text)) {
                DocumentChange change = new DocumentChange("Format", document);
                change.setEdit(new ReplaceEdit(from, length, text));
                EditorUtil.performChange(change);
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
