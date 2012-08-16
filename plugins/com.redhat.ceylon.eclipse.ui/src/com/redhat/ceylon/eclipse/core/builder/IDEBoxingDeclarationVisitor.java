package com.redhat.ceylon.eclipse.core.builder;

import com.redhat.ceylon.compiler.java.codegen.BoxingDeclarationVisitor;
import com.redhat.ceylon.compiler.typechecker.model.BottomType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public class IDEBoxingDeclarationVisitor extends BoxingDeclarationVisitor {

	@Override
	protected boolean isCeylonBasicType(ProducedType type) {
		return (isCeylonString(type) || isCeylonBoolean(type) || isCeylonInteger(type) || isCeylonFloat(type) || isCeylonCharacter(type));
	}

	private boolean isCeylonBoolean(ProducedType type) {
		return type.isSubtypeOf(unit(type).getBooleanDeclaration().getType())
				&& !(type.getDeclaration() instanceof BottomType);
	}

	private boolean isCeylonString(ProducedType type) {
		return unit(type).getStringDeclaration() == type.getDeclaration();
	}

	private Unit unit(ProducedType type) {
		return type.getDeclaration().getUnit();
	}

	private boolean isCeylonInteger(ProducedType type) {
		return unit(type).getIntegerDeclaration() == type.getDeclaration();
	}

	private boolean isCeylonFloat(ProducedType type) {
		return unit(type).getFloatDeclaration() == type.getDeclaration();
	}

	private boolean isCeylonCharacter(ProducedType type) {
		return unit(type).getCharacterDeclaration() == type.getDeclaration();
	}

	@Override
	protected boolean isNothing(ProducedType type) {
		return unit(type).getNothingDeclaration() == type.getDeclaration();
	}

	@Override
	protected boolean isObject(ProducedType type) {
		return unit(type).getObjectDeclaration() == type.getDeclaration();
	}
}
