package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getDefaultValueDescription;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.appendTypeName;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMETER_TYPES_IN_COMPLETIONS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_OUTLINES;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;
import static com.redhat.ceylon.eclipse.util.Highlights.ANN_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.ARROW_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.KW_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.MEMBER_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.TYPE_ID_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.TYPE_STYLER;
import static com.redhat.ceylon.eclipse.util.Highlights.styleIdentifier;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.EXTENDS;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isConstructor;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.graphics.Font;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.OccurrenceLocation;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Generic;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Reference;
import com.redhat.ceylon.model.typechecker.model.Setter;
import com.redhat.ceylon.model.typechecker.model.SiteVariance;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeAlias;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.model.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypedReference;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;

public class CodeCompletions {

    private static boolean forceExplicitTypeArgs(
            Declaration d, OccurrenceLocation ol) {
        if (ol==EXTENDS) {
            return true;
        }
        else {
            //TODO: this is a pretty limited implementation 
            //      for now, but eventually we could do 
            //      something much more sophisticated to
            //      guess if explicit type args will be
            //      necessary (variance, etc)
            if (d instanceof Functional) {
                Functional fun = (Functional) d;
                List<ParameterList> pls = 
                        fun.getParameterLists();
                return pls.isEmpty() || 
                        pls.get(0)
                            .getParameters()
                            .isEmpty();
            }
            else {
                return false;
            }
        }
    }
    
    static String getTextForDocLink(
            CeylonParseController cpc, Declaration decl) {
        
        Package pkg = decl.getUnit().getPackage();
        String qname = decl.getQualifiedNameString();
        
        // handle language package or same module and package
        Unit unit = cpc.getLastCompilationUnit().getUnit();
        if (pkg!=null && 
                (Module.LANGUAGE_MODULE_NAME
                        .equals(pkg.getNameAsString())
            || (unit!=null && pkg.equals(unit.getPackage())))) {
            if (decl.isToplevel()) {
                return decl.getNameAsString();
            }
            else { // not top level in language module
                int loc = qname.indexOf("::");
                if (loc>=0) {
                    return qname.substring(loc + 2);
                }
                else {
                    return qname;
                }
            }
        } 
        else {
            return qname;
        }
        
    }
    
    public static String getTextFor(Declaration dec, Unit unit) {
        StringBuilder result = new StringBuilder();
        result.append(escapeName(dec, unit));
        appendTypeParameters(dec, result);
        return result.toString();
    }
    
    public static String getPositionalInvocationTextFor(
            Declaration dec, OccurrenceLocation ol,
            Reference pr, Unit unit, boolean includeDefaulted,
            String typeArgs) {
        StringBuilder result = 
                new StringBuilder(escapeName(dec, unit));
        if (typeArgs!=null) {
            result.append(typeArgs);
        }
        else if (forceExplicitTypeArgs(dec, ol)) {
            appendTypeParameters(dec, result);
        }
        appendPositionalArgs(dec, pr, unit, result, 
                includeDefaulted, false);
        appendSemiToVoidInvocation(result, dec);
        return result.toString();
    }

    public static String getNamedInvocationTextFor(
            Declaration dec, Reference pr, Unit unit, 
            boolean includeDefaulted,
            String typeArgs) {
        StringBuilder result = 
                new StringBuilder(escapeName(dec, unit));
        if (typeArgs!=null) {
            result.append(typeArgs);
        }
        else if (forceExplicitTypeArgs(dec, null)) {
            appendTypeParameters(dec, result);
        }
        appendNamedArgs(dec, pr, unit, result, 
                includeDefaulted, false);
        appendSemiToVoidInvocation(result, dec);
        return result.toString();
    }
    
    private static void appendSemiToVoidInvocation(
            StringBuilder result, Declaration dd) {
        if (dd instanceof Function) {
            Function fun = (Function) dd;
            if (fun.isDeclaredVoid() && 
                    fun.getParameterLists().size()==1) {
                result.append(';');
            }
        }
    }
    
    public static String getDescriptionFor(
            Declaration dec, Unit unit) {
        StringBuilder result = 
                new StringBuilder(dec.getName(unit));
        appendTypeParameters(dec, result);
        return result.toString();
    }
    
    public static String getPositionalInvocationDescriptionFor(
            Declaration dec, OccurrenceLocation ol,
            Reference pr, Unit unit, 
            boolean includeDefaulted,
            String typeArgs) {
        StringBuilder result = 
                new StringBuilder(dec.getName(unit));
        if (typeArgs!=null) {
            result.append(typeArgs);
        }
        else if (forceExplicitTypeArgs(dec, ol)) {
            appendTypeParameters(dec, result);
        }
        appendPositionalArgs(dec, pr, unit, result, 
                includeDefaulted, true);
        return result.toString();
    }
    
