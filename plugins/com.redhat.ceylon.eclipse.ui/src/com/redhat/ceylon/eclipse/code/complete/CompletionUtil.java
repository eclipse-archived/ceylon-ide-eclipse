package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static com.redhat.ceylon.eclipse.code.complete.ParameterContextValidator.findCharCount;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Constructor;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Functional;
import com.redhat.ceylon.model.typechecker.model.Interface;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.ParameterList;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.Value;

public class CompletionUtil {

    public static List<Declaration> overloads(Declaration dec) {
        if (dec.isAbstraction()) {
            return dec.getOverloads();
        }
        else {
            return Collections.singletonList(dec);
        }
    }

    static List<Parameter> getParameters(ParameterList pl,
            boolean includeDefaults, boolean namedInvocation) {
        List<Parameter> ps = pl.getParameters();
        if (includeDefaults) {
            return ps;
        }
        else {
            List<Parameter> list = new ArrayList<Parameter>();
            for (Parameter p: ps) {
                if (!p.isDefaulted() || 
                        (namedInvocation && 
                                p==ps.get(ps.size()-1) && 
                                p.getModel() instanceof Value &&
                                p.getType()!=null &&
                                p.getDeclaration().getUnit()
                                        .isIterableParameterType(p.getType()))) {
                    list.add(p);
                }
            }
            return list;
        }
    }

    static String fullPath(int offset, String prefix,
            Tree.ImportPath path) {
        StringBuilder fullPath = new StringBuilder();
        if (path!=null) {
            fullPath.append(formatPath(path.getIdentifiers()));
            fullPath.append('.');
            fullPath.setLength(offset-path.getStartIndex()-prefix.length());
        }
        return fullPath.toString();
    }

    static boolean isPackageDescriptor(CeylonParseController cpc) {
        return cpc.getRootNode() != null && 
                cpc.getRootNode().getUnit() != null &&
                cpc.getRootNode().getUnit().getFilename().equals("package.ceylon"); 
    }

    static boolean isModuleDescriptor(CeylonParseController cpc) {
        return cpc.getRootNode() != null && 
                cpc.getRootNode().getUnit() != null &&
                cpc.getRootNode().getUnit().getFilename().equals("module.ceylon"); 
    }

    static boolean isEmptyModuleDescriptor(CeylonParseController cpc) {
        return isModuleDescriptor(cpc) && 
                cpc.getRootNode() != null && 
                cpc.getRootNode().getModuleDescriptors().isEmpty(); 
    }

    static boolean isEmptyPackageDescriptor(CeylonParseController cpc) {
        return cpc.getRootNode() != null &&
                cpc.getRootNode().getUnit() != null &&
                cpc.getRootNode().getUnit().getFilename().equals("package.ceylon") && 
                cpc.getRootNode().getPackageDescriptors().isEmpty();
    }

