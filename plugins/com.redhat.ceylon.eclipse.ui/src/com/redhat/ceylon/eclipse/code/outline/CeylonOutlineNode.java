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

import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.tree.Node;

public class CeylonOutlineNode {
	
    public static final int ROOT_CATEGORY = -3;
    public static final int DEFAULT_CATEGORY = 0;
    public static final int PACKAGE_CATEGORY = -2;
    public static final int IMPORT_LIST_CATEGORY = -1;

    private List<CeylonOutlineNode> fChildren= new ArrayList<CeylonOutlineNode>();

    private CeylonOutlineNode parent;

    private final Node astNode;

    private final int category;

    public CeylonOutlineNode(Node astNode) {
        this(astNode, DEFAULT_CATEGORY);
    }

    public CeylonOutlineNode(Node astNode, int category) {
        this.astNode= astNode;
        this.category= category;
    }

    public CeylonOutlineNode(Node astNode, CeylonOutlineNode parent) {
        this(astNode, parent, DEFAULT_CATEGORY);
    }

    public CeylonOutlineNode(Node astNode, CeylonOutlineNode parent, 
    		int category) {
        this.astNode= astNode;
        this.parent= parent;
        this.category= category;
    }

    public void addChild(CeylonOutlineNode child) {   
        fChildren.add(child);
    }

    public List<CeylonOutlineNode> getChildren() {
        return fChildren;
    }

    public CeylonOutlineNode getParent() {
        return parent;
    }

    public Node getASTNode() {
        return astNode;
    }

    public int getCategory() {
        return category;
    }

    public String toString() {
        StringBuilder sb= new StringBuilder();

        sb.append(astNode.toString());
        if (!fChildren.isEmpty()) {
            sb.append(" [");
            for(CeylonOutlineNode child: fChildren) {
                sb.append(child);
            }
            sb.append(" ]");
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
    	return obj instanceof CeylonOutlineNode &&
    			((CeylonOutlineNode) obj).astNode==astNode;
    }
    
    @Override
    public int hashCode() {
    	return astNode.hashCode();
    }
}
