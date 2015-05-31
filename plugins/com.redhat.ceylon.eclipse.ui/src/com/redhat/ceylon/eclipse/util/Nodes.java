package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.EOF;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LINE_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_END;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.UIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static com.redhat.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isOverloadedVersion;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
import com.redhat.ceylon.compiler.typechecker.tree.CustomTree;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.DocLink;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.model.loader.AbstractModelLoader;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.Parameter;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Scope;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;

public class Nodes {

    private static final String[] NO_STRINGS = new String[0];
    private static final Pattern IDPATTERN = 
            Pattern.compile("(^|[A-Z])([A-Z]*)([_a-z]+)");
    

    public static Tree.Declaration findDeclaration(Tree.CompilationUnit cu, Node node) {
        FindDeclarationVisitor fcv = 
                new FindDeclarationVisitor(node);
        fcv.visit(cu);
        return fcv.getDeclarationNode();
    }

    public static Tree.Declaration findDeclarationWithBody(Tree.CompilationUnit cu, Node node) {
        FindBodyContainerVisitor fcv = 
                new FindBodyContainerVisitor(node);
        fcv.visit(cu);
        return fcv.getDeclarationNode();
    }

    public static Tree.NamedArgument findArgument(Tree.CompilationUnit cu, Node node) {
        FindArgumentVisitor fcv = 
                new FindArgumentVisitor(node);
        fcv.visit(cu);
        return fcv.getArgumentNode();
    }

    public static Tree.BinaryOperatorExpression findBinaryOperator(Tree.CompilationUnit cu, 
            final Node node) {
        class FindBinaryVisitor extends Visitor {
            Tree.BinaryOperatorExpression result;
            @Override
            public void visit(Tree.BinaryOperatorExpression that) {
                if (node.getStartIndex()>=that.getStartIndex() && 
                        node.getStopIndex()<=that.getStopIndex()) {
                    result = that;
                }
                super.visit(that);
            }
        }
        FindBinaryVisitor fcv = new FindBinaryVisitor();
        fcv.visit(cu);
        return fcv.result;
    }

    public static Statement findStatement(Tree.CompilationUnit cu, Node node) {
        FindStatementVisitor visitor = 
                new FindStatementVisitor(node, false);
        cu.visit(visitor);
        return visitor.getStatement();
    }

    public static Statement findToplevelStatement(Tree.CompilationUnit cu, Node node) {
        FindStatementVisitor visitor = 
                new FindStatementVisitor(node, true);
        cu.visit(visitor);
        return visitor.getStatement();
    }

    public static Declaration getAbstraction(Declaration d) {
        if (isOverloadedVersion(d)) {
            return d.getContainer()
                    .getDirectMember(d.getName(), null, false);
        }
        else {
            return d;
        }
    }

    public static Tree.Declaration getContainer(Tree.CompilationUnit cu,
            final Declaration dec) {
        class FindContainer extends Visitor {
            final Scope container = dec.getContainer();
            Tree.Declaration result;
            @Override
            public void visit(Tree.Declaration that) {
                super.visit(that);
                if (that.getDeclarationModel().equals(container)) {
                    result = that;
                }
            }
        }
        FindContainer fc = new FindContainer();
        cu.visit(fc);
        return fc.result;
    }

