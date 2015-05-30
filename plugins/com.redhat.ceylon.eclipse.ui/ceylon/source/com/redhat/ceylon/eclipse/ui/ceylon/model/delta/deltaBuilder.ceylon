import ceylon.collection {
    HashSet,
    HashMap,
    MutableMap,
    ArrayList,
    MutableList,
    TreeSet
}
import ceylon.interop.java {
    CeylonIterable,
    javaClassFromInstance
}

import com.redhat.ceylon.model.typechecker.util {
    ModuleManager {
        moduleDescriptorFileName=MODULE_FILE,
        packageDescriptorFileName=PACKAGE_FILE
    },
    ProducedTypeNamePrinter
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    AnalysisError
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit,
    TypecheckerUnit
}
import com.redhat.ceylon.model.typechecker.model {
    ModelDeclaration=Declaration,
    Function,
    ModuleImport,
    Module
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Ast=Tree,
    AstAbstractNode=Node,
    Visitor,
    VisitorAdaptor,
    Util {
        formatPath
    },
    Message,
    NaturalVisitor
}
import java.util {
    JList=List
}
import ceylon.language.meta {
    typeLiteral
}

shared interface NodeComparisonListener {
    shared formal void comparedNodes(String? oldNode, String? newNode, Ast.Declaration declaration, String attribute);
    shared formal void comparedDeclaration(Ast.Declaration declaration, Boolean hasStructuralChanges);
}

