package com.redhat.ceylon.eclipse.core.builder;

import com.redhat.ceylon.compiler.java.codegen.BoxingDeclarationVisitor;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;

public class IDEBoxingDeclarationVisitor extends BoxingDeclarationVisitor {

	@Override
	protected boolean isCeylonBasicType(ProducedType type) {
		return IDEBoxingUtil.isCeylonBasicType(type);
	}

	@Override
	protected boolean isNull(ProducedType type) {
		return IDEBoxingUtil.isNull(type);
	}

	@Override
	protected boolean isObject(ProducedType type) {
		return IDEBoxingUtil.isObject(type);
	}

	@Override
	protected boolean willEraseToObject(ProducedType type) {
	    return IDEBoxingUtil.willEraseToObject(type);
	}

    @Override
    protected boolean hasErasure(ProducedType type) {
        return IDEBoxingUtil.hasErasure(type);
    }

    @Override
    protected boolean isCallable(ProducedType type) {
        return IDEBoxingUtil.isCallable(type);
    }

    @Override
    protected boolean isRaw(ProducedType type) {
        return IDEBoxingUtil.isTurnedToRaw(type);
    }
}
