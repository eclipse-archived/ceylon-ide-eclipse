package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.util.Escaping.toInitialLowercase;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
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
    abstract LinkedHashMap<String, Type> getParameters();
    abstract Image getImage();
    abstract Tree.CompilationUnit getRootNode();
    abstract Node getNode();
    
    static void appendParameters(
            LinkedHashMap<String,Type> parameters,
            StringBuffer buffer, TypeDeclaration supertype) {
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
                    buffer.append(e.getValue().getProducedTypeName()).append(" ");
                }
                buffer.append(e.getKey()).append(", ");
            }
            buffer.setLength(buffer.length()-2);
            buffer.append(")");
        }
    }

    static LinkedHashMap<String,Type> 
    getParametersFromPositionalArgs(Tree.PositionalArgumentList pal) {
        LinkedHashMap<String,Type> types = 
                new LinkedHashMap<String,Type>();
        int i=0;
        for (Tree.PositionalArgument pa: 
                pal.getPositionalArguments()) {
            if (pa instanceof Tree.ListedArgument) {
                Tree.ListedArgument la = 
                        (Tree.ListedArgument) pa;
                Tree.Expression e = la.getExpression();
                Type et = e.getTypeModel();
                String name;
                Type t;
                Unit unit = pa.getUnit();
                if (et == null) {
                    t = unit.getAnythingType();
                    name = "arg";
                }
                else {
                    t = unit.denotableType(et);
                    Tree.Term term = e.getTerm();
                    if (term instanceof Tree.StaticMemberOrTypeExpression) {
                        Tree.StaticMemberOrTypeExpression smte = 
                                (Tree.StaticMemberOrTypeExpression) 
                                    term;
                        String id = 
                                smte.getIdentifier().getText();
                        name = toInitialLowercase(id);
                    }
                    else {
                        if (et.isClassOrInterface() || 
                            et.isTypeParameter()) {
                            String tn = et.getDeclaration().getName();
                            name = toInitialLowercase(tn);
                        }
                        else {
                            name = "arg";
                        }
                    }
                }
                if (types.containsKey(name)) {
                    name = name + ++i;
                }
                types.put(name, t);
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
                String name = na.getIdentifier().getText();
                Type t;
                Unit unit = a.getUnit();
                if (e==null) {
                    t = unit.getAnythingType();
                }
                else {
                    t = unit.denotableType(e.getTypeModel());
                }
                if (types.containsKey(name)) {
                    name = name + ++i;
                }
                types.put(name, t);
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
        if (typeParam.isDefaulted() && dta != null) {
            typeParamDef.append("=");
            typeParamDef.append(dta.getProducedTypeName());
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
                    typeParamConstDef.append(satisfiedType.getProducedTypeName());
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
                    typeParamConstDef.append(caseType.getProducedTypeName());
                }
            }
            
            if (typeParam.getParameterLists() != null) {
                for (ParameterList paramList : 
                        typeParam.getParameterLists()) {
                    if (paramList != null && 
                            paramList.getParameters() != null) {
                        typeParamConstDef.append("(");
                        boolean firstParam = true;
                        for (Parameter param : 
                                paramList.getParameters()) {
                            if (firstParam) {
                                firstParam = false;
                            } else {
                                typeParamConstDef.append(",");
                            }
                            typeParamConstDef.append(param.getType().getProducedTypeName());
                            typeParamConstDef.append(" ");
                            typeParamConstDef.append(param.getName());
                        }
                        typeParamConstDef.append(")");
                    }
                }
            }
        }        
    }

    static void appendTypeParams(List<TypeParameter> typeParams, 
            StringBuilder typeParamDef, StringBuilder typeParamConstDef, 
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

    static LinkedHashMap<String,Type> getParameters(
            FindArgumentsVisitor fav) {
        if (fav.positionalArgs!=null) {
            return getParametersFromPositionalArgs(fav.positionalArgs);
        }
        else if (fav.namedArgs!=null) {
            return getParametersFromNamedArgs(fav.namedArgs);
        }
        else {
            return null;
        }
    }

}
