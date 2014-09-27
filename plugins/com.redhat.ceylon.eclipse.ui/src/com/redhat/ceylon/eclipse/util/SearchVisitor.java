package com.redhat.ceylon.eclipse.util;

import org.eclipse.jface.text.IRegion;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.DocLink;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class SearchVisitor extends Visitor {
    
    public interface Matcher {
        boolean matches(String string);
        boolean includeDeclarations();
        boolean includeReferences();
    }
    
    private final Matcher matcher;
    
    public SearchVisitor(Matcher matcher) {
        this.matcher = matcher;
    }
    
    public void matchingNode(Node node) {}
    public void matchingRegion(Node node, IRegion region) {}
    
    @Override
    public void visit(Tree.ExtendedTypeExpression that) {}
            
    @Override
    public void visit(Tree.StaticMemberOrTypeExpression that) {
        if (matcher.includeReferences() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.SimpleType that) {
        if (matcher.includeReferences() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.Declaration that) {
        if (matcher.includeDeclarations() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.ImportMemberOrType that) {
        if (matcher.includeReferences() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.TypedArgument that) {
        if (matcher.includeReferences() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.SpecifiedArgument that) {
        if (matcher.includeReferences() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText())) {
            matchingNode(that);
        }
        super.visit(that);
    }
            
    @Override
    public void visit(DocLink that) {
        if (matcher.includeReferences()) {
            int i=0;
            String name;
            while ((name = DocLinks.name(that, i))!=null) {
                if (matcher.matches(name)) {
                    matchingRegion(that, DocLinks.nameRegion(that, i));
                }
                i++;
            }
        }
    }
}
