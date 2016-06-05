package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.util.Types.getResultType;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.unionType;

import java.util.List;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.NamedArgumentList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.SwitchCaseList;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;

@Deprecated
class RequiredTypeVisitor 
        extends Visitor implements Types.Required {
    
    private Node node;
    private Type requiredType = null;
    private Type finalResult = null;
    private Reference namedArgTarget = null;
    private Token token;
    private String parameterName;
    
    public Type getType() {
        return finalResult;
    }
    
    public String getParameterName() {
        return parameterName;
    }
    
    public RequiredTypeVisitor(Node node, Token token) {
        this.node = node;
        this.token = token;
    }
    
    @Override
    public void visitAny(Node that) {
        if (node==that) {
            finalResult=requiredType;
            if (that instanceof Tree.PositionalArgument) {
                Tree.PositionalArgument pa = 
                        (Tree.PositionalArgument) that;
                Parameter parameter = pa.getParameter();
                if (parameter!=null) {
                    parameterName = parameter.getName();
                }
            }
            else if (that instanceof Tree.NamedArgument) {
                Tree.NamedArgument na = 
                        (Tree.NamedArgument) that;
                Parameter parameter = na.getParameter();
                if (parameter!=null) {
                    parameterName = parameter.getName();
                }
            }
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
        Tree.PositionalArgumentList pal = 
                that.getPositionalArgumentList();
        Unit unit = that.getUnit();
        if (pal!=null) {
            int pos;
            List<Tree.PositionalArgument> pas = 
                    pal.getPositionalArguments();
            if (pas.isEmpty()) {
                pos = 0;
            }
            else {
                pos = pas.size(); //default to the last argument if incomplete
                for (int i=0; i<pas.size(); i++) {
                    Tree.PositionalArgument pa = pas.get(i);
                    if (token!=null) {
                        CommonToken tok = (CommonToken) token;
                        int tokenEnd = tok.getStopIndex()+1;
                        if (pa.getEndIndex()>=tokenEnd) {
                            pos = i;
                            break;
                        }
                    }
                    else {
                        if (node.getStartIndex()>=pa.getStartIndex() && 
                            node.getEndIndex()<=pa.getEndIndex()) {
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
                        String name = 
                                pr.getDeclaration()
                                    .getQualifiedNameString();
                        if (name.equals("ceylon.language::print")) {
                            requiredType = 
                                    unit.getStringDeclaration()
                                        .getType();
                        }
                        else {
                            requiredType = 
                                    pr.getTypedParameter(param)
                                        .getFullType();
                            if (param.isSequenced()) {
                                requiredType = 
                                        unit.getIteratedType(
                                                requiredType);
                            }
                        }
                    }
                    else if (!params.isEmpty()) {
                        Parameter param = 
                                params.get(params.size()-1);
                        if (param.isSequenced()) {
                            requiredType = 
                                    pr.getTypedParameter(param)
                                        .getFullType();
                            requiredType = 
                                    unit.getIteratedType(
                                            requiredType);
                        }
                    }
                }
            }
            else {
                //indirect invocations
                Type ct = that.getPrimary().getTypeModel();
                if (ct!=null && unit.isCallableType(ct)) {
                    List<Type> pts = 
                            unit.getCallableArgumentTypes(ct);
                    if (pts.size()>pos) {
                        requiredType = pts.get(pos);
                    }
                }
            }
        }
        NamedArgumentList nal = that.getNamedArgumentList();
        if (nal!=null) {
            namedArgTarget = getTarget(that);
            if (namedArgTarget!=null) {
                List<Parameter> params = 
                        getParameters(namedArgTarget);
                if (params!=null && !params.isEmpty()) {
                    Parameter param = 
                            params.get(params.size()-1);
                    if (unit.isIterableType(param.getType())) {
                        requiredType = 
                                namedArgTarget
                                    .getTypedParameter(param)
                                    .getFullType();
                        requiredType = 
                                unit.getIteratedType(
                                        requiredType);
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
            Tree.MemberOrTypeExpression mte = 
                    (Tree.MemberOrTypeExpression) p;
            return mte.getTarget();
        }
        else {
            return null;
        }
    }
    
    private static List<Parameter> getParameters(Reference pr) {
        Declaration declaration = pr.getDeclaration();
        if (declaration instanceof Functional) {
            Functional fun = (Functional) declaration;
            List<ParameterList> pls = fun.getParameterLists();
            return pls.isEmpty() ? null : 
                pls.get(0).getParameters();
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
                requiredType = 
                        namedArgTarget
                            .getTypedParameter(p)
                            .getType();
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
        Unit unit = that.getUnit();
        requiredType = 
                unit.getIterableType(
                        unit.getAnythingType());
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.SpecifierStatement that) {
        Type ort = requiredType;
        requiredType = 
                that.getBaseMemberExpression()
                    .getTypeModel();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.SwitchStatement that) {
        Type ort = requiredType;
        Tree.SwitchClause switchClause = 
                that.getSwitchClause();
        Type srt = that.getUnit().getAnythingType();
        if (switchClause!=null) {
            switchClause.visit(this);
            Tree.Expression e = 
                    switchClause.getSwitched()
                        .getExpression();
            Tree.Variable v = 
                    switchClause.getSwitched()
                        .getVariable();
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
        SwitchCaseList switchCaseList = 
                that.getSwitchCaseList();
        if (switchCaseList!=null) {
            for (Tree.CaseClause cc: 
                    switchCaseList.getCaseClauses()) {
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
    public void visit(Tree.SwitchExpression that) {
        Type ort = requiredType;
        Tree.SwitchClause switchClause = 
                that.getSwitchClause();
        Type srt;
        if (switchClause!=null) {
            switchClause.visit(this);
            Tree.Expression e = 
                    switchClause.getSwitched()
                        .getExpression();
            Tree.Variable v = 
                    switchClause.getSwitched()
                        .getVariable();
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
        else {
            srt = null;
        }
        SwitchCaseList switchCaseList = 
                that.getSwitchCaseList();
        if (switchCaseList!=null) {
            for (Tree.CaseClause cc: 
                    switchCaseList.getCaseClauses()) {
                if (cc==node || cc.getCaseItem()==node) {
                    finalResult = srt;
                }
                if (cc.getCaseItem()!=null) {
                    requiredType = srt;
                    cc.getCaseItem().visit(this);
                }
                if (cc.getExpression()!=null) {
                    requiredType = ort;
                    cc.getExpression().visit(this);
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
        requiredType = 
                getResultType(that.getDeclaration());
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.Throw that) {
        Type ort = requiredType;
        requiredType = that.getUnit().getExceptionType();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.ConditionList that) {
        Type ort = requiredType;
        requiredType = that.getUnit().getBooleanType();
        super.visit(that);
        requiredType = ort;
    }
    
    @Override
    public void visit(Tree.ResourceList that) {
        Type ort = requiredType;
        Unit unit = that.getUnit();
        requiredType = 
                unionType(unit.getDestroyableType(), 
                        unit.getObtainableType(), unit);
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
        Declaration base = that.getBase();
        requiredType = getResultType(base);
        if (requiredType == null && base!=null) {
            requiredType = 
                    base.getReference()
                        .getFullType();
        }
        super.visit(that);
        requiredType = ort;
    }
}