    public static Tree.ImportMemberOrType findImport(Tree.CompilationUnit cu, Node node) {
        
        if (node instanceof Tree.ImportMemberOrType) {
            return (Tree.ImportMemberOrType) node;
        }
        
        final Declaration declaration;
        if (node instanceof Tree.MemberOrTypeExpression) {
            Tree.MemberOrTypeExpression mte = 
                    (Tree.MemberOrTypeExpression) node;
            declaration = mte.getDeclaration();
        }
        else if (node instanceof Tree.SimpleType) {
            Tree.SimpleType st = (Tree.SimpleType) node;
            declaration = st.getDeclarationModel();
        }
        else if (node instanceof Tree.MemberLiteral) {
            Tree.MemberLiteral ml = (Tree.MemberLiteral) node;
            declaration = ml.getDeclaration();
        }
        else {
            return null;
        }
        
        class FindImportVisitor extends Visitor {
            Tree.ImportMemberOrType result;
            @Override
            public void visit(Tree.Declaration that) {}
            @Override
            public void visit(Tree.ImportMemberOrType that) {
                super.visit(that);
                Declaration dec = that.getDeclarationModel();
                if (dec!=null && dec.equals(declaration)) {
                    result = that;
                }
            }
        }
        
        if (declaration!=null) {
            FindImportVisitor visitor = new FindImportVisitor();
            visitor.visit(cu);
            return visitor.result;
        }
        else {
            return null;
        }
    
    }

    public static Node findNode(Tree.CompilationUnit cu, int offset) {
        return findNode(cu, offset, offset+1);
    }

    public static Node findNode(Node node, 
            int startOffset, int endOffset) {
        FindNodeVisitor visitor = 
                new FindNodeVisitor(startOffset, endOffset);
        node.visit(visitor);
        return visitor.getNode();
    }

    private static Node findScope(Tree.CompilationUnit cu, 
            int startOffset, int endOffset) {
        FindScopeVisitor visitor = 
                new FindScopeVisitor(startOffset, endOffset);
        cu.visit(visitor);
        return visitor.getNode();
    }

    public static Node findNode(Tree.CompilationUnit cu, 
            ITextSelection s) {
        return findNode(cu, 
                s.getOffset(), 
                s.getOffset()+s.getLength());
    }

    public static Node findScope(Tree.CompilationUnit cu, 
            ITextSelection s) {
        return findScope(cu, 
                s.getOffset(), 
                s.getOffset()+s.getLength());
    }
    
    public static int getIdentifyingStartOffset(Node node) {
        return getNodeStartOffset(getIdentifyingNode(node));
    }

    public static int getIdentifyingEndOffset(Node node) {
        return getNodeEndOffset(getIdentifyingNode(node));
    }

    public static int getIdentifyingLength(Node node) {
        return getIdentifyingEndOffset(node) - 
                getIdentifyingStartOffset(node);
    }

    public static int getNodeLength(Node node) {
        return getNodeEndOffset(node) - 
               getNodeStartOffset(node);
    }

    public static Node getIdentifyingNode(Node node) {
        if (node instanceof Tree.Declaration) {
            Tree.Identifier identifier = 
                    ((Tree.Declaration) node).getIdentifier();
            if (identifier==null && 
                    !(node instanceof Tree.MissingDeclaration)) {
                //TODO: whoah! this is really ugly!
                CommonToken fakeToken = 
                        new CommonToken(node.getMainToken());
                identifier = new Tree.Identifier(fakeToken); 
            }
            return identifier;
        }
        else if (node instanceof Tree.ModuleDescriptor) {
            return ((Tree.ModuleDescriptor) node).getImportPath();
        }
        else if (node instanceof Tree.PackageDescriptor) {
            return ((Tree.PackageDescriptor) node).getImportPath();
        }
        else if (node instanceof Tree.Import) {
            return ((Tree.Import) node).getImportPath();
        }
        else if (node instanceof Tree.ImportModule) {
            return ((Tree.ImportModule) node).getImportPath();
        }
        else if (node instanceof Tree.NamedArgument) {
            Tree.Identifier id = 
                    ((Tree.NamedArgument) node).getIdentifier();
            if (id==null || id.getToken()==null) {
                return node;
            }
            else {
                return id;
            }
        }
        else if (node instanceof Tree.StaticMemberOrTypeExpression) {
            return ((Tree.StaticMemberOrTypeExpression) node).getIdentifier();
        }
        else if (node instanceof Tree.ExtendedTypeExpression) {
            //TODO: whoah! this is really ugly!
            return ((CustomTree.ExtendedTypeExpression) node).getType()
                            .getIdentifier();
        }
        else if (node instanceof Tree.SimpleType) {
            return ((Tree.SimpleType) node).getIdentifier();
        }
        else if (node instanceof Tree.ImportMemberOrType) {
            return ((Tree.ImportMemberOrType) node).getIdentifier();
        }
        else if (node instanceof Tree.InitializerParameter) {
            return ((Tree.InitializerParameter) node).getIdentifier();
        }
        else if (node instanceof Tree.MemberLiteral) {
            return ((Tree.MemberLiteral) node).getIdentifier();
        }
        else if (node instanceof Tree.TypeLiteral) {
            return getIdentifyingNode(((Tree.TypeLiteral) node).getType());
        }
        //TODO: this would be better for navigation to refinements
        //      so I guess we should split this method into two
        //      versions :-/
        /*else if (node instanceof Tree.SpecifierStatement) {
            Tree.SpecifierStatement st = (Tree.SpecifierStatement) node;
            if (st.getRefinement()) {
                Tree.Term lhs = st.getBaseMemberExpression();
                while (lhs instanceof Tree.ParameterizedExpression) {
                    lhs = ((Tree.ParameterizedExpression) lhs).getPrimary();
                }
                if (lhs instanceof Tree.StaticMemberOrTypeExpression) {
                    return ((Tree.StaticMemberOrTypeExpression) lhs).getIdentifier();
                }
            }
            return node;
        }*/
        else {    
            return node;
        }
    }

