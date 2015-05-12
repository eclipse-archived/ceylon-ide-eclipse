package com.redhat.ceylon.eclipse.core.model;

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

import com.redhat.ceylon.eclipse.core.model.mirror.IBindingProvider;
import com.redhat.ceylon.eclipse.core.model.mirror.JDTMethod;
import com.redhat.ceylon.model.loader.model.FieldValue;
import com.redhat.ceylon.model.loader.model.JavaBeanValue;
import com.redhat.ceylon.model.loader.model.JavaMethod;
import com.redhat.ceylon.model.loader.model.LazyClass;
import com.redhat.ceylon.model.loader.model.LazyInterface;
import com.redhat.ceylon.model.loader.model.LazyMethod;
import com.redhat.ceylon.model.loader.model.LazyValue;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Scope;

public class CeylonToJavaMatcher {
    private Declaration ceylonDeclaration = null;
    private final ITypeRoot typeRoot;
    private final ASTParser parser;
    
    @SuppressWarnings("deprecation")
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
            if (declarationMatched(javaType, mirror) != null) {
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
            if (declarationMatched(javaType, mirror) != null) {
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
            return visit((IParent)javaType);
        }
        
        IJavaElement result = declarationMatched(javaType, mirror);
        if (result != null) {
            return result;
        }
        return visit((IParent)javaType);
    }
    
    private IJavaElement visit(IMethod javaMethod) {
        IBindingProvider mirror = null;
        
        if (ceylonDeclaration instanceof LazyMethod) {
            mirror = (IBindingProvider) ((LazyMethod) ceylonDeclaration).getMethodMirror();
        }
        if (ceylonDeclaration instanceof JavaMethod) {
            mirror = (IBindingProvider) ((JavaMethod) ceylonDeclaration).mirror;
        }
        IJavaElement result = declarationMatched(javaMethod, mirror);
        if (result != null) {
            return result;
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
            e.printStackTrace();
        }
        return null;
    }
    
    private IJavaElement declarationMatched(IJavaElement javaElement,
            IBindingProvider mirror) {
        if (mirror != null) { 
            parser.setProject(typeRoot.getJavaProject());
            IBinding[] bindings = parser.createBindings(new IJavaElement[] { javaElement }, null);
            if (bindings.length > 0 && bindings[0] != null) {
                if (mirror instanceof JDTMethod && bindings[0] instanceof ITypeBinding) {
                    // Case of a constructor : let's go to the constructor and not to the type.
                    ITypeBinding typeBinding = (ITypeBinding) bindings[0];
                    for (IMethodBinding methodBinding : typeBinding.getDeclaredMethods()) {
//                        if (methodBinding.isConstructor()) {
                    	if (CharOperation.equals(methodBinding.getKey().toCharArray(), mirror.getBindingKey())) {
                    		return methodBinding.getJavaElement();
                    	}
//                        }
                    }
                }
                if (CharOperation.equals(bindings[0].getKey().toCharArray(), mirror.getBindingKey())) {
                    return javaElement;
                }
            }
        }
        return null;
    }
}