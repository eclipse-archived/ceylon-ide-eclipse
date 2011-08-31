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
	
	public int getLocation() {
		return location.getLine();
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
			return getLocation()==that.getLocation() && 
					file.equals(that.file);
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return getLocation() ^ file.getName().hashCode();
	}
	
}
