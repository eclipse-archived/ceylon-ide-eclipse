import ceylon.collection {
    LinkedList,
    MutableList
}
import ceylon.interop.java {
    createJavaObjectArray,
    javaString
}

import com.redhat.ceylon.eclipse.core.model.mirror {
    IBindingProvider,
    JDTMethod
}
import com.redhat.ceylon.eclipse.util {
    withJavaModel
}
import com.redhat.ceylon.ide.common.model {
    IJavaModelAware
}
import com.redhat.ceylon.ide.common.util {
    ProgressMonitor,
    synchronize,
    equalsWithNulls,
    BaseProgressMonitor
}
import com.redhat.ceylon.model.loader.model {
    LazyClass,
    LazyInterface,
    LazyValue,
    JavaBeanValue,
    FieldValue,
    LazyFunction,
    JavaMethod
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Scope
}

import java.lang {
    ObjectArray
}
import java.lang.ref {
    SoftReference
}

import org.eclipse.core.resources {
    IProject
}
import org.eclipse.core.runtime {
    IProgressMonitor
}
import org.eclipse.jdt.core {
    ITypeRoot,
    IJavaElement,
    IType,
    IMethod,
    IParent,
    JavaModelException,
    IField
}
import org.eclipse.jdt.core.compiler {
    CharOperation
}
import org.eclipse.jdt.core.dom {
    ASTParser,
    AST,
    IBinding,
    ITypeBinding
}

