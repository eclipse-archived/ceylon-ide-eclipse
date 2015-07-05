package com.redhat.ceylon.eclipse.util;

import org.eclipse.jface.text.IRegion;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.DocLink;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.model.typechecker.model.Constructor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Setter;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Value;

public class SearchVisitor extends Visitor {
    
    public static final int TYPE = 1;
    public static final int FUNCTION = 1<<1;
    public static final int VALUE = 1<<2;
    public static final int ANYTHING = TYPE|FUNCTION|VALUE;
    
    public interface Matcher {
        boolean matches(String string);
        boolean includeDeclarations();
        boolean includeReferences();
        boolean includeTypes();
        boolean includeImports();
        boolean includeDoc();
        int kinds();
    }
    
    boolean hasKind(Declaration declaration, int kind) {
        //TODO: anonymous classes
        if (declaration instanceof Constructor) {
            Constructor constructor = 
                    (Constructor) declaration;
            return (constructor.getParameterList()==null ?
                     kind&VALUE : kind&FUNCTION) != 0;
        }
        else if (declaration instanceof TypeDeclaration) {
            return (kind&TYPE) != 0;
        }
        else if (declaration instanceof Function) {
            return (kind&FUNCTION) != 0;
        }
        else if (declaration instanceof Value ||
                declaration instanceof Setter) {
            return (kind&VALUE) != 0;
        }
        else {
            return false;
        }
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
                matcher.matches(that.getIdentifier().getText()) &&
                hasKind(that.getDeclaration(), 
                        matcher.kinds())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.SimpleType that) {
        if (matcher.includeTypes() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText()) &&
                hasKind(that.getDeclarationModel(), 
                        matcher.kinds())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.Declaration that) {
        if (matcher.includeDeclarations() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText()) &&
                hasKind(that.getDeclarationModel(), 
                        matcher.kinds())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.ImportMemberOrType that) {
        if (matcher.includeImports() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText()) &&
                hasKind(that.getDeclarationModel(), 
                        matcher.kinds())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.TypedArgument that) {
        if (matcher.includeReferences() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText()) &&
                hasKind(that.getDeclarationModel(), 
                        matcher.kinds())) {
            matchingNode(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.SpecifiedArgument that) {
        if (matcher.includeReferences() &&
                that.getIdentifier()!=null && 
                matcher.matches(that.getIdentifier().getText()) &&
                hasKind(that.getParameter().getModel(), 
                        matcher.kinds())) {
            matchingNode(that);
        }
        super.visit(that);
    }
            
    @Override
    public void visit(DocLink that) {
        if (matcher.includeDoc()) {
            int i=0;
            String name;
            while ((name = DocLinks.name(that, i))!=null) {
                if (matcher.matches(name)) {
                    if (matcher.kinds()==ANYTHING ||
                            hasKind(getLinkedDec(that, i), 
                                    matcher.kinds())) {
                        matchingRegion(that, 
                                DocLinks.nameRegion(that, i));
                    }
                }
                i++;
            }
        }
    }

    private static Declaration getLinkedDec(DocLink that, int i) {
        Declaration dec = i==0 ? 
                that.getBase() :
                that.getQualified().get(i-1);
        return dec;
    }
}
