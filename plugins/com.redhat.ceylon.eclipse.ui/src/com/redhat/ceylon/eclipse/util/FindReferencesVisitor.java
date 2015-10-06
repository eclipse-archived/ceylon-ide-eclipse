package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static com.redhat.ceylon.eclipse.util.Nodes.getImportedName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.tree.CustomTree.GuardedVariable;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.model.typechecker.model.Constructor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Setter;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.Value;

public class FindReferencesVisitor extends Visitor {
    
    private Referenceable declaration;
    protected final Set<Node> nodes = new HashSet<Node>();
    
    public FindReferencesVisitor(Referenceable declaration) {
        if (declaration instanceof TypedDeclaration) {
            Referenceable od = declaration;
            while (od!=null && od!=declaration) {
                declaration = od;
                TypedDeclaration td = (TypedDeclaration) od;
                od = td.getOriginalDeclaration();
            }
        }
        if (declaration instanceof Declaration) { 
            Declaration dec = (Declaration) declaration;
            Scope container = dec.getContainer();
            if (container instanceof Setter) {
                Setter setter = (Setter) container;
                Declaration member = 
                        setter.getDirectMember(
                                setter.getName(), 
                                null, false);
                if (member.equals(declaration)) {
                    declaration = setter;
                }
            }
        }
        if (declaration instanceof Setter) {
            Setter setter = (Setter) declaration;
            declaration = setter.getGetter();
        }
        if (declaration instanceof Constructor &&
                declaration.getNameAsString()==null) {
            //default constructor
            Constructor constructor =
                    (Constructor) declaration;
            Type extended = 
                    constructor.getExtendedType();
            if (extended!=null) {
                declaration = extended.getDeclaration();
            }
        }
        this.declaration = declaration;
    }
    
    public Referenceable getDeclaration() {
        return declaration;
    }
    
    public Set<Node> getNodes() {
        return nodes;
    }
    
    protected boolean isReference(Parameter param) {
        return param!=null && isReference(param.getModel());
    }
    
    protected boolean isReference(Declaration ref) {
        if (ref!=null) {
            //include references to things that the  
            //selected declaration refines, and refs
            //within setters
            return isRefinedDeclarationReference(ref) || 
                    isSetterParameterReference(ref);
        }
        else {
            return false;
        }
    }
    
    private boolean isRefinedDeclarationReference(Declaration ref) {
        if (declaration instanceof Declaration) {
            Declaration dec = (Declaration) declaration;
            return dec.refines(ref);
        }
        else {
            return false;
        }
    }

    private boolean isSetterParameterReference(Declaration ref) {
        Scope container = ref.getContainer();
        if (container instanceof Setter) {
            Setter setter = (Setter) container;
            Declaration member = 
                    setter.getDirectMember(
                            setter.getName(), 
                            null, false);
            return member.equals(ref) && 
                    isReference(setter.getGetter());
        }
        else {
            return false;
        }
    }
    
    protected boolean isReference(Declaration ref, String id) {
        return isReference(ref);
    }
    
    private Tree.Variable getConditionVariable(Tree.Condition c) {
        //NOTE: returns null for a destructuring condition!
        if (c instanceof Tree.ExistsOrNonemptyCondition) {
            Tree.ExistsOrNonemptyCondition eonc = 
                    (Tree.ExistsOrNonemptyCondition) c;
            Tree.Statement st = eonc.getVariable();
            if (st instanceof Tree.Variable) {
                return (Tree.Variable) st;
            }
        }
        if (c instanceof Tree.IsCondition) {
            Tree.IsCondition ic = (Tree.IsCondition) c;
            return ic.getVariable();
        }
        return null;
    }
    
