import ceylon.collection {
    HashSet,
    TreeSet,
    ArrayList,
    naturalOrderTreeMap
}
import ceylon.interop.java {
    CeylonIterable
}
import ceylon.language.meta {
    type
}
import ceylon.language.meta.declaration {
    ValueDeclaration,
    ClassDeclaration
}
import ceylon.test {
    assertEquals
}

import com.redhat.ceylon.compiler.typechecker.analyzer {
    UsageWarning
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Ast=Tree,
    Message,
    Node
}
import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    DeltaBuilderFactory,
    NodeComparisonListener
}

import test.com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    CompilationUnitDeltaMockup,
    createPhasedUnit
}

alias NodeComparison => [String, String, String->String];
class NodeComparisons({NodeComparison*} elements = {}) extends TreeSet<NodeComparison>((NodeComparison x, NodeComparison y) => x.string <=> y.string, elements) {
    shared actual String string {
        value builder = StringBuilder();
        for (comparison in this) {
            value scope = comparison[0];
            value memberName = comparison[1];
            value compared = comparison[2];
            builder.append("
                                 ``scope``.<``memberName``>
                                      ``compared.key``
                                   -> ``compared.item``");
        }
        return builder.string;
    }
}

object declarationFieldFilter {
    {ValueDeclaration*} ignoredFields = {
        `value Ast.Declaration.identifier`,
        `value Ast.Declaration.declarationModel`,
        `value Ast.Declaration.compilerAnnotations`,
        `value Ast.Constructor.block`,
        `value Ast.AttributeDeclaration.specifierOrInitializerExpression`,
        `value Ast.AttributeGetterDefinition.block`,
        `value Ast.MethodDeclaration.specifierExpression`,
        `value Ast.MethodDefinition.block`,
        `value Ast.AttributeSetterDefinition.block`,
        `value Ast.AttributeSetterDefinition.specifierExpression`,
        `value Ast.ObjectDefinition.anonymousClass`,
        `value Ast.ObjectDefinition.classBody`,
        `value Ast.Variable.specifierExpression`,
        `value Ast.ClassDefinition.classBody`,
        `value Ast.InterfaceDefinition.interfaceBody`
    };

    shared Boolean isIgnored(ValueDeclaration field) {
        if (!field.container is ClassDeclaration
                    || field.container == `class Node`
                    || field.parameter
                    || field in ignoredFields) {
            return true;
        }
        assert (is ClassDeclaration decl = field.container);
        if (exists parent = decl.extendedType?.declaration,
            exists refinedField = parent.getMemberDeclaration<ValueDeclaration>(field.name)) {
            if (isIgnored(refinedField)) {
                return true;
            }
        }
        return false;
    }
}

void compare(PhasedUnit oldPhasedUnit, PhasedUnit newPhasedUnit,
    CompilationUnitDeltaMockup expectedDelta,
    Boolean printNodeComparisons = false,
    Anything({[String, String, String->String]*})? doWithNodeComparisons = null,
    DeltaBuilderFactory deltaBuilderFactory = DeltaBuilderFactory()) {
    value nodeComparisons = NodeComparisons();
    value memberCheckedByDeclaration = naturalOrderTreeMap<String,ArrayList<String>> { };
    value checkedDeclarations = naturalOrderTreeMap<String,Ast.Declaration> { };

    Boolean checkTestCompleteness;
    if (expectedDelta.changes.empty && expectedDelta.childrenDeltas.empty) {
        // we are expecting structural equivalence between the 2 AST
        // => check the completeness of the test (whether all the nodes of the various declarations have been correctly been compared
        checkTestCompleteness = true;
    } else {
        // test completeness has no meaning since checks are stop at the first encountered node difference
        checkTestCompleteness = false;
    }

    object listener satisfies NodeComparisonListener {
        shared actual void comparedDeclaration(Ast.Declaration declaration, Boolean hasStructuralChanges) {
            if (hasStructuralChanges) {
                // Only check for declaration comparison completeness if the declarations are seen as equal
                String declarationName = declaration.declarationModel.qualifiedNameString;
                checkedDeclarations.remove(declarationName);
                memberCheckedByDeclaration.remove(declarationName);
            }
        }

        shared actual void comparedNodes(String? oldSignature, String? newSignature, Ast.Declaration declaration, String memberName) {
            String declarationName = declaration.declarationModel.qualifiedNameString;
            ArrayList<String> checkedMembers;
            if (exists members = memberCheckedByDeclaration.get(declarationName)) {
                checkedMembers = members;
            } else {
                checkedDeclarations.put(declarationName, declaration);
                checkedMembers = ArrayList<String>();
                memberCheckedByDeclaration.put(declarationName, checkedMembers);
            }
            checkedMembers.add(memberName);

            if (oldSignature is Null && newSignature is Null) {
                return;
            }
            nodeComparisons.add([declarationName, memberName, (oldSignature else "<null>") -> (newSignature else "<null>")]);
        }
    }

    value delta = deltaBuilderFactory.buildDeltas(oldPhasedUnit, newPhasedUnit, listener);

    if (printNodeComparisons) {
        print("Node signature comparisons for ``oldPhasedUnit.unit.fullPath`` :");
        print(nodeComparisons);
    }
    assertEquals(delta, expectedDelta);

    if (checkTestCompleteness) {
        for (name->decl in checkedDeclarations) {
            value requiredChecks = HashSet {
                for (attr in type(decl).declaration.memberDeclarations<ValueDeclaration>())
                    if (!declarationFieldFilter.isIgnored(attr)) attr.name
            };
            value performedChecks = memberCheckedByDeclaration.get(name);
            assert (exists performedChecks);
            value missingChecks = requiredChecks.complement(HashSet { *performedChecks });
            assertEquals(missingChecks.sequence(), empty, "Some members of the declaration ' ``name`` ' were not compared.");
        }
    }

    if (exists doWithNodeComparisons) {
        doWithNodeComparisons(nodeComparisons);
    }
}

void comparePhasedUnits(String path, String oldContents, String newContents,
    CompilationUnitDeltaMockup expectedDelta,
    Boolean printNodeComparisons = false,
    Anything({[String, String, String->String]*})? doWithNodeComparisons = null) {
    value oldPasedUnit = createPhasedUnit(oldContents, path);
    assert (exists oldPasedUnit);
    assertEquals(CeylonIterable(oldPasedUnit.compilationUnit.errors)
            .filter((Message message) => !(message is UsageWarning)).sequence(), []);
    value newPhasedUnit = createPhasedUnit(newContents, path);
    assert (exists newPhasedUnit);
    assertEquals(CeylonIterable(newPhasedUnit.compilationUnit.errors)
            .filter((Message message) => !(message is UsageWarning)).sequence(), []);

    compare(oldPasedUnit, newPhasedUnit, expectedDelta, printNodeComparisons, doWithNodeComparisons);
}
