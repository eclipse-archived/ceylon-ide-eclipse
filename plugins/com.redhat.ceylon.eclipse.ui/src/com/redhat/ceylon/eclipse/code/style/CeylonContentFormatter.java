package com.redhat.ceylon.eclipse.code.style;

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

import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

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
        CeylonParseController cpc = new CeylonParseController();
        cpc.parse(document, null, null);
        final StringBuilder builder = new StringBuilder(document.getLength());
        format_.format(
                cpc.getRootNode(),
                ((FormatterPreferences) formattingContext
                        .getProperty(FormattingContextProperties.CONTEXT_PREFERENCES))
                        .getOptions(), new StringBuilderWriter(builder));
        document.set(builder.toString());
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
