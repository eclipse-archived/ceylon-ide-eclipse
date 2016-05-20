package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.escaping_;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.Unit;

public abstract class DefinitionGenerator {
    
    abstract String generateShared(String indent, String delim);
    abstract String generate(String indent, String delim);
    abstract String generateSharedFormal(String indent, String delim);
    abstract boolean isFormalSupported();
    abstract Set<Declaration> getImports();
    abstract String getBrokenName();
    abstract String getDescription();
    abstract Type getReturnType();
    abstract Map<String, Type> getParameters();
    abstract Image getImage();
    abstract Tree.CompilationUnit getRootNode();
    abstract Node getNode();
    
    static void appendParameters(
            Map<String,Type> parameters,
            StringBuffer buffer, 
            TypeDeclaration supertype) {
        if (parameters.isEmpty()) {
            buffer.append("()");
        }
        else {
            buffer.append("(");
            for (Map.Entry<String,Type> e: 
                    parameters.entrySet()) {
                Declaration member = 
                        supertype.getMember(e.getKey(), 
                                null, false);
                if (member==null || !member.isFormal()) {
                    buffer.append(e.getValue().asString())
                        .append(" ");
                }
                buffer.append(e.getKey()).append(", ");
            }
            buffer.setLength(buffer.length()-2);
            buffer.append(")");
        }
    }

    private static String parameterName(
            Tree.Expression e, Type et) {
        Tree.Term term = e.getTerm();
        if (term instanceof Tree.StaticMemberOrTypeExpression) {
            Tree.StaticMemberOrTypeExpression smte = 
                    (Tree.StaticMemberOrTypeExpression) 
                        term;
            String id = 
                    smte.getIdentifier()
                        .getText();
            return escaping_.get_().toInitialLowercase(id);
        }
        else {
            if (et.isClassOrInterface() || 
                et.isTypeParameter()) {
                String tn = et.getDeclaration().getName();
                return escaping_.get_().toInitialLowercase(tn);
            }
            else {
                return "it";
            }
        }
    }

    private static Type parameterType(
            Node arg, Tree.Expression e) {
        Unit unit = arg.getUnit();
        Type et = e==null ? null : 
                e.getTypeModel();
        return et == null ? 
                unit.getAnythingType() : 
                unit.denotableType(et);
    }
    static LinkedHashMap<String,Type> 
    getParametersFromPositionalArgs(
            Tree.PositionalArgumentList pal) {
        LinkedHashMap<String,Type> types = 
                new LinkedHashMap<String,Type>();
        int i=0;
        for (Tree.PositionalArgument pa: 
                pal.getPositionalArguments()) {
            if (pa instanceof Tree.ListedArgument) {
                Tree.ListedArgument la = 
                        (Tree.ListedArgument) pa;
                Tree.Expression e = la.getExpression();
                Type type = parameterType(pa, e);
                String name = parameterName(e, type);
                if (types.containsKey(name)) {
                    name = name + ++i;
                }
                types.put(name, type);
            }
        }
        return types;
    }
    
    static LinkedHashMap<String,Type> 
    getParametersFromNamedArgs(Tree.NamedArgumentList nal) {
        LinkedHashMap<String,Type> types = 
                new LinkedHashMap<String,Type>();
        int i=0;
        for (Tree.NamedArgument a: nal.getNamedArguments()) {
            if (a instanceof Tree.SpecifiedArgument) {
                Tree.SpecifiedArgument na = 
                        (Tree.SpecifiedArgument) a;
                Tree.Expression e = 
                        na.getSpecifierExpression()
                            .getExpression();
                Tree.Identifier id = na.getIdentifier();
                Type type = parameterType(a, e);
                String name = id==null ? 
                        parameterName(e, type) : 
                        id.getText();
                if (types.containsKey(name)) {
                    name = name + ++i;
                }
                types.put(name, type);
            }
        }
        return types;
    }

    static void appendTypeParams(
            List<TypeParameter> typeParams, 
            StringBuilder typeParamDef, 
            StringBuilder typeParamConstDef, 
            TypeParameter typeParam) {
        if (typeParams.contains(typeParam)) {
            return;
        } else {
            typeParams.add(typeParam);
        }
        
        if (typeParam.isContravariant()) {
            typeParamDef.append("in ");
        }
        if (typeParam.isCovariant()) {
            typeParamDef.append("out ");
        }
        typeParamDef.append(typeParam.getName());
        Type dta = typeParam.getDefaultTypeArgument();
        if (typeParam.isDefaulted() && dta!=null) {
            typeParamDef.append("=");
            typeParamDef.append(dta.asString());
        }
        typeParamDef.append(",");
        
        if (typeParam.isConstrained()) {
            typeParamConstDef.append(" given ");
            typeParamConstDef.append(typeParam.getName());
    
            List<Type> satisfiedTypes = 
                    typeParam.getSatisfiedTypes();
            if (satisfiedTypes != null && 
                    !satisfiedTypes.isEmpty()) {
                typeParamConstDef.append(" satisfies ");
                boolean firstSatisfiedType = true;
                for (Type satisfiedType : satisfiedTypes) {
                    if (firstSatisfiedType) {
                        firstSatisfiedType = false;
                    } else {
                        typeParamConstDef.append("&");
                    }
                    typeParamConstDef.append(satisfiedType.asString());
                }
            }
    
            List<Type> caseTypes = 
                    typeParam.getCaseTypes();
            if (caseTypes != null && !caseTypes.isEmpty()) {
                typeParamConstDef.append(" of ");
                boolean firstCaseType = true;
                for (Type caseType : caseTypes) {
                    if (firstCaseType) {
                        firstCaseType = false;
                    } else {
                        typeParamConstDef.append("|");
                    }
                    typeParamConstDef.append(caseType.asString());
                }
            }
            
        }        
    }

    static void appendTypeParams(
            List<TypeParameter> typeParams, 
            StringBuilder typeParamDef, 
            StringBuilder typeParamConstDef, 
            Type pt) {
        if (pt != null) {
            if (pt.isUnion()) {
                DefinitionGenerator.appendTypeParams(typeParams, 
                        typeParamDef, typeParamConstDef, 
                        pt.getCaseTypes());
            }
            else if (pt.isIntersection()) {
                DefinitionGenerator.appendTypeParams(typeParams, 
                        typeParamDef, typeParamConstDef, 
                        pt.getSatisfiedTypes());
            }
            else if (pt.isTypeParameter()) {
                appendTypeParams(typeParams, 
                        typeParamDef, typeParamConstDef, 
                        (TypeParameter) pt.getDeclaration());
            }
        }
    }

    static void appendTypeParams(List<TypeParameter> typeParams, 
            StringBuilder typeParamDef, 
            StringBuilder typeParamConstDef, 
            Collection<Type> parameterTypes) {
        if (parameterTypes != null) {
            for (Type pt: parameterTypes) {
                appendTypeParams(typeParams, typeParamDef, 
                        typeParamConstDef, pt);
            }
        }
    }

//    static LinkedHashMap<String,Type> getParameters(
//            FindArgumentsVisitor fav) {
//        if (fav.positionalArgs!=null) {
//            return getParametersFromPositionalArgs(
//                    fav.positionalArgs);
//        }
//        else if (fav.namedArgs!=null) {
//            return getParametersFromNamedArgs(
//                    fav.namedArgs);
//        }
//        else {
//            return null;
//        }
//    }

}
