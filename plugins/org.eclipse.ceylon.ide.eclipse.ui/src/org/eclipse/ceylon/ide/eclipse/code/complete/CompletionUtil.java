/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static org.eclipse.ceylon.ide.eclipse.code.complete.ParameterContextValidator.findCharCount;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedNode;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isNameMatching;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;
import org.eclipse.ceylon.ide.common.util.escaping_;
import org.eclipse.ceylon.model.typechecker.model.Class;
import org.eclipse.ceylon.model.typechecker.model.Constructor;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Function;
import org.eclipse.ceylon.model.typechecker.model.FunctionOrValue;
import org.eclipse.ceylon.model.typechecker.model.Functional;
import org.eclipse.ceylon.model.typechecker.model.Interface;
import org.eclipse.ceylon.model.typechecker.model.Parameter;
import org.eclipse.ceylon.model.typechecker.model.ParameterList;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypeParameter;
import org.eclipse.ceylon.model.typechecker.model.Unit;
import org.eclipse.ceylon.model.typechecker.model.Value;

public class CompletionUtil {

    public static List<Declaration> overloads(Declaration dec) {
        return dec.isAbstraction() ? 
                dec.getOverloads() : 
                singletonList(dec);
    }

    static List<Parameter> getParameters(ParameterList pl,
            boolean includeDefaults, boolean namedInvocation) {
        List<Parameter> ps = pl.getParameters();
        if (includeDefaults) {
            return ps;
        }
        else {
            List<Parameter> list = 
                    new ArrayList<Parameter>();
            for (Parameter p: ps) {
                if (!p.isDefaulted() || 
                        (namedInvocation && 
                                spreadable(p, ps))) {
                    list.add(p);
                }
            }
            return list;
        }
    }

    private static boolean spreadable(Parameter param, 
            List<Parameter> list) {
        Parameter lastParam = 
                list.get(list.size()-1);
        if (param==lastParam &&
                param.getModel() instanceof Value) {
            Type type = param.getType();
            Unit unit = param.getDeclaration().getUnit();
            return type!=null &&
                unit.isIterableParameterType(type);
        }
        else {
            return false;
        }
    }

    static String fullPath(int offset, String prefix,
            Tree.ImportPath path) {
        StringBuilder fullPath = new StringBuilder();
        if (path!=null) {
            String pathString = 
                    formatPath(path.getIdentifiers());
            fullPath.append(pathString)
                    .append('.');
            int len = 
                    offset
                        -path.getStartIndex()
                        -prefix.length();
            fullPath.setLength(len);
        }
        return fullPath.toString();
    }

    static boolean isPackageDescriptor(CeylonParseController cpc) {
        Tree.CompilationUnit lcu = 
                cpc.getLastCompilationUnit();
        return lcu != null 
            && lcu.getUnit() != null 
            && lcu.getUnit()
                .getFilename()
                .equals("package.ceylon"); 
    }

    static boolean isModuleDescriptor(CeylonParseController cpc) {
        Tree.CompilationUnit lcu = 
                cpc.getLastCompilationUnit();
        return lcu != null 
            && lcu.getUnit() != null 
            && lcu.getUnit()
                .getFilename()
                .equals("module.ceylon"); 
    }

    static boolean isEmptyModuleDescriptor(CeylonParseController cpc) {
        Tree.CompilationUnit lcu = 
                cpc.getLastCompilationUnit();
        return isModuleDescriptor(cpc) 
            && lcu != null 
            && lcu.getModuleDescriptors()
                .isEmpty(); 
    }

    static boolean isEmptyPackageDescriptor(CeylonParseController cpc) {
        Tree.CompilationUnit lcu = 
                cpc.getLastCompilationUnit();
        return lcu != null 
            && lcu.getUnit() != null 
            && lcu.getUnit()
                .getFilename()
                .equals("package.ceylon") 
            && lcu.getPackageDescriptors()
                .isEmpty();
    }

    static int nextTokenType(CeylonParseController cpc,
            CommonToken token) {
        for (int i=token.getTokenIndex()+1; 
                i<cpc.getTokens().size(); 
                i++) {
            CommonToken tok = cpc.getTokens().get(i);
            if (tok.getChannel()!=CommonToken.HIDDEN_CHANNEL) {
                return tok.getType();
            }
        }
        return -1;
    }

