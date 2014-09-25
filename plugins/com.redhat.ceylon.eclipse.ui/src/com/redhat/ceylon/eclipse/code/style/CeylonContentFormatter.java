package com.redhat.ceylon.eclipse.code.style;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.IContentFormatterExtension;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.formatter.IFormattingStrategy;
import org.eclipse.jface.text.source.ISourceViewer;

import ceylon.file.Writer;
import ceylon.file.Writer$impl;
import ceylon.formatter.format_;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonParser;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonContentFormatter implements IContentFormatter,
        IContentFormatterExtension {

    private CeylonSourceViewer viewer;

    public CeylonContentFormatter(ISourceViewer sourceViewer) {
        if (sourceViewer instanceof CeylonSourceViewer) {
            this.viewer = (CeylonSourceViewer) sourceViewer;
        }
    }

    @Override
    public void format(IDocument document, IRegion region) {
        // TODO Do we need this?
    }

    @Override
    public IFormattingStrategy getFormattingStrategy(String s) {
        return new CeylonFormattingStrategy(s);
    }

    @Override
    public void format(IDocument document, IFormattingContext formattingContext) {
        CeylonLexer lexer = new CeylonLexer(new ANTLRStringStream(
                document.get()));
        try {
            Tree.CompilationUnit cu = new CeylonParser(new CommonTokenStream(
                    lexer)).compilationUnit();
            lexer.reset();
            final StringBuilder builder = new StringBuilder(
                    document.getLength());
            format_.format(
                    cu,
                    ((FormatterPreferences) formattingContext
                            .getProperty(FormattingContextProperties.CONTEXT_PREFERENCES))
                            .getOptions(), new StringBuilderWriter(builder));
            document.set(builder.toString());
        } catch (RecognitionException re) {
            CeylonPlugin
                    .getInstance()
                    .getLog()
                    .log(new StatusInfo(IStatus.WARNING,
                            "Error formatting sample code, should not have happened"));
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

        @Override
        public Object writeBytes(
                ceylon.language.Iterable<? extends ceylon.language.Byte, ? extends Object> bytes) {
            // unused; ceylon.formatter never writes bytes
            throw new UnsupportedOperationException();
        }
    }
}