shared interface EclipseJavaModelAware 
        satisfies IJavaModelAware<IProject, ITypeRoot, IJavaElement> {
    shared actual IJavaElement? toJavaElement(Declaration ceylonDeclaration, BaseProgressMonitor? monitor) {
        assert(is ProgressMonitor<IProgressMonitor>? monitor);
        return searchInClass(ceylonDeclaration, monitor);
    }
    
    shared actual IProject javaClassRootToNativeProject(ITypeRoot javaClassRoot) =>
            javaClassRoot.javaProject.project;

    shared interface ResolvedElements {
        shared formal ObjectArray<IJavaElement> modelElements;
        shared formal ObjectArray<IBinding> bindings;
    }
    
    shared formal variable SoftReference<ResolvedElements> resolvedElementsRef;

    ResolvedElements resolveUnitElements(ProgressMonitor<IProgressMonitor>? monitor) =>
            synchronize {
                on = this;
                function do() {
                    ResolvedElements? oldResolvedElements = resolvedElementsRef.get();
                    if (exists oldResolvedElements) {
                        return oldResolvedElements;
                    } else {
                        ASTParser parser = ASTParser.newParser(AST.\iJLS4);
                        parser.setProject(typeRoot.javaProject);
                        value list = LinkedList<IJavaElement>();
                        traverseModel(typeRoot.primaryElement, list);
                        value theModelElements = createJavaObjectArray(list);
                        value theBindings = parser.createBindings(theModelElements, monitor?.Progress(1000, null)?.newChild(1000)?.wrapped);
                        assert (theBindings.size == theModelElements.size);
                        value newResolvedElements = object satisfies ResolvedElements {
                            modelElements => theModelElements;
                            bindings => theBindings;
                        };
                        resolvedElementsRef = SoftReference<ResolvedElements>(newResolvedElements);
                        return newResolvedElements;
                    }
                }
            };
    
    shared IJavaElement? searchInClass(Declaration ceylonDeclaration, ProgressMonitor<IProgressMonitor>? monitor) {
        value resolvedElements = resolveUnitElements(monitor);
        for (javaElement in resolvedElements.modelElements.array.coalesced) {
            variable IJavaElement? result = null;
            try {
                if (is IType javaElement) {
                    result = declarationMatchesIType(ceylonDeclaration, javaElement, resolvedElements);
                }
                if (is IMethod javaElement ) {
                    result = declarationMatchesIMethod(ceylonDeclaration, javaElement, resolvedElements);
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            if (exists existingResult = result) {
                return existingResult;
            }
        }
        return null;
    }
    
    void traverseModel(IJavaElement element, MutableList<IJavaElement> elements) {
        if (is IType | IMethod element) {
            elements.add(element);
        }
        if (is IParent element) {
            try {
                for (child in element.children.array.coalesced) {
                    traverseModel(child, elements);
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
    }
    
    IJavaElement? declarationMatchesIType(Declaration ceylonDeclaration, IType javaType, ResolvedElements resolvedElements) {
        variable IBindingProvider? mirror = null;
        if (is LazyClass ceylonDeclaration) {
            if (! ceylonDeclaration.abstraction
                && ceylonDeclaration.overloaded) {
                assert (is IBindingProvider? constructor = ceylonDeclaration.constructor);
                if (exists constructor) {
                    mirror = constructor;
                }
            }
            if (! mirror exists) {
                assert(is IBindingProvider classMirror = ceylonDeclaration.classMirror);
                mirror = classMirror;
            }
        }
        if (is LazyInterface ceylonDeclaration) {
            assert(is IBindingProvider classMirror = ceylonDeclaration.classMirror);
            mirror = classMirror; 
        }                    
        if (is LazyValue ceylonDeclaration) {
            assert(is IBindingProvider classMirror = ceylonDeclaration.classMirror);
            mirror = classMirror; 
        }
        if (is JavaBeanValue ceylonDeclaration) {
            Scope container = ceylonDeclaration.container;
            if (is LazyClass container) {
                assert(is IBindingProvider classMirror = container.classMirror);
                mirror = classMirror;
            }
            if (is LazyInterface container) {
                assert(is IBindingProvider classMirror = container.classMirror);
                mirror = classMirror;
            }
            if (is LazyValue container) {
                assert(is IBindingProvider classMirror = container.classMirror);
                mirror = classMirror; 
            }
            if (declarationMatched(javaType, mirror, resolvedElements) exists) {
                try {
                    for (javaMethod in javaType.methods.array.coalesced) {
                        if (equalsWithNulls(javaMethod.elementName, ceylonDeclaration.getterName)) {
                            return javaMethod;
                        }
                    }
                } catch (JavaModelException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }
        if (is FieldValue ceylonDeclaration) {
            Scope container = ceylonDeclaration.container;
            if (is LazyClass container) {
                assert(is IBindingProvider classMirror = container.classMirror);
                mirror = classMirror;
            }
            if (is LazyInterface container) {
                assert(is IBindingProvider classMirror = container.classMirror);
                mirror = classMirror;
            }
            if (is LazyValue container) {
                assert(is IBindingProvider classMirror = container.classMirror);
                mirror = classMirror; 
            }
            if (declarationMatched(javaType, mirror, resolvedElements) exists) {
                return withJavaModel {
                    do() =>
                            javaType.fields.array
                            .coalesced
                            .find((IField javaField) 
                                        => equalsWithNulls(javaField.elementName, ceylonDeclaration.realName));
                };
            }
            return null;
        }
        
        return declarationMatched(javaType, mirror, resolvedElements);
    }
    
    IJavaElement? declarationMatchesIMethod(Declaration ceylonDeclaration, IMethod javaMethod, ResolvedElements resolvedElements) {
        variable IBindingProvider? mirror = null;
        
        if (is LazyFunction ceylonDeclaration) {
            assert(is IBindingProvider methodMirror = ceylonDeclaration.methodMirror);
            mirror = methodMirror;
        }
        if (is JavaMethod ceylonDeclaration) {
            assert(is IBindingProvider methodMirror = ceylonDeclaration.mirror);
            mirror = methodMirror;
        }
        return declarationMatched(javaMethod, mirror, resolvedElements);
    }
    
    IJavaElement? declarationMatched(
        IJavaElement javaElement,
        IBindingProvider? mirror, 
        ResolvedElements resolvedElements) {
        if (exists mirror, is Identifiable javaElement) {
            ObjectArray<IJavaElement> modelElements = resolvedElements.modelElements;
            ObjectArray<IBinding> bindings = resolvedElements.bindings;
            variable Integer javaElementIndex = -1;
            for (i in 0 : modelElements.size) {
                if (exists modelElement = modelElements.get(i)) {
                    assert(is Identifiable modelElement);
                    if (modelElement === javaElement) {
                        javaElementIndex = i;
                        break;
                    }
                }
            }
            if (javaElementIndex >=0) {
                IBinding? binding = bindings.get(javaElementIndex);
                if (exists binding) {
                    if (is JDTMethod  mirror, is ITypeBinding binding) {
                        // Case of a constructor : let's go to the constructor and not to the type.
                        for (methodBinding in binding.declaredMethods.array.coalesced) {
                            //                            if (methodBinding.isConstructor()) {
                            if (CharOperation.equals(javaString(methodBinding.key).toCharArray(), mirror.bindingKey)) {
                                return methodBinding.javaElement;
                            }
                            //                            }
                        }
                    }
                    if (CharOperation.equals(javaString(binding.key).toCharArray(), mirror.bindingKey)) {
                        return javaElement;
                    }
                }
            }
        }
        return null;
    }

}