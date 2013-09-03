package com.redhat.ceylon.eclipse.code.outline;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class CeylonHierarchyNode implements Comparable<CeylonHierarchyNode>{
    
	private final Declaration declaration;
	private boolean nonUnique;
	//private final CeylonHierarchyNode parent;
	private final List<CeylonHierarchyNode> children= new ArrayList<CeylonHierarchyNode>();

	CeylonHierarchyNode(Declaration declaration) {
		this.declaration = declaration;
	}
	
	public boolean isNonUnique() {
		return nonUnique;
	}
	
	void setNonUnique(boolean nonUnique) {
		this.nonUnique = nonUnique;
	}
	
	/*CeylonHierarchyNode(Declaration declaration, CeylonHierarchyNode parent) {
		this.declaration = declaration;
		this.parent = parent;
	}*/
	
	void addChild(CeylonHierarchyNode child) {
		if (!children.contains(child)) children.add(child);
	}
	
	public List<CeylonHierarchyNode> getChildren() {
		return children;
	}
	
	public Declaration getDeclaration() {
		return declaration;
	}
	
	/*public CeylonHierarchyNode getParent() {
		return parent;
	}*/
	
	@Override
	public boolean equals(Object obj) {
		if (this==obj) {
			return true;
		}
        else if (obj==null) {
            return false;
        }
		else if (declaration==null) {
			return false;
		}
		else if (obj instanceof CeylonHierarchyNode) {
			return declaration.equals(((CeylonHierarchyNode) obj).declaration);
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return declaration==null ? 
				0 : declaration.hashCode();
	}
	
	@Override
	public String toString() {
	    return declaration==null ? "null" : declaration.toString();
	}

    @Override
    public int compareTo(CeylonHierarchyNode node) {
        if (node.declaration==declaration) {
            return 0;
        }
        else if (node.declaration==null) {
            return 1;
        } 
        else if (declaration==null) {
            return -1;
        }
        else {
            int ct = declaration.getName().compareTo( node.declaration.getName());
            if (ct!=0) return ct;
            return declaration.getQualifiedNameString()
                    .compareTo( node.declaration.getQualifiedNameString());
        }
    }
}
