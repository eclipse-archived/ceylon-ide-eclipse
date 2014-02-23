package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.eclipse.code.outline.CeylonOutlineNode.ROOT_CATEGORY;

import java.util.Arrays;

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
                outlineNode.getCategory()==ROOT_CATEGORY ?
                        "@root" :
                        outlineNode.getIdentifier(), 
                document,
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
        return node.getIdentifier() + Arrays.asList(getChildren());
    }
    
}