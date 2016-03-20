package com.redhat.ceylon.eclipse.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Generic;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;

//TODO: fix all the copy/paste from FindReferenceVisitor
public class FindAssignmentsVisitor extends Visitor {
    
    private Declaration declaration;
    private final Set<Node> nodes = new HashSet<Node>();
    
    public FindAssignmentsVisitor(Declaration declaration) {
        if (declaration instanceof TypedDeclaration) {
            Declaration od = declaration;
            while (od!=null) {
                declaration = od;
                TypedDeclaration td = (TypedDeclaration) od;
                od = td.getOriginalDeclaration();
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
    
    protected boolean isReference(Parameter p) {
        return p!=null && isReference(p.getModel());
    }
    
    protected boolean isReference(Declaration ref) {
        return ref!=null && declaration.refines(ref) 
                || ref instanceof FunctionOrValue 
                && ((FunctionOrValue)ref).isShortcutRefinement()
                && ref.getRefinedDeclaration().equals(declaration);
    }

    private boolean isReference(Tree.Term lhs) {
        if (lhs instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression mte = 
                    (Tree.MemberOrTypeExpression)lhs;
            return isReference(mte.getDeclaration());
        }
        else {
            return false;
        }
    }
    
    @Override
    public void visit(Tree.TypeParameterDeclaration that) {
    	super.visit(that);
    	if (that.getTypeSpecifier()!=null) {
    		if (isReference(that.getDeclarationModel())) {
    			nodes.add(that.getTypeSpecifier());
    		}
    	}
    }
    
    @Override
    public void visit(Tree.TypeAliasDeclaration that) {
    	super.visit(that);
    	if (that.getTypeSpecifier()!=null) {
    		if (isReference(that.getDeclarationModel())) {
    			nodes.add(that.getTypeSpecifier());
    		}
    	}
    }
    
    @Override
    public void visit(Tree.ClassDeclaration that) {
    	super.visit(that);
    	if (that.getClassSpecifier()!=null) {
    		if (isReference(that.getDeclarationModel())) {
    			nodes.add(that.getClassSpecifier());
    		}
    	}
    }
    
    @Override
    public void visit(Tree.InterfaceDeclaration that) {
    	super.visit(that);
    	if (that.getTypeSpecifier()!=null) {
    		if (isReference(that.getDeclarationModel())) {
    			nodes.add(that.getTypeSpecifier());
    		}
    	}
    }
    
    @Override
    public void visit(Tree.SpecifierStatement that) {
        super.visit(that);
        Tree.Term lhs = that.getBaseMemberExpression();
        while (lhs instanceof Tree.ParameterizedExpression) {
        	lhs = ((Tree.ParameterizedExpression)lhs).getPrimary();
        }
        if (isReference(lhs)) {
            nodes.add(that.getSpecifierExpression());
        }
    }

    public void visit(Tree.InitializerParameter that) {
        super.visit(that);
        if (that.getSpecifierExpression()!=null) {
            if (isReference(that.getParameterModel())) {
                nodes.add(that.getSpecifierExpression());
            }
        }
    }

    @Override
    public void visit(Tree.AssignmentOp that) {
        super.visit(that);
        Tree.Term lhs = that.getLeftTerm();
        if (isReference(lhs)) {
            nodes.add(that.getRightTerm());
        }
    }
        
    @Override
    public void visit(Tree.PostfixOperatorExpression that) {
        super.visit(that);
        Tree.Term lhs = that.getTerm();
        if (isReference(lhs)) {
            nodes.add(that.getTerm());
        }
    }
        
    @Override
    public void visit(Tree.PrefixOperatorExpression that) {
        super.visit(that);
        Tree.Term lhs = that.getTerm();
        if (isReference(lhs)) {
            nodes.add(that.getTerm());
        }
    }
        
    @Override
    public void visit(Tree.AttributeDeclaration that) {
        super.visit(that);
        Tree.SpecifierOrInitializerExpression sie = 
                that.getSpecifierOrInitializerExpression();
        if (sie!=null && 
                isReference(that.getDeclarationModel())) {
            nodes.add(sie);
        }
    }
        
    @Override
    public void visit(Tree.MethodDeclaration that) {
        super.visit(that);
        Tree.SpecifierExpression se = 
                that.getSpecifierExpression();
        if (se!=null && 
                isReference(that.getDeclarationModel())) {
            nodes.add(se);
        }
    }
        
    @Override
    public void visit(Tree.Variable that) {
        super.visit(that);
        Tree.SpecifierExpression se = 
                that.getSpecifierExpression();
        if (se!=null && 
                isReference(that.getDeclarationModel())) {
            nodes.add(se);
        }
    }
    
    @Override
    public void visit(Tree.NamedArgument that) {
        if (isReference(that.getParameter())) {
            if (that instanceof Tree.SpecifiedArgument) {
                Tree.SpecifiedArgument sa = 
                        (Tree.SpecifiedArgument) that;
                nodes.add(sa.getSpecifierExpression());
            }
            else {
                nodes.add(that);
            }
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.PositionalArgument that) {
        if (isReference(that.getParameter())) {
            nodes.add(that);
        }
        super.visit(that);
    }
        
    @Override
    public void visit(Tree.SequencedArgument that) {
        if (isReference(that.getParameter())) {
            nodes.add(that);
        }
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.StaticMemberOrTypeExpression that) {
    	Tree.TypeArguments typeArguments = 
    	        that.getTypeArguments();
    	if (typeArguments instanceof Tree.TypeArgumentList) {
    		Declaration dec = that.getDeclaration();
    		if (dec instanceof Generic) {
    			Generic g = (Generic) dec;
                List<TypeParameter> typeParameters = 
    					g.getTypeParameters();
    			Tree.TypeArgumentList tal = 
    			        (Tree.TypeArgumentList) 
    			            typeArguments;
                List<Tree.Type> types = 
    					tal.getTypes();
    			for (int i=0; 
    			        i<types.size() && 
    			        i<typeParameters.size(); 
    			        i++) {
    				if (isReference(typeParameters.get(i))) {
    					nodes.add(types.get(i));
    				}
    			}
    		}
    	}
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.SimpleType that) {
    	Tree.TypeArgumentList typeArguments = 
    	        that.getTypeArgumentList();
    	if (typeArguments!=null) {
    		Declaration dec = that.getDeclarationModel();
    		if (dec instanceof Generic) {
    			Generic g = (Generic) dec;
                List<TypeParameter> typeParameters = 
    					g.getTypeParameters();
    			List<Tree.Type> types = 
    			        typeArguments.getTypes();
    			for (int i=0; 
    			        i<types.size() && 
    			        i<typeParameters.size(); 
    			        i++) {
    				if (isReference(typeParameters.get(i))) {
    					nodes.add(types.get(i));
    				}
    			}
    		}
    	}
        super.visit(that);
    }
    
    @Override
    public void visit(Tree.Return that) {
    	if (isReference(that.getDeclaration())) {
    		nodes.add(that);
    	}
		super.visit(that);
    }
        
}
