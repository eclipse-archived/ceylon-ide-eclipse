package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.util.Nodes.findNode;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextHoverExtension2;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public abstract class SourceInfoHover implements ITextHover, ITextHoverExtension, ITextHoverExtension2 {

    protected static Node getHoverNode(IRegion hoverRegion, CeylonParseController parseController) {
        if (parseController==null) {
            return null;
        }
        Tree.CompilationUnit rootNode = 
                parseController.getRootNode();
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
        IDocument document = textViewer.getDocument();
        int start= -2;
        int end= -1;
        
        try {
            int pos= offset;
            char c;
        
            while (pos >= 0) {
                c= document.getChar(pos);
                if (!Character.isJavaIdentifierPart(c)) {
                    break;
                }
                --pos;
            }
            start= pos;
        
            pos= offset;
            int length= document.getLength();
        
            while (pos < length) {
                c= document.getChar(pos);
                if (!Character.isJavaIdentifierPart(c)) {
                    break;
        
                }
                ++pos;
            }
            end= pos;
        
        } catch (BadLocationException x) {
        }
        
        if (start >= -1 && end > -1) {
            if (start == offset && end == offset)
                return new Region(offset, 0);
            else if (start == offset)
                return new Region(start, end - start);
            else
                return new Region(start + 1, end - start - 1);
        }
        
        return null;
    }

}