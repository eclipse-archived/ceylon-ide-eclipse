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
    
    "String representation of the changedElement"
    shared default String changedElementString => changedElement.string;

        "Deltas related to the members of the  [[model element|AbstractDelta.changedElement]] that might impact some other compilation units"
    shared formal {AbstractDelta*} childrenDeltas;
    
    "Changes on the [[model element|AbstractDelta.changedElement]] that might impact some other compilation units"
    shared formal {ImpactingChange*} changes;
    
    shared actual String string {
        return "`` changedElementString `` {
                  changes = `` changes ``
                  childrenDeltas = {`` ((childrenDeltas.empty) then "}" else "
                  ") + operatingSystem.newline.join {
                          for (childDelta in childrenDeltas) for (line in childDelta.string.lines) "    " + line
                      }
                  + ((! childrenDeltas.empty) then 
               "
                  }" else "") ``
                }";
    }
    shared default actual Boolean equals(Object that) {
        if (is AbstractDelta that) {
            return 
                changedElementString==that.changedElementString && 
                ! anyPair((AbstractDelta first, AbstractDelta second) => first != second, childrenDeltas, that.childrenDeltas) &&
                ! anyPair((ImpactingChange first, ImpactingChange second) => first != second, changes, that.changes);
        }
        else {
            return false;
        }
    }
}

shared interface CompilationUnitDelta of RegularCompilationUnitDelta | ModuleDescriptorDelta | PackageDescriptorDelta satisfies AbstractDelta {}

shared interface ModuleDescriptorDelta satisfies CompilationUnitDelta {
    shared formal actual Module changedElement;
    shared formal actual {ModuleImportDelta*} childrenDeltas;
    shared alias PossibleChange => StructuralChange|ModuleImportAdded;
    shared formal actual {PossibleChange *} changes;
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
