/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.hover;

import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findNode;
import static java.lang.Character.isJavaIdentifierPart;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;

public abstract class SourceInfoHover 
        implements ITextHover, ITextHoverExtension, 
                   ITextHoverExtension2 {

    protected static Node getHoverNode(IRegion hoverRegion, 
            CeylonParseController parseController) {
        if (parseController==null) {
            return null;
        }
        Tree.CompilationUnit rootNode = 
                parseController.getTypecheckedRootNode();
        if (rootNode!=null) {
            return findNode(rootNode, 
                    hoverRegion.getOffset());
        }
        return null;
    }

    protected CeylonEditor editor;

    public SourceInfoHover(CeylonEditor editor) {
        this.editor = editor;
    }

    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return findWord(textViewer, offset);
    }

    static IRegion findWord(ITextViewer textViewer, int offset) {
        IDocument document = textViewer.getDocument();
        int start = -2;
        int end = -1;
        
        try {
            int pos = offset;
            char c;
        
            while (pos >= 0) {
                c = document.getChar(pos);
                if (!isJavaIdentifierPart(c)) {
                    break;
                }
                --pos;
            }
            start = pos;
        
            pos = offset;
            int length = document.getLength();
        
            while (pos < length) {
                c = document.getChar(pos);
                if (!isJavaIdentifierPart(c)) {
                    break;
        
                }
                ++pos;
            }
            end= pos;
        
        } 
        catch (BadLocationException x) {}
        
        if (start >= -1 && end > -1) {
            if (start == offset && end == offset) {
                return new Region(offset, 0);
            }
            else if (start == offset) {
                return new Region(start, end - start);
            }
            else {
                return new Region(start + 1, end - start - 1);
            }
        }
        
        return null;
    }

}