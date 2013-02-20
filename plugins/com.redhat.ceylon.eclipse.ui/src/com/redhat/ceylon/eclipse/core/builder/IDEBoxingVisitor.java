package com.redhat.ceylon.eclipse.core.builder;

import com.redhat.ceylon.compiler.java.codegen.BoxingVisitor;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;

public class IDEBoxingVisitor extends BoxingVisitor {

	@Override
	protected boolean isBooleanTrue(Declaration decl) {
		return IDEBoxingUtil.isBooleanTrue(decl);
	}

	@Override
	protected boolean isBooleanFalse(Declaration decl) {
        return IDEBoxingUtil.isBooleanFalse(decl);
	}

    @Override
    protected boolean hasErasure(ProducedType type) {
        return IDEBoxingUtil.hasErasure(type);
    }

    @Override
    protected boolean isTypeParameter(ProducedType type) {
        return IDEBoxingUtil.isTypeParameter(type);
    }
}
