package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageKeyForNode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getStyledLabelForNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonDocumentRangeNode extends DocumentRangeNode 
        implements ITypedElement {
	
    private static ImageRegistry imageRegistry = CeylonPlugin.getInstance()
            .getImageRegistry();
    
    private final CeylonOutlineNode node;
    
    public CeylonDocumentRangeNode(DocumentRangeNode parent, 
            CeylonOutlineNode outlineNode, 
    		IDocument document) {
        super(parent, 1, 
                outlineNode.getIdentifier(), 
                document,
                getStartOffset(outlineNode), 
                getLength(outlineNode));
        node = outlineNode;
    }
    
    @Override
    public Image getImage() {
        return imageRegistry.get(getImageKeyForNode(node.getTreeNode()));
    }
    
    @Override
    public String getName() {
        if (node.getParent()==null) {
            return "Ceylon Compilation Unit";
        }
        else {
            return getStyledLabelForNode(node.getTreeNode()).toString();
        }
    }
    
    @Override
    public String getType() {
        return "ceylon";
    }

    @Override
    public String toString() {
        return node.getIdentifier();
    }
    
}