    public static Iterator<CommonToken> getTokenIterator(List<CommonToken> tokens, 
            IRegion region) {
        int regionOffset = region.getOffset();
        int regionLength = region.getLength();
        if (regionLength<=0) {
            return Collections.<CommonToken>emptyList().iterator();
        }
        int regionEnd = regionOffset + regionLength - 1;
        if (tokens==null) {
            return null;
        }
        else {
            int firstTokIdx = 
                    getTokenIndexAtCharacter(tokens, regionOffset);
            // getTokenIndexAtCharacter() answers the negative of the index of the
            // preceding token if the given offset is not actually within a token.
            if (firstTokIdx < 0) {
                firstTokIdx= -firstTokIdx + 1;
            }
            int lastTokIdx = 
                    getTokenIndexAtCharacter(tokens, regionEnd);
            if (lastTokIdx < 0) {
                lastTokIdx= -lastTokIdx;
            }
            return tokens.subList(firstTokIdx, lastTokIdx+1).iterator();
        }
    }

    //
    // This function returns the index of the token element
    // containing the offset specified. If such a token does
    // not exist, it returns the negation of the index of the 
    // element immediately preceding the offset.
    //
    public static int getTokenIndexAtCharacter(List<CommonToken> tokens, int offset) {
        //search using bisection
        int low = 0,
                high = tokens.size();
        while (high > low)
        {
            int mid = (high + low) / 2;
            CommonToken midElement = (CommonToken) tokens.get(mid);
            if (offset >= midElement.getStartIndex() &&
                    offset <= midElement.getStopIndex())
                return mid;
            else if (offset < midElement.getStartIndex())
                high = mid;
            else low = mid + 1;
        }
        
        return -(low - 1);
    }

    public static int getNodeStartOffset(Node node) {
        if (node==null) {
            return 0;
        }
        else {
            Integer index = node.getStartIndex();
            return index==null?0:index;
        }
    }

    public static int getNodeEndOffset(Node node) {
        if (node==null) {
            return 0;
        }
        else {
            Integer index = node.getStopIndex();
            return index==null?0:index+1;
        }
    }
    
    /** 
     * Get the Node referenced by the given Node, searching
     * in all relevant compilation units.
     */
    public static Node getReferencedNode(Node node) {
        return getReferencedNode(getReferencedModel(node));
    }