    public static String getNamedInvocationDescriptionFor(
            Declaration dec, Reference pr, Unit unit, 
            boolean includeDefaulted, 
            String typeArgs) {
        StringBuilder result = 
                new StringBuilder(dec.getName(unit));
        if (typeArgs!=null) {
            result.append(typeArgs);
        }
        else if (forceExplicitTypeArgs(dec, null)) {
            appendTypeParameters(dec, result);
        }
        appendNamedArgs(dec, pr, unit, result, 
                includeDefaulted, true);
        return result.toString();
    }
    
    public static String getRefinementTextFor(
            Declaration d, Reference pr, Unit unit, 
            boolean isInterface, 
            ClassOrInterface ci, String indent, 
            boolean containsNewline) {
        return getRefinementTextFor(d, pr, unit, 
                isInterface, ci, indent, containsNewline, 
                true);
    }
    
    public static String getRefinementTextFor(
            Declaration d, Reference pr, Unit unit, 
            boolean isInterface,
            ClassOrInterface ci, String indent, 
            boolean containsNewline, 
            boolean preamble) {
        StringBuilder result = new StringBuilder();
        if (preamble) {
            result.append("shared actual ");
            if (isVariable(d) && !isInterface) {
                result.append("variable ");
            }
        }
        appendDeclarationHeaderText(d, pr, unit, result);
        appendTypeParameters(d, result);
        appendParametersText(d, pr, unit, result);
        if (d instanceof Class) {
            String extraIndent = 
                    extraIndent(extraIndent(indent, containsNewline), 
                            containsNewline);
            result.append(extraIndent)
                .append(" extends super.")
                .append(escapeName(d));
            appendPositionalArgs(d, pr, unit, result, true, false);
        }
        appendConstraints(d, pr, unit, indent, containsNewline, result);
        appendImplText(d, pr, isInterface, unit, indent, result, ci);
        return result.toString();
    }

    private static void appendConstraints(Declaration d, 
            Reference pr, Unit unit, String indent, 
            boolean containsNewline, 
            StringBuilder result) {
        if (d instanceof Generic) {
            Generic generic = (Generic) d;
            for (TypeParameter tp: generic.getTypeParameters()) {
                List<Type> sts = tp.getSatisfiedTypes();
                if (!sts.isEmpty()) {
                    String extraIndent = 
                            extraIndent(extraIndent(indent, containsNewline), 
                                    containsNewline);
                    result.append(extraIndent)
                        .append("given ")
                        .append(tp.getName())
                        .append(" satisfies ");
                    boolean first = true;
                    for (Type st: sts) {
                        if (first) {
                            first = false;
                        }
                        else {
                            result.append("&");
                        }
                        if (pr instanceof Type) {
                            st = st.substitute((Type) pr);
                        }
                        else {
                            st = st.substitute((TypedReference) pr);
                        }
                        result.append(st.asSourceCodeString(unit));
                    }
                }
            }
        }
    }

    static String getInlineFunctionTextFor(Parameter p, 
            Reference pr, Unit unit, String indent) {
        StringBuilder result = new StringBuilder();
        appendNamedArgumentHeader(p, pr, result, false);
        appendTypeParameters(p.getModel(), result);
        appendParametersText(p.getModel(), pr, unit, result);
        if (p.isDeclaredVoid()) {
            result.append(" {}");
        }
        else {
            result.append(" => nothing;");
        }
        return result.toString();
    }

    public static boolean isVariable(Declaration d) {
        if (d instanceof TypedDeclaration) { 
            TypedDeclaration td = (TypedDeclaration) d;
            return td.isVariable();
        }
        else {
            return false;
        }
    }
    
    static String getRefinementDescriptionFor(
            Declaration d, Reference pr, Unit unit) {
        StringBuilder result = 
                new StringBuilder("shared actual ");
        if (isVariable(d)) {
            result.append("variable ");
        }
        appendDeclarationHeaderDescription(d, pr, unit, result);
        appendTypeParameters(d, result);
        appendParametersDescription(d, pr, unit, result);
        /*result.append(" - refine declaration in ") 
            .append(((Declaration) d.getContainer()).getName());*/
        return result.toString();
    }
    