    @Override
    public void visit(Tree.CaseClause that) {
        Tree.CaseItem ci = that.getCaseItem();
        if (ci instanceof Tree.IsCase) {
            Tree.IsCase ic = (Tree.IsCase) ci;
            Tree.Variable var = ic.getVariable();
            if (var!=null) {
                Value vd = var.getDeclarationModel();
                TypedDeclaration od = 
                        vd.getOriginalDeclaration();
                if (od!=null && od.equals(declaration)) {
                    Referenceable d = declaration;
                    declaration = vd;
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
        Tree.ConditionList cl = that.getConditionList();
        List<Tree.Condition> conditions = cl.getConditions();
        for (int i=0; i<conditions.size(); i++) {
            Tree.Condition c = conditions.get(i);
            Tree.Variable var = getConditionVariable(c);
            if (var!=null && 
                    var.getType() 
                        instanceof Tree.SyntheticVariable) {
                Value vd = var.getDeclarationModel();
                TypedDeclaration od = 
                        vd.getOriginalDeclaration();
                if (od!=null && od.equals(declaration)) {
                    for (int j=0; j<=i; j++) {
                        Tree.Condition oc = conditions.get(j);
                        oc.visit(this);
                    }
                    Referenceable d = declaration;
                    declaration = vd;
                    that.getBlock().visit(this);
                    for (int j=i; j<conditions.size(); j++) {
                        Tree.Condition oc = conditions.get(j);
                        oc.visit(this);
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
        Tree.ConditionList cl = that.getConditionList();
        List<Tree.Condition> conditions = cl.getConditions();
        for (int i=0; i<conditions.size(); i++) {
            Tree.Condition c = conditions.get(i);
            Tree.Variable var = getConditionVariable(c);
            if (var!=null && 
                    var.getType() 
                        instanceof Tree.SyntheticVariable) {
                Value vd = var.getDeclarationModel();
                TypedDeclaration od = 
                        vd.getOriginalDeclaration();
                if (od!=null && od.equals(declaration)) {
                    for (int j=0; j<=i; j++) {
                        Tree.Condition oc = conditions.get(j);
                        oc.visit(this);
                    }
                    Referenceable d = declaration;
                    declaration = vd;
                    if (that.getBlock()!=null) {
                        that.getBlock().visit(this);
                    }
                    if (that.getExpression()!=null) {
                        that.getExpression().visit(this);
                    }
                    for (int j=i+1; j<conditions.size(); j++) {
                        Tree.Condition oc = conditions.get(j);
                        oc.visit(this);
                    }
                    declaration = d;
                    return;
                }
            }
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.ElseClause that) {
        Tree.Variable var = that.getVariable();
        if (var!=null) {
            Value vd = var.getDeclarationModel();
            TypedDeclaration od = 
                    vd.getOriginalDeclaration();
            if (od!=null && od.equals(declaration)) {
                Referenceable d = declaration;
                declaration = vd;
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
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.Variable that) {
        if (that instanceof GuardedVariable) {
            Value d = that.getDeclarationModel();
            TypedDeclaration od = 
                    d.getOriginalDeclaration();
            if (od!=null && od.equals(declaration)) {
                declaration = d;
            }
        }
        else {
            super.visit(that);
        }
    }

    @Override
    public void visit(Tree.Body body) {
        Referenceable d = declaration;
        for (Tree.Statement st: body.getStatements()) {
            if (st instanceof Tree.Assertion) {
                Tree.Assertion that = (Tree.Assertion) st;
                Tree.ConditionList cl = that.getConditionList();
                for (Tree.Condition c: cl.getConditions()) {
                    Tree.Variable var = getConditionVariable(c);
                    if (var!=null && 
                            var.getType() 
                                instanceof Tree.SyntheticVariable) {
                        Value vd = var.getDeclarationModel();
                        TypedDeclaration od = 
                                vd.getOriginalDeclaration();
                        if (od!=null && od.equals(declaration)) {
                            c.visit(this);
                            declaration = vd;
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
        Type type = that.getTypeModel();
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
    public void visit(Tree.Import that) {
        super.visit(that);
        if (declaration instanceof Package) {
            if (formatPath(that.getImportPath().getIdentifiers())
                    .equals(declaration.getNameAsString())) {
                nodes.add(that);
            }
        }
    }
    
    @Override
    public void visit(Tree.ImportModule that) {
        super.visit(that);
        if (declaration instanceof Module) {
            String path = getImportedName(that);
            if (path!=null &&
                    path.equals(declaration.getNameAsString())) {
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
    public void visit(Tree.TypeConstraint that) {
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
