package com.redhat.ceylon.eclipse.util;

import java.util.HashSet;
import java.util.Set;

import ceylon.language.null_;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.ValueParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Condition;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class FindReferenceVisitor extends Visitor {
	
	private Declaration declaration;
	private final Set<Node> nodes = new HashSet<Node>();
	
	public FindReferenceVisitor(Declaration declaration) {
        if (declaration instanceof ValueParameter 
                && ((ValueParameter) declaration).isHidden()) {
            declaration = declaration.getContainer().getMember(declaration.getName(), null, false);
        }
	    if (declaration instanceof TypedDeclaration) {
	        Declaration od = declaration;
	        while (od!=null && od!=declaration) {
	            declaration = od;
	            od = ((TypedDeclaration) od).getOriginalDeclaration();
	        }
	    }
		this.declaration = declaration;
	}
	
	public Declaration getDeclaration() {
        return declaration;
    }
	
	public Set<Node> getNodes() {
		return nodes;
	}
	
	protected boolean isReference(Declaration ref) {
	    if (ref instanceof ValueParameter 
	            && ((ValueParameter) ref).isHidden()) {
	        ref = ref.getContainer().getMember(ref.getName(), null, false);
	    }
	    return ref!=null && declaration!=null && declaration.refines(ref);
	}
	
    protected boolean isReference(Declaration ref, String id) {
        return isReference(ref);
    }
    
    private Tree.Variable getConditionVariable(Condition c) {
        if (c instanceof Tree.ExistsOrNonemptyCondition) {
            return ((Tree.ExistsOrNonemptyCondition) c).getVariable();
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
            TypedDeclaration od = var.getDeclarationModel().getOriginalDeclaration();
			if (od!=null && od.equals(declaration)) {
                Declaration d = declaration;
                declaration = var.getDeclarationModel();
                that.getBlock().visit(this);
                declaration = d;
                return;
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
        			Declaration d = declaration;
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
    public void visit(Tree.WhileClause that) {
        for (Condition c: that.getConditionList().getConditions()) {
        	Tree.Variable var = getConditionVariable(c);
        	if (var!=null && var.getType() instanceof Tree.SyntheticVariable) {
        		TypedDeclaration od = var.getDeclarationModel()
        				.getOriginalDeclaration();
        		if (od!=null && od.equals(declaration)) {
        			c.visit(this);
        			Declaration d = declaration;
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
    public void visit(Tree.ExtendedTypeExpression that) {}
    		
    @Override
    public void visit(Tree.StaticMemberOrTypeExpression that) {
        if (isReference(that.getDeclaration(), 
                id(that.getIdentifier()))) {
            nodes.add(that);
        }
        super.visit(that);
    }
        
	@Override
	public void visit(Tree.NamedArgument that) {
		if (isReference(that.getParameter())) {
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
    public void visit(Tree.ValueParameterDeclaration that) {
        if(that.getType() instanceof Tree.LocalModifier) {
            if (isReference(that.getDeclarationModel())) {
                nodes.add(that);
            }
        }
        super.visit(that);
    }
    
	private String id(Tree.Identifier that) {
	    return that==null ? null : that.getText();
	}
		
}