    static String getInlineFunctionDescriptionFor(
            Parameter p, Reference pr, Unit unit) {
        StringBuilder result = new StringBuilder();
        appendNamedArgumentHeader(p, pr, result, true);
        appendTypeParameters(p.getModel(), result);
        appendParametersDescription(p.getModel(), pr, unit, result);
        return result.toString();
    }
    
    public static String getLabelDescriptionFor(
            Declaration d) {
        return getLabelDescriptionFor(d, true, true);
    }    
    
    public static String getLabelDescriptionFor(
            Declaration d, 
            boolean typeParams, boolean params) {
        StringBuilder result = new StringBuilder();
        if (d!=null) {
            appendDeclarationAnnotations(d, result);
            appendDeclarationHeaderDescription(d, d.getUnit(), result);
            if (typeParams) appendTypeParameters(d, result, true);
            if (params) appendParametersDescription(d, result, null);
        }
        return result.toString();
    }

    private static void appendDeclarationAnnotations(
            Declaration d, StringBuilder result) {
        if (d.isActual()) {
            result.append("actual ");
        }
        if (d.isFormal()) {
            result.append("formal ");
        }
        if (d.isDefault()) {
            result.append("default ");
        }
        if (isVariable(d)) {
            result.append("variable ");
        }
    }
    
    public static String getDocDescriptionFor(
            Declaration d, Reference pr, Unit unit) {
        StringBuilder result = new StringBuilder();
        appendDeclarationHeaderDescription(d, pr, unit, result);
        appendTypeParameters(d, pr, result, true, unit);
        appendParametersDescription(d, pr, unit, result);
        return result.toString();
    }
    
    public static StyledString getQualifiedDescriptionFor(
            Declaration d) {
        return getQualifiedDescriptionFor(d, true, true, true,
                getPreferences()
                    .getBoolean(RETURN_TYPES_IN_OUTLINES));
    }
    
    public static StyledString getQualifiedDescriptionFor(
            Declaration d, 
            boolean typeParameters, boolean parameters, boolean parameterTypes, 
            boolean types) {
    	 return getQualifiedDescriptionFor(d, 
    	            typeParameters, parameters, parameterTypes, 
    	            types, null, null);
    }
    public static StyledString getQualifiedDescriptionFor(
            Declaration d, 
            boolean typeParameters, boolean parameters, boolean parameterTypes, 
            boolean types, String prefix, Font font) {
        StyledString result = new StyledString();
        if (d!=null) {
            appendDeclarationDescription(d, result);
            result.append(' ');
            if (d.isClassOrInterfaceMember()) {
                Declaration ci = 
                        (Declaration) 
                            d.getContainer();
                appendQualifyingTypeName(result, ci, prefix, font);
                appendMemberName(d, result, prefix, font);
            }
            else {
                appendDeclarationName(d, result, prefix, font);
            }
            if (typeParameters) {
                appendTypeParameters(d, result, true);
            }
            if (parameters||parameterTypes) {
                appendParametersDescription(d, result, 
                        parameters, parameterTypes);
            }
            if (d instanceof TypedDeclaration) {
                if (types) {
                    TypedDeclaration td = 
                            (TypedDeclaration) d;
                    if (!td.isParameter() && 
                            !td.isDynamicallyTyped() &&
                            !(td instanceof Function && 
                                    ((Function) td).isDeclaredVoid())) {
                        Type t = td.getType();
                        if (t!=null) {
                            result.append(" ∊ ");
                            appendTypeName(result, t, ARROW_STYLER);
                        }
                    }
                }
            }
            /*result.append(" - refines declaration in ") 
                .append(((Declaration) d.getContainer()).getName());*/
        }
        return result;
    }

	private static void appendQualifyingTypeName(
	        StyledString result, 
	        Declaration ci, String prefix, Font font) {
		String name = ci.getName();
		if (prefix!=null) {
			int loc = prefix.indexOf('.');
			if (loc>0) {
				prefix = prefix.substring(0, loc);
			}
			styleIdentifier(result, prefix, name, 
					TYPE_ID_STYLER, font);
		}
		else {
			result.append(name, TYPE_ID_STYLER);
		}
		result.append('.');
	}
    
