package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;

public class JavaCompilationUnit extends JavaUnit {
    ICompilationUnit typeRoot;

    public JavaCompilationUnit(ICompilationUnit typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super(fileName, relativePath, fullPath, pkg);
        this.typeRoot = typeRoot;
    }

    @Override
    public ICompilationUnit getTypeRoot() {
        return typeRoot;
    }

    @Override
    public IJavaElement toJavaElement(final Declaration ceylonDeclaration) {
/*        
        try {
            ASTParser parser = ASTParser.newParser(AST.JLS4);
            parser.setBindingsRecovery(true);
            parser.setResolveBindings(true);
            parser.setSource(typeRoot);
            class MatchingASTVisitor extends ASTVisitor {
                IBinding declarationBinding = null;
                private boolean declarationMatched(IBinding currentBinding,
                        IBindingProvider mirror) {
                    if (mirror != null && CharOperation.equals(currentBinding.getKey().toCharArray(), mirror.getBindingKey())) {
                        declarationBinding = currentBinding;
                        return true;
                    }
                    return false;
                }
                @Override
                public boolean visit(MethodDeclaration node) {
                    IBindingProvider mirror = null;
                    if (node.isConstructor() && ceylonDeclaration instanceof LazyClass) {
                        LazyClass lazyClass = (LazyClass) ceylonDeclaration;
                        if (! lazyClass.isAbstraction()) {
                            IBindingProvider constructor = (IBindingProvider) lazyClass.getConstructor();
                            if (constructor != null) {
                                mirror = constructor;
                            }
                        }
                    }
                    if (ceylonDeclaration instanceof LazyMethod) {
                        mirror = (IBindingProvider) ((LazyMethod) ceylonDeclaration).getMethodMirror();
                    }
                    if (ceylonDeclaration instanceof JavaMethod) {
                        mirror = (IBindingProvider) ((JavaMethod) ceylonDeclaration).mirror;
                    }
                    if (declarationMatched(node.resolveBinding(), mirror)) {
                        return false;
                    }

                    return super.visit(node);
                }
                @Override
                public boolean visit(org.eclipse.jdt.core.dom.TypeDeclaration node) {
                    IBindingProvider mirror = null;
                    if (ceylonDeclaration instanceof LazyClass) {
                        LazyClass lazyClass = (LazyClass) ceylonDeclaration;
                        if (! lazyClass.isAbstraction()) {
                            IBindingProvider constructor = (IBindingProvider) lazyClass.getConstructor();
                            if (constructor != null) {
                                return super.visit(node); //this will be managed in the IType element  
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
                        if (declarationMatched(node.resolveBinding(), mirror)) {
                            for (MethodDeclaration methodDecl : node.getMethods()) {
                                if (methodDecl.getName().toString().equals(javaBeanValue.getGetterName())) {
                                    declarationBinding = methodDecl.resolveBinding();
                                    return false;
                                }
                            }
                        }
                        return super.visit(node);
                    }
                    if (declarationMatched(node.resolveBinding(), mirror)) {
                        return false;
                    }
                    return super.visit(node);
                }
                @Override
                public boolean visit(org.eclipse.jdt.core.dom.EnumDeclaration node) {
                    IBindingProvider mirror = null;
                    if (ceylonDeclaration instanceof LazyClass) {
                        LazyClass lazyClass = (LazyClass) ceylonDeclaration;
                        if (! lazyClass.isAbstraction()) {
                            IBindingProvider constructor = (IBindingProvider) lazyClass.getConstructor();
                            if (constructor != null) {
                                return super.visit(node); //this will be managed in the IType element  
                            }
                        }
                        mirror = (IBindingProvider) lazyClass.classMirror;
                    }
                    if (declarationMatched(node.resolveBinding(), mirror)) {
                        return false;
                    }
                    return super.visit(node);
                }
                @Override
                public boolean visit(org.eclipse.jdt.core.dom.AnnotationTypeDeclaration node) {
                    IBindingProvider mirror = null;
                    if (ceylonDeclaration instanceof LazyClass) {
                        LazyClass lazyClass = (LazyClass) ceylonDeclaration;
                        if (! lazyClass.isAbstraction()) {
                            IBindingProvider constructor = (IBindingProvider) lazyClass.getConstructor();
                            if (constructor != null) {
                                return super.visit(node); //this will be managed in the IType element  
                            }
                        }
                        mirror = (IBindingProvider) lazyClass.classMirror;
                    }
                    if (ceylonDeclaration instanceof LazyInterface) {
                        mirror = (IBindingProvider) ((LazyInterface) ceylonDeclaration).classMirror; 
                    }
                    
                    if (declarationMatched(node.resolveBinding(), mirror)) {
                        return false;
                    }
                    return super.visit(node);
                }
            }
            MatchingASTVisitor matchingVisitor = new MatchingASTVisitor();            
            parser.createAST(null).accept(matchingVisitor);
            if (matchingVisitor.declarationBinding != null) {
                return matchingVisitor.declarationBinding.getJavaElement();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
*/
        return new CeylonToJavaMatcher(this).searchInClass(ceylonDeclaration);
    }
}
