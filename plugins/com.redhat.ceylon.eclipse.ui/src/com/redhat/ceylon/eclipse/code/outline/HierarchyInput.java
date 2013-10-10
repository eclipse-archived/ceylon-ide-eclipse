package com.redhat.ceylon.eclipse.code.outline;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public final class HierarchyInput {
	Declaration declaration;
	IProject project;
	public HierarchyInput(Declaration declaration, IProject project) {
		this.declaration = declaration;
		this.project = project;
	}
}