package com.redhat.ceylon.eclipse.util;

import ceylon.file.Writer;
import ceylon.file.Writer$impl;

/**
 * A {@link Writer ceylon.file::Writer} that writes to a {@link StringBuilder java.lang.StringBuilder}.
 * <p>
 * Intended only for usage with ceylon.formatter;
 * attempting to {@link #writeLine()} or {@link #writeBytes(ceylon.language.Iterable) writeBytes()}
 * will throw an {@link UnsupportedOperationException}, because the formatter should never use
 * these methods, and it's unclear what they should do (which newline? which encoding?).
 */
public class StringBuilderWriter implements Writer {
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
    public Object writeBytes(ceylon.language.Iterable<? extends ceylon.language.Byte,? extends Object> bytes) {
        // unused; ceylon.formatter never writes bytes
        throw new UnsupportedOperationException();
    }
}
