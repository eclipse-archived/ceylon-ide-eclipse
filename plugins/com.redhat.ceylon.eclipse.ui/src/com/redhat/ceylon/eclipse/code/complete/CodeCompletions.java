package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.OccurrenceLocation.EXTENDS;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDefaultValue;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ANN_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ID_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.KW_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.TYPE_STYLER;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.StyledString;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.ProducedTypedReference;
import com.redhat.ceylon.compiler.typechecker.model.TypeAlias;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.UnknownType;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CodeCompletions {

    private static boolean forceExplicitTypeArgs(Declaration d, 
    		OccurrenceLocation ol) {
        if (ol==EXTENDS) {
            return true;
        }
        else {
            //TODO: this is a pretty limited implementation 
            //      for now, but eventually we could do 
            //      something much more sophisticated to
            //      guess is explicit type args will be
            //      necessary (variance, etc)
            if (d instanceof Functional) {
                List<ParameterList> pls = ((Functional) d).getParameterLists();
                return pls.isEmpty() || 
                		pls.get(0).getParameters().isEmpty();
            }
            else {
                return false;
            }
        }
    }
    
    static String getTextForDocLink(CeylonParseController cpc, 
    		DeclarationWithProximity d) {
        
        StringBuilder result = new StringBuilder();
        
        Declaration decl = d.getDeclaration();
        Package pkg = decl.getUnit().getPackage();
        
        // handle language package or same module and package
        if (pkg != null && (Module.LANGUAGE_MODULE_NAME.equals(pkg.getNameAsString())
                            ||(cpc.getRootNode().getUnit() != null && pkg.equals(
                                    cpc.getRootNode().getUnit().getPackage())))
                ) {
            if (decl.isToplevel()) {
                result.append(decl.getNameAsString());
            } else { // not top level in language module
                int loc = decl.getQualifiedNameString().indexOf("::");
                if (loc != -1) {
                    result.append(decl.getQualifiedNameString().substring(loc + 2));
                }
            }
        } 
        
        // no special case
        if (result.length() == 0) {
            result.append(decl.getQualifiedNameString());
        }
        
        return result.toString();
    }
    
    static String getTextFor(DeclarationWithProximity d) {
        StringBuilder result = new StringBuilder();
        result.append(escapeName(d));
        appendTypeParameters(d.getDeclaration(), result);
        return result.toString();
    }
    
    public static String getPositionalInvocationTextFor(
    		DeclarationWithProximity d, OccurrenceLocation ol, 
    		ProducedReference pr, Unit unit, 
    		boolean includeDefaulted, String typeArgs) {
        StringBuilder result = new StringBuilder(escapeName(d));
        Declaration dd = d.getDeclaration();
        if (typeArgs!=null) {
        	result.append(typeArgs);
        }
        else if (forceExplicitTypeArgs(dd, ol)) {
            appendTypeParameters(dd, result);
        }
        appendPositionalArgs(dd, pr, unit, result, includeDefaulted);
        appendSemiToVoidInvocation(result, dd);
        return result.toString();
    }

    static String getNamedInvocationTextFor(DeclarationWithProximity d, 
            ProducedReference pr, Unit unit, boolean includeDefaulted) {
        StringBuilder result = new StringBuilder(escapeName(d));
        Declaration dd = d.getDeclaration();
        if (forceExplicitTypeArgs(dd, null))
            appendTypeParameters(dd, result);
        appendNamedArgs(dd, pr, unit, result, includeDefaulted, false);
        appendSemiToVoidInvocation(result, dd);
        return result.toString();
    }
    
    private static void appendSemiToVoidInvocation(StringBuilder result,
            Declaration dd) {
        if ((dd instanceof Method) && ((Method) dd).isDeclaredVoid() && 
                ((Method) dd).getParameterLists().size()==1) {
            result.append(';');
        }
    }
    
    static String getDescriptionFor(DeclarationWithProximity d) {
        StringBuilder result = new StringBuilder(d.getName());
        appendTypeParameters(d.getDeclaration(), result);
        return result.toString();
    }
    
    static String getPositionalInvocationDescriptionFor(
    		DeclarationWithProximity d, OccurrenceLocation ol, 
    		ProducedReference pr, Unit unit, boolean includeDefaulted, 
            String typeArgs) {
        StringBuilder result = new StringBuilder(d.getName());
        if (typeArgs!=null) {
        	result.append(typeArgs);
        }
        else if (forceExplicitTypeArgs(d.getDeclaration(), ol)) {
            appendTypeParameters(d.getDeclaration(), result);
        }
        appendPositionalArgs(d.getDeclaration(), pr, unit, result, 
        		includeDefaulted);
        return result.toString();
    }
    
    static String getNamedInvocationDescriptionFor(
    		DeclarationWithProximity d, ProducedReference pr, 
    		Unit unit, boolean includeDefaulted) {
        StringBuilder result = new StringBuilder(d.getName());
        if (forceExplicitTypeArgs(d.getDeclaration(), null))
            appendTypeParameters(d.getDeclaration(), result);
        appendNamedArgs(d.getDeclaration(), pr, unit, result, 
        		includeDefaulted, true);
        return result.toString();
    }
    
    public static String getRefinementTextFor(Declaration d, 
    		ProducedReference pr, Unit unit, boolean isInterface, 
    		ClassOrInterface ci, String indent, boolean containsNewline) {
    	return getRefinementTextFor(d, pr, unit, isInterface, ci, 
    			indent, containsNewline, true);
    }
    
    public static String getRefinementTextFor(Declaration d, 
    		ProducedReference pr, Unit unit, boolean isInterface,
    		ClassOrInterface ci, String indent, boolean containsNewline, 
    		boolean preamble) {
    	StringBuilder result = new StringBuilder();
    	if (preamble) {
    		result.append("shared actual ");
    		if (isVariable(d) && !isInterface) {
    			result.append("variable ");
    		}
    	}
        appendDeclarationText(d, pr, unit, result);
        appendTypeParameters(d, result);
        appendParameters(d, pr, unit, result);
        if (d instanceof Class) {
            result.append(extraIndent(extraIndent(indent, containsNewline), 
            		containsNewline))
                .append(" extends super.").append(d.getName());
            appendPositionalArgs(d, pr, unit, result, true);
        }
        appendConstraints(d, pr, unit, indent, containsNewline, result);
        appendImpl(d, pr, isInterface, unit, indent, result, ci);
        return result.toString();
    }

    private static void appendConstraints(Declaration d, ProducedReference pr,
            Unit unit, String indent, boolean containsNewline, 
            StringBuilder result) {
        if (d instanceof Functional) {
            for (TypeParameter tp: ((Functional) d).getTypeParameters()) {
                List<ProducedType> sts = tp.getSatisfiedTypes();
                if (!sts.isEmpty()) {
                    result.append(extraIndent(extraIndent(indent, containsNewline), 
                    		containsNewline))
                        .append("given ").append(tp.getName())
                        .append(" satisfies ");
                    boolean first = true;
                    for (ProducedType st: sts) {
                        if (first) {
                            first = false;
                        }
                        else {
                            result.append("&");
                        }
                        result.append(st.substitute(pr.getTypeArguments())
                                .getProducedTypeName(unit));
                    }
                }
            }
        }
    }

    static String getInlineFunctionTextFor(Parameter p, 
    		ProducedReference pr, Unit unit, String indent) {
        StringBuilder result = new StringBuilder();
        appendNamedArgumentText(p, pr, result);
        appendTypeParameters(p.getModel(), result);
        appendParameters(p.getModel(), pr, unit, result);
        if (p.isDeclaredVoid()) {
            result.append(" {}");
        }
        else {
            result.append(" => nothing;");
        }
        return result.toString();
    }

    private static boolean isVariable(Declaration d) {
        return d instanceof TypedDeclaration && 
        		((TypedDeclaration) d).isVariable();
    }
    
    static String getRefinementDescriptionFor(Declaration d, 
    		ProducedReference pr, Unit unit) {
        StringBuilder result = new StringBuilder("shared actual ");
        if (isVariable(d)) {
            result.append("variable ");
        }
        appendDeclarationText(d, pr, unit, result);
        appendTypeParameters(d, result);
        appendParameters(d, pr, unit, result);
        /*result.append(" - refine declaration in ") 
            .append(((Declaration) d.getContainer()).getName());*/
        return result.toString();
    }
    
    static String getInlineFunctionDescriptionFor(Parameter p, 
    		ProducedReference pr, Unit unit) {
        StringBuilder result = new StringBuilder();
        appendNamedArgumentText(p, pr, result);
        appendTypeParameters(p.getModel(), result);
        appendParameters(p.getModel(), pr, unit, result);
        return result.toString();
    }
    
    public static String getDescriptionFor(Declaration d) {
        return getDescriptionFor(d, null);
    }
    
    public static String getDescriptionFor(Declaration d, 
    		CeylonParseController cpc) {
        StringBuilder result = new StringBuilder();
        if (d!=null) {
            if (d.isFormal()) result.append("formal ");
            if (d.isDefault()) result.append("default ");
            if (isVariable(d)) result.append("variable ");
            appendDeclarationText(d, d.getUnit(), result);
            appendTypeParameters(d, result);
            appendParameters(d, result, cpc);
        }
        return result.toString();
    }
    
    public static String getDescriptionFor(Declaration d, 
    		ProducedReference pr, Unit unit) {
        StringBuilder result = new StringBuilder();
        if (d.isFormal()) result.append("formal ");
        if (d.isDefault()) result.append("default ");
        if (isVariable(d)) result.append("variable ");
        appendDeclarationText(d, pr, unit, result);
        appendTypeParameters(d, result);
        appendParameters(d, pr, unit, result);
        return result.toString();
    }
    
    public static StyledString getStyledDescriptionFor(Declaration d) {
        StyledString result = new StyledString();
        if (d!=null) {
            if (d.isFormal()) result.append("formal ", ANN_STYLER);
            if (d.isDefault()) result.append("default ", ANN_STYLER);
            if (isVariable(d)) result.append("variable ", ANN_STYLER);
            appendDeclarationText(d, result);
            appendTypeParameters(d, result);
            appendParameters(d, result);
            /*result.append(" - refine declaration in ") 
                .append(((Declaration) d.getContainer()).getName());*/
        }
        return result;
    }
    
    static void appendPositionalArgs(Declaration d, ProducedReference pr, 
            Unit unit, StringBuilder result, boolean includeDefaulted) {
        if (d instanceof Functional) {
            List<Parameter> params = getParameters((Functional) d, 
            		includeDefaulted, false);
            if (params.isEmpty()) {
                result.append("()");
            }
            else {
                result.append("(");
                for (Parameter p: params) {
                    if (p.isSequenced()) {
                        result.append("*");
                    }
                    if (p.getModel() instanceof Functional) {
                    	if (p.isDeclaredVoid()) {
                    		result.append("void ");
                    	}
                    	appendParameters(p.getModel(), 
                    			pr.getTypedParameter(p), 
                    			unit, result);
                    	if (p.isDeclaredVoid()) {
                    		result.append(" {}");
                    	}
                    	else {
                    		result.append(" => ")
                    		    .append(p.getName());
                    	}
                    }
                    else {
                    	result.append(p.getName());
                    }
                    result.append(", ");
                }
                result.setLength(result.length()-2);
                result.append(")");
            }
        }
    }

    private static List<Parameter> getParameters(Functional fd, 
    		boolean includeDefaults, boolean namedInvocation) {
        List<ParameterList> plists = fd.getParameterLists();
        if (plists==null || plists.isEmpty()) {
            return Collections.<Parameter>emptyList();
        }
        return CompletionUtil.getParameters(plists.get(0), 
                includeDefaults, namedInvocation);
    }

	private static void appendNamedArgs(Declaration d, ProducedReference pr, 
            Unit unit, StringBuilder result, boolean includeDefaulted, 
            boolean descriptionOnly) {
        if (d instanceof Functional) {
            List<Parameter> params = getParameters((Functional) d, 
            		includeDefaulted, true);
            if (params.isEmpty()) {
                result.append(" {}");
            }
            else {
                result.append(" { ");
                for (Parameter p: params) {
                    if (p.getModel() instanceof Functional) {
                        if (p.isDeclaredVoid()) {
                            result.append("void ");
                        }
                        else {
                            result.append("function ");
                        }
                        result.append(p.getName());
                        appendParameters(p.getModel(), 
                        		pr.getTypedParameter(p), 
                        		unit, result);
                        if (descriptionOnly) {
                            result.append("; ");
                        }
                        else if (p.isDeclaredVoid()) {
                            result.append(" {} ");
                        }
                        else {
                            result.append(" => ")
                                //.append(CeylonQuickFixAssistant.defaultValue(p.getUnit(), p.getType()))
                                .append("nothing; ");
                        }
                    }
                    else {
                        if (p==params.get(params.size()-1) && 
                        		unit.isIterableParameterType(p.getType())) {
//                        	result.append(" ");
                        }
                        else {
                            result.append(p.getName()).append(" = ")
                                //.append(CeylonQuickFixAssistant.defaultValue(p.getUnit(), p.getType()))
                                .append("nothing")
                                .append("; ");
                        }
                    }
                }
                result.append("}");
            }
        }
    }
    
    private static void appendTypeParameters(Declaration d, 
    		StringBuilder result) {
        if (d instanceof Generic) {
            List<TypeParameter> types = 
            		((Generic) d).getTypeParameters();
            if (!types.isEmpty()) {
                result.append("<");
                for (TypeParameter p: types) {
                    result.append(p.getName()).append(", ");
                }
                result.setLength(result.length()-2);
                result.append(">");
            }
        }
    }
    
    private static void appendTypeParameters(Declaration d, 
    		StyledString result) {
        if (d instanceof Generic) {
            List<TypeParameter> types = 
            		((Generic) d).getTypeParameters();
            if (!types.isEmpty()) {
                result.append("<");
                int len = types.size(), i = 0;
                for (TypeParameter p: types) {
                    result.append(p.getName(), TYPE_STYLER);
                    if (++i<len) result.append(", ");
                }
                result.append(">");
            }
        }
    }
    
    private static void appendDeclarationText(Declaration d, 
    		Unit unit, StringBuilder result) {
        appendDeclarationText(d, null, unit, result);
    }
    
    static void appendDeclarationText(Declaration d, 
    		ProducedReference pr, Unit unit, 
    		StringBuilder result) {
        if (d instanceof Class) {
            if (d.isAnonymous()) {
                result.append("object");
            }
            else {
                result.append("class");
            }
        }
        else if (d instanceof Interface) {
            result.append("interface");
        }
        else if (d instanceof TypeAlias) {
            result.append("alias");
        }
        else if (d instanceof TypedDeclaration) {
            TypedDeclaration td = (TypedDeclaration) d;
            ProducedType type = td.getType();
            if (type==null) type = new UnknownType(d.getUnit()).getType();
            boolean isSequenced = d.isParameter() && 
                    ((MethodOrValue) d).getInitializerParameter()
                            .isSequenced();
            if (pr!=null) {
                type = type.substitute(pr.getTypeArguments());
            }
            if (isSequenced) {
                type = d.getUnit().getIteratedType(type);
            }
            String typeName = type.getProducedTypeName(unit);
            if (td.isDynamicallyTyped()) {
                result.append("dynamic");
            }
            else if (td instanceof Value && 
                    type.getDeclaration().isAnonymous()) {
                result.append("object");
            }
            else if (d instanceof Method) {
                if (((Functional) d).isDeclaredVoid()) {
                    result.append("void");
                }
                else {
                    result.append(typeName);
                }
            }
            else {
                result.append(typeName);
            }
            if (isSequenced) {
                if (((MethodOrValue) d).getInitializerParameter()
                		.isAtLeastOne()) {
                    result.append("+");
                }
                else {
                    result.append("*");
                }
            }
        }
        result.append(" ").append(escapeName(d));
    }
    
    private static void appendNamedArgumentText(Parameter p, 
    		ProducedReference pr, StringBuilder result) {
        if (p.getModel() instanceof Functional) {
            Functional fp = (Functional) p.getModel();
            result.append(fp.isDeclaredVoid() ? "void" : "function");
        }
        else {
            result.append("value");
        }
        result.append(" ").append(p.getName());
    }
    
    private static void appendDeclarationText(Declaration d, 
    		StyledString result) {
        if (d instanceof Class) {
            if (d.isAnonymous()) {
                result.append("object", KW_STYLER);
            }
            else {
                result.append("class", KW_STYLER);
            }
        }
        else if (d instanceof Interface) {
            result.append("interface", KW_STYLER);
        }
        else if (d instanceof TypeAlias) {
            result.append("alias", KW_STYLER);
        }
        else if (d instanceof TypedDeclaration) {
            TypedDeclaration td = (TypedDeclaration) d;
            ProducedType type = td.getType();
            if (td.isDynamicallyTyped()) {
                result.append("dynamic", KW_STYLER);
            }
            else if (type!=null) {
                boolean isSequenced = d.isParameter() && 
                        ((MethodOrValue) d).getInitializerParameter()
                                .isSequenced();
                if (isSequenced) {
                    type = d.getUnit().getIteratedType(type);
                }
                String typeName = type.getProducedTypeName();
                if (td instanceof Value &&
                        td.getTypeDeclaration().isAnonymous()) {
                    result.append("object", KW_STYLER);
                }
                else if (d instanceof Method) {
                    if (((Functional)d).isDeclaredVoid()) {
                        result.append("void", KW_STYLER);
                    }
                    else {
                        result.append(typeName, TYPE_STYLER);
                    }
                }
                else {
                    result.append(typeName, TYPE_STYLER);
                }
                if (isSequenced) {
                    result.append("*");
                }
            }
        }
        String name = d.getName();
        if (name != null) {
            result.append(" ");
            if (d instanceof TypeDeclaration) {
                result.append(name, TYPE_STYLER);
            }
            else {
                result.append(name, ID_STYLER);
            }
        }
    }
    
    /*private static void appendPackage(Declaration d, StringBuilder result) {
    if (d.isToplevel()) {
        result.append(" - ").append(getPackageLabel(d));
    }
    if (d.isClassOrInterfaceMember()) {
        result.append(" - ");
        ClassOrInterface td = (ClassOrInterface) d.getContainer();
        result.append( td.getName() );
        appendPackage(td, result);
    }
  }*/
    
    private static void appendImpl(Declaration d, ProducedReference pr, 
    		boolean isInterface, Unit unit, String indent, StringBuilder result,
    		ClassOrInterface ci) {
    	if (d instanceof Method) {
    		if (ci!=null && !ci.isAnonymous()) {
    			if (d.getName().equals("equals")) {
    				List<ParameterList> pl = ((Method) d).getParameterLists();
    				if (!pl.isEmpty()) {
    					List<Parameter> ps = pl.get(0).getParameters();
    					if (!ps.isEmpty()) {
    						appendEqualsImpl(unit, indent, result, ci, ps);
    						return;
    					}
    				}
    			}
    		}
            if (!d.isFormal()) {
                result.append(" => super.").append(d.getName());
                appendPositionalArgs(d, pr, unit, result, true);
                result.append(";");
                
            }
            else {
                if (((Functional) d).isDeclaredVoid()) {
                    result.append(" {}");
                }
                else {
                    result.append(" => nothing;");
                }
            }
        }
        else if (d instanceof MethodOrValue) {
        	if (ci!=null && !ci.isAnonymous()) {
        		if (d.getName().equals("hash")) {
					appendHashImpl(unit, indent, result, ci);
					return;
        		}
        	}
            if (isInterface||d.isParameter()) {
                if (d.isFormal()) {
                    result.append(" => nothing;");
                }
                else {
                    result.append(" => super.")
                        .append(d.getName()).append(";");
                }
                if (isVariable(d)) {
                    result.append(indent + "assign " +
                    		d.getName() + " {}");
                }
            }
            else {
                if (d.isFormal()) {
                    result.append(" => nothing;");
                }
                else {
                    result.append(" => super.")
                        .append(d.getName()).append(";");
                }
            }
        }
        else {
            //TODO: in the case of a class, formal member refinements!
            result.append(" {}");
        }
    }

	private static void appendHashImpl(Unit unit, String indent, 
			StringBuilder result, ClassOrInterface ci) {
		result.append(" {")
			.append(indent).append(getDefaultIndent())
			.append("variable value hash = 1;")
			.append(indent).append(getDefaultIndent());
		String ind = indent+getDefaultIndent();
	    appendMembersToHash(unit, ind, result, ci);
	    result.append("return hash;")
			.append(indent)
			.append("}");
    }

	private static void appendEqualsImpl(Unit unit, String indent,
            StringBuilder result, ClassOrInterface ci, List<Parameter> ps) {
	    Parameter p = ps.get(0);
	    result.append(" {")
	    	.append(indent).append(getDefaultIndent())
	    	.append("if (is ").append(ci.getName()).append(" ").append(p.getName()).append(") {")
	    	.append(indent).append(getDefaultIndent()).append(getDefaultIndent())
	    	.append("return ");
	    String ind = indent+getDefaultIndent()+getDefaultIndent()+getDefaultIndent();
	    appendMembersToEquals(unit, ind, result, ci, p);
	    result.append(indent).append(getDefaultIndent())
	    	.append("}")
	    	.append(indent).append(getDefaultIndent())
	    	.append("else {")
	    	.append(indent).append(getDefaultIndent()).append(getDefaultIndent())
	    	.append("return false;")
	    	.append(indent).append(getDefaultIndent())
	    	.append("}")
	    	.append(indent)
	    	.append("}");
    }

	private static void appendMembersToEquals(Unit unit, String indent,
            StringBuilder result, ClassOrInterface ci, Parameter p) {
	    boolean found = false;
	    for (Declaration m: ci.getMembers()) {
	    	if (m instanceof Value) {
	    		Value value = (Value) m;
	    		if (!value.isTransient()) {
	    			if (!unit.getNullValueDeclaration().getType()
	    					.isSubtypeOf(value.getType())) {
	    				result.append(value.getName())
	    					.append("==")
	    					.append(p.getName())
	    					.append(".")
	    					.append(value.getName())
	    					.append(" && ")
	    					.append(indent);
	    				found = true;
	    			}
	    		}
	    	}
	    }
		if (found) {
			result.setLength(result.length()-4-indent.length());
			result.append(";");
		}
		else {
			result.append("true;");
		}
    }

	private static void appendMembersToHash(Unit unit, String indent,
            StringBuilder result, ClassOrInterface ci) {
	    for (Declaration m: ci.getMembers()) {
	    	if (m instanceof Value) {
	    		Value value = (Value) m;
	    		if (!value.isTransient()) {
	    			if (!unit.getNullValueDeclaration().getType()
	    					.isSubtypeOf(value.getType())) {
	    				result.append("hash = 31*hash + ")
	    					.append(value.getName())
	    					.append(".hash;")
	    					.append(indent);
	    			}
	    		}
	    	}
	    }
    }

    private static String extraIndent(String indent, boolean containsNewline) {
        return containsNewline ? indent + getDefaultIndent() : indent;
    }
    
    public static void appendParameters(Declaration d, StringBuilder result, 
            CeylonParseController cpc) {
        appendParameters(d, null, d.getUnit(), result, cpc);
    }
    
    public static void appendParameters(Declaration d, ProducedReference pr, 
            Unit unit, StringBuilder result) {
        appendParameters(d, pr, unit, result, null);
    }
    
    private static void appendParameters(Declaration d, ProducedReference pr, 
            Unit unit, StringBuilder result, CeylonParseController cpc) {
        if (d instanceof Functional) {
            List<ParameterList> plists = ((Functional) d).getParameterLists();
            if (plists!=null) {
                for (ParameterList params: plists) {
                    if (params.getParameters().isEmpty()) {
                        result.append("()");
                    }
                    else {
                        result.append("(");
                        for (Parameter p: params.getParameters()) {
                            appendParameter(result, pr, p, unit);
                            /*ProducedType type = p.getType();
                            if (pr!=null) {
                                type = type.substitute(pr.getTypeArguments());
                            }
                            result.append(type.getProducedTypeName(unit)).append(" ")
                                .append(p.getName());
                            if (p instanceof FunctionalParameter) {
                                result.append("(");
                                FunctionalParameter fp = (FunctionalParameter) p;
                                for (Parameter pp: fp.getParameterLists().get(0).getParameters()) {
                                    result.append(pp.getType().substitute(pr.getTypeArguments())
                                            .getProducedTypeName(unit))
                                        .append(" ").append(pp.getName()).append(", ");
                                }
                                result.setLength(result.length()-2);
                                result.append(")");
                            }*/
                            if (cpc!=null) {
                                result.append(getDefaultValue(p, cpc));
                            }
                            result.append(", ");
                        }
                        result.setLength(result.length()-2);
                        result.append(")");
                    }
                }
            }
        }
    }

	public static void appendParameter(StringBuilder result,
            ProducedReference pr, Parameter p, Unit unit) {
	    ProducedTypedReference ppr = pr==null ? 
	            null : pr.getTypedParameter(p);
	    if (p.getModel() == null) {
	        result.append(p.getName());
	    }
	    else {
	        appendDeclarationText(p.getModel(), ppr, unit, result);
	        appendParameters(p.getModel(), ppr, unit, result);
	    }
    }
    
    private static void appendParameters(Declaration d, StyledString result) {
        if (d instanceof Functional) {
            List<ParameterList> plists = ((Functional) d).getParameterLists();
            if (plists!=null) {
                for (ParameterList params: plists) {
                    if (params.getParameters().isEmpty()) {
                        result.append("()");
                    }
                    else {
                        result.append("(");
                        int len = params.getParameters().size(), i=0;
                        for (Parameter p: params.getParameters()) {
                            if (p.getModel()==null) {
                                result.append(p.getName());
                            }
                            else {
                                appendDeclarationText(p.getModel(), result);
                                appendParameters(p.getModel(), result);
                                /*result.append(p.getType().getProducedTypeName(), TYPE_STYLER)
                                    .append(" ").append(p.getName(), ID_STYLER);
                                if (p instanceof FunctionalParameter) {
                                    result.append("(");
                                    FunctionalParameter fp = (FunctionalParameter) p;
                                    List<Parameter> fpl = fp.getParameterLists().get(0).getParameters();
                                    int len2 = fpl.size(), j=0;
                                    for (Parameter pp: fpl) {
                                        result.append(pp.getType().getProducedTypeName(), TYPE_STYLER)
                                            .append(" ").append(pp.getName(), ID_STYLER);
                                        if (++j<len2) result.append(", ");
                                    }
                                    result.append(")");
                                }*/
                            }
                            if (++i<len) result.append(", ");
                        }
                        result.append(")");
                    }
                }
            }
        }
    }

}