    public static StyledString getStyledDescriptionFor(
            Declaration d) {
        StyledString result = new StyledString();
        if (d!=null) {
            appendDeclarationAnnotations(d, result);
            appendDeclarationDescription(d, result);
            result.append(' ');
            appendDeclarationName(d, result);
            appendTypeParameters(d, result, true);
            appendParametersDescription(d, result, true, true);
            if (d instanceof TypedDeclaration) {
                if (getPreferences()
                        .getBoolean(RETURN_TYPES_IN_OUTLINES)) {
                    TypedDeclaration td = 
                            (TypedDeclaration) d;
                    if (!td.isParameter() && 
                            !td.isDynamicallyTyped() &&
                            !(td instanceof Function && 
                                    ((Function) td).isDeclaredVoid())) {
                        Type t = td.getType();
                        if (t!=null) {
                            result.append(" ∊ ");
                            appendTypeName(result, t, ARROW_STYLER);
                        }
                    }
                }
            }
            /*result.append(" - refines declaration in ") 
                .append(((Declaration) d.getContainer()).getName());*/
        }
        return result;
    }

    private static void appendDeclarationAnnotations(Declaration d,
            StyledString result) {
        if (d.isActual()) {
            result.append("actual ", ANN_STYLER);
        }
        if (d.isFormal()) {
            result.append("formal ", ANN_STYLER);
        }
        if (d.isDefault()) {
            result.append("default ", ANN_STYLER);
        }
        if (isVariable(d)) {
            result.append("variable ", ANN_STYLER);
        }
    }
    
    public static void appendPositionalArgs(Declaration dec,
            Unit unit, StringBuilder result, boolean includeDefaulted,
            boolean descriptionOnly) {
        appendPositionalArgs(dec, dec.getReference(), 
                unit, result, includeDefaulted,
                descriptionOnly);
    }
    
    private static void appendPositionalArgs(
            Declaration d, Reference pr, 
            Unit unit, StringBuilder result, boolean includeDefaulted,
            boolean descriptionOnly) {
        if (d instanceof Functional) {
            List<Parameter> params = getParameters((Functional) d, 
                    includeDefaulted, false);
            if (params.isEmpty()) {
                result.append("()");
            }
            else {
                boolean paramTypes = 
                        descriptionOnly && 
                        getPreferences()
                            .getBoolean(PARAMETER_TYPES_IN_COMPLETIONS);
                result.append("(");
                for (Parameter p: params) {
                    TypedReference typedParameter = 
                            pr.getTypedParameter(p);
                    if (p.getModel() instanceof Functional) {
                        if (p.isDeclaredVoid()) {
                            result.append("void ");
                        }
                        appendParameters(p.getModel(), 
                                typedParameter, 
                                unit, result, 
                                descriptionOnly);
                        if (p.isDeclaredVoid()) {
                            result.append(" {}");
                        }
                        else {
                            result.append(" => ")
                                .append("nothing");
                        }
                    }
                    else {
                        Type pt = typedParameter.getType();
                        if (paramTypes && !isTypeUnknown(pt)) {
                            if (p.isSequenced()) {
                                pt = unit.getSequentialElementType(pt);
                            }
                            result.append(pt.asString(unit));
                            if (p.isSequenced()) {
                                result.append(p.isAtLeastOne()?'+':'*');
                            }
                            result.append(" ");
                        }
                        else if (p.isSequenced()) {
                            result.append("*");
                        }
                        result.append(descriptionOnly || p.getModel()==null ? 
                                p.getName() : escapeName(p.getModel()));
                    }
                    result.append(", ");
                }
                result.setLength(result.length()-2);
                result.append(")");
            }
        }
    }
    
