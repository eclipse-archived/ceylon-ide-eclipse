package com.redhat.ceylon.eclipse.util;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class FindScopeVisitor extends Visitor {
    
    FindScopeVisitor(int fStartOffset, int fEndOffset) {
        this.fStartOffset = fStartOffset;
        this.fEndOffset = fEndOffset;
    }
    
    private Node node;
    private int fStartOffset;
    private int fEndOffset;
    
    public Node getNode() {
        return node;
    }
        
    @Override
    public void visit(Tree.Import that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.PackageDescriptor that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ModuleDescriptor that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ImportModule that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.InterfaceDefinition that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ClassDefinition that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.MethodDefinition that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.AttributeGetterDefinition that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.AttributeSetterDefinition that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ObjectDefinition that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.TypedArgument that) {
        if (inBounds(that)) {
            node = that;
        }
        super.visit(that);
    }
    
    private boolean inBounds(Node that) {
        return inBounds(that, that);
    }
    
    private boolean inBounds(Node left, Node right) {
        if (left==null) return false;
        if (right==null) left=right;
        Integer tokenStartIndex = left.getStartIndex();
        Integer tokenEndIndex = right.getEndIndex();
        return tokenStartIndex!=null && tokenEndIndex!=null &&
                tokenStartIndex <= fStartOffset && 
                tokenEndIndex >= fEndOffset;
    }
    
}