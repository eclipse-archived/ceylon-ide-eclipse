import ceylon.collection {
    HashSet,
    StringBuilder,
    TreeSet,
    ArrayList,
    HashMap
}
import ceylon.test {
    test,
    assertEquals
}

import com.redhat.ceylon.compiler.typechecker.model {
    Declaration
}
import com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    buildDeltas,
    DeclarationMemberAdded,
    removed,
    structuralChange,
    TopLevelDeclarationAdded,
    invisibleOutside
}

import test.com.redhat.ceylon.eclipse.ui.ceylon.model.delta {
    NestedDeclarationDeltaMockup,
    CompilationUnitDeltaMockup,
    RegularCompilationUnitDeltaMockup,
    TopLevelDeclarationDeltaMockup,
    createPhasedUnit
}
import ceylon.interop.java {
    CeylonIterable
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Ast=Tree,
    Message
}
import com.redhat.ceylon.compiler.typechecker.analyzer {
    UsageWarning
}
import ceylon.language.meta.declaration {
    ValueDeclaration
}
import ceylon.language.meta {
    type
}

alias NodeComparison => [String,String, String->String];
Comparison compare(NodeComparison x, NodeComparison y) => x.string.compare(y.string);

class NodeComparisons({NodeComparison*} elements = {}) extends TreeSet<NodeComparison>(compare, elements) {
    shared actual String string {
        value builder = StringBuilder();
        for (comparison in this) {
            value scope = comparison[0];
            value memberName = comparison[1];
            value compared = comparison[2];
            builder.append("
                                 ``scope``.<``memberName``>
                                      ``compared.key``
                                   -> ``compared.item``");
        }
        return builder.string;
    }
}

{ValueDeclaration+} ignoredDeclarationfields = { 
    `value Ast.Declaration.identifier`,
    `value Ast.Declaration.declarationModel`,
    `value Ast.Declaration.compilerAnnotations`,
    `value Ast.Declaration.children`,
    `value Ast.Declaration.endToken`,
    `value Ast.Declaration.errors`,
    `value Ast.Declaration.location`,
    `value Ast.Declaration.mainEndToken`,
    `value Ast.Declaration.mainToken`,
    `value Ast.Declaration.missingToken`,
    `value Ast.Declaration.nodeType`,
    `value Ast.Declaration.scope`,
    `value Ast.Declaration.startIndex`,
    `value Ast.Declaration.stopIndex`,
    `value Ast.Declaration.string`,
    `value Ast.Declaration.text`,
    `value Ast.Declaration.unit`,
    `value Ast.Declaration.hash`,
    `value Ast.AttributeDeclaration.specifierOrInitializerExpression`,
    `value Ast.AttributeGetterDefinition.block`,
    `value Ast.MethodDeclaration.specifierExpression`,
    `value Ast.MethodDefinition.block`,
    `value Ast.AttributeSetterDefinition.block`,
    `value Ast.AttributeSetterDefinition.specifierExpression`,
    `value Ast.ObjectDefinition.anonymousClass`,
    `value Ast.ObjectDefinition.classBody`,
    `value Ast.Variable.specifierExpression`,
    `value Ast.ClassDefinition.classBody`,
    `value Ast.InterfaceDefinition.interfaceBody`
};

void comparePhasedUnits(String path, String oldContents, String newContents, 
                        CompilationUnitDeltaMockup expectedDelta,
                        Boolean printNodeComparisons = false, 
                        Set<[String,String, String->String]>? expectedNodeComparisons = null) {
    value oldPasedUnit = createPhasedUnit(oldContents, path);
    assert(exists oldPasedUnit);
    assertEquals(CeylonIterable(oldPasedUnit.compilationUnit.errors)
                       .filter((Message message) => !(message is UsageWarning)).sequence(), []);
    value newPasedUnit = createPhasedUnit(newContents, path);
    assert(exists newPasedUnit);
    assertEquals(CeylonIterable(newPasedUnit.compilationUnit.errors)
        .filter((Message message) => !(message is UsageWarning)).sequence(), []);

    value nodeComparisons = NodeComparisons();
    value memberCheckedByDeclaration = HashMap<String, ArrayList<String>>();
    value checkedDeclarations = HashMap<String, Ast.Declaration>();
    
    void listener(String? oldSignature, String? newSignature, Ast.Declaration declaration, String memberName) {
        
        String declarationName = declaration.declarationModel.qualifiedNameString;
        ArrayList<String> checkedMembers;
        if (exists members = memberCheckedByDeclaration.get(declarationName)) {
            checkedMembers = members;
        } else {
            checkedDeclarations.put(declarationName, declaration);
            checkedMembers = ArrayList<String>();
            memberCheckedByDeclaration.put(declarationName, checkedMembers);
        }
        checkedMembers.add(memberName);
        
        if (oldSignature is Null && newSignature is Null) {
            return;
        }
        nodeComparisons.add([declarationName, memberName, (oldSignature else "<null>") -> (newSignature else "<null>")]);
    }
    
    value delta = buildDeltas(oldPasedUnit, newPasedUnit, listener);
    assertEquals(delta, expectedDelta);
    
    if (printNodeComparisons) {
        print("Node signature comparisons for ``path`` :");
        print(nodeComparisons);
    }
    
    for (name -> decl in checkedDeclarations) {
        value requiredChecks = HashSet {
            for (attr in type(decl).declaration.declaredMemberDeclarations<ValueDeclaration>()) 
                if (! attr in ignoredDeclarationfields) attr.name
        };
        value performedChecks = memberCheckedByDeclaration.get(name);
        assert(exists performedChecks);
        requiredChecks.removeAll(performedChecks);
        assertEquals(requiredChecks.sequence(), empty, "Some members of the declaration ' ``name`` ' were not compared.");
    }

    if (exists expectedNodeComparisons) {
        assertEquals(nodeComparisons, expectedNodeComparisons);
    }
}

test void firstTest() {
    comparePhasedUnits {
        path = "dir/test.ceylon";
        oldContents = 
            "
             shared abstract class Test() {
                shared formal List<Set<Integer>> test0<Type>(Boolean functional(Integer arg), Type|Integer a);
                shared formal Iterable<Float, Null> test();
                shared formal void test2();
             }
             ";
        newContents =
            "
             class Integer {
             }
             
             shared abstract class Test() {
                shared formal List<Set<Integer>> test0<Type>(Boolean functional(Integer arg1), Type|Integer a);
                shared formal {<Float> * } test();
                shared formal void test3();
             }
             ";
        expectedDelta = 
            RegularCompilationUnitDeltaMockup {
                changedElementString = "Unit[test.ceylon]";
                changes = { TopLevelDeclarationAdded ("Integer", invisibleOutside) };
                childrenDeltas = {
                    TopLevelDeclarationDeltaMockup {
                        changedElementString = "Class[Test]";
                        changes = { DeclarationMemberAdded("test3") };
                        childrenDeltas = {
                            NestedDeclarationDeltaMockup {
                                changedElementString = "Method[test0]";
                                changes = { structuralChange };
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
        printNodeComparisons = true;
    };
}