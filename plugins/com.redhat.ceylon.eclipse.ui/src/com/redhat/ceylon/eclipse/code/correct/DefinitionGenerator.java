package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public abstract class DefinitionGenerator {
    
    abstract String generateShared(String indent, String delim);
    abstract String generate(String indent, String delim);
    abstract String generateSharedFormal(String indent, String delim);
    abstract boolean isFormalSupported();
    abstract Set<Declaration> getImports();
    abstract String getBrokenName();
    abstract String getDescription();
    abstract ProducedType getReturnType();
    abstract LinkedHashMap<String, ProducedType> getParameters();
    abstract Image getImage();
    abstract Tree.CompilationUnit getRootNode();
    abstract Node getNode();
    
    static void appendParameters(LinkedHashMap<String,ProducedType> parameters,
            StringBuffer buffer, TypeDeclaration supertype) {
        if (parameters.isEmpty()) {
            buffer.append("()");
        }
        else {
            buffer.append("(");
            for (Map.Entry<String,ProducedType> e: parameters.entrySet()) {
                Declaration member = supertype.getMember(e.getKey(), null, false);
                if (member==null || !member.isFormal()) {
                    buffer.append(e.getValue().getProducedTypeName()).append(" ");
                }
                buffer.append(e.getKey()).append(", ");
            }
            buffer.setLength(buffer.length()-2);
            buffer.append(")");
        }
    }

    static LinkedHashMap<String,ProducedType> getParametersFromPositionalArgs(Tree.PositionalArgumentList pal) {
        LinkedHashMap<String,ProducedType> types = new LinkedHashMap<String,ProducedType>();
        int i=0;
        for (Tree.PositionalArgument pa: pal.getPositionalArguments()) {
            if (pa instanceof Tree.ListedArgument) {
                Tree.Expression e = ((Tree.ListedArgument) pa).getExpression();
                ProducedType et = e.getTypeModel();
                String name;
                ProducedType t;
                if (et == null) {
                    t = pa.getUnit().getAnythingDeclaration().getType();
                    name = "arg";
                }
                else {
                    t = pa.getUnit().denotableType(et);
                    if (e.getTerm() instanceof Tree.StaticMemberOrTypeExpression) {
                        String id = ((Tree.StaticMemberOrTypeExpression) e.getTerm())
                                .getIdentifier().getText();
                        name = Character.toLowerCase(id.charAt(0)) + id.substring(1);
                    }
                    else {
                        if (et.getDeclaration() instanceof ClassOrInterface || 
                            et.getDeclaration() instanceof TypeParameter) {
                            String tn = et.getDeclaration().getName();
                            name = Character.toLowerCase(tn.charAt(0)) + tn.substring(1);
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

    static LinkedHashMap<String,ProducedType> getParametersFromNamedArgs(Tree.NamedArgumentList nal) {
        LinkedHashMap<String,ProducedType> types = new LinkedHashMap<String,ProducedType>();
        int i=0;
        for (Tree.NamedArgument a: nal.getNamedArguments()) {
            if (a instanceof Tree.SpecifiedArgument) {
                Tree.SpecifiedArgument na = (Tree.SpecifiedArgument) a;
                Tree.Expression e = na.getSpecifierExpression().getExpression();
                String name = na.getIdentifier().getText();
                ProducedType t;
                if (e==null) {
                    t = a.getUnit().getAnythingDeclaration().getType();
                }
                else {
                    t = a.getUnit().denotableType(e.getTypeModel());
                }
                if (types.containsKey(name)) {
                    name = name + ++i;
                }
                types.put(name, t);
            }
        }
        return types;
    }

    static void appendTypeParams(List<TypeParameter> typeParams, 
            StringBuilder typeParamDef, StringBuilder typeParamConstDef, 
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
        if (typeParam.isDefaulted() && typeParam.getDefaultTypeArgument() != null) {
            typeParamDef.append("=");
            typeParamDef.append(typeParam.getDefaultTypeArgument().getProducedTypeName());
        }
        typeParamDef.append(",");
        
        if (typeParam.isConstrained()) {
            typeParamConstDef.append(" given ");
            typeParamConstDef.append(typeParam.getName());
    
            List<ProducedType> satisfiedTypes = typeParam.getSatisfiedTypes();
            if (satisfiedTypes != null && !satisfiedTypes.isEmpty()) {
                typeParamConstDef.append(" satisfies ");
                boolean firstSatisfiedType = true;
                for (ProducedType satisfiedType : satisfiedTypes) {
                    if (firstSatisfiedType) {
                        firstSatisfiedType = false;
                    } else {
                        typeParamConstDef.append("&");
                    }
                    typeParamConstDef.append(satisfiedType.getProducedTypeName());
                }
            }
    
            List<ProducedType> caseTypes = typeParam.getCaseTypes();
            if (caseTypes != null && !caseTypes.isEmpty()) {
                typeParamConstDef.append(" of ");
                boolean firstCaseType = true;
                for (ProducedType caseType : caseTypes) {
                    if (firstCaseType) {
                        firstCaseType = false;
                    } else {
                        typeParamConstDef.append("|");
                    }
                    typeParamConstDef.append(caseType.getProducedTypeName());
                }
            }
            
            if (typeParam.getParameterLists() != null) {
                for (ParameterList paramList : typeParam.getParameterLists()) {
                    if (paramList != null && paramList.getParameters() != null) {
                        typeParamConstDef.append("(");
                        boolean firstParam = true;
                        for (Parameter param : paramList.getParameters()) {
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
            ProducedType pt) {
        if (pt != null) {
            if (pt.getDeclaration() instanceof UnionType) {
                DefinitionGenerator.appendTypeParams(typeParams, typeParamDef, typeParamConstDef, 
                        ((UnionType) pt.getDeclaration()).getCaseTypes());
            }
            else if (pt.getDeclaration() instanceof IntersectionType) {
                DefinitionGenerator.appendTypeParams(typeParams, typeParamDef, typeParamConstDef, 
                        ((IntersectionType) pt.getDeclaration()).getSatisfiedTypes());
            }
            else if (pt.getDeclaration() instanceof TypeParameter) {
                appendTypeParams(typeParams, typeParamDef, typeParamConstDef, 
                        (TypeParameter) pt.getDeclaration());
            }
        }
    }

    static void appendTypeParams(List<TypeParameter> typeParams, StringBuilder typeParamDef, 
            StringBuilder typeParamConstDef, Collection<ProducedType> parameterTypes) {
        if (parameterTypes != null) {
            for (ProducedType pt: parameterTypes) {
                appendTypeParams(typeParams, typeParamDef, typeParamConstDef, pt);
            }
        }
    }

    static LinkedHashMap<String,ProducedType> getParameters(
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
