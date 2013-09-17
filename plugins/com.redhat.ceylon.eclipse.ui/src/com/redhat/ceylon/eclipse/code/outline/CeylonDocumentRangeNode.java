package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageKeyForNode;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getStyledLabelForNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
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
                getIdentifier(outlineNode), 
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
    
    private static String getIdentifier(CeylonOutlineNode on) {
        Node treeNode = ((CeylonOutlineNode) on).getTreeNode();
        if (treeNode instanceof Tree.Import) {
            return "@import:" + formatPath(((Tree.Import) treeNode).getImportPath().getIdentifiers());
        }
        else if (treeNode instanceof Tree.Declaration) {
            String name = ((Tree.Declaration) treeNode).getIdentifier().getText();
            if (on.getParent().getTreeNode() instanceof Tree.Declaration) {
                return getIdentifier(on.getParent()) + ":" + name;
            }
            else {
                return "@declaration:" + name;
            }
        }
        else if (treeNode instanceof Tree.ImportModule) {
            return "@importmodule:" + formatPath(((Tree.Import) treeNode).getImportPath().getIdentifiers());
        }
        else if (treeNode instanceof Tree.ImportList) {
            return "@importlist";
        }
        else if (treeNode instanceof Tree.CompilationUnit) {
            return "@compilationunit";
        }
        else if (treeNode instanceof Tree.ModuleDescriptor) {
            return "@moduledescriptor";
        }
        else if (treeNode instanceof Tree.PackageDescriptor) {
            return "@packagedescriptor";
        }
        else {
            return null;
        }
    }

}