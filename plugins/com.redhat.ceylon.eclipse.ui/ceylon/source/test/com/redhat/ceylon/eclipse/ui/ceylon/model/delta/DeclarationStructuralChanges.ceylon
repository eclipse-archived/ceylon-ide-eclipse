import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    DeclarationMemberAdded,
    removed,
    structuralChange
}
import ceylon.test {
    test
}

test void addAnnotation() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "shared abstract class Test() {
                 }";
        newContents =
                "shared sealed abstract class Test() {
                 }";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = {};
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Class[Test]";
                    changes = { structuralChange };
                    childrenDeltas = {};
                }
            };
        };
    };
}

test void ignoredAnnotations() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "doc(\"my doc\")
                 by(\"David Festal\")
                 license(\"LGPL\")
                 shared abstract class Test() {
                 }";
        newContents =
                "shared abstract class Test() {
                 }";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = {};
            childrenDeltas = {};
        };
    };
}

test void removeAnnotation() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "shared abstract class Test() {
                 }";
        newContents =
                "shared class Test() {
                 }";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = {};
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Class[Test]";
                    changes = { structuralChange };
                    childrenDeltas = {};
                }
            };
        };
    };
}

test void changeAnnotationOrder() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "sealed shared abstract class Test() {
                 }";
        newContents =
                "shared sealed abstract class Test() {
                 }";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = {};
            childrenDeltas = {};
        };
    };
}

test void changeAnnotationParameter() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "shared abstract truc(true) class Test() {
                 }";
        newContents =
                "shared abstract truc(false) class Test() {
                 }";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = {};
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Class[Test]";
                    changes = { structuralChange };
                    childrenDeltas = {};
                }
            };
        };
    };
}


/*
test void change() {
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
*/