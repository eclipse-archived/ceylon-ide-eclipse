import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    buildDeltas,
    DeclarationMemberAdded,
    removed
}
import ceylon.test {
    test,
    assertEquals
}

void comparePhasedUnits(String path, String oldContents, String newContents, CompilationUnitDeltaMockup expectedDelta) {
    value oldPasedUnit = createPhasedUnit(oldContents, path);
    value newPasedUnit = createPhasedUnit(newContents, path);
    value delta = buildDeltas(oldPasedUnit, newPasedUnit);
    print(delta);
    print(expectedDelta);
    assertEquals(delta, expectedDelta);
}

test void firstTest() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
            "shared abstract class Test() {
                shared formal List<Set<Integer>> test0(Boolean functional(Integer arg) functional, Integer a);
                shared formal {Integer*} test();
                shared formal void test2();
             }";
        newContents =
            "shared abstract class Test() {
                shared formal List<Set<Integer>> test0(Boolean functional(Integer arg1) functional, Float|Integer a);
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
    };
}