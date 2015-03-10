package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.FunctionArgument;

class FindReferencedNodeVisitor extends Visitor {
    
    private final Referenceable declaration;
    private Node declarationNode;
    
    FindReferencedNodeVisitor(Referenceable declaration) {
        this.declaration = declaration;
    }
    
    Node getDeclarationNode() {
        return declarationNode;
    }
    
    private boolean isDeclaration(Declaration dec) {
        if (dec!=null && dec.equals(declaration)) {
            if (declaration instanceof Method) {
                Method method = (Method) declaration;
                if (method.isOverloaded()) {
                    return method==dec;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    @Override
    public void visit(Tree.ModuleDescriptor that) {
        super.visit(that);
        Referenceable m = that.getImportPath().getModel();
        if (m!=null && m.equals(declaration)) {
            declarationNode = that;
        }
    }
    
    @Override
    public void visit(Tree.PackageDescriptor that) {
        super.visit(that);
        Referenceable p = that.getImportPath().getModel();
        if (p!=null && p.equals(declaration)) {
            declarationNode = that;
        }
    }
    
    @Override
    public void visit(Tree.Declaration that) {
        if (isDeclaration(that.getDeclarationModel())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.AttributeSetterDefinition that) {
        Setter setter = that.getDeclarationModel();
        if (isDeclaration(setter.getDirectMember(setter.getName(), null, false))) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ObjectDefinition that) {
        if (isDeclaration(that.getDeclarationModel()
                .getTypeDeclaration())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.SpecifierStatement that) {
        if (that.getRefinement()) {
            if (isDeclaration(that.getDeclaration())) {
                declarationNode = that;
            }
        }
        super.visit(that);
    }
    
    @Override
    public void visit(FunctionArgument that) {
        if (isDeclaration(that.getDeclarationModel())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.InitializerParameter that) {
        if (isDeclaration(that.getParameterModel().getModel())) {
            declarationNode = that;
        }
        super.visit(that);
    }
    
    public void visitAny(Node node) {
        if (declarationNode==null ||
                declarationNode instanceof Tree.InitializerParameter) {
            super.visitAny(node);
        }
    }
    
}