shared class DeltaBuilderFactory(
    Boolean compareAnalysisErrors = false) {

    object producedTypeNamePrinter extends ProducedTypeNamePrinter(true, true, true, true, false) {
        printQualifier() => true;
        printFullyQualified() => true;
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
        PhasedUnit changedPhasedUnit,
        "Listener that registers the detail of every structural node comparisons"
        NodeComparisonListener? nodeComparisonListener = null) {

        assert (exists unitFile = referencePhasedUnit.unitFile);
        if (unitFile.name == moduleDescriptorFileName) {
            return buildModuleDescriptorDeltas(referencePhasedUnit, changedPhasedUnit, nodeComparisonListener);
        }

        if (unitFile.name == packageDescriptorFileName) {
            return buildPackageDescriptorDeltas(referencePhasedUnit, changedPhasedUnit, nodeComparisonListener);
        }

        return buildCompilationUnitDeltas(referencePhasedUnit, changedPhasedUnit, nodeComparisonListener);
    }

    ModuleDescriptorDelta buildModuleDescriptorDeltas(PhasedUnit referencePhasedUnit, PhasedUnit changedPhasedUnit, NodeComparisonListener? nodeComparisonListener) {
        if(exists oldDescriptor = referencePhasedUnit.compilationUnit?.moduleDescriptors?.get(0)) {
            value builder = ModuleDescriptorDeltaBuilder(oldDescriptor, changedPhasedUnit.compilationUnit?.moduleDescriptors?.get(0), nodeComparisonListener);
            return builder.buildDelta();
        } else {
            return InvalidModuleDescriptorDelta();
        }
    }

    PackageDescriptorDelta buildPackageDescriptorDeltas(PhasedUnit referencePhasedUnit, PhasedUnit changedPhasedUnit, NodeComparisonListener? nodeComparisonListener) {
        if (exists oldDescriptor = referencePhasedUnit.compilationUnit?.packageDescriptors?.get(0)) {
            value builder = PackageDescriptorDeltaBuilder(oldDescriptor, changedPhasedUnit.compilationUnit?.packageDescriptors?.get(0), nodeComparisonListener);
            return builder.buildDelta();
        } else {
            return InvalidPackageDescriptorDelta();
        }
    }

    RegularCompilationUnitDelta buildCompilationUnitDeltas(PhasedUnit referencePhasedUnit, PhasedUnit changedPhasedUnit, NodeComparisonListener? nodeComparisonListener) {
        value builder = RegularCompilationUnitDeltaBuilder(referencePhasedUnit.compilationUnit, changedPhasedUnit.compilationUnit, nodeComparisonListener);
        return builder.buildDelta();
    }

    String importedModuleName(Ast.ImportModule child) {
        Ast.ImportPath? importPath = child.importPath;
        Ast.QuotedLiteral? quotedLitteral = child.quotedLiteral;
        String moduleName;
        if (exists quotedLitteral) {
            moduleName = quotedLitteral.text;
        } else {
            if (exists importPath) {
                moduleName = formatPath(importPath.identifiers);
            } else {
                moduleName = "<unknown>";
            }
        }
        return moduleName;
    }

    "Compares two message lists to see if they have the same errors.

     Because error messages contain identifiers which might have been aliased,
     it's difficult to tell if two errors are really the same error.
     At the moment, to avoid false positives, we consider any two message lists
     containing at least one error to be different...

     ... unless the [[compareAnalysisErrors]] parameter is true.

     (This method isn't useless, though: It ignores other kinds of messages.)"
    Boolean errorListsEquals(JList<Message> these, JList<Message> those)
        => let (theseAnalysisErrors = TreeSet(byIncreasing(Message.message), CeylonIterable(these).filter((element) => element is AnalysisError)),
                        thoseAnalysisErrors = TreeSet(byIncreasing(Message.message), CeylonIterable(those).filter((element) => element is AnalysisError)))
                    if (compareAnalysisErrors)
                        then theseAnalysisErrors == thoseAnalysisErrors
                        else theseAnalysisErrors.size == 0 && thoseAnalysisErrors.size == 0;

    alias AstNode => <Ast.Declaration | Ast.CompilationUnit | Ast.ModuleDescriptor | Ast.ImportModule | Ast.PackageDescriptor> & AstAbstractNode;

    abstract class DeltaBuilder(AstNode oldNode, AstNode? newNode) {

        shared formal [AstNode*] getChildren(AstNode astNode);
        shared formal AbstractDelta buildDelta();

        shared formal void registerRemovedChange();
        shared formal void calculateLocalChanges();
        shared formal void manageChildDelta(AstNode oldChild, AstNode? newChild);
        shared formal void registerMemberAddedChange(AstNode newChild);

        shared default void recurse() {
            if (newNode is Null) {
                registerRemovedChange();
                return;
            }
            assert(exists newNode);

            calculateLocalChanges();

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
                                childKey = importedModuleName(child) + "/" + (child.version?.text?.trim('"'.equals) else "<unknown>");
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
                        registerMemberAddedChange(newChild);
                    }
                }
            }
        }
    }

    class PackageDescriptorDeltaBuilder(Ast.PackageDescriptor oldNode, Ast.PackageDescriptor? newNode, NodeComparisonListener? nodeComparisonListener)
            extends DeltaBuilder(oldNode, newNode) {
        variable PackageDescriptorDelta.PossibleChange? change = null;

        shared actual PackageDescriptorDelta buildDelta() {
            recurse();
            object delta satisfies PackageDescriptorDelta {
                changedElement => oldNode.unit.\ipackage;
                shared actual [PackageDescriptorDelta.PossibleChange]|[] changes {
                    if (exists existingChange = change) {
                        return [existingChange];
                    } else {
                        return empty;
                    }
                }
                shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
            }
            return delta;
        }

        shared actual void manageChildDelta(AstNode oldChild, AstNode? newChild) {
            assert(false);
        }

        shared actual void registerMemberAddedChange(AstNode newChild) {
            assert(false);
        }

        shared actual void registerRemovedChange() {
            assert(false);
        }

        shared actual void calculateLocalChanges() {
            assert(exists newNode);
            if (formatPath(oldNode.importPath.identifiers) != formatPath(newNode.importPath.identifiers)) {
                change = structuralChange;
                return;
            }

            function isShared(Ast.PackageDescriptor descriptor)
                    => Util.hasAnnotation(descriptor.annotationList, "shared", descriptor.unit);

            value sharedBefore = isShared(oldNode);
            value sharedNow = isShared(newNode);

            if (sharedBefore && !sharedNow) {
                change = madeInvisibleOutsideScope;
            }
            if (!sharedBefore && sharedNow) {
                change = madeVisibleOutsideScope;
            }
        }

        shared actual Ast.Declaration[] getChildren(AstNode astNode)
            => empty;
    }

    function sameBackend([Ast.AnnotationList, TypecheckerUnit] oldNode, [Ast.AnnotationList, TypecheckerUnit] newNode)
            => let (String? oldNative = Util.getNativeBackend(*oldNode),
                    String? newNative = Util.getNativeBackend(*newNode))
                    if (exists oldNative, exists newNative)
                    then oldNative == newNative
                    else
                        if (oldNative is Null && newNative is Null)
                        then true
                        else false;

    class ModuleDescriptorDeltaBuilder(Ast.ModuleDescriptor oldNode, Ast.ModuleDescriptor? newNode, NodeComparisonListener? nodeComparisonListener)
            extends DeltaBuilder(oldNode, newNode) {
        variable value changes = ArrayList<ModuleDescriptorDelta.PossibleChange>();
        variable value childrenDeltas = ArrayList<ModuleImportDelta>();
        Module? oldModule;
        if (is Module model = oldNode.importPath?.model) {
            oldModule = model;
        } else {
            oldModule = null;
        }

        shared actual ModuleDescriptorDelta buildDelta() {
            recurse();
            object delta satisfies ModuleDescriptorDelta {
                changedElement => oldModule;
                shared actual {ModuleDescriptorDelta.PossibleChange*} changes => outer.changes;
                shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
                shared actual {ModuleImportDelta*} childrenDeltas => outer.childrenDeltas;

            }
            return delta;
        }

        shared actual void manageChildDelta(AstNode oldChild, AstNode? newChild) {
            assert(is Ast.ImportModule oldChild,
                is Ast.ImportModule? newChild,
                exists oldModule);
            value builder = ModuleImportDeclarationDeltaBuilder(oldChild, newChild, oldModule, nodeComparisonListener);
            value delta = builder.buildDelta();
            if (delta.changes.empty && delta.childrenDeltas.empty) {
                return;
            }
            childrenDeltas.add(delta);
        }

        shared actual void registerMemberAddedChange(AstNode newChild) {
            assert(is Ast.ImportModule newChild);
            changes.add(ModuleImportAdded(
                importedModuleName(newChild),
                newChild.version.text.trim('"'.equals),
                Util.hasAnnotation(newChild.annotationList, "shared", newChild.unit)
                then visibleOutside else invisibleOutside
            ));
        }

        shared actual void registerRemovedChange() {
            assert(false);
        }

        shared actual void calculateLocalChanges() {
            assert(exists newNode);
            if (any {
                oldNode.version.text != newNode.version.text,
                formatPath(oldNode.importPath.identifiers) != formatPath(newNode.importPath.identifiers),
                !sameBackend(
                    [oldNode.annotationList, oldNode.unit],
                    [newNode.annotationList, newNode.unit])
            }) {
                changes.add(structuralChange);
                return;
            }
        }

        shared actual Ast.ImportModule[] getChildren(AstNode astNode) {
            if (changes.contains(structuralChange)) {
                return [];
            }
            assert(is Ast.ModuleDescriptor astNode);
            return CeylonIterable(astNode.importModuleList.importModules).sequence();
        }
    }

    class ModuleImportDeclarationDeltaBuilder(Ast.ImportModule oldNode, Ast.ImportModule? newNode, Module oldParentModule, NodeComparisonListener? nodeComparisonListener)
            extends DeltaBuilder(oldNode, newNode) {

        variable ModuleImportDelta.PossibleChange? change = null;

        shared actual ModuleImportDelta buildDelta() {
            recurse();
            object delta satisfies ModuleImportDelta {
                shared actual ModuleImport changedElement {
                    value moduleImport = CeylonIterable(oldParentModule.imports).find {
                        Boolean selecting(ModuleImport element) {
                            value modelName = element.\imodule.nameAsString;
                            value modelVersion = element.\imodule.version;
                            value astName = importedModuleName(oldNode);
                            value astVersion = oldNode.version.text.trim('"'.equals);

                            return  modelName == astName &&
                                    modelVersion == astVersion;
                        }
                    };
                    assert (exists moduleImport);
                    return moduleImport;
                }
                shared actual [ModuleImportDelta.PossibleChange]|[] changes {
                    if (exists existingChange = change) {
                        return [existingChange];
                    } else {
                        return empty;
                    }
                }
                shared actual Boolean equals(Object that) => (super of AbstractDelta).equals(that);
                shared actual String changedElementString => "ModuleImport[``changedElement.\imodule.nameAsString``, ``changedElement.\imodule.version``]";
            }
            return delta;
        }

        shared actual void calculateLocalChanges() {
            assert(exists newNode);

            function isOptional(Ast.ImportModule descriptor)
                    => Util.hasAnnotation(descriptor.annotationList, "optional", descriptor.unit);


            if (any{
                    isOptional(oldNode) != isOptional(newNode),
                    !sameBackend(
                        [oldNode.annotationList, oldNode.unit],
                        [newNode.annotationList, newNode.unit])
                }) {
                change = structuralChange;
                return;
            }

            function isShared(Ast.ImportModule descriptor)
                    => Util.hasAnnotation(descriptor.annotationList, "shared", descriptor.unit);

            value sharedBefore = isShared(oldNode);
            value sharedNow = isShared(newNode);

            if (sharedBefore && !sharedNow) {
                change = madeInvisibleOutsideScope;
            }
            if (!sharedBefore && sharedNow) {
                change = madeVisibleOutsideScope;
            }
        }

        shared actual void manageChildDelta(AstNode oldChild, AstNode? newChild) {
            assert(false);
        }

        shared actual void registerMemberAddedChange(AstNode newChild) {
            assert(false);
        }

        shared actual void registerRemovedChange() {
            change = removed;
        }

        shared actual AstNode[] getChildren(AstNode astNode)
            => empty;
    }

    class RegularCompilationUnitDeltaBuilder(Ast.CompilationUnit oldNode, Ast.CompilationUnit newNode, NodeComparisonListener? nodeComparisonListener)
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
            value builder = TopLevelDeclarationDeltaBuilder(oldChild, newChild, nodeComparisonListener);
            value delta = builder.buildDelta();
            if (delta.changes.empty && delta.childrenDeltas.empty) {
                return;
            }
            childrenDeltas.add(delta);
        }

        shared actual void registerMemberAddedChange(AstNode newChild) {
            assert(is Ast.Declaration newChild, newChild.declarationModel.toplevel);
            changes.add(TopLevelDeclarationAdded(
                newChild.declarationModel.nameAsString,
                newChild.declarationModel.shared
                then visibleOutside else invisibleOutside));
        }

        shared actual void registerRemovedChange() {
            "A compilation unit cannot be removed from a PhasedUnit"
            assert(false);
        }

        shared actual void calculateLocalChanges() {
            // No structural change can occur within a compilation unit
            // Well ... is it true ? What about the initialization order of toplevel declarations ?
            // TODO consider the declaration order of top-levels inside a compilation unit as a structural change ?
            // TODO extend this question to the order of declaration inside the initialization section :
            //      we should check that the initialization section of a class is not changed
            // TODO more generally : where is the order of declaration important ? and when an order change can trigger compilation errors ?

        }

        shared actual Ast.Declaration[] getChildren(AstNode astNode) {
            value children = ArrayList<Ast.Declaration>(5);
            object visitor extends Visitor() satisfies NaturalVisitor {
                shared actual void visitAny(AstAbstractNode? node) {
                    if (is Ast.Declaration declaration = node) {
                        assert(declaration.declarationModel.toplevel);
                        children.add(declaration);
                    } else {
                        super.visitAny(node);
                    }
                }
            }
            astNode.visitChildren(visitor);
            return children.sequence();
        }
    }

    abstract class DeclarationDeltaBuilder(Ast.Declaration oldNode, Ast.Declaration? newNode, NodeComparisonListener? nodeComparisonListener)
            of TopLevelDeclarationDeltaBuilder | NestedDeclarationDeltaBuilder
            extends DeltaBuilder(oldNode, newNode) {

        shared variable MutableList<NestedDeclarationDelta> childrenDeltas = ArrayList<NestedDeclarationDelta>();
        shared formal {ImpactingChange*} changes;

        shared actual void manageChildDelta(AstNode oldChild, AstNode? newChild) {
            assert(is Ast.Declaration oldChild,
                is Ast.Declaration? newChild,
                ! oldChild.declarationModel.toplevel);
            value builder = NestedDeclarationDeltaBuilder(oldChild, newChild, nodeComparisonListener);
            value delta = builder.buildDelta();
            if (delta.changes.empty && delta.childrenDeltas.empty) {
                return;
            }
            childrenDeltas.add(delta);
        }

        shared actual Ast.Declaration[] getChildren(AstNode astNode) {
            value children = ArrayList<Ast.Declaration>(5);
            object visitor extends Visitor() satisfies NaturalVisitor {
                shared actual void visitAny(AstAbstractNode? node) {
                    if (is Ast.Declaration declaration = node) {
                        assert(!declaration.declarationModel.toplevel);
                        if (declaration.declarationModel.shared) {
                            children.add(declaration);
                        }
                    } else {
                        super.visitAny(node);
                    }
                }
            }
            astNode.visitChildren(visitor);
            return children.sequence();
        }

        shared Boolean hasStructuralChanges(Ast.Declaration oldAstDeclaration, Ast.Declaration newAstDeclaration, NodeComparisonListener? listener) {

            ModelDeclaration? identifierToDeclaration(Ast.Identifier id)
                    => id.unit?.getImport(Util.name(id))?.declaration;

            object nodeSigner extends VisitorAdaptor() {
                variable value builder = StringBuilder();
                variable Boolean mustSearchForIndentifierDeclaration = false;

                shared String sign(AstAbstractNode node) {
                    builder = StringBuilder();
                    mustSearchForIndentifierDeclaration = false;
                    node.visit(this);
                    return builder.string;
                }

                void enclose(String title, void action()) {
                    builder.append("``title``[");
                    action();
                    builder.append("]");
                }

                shared actual void visitType(Ast.Type node) {
                    enclose {
                        title => (node is Ast.StaticType) then "Type" else node.nodeType;
                        void action() {
                            if (exists type = node.typeModel) {
                                builder.append(producedTypeNamePrinter.getProducedTypeName(type, node.unit));
                            }
                        }
                    };
                }

                shared actual void visitAny(AstAbstractNode node) {
                    Visitor v = this;
                    enclose {
                        title => node.nodeType;
                        void action() {
                            node.visitChildren(v);
                        }
                    };
                }

                shared actual void visitStaticMemberOrTypeExpression(Ast.StaticMemberOrTypeExpression node) {
                    mustSearchForIndentifierDeclaration = true;
                    super.visitStaticMemberOrTypeExpression(node);
                    mustSearchForIndentifierDeclaration = false;
                }

                shared actual void visitIdentifier(Ast.Identifier node) {
                    if (is Function method = node.scope,
                        method.parameter,
                        method.nameAsString != node.text) {
                        // parameters of a method functional parameter are not
                        // part of the externally visible structure of the outer method
                        return;
                    }
                    enclose {
                        title = node.nodeType;
                        void action() {
                            variable value identifier = node.text;
                            if (mustSearchForIndentifierDeclaration) {
                                mustSearchForIndentifierDeclaration = false;
                                if (exists decl = identifierToDeclaration(node)) {
                                    identifier = decl.qualifiedNameString;
                                } else {
                                    if (exists decl = node.unit?.\ipackage?.getMemberOrParameter(node.unit, identifier, null, false)) {
                                        identifier = decl.qualifiedNameString;
                                    }
                                }
                            }
                            builder.append(identifier);
                        }
                    };
                }
            }

            String annotationName(Ast.Annotation annot) {
                assert (is Ast.BaseMemberExpression primary = annot.primary);
                value identifier = primary.identifier;
                value declaration = identifierToDeclaration(identifier);
                if (exists declaration) {
                    return declaration.name;
                }
                return Util.name(identifier);
            }

            Set<String> annotationsAsStringSet(Ast.AnnotationList annotationList) {
                return TreeSet {
                    compare = (String x, String y) => x.compare(y);
                    for (annotation in CeylonIterable(annotationList.annotations))
                    if (! ["shared", "license", "by", "see", "doc"].contains(annotationName(annotation))) nodeSigner.sign(annotation)
                };
            }

            Boolean nodesDiffer(AstAbstractNode? oldNode, AstAbstractNode? newNode, String declarationMemberName) {
                Boolean changed;
                if(exists oldNode, exists newNode) {
                    String oldSignature = nodeSigner.sign(oldNode);
                    String newSignature = nodeSigner.sign(newNode);
                    listener?.comparedNodes(oldSignature, newSignature, oldAstDeclaration, declarationMemberName);
                    changed = oldSignature != newSignature || !errorListsEquals(oldNode.errors, newNode.errors);
                } else {
                    changed = !(oldNode is Null && newNode is Null);
                    if (exists listener) {
                        variable String? oldSignature = null;
                        variable String? newSignature = null;
                        if (exists oldNode) {
                            oldSignature = nodeSigner.sign(oldNode);
                        }
                        if (exists newNode) {
                            newSignature = nodeSigner.sign(newNode);
                        }
                        listener.comparedNodes(oldSignature, newSignature, oldAstDeclaration, declarationMemberName);
                    }
                }
                return changed;
            }

            function lookForChanges<NodeType>(Boolean between(NodeType oldNode, NodeType newNode))
                    given NodeType satisfies Ast.Declaration {
                if (is NodeType oldAstDeclaration) {
                    if (is NodeType newAstDeclaration) {
                        return between(oldAstDeclaration, newAstDeclaration);
                    } else {
                        // There are changes since the declaration type is not the same
                        return true;
                    }
                }
                assert(! typeLiteral<NodeType>().exactly(typeLiteral<Nothing>()));
                // Don't search For Changes
                return false;
            }

            Boolean hasChanges = lookForChanges<Ast.Declaration> {
                function between(Ast.Declaration oldNode, Ast.Declaration newNode) {
                    assert(exists oldDeclaration = oldNode.declarationModel);
                    assert(exists newDeclaration = newNode.declarationModel);
                    value oldAnnotations = annotationsAsStringSet(oldNode.annotationList);
                    value newAnnotations = annotationsAsStringSet(newNode.annotationList);
                    listener?.comparedNodes(oldAnnotations.string, newAnnotations.string, oldNode, "annotationList");
                    return any {
                        oldAnnotations != newAnnotations,
                        !errorListsEquals(oldNode.errors, newNode.errors),
                        lookForChanges<Ast.Constructor> {
                            function between(Ast.Constructor oldConstructor, Ast.Constructor newConstructor) {
                                return any {
                                    nodesDiffer(oldConstructor.delegatedConstructor, newConstructor.delegatedConstructor, "delegatedConstructor"),
                                    nodesDiffer(oldConstructor.parameterList, newConstructor.parameterList, "parameterList")
                                };
                            }
                        },
                        lookForChanges<Ast.TypedDeclaration> {
                            function between(Ast.TypedDeclaration oldTyped, Ast.TypedDeclaration newTyped) {
                                return any {
                                    nodesDiffer(oldTyped.type, newTyped.type, "type"),
                                    lookForChanges<Ast.AnyMethod> {
                                        function between(Ast.AnyMethod oldMethod, Ast.AnyMethod newMethod) {
                                            return any {
                                                nodesDiffer(oldMethod.typeConstraintList, newMethod.typeConstraintList, "typeConstraintList"),
                                                nodesDiffer(oldMethod.typeParameterList, newMethod.typeParameterList, "typeParameterList"),
                                                oldMethod.parameterLists.size() != newMethod.parameterLists.size(),
                                                anyPair {
                                                    firstIterable => CeylonIterable(oldMethod.parameterLists);
                                                    secondIterable => CeylonIterable(newMethod.parameterLists);
                                                    Boolean selecting(Ast.ParameterList oldParamList, Ast.ParameterList newParamlist) {
                                                        return nodesDiffer(oldParamList, newParamlist, "parameterLists");
                                                    }
                                                }
                                            };
                                        }
                                    },
                                    lookForChanges<Ast.ObjectDefinition> {
                                        function between(Ast.ObjectDefinition oldObject, Ast.ObjectDefinition newObject) {
                                            return any {
                                                nodesDiffer(oldObject.extendedType, newObject.extendedType, "extendedType"),
                                                nodesDiffer(oldObject.satisfiedTypes, newObject.satisfiedTypes, "satisfiedTypes")
                                            };
                                        }
                                    },
                                    lookForChanges<Ast.Variable> {
                                        function between(Ast.Variable oldVariable, Ast.Variable newVariable) {
                                            return any {
                                                oldVariable.parameterLists.size() != oldVariable.parameterLists.size(),
                                                anyPair {
                                                    firstIterable => CeylonIterable(oldVariable.parameterLists);
                                                    secondIterable => CeylonIterable(newVariable.parameterLists);
                                                    Boolean selecting(Ast.ParameterList oldParamList, Ast.ParameterList newParamlist) {
                                                        return nodesDiffer(oldParamList, newParamlist,"parameterLists");
                                                    }
                                                }
                                            };
                                        }
                                    }
                                };
                            }
                        },
                        lookForChanges<Ast.TypeDeclaration> {
                            function between(Ast.TypeDeclaration oldType, Ast.TypeDeclaration newType) {
                                return any {
                                    nodesDiffer(oldType.caseTypes, newType.caseTypes, "caseTypes"),
                                    nodesDiffer(oldType.satisfiedTypes, newType.satisfiedTypes, "satisfiedTypes"),
                                    nodesDiffer(oldType.typeParameterList, newType.typeParameterList, "typeParameterList"),
                                    lookForChanges<Ast.ClassOrInterface> {
                                        function between(Ast.ClassOrInterface oldClassOrInterface, Ast.ClassOrInterface newClassOrInterface) {
                                            return any {
                                                nodesDiffer(oldClassOrInterface.typeConstraintList, newClassOrInterface.typeConstraintList, "typeConstraintList"),
                                                lookForChanges<Ast.AnyClass> {
                                                    function between(Ast.AnyClass oldClass, Ast.AnyClass newClass) {
                                                        return any {
                                                            nodesDiffer(oldClass.extendedType, newClass.extendedType, "extendedType"),
                                                            nodesDiffer(oldClass.parameterList, newClass.parameterList, "parameterList"),
                                                            lookForChanges<Ast.ClassDeclaration> {
                                                                function between(Ast.ClassDeclaration oldClassDecl, Ast.ClassDeclaration newClassDecl) {
                                                                    return any {
                                                                        nodesDiffer(oldClassDecl.classSpecifier, newClassDecl.classSpecifier, "classSpecifier")
                                                                    };
                                                                }
                                                            }
                                                        };
                                                    }
                                                },
                                                lookForChanges<Ast.AnyInterface> {
                                                    function between(Ast.AnyInterface oldInterface, Ast.AnyInterface newInterface) {
                                                        return any {
                                                            lookForChanges<Ast.InterfaceDeclaration> {
                                                                function between(Ast.InterfaceDeclaration oldInterfaceDecl, Ast.InterfaceDeclaration newInterfaceDecl) {
                                                                    return any {
                                                                        nodesDiffer(oldInterfaceDecl.typeSpecifier, newInterfaceDecl.typeSpecifier, "typeSpecifier")
                                                                    };
                                                                }
                                                            },
                                                            lookForChanges<Ast.InterfaceDefinition> {
                                                                function between(Ast.InterfaceDefinition oldInterface, Ast.InterfaceDefinition newInterface) {
                                                                    listener?.comparedNodes(oldInterface.\idynamic.string, newInterface.\idynamic.string, oldNode, "dynamic");
                                                                    return oldInterface.\idynamic != newInterface.\idynamic;
                                                                }
                                                            }
                                                        };
                                                    }
                                                }
                                            };
                                        }
                                    },
                                    lookForChanges<Ast.TypeAliasDeclaration> {
                                        function between(Ast.TypeAliasDeclaration oldTypeAliasDeclaration, Ast.TypeAliasDeclaration newTypeAliasDeclaration) {
                                            return any {
                                                nodesDiffer(oldTypeAliasDeclaration.typeConstraintList, newTypeAliasDeclaration.typeConstraintList, "typeConstraintList"),
                                                nodesDiffer(oldTypeAliasDeclaration.typeSpecifier, newTypeAliasDeclaration.typeSpecifier, "typeSpecifier")
                                            };
                                        }
                                    },
                                    lookForChanges<Ast.TypeConstraint> {
                                        function between(Ast.TypeConstraint oldTypeConstraint, Ast.TypeConstraint newTypeConstraint) {
                                            return any {
                                                nodesDiffer(oldTypeConstraint.abstractedType, newTypeConstraint.abstractedType, "abstractedType"),
                                                nodesDiffer(oldTypeConstraint.parameterList, newTypeConstraint.parameterList, "parameterList")
                                            };
                                        }
                                    }
                                };
                            }
                        },
                        lookForChanges<Ast.TypeParameterDeclaration> {
                            function between(Ast.TypeParameterDeclaration oldTypeParameter, Ast.TypeParameterDeclaration newTypeParameter) {
                                return any {
                                    nodesDiffer(oldTypeParameter.typeSpecifier, newTypeParameter.typeSpecifier, "typeSpecifier"),
                                    nodesDiffer(oldTypeParameter.typeVariance, newTypeParameter.typeVariance, "typeVariance")
                                };
                            }
                        }
                    };
                }
            };

            listener?.comparedDeclaration(oldAstDeclaration, hasChanges);
            return hasChanges;
        }
    }

    class TopLevelDeclarationDeltaBuilder(Ast.Declaration oldNode, Ast.Declaration? newNode, NodeComparisonListener? nodeComparisonListener)
            extends DeclarationDeltaBuilder(oldNode, newNode, nodeComparisonListener) {

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

        shared actual void registerMemberAddedChange(AstNode newChild) {
            assert(is Ast.Declaration newChild);
            _changes.add(DeclarationMemberAdded(newChild.declarationModel.nameAsString));
        }

        shared actual void registerRemovedChange() {
            _changes.add(removed);
        }

        shared actual void calculateLocalChanges() {
            assert(exists newNode);

            assert(exists oldDeclaration = oldNode.declarationModel);
            assert(exists newDeclaration = newNode.declarationModel);
            if (oldDeclaration.shared && !newDeclaration.shared) {
                _changes.add(madeInvisibleOutsideScope);
            }
            if (!oldDeclaration.shared && newDeclaration.shared) {
                _changes.add(madeVisibleOutsideScope);
            }

            if (hasStructuralChanges(oldNode, newNode, nodeComparisonListener)) {
                _changes.add(structuralChange);
            }
        }
    }


    class NestedDeclarationDeltaBuilder(Ast.Declaration oldNode, Ast.Declaration? newNode, NodeComparisonListener? nodeComparisonListener)
            extends DeclarationDeltaBuilder(oldNode, newNode, nodeComparisonListener) {

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

        shared actual void registerMemberAddedChange(AstNode newChild) {
            assert(is Ast.Declaration newChild);
            _changes.add(DeclarationMemberAdded(newChild.declarationModel.nameAsString));
        }

        shared actual void registerRemovedChange() {
            _changes.add(removed);
        }

        shared actual void calculateLocalChanges() {
            assert(exists newNode);
            if (hasStructuralChanges(oldNode, newNode, nodeComparisonListener)) {
                _changes.add(structuralChange);
            }
        }
    }
}