    static void appendSuperArgsText(Declaration d, 
            Reference pr, Unit unit, StringBuilder result, 
            boolean includeDefaulted) {
        if (d instanceof Functional) {
            List<Parameter> params = 
                    getParameters((Functional) d, 
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
                    result.append(escapeName(p.getModel()))
                        .append(", ");
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
        else {
            return CompletionUtil.getParameters(plists.get(0), 
                    includeDefaults, namedInvocation);
        }
    }

    private static void appendNamedArgs(
            Declaration d, Reference pr, 
            Unit unit, StringBuilder result, 
            boolean includeDefaulted, 
            boolean descriptionOnly) {
        if (d instanceof Functional) {
            List<Parameter> params = 
                    getParameters((Functional) d, 
                            includeDefaulted, true);
            if (params.isEmpty()) {
                result.append(" {}");
            }
            else {
                boolean paramTypes = 
                        descriptionOnly && 
                        getPreferences()
                            .getBoolean(PARAMETER_TYPES_IN_COMPLETIONS);
                result.append(" { ");
                for (Parameter p: params) {
                    String name = descriptionOnly ? 
                            p.getName() : 
                            escapeName(p.getModel());
                    if (p.getModel() instanceof Functional) {
                        if (p.isDeclaredVoid()) {
                            result.append("void ");
                        }
                        else {
                            if (paramTypes && 
                                    !isTypeUnknown(p.getType())) {
                                String ptn = p.getType().asString(unit);
                                result.append(ptn).append(" ");
                            }
                            else {
                                result.append("function ");
                            }
                        }
                        result.append(name);
                        appendParameters(p.getModel(), 
                                pr.getTypedParameter(p), 
                                unit, result, 
                                descriptionOnly);
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
                                !isTypeUnknown(p.getType()) &&
                                unit.isIterableParameterType(p.getType())) {
//                            result.append(" ");
                        }
                        else {
                            if (paramTypes && 
                                    !isTypeUnknown(p.getType())) {
                                String ptn = p.getType().asString(unit);
                                result.append(ptn).append(" ");
                            }
                            result.append(name)
                                .append(" = ")
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

    private static void appendTypeParameters(
            Declaration d, StringBuilder result) {
        appendTypeParameters(d, result, false);
    }
    
    private static void appendTypeParameters(
            Declaration d, StringBuilder result, 
            boolean variances) {
        if (d instanceof Generic) {
            Generic g = (Generic) d;
            List<TypeParameter> types = 
                    g.getTypeParameters();
            if (!types.isEmpty()) {
                result.append("<");
                for (TypeParameter tp: types) {
                    if (variances) {
                        if (tp.isCovariant()) {
                            result.append("out ");
                        }
                        if (tp.isContravariant()) {
                            result.append("in ");
                        }
                    }
                    result.append(tp.getName())
                        .append(", ");
                }
                result.setLength(result.length()-2);
                result.append(">");
            }
        }
    }
    
    private static void appendTypeParameters(
            Declaration d, Reference pr, 
            StringBuilder result, 
            boolean variances, Unit unit) {
        if (d instanceof Generic) {
            Generic g = (Generic) d;
            List<TypeParameter> types = 
                    g.getTypeParameters();
            if (!types.isEmpty()) {
                result.append("<");
                boolean first = true;
                for (TypeParameter tp: types) {
                    if (first) {
                        first = false;
                    }
                    else {
                        result.append(", ");
                    }
                    Type arg = pr==null ? null : 
                        pr.getTypeArguments().get(tp);
                    if (arg == null) {
                        if (variances) {
                            if (tp.isCovariant()) {
                                result.append("out ");
                            }
                            else if (tp.isContravariant()) {
                                result.append("in ");
                            }
                        }
                        result.append(tp.getName());
                    }
                    else {
                        if (pr instanceof Type) {
                            if (variances) {
                                Type t = (Type) pr;
                                SiteVariance variance = 
                                        t.getVarianceOverrides()
                                            .get(tp);
                                if (variance==null) {
                                    if (tp.isCovariant()) {
                                        result.append("out ");
                                    }
                                    else if (tp.isContravariant()) {
                                        result.append("in ");
                                    }
                                }
                                else if (variance==SiteVariance.OUT) {
                                    result.append("out ");
                                }
                                else if (variance==SiteVariance.IN) {
                                    result.append("in ");
                                }
                            }
                        }
                        result.append(arg.asString(unit));
                    }
                }
                result.append(">");
            }
        }
    }
    
    private static void appendTypeParameters(
            Declaration d, StyledString result, 
            boolean variances) {
        if (d instanceof Generic) {
            Generic g = (Generic) d;
            List<TypeParameter> types = 
                    g.getTypeParameters();
            if (!types.isEmpty()) {
                result.append("<");
                int len = types.size(), i = 0;
                for (TypeParameter tp: types) {
                    if (variances) {
                        if (tp.isCovariant()) {
                            result.append("out ", KW_STYLER);
                        }
                        if (tp.isContravariant()) {
                            result.append("in ", KW_STYLER);
                        }
                    }
                    result.append(tp.getName(), TYPE_STYLER);
                    if (++i<len) {
                        result.append(", ");
                    }
                }
                result.append(">");
            }
        }
    }
    
    private static void appendDeclarationHeaderDescription(
            Declaration d, Unit unit, 
            StringBuilder result) {
        appendDeclarationHeader(d, null, unit, result, true);
    }
    
    private static void appendDeclarationHeaderDescription(
            Declaration d, Reference pr, Unit unit, 
            StringBuilder result) {
        appendDeclarationHeader(d, pr, unit, result, true);
    }
    
    private static void appendDeclarationHeaderText(
            Declaration d, Reference pr, Unit unit, 
            StringBuilder result) {
        appendDeclarationHeader(d, pr, unit, result, false);
    }
    
    private static void appendDeclarationHeader(
            Declaration d, Reference pr, Unit unit, 
            StringBuilder result, 
            boolean descriptionOnly) {
        if (d instanceof TypeAlias && d.isAnonymous()) {
            return;
        }
        if (isConstructor(d)) {
            result.append("new");
        }
        else if (d instanceof Class) {
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
            FunctionOrValue fov = (FunctionOrValue) d;
            boolean isSequenced = 
                    d.isParameter() && 
                    fov.getInitializerParameter()
                            .isSequenced();
            Type type;
            if (pr == null) {
                type = td.getType();
            }
            else {
                type = pr.getType();
            }
            if (isSequenced && type!=null) {
//                type = unit.getIteratedType(type);
                //TODO: nasty workaround because unit can be null
                //      in docs for Open dialogs
                List<Type> args = type.getTypeArgumentList();
                if (args.size()>0) {
                    type = args.get(0);
                }
            }
            if (type==null) {
                type = unit.getUnknownType();
            }
            String typeName = 
                    descriptionOnly ? 
                        type.asString(unit) :
                        type.asSourceCodeString(unit);
            if (td.isDynamicallyTyped()) {
                result.append("dynamic");
            }
            else if (td instanceof Value && 
                    type.getDeclaration()
                        .isAnonymous() &&
                    !type.isTypeConstructor()) {
                result.append("object");
            }
            else if (d instanceof Function) {
                Functional fun = (Functional) d;
                if (fun.isDeclaredVoid()) {
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
                if (fov.getInitializerParameter()
                        .isAtLeastOne()) {
                    result.append("+");
                }
                else {
                    result.append("*");
                }
            }
        }
        result.append(" ");
        if (d.getName()!=null) {
            result.append(descriptionOnly ? 
                    d.getName() : escapeName(d));
        }
    }
    
    private static void appendNamedArgumentHeader(
            Parameter p, Reference pr, 
            StringBuilder result,
            boolean descriptionOnly) {
        if (p.getModel() instanceof Functional) {
            Functional fp = (Functional) p.getModel();
            result.append(fp.isDeclaredVoid() ? "void" : "function");
        }
        else {
            result.append("value");
        }
        result.append(" ")
            .append(descriptionOnly ? 
                    p.getName() : 
                    escapeName(p.getModel()));
    }
    
    private static void appendDeclarationDescription(
            Declaration d, StyledString result) {
        if (isConstructor(d)) {
            result.append("new", KW_STYLER);
        }
        else if (d instanceof Class) {
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
        else if (d.isParameter()) {
            TypedDeclaration td = (TypedDeclaration) d;
            Type type = td.getType();
            if (td.isDynamicallyTyped()) {
                result.append("dynamic", KW_STYLER);
            }
            else if (type!=null) {
                FunctionOrValue fov = (FunctionOrValue) d;
                boolean isSequenced = //d.isParameter() && 
                        fov.getInitializerParameter()
                                .isSequenced();
                if (isSequenced) {
                    type = d.getUnit().getIteratedType(type);
                }
                /*if (td instanceof Value &&
                        td.getTypeDeclaration().isAnonymous()) {
                    result.append("object", KW_STYLER);
                }
                else*/ if (d instanceof Function) {
                    Functional fun = (Functional) d;
                    if (fun.isDeclaredVoid()) {
                        result.append("void", KW_STYLER);
                    }
                    else {
                        appendTypeName(result, type);
                    }
                }
                else {
                    appendTypeName(result, type);
                }
                if (isSequenced) {
                    result.append("*");
                }
            }
        }
        else if (d instanceof Value) {
            Value v = (Value) d;
            if (v.isDynamicallyTyped()) {
                result.append("dynamic", KW_STYLER);
            }
            else if (v.getTypeDeclaration()!=null &&
                    v.getTypeDeclaration().isAnonymous()) {
                result.append("object", KW_STYLER);
            }
            else {
                result.append("value", KW_STYLER);
            }
        }
        else if (d instanceof Function) {
            Function m = (Function) d;
            if (m.isDynamicallyTyped()) {
                result.append("dynamic", KW_STYLER);
            }
            else if (m.isDeclaredVoid()) {
                result.append("void", KW_STYLER);
            }
            else {
                result.append("function", KW_STYLER);
            }
        }
        else if (d instanceof Setter) {
            result.append("assign", KW_STYLER);
        }
    }

    private static void appendMemberName(
            Declaration d, 
    		StyledString result, 
    		String prefix, Font font) {
        String name = d.getName();
        if (name!=null) {
        	Styler styler = 
                    d instanceof TypeDeclaration ? 
                            TYPE_STYLER : MEMBER_STYLER;
            if (prefix!=null) {
            	int loc = prefix.indexOf('.');
            	if (loc>0) {
            		prefix = prefix.substring(loc);
            	}
            	styleIdentifier(result, prefix, name, 
            			styler, font);
            }
            else {
            	result.append(name, styler);
            }
        }
    }
    
    private static void appendDeclarationName(
            Declaration d, 
    		StyledString result, 
    		String prefix, Font font) {
        String name = d.getName();
        if (name!=null) {
            Styler styler = 
                    d instanceof TypeDeclaration ? 
                            TYPE_STYLER : MEMBER_STYLER;
            if (prefix!=null) {
            	styleIdentifier(result, prefix, name, 
            			styler, font);
            }
            else {
            	result.append(name, styler);
            }
        }
    }
    
    private static void appendDeclarationName(
            Declaration d, 
    		StyledString result) {
    	appendDeclarationName(d, result, null, null);
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
    
    private static void appendImplText(
            Declaration d, Reference pr, 
            boolean isInterface, Unit unit, 
            String indent, StringBuilder result,
            ClassOrInterface ci) {
        if (d instanceof Function) {
            Function fun = (Function) d;
            if (ci!=null && !ci.isAnonymous()) {
                if (d.getName().equals("equals")) {
                    List<ParameterList> pl = 
                            fun.getParameterLists();
                    if (!pl.isEmpty()) {
                        List<Parameter> ps = 
                                pl.get(0).getParameters();
                        if (!ps.isEmpty()) {
                            appendEqualsImpl(unit, indent, 
                                    result, ci, ps);
                            return;
                        }
                    }
                }
            }
            if (!d.isFormal()) {
                result.append(" => super.")
                    .append(d.getName());
                appendSuperArgsText(d, pr, unit, result, true);
                result.append(";");
                
            }
            else {
                if (fun.isDeclaredVoid()) {
                    result.append(" {}");
                }
                else {
                    result.append(" => nothing;");
                }
            }
        }
        else if (d instanceof Value) {
            if (ci!=null && !ci.isAnonymous()) {
                if (d.getName().equals("hash")) {
                    appendHashImpl(unit, indent, result, ci);
                    return;
                }
            }
            if (isInterface/*||d.isParameter()*/) {
                //interfaces can't have references,
                //so generate a setter for variables
                if (d.isFormal()) {
                    result.append(" => nothing;");
                }
                else {
                    result.append(" => super.")
                        .append(d.getName())
                        .append(";");
                }
                if (isVariable(d)) {
                    result.append(indent)
                        .append("assign ")
                        .append(d.getName())
                        .append(" {}");
                }
            }
            else {
                //we can have a references, so use = instead 
                //of => for variables
                String arrow = 
                        isVariable(d) ? " = " : " => ";
                if (d.isFormal()) {
                    result.append(arrow)
                        .append("nothing;");
                }
                else {
                    result.append(arrow)
                        .append("super.")
                        .append(d.getName())
                        .append(";");
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
        String ind = indent + getDefaultIndent();
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

    private static boolean isObjectField(Declaration m) {
        String name = m.getName();
        return name!=null &&
                (name.equals("hash") ||
                 name.equals("string"));
    }

    private static void appendMembersToEquals(Unit unit, 
            String indent, StringBuilder result, 
            ClassOrInterface ci, Parameter p) {
        boolean found = false;
        Type nt = unit.getNullValueDeclaration().getType();
        for (Declaration m: ci.getMembers()) {
            if (m instanceof Value && 
                    !isObjectField(m) && !isConstructor(m)) {
                Value value = (Value) m;
                if (!value.isTransient()) {
                    if (!nt.isSubtypeOf(value.getType())) {
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

    private static void appendMembersToHash(Unit unit, 
            String indent, StringBuilder result, 
            ClassOrInterface ci) {
        Type nt = unit.getNullValueDeclaration().getType();
        for (Declaration m: ci.getMembers()) {
            if (m instanceof Value && 
                    !isObjectField(m) && !isConstructor(m)) {
                Value value = (Value) m;
                if (!value.isTransient()) {
                    if (!nt.isSubtypeOf(value.getType())) {
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
    
    public static void appendParametersDescription(Declaration d, StringBuilder result, 
            CeylonParseController cpc) {
        appendParameters(d, null, d.getUnit(), result, cpc, true);
    }
    
    public static void appendParametersText(Declaration d, Reference pr, 
            Unit unit, StringBuilder result) {
        appendParameters(d, pr, unit, result, null, false);
    }
    
    private static void appendParametersDescription(Declaration d, Reference pr, 
            Unit unit, StringBuilder result) {
        appendParameters(d, pr, unit, result, null, true);
    }
    
    private static void appendParameters(Declaration d, Reference pr, 
            Unit unit, StringBuilder result, boolean descriptionOnly) {
        appendParameters(d, pr, unit, result, null, descriptionOnly);
    }
    
    private static void appendParameters(Declaration d, Reference pr, 
            Unit unit, StringBuilder result, CeylonParseController cpc,
            boolean descriptionOnly) {
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
                            appendParameter(result, pr, p, unit,
                                    descriptionOnly);
                            if (cpc!=null) {
                                result.append(getDefaultValueDescription(p, cpc));
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
    
    public static void appendParameterText(StringBuilder result,
            Reference pr, Parameter p, Unit unit) {
        appendParameter(result, pr, p, unit, false);
    }

    private static void appendParameter(StringBuilder result,
            Reference pr, Parameter p, Unit unit,
            boolean descriptionOnly) {
        if (p.getModel() == null) {
            result.append(p.getName());
        }
        else {
            TypedReference ppr = pr==null ? 
                    null : pr.getTypedParameter(p);
            appendDeclarationHeader(p.getModel(), ppr, unit, result,
                    descriptionOnly);
            appendParameters(p.getModel(), ppr, unit, result,
                    descriptionOnly);
        }
    }
    
    public static void appendParameterContextInfo(StringBuilder result,
            Reference pr, Parameter p, Unit unit, 
            boolean namedInvocation, boolean isListedValues) {
        if (p.getModel() == null) {
            result.append(p.getName());
        }
        else {
            TypedReference ppr = pr==null ? 
                    null : pr.getTypedParameter(p);
            String typeName;
            Type type = ppr.getType();
            if (isListedValues && namedInvocation) {
                Type et = unit.getIteratedType(type);
                typeName = et.asString(unit);
                if (unit.isEntryType(et)) {
                    typeName = '<' + typeName + '>';
                }
                typeName += unit.isNonemptyIterableType(type) ? '+' : '*';
            }
            else if (p.isSequenced() && !namedInvocation) {
                Type et = unit.getSequentialElementType(type);
                typeName = et.asString(unit);
                if (unit.isEntryType(et)) {
                    typeName = '<' + typeName + '>';
                }
                typeName += p.isAtLeastOne() ? '+' : '*';
            }
            else {
                typeName = type.asString(unit);
            }
            result.append(typeName).append(" ").append(p.getName());
            appendParametersDescription(p.getModel(), ppr, unit, result);
        }
        if (namedInvocation && !isListedValues) {
            result.append(p.getModel() instanceof Function ? 
                    " => ... " : " = ... " );
        }
    }
    
    private static void appendParametersDescription(Declaration d, StyledString result,
            boolean names, boolean types) {
        if (d instanceof Functional) {
            List<ParameterList> plists = ((Functional) d).getParameterLists();
            if (plists!=null) {
                for (ParameterList params: plists) {
                    if (params.getParameters().isEmpty()) {
                        result.append("()");
                    }
                    else {
                        result.append("(");
                        int len = params.getParameters().size(); 
                        int i=0;
                        for (Parameter p: params.getParameters()) {
                            if (p.getModel()==null) {
                                if (names) {
                                    result.append(p.getName());
                                }
                            }
                            else {
                                if (types) {
                                    appendDeclarationDescription(p.getModel(), result);
                                }
                                if (names && types) {
                                    result.append(' ');
                                }
                                if (names) {
                                    appendDeclarationName(p.getModel(), result);
                                }
                                appendParametersDescription(p.getModel(), result, names, types);
                                /*result.append(p.getType().asString(), TYPE_STYLER)
                                    .append(" ").append(p.getName(), ID_STYLER);
                                if (p instanceof FunctionalParameter) {
                                    result.append("(");
                                    FunctionalParameter fp = (FunctionalParameter) p;
                                    List<Parameter> fpl = fp.getParameterLists().get(0).getParameters();
                                    int len2 = fpl.size(), j=0;
                                    for (Parameter pp: fpl) {
                                        result.append(pp.getType().asString(), TYPE_STYLER)
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
