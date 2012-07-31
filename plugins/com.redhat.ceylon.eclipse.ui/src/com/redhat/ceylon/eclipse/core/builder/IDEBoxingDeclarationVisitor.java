package com.redhat.ceylon.eclipse.core.builder;

import com.redhat.ceylon.compiler.java.codegen.BoxingDeclarationVisitor;
import com.redhat.ceylon.compiler.typechecker.model.BottomType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;

public class IDEBoxingDeclarationVisitor extends BoxingDeclarationVisitor {

	@Override
	protected boolean isCeylonBasicType(ProducedType type) {
		return (isCeylonString(type) || isCeylonBoolean(type) || isCeylonInteger(type) || isCeylonFloat(type) || isCeylonCharacter(type));
	}

	private boolean isCeylonBoolean(ProducedType type) {
		return type.isSubtypeOf(type.getDeclaration().getUnit().getBooleanDeclaration().getType())
				&& !(type.getDeclaration() instanceof BottomType);
	}

	private boolean isCeylonString(ProducedType type) {
		return type.getDeclaration().getUnit().getStringDeclaration() == type.getDeclaration();
	}

	private boolean isCeylonInteger(ProducedType type) {
		return type.getDeclaration().getUnit().getIntegerDeclaration() == type.getDeclaration();
	}

	private boolean isCeylonFloat(ProducedType type) {
		return type.getDeclaration().getUnit().getFloatDeclaration() == type.getDeclaration();
	}

	private boolean isCeylonCharacter(ProducedType type) {
		return type.getDeclaration().getUnit().getCharacterDeclaration() == type.getDeclaration();
	}

	@Override
	protected boolean isNothing(ProducedType type) {
		return type.getDeclaration().getUnit().getNothingDeclaration() == type.getDeclaration();
	}

	@Override
	protected boolean isObject(ProducedType type) {
		return type.getDeclaration().getUnit().getObjectDeclaration() == type.getDeclaration();
	}
}
