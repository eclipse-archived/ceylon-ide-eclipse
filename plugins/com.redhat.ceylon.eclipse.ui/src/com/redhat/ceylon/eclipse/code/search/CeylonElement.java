package com.redhat.ceylon.eclipse.code.search;

import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;

public class CeylonElement {
	Tree.StatementOrArgument node;
	VirtualFile file;
	Token location;
	
	public CeylonElement(Tree.StatementOrArgument node, 
			VirtualFile file, Token location) {
		if (node==null) {
			file.getName();
		}
		this.node = node;
		this.file = file;
		this.location = location;
	}
	
	public int getLocation() {
		return location.getLine();
	}

	public Tree.StatementOrArgument getNode() {
		return node;
	}
	
	public VirtualFile getVirtualFile() {
		return file;
	}
	
	public IFile getFile() {
		if (file instanceof IFileVirtualFile) {
			return ((IFileVirtualFile) file).getFile();
		}
		else {
			return null;
		}
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
