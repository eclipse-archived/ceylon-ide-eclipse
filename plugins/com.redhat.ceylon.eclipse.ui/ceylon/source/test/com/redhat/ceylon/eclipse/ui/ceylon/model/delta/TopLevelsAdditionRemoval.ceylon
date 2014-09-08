import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    buildDeltas,
    DeclarationMemberAdded,
    removed,
    NodeComparisonListener,
    TopLevelDeclarationAdded
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

test void addTopLevel() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "shared void test() {}
                 ";
        newContents =
                "shared void test() {}
                 shared void test2() {}
                 void hidden()
                 ";
        expectedDelta = 
            RegularCompilationUnitDeltaMockup {
                changedElementString = "Unit[test.ceylon]";
                changes = { TopLevelDeclarationAdded("test2", true),
                            TopLevelDeclarationAdded("hidden", false)};
                childrenDeltas = {};
            };
    };
}

test void removeTopLevel() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents =
                "shared void test() {}
                 void hidden() {}
                 ";
        newContents = 
                "
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = {};
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Method[test]";
                    changes = { removed };
                },
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Method[hidden]";
                    changes = { removed };
                }
            };
        };
    };
}

test void changeToplevelName() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "shared void test();
                 ";
        newContents =
                "shared void testChanged()
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { TopLevelDeclarationAdded("testChanged", true) };
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Method[test]";
                    changes = { removed };
                    childrenDeltas = {};
                }
            };
        };
    };
}
