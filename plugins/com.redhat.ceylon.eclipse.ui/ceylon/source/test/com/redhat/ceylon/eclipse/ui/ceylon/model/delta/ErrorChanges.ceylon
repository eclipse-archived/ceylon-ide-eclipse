import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    structuralChange
}
import ceylon.test {
    test
}
import test.com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    comparePhasedUnits,
    RegularCompilationUnitDeltaMockup,
    TopLevelDeclarationDeltaMockup
}

test void annotationErrorAdded() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared void test() {}
                 ";
        newContents =
                "
                 shared unknownAnnotation void test() {}
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { };
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Method[test]";
                    changes = { structuralChange };
                    childrenDeltas = {};
                }
            };
        };
    };
}

test void annotationErrorChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared unknownAnnotation void test() {}
                 ";
        newContents =
                "
                 shared otherUnknownAnnotation void test() {}
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { };
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Method[test]";
                    changes = { structuralChange };
                    childrenDeltas = {};
                }
            };
        };
    };
}

test void typeErrorAdded() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared Anything test() => nothing;
                 ";
        newContents =
                "
                 shared UnknownType test() => nothing;
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { };
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Method[test]";
                    changes = { structuralChange };
                    childrenDeltas = {};
                }
            };
        };
    };
}

test void typeErrorChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared UnknownType test() => nothing;
                 ";
        newContents =
                "
                 shared OtherUnknownType test() => nothing;
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { };
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Method[test]";
                    changes = { structuralChange };
                    childrenDeltas = {};
                }
            };
        };
    };
}

test void innerErrorAdded() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared void test() {
                     print(nothing);
                 }
                 ";
        newContents =
                "
                 shared void test() {
                     print(unknown);
                 }
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { };
            childrenDeltas = { };
        };
    };
}
