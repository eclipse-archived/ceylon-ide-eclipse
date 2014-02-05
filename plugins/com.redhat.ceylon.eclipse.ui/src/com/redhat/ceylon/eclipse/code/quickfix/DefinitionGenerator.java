package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ATTRIBUTE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CLASS;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.METHOD;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getRefinedProducedReference;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.quickfix.CreateProposal.defaultValue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.UnionType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberOrTypeExpression;

class DefinitionGenerator {
	
	String brokenName;
	MemberOrTypeExpression node;
	CompilationUnit cu;

	public DefinitionGenerator(String brokenName, 
			Tree.MemberOrTypeExpression node, 
			Tree.CompilationUnit cu) {
		this.brokenName = brokenName;
		this.node = node;
		this.cu = cu;
    }
	
    String def;
    String desc;
    Image image;
    ProducedType returnType;
    List<ProducedType> paramTypes;
    boolean generated;
    
    void generateShared(String indent, String delim) {
    	generate(indent, delim);
    	def = "shared " + def;
    }
    
	void generate(String indent, String delim) {
        boolean isUpperCase = Character.isUpperCase(brokenName.charAt(0));
        FindArgumentsVisitor fav = new FindArgumentsVisitor(node);
        cu.visit(fav);
        ProducedType et = fav.expectedType;
        final boolean isVoid = et==null;
        returnType = isVoid ? null : node.getUnit().denotableType(et);
        String stn = isVoid ? null : returnType.getProducedTypeName();
        if (fav.positionalArgs!=null || fav.namedArgs!=null) {
            StringBuilder params = new StringBuilder();
            params.append("(");
            if (fav.positionalArgs!=null) {
            	paramTypes = CeylonQuickFixAssistant.appendPositionalArgs(fav, params);
            }
            if (fav.namedArgs!=null) {
            	paramTypes = CeylonQuickFixAssistant.appendNamedArgs(fav, params);
            }
            if (params.length()>1) {
                params.setLength(params.length()-2);
            }
            params.append(")");
            
            List<TypeParameter> typeParams = new ArrayList<TypeParameter>();
            StringBuilder typeParamDef = new StringBuilder("<");
            StringBuilder typeParamConstDef = new StringBuilder();
            appendTypeParams(typeParams, typeParamDef, typeParamConstDef, returnType);
            appendTypeParams(typeParams, typeParamDef, typeParamConstDef, paramTypes);
            if (typeParamDef.length() > 1) {
                typeParamDef.setLength(typeParamDef.length() - 1);
                typeParamDef.append(">");
            }
            else {
                typeParamDef.setLength(0);
            }
            
            String defIndent = getDefaultIndent();
			if (isUpperCase) {
                String supertype = "";
                if (!isVoid) {
                    if (!stn.equals("unknown")) {
                        if (et.getDeclaration() instanceof Class) {
                            supertype = " extends " + stn + "()"; //TODO: arguments!
                        }
                        else {
                            supertype = " satisfies " + stn;
                        }
                    }
                }
                def = "class " + brokenName + typeParamDef + params + supertype + 
                		typeParamConstDef + " {" + delim;
                if (!isVoid) {
                    for (DeclarationWithProximity dwp: et.getDeclaration()
                    		.getMatchingMemberDeclarations(null, "", 0).values()) {
                        Declaration d = dwp.getDeclaration();
                        if (d.isFormal() /*&& td.isInheritedFromSupertype(d)*/) {
                            ProducedReference pr = getRefinedProducedReference(et, d);
                            def += indent + defIndent + 
                            		getRefinementTextFor(d, pr, node.getUnit(), false, 
                            				"", false) + 
                            		delim;
                        }
                    }
                }
                def += indent + "}";
                desc = "class '" + brokenName + params + supertype + "'";
                image = CLASS;
        		generated = true;
            }
            else {
                String type = isVoid ? "void" : 
                    stn.equals("unknown") ? "function" : stn;
                String impl = isVoid ? 
                		" {}" : 
                		//removed because it's ugly for parameters:
                		//delim + indent + defIndent + defIndent +
                			" => nothing;";
                def = type + " " + brokenName + typeParamDef + params + 
                		typeParamConstDef + impl;
                desc = "function '" + brokenName + params + "'";
                image = METHOD;
        		generated = true;
            }
        }
        else if (!isUpperCase) {
            String type = isVoid ? "Anything" : 
                stn.equals("unknown") ? "value" : stn;
            def = type + " " + brokenName + " = " + 
                defaultValue(node.getUnit(), et) + ";";
            desc = "value '" + brokenName + "'";
            image = ATTRIBUTE;
    		generated = true;
        }
        else {
            return;
        }

	}

    static void appendTypeParams(List<TypeParameter> typeParams, StringBuilder typeParamDef, 
    		StringBuilder typeParamConstDef, List<ProducedType> pts) {
        if (pts != null) {
            for (ProducedType pt : pts) {
                appendTypeParams(typeParams, typeParamDef, typeParamConstDef, pt);
            }
        }
    }
    
    static void appendTypeParams(List<TypeParameter> typeParams, 
    		StringBuilder typeParamDef, StringBuilder typeParamConstDef, 
    		ProducedType pt) {
        if (pt != null) {
            if (pt.getDeclaration() instanceof UnionType) {
                appendTypeParams(typeParams, typeParamDef, typeParamConstDef, 
                		((UnionType) pt.getDeclaration()).getCaseTypes());
            }
            else if (pt.getDeclaration() instanceof IntersectionType) {
                appendTypeParams(typeParams, typeParamDef, typeParamConstDef, 
                		((IntersectionType) pt.getDeclaration()).getSatisfiedTypes());
            }
            else if (pt.getDeclaration() instanceof TypeParameter) {
                appendTypeParams(typeParams, typeParamDef, typeParamConstDef, 
                		(TypeParameter) pt.getDeclaration());
            }
        }
    }

    private static void appendTypeParams(List<TypeParameter> typeParams, 
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
    
}