package com.redhat.ceylon.eclipse.core.builder;

import com.redhat.ceylon.compiler.java.codegen.BoxingDeclarationVisitor;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;

public class IDEBoxingDeclarationVisitor extends BoxingDeclarationVisitor {

	@Override
	protected boolean isCeylonBasicType(ProducedType type) {
		return isCeylonString(type) || 
				isCeylonBoolean(type) || 
				isCeylonInteger(type) || 
				isCeylonFloat(type) || 
				isCeylonCharacter(type);
	}

	private boolean isCeylonBoolean(ProducedType type) {
		return type.isSubtypeOf(unit(type).getBooleanDeclaration().getType())
				&& !(type.getDeclaration() instanceof NothingType);
	}

	private boolean isCeylonString(ProducedType type) {
		return unit(type).getStringDeclaration().equals(type.getDeclaration());
	}

	private Unit unit(ProducedType type) {
		return type.getDeclaration().getUnit();
	}

	private boolean isCeylonInteger(ProducedType type) {
		return unit(type).getIntegerDeclaration().equals(type.getDeclaration());
	}

	private boolean isCeylonFloat(ProducedType type) {
		return unit(type).getFloatDeclaration().equals(type.getDeclaration());
	}

	private boolean isCeylonCharacter(ProducedType type) {
		return unit(type).getCharacterDeclaration().equals(type.getDeclaration());
	}

	@Override
	protected boolean isNull(ProducedType type) {
		return unit(type).getNullDeclaration().equals(type.getDeclaration());
	}

	@Override
	protected boolean isObject(ProducedType type) {
		return unit(type).getObjectDeclaration().equals(type.getDeclaration());
	}

	@Override
	protected boolean willEraseToObject(ProducedType type) {
		//TODO: is this correct??
		Unit unit = unit(type);
		TypeDeclaration dec = type.getDeclaration();
		return unit.getObjectDeclaration()==dec ||
				unit.getIdentifiableDeclaration()==dec ||
				unit.getBasicDeclaration()==dec ||
				unit.getNullDeclaration()==dec ||
				unit.getAnythingDeclaration()==dec ||
				dec instanceof NothingType ||
				dec instanceof UnionType || 
				dec instanceof IntersectionType;
	}
}
