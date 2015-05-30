package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.model.typechecker.model.ModelUtil.unionType;
import static com.redhat.ceylon.eclipse.util.Types.getResultType;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.NaturalVisitor;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.NamedArgumentList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SwitchCaseList;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

class RequiredTypeVisitor extends Visitor 
        implements NaturalVisitor {
    
    private Node node;
    private Type requiredType = null;
    private Type finalResult = null;
    private Reference namedArgTarget = null;
    private Token token;
    
    public Type getType() {
        return finalResult;
    }
    
    public RequiredTypeVisitor(Node node, Token token) {
        this.node = node;
        this.token = token;
    }
    
    @Override
    public void visitAny(Node that) {
        if (node==that) {
            finalResult=requiredType;  
        }
        super.visitAny(that);
    }
    
    @Override
    public void visit(Tree.InvocationExpression that) {
        if (that.getPrimary()!=null) {
            that.getPrimary().visit(this);
        }
        Type ort = requiredType;
        Reference onat = namedArgTarget;
        Tree.PositionalArgumentList pal = that.getPositionalArgumentList();
        Unit unit = that.getUnit();
        if (pal!=null) {
            int pos;
            List<Tree.PositionalArgument> pas = pal.getPositionalArguments();
            if (pas.isEmpty()) {
                pos = 0;
            }
            else {
                pos = pas.size(); //default to the last argument if incomplete
                for (int i=0; i<pas.size(); i++) {
                    Tree.PositionalArgument pa = pas.get(i);
                    if (token!=null) {
                        if (pa.getStopIndex()>=((CommonToken) token).getStopIndex()) {
                            pos = i;
                            break;
                        }
                    }
                    else {
                        if (node.getStartIndex()>=pa.getStartIndex() && 
                                node.getStopIndex()<=pa.getStopIndex()) {
                            pos = i;
                            break;
                        }
                    }
                }
            }
            Reference pr = getTarget(that);
            if (pr!=null) {
                List<Parameter> params = getParameters(pr);
                if (params!=null) { 
                    if (params.size()>pos) {
                        Parameter param = params.get(pos);
                        if (pr.getDeclaration().getQualifiedNameString().equals("ceylon.language::print")) {
                            requiredType = unit.getStringDeclaration().getType();
                        }
                        else {
                            requiredType = pr.getTypedParameter(param).getFullType();
                            if (param.isSequenced()) {
                                requiredType = unit.getIteratedType(requiredType);
                            }
                        }
                    }
                    else if (!params.isEmpty()) {
                        Parameter param = params.get(params.size()-1);
                        if (param.isSequenced()) {
                            requiredType = pr.getTypedParameter(param).getFullType();
                            requiredType = unit.getIteratedType(requiredType);
                        }
                    }
                }
            }
        }
        NamedArgumentList nal = that.getNamedArgumentList();
        if (nal!=null) {
            namedArgTarget = getTarget(that);
            if (namedArgTarget!=null) {
                List<Parameter> params = getParameters(namedArgTarget);
                if (params!=null && !params.isEmpty()) {
                    Parameter param = params.get(params.size()-1);
                    if (unit.isIterableType(param.getType())) {
                        requiredType = namedArgTarget.getTypedParameter(param).getFullType();
                        requiredType = unit.getIteratedType(requiredType);
                    }
                }
            }
        }
        if (node==that.getPositionalArgumentList() ||
            node==that.getNamedArgumentList()) {
            finalResult = requiredType;
        }
        if (nal!=null) {
            nal.visit(this);
        }
        if (pal!=null) {
            pal.visit(this);
        }
        requiredType = ort;
        namedArgTarget = onat;
    }

    private static Reference getTarget(Tree.InvocationExpression that) {
        Tree.Primary p = that.getPrimary();
        if (p instanceof Tree.MemberOrTypeExpression) {
            return ((Tree.MemberOrTypeExpression) p).getTarget();
        }
        else {
            return null;
        }
    }
    
    private static List<Parameter> getParameters(Reference pr) {
        Declaration declaration = pr.getDeclaration();
        if (declaration instanceof Functional) {
            List<ParameterList> pls = ((Functional) declaration).getParameterLists();
            return pls.isEmpty() ? null : pls.get(0).getParameters();
        }
        else {
            return null;
        }
    }
    
    @Override
    public void visit(Tree.SpecifiedArgument that) {
        Type ort = requiredType;
        Parameter p = that.getParameter();
        if (p!=null) {
            if (namedArgTarget!=null) {
                requiredType = namedArgTarget.getTypedParameter(p).getType();
            }
            else {
                requiredType = p.getType();            
            }
        }
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.ForIterator that) {
        Type ort = requiredType;
        requiredType = that.getUnit()
                .getIterableType(that.getUnit()
                        .getAnythingDeclaration().getType());
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.SpecifierStatement that) {
        Type ort = requiredType;
        requiredType = that.getBaseMemberExpression().getTypeModel();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.SwitchStatement that) {
        Type ort = requiredType;
        Tree.SwitchClause switchClause = that.getSwitchClause();
        Type srt = that.getUnit().getAnythingDeclaration().getType();
        if (switchClause!=null) {
            switchClause.visit(this);
            Tree.Expression e = 
                    switchClause.getSwitched().getExpression();
            Tree.Variable v = 
                    switchClause.getSwitched().getVariable();
            if (e!=null) {
                srt = e.getTypeModel();
            }
            else if (v!=null) {
                srt = v.getType().getTypeModel();
            }
            else {
                srt = null;
            }
        }
        SwitchCaseList switchCaseList = that.getSwitchCaseList();
        if (switchCaseList!=null) {
            for (Tree.CaseClause cc: switchCaseList.getCaseClauses()) {
                if (cc==node || cc.getCaseItem()==node) {
                    finalResult = srt;
                }
                if (cc.getCaseItem()!=null) {
                    requiredType = srt;
                    cc.getCaseItem().visit(this);
                }
                if (cc.getBlock()!=null) {
                    requiredType = ort;
                    cc.getBlock().visit(this);
                }
            }
        }
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.AnnotationList that) {
        Type ort = requiredType;
        requiredType = null;
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.AttributeDeclaration that) {
        Type ort = requiredType;
        requiredType = that.getType().getTypeModel();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.MethodDeclaration that) {
        Type ort = requiredType;
        requiredType = that.getType().getTypeModel();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.FunctionArgument that) {
        Type ort = requiredType;
        requiredType = that.getType().getTypeModel();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.AssignmentOp that) {
        Type ort = requiredType;
        requiredType = that.getLeftTerm().getTypeModel();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.Return that) {
        Type ort = requiredType;
        requiredType = getResultType(that.getDeclaration());
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.Throw that) {
        Type ort = requiredType;
        requiredType = that.getUnit().getExceptionDeclaration().getType();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.ConditionList that) {
        Type ort = requiredType;
        requiredType = that.getUnit().getBooleanDeclaration().getType();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.ResourceList that) {
        Type ort = requiredType;
        Unit unit = that.getUnit();
        requiredType = unionType(unit.getDestroyableDeclaration().getType(), 
                unit.getObtainableDeclaration().getType(), unit);
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.StringLiteral that) {
        Type ort = requiredType;
        super.visit(that); // pass on
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.DocLink that) {
        Type ort = requiredType;
        requiredType = getResultType(that.getBase());
        if (requiredType == null && that.getBase()!=null) {
            requiredType = that.getBase().getReference().getFullType();
        }
        super.visit(that);
        requiredType = ort;
    }
}