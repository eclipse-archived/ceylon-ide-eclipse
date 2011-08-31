package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Declaration;

public class CeylonElement {
	Tree.Declaration node;
	IFile file;
	Token location;
	
	public CeylonElement(Declaration node, IFile file, Token location) {
		this.node = node;
		this.file = file;
		this.location = location;
	}
	
	public String getLocation() {
		return location.getLine() + ":" + 
				location.getCharPositionInLine();
	}

	public Tree.Declaration getNode() {
		return node;
	}
	
	public IFile getFile() {
		return file;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CeylonElement) {
			CeylonElement that = (CeylonElement) obj;
			return node==that.node && 
					file.equals(that.file);
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return node.hashCode();
	}
	
}
