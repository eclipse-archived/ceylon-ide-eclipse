import com.redhat.ceylon.compiler.typechecker.context {
	PhasedUnit
}

import com.redhat.ceylon.compiler.typechecker.analyzer {
	ModuleManager {
		moduleDescriptorFileName = \iMODULE_FILE,
		packageDescriptorFileName = \iPACKAGE_FILE
	}
}

import com.redhat.ceylon.compiler.typechecker.tree {
	Tree {
		TreeDeclaration = Declaration
	}
}

import com.redhat.ceylon.compiler.typechecker.model {
		ModelDeclaration = Declaration
}

"Builds a [[model delta|AbstractDelta]] that describes the model differences 
 between a [[reference PhasedUnit|buildDeltas.referencePhasedUnit]] 
 and a [[changed PhasedUnit|buildDeltas.changedPhasedUnit]]
 related to the same file.
 
 In case of a regular compilation unit(not a descriptor), only the 
 model elements visibile _outside_ the unit are considered.
 "
shared  CompilationUnitDelta buildDeltas(
	"Referenced phased unit, typically of central Ceylon model"
	PhasedUnit referencePhasedUnit, 
	"Changed phased unit, typically a just-saved working copy"
	PhasedUnit changedPhasedUnit) {
	
	assert(exists unitFile = referencePhasedUnit.unitFile);
	if (unitFile.name == moduleDescriptorFileName) {
		return buildModuleDescriptorDeltas(referencePhasedUnit, changedPhasedUnit);
	}
	
	if (unitFile.name == packageDescriptorFileName) {
		return buildPackageDescriptorDeltas(referencePhasedUnit, changedPhasedUnit);
	}
	
	return buildCompilationUnitDeltas(referencePhasedUnit, changedPhasedUnit);
}

ModuleDescriptorDelta buildModuleDescriptorDeltas(PhasedUnit referencePhasedUnit, PhasedUnit changedPhasedUnit) => nothing;

PackageDescriptorDelta buildPackageDescriptorDeltas(PhasedUnit referencePhasedUnit, PhasedUnit changedPhasedUnit) => nothing;

RegularCompilationUnitDelta buildCompilationUnitDeltas(PhasedUnit referencePhasedUnit, PhasedUnit changedPhasedUnit) => nothing;

