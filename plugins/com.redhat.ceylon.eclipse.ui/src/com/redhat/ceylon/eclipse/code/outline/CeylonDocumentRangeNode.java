package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class CeylonDocumentRangeNode extends DocumentRangeNode 
        implements ITypedElement {
	
    private final ILabelProvider labelProvider;
    private final Node node;
    
    public CeylonDocumentRangeNode(ILabelProvider labelProvider, 
            DocumentRangeNode parent, CeylonOutlineNode outlineNode, 
    		IDocument document) {
        super(parent, 
                getTypeCode(outlineNode), 
                getIdentifier(outlineNode), 
                document,
                getStartOffset(outlineNode.getTreeNode()), 
                getLength(outlineNode.getTreeNode()));
        node = outlineNode.getTreeNode();
        this.labelProvider = labelProvider;
    }
    
    @Override
    public Image getImage() {
        return labelProvider.getImage(node);
    }
    
    @Override
    public String getName() {
        return labelProvider.getText(node);
    }
    
    @Override
    public String getType() {
        return "ceylon";
    }
    
    private static int getTypeCode(CeylonOutlineNode on) {
        Node treeNode = ((CeylonOutlineNode) on).getTreeNode();
        if (treeNode instanceof Tree.Declaration) {
            return ((Tree.Declaration) treeNode).getDeclarationModel()
                    .getDeclarationKind().ordinal();
        }
        else if (treeNode instanceof Tree.ImportList) {
            return 50;
        }
        else if (treeNode instanceof Tree.Import) {
            return 100;
        }
        else if (treeNode instanceof Tree.ImportModule) {
            return 150;
        }
        else if (treeNode instanceof Tree.CompilationUnit) {
            return 200;
        }
        else {
            return -1;
        }
    }
    
    private static String getIdentifier(CeylonOutlineNode on) {
        Node treeNode = ((CeylonOutlineNode) on).getTreeNode();
        if (treeNode instanceof Tree.Import) {
            return "@import:" + formatPath(((Tree.Import) treeNode).getImportPath().getIdentifiers());
        }
        else if (treeNode instanceof Tree.Declaration) {
            String qualifiedName = ((Tree.Declaration) treeNode).getDeclarationModel().getQualifiedNameString();
            int index = qualifiedName.indexOf("::");
            return "@element:" + (index>0 ? qualifiedName.substring(index+2) : qualifiedName);
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
        else {
            return treeNode.toString();
        }
    }

}