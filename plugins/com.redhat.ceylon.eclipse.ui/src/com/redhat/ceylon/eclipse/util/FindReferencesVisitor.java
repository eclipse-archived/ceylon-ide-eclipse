package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;

import java.util.HashSet;
import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Condition;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Import;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportModule;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.TypeConstraint;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindReferencesVisitor extends Visitor implements NaturalVisitor {
    
    private Referenceable declaration;
    private final Set<Node> nodes = new HashSet<Node>();
    
    public FindReferencesVisitor(Referenceable declaration) {
        if (declaration instanceof TypedDeclaration) {
            Referenceable od = declaration;
            while (od!=null && od!=declaration) {
                declaration = od;
                od = ((TypedDeclaration) od).getOriginalDeclaration();
            }
        }
        if (declaration instanceof Declaration && 
                ((Declaration)declaration).getContainer() instanceof Setter) {
            Setter setter = (Setter) ((Declaration)declaration).getContainer();
            if (setter.getDirectMember(setter.getName(), null, false)
                    .equals(declaration)) {
                declaration = setter;
            }
        }
        if (declaration instanceof Setter) {
            declaration = ((Setter) declaration).getGetter();
        }
        this.declaration = declaration;
    }
    
    public Referenceable getDeclaration() {
        return declaration;
    }
    
    public Set<Node> getNodes() {
        return nodes;
    }
    
    protected boolean isReference(Parameter p) {
        return p!=null && isReference(p.getModel());
    }
    
    protected boolean isReference(Declaration ref) {
        return ref!=null && declaration instanceof Declaration && 
                (((Declaration)declaration).refines(ref) || 
                        isSetterParameterReference(ref));
    }

    private boolean isSetterParameterReference(Declaration ref) {
        if (ref.getContainer() instanceof Setter) {
            Setter setter = (Setter) ref.getContainer();
            return setter.getDirectMember(setter.getName(), null, false).equals(ref) &&
                    isReference(setter.getGetter());
        }
        else {
            return false;
        }
    }

    protected boolean isReference(Declaration ref, String id) {
        return isReference(ref);
    }
    
    private Tree.Variable getConditionVariable(Condition c) {
        //NOTE: returns null for a destructuring condition!
        if (c instanceof Tree.ExistsOrNonemptyCondition) {
            Tree.Statement st = 
                    ((Tree.ExistsOrNonemptyCondition) c).getVariable();
            if (st instanceof Tree.Variable) {
                return (Tree.Variable) st;
            }
        }
        if (c instanceof Tree.IsCondition) {
            return ((Tree.IsCondition) c).getVariable();
        }
        return null;
    }
    
    @Override
    public void visit(Tree.CaseClause that) {
        Tree.CaseItem ci = that.getCaseItem();
        if (ci instanceof Tree.IsCase) {
            Tree.Variable var = ((Tree.IsCase) ci).getVariable();
            if (var!=null) {
                TypedDeclaration od = var.getDeclarationModel().getOriginalDeclaration();
                if (od!=null && od.equals(declaration)) {
                    Referenceable d = declaration;
                    declaration = var.getDeclarationModel();
                    if (that.getBlock()!=null) {
                        that.getBlock().visit(this);
                    }
                    if (that.getExpression()!=null) {
                        that.getExpression().visit(this);
                    }
                    declaration = d;
                    return;
                }
            }
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.IfClause that) {
        for (Condition c: that.getConditionList().getConditions()) {
            Tree.Variable var = getConditionVariable(c);
            if (var!=null && var.getType() instanceof Tree.SyntheticVariable) {
                TypedDeclaration od = var.getDeclarationModel().getOriginalDeclaration();
                if (od!=null && od.equals(declaration)) {
                    c.visit(this);
                    Referenceable d = declaration;
                    declaration = var.getDeclarationModel();
                    if (that.getBlock()!=null) {
                        that.getBlock().visit(this);
                    }
                    if (that.getExpression()!=null) {
                        that.getExpression().visit(this);
                    }
                    declaration = d;
                    return;
                }
            }
        }
        super.visit(that);
    }

    @Override
    public void visit(Tree.WhileClause that) {
        for (Condition c: that.getConditionList().getConditions()) {
            Tree.Variable var = getConditionVariable(c);
            if (var!=null && var.getType() instanceof Tree.SyntheticVariable) {
                TypedDeclaration od = var.getDeclarationModel()
                        .getOriginalDeclaration();
                if (od!=null && od.equals(declaration)) {
                    c.visit(this);
                    Referenceable d = declaration;
                    declaration = var.getDeclarationModel();
                    that.getBlock().visit(this);
                    declaration = d;
                    return;
                }
            }
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.Body body) {
        Referenceable d = declaration;
        for (Tree.Statement st: body.getStatements()) {
            if (st instanceof Tree.Assertion) {
                Tree.Assertion that = (Tree.Assertion) st;
                for (Condition c: that.getConditionList().getConditions()) {
                    Tree.Variable var = getConditionVariable(c);
                    if (var!=null && var.getType() instanceof Tree.SyntheticVariable) {
                        TypedDeclaration od = var.getDeclarationModel()
                                .getOriginalDeclaration();
                        if (od!=null && od.equals(declaration)) {
                            c.visit(this);
                            declaration = var.getDeclarationModel();
                            break;
                        }
                    }
                }
            }
            st.visit(this);
        }
        declaration = d;
    }
    
    @Override
    public void visit(Tree.ExtendedTypeExpression that) {}
            
    @Override
    public void visit(Tree.StaticMemberOrTypeExpression that) {
        if (isReference(that.getDeclaration(), 
                id(that.getIdentifier()))) {
            nodes.add(that);
        }
        super.visit(that);
    }
        
    public void visit(Tree.MemberLiteral that) {
        if (isReference(that.getDeclaration(), 
                id(that.getIdentifier()))) {
            nodes.add(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.TypedArgument that) {
        if (isReference(that.getParameter())) {
            nodes.add(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.SpecifiedArgument that) {
        if (that.getIdentifier()!=null &&
                that.getIdentifier().getToken()!=null &&
                isReference(that.getParameter())) {
            nodes.add(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.SimpleType that) {
        ProducedType type = that.getTypeModel();
        if (type!=null && isReference(type.getDeclaration(), 
                id(that.getIdentifier()))) {
            nodes.add(that);
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ImportMemberOrType that) {
        if (isReference(that.getDeclarationModel())) {
            nodes.add(that);
        }
        super.visit(that);
    }
        
    
    @Override
    public void visit(Import that) {
        super.visit(that);
        if (declaration instanceof Package) {
            if (formatPath(that.getImportPath().getIdentifiers())
                    .equals(declaration.getNameAsString())) {
                nodes.add(that);
            }
        }
    }
    
    @Override
    public void visit(ImportModule that) {
        super.visit(that);
        if (declaration instanceof Module) {
            if (formatPath(that.getImportPath().getIdentifiers())
                    .equals(declaration.getNameAsString())) {
                nodes.add(that);
            }
        }
    }
    
    @Override
    public void visit(Tree.InitializerParameter that) {
        if (isReference(that.getParameterModel())) {
            nodes.add(that);
        }
        else {
            super.visit(that);
        }
    }
    
    @Override
    public void visit(TypeConstraint that) {
        if (isReference(that.getDeclarationModel())) {
            nodes.add(that);
        }
        else { 
            super.visit(that);
        }
    }
    
    private String id(Tree.Identifier that) {
        return that==null ? null : that.getText();
    }
        
}
