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
    assertEquals(delta, expectedDelta);
}

test void firstTest() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
            "shared class Test() {
                shared void test();
                shared void test2();
             }
             ";
        newContents =
            "shared class Test() {
                void test();
                shared void test3();
             }
             ";
        expectedDelta = 
            RegularCompilationUnitDeltaMockup {
                changedElementString = "Unit[test.ceylon]";
                changes = [];
                childrenDeltas = {
                    TopLevelDeclarationDeltaMockup {
                        changedElementString = "Class[Test]";
                        changes = { DeclarationMemberAdded("test3") };
                        childrenDeltas = {
                            NestedDeclarationDeltaMockup {
                                changedElementString = "Method[Test.test:Anything]";
                                changes = { removed };
                                childrenDeltas = {};
                            },
                            NestedDeclarationDeltaMockup {
                                changedElementString = "Method[Test.test2:Anything]";
                                changes = { removed };
                                childrenDeltas = {};
                            }
                        };
                    }
                };
            };
    };
}