import com.redhat.ceylon.compiler.typechecker.model {
    Declaration,
    Unit,
    Module,
    Package,
    ModuleImport
}

shared alias DifferencedModelElement => Module | ModuleImport | Package | Unit | Declaration;

shared interface AbstractDelta of CompilationUnitDelta | ModuleImportDelta | DeclarationDelta {
	"Element for which the delta has been calculated"
    shared formal DifferencedModelElement changedElement;

    "Deltas related to the members of the  [[model element|AbstractDelta.changedElement]] that might impact some other compilation units"
    shared formal {AbstractDelta*} childrenDeltas;

    "Changes on the [[model element|AbstractDelta.changedElement]] that might impact some other compilation units"
    shared formal {ImpactingChange*} changes;
}

shared interface CompilationUnitDelta of RegularCompilationUnitDelta | ModuleDescriptorDelta | PackageDescriptorDelta satisfies AbstractDelta {}

shared interface ModuleDescriptorDelta satisfies CompilationUnitDelta {
    shared formal actual Module changedElement;
    shared formal actual {ModuleImportDelta*} childrenDeltas;
    shared alias PossibleChange => StructuralChange|ModuleImportAdded;
    shared formal actual {<PossibleChange> *} changes;
}

shared interface ModuleImportDelta satisfies AbstractDelta {
    shared formal actual ModuleImport changedElement;
    shared actual [] childrenDeltas => [];
    shared alias PossibleChange => <Removed | MadeVisibleOutsideScope | MadeInvisibleOutsideScope>;
    shared formal actual [PossibleChange]|[] changes;
}
 
shared interface PackageDescriptorDelta satisfies CompilationUnitDelta {
    shared formal actual Package changedElement;
    shared actual [] childrenDeltas => [];
    shared alias PossibleChange => <StructuralChange|MadeVisibleOutsideScope|MadeInvisibleOutsideScope>;
    shared formal actual [<PossibleChange>]|[] changes;
}
 
 shared interface DeclarationDelta of TopLevelDeclarationDelta | NestedDeclarationDelta satisfies AbstractDelta {
    shared formal actual Declaration changedElement;
    shared formal actual {NestedDeclarationDelta*} childrenDeltas;
}

shared interface TopLevelDeclarationDelta satisfies DeclarationDelta {
    shared alias PossibleChange => <StructuralChange | Removed | DeclarationMemberAdded | MadeVisibleOutsideScope | MadeInvisibleOutsideScope>;
    shared formal actual {<PossibleChange>*} changes;
}

shared interface NestedDeclarationDelta satisfies DeclarationDelta {
    shared alias PossibleChange => <StructuralChange | Removed | DeclarationMemberAdded>;
    shared formal actual {<StructuralChange | Removed | DeclarationMemberAdded>*} changes;
}

shared interface RegularCompilationUnitDelta satisfies CompilationUnitDelta {
    shared formal actual Unit changedElement;
    shared alias PossibleChange => TopLevelDeclarationAdded;
    shared formal actual {PossibleChange*} changes;
    shared formal actual {TopLevelDeclarationDelta*} childrenDeltas;
}
