/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation

*******************************************************************************/

package com.redhat.ceylon.eclipse.code.outline;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class CeylonOutlineNode {
	
    public static final int ROOT_CATEGORY = -3;
    public static final int DEFAULT_CATEGORY = 0;
    public static final int PACKAGE_CATEGORY = -2;
    public static final int IMPORT_LIST_CATEGORY = -1;

    private final List<CeylonOutlineNode> children= new ArrayList<CeylonOutlineNode>();

    private CeylonOutlineNode parent;

    private final Node treeNode;

    private final int category;

    CeylonOutlineNode(Node treeNode) {
        this(treeNode, DEFAULT_CATEGORY);
    }

    CeylonOutlineNode(Node treeNode, int category) {
        this.treeNode= treeNode;
        this.category= category;
    }

    CeylonOutlineNode(Node treeNode, CeylonOutlineNode parent) {
        this(treeNode, parent, DEFAULT_CATEGORY);
    }

    CeylonOutlineNode(Node treeNode, CeylonOutlineNode parent, 
    		int category) {
        this.treeNode= treeNode;
        this.parent= parent;
        this.category= category;
    }

    void addChild(CeylonOutlineNode child) {   
        children.add(child);
    }

    public List<CeylonOutlineNode> getChildren() {
        return children;
    }

    public CeylonOutlineNode getParent() {
        return parent;
    }

    public Node getTreeNode() {
        return treeNode;
    }

    public int getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object obj) {
    	return obj instanceof CeylonOutlineNode &&
    			(((CeylonOutlineNode) obj).treeNode==treeNode ||
    			((CeylonOutlineNode) obj).getIdentifier().equals(getIdentifier()));
    }
    
    @Override
    public int hashCode() {
        return getIdentifier().hashCode();
    }

    public String getIdentifier() {
        if (treeNode instanceof Tree.Import) {
            return "@import:" + formatPath(((Tree.Import) treeNode).getImportPath().getIdentifiers());
        }
        else if (treeNode instanceof Tree.Declaration) {
            String name = ((Tree.Declaration) treeNode).getIdentifier().getText();
            if (getParent()!=null && getParent().getTreeNode() instanceof Tree.Declaration) {
                return getParent().getIdentifier() + ":" + name;
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
        else if (treeNode instanceof PackageNode) {
            return "@packagenode";
        }
        else {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }
    
}
