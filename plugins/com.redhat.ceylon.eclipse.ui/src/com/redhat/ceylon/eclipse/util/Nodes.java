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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Region;

import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.eclipse.core.model.CeylonBinaryUnit;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.ide.common.util.NodePrinter;
import com.redhat.ceylon.ide.common.util.nodes_;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.FunctionOrValue;
import com.redhat.ceylon.model.typechecker.model.ModelUtil;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class Nodes {

    private static final nodes_ delegate = nodes_.get_();
    
    public static Tree.Declaration findDeclaration(Tree.CompilationUnit cu, Node node) {
        return delegate.findDeclaration(cu, node);
    }

    public static Tree.Declaration findDeclarationWithBody(Tree.CompilationUnit cu, Node node) {
        return delegate.findDeclarationWithBody(cu, node);
    }

    public static Tree.NamedArgument findArgument(Tree.CompilationUnit cu, Node node) {
        return delegate.findArgument(cu, node);
    }

    public static Tree.OperatorExpression findOperator(Tree.CompilationUnit cu, 
            final Node node) {
        return delegate.findOperator(cu, node);
    }

    public static Statement findStatement(Tree.CompilationUnit cu, Node node) {
        return delegate.findStatement(cu, node);
    }

    public static Statement findToplevelStatement(Tree.CompilationUnit cu, Node node) {
        return delegate.findTopLevelStatement(cu, node);
    }

    public static Declaration getAbstraction(Declaration d) {
        return delegate.getAbstraction(d);
    }

    public static Tree.Declaration getContainer(Tree.CompilationUnit cu,
            final Declaration dec) {
        return delegate.getContainer(cu, dec);
    }

    public static Tree.ImportMemberOrType findImport(Tree.CompilationUnit cu, Node node) {
        return delegate.findImport(cu, node);
    }

    public static Node findNode(Tree.CompilationUnit cu, int offset) {
        return findNode(cu, null, offset, offset+1);
    }

    public static Node findNode(Tree.CompilationUnit cu, 
            int startOffset, int endOffset) {
        return findNode(cu, null, startOffset, endOffset);
    }

    public static Node findNode(Node node, 
            List<CommonToken> tokens, 
            int startOffset, int endOffset) {
        return delegate.findNode(node, tokens, startOffset, endOffset);
    }

    private static Node findScope(Tree.CompilationUnit cu, 
            int startOffset, int endOffset) {
        return delegate.findScope(cu, startOffset, endOffset);
    }

    public static Node findNode(Tree.CompilationUnit cu, 
            List<CommonToken> tokens, 
            ITextSelection selection) {
        return findNode(cu, 
                tokens, 
                selection.getOffset(), 
                selection.getOffset() + selection.getLength());
    }

    public static Node findNode(Tree.CompilationUnit cu, 
            List<CommonToken> tokens, 
            IRegion selection) {
        return findNode(cu, 
                tokens, 
                selection.getOffset(), 
                selection.getOffset() + selection.getLength());
    }

    public static Node findScope(Tree.CompilationUnit cu, 
            ITextSelection s) {
        return findScope(cu, 
                s.getOffset(), 
                s.getOffset()+s.getLength());
    }
    
    public static Node getIdentifyingNode(Node node) {
        return delegate.getIdentifyingNode(node);
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
        return delegate.getReferencedExplicitDeclaration(node, rn);
    }

    public static Referenceable getReferencedDeclaration(Node node) {
        return delegate.getReferencedDeclaration(node);
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
                        JDTModule module = (JDTModule) unit.getPackage().getModule();
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
                            Declaration headerDeclaration = ModelUtil.getNativeHeader(decl.getContainer(), decl.getName());
                            if (headerDeclaration != null) {
                                List<Declaration> overloads = headerDeclaration.getOverloads();
                                if (overloads != null) {
                                    for (Declaration overload : overloads) {
                                        if (Backend.None.nativeAnnotation.equals(overload.getNativeBackend())) {
                                            model = overload;
                                            foundTheCeylonDeclaration = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return findReferencedNode(rootNode, model);
        }
    }

    public static Node findReferencedNode(
            Tree.CompilationUnit rootNode, Referenceable model) {
        FindReferencedNodeVisitor visitor = 
                new FindReferencedNodeVisitor(model);
        rootNode.visit(visitor);
        return visitor.getDeclarationNode();
    }
    
    public static String toString(Node term, List<CommonToken> tokens) {
        int start = term.getStartIndex();
        int length = term.getDistance();
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
    	return delegate.nameProposals(node, unplural);
    }

    public static void addNameProposals(Set<String> names, 
            boolean plural, String tn) {
        delegate.addNameProposals(names, plural, tn);
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
            final List<CommonToken> tokens) {

        delegate.appendParameters(result, fa, unit, new NodePrinter() {
            @Override
            public String $toString(Node node) {
                return Nodes.toString(node, tokens);
            }
        });
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
