package com.redhat.ceylon.eclipse.core.builder;

import com.redhat.ceylon.compiler.java.codegen.BoxingVisitor;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;

public class IDEBoxingVisitor extends BoxingVisitor {

	@Override
	protected boolean isBooleanTrue(Declaration decl) {
		return decl.getUnit().getLanguageModuleDeclaration("true") == decl;
	}

	@Override
	protected boolean isBooleanFalse(Declaration decl) {
		return decl.getUnit().getLanguageModuleDeclaration("false") == decl;
	}

}
