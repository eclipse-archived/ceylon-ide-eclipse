package com.redhat.ceylon.eclipse.util;

import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;
import static org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.compiler.typechecker.tree.Node;

public class Indents {

    public static String getIndent(Node node, IDocument doc) {
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
    
    public static String getDefaultIndent() {
        StringBuilder result = new StringBuilder();
        initialIndent(result);
        return result.toString();
    }
    
    public static int getIndentSpaces() {
        IPreferenceStore store = EditorsUI.getPreferenceStore();
        return store==null ? 4 : store.getInt(EDITOR_TAB_WIDTH);
    }
    
    public static boolean getIndentWithSpaces() {
        IPreferenceStore store = EditorsUI.getPreferenceStore();
        return store==null ? false : store.getBoolean(EDITOR_SPACES_FOR_TABS);
    }

    public static void initialIndent(StringBuilder buf) {
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

    public static String getDefaultLineDelimiter(IDocument document) {
        if (document instanceof IDocumentExtension4) {
            return ((IDocumentExtension4) document).getDefaultLineDelimiter();
        }
        else {
            return System.lineSeparator();
        }
    }
    
}
