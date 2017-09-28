package org.eclipse.ceylon.ide.eclipse.core.model;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import org.eclipse.ceylon.ide.eclipse.core.model.mirror.IBindingProvider;
import org.eclipse.ceylon.ide.eclipse.core.model.mirror.JDTMethod;
import org.eclipse.ceylon.model.loader.model.FieldValue;
import org.eclipse.ceylon.model.loader.model.JavaBeanValue;
import org.eclipse.ceylon.model.loader.model.JavaMethod;
import org.eclipse.ceylon.model.loader.model.LazyClass;
import org.eclipse.ceylon.model.loader.model.LazyFunction;
import org.eclipse.ceylon.model.loader.model.LazyInterface;
import org.eclipse.ceylon.model.loader.model.LazyValue;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Scope;

public class CeylonToJavaMatcher {
    private static class ResolvedElements {
        public ResolvedElements(
                IJavaElement[] modelElements,
                IBinding[] bindings) {
            this.modelElements = modelElements;
            this.bindings= bindings;
        }
        public IJavaElement[] modelElements;
        public IBinding[] bindings;
    }
    
    private ITypeRoot typeRoot = null;
    private SoftReference<ResolvedElements> resolvedElementsRef = new SoftReference<>(null);
    
    public CeylonToJavaMatcher(ITypeRoot typeRoot) {
        this.typeRoot = typeRoot;
    }

    private synchronized ResolvedElements resolveUnitElements(IProgressMonitor monitor) {
        ResolvedElements resolvedElements = resolvedElementsRef.get();
        if (resolvedElements == null) {
            ASTParser parser = ASTParser.newParser(AST.JLS4);
            parser.setProject(typeRoot.getJavaProject());
            List<IJavaElement> list = new LinkedList<>();
            traverseModel(typeRoot.getPrimaryElement(), list);
            IJavaElement[] modelElements = new IJavaElement[list.size()];
            modelElements = list.toArray(modelElements);
            IBinding[] bindings = parser.createBindings(modelElements, monitor);
            if (bindings != null && bindings.length == modelElements.length) {
                resolvedElements = new ResolvedElements(modelElements, bindings);
                resolvedElementsRef = new SoftReference<>(resolvedElements);
            }
        }
        return resolvedElements;
    }
    