    static int getLine(final int offset, ITextViewer viewer) {
        int line=-1;
        try {
            line = viewer.getDocument().getLineOfOffset(offset);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static boolean isInBounds(List<Type> upperBounds, Type t) {
        boolean ok = true;
        for (Type ub: upperBounds) {
            if (!t.isSubtypeOf(ub) &&
                    !(ub.involvesTypeParameters() &&
                            t.getDeclaration()
                                .inherits(ub.getDeclaration()))) {
                ok = false;
                break;
            }
        }
        return ok;
    }
    
    public static List<DeclarationWithProximity> 
    getSortedProposedValues(Scope scope, Unit unit) {
        return getSortedProposedValues(scope, unit, null);
    }
    
    public static List<DeclarationWithProximity> 
    getSortedProposedValues(Scope scope, Unit unit, 
            final String exactName) {
        Map<String, DeclarationWithProximity> map = 
                scope.getMatchingDeclarations(unit, "", 0, null);
        if (exactName!=null) {
            for (DeclarationWithProximity dwp: 
                new ArrayList<DeclarationWithProximity>
                        (map.values())) {
                if (!dwp.isUnimported() && 
                    !dwp.isAlias() &&
                        isNameMatching(dwp.getName(), 
                                exactName)) {
                    map.put(dwp.getName(), 
                            new DeclarationWithProximity(
                                    dwp.getDeclaration(), -5));
                }
            }
        }
        List<DeclarationWithProximity> results = 
                new ArrayList<DeclarationWithProximity>(
                        map.values());
        Collections.sort(results, 
                new ArgumentProposalComparator(exactName));
        return results;
    }

    public static boolean isIgnoredLanguageModuleClass(Class clazz) {
        return clazz.isString() 
            || clazz.isInteger() 
            || clazz.isFloat() 
            || clazz.isCharacter() 
            || clazz.isAnnotation();
    }

    public static boolean isIgnoredLanguageModuleValue(Value value) {
        String name = value.getName();
        return name.equals("process") 
            || name.equals("runtime") 
            || name.equals("system") 
            || name.equals("operatingSystem") 
            || name.equals("language") 
            || name.equals("emptyIterator") 
            || name.equals("infinity") 
            || name.endsWith("IntegerValue") 
            || name.equals("finished");
    }

    public static boolean isIgnoredLanguageModuleMethod(Function method) {
        String name = method.getName();
        return name.equals("className") 
            || name.equals("flatten") 
            || name.equals("unflatten")
            || name.equals("curry") 
            || name.equals("uncurry") 
            || name.equals("compose") 
            || method.isAnnotation();
    }

    static boolean isIgnoredLanguageModuleType(TypeDeclaration td) {
        return !td.isObject() 
            && !td.isAnything() 
            && !td.isString() 
            && !td.isInteger() 
            && !td.isCharacter() 
            && !td.isFloat() 
            && !td.isBoolean();
    }

    public static String getInitialValueDescription(
            final Declaration dec, 
            CeylonParseController cpc) {
        if (cpc!=null) {
            Node refnode = getReferencedNode(dec);
            Tree.SpecifierOrInitializerExpression sie = null;
            String arrow = null;
            if (refnode instanceof Tree.AttributeDeclaration) {
                Tree.AttributeDeclaration ad = 
                        (Tree.AttributeDeclaration) 
                            refnode;
                sie = ad.getSpecifierOrInitializerExpression();
                arrow = " = ";
            }
            else if (refnode instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration md =
                        (Tree.MethodDeclaration) 
                            refnode;
                sie = md.getSpecifierExpression();
                arrow = " => ";
            }
            Tree.CompilationUnit lcu = 
                    cpc.getLastCompilationUnit();
            if (sie==null) {
                class FindInitializerVisitor extends Visitor {
                    Tree.SpecifierOrInitializerExpression result;
                    @Override
                    public void visit(
                            Tree.InitializerParameter that) {
                        super.visit(that);
                        Declaration d = 
                                that.getParameterModel()
                                    .getModel();
                        if (d!=null && d.equals(dec)) {
                            result = that.getSpecifierExpression();
                        }
                    }
                }
                FindInitializerVisitor fiv = 
                        new FindInitializerVisitor();
                fiv.visit(lcu);
                sie = fiv.result;
            }
            if (sie!=null) {
                Tree.Expression e = sie.getExpression();
                if (e!=null) {
                    Tree.Term term = e.getTerm();
                    if (term instanceof Tree.Literal) {
                        String text = 
                                term.getToken()
                                    .getText();
                        if (text.length()<20) {
                            return arrow + text;
                        }
                    }
                    else if (term instanceof Tree.BaseMemberOrTypeExpression) {
                        Tree.BaseMemberOrTypeExpression bme = 
                                (Tree.BaseMemberOrTypeExpression) 
                                    term;
                        Tree.Identifier id = bme.getIdentifier();
                        if (id!=null && 
                                bme.getTypeArguments()==null) {
                            return arrow + id.getText();
                        }
                    }
                    else if (term!=null) {
                        Unit unit = lcu.getUnit();
                        if (term.getUnit().equals(unit)) {
                            String impl = 
                                    Nodes.text(term, 
                                            cpc.getTokens());
                            if (impl.length()<10) {
                                return arrow + impl;
                            }
                        }
                    }
                    //don't have the token stream :-/
                    //TODO: figure out where to get it from!
                    return arrow + "...";
                }
            }
        }
        return "";
    }

    public static String getDefaultValueDescription(
            Parameter param, CeylonParseController cpc) {
        if (param.isDefaulted()) {
            FunctionOrValue model = param.getModel();
            if (model instanceof Functional) {
                return " => ...";
            }
            else {
                return getInitialValueDescription(model, cpc);
            }
        }
        else {
            return "";
        }
    }

    static String anonFunctionHeader(
            Type requiredType, Unit unit) {
        StringBuilder text = new StringBuilder();
        text.append("(");
        boolean first = true;
        char c = 'a';
        List<Type> argTypes = 
                unit.getCallableArgumentTypes(requiredType);
        for (Type paramType: argTypes) {
            if (first) {
                first = false;
            }
            else {
                text.append(", ");
            }
            text.append(paramType.asSourceCodeString(unit))
                .append(" ")
                .append(c++);
        }
        text.append(")");
        return text.toString();
    }

    public static IRegion getCurrentSpecifierRegion(
            IDocument document, int offset) 
                    throws BadLocationException {
        int start = offset;
        int length = 0;
        for (int i=offset;
                i<document.getLength(); 
                i++) {
            char ch = document.getChar(i);
            if (Character.isWhitespace(ch) ||
                    ch==';'||ch==','||ch==')') {
                break;
            }
            length++;
        }
        return new Region(start, length);
    }
    
    public static String getProposedName(
            Declaration qualifier, Declaration dec, 
            Unit unit) {
        StringBuilder buf = new StringBuilder();
        if (qualifier!=null) {
            buf.append(escaping_.get_().escapeName(qualifier, unit))
              .append('.');
        }
        if (dec instanceof Constructor) {
            Constructor constructor = 
                    (Constructor) dec;
            TypeDeclaration clazz = 
                    constructor.getExtendedType()
                        .getDeclaration();
            buf.append(escaping_.get_().escapeName(clazz, unit))
                    .append('.');
        }
        buf.append(escaping_.get_().escapeName(dec, unit));
        return buf.toString();
    }

    static IRegion getCurrentArgumentRegion(
            IDocument document, int loc, int index,
            int startOfArgs) 
                    throws BadLocationException {
        IRegion li = 
                document.getLineInformationOfOffset(loc);
        int endOfLine = li.getOffset() + li.getLength();
        int offset = 
                findCharCount(index, document, 
                    loc+startOfArgs, endOfLine, 
                    ",;", "", true) + 1;
        if (offset>0 && document.getChar(offset)==' ') {
            offset++;
        }
        int nextOffset = 
                findCharCount(index+1, document, 
                    loc+startOfArgs, endOfLine, 
                    ",;", "", true);
        int middleOffset = 
                findCharCount(1, document, 
                    offset, nextOffset, 
                    "=", "", true)+1;
        if (middleOffset>0 &&
                document.getChar(middleOffset)=='>') {
            middleOffset++;
        }
        while (middleOffset>0 &&
                document.getChar(middleOffset)==' ') {
            middleOffset++;
        }
        if (middleOffset>offset &&
                middleOffset<nextOffset) {
            offset = middleOffset;
        }
        if (nextOffset==-1) {
            nextOffset = offset;
        }
        return new Region(offset, nextOffset-offset);
    }
    
    public static String[] getAssignableLiterals(
            Type type, Unit unit) {
        TypeDeclaration dtd = 
                unit.getDefiniteType(type)
                    .getDeclaration();
        if (dtd instanceof Class) {
            if (dtd.isInteger()) {
                return new String[] { "0", "1", "2" };
            }
            if (dtd.isByte()) {
                return new String[] { "0.byte", "1.byte" };
            }
            else if (dtd.isFloat()) {
                return new String[] { "0.0", "1.0", "2.0" };
            }
            else if (dtd.isString()) {
                return new String[] { "\"\"", "\"\"\"\"\"\"" };
            }
            else if (dtd.isCharacter()) {
                return new String[] { "' '", "'\\n'", "'\\t'" };
            }
            else {
                return new String[0];
            }
        }
        else if (dtd instanceof Interface) {
            if (dtd.isIterable()) {
                return new String[] { "{}" };
            }
            else if (dtd.isSequential() || dtd.isEmpty()) {
                return new String[] { "[]" };
            }
            else {
                return new String[0]; 
            }
         }
        else {
            return new String[0];
        }
    }

    @Deprecated
    protected static boolean withinBounds(Type requiredType, Type type, Scope scope) {
        TypeDeclaration td = requiredType.resolveAliases().getDeclaration();
        if (type.isSubtypeOf(requiredType)) {
            return true;
        }
        else if (td instanceof TypeParameter) {
            return !td.isDefinedInScope(scope) &&
                    isInBounds(td.getSatisfiedTypes(), type);
        }
        else if (type.getDeclaration().inherits(td)) {
            Type supertype = type.getSupertype(td);
            for (TypeParameter tp: td.getTypeParameters()) {
                Type ta = supertype.getTypeArguments().get(tp);
                Type rta = requiredType.getTypeArguments().get(tp);
                if (ta!=null && rta!=null) {
                    if (!withinBounds(rta, ta, scope)) {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

}
