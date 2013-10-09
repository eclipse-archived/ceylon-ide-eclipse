package com.redhat.ceylon.eclipse.code.outline;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public final class HierarchyInput {
	Declaration declaration;
	TypeChecker typeChecker;
	public HierarchyInput(Declaration declaration, TypeChecker typeChecker) {
		this.declaration = declaration;
		this.typeChecker = typeChecker;
	}
}