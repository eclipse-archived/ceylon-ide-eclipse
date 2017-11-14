/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import org.eclipse.jface.text.IDocument;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.editors.text.EditorsUI;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;

public class Indents {
    
    private final static org.eclipse.ceylon.ide.common.util.Indents<IDocument> indents = utilJ2C().indents();
    
    public static org.eclipse.ceylon.ide.common.util.Indents<IDocument> indents() {
        return indents;
    }
    
    public String getIndent(Node node, IDocument doc) {
        if (node==null||node.getEndToken()==null||
                node.getEndToken().getLine()==0) {
            return "";
        }
        try {
            IRegion region = doc.getLineInformation(node.getToken().getLine()-1);
            String line = doc.get(region.getOffset(), region.getLength());
            char[] chars = line.toCharArray();
            for (int i=0; i<chars.length; i++) {
                if (chars[i]!='\t' && chars[i]!=' ') {
                    return line.substring(0,i);
                }
            }
            return line;
        }
        catch (BadLocationException ble) {
            return "";
        }
    }
    
    public String getDefaultIndent() {
        StringBuilder result = new StringBuilder();
        initialIndent(result);
        return result.toString();
    }
    
    public int getIndentSpaces() {
        IPreferenceStore store = EditorsUI.getPreferenceStore();
        return store==null ? 4 : store.getInt(EDITOR_TAB_WIDTH);
    }
    
    public boolean getIndentWithSpaces() {
        IPreferenceStore store = EditorsUI.getPreferenceStore();
        return store==null ? false : store.getBoolean(EDITOR_SPACES_FOR_TABS);
    }

    public void initialIndent(StringBuilder buf) {
        //guess an initial indent level
        if (getIndentWithSpaces()) {
            int spaces = getIndentSpaces();
            for (int i=1; i<=spaces; i++) {
                buf.append(' ');
            }
        }
        else {
            buf.append('\t');
        }
    }

    public String getDefaultLineDelimiter(IDocument document) {
        if (document instanceof IDocumentExtension4) {
            return ((IDocumentExtension4) document).getDefaultLineDelimiter();
        }
        else {
            return System.lineSeparator();
        }
    }
    
}