    /** 
     * Get the Node referenced by the given model, searching
     * in all relevant compilation units.
     */
    public static Node getReferencedNode(Referenceable model) {
        if (model==null) {
            return null;
        }
        else {
            Unit unit = model.getUnit();
            if (unit instanceof CeylonUnit) {
                CeylonUnit ceylonUnit = (CeylonUnit) unit;
                return getReferencedNodeInUnit(model, 
                        ceylonUnit.getCompilationUnit());
            }
            else {
                return null;
            }
        }
    }

    public static Referenceable getReferencedModel(Node node) {
        if (node instanceof Tree.ImportPath) {
            Tree.ImportPath importPath = (Tree.ImportPath) node;
            return importPath.getModel();
        }
        else if (node instanceof Tree.DocLink) {
            Tree.DocLink docLink = (Tree.DocLink) node;
            if (docLink.getBase()==null) {
                if (docLink.getModule()!=null) {
                    return docLink.getModule(); 
                }
                if (docLink.getPkg()!=null) {
                    return docLink.getPkg();
                }
            }
        }
        Referenceable dec = getReferencedDeclaration(node);
        if (dec instanceof FunctionOrValue) {
            FunctionOrValue mv = (FunctionOrValue) dec;
            if (mv.isShortcutRefinement()) {
                dec = mv.getRefinedDeclaration();
            }
        }
        return dec;
    }

    public static Referenceable getReferencedExplicitDeclaration(Node node, 
            Tree.CompilationUnit rn) {
        Referenceable dec = getReferencedDeclaration(node);
        if (dec!=null && dec.getUnit()!=null &&
                dec.getUnit().equals(node.getUnit())) {
            FindDeclarationNodeVisitor fdv = 
                    new FindDeclarationNodeVisitor(dec);
            fdv.visit(rn);
            Node decNode = fdv.getDeclarationNode();
            if (decNode instanceof Tree.Variable) {
                Tree.Variable var = (Tree.Variable) decNode;
                if (var.getType() instanceof Tree.SyntheticVariable) {
                    Tree.Term term = var.getSpecifierExpression()
                            .getExpression().getTerm();
                    return getReferencedExplicitDeclaration(term, rn);
                }
            }
        }
        return dec;
    }

    public static Referenceable getReferencedDeclaration(Node node) {
        //NOTE: this must accept a null node, returning null!
        if (node instanceof Tree.MemberOrTypeExpression) {
            return ((Tree.MemberOrTypeExpression) node).getDeclaration();
        } 
        else if (node instanceof Tree.SimpleType) {
            return ((Tree.SimpleType) node).getDeclarationModel();
        } 
        else if (node instanceof Tree.ImportMemberOrType) {
            return ((Tree.ImportMemberOrType) node).getDeclarationModel();
        } 
        else if (node instanceof Tree.Declaration) {
            return ((Tree.Declaration) node).getDeclarationModel();
        } 
        else if (node instanceof Tree.NamedArgument) {
            Parameter p = ((Tree.NamedArgument) node).getParameter();
            return p==null ? null : p.getModel();
        }
        else if (node instanceof Tree.InitializerParameter) {
            Parameter p = ((Tree.InitializerParameter) node).getParameterModel();
            return  p==null ? null : p.getModel();
        }
        else if (node instanceof Tree.MetaLiteral) {
            return ((Tree.MetaLiteral) node).getDeclaration();
        }
        else if (node instanceof Tree.SelfExpression) {
            return ((Tree.SelfExpression) node).getDeclarationModel();
        }
        else if (node instanceof Tree.Outer) {
            return ((Tree.Outer) node).getDeclarationModel();
        }
        else if (node instanceof Tree.Return) {
            return ((Tree.Return) node).getDeclaration();
        }
        else if (node instanceof Tree.DocLink) {
            DocLink docLink = (Tree.DocLink) node;
            List<Declaration> qualified = docLink.getQualified();
            if (qualified!=null && !qualified.isEmpty()) {
                return qualified.get(qualified.size()-1);
            }
            else {
                return docLink.getBase();
            }
        }
        else if (node instanceof Tree.ImportPath) {
            return ((Tree.ImportPath) node).getModel();
        }
        else {
            return null;
        }
    }
    
