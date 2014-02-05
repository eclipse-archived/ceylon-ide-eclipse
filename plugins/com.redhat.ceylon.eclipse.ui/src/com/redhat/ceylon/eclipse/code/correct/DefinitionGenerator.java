package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.CreateParameterProposal.defaultValue;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ATTRIBUTE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CLASS;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.METHOD;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getRefinedProducedReference;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
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
	
	final String brokenName;
	final MemberOrTypeExpression node;
	final CompilationUnit rootNode;
	final String desc;
	final Image image;
	final ProducedType returnType;
	final LinkedHashMap<String, ProducedType> parameters;

	private DefinitionGenerator(String brokenName, 
			Tree.MemberOrTypeExpression node, 
			Tree.CompilationUnit rootNode,
		    String desc,
		    Image image,
		    ProducedType returnType,
		    LinkedHashMap<String, ProducedType> paramTypes) {
		this.brokenName = brokenName;
		this.node = node;
		this.rootNode = rootNode;
		this.desc = desc;
		this.image = image;
		this.returnType = returnType;
		this.parameters = paramTypes;
    }
	    
    String generateShared(String indent, String delim) {
    	return "shared " + generate(indent, delim);
    }
    
	String generate(String indent, String delim) {
        StringBuffer def = new StringBuffer();
        boolean isUpperCase = Character.isUpperCase(brokenName.charAt(0));
        final boolean isVoid = returnType==null;
        String stn = isVoid ? null : returnType.getProducedTypeName();
        if (parameters!=null) {            
            List<TypeParameter> typeParams = new ArrayList<TypeParameter>();
            StringBuilder typeParamDef = new StringBuilder();
            StringBuilder typeParamConstDef = new StringBuilder();
            appendTypeParams(typeParams, typeParamDef, typeParamConstDef, returnType);
            appendTypeParams(typeParams, typeParamDef, typeParamConstDef, parameters.values());
            if (typeParamDef.length() > 0) {
            	typeParamDef.insert(0, "<");
                typeParamDef.setLength(typeParamDef.length() - 1);
                typeParamDef.append(">");
            }
            
            String defIndent = getDefaultIndent();
			if (isUpperCase) {
                String supertype = isVoid ? "" : supertypeDeclaration(returnType);
                def.append("class ").append(brokenName).append(typeParamDef);
                appendParameters(parameters, def);
                def.append(supertype).append(typeParamConstDef)
                    .append(" {").append(delim);
                if (!isVoid) {
                    Collection<DeclarationWithProximity> members = 
                    		returnType.getDeclaration()
                    			.getMatchingMemberDeclarations(null, "", 0)
                    			.values();
					for (DeclarationWithProximity dwp: members) {
                        Declaration d = dwp.getDeclaration();
                        if (d.isFormal() /*&& td.isInheritedFromSupertype(d)*/) {
                            ProducedReference pr = 
                            		getRefinedProducedReference(returnType, d);
                            String text = getRefinementTextFor(d, pr, node.getUnit(), 
									false, "", false);
							def.append(indent).append(defIndent)
							    .append(text).append(delim);
                        }
                    }
                }
                def.append(indent).append("}");
            }
            else {
                String type = isVoid ? "void" : 
                    stn.equals("unknown") ? "function" : stn;
                String impl = isVoid ? " {}" : 
                		//removed because it's ugly for parameters:
                		//delim + indent + defIndent + defIndent +
                			" => nothing;";
                def.append(type).append(" ").append(brokenName).append(typeParamDef);
                appendParameters(parameters, def);
                def.append(typeParamConstDef).append(impl);
            }
        }
        else if (!isUpperCase) {
            String type = isVoid ? "Anything" : 
                stn.equals("unknown") ? "value" : stn;
            def.append(type).append(" ").append(brokenName).append(" = ")
            		.append(defaultValue(node.getUnit(), returnType))
            		.append(";");
        }
        else {
            throw new RuntimeException();
        }
        return def.toString();
	}

	static DefinitionGenerator create(String brokenName, 
			Tree.MemberOrTypeExpression node, 
			Tree.CompilationUnit rootNode) {
        boolean isUpperCase = Character.isUpperCase(brokenName.charAt(0));
        FindArgumentsVisitor fav = new FindArgumentsVisitor(node);
        rootNode.visit(fav);
        ProducedType et = fav.expectedType;
        final boolean isVoid = et==null;
        ProducedType returnType = isVoid ? null : node.getUnit().denotableType(et);
        StringBuilder params = new StringBuilder();
        LinkedHashMap<String, ProducedType> paramTypes = getParameters(fav);
        if (paramTypes!=null) {         
			if (isUpperCase) {
                String supertype = isVoid ? "" : supertypeDeclaration(returnType);
                String desc = "class '" + brokenName + params + supertype + "'";
        		return new DefinitionGenerator(brokenName, node, rootNode, 
        				desc, CLASS, returnType, paramTypes);
            }
            else {
                String desc = "function '" + brokenName + params + "'";
        		return new DefinitionGenerator(brokenName, node, rootNode, 
        				desc, METHOD, returnType, paramTypes);
            }
        }
        else if (!isUpperCase) {
            String desc = "value '" + brokenName + "'";
    		return new DefinitionGenerator(brokenName, node, rootNode, 
    				desc, ATTRIBUTE, returnType, null);
        }
        else {
            return null;
        }

	}

	private static String supertypeDeclaration(ProducedType returnType) {
	    String stn = returnType.getProducedTypeName();
	    if (stn.equals("unknown")) {
	    	return "";
	    }
	    else {
	        if (returnType.getDeclaration() instanceof Class) {
	            return " extends " + stn + "()"; //TODO: supertype arguments!
	        }
	        else {
	            return " satisfies " + stn;
	        }
	    }
    }

	private static LinkedHashMap<String,ProducedType> getParameters(
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

    private static void appendTypeParams(List<TypeParameter> typeParams, StringBuilder typeParamDef, 
    		StringBuilder typeParamConstDef, Collection<ProducedType> parameterTypes) {
        if (parameterTypes != null) {
            for (ProducedType pt: parameterTypes) {
                appendTypeParams(typeParams, typeParamDef, typeParamConstDef, pt);
            }
        }
    }
    
    private static void appendTypeParams(List<TypeParameter> typeParams, 
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
    
    private static LinkedHashMap<String,ProducedType> getParametersFromNamedArgs(Tree.NamedArgumentList nal) {
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

    private static LinkedHashMap<String,ProducedType> getParametersFromPositionalArgs(Tree.PositionalArgumentList pal) {
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
    
    private static void appendParameters(LinkedHashMap<String,ProducedType> parameters,
    		StringBuffer buffer) {
    	if (parameters.isEmpty()) {
    		buffer.append("()");
    	}
    	else {
    		buffer.append("(");
    		for (java.util.Map.Entry<String,ProducedType> e: parameters.entrySet()) {
    			buffer.append(e.getValue().getProducedTypeName()).append(" ")
    			        .append(e.getKey()).append(", ");
    		}
    		buffer.setLength(buffer.length()-2);
    		buffer.append(")");
    	}
    }

}