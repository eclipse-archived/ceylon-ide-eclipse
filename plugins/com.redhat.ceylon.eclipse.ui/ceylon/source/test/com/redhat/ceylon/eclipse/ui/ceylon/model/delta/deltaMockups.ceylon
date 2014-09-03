import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {...}
class ModuleDescriptorDeltaMockup(changedElementString, changes, childrenDeltas) satisfies ModuleDescriptorDelta {
    changedElement => nothing;
    shared actual String changedElementString;
    shared actual {ModuleDescriptorDelta.PossibleChange*} changes;
    shared actual {ModuleImportDeltaMockup*} childrenDeltas;
    shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
}

class ModuleImportDeltaMockup(changedElementString, changes) satisfies ModuleImportDelta {
    changedElement => nothing;
    shared actual String changedElementString;
    shared actual [ModuleImportDelta.PossibleChange]|[] changes;
    shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
}

class PackageDescriptorDeltaMockup(changedElementString, changes) satisfies PackageDescriptorDelta {
    changedElement => nothing;
    shared actual String changedElementString;
    shared actual [PackageDescriptorDelta.PossibleChange]|[] changes;
    shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
}

class TopLevelDeclarationDeltaMockup(changedElementString, changes, childrenDeltas) satisfies TopLevelDeclarationDelta {
    changedElement => nothing;
    shared actual String changedElementString;
    shared actual {TopLevelDeclarationDelta.PossibleChange*} changes;
    shared actual {NestedDeclarationDeltaMockup*} childrenDeltas;
    shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
}

class NestedDeclarationDeltaMockup(changedElementString, changes, childrenDeltas) satisfies NestedDeclarationDelta {
    changedElement => nothing;
    shared actual String changedElementString;
    shared actual {NestedDeclarationDelta.PossibleChange*} changes;
    shared actual {NestedDeclarationDeltaMockup*} childrenDeltas;
    shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
}

class RegularCompilationUnitDeltaMockup(changedElementString, changes, childrenDeltas) satisfies RegularCompilationUnitDelta {
    changedElement => nothing;
    shared actual String changedElementString;
    shared actual {RegularCompilationUnitDelta.PossibleChange*} changes;
    shared actual {TopLevelDeclarationDeltaMockup*} childrenDeltas;
    shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
}

alias CompilationUnitDeltaMockup => ModuleDescriptorDeltaMockup | PackageDescriptorDeltaMockup | RegularCompilationUnitDeltaMockup;