    /**
     * Find the Node defining the given model within the
     * given CompilationUnit.
     */
    public static Node getReferencedNodeInUnit(Referenceable model,
            Tree.CompilationUnit rootNode) {
        if (rootNode==null || model==null) {
            return null;
        }
        else {
            if (model instanceof Declaration) {
                Declaration decl = (Declaration) model;
                Unit unit = decl.getUnit();
                if (unit != null 
                        && ! unit.getFilename().toLowerCase().endsWith(".ceylon")) {
                    boolean foundTheCeylonDeclaration = false;
                    if (unit instanceof CeylonBinaryUnit) {
                        JDTModule module = ((JDTModule)unit.getPackage().getModule());
                        String sourceRelativePath = module.toSourceUnitRelativePath(unit.getRelativePath());
                        if (sourceRelativePath != null) {
                            String ceylonSourceRelativePath = module.getCeylonDeclarationFile(sourceRelativePath);
                            if (ceylonSourceRelativePath != null) {
                                ExternalPhasedUnit externalPhasedUnit = module.getPhasedUnitFromRelativePath(ceylonSourceRelativePath);
                                if (externalPhasedUnit != null) {
                                    ExternalSourceFile sourceFile = externalPhasedUnit.getUnit();
                                    if (sourceFile != null) {
                                        for (Declaration sourceDecl : sourceFile.getDeclarations()) {
                                            if (sourceDecl.getQualifiedNameString().equals(decl.getQualifiedNameString())) {
                                                model = sourceDecl;
                                                foundTheCeylonDeclaration = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (! foundTheCeylonDeclaration) {
                        if (decl.isNative() 
                                && unit != null 
                                && ! unit.getFilename().toLowerCase().endsWith(".ceylon")) {
                            List<Declaration> overloads = AbstractModelLoader.getOverloads((Declaration)model);
                            for (Declaration overload : overloads) {
                                if (Backend.None.nativeAnnotation.equals(overload.getNative())) {
                                    model = overload;
                                    foundTheCeylonDeclaration = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            FindReferencedNodeVisitor visitor = 
                    new FindReferencedNodeVisitor(model);
            rootNode.visit(visitor);
            return visitor.getDeclarationNode();
        }
    }
    
    public static String toString(Node term, List<CommonToken> tokens) {
        Integer start = term.getStartIndex();
        int length = term.getStopIndex()-start+1;
        Region region = new Region(start, length);
        StringBuilder exp = new StringBuilder();
        for (Iterator<CommonToken> ti = 
                getTokenIterator(tokens, region); 
                ti.hasNext();) {
            CommonToken token = ti.next();
            int type = token.getType();
            String text = token.getText();
            if (type==LIDENTIFIER &&
                    getTokenLength(token)>text.length()) {
                exp.append("\\i");
            }
            else if (type==UIDENTIFIER &&
                    getTokenLength(token)>text.length()) {
                exp.append("\\I"); 
            }
            exp.append(text);
        }
        return exp.toString();
    }

    public static int getTokenLength(CommonToken token) {
        return token.getStopIndex()-token.getStartIndex()+1;
    }

    public static String[] nameProposals(Node node) {
        return nameProposals(node, false);
    }
    public static String[] nameProposals(Node node, boolean unplural) {
    	if (node instanceof Tree.FunctionArgument) {
    		Tree.FunctionArgument fa = (Tree.FunctionArgument) node;
			if (fa.getExpression()!=null) {
    			node = fa.getExpression();
			}
    	}
        Set<String> names = new LinkedHashSet<String>();
        Node identifyingNode = node;
        if (identifyingNode instanceof Tree.Expression) {
            identifyingNode = 
                    ((Tree.Expression) identifyingNode).getTerm();
        }
        if (identifyingNode instanceof Tree.InvocationExpression) {
            identifyingNode = 
                    ((Tree.InvocationExpression) identifyingNode)
                            .getPrimary();
        }
        
        if (identifyingNode instanceof Tree.QualifiedMemberOrTypeExpression) {
            Tree.QualifiedMemberOrTypeExpression qmte = 
                    (Tree.QualifiedMemberOrTypeExpression) identifyingNode;
            Declaration declaration = qmte.getDeclaration();
            if (declaration!=null) {
                addNameProposals(names, false, declaration.getName());
                //TODO: propose a compound name like personName for person.name
            }
        }
        if (identifyingNode instanceof Tree.FunctionType) {
            Tree.StaticType type = 
                    ((Tree.FunctionType) identifyingNode).getReturnType();
            if (type instanceof Tree.SimpleType) {
                String name = ((Tree.SimpleType) type).getDeclarationModel().getName();
                addNameProposals(names, false, name);
            }
        }
        if (identifyingNode instanceof Tree.BaseMemberOrTypeExpression) {
            BaseMemberOrTypeExpression bmte = 
                    (Tree.BaseMemberOrTypeExpression) identifyingNode;
            Declaration declaration = bmte.getDeclaration();
            if (unplural) {
                String name = declaration.getName();
                if (name.endsWith("s") && name.length()>1) {
                    addNameProposals(names, false, name.substring(0, name.length()-1));
                }
            }
        }
        if (identifyingNode instanceof Tree.SumOp) {
            names.add("sum");
        }
        else if (identifyingNode instanceof Tree.DifferenceOp) {
            names.add("difference");
        }
        else if (identifyingNode instanceof Tree.ProductOp) {
            names.add("product");
        }
        else if (identifyingNode instanceof Tree.QuotientOp) {
            names.add("ratio");
        }
        else if (identifyingNode instanceof Tree.RemainderOp) {
            names.add("remainder");
        }
        else if (identifyingNode instanceof Tree.UnionOp) {
            names.add("union");
        }
        else if (identifyingNode instanceof Tree.IntersectionOp) {
            names.add("intersection");
        }
        else if (identifyingNode instanceof Tree.ComplementOp) {
            names.add("complement");
        }
        else if (identifyingNode instanceof Tree.RangeOp) {
            names.add("range");
        }
        else  if (identifyingNode instanceof Tree.EntryOp) {
            names.add("entry");
        }

        if (identifyingNode instanceof Tree.Term) {
            Tree.Term term = (Tree.Term) node;
            Type type = term.getTypeModel();
            if (!isTypeUnknown(type)) {
                if (!unplural) {
                    if (type.isClassOrInterface() || 
                        type.isTypeParameter()) {
                        addNameProposals(names, false, 
                                type.getDeclaration()
                                    .getName());
                    }
                }
                Unit unit = node.getUnit();
                if (unit.isIterableType(type)) {
                    Type iteratedType = 
                            unit.getIteratedType(type);
                    if (iteratedType!=null) {
                        if (iteratedType.isClassOrInterface() || 
                            iteratedType.isTypeParameter()) {
                            addNameProposals(names, 
                                    true && !unplural, 
                                    iteratedType.getDeclaration()
                                        .getName());
                        }
                    }
                }
            }
        }
        if (names.isEmpty()) {
            names.add("it");
        }
        return names.toArray(NO_STRINGS);
    }

    public static void addNameProposals(Set<String> names, 
            boolean plural, String tn) {
        String name = Escaping.toInitialLowercase(tn);
        Matcher matcher = IDPATTERN.matcher(name);
        while (matcher.find()) {
            int loc = matcher.start(2);
            String initial = name.substring(matcher.start(1), loc);
            if (Character.isLowerCase(name.codePointAt(0))) {
                initial = initial.toLowerCase();
            }
            String subname = initial + name.substring(loc);
            if (plural) subname += "s";
            if (Escaping.KEYWORDS.contains(subname)) {
                names.add("\\i" + subname);
            }
            else {
                names.add(subname);
            }
        }
    }

    public static Tree.SpecifierOrInitializerExpression getDefaultArgSpecifier(
            Tree.Parameter p) {
        if (p instanceof Tree.ValueParameterDeclaration) {
            Tree.AttributeDeclaration pd = (Tree.AttributeDeclaration)
                    ((Tree.ValueParameterDeclaration) p).getTypedDeclaration();
            return pd.getSpecifierOrInitializerExpression();
        }
        else if (p instanceof Tree.FunctionalParameterDeclaration) {
            Tree.MethodDeclaration pd = (Tree.MethodDeclaration)
                    ((Tree.FunctionalParameterDeclaration) p).getTypedDeclaration();
            return pd.getSpecifierExpression();
        }
        else if (p instanceof Tree.InitializerParameter) {
            return ((Tree.InitializerParameter) p).getSpecifierExpression();
        }
        else {
            return null;
        }
    }

    public static CommonToken getTokenStrictlyContainingOffset(int offset,
            List<CommonToken> tokens) {
        if (tokens!=null) {
            if (tokens.size()>1) {
                if (tokens.get(tokens.size()-1).getStartIndex()==offset) { //at very end of file
                    //check to see if last token is an
                    //unterminated string or comment
                    //Note: ANTLR sometimes sends me 2 EOFs, 
                    //      so do this:
                    CommonToken token = null;
                    for (int i=1;
                            tokens.size()>=i &&
                            (token==null || token.getType()==EOF); 
                            i++) {
                        token = tokens.get(tokens.size()-i);
                    }
                    int type = token==null ? -1 : token.getType();
                    if ((type==STRING_LITERAL ||
                            type==STRING_END ||
                            type==ASTRING_LITERAL) && 
                            (!token.getText().endsWith("\"") ||
                            token.getText().length()==1) ||
                            (type==VERBATIM_STRING || type==AVERBATIM_STRING) && 
                            (!token.getText().endsWith("\"\"\"")||
                            token.getText().length()==3) ||
                            (type==MULTI_COMMENT) && 
                            (!token.getText().endsWith("*/")||
                            token.getText().length()==2) ||
                            type==LINE_COMMENT) {
                        return token;
                    }
                }
                else {
                    int tokenIndex = 
                            getTokenIndexAtCharacter(tokens, offset);
                    if (tokenIndex>=0) {
                        CommonToken token = tokens.get(tokenIndex);
                        if (token.getStartIndex()<offset) {
                            return token;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void appendParameters(StringBuilder result,
            Tree.FunctionArgument fa, Unit unit, 
            List<CommonToken> tokens) {
        for (Tree.ParameterList pl: fa.getParameterLists()) {
            result.append('(');
            boolean first=true;
            for (Tree.Parameter p: pl.getParameters()) {
                if (first) {
                    first=false;
                }
                else {
                    result.append(", ");
                }
                if (p instanceof Tree.InitializerParameter) {
                    Type type = 
                            p.getParameterModel().getType();
                    if (!isTypeUnknown(type)) {
                        result.append(type.asSourceCodeString(unit))
                              .append(" ");
                    }
                }
                result.append(toString(p, tokens));
            }
            result.append(')');
        }
    }

    public static OccurrenceLocation getOccurrenceLocation(Tree.CompilationUnit cu, 
            Node node, int offset) {
        FindOccurrenceLocationVisitor visitor = 
                new FindOccurrenceLocationVisitor(offset, node);
        cu.visit(visitor);
        return visitor.getOccurrenceLocation();
    }

    public static String getImportedName(Tree.ImportModule im) {
        Tree.ImportPath ip = im.getImportPath();
        Tree.QuotedLiteral ql = im.getQuotedLiteral();
        if (ip!=null) {
            return formatPath(ip.getIdentifiers());
        }
        else if (ql!=null) {
            return ql.getText();
        }
        else {
            return null;
        }
    }

    public static String getImportedName(Tree.Import i) {
        Tree.ImportPath ip = i.getImportPath();
        if (ip!=null) {
            return formatPath(ip.getIdentifiers());
        }
        else {
            return null;
        }
    }

}
