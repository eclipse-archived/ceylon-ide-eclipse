import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    ModuleManager {
        moduleDescriptorFileName=\iMODULE_FILE,
        packageDescriptorFileName=\iPACKAGE_FILE
    }
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree {
        AstDeclaration=Declaration,
        AstCompilationUnit=CompilationUnit,
        AstModuleDescriptor=ModuleDescriptor,
        AstImportModule=ImportModule,
        AstPackageDescriptor=PackageDescriptor
    },
    AstAbstractNode=Node,
    Visitor
}
import ceylon.collection {
    HashSet,
    HashMap,
    MutableMap,
    MutableSet,
    ArrayList
}
import com.redhat.ceylon.compiler.typechecker.model {
    Declaration,
    Unit
}

"Builds a [[model delta|AbstractDelta]] that describes the model differences 
 between a [[reference PhasedUnit|buildDeltas.referencePhasedUnit]] 
 and a [[changed PhasedUnit|buildDeltas.changedPhasedUnit]]
 related to the same file.
 
 In case of a regular compilation unit(not a descriptor), only the 
 model elements visibile _outside_ the unit are considered.
 "
shared CompilationUnitDelta buildDeltas(
    "Referenced phased unit, typically of central Ceylon model"
    PhasedUnit referencePhasedUnit,
    "Changed phased unit, typically a just-saved working copy"
    PhasedUnit changedPhasedUnit) {
    
    assert (exists unitFile = referencePhasedUnit.unitFile);
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

RegularCompilationUnitDelta buildCompilationUnitDeltas(PhasedUnit referencePhasedUnit, PhasedUnit changedPhasedUnit) {
    value builder = RegularCompilationUnitDeltaBuilder(referencePhasedUnit.compilationUnit, changedPhasedUnit.compilationUnit);
    return builder.buildDelta();
}

alias AstNode => <AstDeclaration | AstCompilationUnit | AstModuleDescriptor | AstImportModule | AstPackageDescriptor> & AstAbstractNode;

abstract class DeltaBuilder(AstNode oldNode, AstNode? newNode) {
    
    shared formal [AstNode*] getChildren(AstNode astNode);
    shared formal AbstractDelta buildDelta();
        
    shared formal void addRemovedChange();
    shared formal void calculateStructuralChanges();
    shared formal void addChildDelta(AstNode oldChild, AstNode? newChild);
    shared formal void addMemberAddedChange(AstNode newChild);
    
    shared default void recurse() {
        if (newNode is Null) {
            addRemovedChange();
            return;
        }
        assert(exists newNode);
        
        calculateStructuralChanges();
        
        [AstNode*] oldChildren = getChildren(oldNode);
        [AstNode*] newChildren = getChildren(newNode);
        
        if (newChildren nonempty || oldChildren nonempty) {
            value allChildrenSet = HashSet<String>();

            function toMap([AstNode*] children) {
                MutableMap<String,AstNode>? childrenSet;
                if (nonempty children) {
                    childrenSet = HashMap<String,AstNode>();
                    assert (exists childrenSet);
                    for (child in children) {
                        String childKey;
                        switch (child)
                        case(is AstDeclaration) {
                            childKey = child.declarationModel.qualifiedNameString;
                        }
                        case(is AstModuleDescriptor) {
                            childKey = child.unit.fullPath;
                        }
                        case(is AstPackageDescriptor) {
                            childKey = child.unit.fullPath;
                        }
                        case(is AstCompilationUnit) {
                            childKey = child.unit.fullPath;
                        }
                        case(is AstImportModule) {
                            childKey = "/".join {child.quotedLiteral.string, child.version.string};
                        }
                        
                        allChildrenSet.add(childKey);
                        childrenSet.put(childKey, child);
                    }
                } else {
                    childrenSet = null;
                }
                return childrenSet;
            }
            
            MutableMap<String,AstNode>? oldChildrenSet = toMap(oldChildren);
            MutableMap<String,AstNode>? newChildrenSet = toMap(newChildren);
            
            for (keyChild in allChildrenSet) {
                value oldChild = oldChildrenSet?.get(keyChild) else null;
                value newChild = newChildrenSet?.get(keyChild) else null;
                if (exists oldChild) {
                    addChildDelta(oldChild, newChild);
                } else {
                    assert(exists newChild);
                    addMemberAddedChange(newChild);
                }
            }
        }
    }
}

class RegularCompilationUnitDeltaBuilder(AstCompilationUnit oldNode, AstCompilationUnit newNode)
        extends DeltaBuilder(oldNode, newNode) {

    variable value changes = ArrayList<RegularCompilationUnitDelta.PossibleChange>();
    variable value childrenDeltas = ArrayList<TopLevelDeclarationDelta>();
    
    shared actual RegularCompilationUnitDelta buildDelta() {
        recurse();
        object delta satisfies RegularCompilationUnitDelta {
            changedElement => oldNode.unit;
            shared actual {RegularCompilationUnitDelta.PossibleChange*} changes => outer.changes;
            shared actual {TopLevelDeclarationDelta*} childrenDeltas => outer.childrenDeltas;
        }
        return delta;
    }
    
    shared actual void addChildDelta(AstNode oldChild, AstNode? newChild) {
        assert(is AstDeclaration oldChild, 
                is AstDeclaration? newChild, 
                oldChild.declarationModel.toplevel);
        value builder = TopLevelDeclarationDeltaBuilder(oldChild, newChild);
        childrenDeltas.add(builder.buildDelta());
    }
    
    shared actual void addMemberAddedChange(AstNode newChild) {
        assert(is AstDeclaration newChild, newChild.declarationModel.toplevel);
        changes.add(TopLevelDeclarationAdded(newChild.declarationModel.nameAsString, newChild.declarationModel.shared));
    }
    
    shared actual void addRemovedChange() {
        "A compilation unit cannot be removed from a PhasedUnit"
        assert(false);
    }
    
    shared actual void calculateStructuralChanges() {
        // No structural change can occur within a compilation unit
        // Well ... is it true ? What about the initialization order of toplevel declarations ?
        // TODO consider the declaration order of top-levels inside a compilation unit as a structural change ?
        // TODO extend this question to the order of declaration inside the initialization section : 
        //      we should check that the initialization section of a class is not changed
        // TODO more generally : where is the order of declaration important ? and when an order change can trigger compilation errors ?
        
    }
    
    shared actual AstDeclaration[] getChildren(AstNode astNode) {
        value children = ArrayList<AstDeclaration>(5, 0.5);
        object visitor extends Visitor() {
            shared actual void visit(AstDeclaration declaration) {
                assert(declaration.declarationModel.toplevel);
                children.add(declaration);
            }
        }
        astNode.visitChildren(visitor);
        return children.sequence();
    }
}
    
class TopLevelDeclarationDeltaBuilder(AstDeclaration oldNode, AstDeclaration? newNode)
        extends DeltaBuilder(oldNode, newNode) {
    
    variable value changes = ArrayList<TopLevelDeclarationDelta.PossibleChange>();
    variable value childrenDeltas = ArrayList<NestedDeclarationDelta>();
    
    shared actual TopLevelDeclarationDelta buildDelta() {
        recurse();
        object delta satisfies TopLevelDeclarationDelta {
            changedElement => oldNode.declarationModel;
            shared actual {TopLevelDeclarationDelta.PossibleChange*} changes => outer.changes;
            shared actual {NestedDeclarationDelta*} childrenDeltas => outer.childrenDeltas;
        }
        return delta;
    }
    
    shared actual void addChildDelta(AstNode oldChild, AstNode? newChild) {
        assert(is AstDeclaration oldChild, 
            is AstDeclaration? newChild, 
            ! oldChild.declarationModel.toplevel);
        value builder = NestedDeclarationDeltaBuilder(oldChild, newChild);
        childrenDeltas.add(builder.buildDelta());
    }
    
    shared actual void addMemberAddedChange(AstNode newChild) {
        assert(is AstDeclaration newChild);
        changes.add(DeclarationMemberAdded(newChild.declarationModel.nameAsString));
    }
    
    shared actual void addRemovedChange() {
        changes.add(removed);
    }
    
    shared actual void calculateStructuralChanges() {
        // No structural change can occur within a compilation unit
        // Well ... is it true ? What about the initialization order of toplevel declarations ?
        // TODO consider the declaration order of top-levels inside a compilation unit as a structural change ?
        // TODO extend this question to the order of declaration inside the initialization section : 
        //      we should check that the initialization section of a class is not changed
        // TODO more generally : where is the order of declaration important ? and when an order change can trigger compilation errors ?
    }
    
    shared actual AstDeclaration[] getChildren(AstNode astNode) {
        value children = ArrayList<AstDeclaration>(5, 0.5);
        object visitor extends Visitor() {
            shared actual void visit(AstDeclaration declaration) {
                assert(!declaration.declarationModel.toplevel);
                children.add(declaration);
            }
        }
        astNode.visitChildren(visitor);
        return children.sequence();
    }
}
    
class NestedDeclarationDeltaBuilder(AstDeclaration oldNode, AstDeclaration? newNode)
        extends DeltaBuilder(oldNode, newNode) {
    
    variable value changes = ArrayList<NestedDeclarationDelta.PossibleChange>();
    variable value childrenDeltas = ArrayList<NestedDeclarationDelta>();
    
    shared actual NestedDeclarationDelta buildDelta() {
        recurse();
        object delta satisfies NestedDeclarationDelta {
            changedElement => oldNode.declarationModel;
            shared actual {NestedDeclarationDelta.PossibleChange*} changes => outer.changes;
            shared actual {NestedDeclarationDelta*} childrenDeltas => outer.childrenDeltas;
        }
        return delta;
    }
    
    shared actual void addChildDelta(AstNode oldChild, AstNode? newChild) {
        assert(is AstDeclaration oldChild, 
            is AstDeclaration? newChild, 
            ! oldChild.declarationModel.toplevel);
        value builder = NestedDeclarationDeltaBuilder(oldChild, newChild);
        childrenDeltas.add(builder.buildDelta());
    }
    
    shared actual void addMemberAddedChange(AstNode newChild) {
        assert(is AstDeclaration newChild);
        changes.add(DeclarationMemberAdded(newChild.declarationModel.nameAsString));
    }
    
    shared actual void addRemovedChange() {
        changes.add(removed);
    }
    
    shared actual void calculateStructuralChanges() {
        // No structural change can occur within a compilation unit
        // Well ... is it true ? What about the initialization order of toplevel declarations ?
        // TODO consider the declaration order of top-levels inside a compilation unit as a structural change ?
        // TODO extend this question to the order of declaration inside the initialization section : 
        //      we should check that the initialization section of a class is not changed
        // TODO more generally : where is the order of declaration important ? and when an order change can trigger compilation errors ?
    }
    
    shared actual AstDeclaration[] getChildren(AstNode astNode) {
        value children = ArrayList<AstDeclaration>(5, 0.5);
        object visitor extends Visitor() {
            shared actual void visit(AstDeclaration declaration) {
                assert(!declaration.declarationModel.toplevel);
                children.add(declaration);
            }
        }
        astNode.visitChildren(visitor);
        return children.sequence();
    }
}