    public IJavaElement searchInClass(Declaration ceylonDeclaration, IProgressMonitor monitor) {
            ResolvedElements resolvedElements = resolveUnitElements(monitor);
            for (IJavaElement javaElement : resolvedElements.modelElements) {
                IJavaElement result = null;
                try {
                    if (javaElement instanceof IType) {
                        result = declarationMatchesIType(ceylonDeclaration, (IType)javaElement, resolvedElements);
                    }
                    if (javaElement instanceof IMethod) {
                        result = declarationMatchesIMethod(ceylonDeclaration, (IMethod)javaElement, resolvedElements);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                if (result != null) {
                    return result;
                }
            }
        return null;
    }
    
    private void traverseModel(IJavaElement element, List<IJavaElement> elements) {
        if (element instanceof IType ||
                element instanceof IMethod) {
            elements.add(element);
        }
        if (element instanceof IParent) {
            IParent parent = (IParent) element;
            try {
                for (IJavaElement child : parent.getChildren()) {
                    traverseModel(child, elements);
                }
            } catch (JavaModelException e) {
                e.printStackTrace();
            }
        }
    }
    
    private IJavaElement declarationMatchesIType(Declaration ceylonDeclaration, IType javaType, ResolvedElements resolvedElements) {
        IBindingProvider mirror = null;
        if (ceylonDeclaration instanceof LazyClass) {
            LazyClass lazyClass = (LazyClass) ceylonDeclaration;
            if (! lazyClass.isAbstraction() && lazyClass.isOverloaded()) {
                IBindingProvider constructor = (IBindingProvider) lazyClass.getConstructor();
                if (constructor != null) {
                    mirror = constructor;
                }
            }
            if (mirror == null) {
                mirror = (IBindingProvider) lazyClass.classMirror;
            }
        }
        if (ceylonDeclaration instanceof LazyInterface) {
            mirror = (IBindingProvider) ((LazyInterface) ceylonDeclaration).classMirror; 
        }                    
        if (ceylonDeclaration instanceof LazyValue) {
            mirror = (IBindingProvider) ((LazyValue) ceylonDeclaration).classMirror; 
        }
        if (ceylonDeclaration instanceof JavaBeanValue) {
            JavaBeanValue javaBeanValue = ((JavaBeanValue) ceylonDeclaration);
            Scope container = javaBeanValue.getContainer();
            if (container instanceof LazyClass) {
                mirror = (IBindingProvider) ((LazyClass) container).classMirror;
            }
            if (container instanceof LazyInterface) {
                mirror = (IBindingProvider) ((LazyInterface) container).classMirror;
            }
            if (container instanceof LazyValue) {
                mirror = (IBindingProvider) ((LazyValue) container).classMirror; 
            }
            if (declarationMatched(javaType, mirror, resolvedElements) != null) {
                try {
                    for (IMethod javaMethod : javaType.getMethods()) {
                        if (javaMethod.getElementName().equals(javaBeanValue.getGetterName())) {
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
        if (ceylonDeclaration instanceof FieldValue) {
            FieldValue fieldValue = ((FieldValue) ceylonDeclaration);
            Scope container = fieldValue.getContainer();
            if (container instanceof LazyClass) {
                mirror = (IBindingProvider) ((LazyClass) container).classMirror;
            }
            if (container instanceof LazyInterface) {
                mirror = (IBindingProvider) ((LazyInterface) container).classMirror;
            }
            if (container instanceof LazyValue) {
                mirror = (IBindingProvider) ((LazyValue) container).classMirror; 
            }
            if (declarationMatched(javaType, mirror, resolvedElements) != null) {
                try {
                    for (IField javaField : javaType.getFields()) {
                        if (javaField.getElementName().equals(fieldValue.getRealName())) {
                            return javaField;
                        }
                    }
                } catch (JavaModelException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return null;
        }
        
        return declarationMatched(javaType, mirror, resolvedElements);
    }
    
    private IJavaElement declarationMatchesIMethod(Declaration ceylonDeclaration, IMethod javaMethod, ResolvedElements resolvedElements) {
        IBindingProvider mirror = null;
        
        if (ceylonDeclaration instanceof LazyFunction) {
            mirror = (IBindingProvider) ((LazyFunction) ceylonDeclaration).getMethodMirror();
        }
        if (ceylonDeclaration instanceof JavaMethod) {
            mirror = (IBindingProvider) ((JavaMethod) ceylonDeclaration).mirror;
        }
        return declarationMatched(javaMethod, mirror, resolvedElements);
    }
    
    private IJavaElement declarationMatched(
            IJavaElement javaElement,
            IBindingProvider mirror, 
            ResolvedElements resolvedElements) {
        if (mirror != null && resolvedElements != null) {
            IJavaElement[] modelElements = resolvedElements.modelElements;
            IBinding[] bindings = resolvedElements.bindings;
            int javaElementIndex = -1;
            for (int i = 0; i<modelElements.length; i++) {
                if (modelElements[i] == javaElement) {
                    javaElementIndex = i;
                    break;
                }
            }
            if (javaElementIndex >=0) {
                IBinding binding = bindings[javaElementIndex];
                if (binding != null) {
                    if (mirror instanceof JDTMethod && binding instanceof ITypeBinding) {
                        // Case of a constructor : let's go to the constructor and not to the type.
                        ITypeBinding typeBinding = (ITypeBinding) binding;
                        for (IMethodBinding methodBinding : typeBinding.getDeclaredMethods()) {
//                            if (methodBinding.isConstructor()) {
                            if (CharOperation.equals(methodBinding.getKey().toCharArray(), mirror.getBindingKey())) {
                                return methodBinding.getJavaElement();
                            }
//                            }
                        }
                    }
                    if (CharOperation.equals(binding.getKey().toCharArray(), mirror.getBindingKey())) {
                        return javaElement;
                    }
                }
            }
        }
        return null;
    }
}