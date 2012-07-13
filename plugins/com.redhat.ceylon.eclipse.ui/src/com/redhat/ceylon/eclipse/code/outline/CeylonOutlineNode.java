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

import org.eclipse.imp.editor.ModelTreeNode;

import com.redhat.ceylon.compiler.typechecker.tree.Node;

public class CeylonOutlineNode extends ModelTreeNode {

	public CeylonOutlineNode(Node astNode, int category) {
		super(astNode, category);
	}

	public CeylonOutlineNode(Node astNode, CeylonOutlineNode parent, int category) {
		super(astNode, parent, category);
	}

	public CeylonOutlineNode(Node astNode, CeylonOutlineNode parent) {
		super(astNode, parent);
	}

	public CeylonOutlineNode(Node astNode) {
		super(astNode);
	}
	
	@Override
	public Node getASTNode() {
		return (Node) super.getASTNode();
	}
	
    public CeylonOutlineNode[] getChildren() {
        ModelTreeNode[] children = super.getChildren();
        CeylonOutlineNode[] result = new CeylonOutlineNode[children.length];
        System.arraycopy(children, 0, result, 0, children.length);
		return result;
    }

    public CeylonOutlineNode getParent() {
        return (CeylonOutlineNode) super.getParent();
    }
	
    /*public static final int DEFAULT_CATEGORY= 0;

    private static final CeylonOutlineNode[] NO_CHILDREN= new CeylonOutlineNode[0];

    private CeylonOutlineNode[] fChildren= NO_CHILDREN;

    private CeylonOutlineNode fParent;

    private final Object fASTNode;

    private final int fCategory;

    public CeylonOutlineNode(Object astNode) {
        this(astNode, DEFAULT_CATEGORY);
    }

    public CeylonOutlineNode(Object astNode, int category) {
        fASTNode= astNode;
        fCategory= category;
    }

    public CeylonOutlineNode(Object astNode, CeylonOutlineNode parent) {
        this(astNode, parent, DEFAULT_CATEGORY);
    }

    public CeylonOutlineNode(Object astNode, CeylonOutlineNode parent, int category) {
        fASTNode= astNode;
        fParent= parent;
        fCategory= category;
    }

    public void setChildren(CeylonOutlineNode[] children) {
        fChildren= children;
        for(int i= 0; i < children.length; i++) {
            children[i].fParent= this;
        }
    }

    public void addChild(CeylonOutlineNode child) {
        CeylonOutlineNode[] newChildren= new CeylonOutlineNode[fChildren.length + 1];
        System.arraycopy(fChildren, 0, newChildren, 0, fChildren.length);
        newChildren[fChildren.length]= child;
        fChildren= newChildren;
    }

    public CeylonOutlineNode[] getChildren() {
        return fChildren;
    }

    public CeylonOutlineNode getParent() {
        return fParent;
    }

    public Object getASTNode() {
        return fASTNode;
    }

    public int getCategory() {
        return fCategory;
    }

    public String toString() {
        StringBuilder sb= new StringBuilder();

        sb.append(fASTNode.toString());
        if (fChildren.length > 0) {
            sb.append(" [");
            for(int i= 0; i < fChildren.length; i++) {
                sb.append(fChildren[i].toString());
            }
            sb.append(" ]");
        }
        return sb.toString();
    }*/
}
