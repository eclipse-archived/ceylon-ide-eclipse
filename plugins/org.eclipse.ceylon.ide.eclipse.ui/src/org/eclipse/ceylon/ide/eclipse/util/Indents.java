/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import org.eclipse.jface.text.IDocument;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.ide.eclipse.code.correct.correctJ2C;
import org.eclipse.ceylon.ide.eclipse.platform.platformJ2C;

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
