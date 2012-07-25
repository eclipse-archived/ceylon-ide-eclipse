package com.redhat.ceylon.eclipse.code.outline;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class CeylonHierarchyNode {
	private final Declaration declaration;
	//private final CeylonHierarchyNode parent;
	private final List<CeylonHierarchyNode> children= new ArrayList<CeylonHierarchyNode>();

	CeylonHierarchyNode(Declaration declaration) {
		this.declaration = declaration;
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
		if (obj instanceof CeylonHierarchyNode) {
			return declaration.equals(((CeylonHierarchyNode) obj).declaration);
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return declaration.hashCode();
	}
}
