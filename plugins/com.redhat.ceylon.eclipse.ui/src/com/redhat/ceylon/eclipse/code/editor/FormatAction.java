package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.getSelection;

import java.util.List;

import org.antlr.runtime.BufferedTokenStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenSource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.ReplaceEdit;

import ceylon.file.Writer;
import ceylon.file.Writer$impl;
import ceylon.formatter.format_;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Indents;
import com.redhat.ceylon.eclipse.util.Nodes;

final class FormatAction extends Action {
    private final CeylonEditor editor;

    FormatAction(CeylonEditor editor) {
        super(null);
        this.editor = editor;
    }
    
    @Override
    public void run() {
        IDocument document = editor.getCeylonSourceViewer().getDocument();
        final ITextSelection ts = getSelection(editor);
        final boolean selected = ts.getLength() > 0;
        final CeylonParseController pc = editor.getParseController();
        final Node node;
        if (selected) {
            // a node was selected, format only that
            node = Nodes.findNode(pc.getRootNode(), ts);
        } else {
            // format everything
            node = pc.getRootNode();
        }
        if (node == null) {
            return;
        }
        final CommonToken startToken = (CommonToken)node.getToken();
        final CommonToken endToken = (CommonToken)node.getEndToken();
        if (startToken == null || endToken == null) {
            return;
        }
        final int startTokenIndex = startToken.getTokenIndex();
        final int endTokenIndex = endToken.getTokenIndex();
        final int startIndex = startToken.getStartIndex();
        final int stopIndex = endToken.getStopIndex();
        final TokenSource tokens = new TokenSource() {
            int i = startTokenIndex;
            List<CommonToken> tokens = pc.getTokens();
            @Override
            public Token nextToken() {
                if (i <= endTokenIndex)
                    return tokens.get(i++);
                else if (i == endTokenIndex + 1)
                    return tokens.get(tokens.size() - 1); // EOF token
                else
                    return null;
            }
            @Override
            public String getSourceName() {
                throw new IllegalStateException("No one should need this");
            }
        };
        
        final StringBuilder builder = new StringBuilder(document.getLength());
        final SparseFormattingOptions wsOptions = getWsOptions();
        format_.format(
                node,
                format_.format$options(node),
                new StringBuilderWriter(builder),
                new BufferedTokenStream(tokens),
                Indents.getIndent(node, document).length() / Indents.getIndentSpaces()
                );
        
        final String text;
        if (selected) {
            // remove the trailing line break
            // TODO when we pass options to the formatter, we'll have to account for \r\n here, which is two chars long.
            text = builder.substring(0, builder.length() - 1);
        } else {
            text = builder.toString();
        }
        try {
            if (!document.get().equals(text)) {
                DocumentChange change = 
                        new DocumentChange("Format", document);
                change.setEdit(new ReplaceEdit(
                        selected ? startIndex : 0,
                                selected ? stopIndex - startIndex + 1 : document.getLength(),
                                        text));
                change.perform(new NullProgressMonitor());
                if (selected) {
                    editor.getSelectionProvider().setSelection(new TextSelection(startIndex, text.length()));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static class StringBuilderWriter implements Writer {
        private final StringBuilder builder;
        public StringBuilderWriter(StringBuilder builder) {
            this.builder = builder;
        }
        
        @Override
        public Object write(String string) {
            builder.append(string);
            return null; // void
        }

        // the rest is boring

        @Override
        public Writer$impl $ceylon$file$Writer$impl() {
            return new Writer$impl(this);
        }
        @Override
        public Object close() {
            return null; // void
        }
        @Override
        public Object destroy(Throwable arg0) {
            return null; // void
        }
        @Override
        public Object flush() {
            return null; // void
        }
        @Override
        public Object writeLine() {
            // unused; ceylon.formatter has its own newline handling
            throw new UnsupportedOperationException();
        }
        @Override
        public Object writeLine(String line) {
            // unused; ceylon.formatter has its own newline handling
            throw new UnsupportedOperationException();
        }
        @Override
        public String writeLine$line() {
            return ""; // default value for "line" parameter
        }
    }
}