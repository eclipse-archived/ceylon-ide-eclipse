import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    structuralChange,
    TopLevelDeclarationAdded,
    invisibleOutside
}
import ceylon.test {
    test
}

test void methodParametersChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal void test(Integer a);
                 ";
        newContents =
                "
                 shared formal void test(Integer a, Float b);
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
            assert(comparisons.contains(["dir::test", "parameterLists", 
                "ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Identifier[a]Type[ceylon.language::Integer]]]]"
                        -> "ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Identifier[a]Type[ceylon.language::Integer]]]"
                                +"ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Identifier[b]Type[ceylon.language::Float]]]]"]));
        }
    };
}

test void methodParameterNameChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal void test(Integer a);
                 ";
        newContents =
                "
                 shared formal void test(Integer aChanged);
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
            assert(comparisons.contains(["dir::test", "parameterLists", 
                "ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Identifier[a]Type[ceylon.language::Integer]]]]"
                        -> "ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Identifier[aChanged]Type[ceylon.language::Integer]]]]"]));
        }
    };
}

test void methodParameterDefaultValueAdded() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal void test(Integer a);
                 ";
        newContents =
                "
                 shared formal void test(Integer a=0);
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
            assert(comparisons.contains(["dir::test", "parameterLists", 
                "ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Identifier[a]Type[ceylon.language::Integer]]]]"
             -> "ParameterList[ValueParameterDeclaration[SpecifierExpression[Expression[NaturalLiteral[]]]AttributeDeclaration[AnnotationList[]Identifier[a]Type[ceylon.language::Integer]]]]"]));
        }
    };
}

test void methodParameterDefaultValueTypeChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal void test(Object a=0);
                 ";
        newContents =
                "
                 shared formal void test(Object a=1.0);
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
            assert(comparisons.contains(["dir::test", "parameterLists", 
                "ParameterList[ValueParameterDeclaration[SpecifierExpression[Expression[NaturalLiteral[]]]AttributeDeclaration[AnnotationList[]Identifier[a]Type[ceylon.language::Object]]]]"
             -> "ParameterList[ValueParameterDeclaration[SpecifierExpression[Expression[FloatLiteral[]]]AttributeDeclaration[AnnotationList[]Identifier[a]Type[ceylon.language::Object]]]]"]));
        }
    };
}

test void methodFunctionalParameterArgumentNameChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal void test(void functionalParameter(Integer a));
                 ";
        newContents =
                "
                 shared formal void test(void functionalParameter(Integer a2));
                 ";
        expectedDelta = 
                RegularCompilationUnitDeltaMockup {
            changedElementString = "Unit[test.ceylon]";
            changes = { };
            childrenDeltas = { };
        };
        void doWithNodeComparisons({NodeComparison*} comparisons) {
            assert(comparisons.contains(["dir::test", "parameterLists", 
                "ParameterList[FunctionalParameterDeclaration[MethodDeclaration[AnnotationList[]Identifier[functionalParameter]" 
                        + "VoidModifier[ceylon.language::Anything]ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Type[ceylon.language::Integer]]]]]]]"
             -> "ParameterList[FunctionalParameterDeclaration[MethodDeclaration[AnnotationList[]Identifier[functionalParameter]"
                        + "VoidModifier[ceylon.language::Anything]ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Type[ceylon.language::Integer]]]]]]]"]));
        }
    };
}

test void methodFunctionalParameterNameChanged() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal void test(void functionalParameter(Integer a));
                 ";
        newContents =
                "
                 shared formal void test(void functionalParameterChanged(Integer a));
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
            assert(comparisons.contains(["dir::test", "parameterLists", 
                "ParameterList[FunctionalParameterDeclaration[MethodDeclaration[AnnotationList[]Identifier[functionalParameter]VoidModifier[ceylon.language::Anything]" 
                    + "ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Type[ceylon.language::Integer]]]]]]]"
             -> "ParameterList[FunctionalParameterDeclaration[MethodDeclaration[AnnotationList[]Identifier[functionalParameterChanged]VoidModifier[ceylon.language::Anything]"
                    + "ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Type[ceylon.language::Integer]]]]]]]"]));
        }
    };
}

// For the moment we consider these 2 case as different, though externally it might be seen as the same
test void methodEquivalentFunctionalParameter() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
                "
                 shared formal void test(Anything functionalParameter(Integer a));
                 ";
        newContents =
                "
                 shared formal void test(Anything(Integer) functionalParameter);
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
            assert(comparisons.contains(["dir::test", "parameterLists", 
                "ParameterList[FunctionalParameterDeclaration[MethodDeclaration[AnnotationList[]Identifier[functionalParameter]Type[ceylon.language::Anything]" 
                    + "ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Type[ceylon.language::Integer]]]]]]]"
             -> "ParameterList[ValueParameterDeclaration[AttributeDeclaration[AnnotationList[]Identifier[functionalParameter]"
                    + "Type[ceylon.language::Anything(ceylon.language::Integer)]]]]"]));
        }
    };
}
