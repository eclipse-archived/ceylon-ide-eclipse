import ceylon.collection {
    HashSet,
    StringBuilder,
    TreeSet,
    ArrayList,
    naturalOrderTreeMap
}
import ceylon.test {
    test,
    assertEquals
}

import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    buildDeltas,
    DeclarationMemberAdded,
    removed,
    structuralChange,
    TopLevelDeclarationAdded,
    invisibleOutside,
    NodeComparisonListener
}

import test.com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    NestedDeclarationDeltaMockup,
    CompilationUnitDeltaMockup,
    RegularCompilationUnitDeltaMockup,
    TopLevelDeclarationDeltaMockup,
    createPhasedUnit
}
import ceylon.interop.java {
    CeylonIterable
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Ast=Tree,
    Message,
    Node
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    UsageWarning
}
import ceylon.language.meta.declaration {
    ValueDeclaration,
    ClassDeclaration
}
import ceylon.language.meta {
    type
}

alias NodeComparison => [String,String, String->String];
class NodeComparisons({NodeComparison*} elements = {}) extends TreeSet<NodeComparison>((NodeComparison x,NodeComparison y)=>x.string <=> y.string, elements) {
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
        if (! field.container is ClassDeclaration
         || field.container == `class Node`
         || field.parameter
         || field in ignoredFields) {
            return true; 
        }
        assert(is ClassDeclaration decl=field.container);
        if (exists parent = decl.extendedType?.declaration,
            exists refinedField = parent.getMemberDeclaration<ValueDeclaration>(field.name)) {
            if (isIgnored(refinedField)) {
                return true;
            }
        }
        return false;
    }
}

void comparePhasedUnits(String path, String oldContents, String newContents, 
                        CompilationUnitDeltaMockup expectedDelta,
                        Boolean printNodeComparisons = false, 
                        Set<[String,String, String->String]>? expectedNodeComparisons = null) {
    value oldPasedUnit = createPhasedUnit(oldContents, path);
    assert(exists oldPasedUnit);
    assertEquals(CeylonIterable(oldPasedUnit.compilationUnit.errors)
                       .filter((Message message) => !(message is UsageWarning)).sequence(), []);
    value newPasedUnit = createPhasedUnit(newContents, path);
    assert(exists newPasedUnit);
    assertEquals(CeylonIterable(newPasedUnit.compilationUnit.errors)
        .filter((Message message) => !(message is UsageWarning)).sequence(), []);

    value nodeComparisons = NodeComparisons();
    value memberCheckedByDeclaration = naturalOrderTreeMap<String, ArrayList<String>>{};
    value checkedDeclarations = naturalOrderTreeMap<String, Ast.Declaration>{};
    
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
    
    value delta = buildDeltas(oldPasedUnit, newPasedUnit, listener);
    assertEquals(delta, expectedDelta);
    
    if (printNodeComparisons) {
        print("Node signature comparisons for ``path`` :");
        print(nodeComparisons);
    }
    if (checkTestCompleteness) {
        for (name -> decl in checkedDeclarations) {
            value requiredChecks = HashSet {
                for (attr in type(decl).declaration.memberDeclarations<ValueDeclaration>()) 
                if (!declarationFieldFilter.isIgnored(attr)) attr.name
            };
            value performedChecks = memberCheckedByDeclaration.get(name);
            assert(exists performedChecks);
            value missingChecks = requiredChecks.complement(HashSet { *performedChecks });
            assertEquals(missingChecks.sequence(), empty, "Some members of the declaration ' ``name`` ' were not compared.");
        }
    }

    if (exists expectedNodeComparisons) {
        assertEquals(nodeComparisons, expectedNodeComparisons);
    }
}

test void firstTest() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
            "
             shared abstract class Test() {
                shared formal List<Set<Integer>> test0<Type>(Boolean functional(Integer arg), Type|Integer a);
                shared formal Iterable<Float, Null> test();
                shared formal void test2();
             }
             ";
        newContents =
            "
             class Integer {
             }
             
             shared abstract class Test() {
                shared formal List<Set<Integer>> test0<Type>(Boolean functional(Integer arg1), Type|Integer a);
                shared formal {<Float> * } test();
                shared formal void test3();
             }
             ";
        expectedDelta = 
            RegularCompilationUnitDeltaMockup {
                changedElementString = "Unit[test.ceylon]";
                changes = { TopLevelDeclarationAdded ("Integer", invisibleOutside) };
                childrenDeltas = {
                    TopLevelDeclarationDeltaMockup {
                        changedElementString = "Class[Test]";
                        changes = { DeclarationMemberAdded("test3") };
                        childrenDeltas = {
                            NestedDeclarationDeltaMockup {
                                changedElementString = "Method[test0]";
                                changes = { structuralChange };
                                childrenDeltas = {};
                            },
                            NestedDeclarationDeltaMockup {
                                changedElementString = "Method[test2]";
                                changes = { removed };
                                childrenDeltas = {};
                            }
                        };
                    }
                };
            };
        printNodeComparisons = true;
    };
}