package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
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
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

import com.redhat.ceylon.compiler.loader.model.JavaBeanValue;
import com.redhat.ceylon.compiler.loader.model.JavaMethod;
import com.redhat.ceylon.compiler.loader.model.LazyClass;
import com.redhat.ceylon.compiler.loader.model.LazyInterface;
import com.redhat.ceylon.compiler.loader.model.LazyMethod;
import com.redhat.ceylon.compiler.loader.model.LazyValue;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.eclipse.core.model.loader.IBindingProvider;

public class CeylonToJavaMatcher {
    private Declaration ceylonDeclaration = null;
    private final ITypeRoot typeRoot;
    private final ASTParser parser;
    
    public CeylonToJavaMatcher(IJavaModelAware unit) {
        typeRoot = unit.getTypeRoot();
        parser = ASTParser.newParser(AST.JLS4);
    }

    public IJavaElement searchInClass(Declaration ceylonDeclaration) {
        try {
            this.ceylonDeclaration = ceylonDeclaration;
            return visit(typeRoot.getPrimaryElement());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private IJavaElement visit(IJavaElement element) {
        if (element instanceof IType) {
            return visit((IType) element);
        }
        if (element instanceof IMethod) {
            return visit((IMethod) element);
        }
        if (element instanceof IParent) {
            return visit((IParent) element);
        }
        return null;
    }
    
    private IJavaElement visit(IType javaType) {
        IBindingProvider mirror = null;
        if (ceylonDeclaration instanceof LazyClass) {
            LazyClass lazyClass = (LazyClass) ceylonDeclaration;
            if (! lazyClass.isAbstraction() && lazyClass.isOverloaded()) {
                IBindingProvider constructor = (IBindingProvider) lazyClass.getConstructor();
                if (constructor != null) {
                    return visit((IParent)javaType); //this will be managed in the IType element  
                }
            }
            mirror = (IBindingProvider) lazyClass.classMirror;
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
            if (ceylonDeclaration instanceof LazyValue) {
                mirror = (IBindingProvider) ((LazyValue) container).classMirror; 
            }
            if (declarationMatched(javaType, mirror)) {
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
            return visit((IParent)javaType);
        }
        if (declarationMatched(javaType, mirror)) {
            return javaType;
        }
        return visit((IParent)javaType);
    }
    
    private IJavaElement visit(IMethod javaMethod) {
        IBindingProvider mirror = null;
        
        try {
            if (javaMethod.isConstructor() && ceylonDeclaration instanceof LazyClass) {
                LazyClass lazyClass = (LazyClass) ceylonDeclaration;
                if (! lazyClass.isAbstraction() && lazyClass.isOverloaded()) {
                    IBindingProvider constructor = (IBindingProvider) lazyClass.getConstructor();
                    if (constructor != null) {
                        mirror = constructor;
                    }
                }
            }
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (ceylonDeclaration instanceof LazyMethod) {
            mirror = (IBindingProvider) ((LazyMethod) ceylonDeclaration).getMethodMirror();
        }
        if (ceylonDeclaration instanceof JavaMethod) {
            mirror = (IBindingProvider) ((JavaMethod) ceylonDeclaration).mirror;
        }
        if (declarationMatched(javaMethod, mirror)) {
            return javaMethod;
        }

        return visit((IParent)javaMethod);
    }
    
    private IJavaElement visit(IParent parent) {
        try {
            for (IJavaElement child : parent.getChildren()) {
                IJavaElement elementFound = visit(child);
                if (elementFound != null) {
                    return elementFound;
                }
            }
        } catch (JavaModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private boolean declarationMatched(IJavaElement javaElement,
            IBindingProvider mirror) {
        if (mirror != null) { 
            parser.setProject(typeRoot.getJavaProject());
            IBinding[] bindings = parser.createBindings(new IJavaElement[] { javaElement }, null);
            if (bindings.length > 0 && bindings[0] != null) {
                if (javaElement instanceof IMethod && bindings[0] instanceof ITypeBinding) {
                    // Case of a default constructor : let's go to the constructor and not to the type.
                    ITypeBinding typeBinding = (ITypeBinding) bindings[0];
                    for (IMethodBinding methodBinding : typeBinding.getDeclaredMethods()) {
                        if (methodBinding.isConstructor()) {
                            if (CharOperation.equals(methodBinding.getKey().toCharArray(), mirror.getBindingKey())) {
                                return true;
                            }
                        }
                    }
                }
                if (CharOperation.equals(bindings[0].getKey().toCharArray(), mirror.getBindingKey())) {
                    return true;
                }
            }
        }
        return false;
    }
}