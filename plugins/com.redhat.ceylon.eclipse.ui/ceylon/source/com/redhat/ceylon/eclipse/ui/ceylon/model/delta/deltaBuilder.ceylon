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
    Ast = Tree,
    AstAbstractNode=Node,
    Visitor
}
import ceylon.collection {
    HashSet,
    HashMap,
    MutableMap,
    MutableSet,
    ArrayList,
    MutableList
}
import com.redhat.ceylon.compiler.typechecker.model {
    ModelDeclaration = Declaration,
    ModelUnit = Unit
}
import ceylon.interop.java {
    CeylonIterable,
    javaClassFromInstance
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

alias AstNode => <Ast.Declaration | Ast.CompilationUnit | Ast.ModuleDescriptor | Ast.ImportModule | Ast.PackageDescriptor> & AstAbstractNode;

abstract class DeltaBuilder(AstNode oldNode, AstNode? newNode) {
    
    shared formal [AstNode*] getChildren(AstNode astNode);
    shared formal AbstractDelta buildDelta();
        
    shared formal void addRemovedChange();
    shared formal void calculateStructuralChanges();
    shared formal void manageChildDelta(AstNode oldChild, AstNode? newChild);
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
                        case(is Ast.Declaration) {
                            value model = child.declarationModel;
                            childKey = "``javaClassFromInstance(model).simpleName``[``model.qualifiedNameString``]";
                        }
                        case(is Ast.ModuleDescriptor) {
                            childKey = child.unit.fullPath;
                        }
                        case(is Ast.PackageDescriptor) {
                            childKey = child.unit.fullPath;
                        }
                        case(is Ast.CompilationUnit) {
                            childKey = child.unit.fullPath;
                        }
                        case(is Ast.ImportModule) {
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
                    manageChildDelta(oldChild, newChild);
                } else {
                    assert(exists newChild);
                    addMemberAddedChange(newChild);
                }
            }
        }
    }
}

