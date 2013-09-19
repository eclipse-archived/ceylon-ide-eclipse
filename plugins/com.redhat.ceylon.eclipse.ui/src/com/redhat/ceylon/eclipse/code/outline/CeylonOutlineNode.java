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
import static java.lang.System.identityHashCode;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class CeylonOutlineNode implements IAdaptable {
	
    public static final int ROOT_CATEGORY = -4;
    public static final int DEFAULT_CATEGORY = 0;
    public static final int PACKAGE_CATEGORY = -3;
    public static final int UNIT_CATEGORY = -2;
    public static final int IMPORT_LIST_CATEGORY = -1;

    private final List<CeylonOutlineNode> children= new ArrayList<CeylonOutlineNode>();

    private CeylonOutlineNode parent;

    private final Node treeNode;
    private final int category;
    private IResource resource;

    CeylonOutlineNode(Node treeNode) {
        this(treeNode, DEFAULT_CATEGORY);
    }

    CeylonOutlineNode(Node treeNode, int category) {
        this.treeNode = treeNode;
        this.category = category;
    }
    
    CeylonOutlineNode(Node treeNode, CeylonOutlineNode parent) {
        this(treeNode, parent, DEFAULT_CATEGORY);
    }

    CeylonOutlineNode(Node treeNode, CeylonOutlineNode parent, 
            int category) {
        this.treeNode = treeNode;
        this.parent = parent;
        this.category = category;
    }

    CeylonOutlineNode(Node treeNode, CeylonOutlineNode parent, 
            int category, IResource resource) {
        this.treeNode = treeNode;
        this.parent = parent;
        this.category = category;
        this.resource = resource;
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
        try {
            //note: we actually have two different outline
            //      nodes that both represent the same
            //      tree node, so we need to use the 
            //      category to distinguish them!
            switch (category) {
            case ROOT_CATEGORY:
                return "@root";
            case PACKAGE_CATEGORY:
                return "@package:" + ((PackageNode)treeNode).getPackageName();
            case UNIT_CATEGORY:
                return "@unit: " + ((Tree.CompilationUnit) treeNode).getUnit().getFilename();
            case IMPORT_LIST_CATEGORY:
                return "@importlist" + ((Tree.ImportList) treeNode).getUnit().getFilename();
            case DEFAULT_CATEGORY:
            default:
                if (treeNode instanceof Tree.Import) {
                    Tree.ImportPath importPath = ((Tree.Import) treeNode).getImportPath();
                    return "@import:" + pathToName(importPath);
                }
                else if (treeNode instanceof Tree.Declaration) {
                    Tree.Identifier id = ((Tree.Declaration) treeNode).getIdentifier();
                    String name = id==null ? 
                            String.valueOf(identityHashCode(treeNode)) : 
                                id.getText();
                            if (parent!=null && parent.getTreeNode() instanceof Tree.Declaration) {
                                return getParent().getIdentifier() + ":" + name;
                            }
                            else {
                                return "@declaration:" + name;
                            }
                }
                else if (treeNode instanceof Tree.ImportModule) {
                    Tree.ImportPath importPath = ((Tree.ImportModule) treeNode).getImportPath();
                    return "@importmodule:" + pathToName(importPath);
                }
                else if (treeNode instanceof Tree.ModuleDescriptor) {
                    Tree.ImportPath importPath = ((Tree.ModuleDescriptor) treeNode).getImportPath();
                    return "@moduledescriptor:" + pathToName(importPath);
                }
                else if (treeNode instanceof Tree.PackageDescriptor) {
                    Tree.ImportPath importPath = ((Tree.PackageDescriptor) treeNode).getImportPath();
                    return "@packagedescriptor:" + pathToName(importPath);
                }
                else {
                    throw new RuntimeException("unexpected node type");
                }
            }
        }
        catch (RuntimeException re) {
            re.printStackTrace();
            return "";
        }
    }

    private String pathToName(Tree.ImportPath importPath) {
        String name = importPath==null ? 
                String.valueOf(identityHashCode(treeNode)) : 
                    formatPath(importPath.getIdentifiers());
        return name;
    }
    
    @Override
    public String toString() {
        return getIdentifier();
    }

    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        if (adapter.equals(IFile.class) || 
            adapter.equals(IResource.class)) {
            return resource;
        }
        else if (adapter.equals(IJavaElement.class) && 
                treeNode instanceof PackageNode) {
            return JavaCore.create(resource);
        }
        else {
            return null;
        }
    }
    
}
