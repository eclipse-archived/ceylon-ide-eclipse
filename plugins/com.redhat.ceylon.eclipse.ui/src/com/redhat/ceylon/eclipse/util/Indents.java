package com.redhat.ceylon.eclipse.util;

import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.correct.correctJ2C;
import com.redhat.ceylon.eclipse.platform.platformJ2C;

public class Indents {
    public static final Indents INSTANCE = new Indents();
    
    public String getDefaultLineDelimiter(IDocument document) {
        return new correctJ2C().newDocument(document).getDefaultLineDelimiter();
    }
    
    public String getIndent(Node node, IDocument document) {
        return new correctJ2C().newDocument(document).getIndent(node);
    }

    public String getDefaultIndent() {
        return new platformJ2C().platformServices().getDocument().getDefaultIndent();
    }

    public int getIndentSpaces() {
        return (int) new platformJ2C().platformServices().getDocument().getIndentSpaces();
    }

    public boolean getIndentWithSpaces() {
        return new platformJ2C().platformServices().getDocument().getIndentWithSpaces();
    }

    public void initialIndent(StringBuilder builder) {
        new platformJ2C().platformServices().getDocument().initialIndent(builder);
    }
}
