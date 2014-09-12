import ceylon.test {
    test
}

import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    removed,
    TopLevelDeclarationAdded,
    visibleOutside,
    invisibleOutside
}

import test.com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    comparePhasedUnits,
    RegularCompilationUnitDeltaMockup,
    TopLevelDeclarationDeltaMockup
}

test void addTopLevel() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared void test() {}
                 ";
        newContents =
                "
                 shared void test() {}
                 shared void test2() {}
                 void hidden() {}
                 ";
        expectedDelta = 
            RegularCompilationUnitDeltaMockup {
                changedElementString = "Unit[test.ceylon]";
                changes = { TopLevelDeclarationAdded("test2", visibleOutside),
                            TopLevelDeclarationAdded("hidden", invisibleOutside)};
                childrenDeltas = {};
            };
    };
}

test void removeTopLevel() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents =
                "
                 shared void test() {}
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
                "
                 shared void test() {}
                 ";
        newContents =
                "
                 shared void testChanged() {}
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { TopLevelDeclarationAdded("testChanged", visibleOutside) };
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
