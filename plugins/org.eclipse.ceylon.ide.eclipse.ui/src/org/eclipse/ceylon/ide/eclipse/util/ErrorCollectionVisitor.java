package org.eclipse.ceylon.ide.eclipse.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ceylon.compiler.typechecker.tree.Message;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

public class ErrorCollectionVisitor extends Visitor {
    
    private final List<Message> errors = new ArrayList<Message>();
    private boolean withinDeclaration;
    private boolean includingChildren;
    
    private final Node declaration;

    public ErrorCollectionVisitor(Node declaration, 
            Boolean includingChildren) {
        this.declaration = declaration;
        this.includingChildren = includingChildren;
    }
    
    public List<Message> getErrors() {
        return errors;
    }
    
    @Override
    public void visit(Tree.Declaration that) {
        if (that==declaration) {
            withinDeclaration=true;
            super.visit(that);
            withinDeclaration=false;
        }
        else if (includingChildren) {
            super.visit(that);
        }
        else {
            boolean outer = withinDeclaration;
            withinDeclaration = false;
            super.visit(that);
            withinDeclaration = outer;
        }
    }
    
    @Override
    public void visit(Tree.Import that) {
        if (that==declaration) {
            withinDeclaration=true;
            super.visit(that);
            withinDeclaration=false;
        }
        else if (includingChildren) {
            super.visit(that);
        }
        else {
            boolean outer = withinDeclaration;
            withinDeclaration = false;
            super.visit(that);
            withinDeclaration = outer;
        }
    }
    
    @Override
    public void visit(Tree.ImportModule that) {
        if (that==declaration) {
            withinDeclaration=true;
            super.visit(that);
            withinDeclaration=false;
        }
        else if (includingChildren) {
            super.visit(that);
        }
        else {
            boolean outer = withinDeclaration;
            withinDeclaration = false;
            super.visit(that);
            withinDeclaration = outer;
        }
    }
    
    @Override
    public void visitAny(Node that) {
        if (withinDeclaration) {
            errors.addAll(that.getErrors());
        }
        super.visitAny(that);
    }
    
}
