package com.redhat.ceylon.eclipse.code.outline;

import static java.util.Arrays.asList;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonDocumentRangeNode extends DocumentRangeNode 
        implements ITypedElement {
    
    private static ImageRegistry imageRegistry = 
            CeylonPlugin.getInstance().getImageRegistry();
    
    private final CeylonOutlineNode node;
    
    public CeylonDocumentRangeNode(DocumentRangeNode parent, 
            String id, CeylonOutlineNode outlineNode, 
            IDocument document) {
        super(parent, 1, id, document,
                outlineNode.getRealStartOffset(), 
                outlineNode.getRealEndOffset()-outlineNode.getRealStartOffset());
        node = outlineNode;
    }
    
    @Override
    public Image getImage() {
        return imageRegistry.get(node.getImageKey());
    }
    
    @Override
    public String getName() {
        if (node.getParent()==null) {
            return "Ceylon Source File";
        }
        else {
            return node.getLabel().toString();
        }
    }
    
    @Override
    public String getType() {
        return "ceylon";
    }

    @Override
    public String toString() {
        return node.getIdentifier() + asList(getChildren());
    }
    
}