package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.search.ui.text.Match;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class CeylonSearchMatch extends Match {
	
	private String location; 
	private Tree.Declaration declarationNode;
	
	public CeylonSearchMatch(Object element, int offset, int length, 
			String location, Tree.Declaration declarationNode) {
		super(element, offset, length);
		this.location=location;
		this.declarationNode=declarationNode;
	}
	
	public String getLocation() {
		return location;
	}
	
	public Tree.Declaration getDeclarationNode() {
		return declarationNode;
	}
	
}