class RegularCompilationUnitDeltaBuilder(Ast.CompilationUnit oldNode, Ast.CompilationUnit newNode)
        extends DeltaBuilder(oldNode, newNode) {

    variable value changes = ArrayList<RegularCompilationUnitDelta.PossibleChange>();
    variable value childrenDeltas = ArrayList<TopLevelDeclarationDelta>();
    
    shared actual RegularCompilationUnitDelta buildDelta() {
        recurse();
        object delta satisfies RegularCompilationUnitDelta {
            changedElement => oldNode.unit;
            shared actual {RegularCompilationUnitDelta.PossibleChange*} changes => outer.changes;
            shared actual {TopLevelDeclarationDelta*} childrenDeltas => outer.childrenDeltas;
            shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
        }
        return delta;
    }
    
    shared actual void manageChildDelta(AstNode oldChild, AstNode? newChild) {
        assert(is Ast.Declaration oldChild, 
                is Ast.Declaration? newChild, 
                oldChild.declarationModel.toplevel);
        value builder = TopLevelDeclarationDeltaBuilder(oldChild, newChild);
        value delta = builder.buildDelta();
        if (delta.changes.empty && childrenDeltas.empty) {
            return;
        }
        childrenDeltas.add(delta);
    }
    
    shared actual void addMemberAddedChange(AstNode newChild) {
        assert(is Ast.Declaration newChild, newChild.declarationModel.toplevel);
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
    
    shared actual Ast.Declaration[] getChildren(AstNode astNode) {
        value children = ArrayList<Ast.Declaration>(5);
        object visitor extends Visitor() {
            shared actual void visit(Ast.Declaration declaration) {
                assert(declaration.declarationModel.toplevel);
                children.add(declaration);
            }
        }
        astNode.visitChildren(visitor);
        return children.sequence();
    }
}
    
Boolean hasStructuralChanges(Ast.Declaration oldNode, Ast.Declaration newNode) {
    function lookForChanges<NodeType>(Boolean changed(NodeType oldNode, NodeType newNode))
            given NodeType satisfies AstAbstractNode {
        if (is NodeType oldNode) {
            if (is NodeType newNode) {
                if (changed(oldNode, newNode)) {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }
    
    Boolean nodeChanged(AstAbstractNode? oldNode, AstAbstractNode? newNode) {
        if(exists oldNode, exists newNode) {
            return oldNode.text != newNode.text;
        } 
        return !(oldNode is Null && newNode is Null);
    }
    
    return lookForChanges {
        function changed(Ast.Declaration oldNode, Ast.Declaration newNode) {
            assert(exists oldDeclaration = oldNode.declarationModel);
            assert(exists newDeclaration = newNode.declarationModel);
            function annotations(ModelDeclaration declaration) {
                return HashSet {
                    for (annotation in CeylonIterable(declaration.annotations)) if (annotation.name != "shared") annotation.string
                };
            }
            return any {
                annotations(oldDeclaration) != annotations(newDeclaration),
                lookForChanges {
                    function changed(Ast.TypedDeclaration oldTyped, Ast.TypedDeclaration newTyped) {
                        print(oldTyped.type?.text);
                        return any {
                            nodeChanged(oldTyped.type, newTyped.type),
                            lookForChanges {
                                function changed(Ast.AnyMethod oldMethod, Ast.AnyMethod newMethod) {
                                    print(oldMethod.typeConstraintList?.text);
                                    print(oldMethod.typeParameterList?.text);
                                    return any {
                                        nodeChanged(oldMethod.typeConstraintList, newMethod.typeConstraintList),
                                        nodeChanged(oldMethod.typeParameterList, newMethod.typeParameterList),
                                        anyPair {
                                            firstIterable => CeylonIterable(oldMethod.parameterLists);
                                            secondIterable => CeylonIterable(newMethod.parameterLists);
                                            Boolean selecting(Ast.ParameterList oldParamList, Ast.ParameterList newParamlist) {
                                                print(oldParamList.text);
                                                return nodeChanged(oldParamList, newParamlist);
                                            }
                                        }
                                    };
                                }
                            },
                            lookForChanges {
                                function changed(Ast.ObjectDefinition oldObject, Ast.ObjectDefinition newObject) {
                                    print(oldObject.extendedType?.text);
                                    print(oldObject.satisfiedTypes?.text);
                                    return any {
                                        nodeChanged(oldObject.extendedType, newObject.extendedType),
                                        nodeChanged(oldObject.satisfiedTypes, newObject.satisfiedTypes)
                                    };
                                }
                            },
                            lookForChanges {
                                function changed(Ast.Variable oldVariable, Ast.Variable newVariable) {
                                    return anyPair {
                                        firstIterable => CeylonIterable(oldVariable.parameterLists);
                                        secondIterable => CeylonIterable(newVariable.parameterLists);
                                        Boolean selecting(Ast.ParameterList oldParamList, Ast.ParameterList newParamlist) {
                                            print(oldParamList.text);
                                            return nodeChanged(oldParamList, newParamlist);
                                        }
                                    };
                                }
                            }
                        };
                    }
                },
                lookForChanges {
                    function changed(Ast.TypeDeclaration oldType, Ast.TypeDeclaration newType) {
                        print(oldType.caseTypes?.text);
                        print(oldType.satisfiedTypes?.text);
                        print(oldType.typeParameterList?.text);
                        return any {
                            nodeChanged(oldType.caseTypes, newType.caseTypes),
                            nodeChanged(oldType.satisfiedTypes, newType.satisfiedTypes),
                            nodeChanged(oldType.typeParameterList, newType.typeParameterList),
                            lookForChanges {
                                function changed(Ast.TypeParameterDeclaration oldTypeParameter, Ast.TypeParameterDeclaration newTypeParameter) {
                                    print(oldTypeParameter.typeSpecifier?.text);
                                    print(oldTypeParameter.typeVariance?.text);
                                    return any {
                                        nodeChanged(oldTypeParameter.typeSpecifier, newTypeParameter.typeSpecifier),
                                        nodeChanged(oldTypeParameter.typeVariance, newTypeParameter.typeVariance)
                                    };
                                }
                            }
                        };
                    }
                }
            };
        }
    };
}

abstract class DeclarationDeltaBuilder(Ast.Declaration oldNode, Ast.Declaration? newNode)
        of TopLevelDeclarationDeltaBuilder | NestedDeclarationDeltaBuilder
        extends DeltaBuilder(oldNode, newNode) {

    shared variable MutableList<NestedDeclarationDelta> childrenDeltas = ArrayList<NestedDeclarationDelta>();
    shared formal {ImpactingChange*} changes;

    shared actual void manageChildDelta(AstNode oldChild, AstNode? newChild) {
        assert(is Ast.Declaration oldChild, 
            is Ast.Declaration? newChild, 
            ! oldChild.declarationModel.toplevel);
        value builder = NestedDeclarationDeltaBuilder(oldChild, newChild);
        value delta = builder.buildDelta();
        if (delta.changes.empty && childrenDeltas.empty) {
            return;
        }
        childrenDeltas.add(delta);
    }
    
    shared actual Ast.Declaration[] getChildren(AstNode astNode) {
        value children = ArrayList<Ast.Declaration>(5);
        object visitor extends Visitor() {
            shared actual void visit(Ast.Declaration declaration) {
                assert(!declaration.declarationModel.toplevel);
                if (declaration.declarationModel.shared) {
                    children.add(declaration);
                }
            }
        }
        astNode.visitChildren(visitor);
        return children.sequence();
    }
}

class TopLevelDeclarationDeltaBuilder(Ast.Declaration oldNode, Ast.Declaration? newNode)
        extends DeclarationDeltaBuilder(oldNode, newNode) {
    
    variable value _changes = ArrayList<TopLevelDeclarationDelta.PossibleChange>();
    shared actual {TopLevelDeclarationDelta.PossibleChange*} changes => _changes;
    
    shared actual TopLevelDeclarationDelta buildDelta() {
        recurse();
        object delta satisfies TopLevelDeclarationDelta {
            changedElement => oldNode.declarationModel;
            shared actual {TopLevelDeclarationDelta.PossibleChange*} changes => outer.changes;
            shared actual {NestedDeclarationDelta*} childrenDeltas => outer.childrenDeltas;
            shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
        }
        return delta;
    }
    
    shared actual void addMemberAddedChange(AstNode newChild) {
        assert(is Ast.Declaration newChild);
        _changes.add(DeclarationMemberAdded(newChild.declarationModel.nameAsString));
    }
    
    shared actual void addRemovedChange() {
        _changes.add(removed);
    }
    
    shared actual void calculateStructuralChanges() {
        assert(exists newNode);

        assert(exists oldDeclaration = oldNode.declarationModel);
        assert(exists newDeclaration = newNode.declarationModel);
        if (oldDeclaration.shared && !newDeclaration.shared) {
            _changes.add(madeInvisibleOutsideScope);
        }
        if (!oldDeclaration.shared && newDeclaration.shared) {
            _changes.add(madeVisibleOutsideScope);
        }
        
        if (hasStructuralChanges(oldNode, newNode)) {
            _changes.add(structuralChange);
        }
    }
}
    
    
class NestedDeclarationDeltaBuilder(Ast.Declaration oldNode, Ast.Declaration? newNode)
        extends DeclarationDeltaBuilder(oldNode, newNode) {
    
    variable value _changes = ArrayList<NestedDeclarationDelta.PossibleChange>();
    shared actual {NestedDeclarationDelta.PossibleChange*} changes => _changes;
    
    shared actual NestedDeclarationDelta buildDelta() {
        recurse();
        object delta satisfies NestedDeclarationDelta {
            changedElement => oldNode.declarationModel;
            shared actual {NestedDeclarationDelta.PossibleChange*} changes => outer.changes;
            shared actual {NestedDeclarationDelta*} childrenDeltas => outer.childrenDeltas;
            shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
            
        }
        return delta;
    }
    
    shared actual void addMemberAddedChange(AstNode newChild) {
        assert(is Ast.Declaration newChild);
        _changes.add(DeclarationMemberAdded(newChild.declarationModel.nameAsString));
    }
    
    shared actual void addRemovedChange() {
        _changes.add(removed);
    }
    
    shared actual void calculateStructuralChanges() {
        assert(exists newNode);
        if (hasStructuralChanges(oldNode, newNode)) {
            _changes.add(structuralChange);
        }
    }
}
