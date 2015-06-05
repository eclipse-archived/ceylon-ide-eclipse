import ceylon.test {
    test
}

import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    DeclarationMemberAdded,
    removed
}

import test.com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    comparePhasedUnits,
    NestedDeclarationDeltaMockup,
    RegularCompilationUnitDeltaMockup,
    TopLevelDeclarationDeltaMockup
}
test void addDeclarationMember() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents =
                "shared abstract class Test() {
                    shared formal void test();
                    formal void hidden();
                 }";
        newContents =
                "shared abstract class Test() {
                    shared formal void test();
                    shared formal void test2();
                    shared formal void hidden();
                 }";
        expectedDelta =
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = {};
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Class[Test]";
                    changes = { DeclarationMemberAdded("test2"),
                                DeclarationMemberAdded("hidden")};
                }
            };
        };
    };
}

test void removeDeclarationMember() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents =
                "shared abstract class Test() {
                    shared formal void test();
                    shared formal void visible();
                 }";
        newContents =
                "shared abstract class Test() {
                     formal void visible();
                 }";
        expectedDelta =
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = {};
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Class[Test]";
                    changes = {};
                    childrenDeltas = {
                        NestedDeclarationDeltaMockup {
                            changedElementString = "Function[test]";
                            changes = { removed };
                            childrenDeltas = {};
                        },
                        NestedDeclarationDeltaMockup {
                            changedElementString = "Function[visible]";
                            changes = { removed };
                            childrenDeltas = {};
                        }
                    };
                }
            };
        };
    };
}

test void changeDeclarationMemberName() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents =
                "shared abstract class Test() {
                     shared formal void test();
                 }";
        newContents =
                "shared abstract class Test() {
                     shared formal void testChanged();
                 }";
        expectedDelta =
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = {};
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Class[Test]";
                    changes = { DeclarationMemberAdded("testChanged") };
                    childrenDeltas = {
                        NestedDeclarationDeltaMockup {
                            changedElementString = "Function[test]";
                            changes = { removed };
                            childrenDeltas = {};
                        }
                    };
                }
            };
        };
    };
}
