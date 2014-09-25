import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    structuralChange,
    TopLevelDeclarationAdded,
    invisibleOutside
}
import ceylon.test {
    test
}
import test.com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    comparePhasedUnits,
    RegularCompilationUnitDeltaMockup,
    NodeComparison,
    TopLevelDeclarationDeltaMockup
}

test void simpleTypeChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Float test();
                 ";
        newContents =
                "
                 shared formal Integer test();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[ceylon.language::Float]" -> "Type[ceylon.language::Integer]"]));
        }
    };
}

test void parametrizedTypeChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Comparable<Float> test();
                 ";
        newContents =
                "
                 shared formal Comparable<Integer> test();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[ceylon.language::Comparable<ceylon.language::Float>]" 
                        -> "Type[ceylon.language::Comparable<ceylon.language::Integer>]"]));
        }
    };
}

test void aliasedTypeRecognized() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Integer test();
                 ";
        newContents =
                "
                 class Integer() {}
                 shared formal Integer test();
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { TopLevelDeclarationAdded ("Integer", invisibleOutside) };
            childrenDeltas = {
                TopLevelDeclarationDeltaMockup {
                    changedElementString = "Method[test]";
                    changes = { structuralChange };
                    childrenDeltas = {};
                }
            };
        };
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[ceylon.language::Integer]" 
                        -> "Type[dir::Integer]"]));
        }
    };
}

test void equivalentTypeNamesChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Iterable<Float  > test();
                 ";
        newContents =
                "
                 shared formal {Float*} test();
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { };
            childrenDeltas = { };
        };
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[{ceylon.language::Float*}]" 
                        -> "Type[{ceylon.language::Float*}]"]));
        }
    };
}

test void genericTypeChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Comparable<Type> test<Type>();
                 ";
        newContents =
                "
                 shared formal Comparable<Type2> test<Type2>();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[ceylon.language::Comparable<Type>]" 
                        -> "Type[ceylon.language::Comparable<Type2>]"]));
        }
    };
}

test void voidTypeChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal void test();
                 ";
        newContents =
                "
                 shared formal Anything test();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "VoidModifier[ceylon.language::Anything]" 
                        -> "Type[ceylon.language::Anything]"]));
        }
    };
}

test void typeMadeOptional() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Integer test();
                 ";
        newContents =
                "
                 shared formal Integer? test();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[ceylon.language::Integer]" 
                        -> "Type[ceylon.language::Integer?]"]));
        }
    };
}

"For the moment, since the comparison is based on the fully-qualified 
 ProducedType name, union of types given in a different order is considered a 
 structural change"
test void unionOrderChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Integer|Float test();
                 ";
        newContents =
                "
                 shared formal Float|Integer test();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[ceylon.language::Integer|ceylon.language::Float]" 
                        -> "Type[ceylon.language::Float|ceylon.language::Integer]"]));
        }
    };
}

"For the moment, since the comparison is based on the fully-qualified 
 ProducedType name, intersection of types given in a different order is considered a 
 structural change"
test void intersectionOrderChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Integer&Object test();
                 ";
        newContents =
                "
                 shared formal Object&Integer test();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[ceylon.language::Integer&ceylon.language::Object]" 
                        -> "Type[ceylon.language::Object&ceylon.language::Integer]"]));
        }
    };
}

test void defaultedTypeAdded() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal [Integer, Integer] test();
                 ";
        newContents =
                "
                 shared formal [Integer, Integer=] test();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[[ceylon.language::Integer, ceylon.language::Integer]]"
                        -> "Type[[ceylon.language::Integer, ceylon.language::Integer=]]"]));
        }
    };
}

test void useSiteVarianceAdded() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Array<Character[]> test();
                 ";
        newContents =
                "
                 shared formal Array<out Character[]> test();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[ceylon.language::Array<ceylon.language::Character[]>]"
                        -> "Type[ceylon.language::Array<out ceylon.language::Character[]>]"]));
        }
    };
}

test void useSiteVarianceFlipped() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal Array<in Character[]> test();
                 ";
        newContents =
                "
                 shared formal Array<out Character[]> test();
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
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "type", 
                "Type[ceylon.language::Array<in ceylon.language::Character[]>]"
                        -> "Type[ceylon.language::Array<out ceylon.language::Character[]>]"]));
        }
    };
}
