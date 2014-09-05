import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    buildDeltas,
    DeclarationMemberAdded,
    removed,
    NodeComparisonListener
}
import ceylon.test {
    test,
    assertEquals
}
import ceylon.collection {
    HashSet
}
import com.redhat.ceylon.compiler.typechecker.model {
    Declaration
}
import test.com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    NestedDeclarationDeltaMockup,
    CompilationUnitDeltaMockup,
    RegularCompilationUnitDeltaMockup,
    TopLevelDeclarationDeltaMockup,
    createPhasedUnit
}

void comparePhasedUnits(String path, String oldContents, String newContents, 
                        CompilationUnitDeltaMockup expectedDelta,
                        Boolean printNodeComparisons = false, 
                        Set<[String,String, String->String]>? expectedNodeComparisons = null) {
    value oldPasedUnit = createPhasedUnit(oldContents, path);
    value newPasedUnit = createPhasedUnit(newContents, path);

    value nodeComparisons = HashSet<[String,String, String->String]>();

    void listener(String? oldSignature, String? newSignature, Declaration scope, String memberName) {
        if (oldSignature is Null && newSignature is Null) {
            return;
        }
        nodeComparisons.add([scope.qualifiedNameString, memberName, (oldSignature else "<null>") -> (newSignature else "<null>")]);
    }
    
    value needsListener = expectedNodeComparisons exists || printNodeComparisons;
    
    value delta = buildDeltas(oldPasedUnit, newPasedUnit, needsListener then listener else null);
    
    if (printNodeComparisons) {
        print("Node signature comparisons for ``path`` :");
        for (comparison in nodeComparisons) {
            value scope = comparison[0];
            value memberName = comparison[1];
            value compared = comparison[2];
            print("  ``scope``.<``memberName``>
                          ``compared.key``
                       -> ``compared.item``");
        }
        print("");
    }
    
    assertEquals(delta, expectedDelta);
    if (exists expectedNodeComparisons) {
        assertEquals(nodeComparisons, expectedNodeComparisons);
    }
}

test void firstTest() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
            "shared abstract class Test() {
                shared formal List<Set<Integer>> test0<Type>(Boolean functional(Integer arg), Type a);
                shared formal Iterable<Float, Null> test();
                shared formal void test2();
             }";
        newContents =
            "shared abstract class Test() {
                shared formal List<Set<Integer>> test0<Type>(Boolean functional(Integer arg1), Float|Integer a);
                shared formal {<Float> * } test();
                shared formal void test3();
             }";
        expectedDelta = 
            RegularCompilationUnitDeltaMockup {
                changedElementString = "Unit[test.ceylon]";
                changes = {};
                childrenDeltas = {
                    TopLevelDeclarationDeltaMockup {
                        changedElementString = "Class[Test]";
                        changes = { DeclarationMemberAdded("test3") };
                        childrenDeltas = {
                            NestedDeclarationDeltaMockup {
                                changedElementString = "Method[test]";
                                changes = { removed };
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
        expectedNodeComparisons = emptySet;
    };
}