    static int nextTokenType(final CeylonParseController cpc,
            final CommonToken token) {
        for (int i=token.getTokenIndex()+1; i<cpc.getTokens().size(); i++) {
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
                            t.getDeclaration().inherits(ub.getDeclaration()))) {
                ok = false;
                break;
            }
        }
        return ok;
    }

    public static List<DeclarationWithProximity> 
    getSortedProposedValues(Scope scope, Unit unit) {
        Collection<DeclarationWithProximity> suggestions = 
                scope.getMatchingDeclarations(unit, "", 0)
                    .values();
        List<DeclarationWithProximity> results = 
                new ArrayList<DeclarationWithProximity>(
                        suggestions);
        Collections.sort(results, 
                new Comparator<DeclarationWithProximity>() {
            public int compare(
                    DeclarationWithProximity x, 
                    DeclarationWithProximity y) {
                int p = x.getProximity()-y.getProximity();
                if (p!=0) {
                    return p;
                }
                Declaration xd = x.getDeclaration();
                Declaration yd = y.getDeclaration();
                int c = xd.getName().compareTo(yd.getName());
                if (c!=0) {
                    return c;  
                }
                return xd.getQualifiedNameString()
                        .compareTo(yd.getQualifiedNameString());
            }
        });
        return results;
    }

    public static boolean isIgnoredLanguageModuleClass(Class clazz) {
        String name = clazz.getName();
        return name.equals("String") ||
                name.equals("Integer") ||
                name.equals("Float") ||
                name.equals("Character") ||
                clazz.isAnnotation();
    }

    public static boolean isIgnoredLanguageModuleValue(Value value) {
        String name = value.getName();
        return name.equals("process") ||
                name.equals("runtime") ||
                name.equals("system") ||
                name.equals("operatingSystem") ||
                name.equals("language") ||
                name.equals("emptyIterator") ||
                name.equals("infinity") ||
                name.endsWith("IntegerValue") ||
                name.equals("finished");
    }

    public static boolean isIgnoredLanguageModuleMethod(Function method) {
        String name = method.getName();
        return name.equals("className") || 
                name.equals("flatten") || 
                name.equals("unflatten")|| 
                name.equals("curry") || 
                name.equals("uncurry") ||
                name.equals("compose") ||
                method.isAnnotation();
    }

    static boolean isIgnoredLanguageModuleType(TypeDeclaration td) {
        String name = td.getName();
        return !name.equals("Object") && 
                !name.equals("Anything") &&
                !name.equals("String") &&
                !name.equals("Integer") &&
                !name.equals("Character") &&
                !name.equals("Float") &&
                !name.equals("Boolean");
    }

    public static String getInitialValueDescription(final Declaration dec, 
            CeylonParseController cpc) {
        if (cpc!=null) {
            Node refnode = Nodes.getReferencedNode(dec);
            Tree.SpecifierOrInitializerExpression sie = null;
            String arrow = null;
            if (refnode instanceof Tree.AttributeDeclaration) {
                Tree.AttributeDeclaration ad = 
                        (Tree.AttributeDeclaration) refnode;
                sie = ad.getSpecifierOrInitializerExpression();
                arrow = " = ";
            }
            else if (refnode instanceof Tree.MethodDeclaration) {
                Tree.MethodDeclaration md =
                        (Tree.MethodDeclaration) refnode;
                sie = md.getSpecifierExpression();
                arrow = " => ";
            }
            if (sie==null) {
                class FindInitializerVisitor extends Visitor {
                    Tree.SpecifierOrInitializerExpression result;
                    @Override
                    public void visit(Tree.InitializerParameter that) {
                        super.visit(that);
                        Declaration d = that.getParameterModel().getModel();
                        if (d!=null && d.equals(dec)) {
                            result = that.getSpecifierExpression();
                        }
                    }
                }
                FindInitializerVisitor fiv = new FindInitializerVisitor();
                fiv.visit(cpc.getRootNode());
                sie = fiv.result;
            }
            if (sie!=null) {
                Tree.Expression e = sie.getExpression();
                if (e!=null) {
                    Tree.Term term = e.getTerm();
                    if (term instanceof Tree.Literal) {
                        String text = term.getToken().getText();
                        if (text.length()<20) {
                            return arrow + text;
                        }
                    }
                    else if (term instanceof Tree.BaseMemberOrTypeExpression) {
                        Tree.BaseMemberOrTypeExpression bme = 
                                (Tree.BaseMemberOrTypeExpression) term;
                        Tree.Identifier id = bme.getIdentifier();
                        if (id!=null && bme.getTypeArguments()==null) {
                            return arrow + id.getText();
                        }
                    }
                    else if (term.getUnit().equals(cpc.getRootNode().getUnit())) {
                        String impl = Nodes.toString(term, cpc.getTokens());
                        if (impl.length()<10) {
                            return arrow + impl;
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

    public static String getDefaultValueDescription(Parameter p, 
            CeylonParseController cpc) {
        if (p.isDefaulted()) {
            if (p.getModel() instanceof Functional) {
                return " => ...";
            }
            else {
                return getInitialValueDescription(p.getModel(), cpc);
            }
        }
        else {
            return "";
        }
    }

    static String anonFunctionHeader(Type requiredType,
            Unit unit) {
        StringBuilder text = new StringBuilder();
        text.append("(");
        boolean first = true;
        char c = 'a';
        for (Type paramType: 
                unit.getCallableArgumentTypes(requiredType)) {
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
            buf.append(escapeName(qualifier, unit))
              .append('.');
        }
        if (dec instanceof Constructor) {
            Constructor constructor = 
                    (Constructor) dec;
            TypeDeclaration clazz = 
                    constructor.getExtendedType()
                        .getDeclaration();
            buf.append(escapeName(clazz, unit))
                    .append('.');
        }
        buf.append(escapeName(dec, unit));
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
    
    public static String[] getAssignableLiterals(Type type, Unit unit) {
        TypeDeclaration dtd = 
                unit.getDefiniteType(type)
                    .getDeclaration();
        if (dtd instanceof Class) {
            if (dtd.equals(unit.getIntegerDeclaration())) {
                return new String[] { "0", "1", "2" };
            }
            if (dtd.equals(unit.getByteDeclaration())) {
                return new String[] { "0.byte", "1.byte" };
            }
            else if (dtd.equals(unit.getFloatDeclaration())) {
                return new String[] { "0.0", "1.0", "2.0" };
            }
            else if (dtd.equals(unit.getStringDeclaration())) {
                return new String[] { "\"\"" };
            }
            else if (dtd.equals(unit.getCharacterDeclaration())) {
                return new String[] { "' '", "'\\n'", "'\\t'" };
            }
            else {
                return new String[0];
            }
        }
        else if (dtd instanceof Interface) {
            if (dtd.equals(unit.getIterableDeclaration())) {
                return new String[] { "{}" };
            }
            else if (dtd.equals(unit.getSequentialDeclaration()) ||
                dtd.equals(unit.getEmptyDeclaration())) {
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

}
