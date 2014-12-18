package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.getDefaultValueDescription;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.DISPLAY_PARAMETER_TYPES;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.DISPLAY_RETURN_TYPES;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.appendTypeName;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultIndent;
import static com.redhat.ceylon.eclipse.util.OccurrenceLocation.EXTENDS;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Constructor;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
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
import com.redhat.ceylon.compiler.typechecker.model.Setter;
import com.redhat.ceylon.compiler.typechecker.model.SiteVariance;
import com.redhat.ceylon.compiler.typechecker.model.TypeAlias;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.UnknownType;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.eclipse.util.OccurrenceLocation;

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
            //      guess if explicit type args will be
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
            Declaration decl) {
        
        Package pkg = decl.getUnit().getPackage();
        String qname = decl.getQualifiedNameString();
        
        // handle language package or same module and package
        Unit unit = cpc.getRootNode().getUnit();
        if (pkg!=null && 
                (Module.LANGUAGE_MODULE_NAME.equals(pkg.getNameAsString())
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
            ProducedReference pr, Unit unit, boolean includeDefaulted,
            String typeArgs) {
        StringBuilder result = new StringBuilder(escapeName(dec, unit));
        if (typeArgs!=null) {
            result.append(typeArgs);
        }
        else if (forceExplicitTypeArgs(dec, ol)) {
            appendTypeParameters(dec, result);
        }
        appendPositionalArgs(dec, pr, unit, result, includeDefaulted, false);
        appendSemiToVoidInvocation(result, dec);
        return result.toString();
    }

    public static String getNamedInvocationTextFor(Declaration dec,
            ProducedReference pr, Unit unit, boolean includeDefaulted,
            String typeArgs) {
        StringBuilder result = new StringBuilder(escapeName(dec, unit));
        if (typeArgs!=null) {
            result.append(typeArgs);
        }
        else if (forceExplicitTypeArgs(dec, null)) {
            appendTypeParameters(dec, result);
        }
        appendNamedArgs(dec, pr, unit, result, includeDefaulted, false);
        appendSemiToVoidInvocation(result, dec);
        return result.toString();
    }
    
    private static void appendSemiToVoidInvocation(StringBuilder result,
            Declaration dd) {
        if ((dd instanceof Method) && ((Method) dd).isDeclaredVoid() && 
                ((Method) dd).getParameterLists().size()==1) {
            result.append(';');
        }
    }
    
    public static String getDescriptionFor(Declaration dec, Unit unit) {
        StringBuilder result = new StringBuilder(dec.getName(unit));
        appendTypeParameters(dec, result);
        return result.toString();
    }
    
    public static String getPositionalInvocationDescriptionFor(
            Declaration dec, OccurrenceLocation ol,
            ProducedReference pr, Unit unit, boolean includeDefaulted,
            String typeArgs) {
        StringBuilder result = new StringBuilder(dec.getName(unit));
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
            Declaration dec, ProducedReference pr, 
            Unit unit, boolean includeDefaulted, String typeArgs) {
        StringBuilder result = new StringBuilder(dec.getName(unit));
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
        appendDeclarationHeaderText(d, pr, unit, result);
        appendTypeParameters(d, result);
        appendParametersText(d, pr, unit, result);
        if (d instanceof Class) {
            result.append(extraIndent(extraIndent(indent, containsNewline), 
                    containsNewline))
                .append(" extends super.").append(escapeName(d));
            appendPositionalArgs(d, pr, unit, result, true, false);
        }
        appendConstraints(d, pr, unit, indent, containsNewline, result);
        appendImplText(d, pr, isInterface, unit, indent, result, ci);
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
        return d instanceof TypedDeclaration && 
                ((TypedDeclaration) d).isVariable();
    }
    
    static String getRefinementDescriptionFor(Declaration d, 
            ProducedReference pr, Unit unit) {
        StringBuilder result = new StringBuilder("shared actual ");
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
    
    static String getInlineFunctionDescriptionFor(Parameter p, 
            ProducedReference pr, Unit unit) {
        StringBuilder result = new StringBuilder();
        appendNamedArgumentHeader(p, pr, result, true);
        appendTypeParameters(p.getModel(), result);
        appendParametersDescription(p.getModel(), pr, unit, result);
        return result.toString();
    }
    
    public static String getLabelDescriptionFor(Declaration d) {
        StringBuilder result = new StringBuilder();
        if (d!=null) {
            appendDeclarationAnnotations(d, result);
            appendDeclarationHeaderDescription(d, d.getUnit(), result);
            appendTypeParameters(d, result, true);
            appendParametersDescription(d, result, null);
        }
        return result.toString();
    }

    private static void appendDeclarationAnnotations(Declaration d,
            StringBuilder result) {
        if (d.isActual()) result.append("actual ");
        if (d.isFormal()) result.append("formal ");
        if (d.isDefault()) result.append("default ");
        if (isVariable(d)) result.append("variable ");
    }
    
    public static String getDocDescriptionFor(Declaration d, 
            ProducedReference pr, Unit unit) {
        StringBuilder result = new StringBuilder();
        appendDeclarationHeaderDescription(d, pr, unit, result);
        appendTypeParameters(d, pr, result, true, unit);
        appendParametersDescription(d, pr, unit, result);
        return result.toString();
    }
    
    public static StyledString getQualifiedDescriptionFor(Declaration d) {
        StyledString result = new StyledString();
        if (d!=null) {
            appendDeclarationDescription(d, result);
            if (d.isClassOrInterfaceMember()) {
                Declaration ci = (Declaration) d.getContainer();
                result.append(ci.getName(), Highlights.TYPE_ID_STYLER).append('.');
                appendMemberName(d, result);
            }
            else {
                appendDeclarationName(d, result);
            }
            appendTypeParameters(d, result, true);
            appendParametersDescription(d, result);
            if (d instanceof TypedDeclaration) {
                if (EditorsUI.getPreferenceStore().getBoolean(DISPLAY_RETURN_TYPES)) {
                    TypedDeclaration td = (TypedDeclaration) d;
                    if (!td.isParameter() && 
                            !td.isDynamicallyTyped() &&
                            !(td instanceof Method && ((Method) td).isDeclaredVoid())) {
                        ProducedType t = td.getType();
                        if (t!=null) {
                            result.append(" ∊ ");
                            appendTypeName(result, t, Highlights.ARROW_STYLER);
                        }
                    }
                }
            }
            /*result.append(" - refines declaration in ") 
                .append(((Declaration) d.getContainer()).getName());*/
        }
        return result;
    }
    
    public static StyledString getStyledDescriptionFor(Declaration d) {
        StyledString result = new StyledString();
        if (d!=null) {
            appendDeclarationAnnotations(d, result);
            appendDeclarationDescription(d, result);
            appendDeclarationName(d, result);
            appendTypeParameters(d, result, true);
            appendParametersDescription(d, result);
            if (d instanceof TypedDeclaration) {
                if (EditorsUI.getPreferenceStore().getBoolean(DISPLAY_RETURN_TYPES)) {
                    TypedDeclaration td = (TypedDeclaration) d;
                    if (!td.isParameter() && 
                            !td.isDynamicallyTyped() &&
                            !(td instanceof Method && ((Method) td).isDeclaredVoid())) {
                        ProducedType t = td.getType();
                        if (t!=null) {
                            result.append(" ∊ ");
                            appendTypeName(result, t, Highlights.ARROW_STYLER);
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
        if (d.isActual()) result.append("actual ", Highlights.ANN_STYLER);
        if (d.isFormal()) result.append("formal ", Highlights.ANN_STYLER);
        if (d.isDefault()) result.append("default ", Highlights.ANN_STYLER);
        if (isVariable(d)) result.append("variable ", Highlights.ANN_STYLER);
    }
    
    public static void appendPositionalArgs(Declaration dec,
            Unit unit, StringBuilder result, boolean includeDefaulted,
            boolean descriptionOnly) {
        appendPositionalArgs(dec, dec.getReference(), 
                unit, result, includeDefaulted,
                descriptionOnly);
    }
    
    private static void appendPositionalArgs(Declaration d, ProducedReference pr, 
            Unit unit, StringBuilder result, boolean includeDefaulted,
            boolean descriptionOnly) {
        if (d instanceof Functional) {
            List<Parameter> params = getParameters((Functional) d, 
                    includeDefaulted, false);
            if (params.isEmpty()) {
                result.append("()");
            }
            else {
                boolean paramTypes = descriptionOnly && 
                        EditorsUI.getPreferenceStore().getBoolean(DISPLAY_PARAMETER_TYPES);
                result.append("(");
                for (Parameter p: params) {
                    ProducedTypedReference typedParameter = 
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
                        ProducedType pt = typedParameter.getType();
                        if (descriptionOnly && paramTypes && !isTypeUnknown(pt)) {
                            if (p.isSequenced()) {
                                pt = unit.getSequentialElementType(pt);
                            }
                            result.append(pt.getProducedTypeName(unit));
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
    
    static void appendSuperArgsText(Declaration d, ProducedReference pr, 
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
                boolean paramTypes = descriptionOnly && 
                        EditorsUI.getPreferenceStore().getBoolean(DISPLAY_PARAMETER_TYPES);
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
                            if (paramTypes && !isTypeUnknown(p.getType())) {
                                result.append(p.getType().getProducedTypeName(unit)).append(" ");
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
                            if (descriptionOnly && paramTypes && !isTypeUnknown(p.getType())) {
                                result.append(p.getType().getProducedTypeName(unit)).append(" ");
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

    private static void appendTypeParameters(Declaration d, 
            StringBuilder result) {
        appendTypeParameters(d, result, false);
    }
    
    private static void appendTypeParameters(Declaration d, 
            StringBuilder result, boolean variances) {
        if (d instanceof Generic) {
            List<TypeParameter> types = 
                    ((Generic) d).getTypeParameters();
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
                    result.append(tp.getName()).append(", ");
                }
                result.setLength(result.length()-2);
                result.append(">");
            }
        }
    }
    
    private static void appendTypeParameters(Declaration d, 
            ProducedReference pr, StringBuilder result, 
            boolean variances, Unit unit) {
        if (d instanceof Generic) {
            List<TypeParameter> types = 
                    ((Generic) d).getTypeParameters();
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
                    ProducedType arg = pr==null ? 
                            null : pr.getTypeArguments().get(tp);
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
                        if (pr instanceof ProducedType) {
                            if (variances) {
                                SiteVariance variance = 
                                        ((ProducedType) pr).getVarianceOverrides().get(tp);
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
                        result.append(arg.getProducedTypeName(unit));
                    }
                }
                result.append(">");
            }
        }
    }
    
    private static void appendTypeParameters(Declaration d, 
            StyledString result, boolean variances) {
        if (d instanceof Generic) {
            List<TypeParameter> types = 
                    ((Generic) d).getTypeParameters();
            if (!types.isEmpty()) {
                result.append("<");
                int len = types.size(), i = 0;
                for (TypeParameter tp: types) {
                    if (variances) {
                        if (tp.isCovariant()) {
                            result.append("out ", Highlights.KW_STYLER);
                        }
                        if (tp.isContravariant()) {
                            result.append("in ", Highlights.KW_STYLER);
                        }
                    }
                    result.append(tp.getName(), Highlights.TYPE_STYLER);
                    if (++i<len) result.append(", ");
                }
                result.append(">");
            }
        }
    }
    
    private static void appendDeclarationHeaderDescription(Declaration d, 
            Unit unit, StringBuilder result) {
        appendDeclarationHeader(d, null, unit, result, true);
    }
    
    private static void appendDeclarationHeaderDescription(Declaration d, 
            ProducedReference pr, Unit unit, StringBuilder result) {
        appendDeclarationHeader(d, pr, unit, result, true);
    }
    
    private static void appendDeclarationHeaderText(Declaration d, 
            ProducedReference pr, Unit unit, StringBuilder result) {
        appendDeclarationHeader(d, pr, unit, result, false);
    }
    
    private static void appendDeclarationHeader(Declaration d, 
            ProducedReference pr, Unit unit, 
            StringBuilder result, 
            boolean descriptionOnly) {
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
        else if (d instanceof Constructor) {
            result.append("new");
        }
        else if (d instanceof TypedDeclaration) {
            TypedDeclaration td = (TypedDeclaration) d;
            boolean isSequenced = d.isParameter() && 
                    ((MethodOrValue) d).getInitializerParameter()
                            .isSequenced();
            ProducedType type;
            if (pr == null) {
                type = td.getType();
            }
            else {
                type = pr.getType();
            }
            if (isSequenced && type!=null) {
                type = unit.getIteratedType(type);
            }
            if (type==null) {
                type = new UnknownType(unit).getType();
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
        result.append(" ")
            .append(descriptionOnly ? 
                    d.getName() : escapeName(d));
    }
    
    private static void appendNamedArgumentHeader(Parameter p, 
            ProducedReference pr, StringBuilder result,
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
                    p.getName() : escapeName(p.getModel()));
    }
    
    private static void appendDeclarationDescription(Declaration d, 
            StyledString result) {
        if (d instanceof Class) {
            if (d.isAnonymous()) {
                result.append("object", Highlights.KW_STYLER);
            }
            else {
                result.append("class", Highlights.KW_STYLER);
            }
        }
        else if (d instanceof Interface) {
            result.append("interface", Highlights.KW_STYLER);
        }
        else if (d instanceof TypeAlias) {
            result.append("alias", Highlights.KW_STYLER);
        }
        else if (d.isParameter()) {
            TypedDeclaration td = (TypedDeclaration) d;
            ProducedType type = td.getType();
            if (td.isDynamicallyTyped()) {
                result.append("dynamic", Highlights.KW_STYLER);
            }
            else if (type!=null) {
                boolean isSequenced = //d.isParameter() && 
                        ((MethodOrValue) d).getInitializerParameter()
                                .isSequenced();
                if (isSequenced) {
                    type = d.getUnit().getIteratedType(type);
                }
                /*if (td instanceof Value &&
                        td.getTypeDeclaration().isAnonymous()) {
                    result.append("object", KW_STYLER);
                }
                else*/ if (d instanceof Method) {
                    if (((Functional)d).isDeclaredVoid()) {
                        result.append("void", Highlights.KW_STYLER);
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
                result.append("dynamic", Highlights.KW_STYLER);
            }
            else if (v.getTypeDeclaration()!=null &&
                    v.getTypeDeclaration().isAnonymous()) {
                result.append("object", Highlights.KW_STYLER);
            }
            else {
                result.append("value", Highlights.KW_STYLER);
            }
        }
        else if (d instanceof Method) {
            Method m = (Method) d;
            if (m.isDynamicallyTyped()) {
                result.append("dynamic", Highlights.KW_STYLER);
            }
            else if (m.isDeclaredVoid()) {
                result.append("void", Highlights.KW_STYLER);
            }
            else {
                result.append("function", Highlights.KW_STYLER);
            }
        }
        else if (d instanceof Setter) {
            result.append("assign", Highlights.KW_STYLER);
        }
        result.append(" ");
    }

    private static void appendMemberName(Declaration d, StyledString result) {
        String name = d.getName();
        if (name != null) {
            if (d instanceof TypeDeclaration) {
                result.append(name, Highlights.TYPE_STYLER);
            }
            else {
                result.append(name, Highlights.MEMBER_STYLER);
            }
        }
    }
    
    private static void appendDeclarationName(Declaration d, StyledString result) {
        String name = d.getName();
        if (name != null) {
            if (d instanceof TypeDeclaration) {
                result.append(name, Highlights.TYPE_STYLER);
            }
            else {
                result.append(name, Highlights.ID_STYLER);
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
    
    private static void appendImplText(Declaration d, ProducedReference pr, 
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
                appendSuperArgsText(d, pr, unit, result, true);
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
                        .append(d.getName()).append(";");
                }
                if (isVariable(d)) {
                    result.append(indent)
                        .append("assign ").append(d.getName())
                        .append(" {}");
                }
            }
            else {
                //we can have a references, so use = instead 
                //of => for variables
                String arrow = isVariable(d) ? " = " : " => ";
                if (d.isFormal()) {
                    result.append(arrow).append("nothing;");
                }
                else {
                    result.append(arrow)
                        .append("super.").append(d.getName())
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

    private static boolean isObjectField(Declaration m) {
        return m.getName()!=null &&
                m.getName().equals("hash") ||
                m.getName().equals("string");
    }

    private static void appendMembersToEquals(Unit unit, String indent,
            StringBuilder result, ClassOrInterface ci, Parameter p) {
        boolean found = false;
        for (Declaration m: ci.getMembers()) {
            if (m instanceof Value && 
                    !isObjectField(m)) {
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
            if (m instanceof Value && 
                    !isObjectField(m)) {
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
    
    public static void appendParametersDescription(Declaration d, StringBuilder result, 
            CeylonParseController cpc) {
        appendParameters(d, null, d.getUnit(), result, cpc, true);
    }
    
    public static void appendParametersText(Declaration d, ProducedReference pr, 
            Unit unit, StringBuilder result) {
        appendParameters(d, pr, unit, result, null, false);
    }
    
    private static void appendParametersDescription(Declaration d, ProducedReference pr, 
            Unit unit, StringBuilder result) {
        appendParameters(d, pr, unit, result, null, true);
    }
    
    private static void appendParameters(Declaration d, ProducedReference pr, 
            Unit unit, StringBuilder result, boolean descriptionOnly) {
        appendParameters(d, pr, unit, result, null, descriptionOnly);
    }
    
    private static void appendParameters(Declaration d, ProducedReference pr, 
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
            ProducedReference pr, Parameter p, Unit unit) {
        appendParameter(result, pr, p, unit, false);
    }

    private static void appendParameter(StringBuilder result,
            ProducedReference pr, Parameter p, Unit unit,
            boolean descriptionOnly) {
        if (p.getModel() == null) {
            result.append(p.getName());
        }
        else {
            ProducedTypedReference ppr = pr==null ? 
                    null : pr.getTypedParameter(p);
            appendDeclarationHeader(p.getModel(), ppr, unit, result,
                    descriptionOnly);
            appendParameters(p.getModel(), ppr, unit, result,
                    descriptionOnly);
        }
    }
    
    public static void appendParameterContextInfo(StringBuilder result,
            ProducedReference pr, Parameter p, Unit unit, 
            boolean namedInvocation, boolean isListedValues) {
        if (p.getModel() == null) {
            result.append(p.getName());
        }
        else {
            ProducedTypedReference ppr = pr==null ? 
                    null : pr.getTypedParameter(p);
            String typeName;
            ProducedType type = ppr.getType();
            if (isListedValues && namedInvocation) {
                ProducedType et = unit.getIteratedType(type);
                typeName = et.getProducedTypeName(unit);
                if (unit.isEntryType(et)) {
                    typeName = '<' + typeName + '>';
                }
                typeName += unit.isNonemptyIterableType(type) ? '+' : '*';
            }
            else if (p.isSequenced() && !namedInvocation) {
                ProducedType et = unit.getSequentialElementType(type);
                typeName = et.getProducedTypeName(unit);
                if (unit.isEntryType(et)) {
                    typeName = '<' + typeName + '>';
                }
                typeName += p.isAtLeastOne() ? '+' : '*';
            }
            else {
                typeName = type.getProducedTypeName(unit);
            }
            result.append(typeName).append(" ").append(p.getName());
            appendParametersDescription(p.getModel(), ppr, unit, result);
        }
        if (namedInvocation && !isListedValues) {
            result.append(p.getModel() instanceof Method ? 
                    " => ... " : " = ... " );
        }
    }
    
    private static void appendParametersDescription(Declaration d, StyledString result) {
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
                                appendDeclarationDescription(p.getModel(), result);
                                appendDeclarationName(p.getModel(), result);
                                appendParametersDescription(p.getModel(), result);
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
