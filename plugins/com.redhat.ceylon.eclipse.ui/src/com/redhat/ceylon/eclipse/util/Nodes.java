package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.EOF;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LINE_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_END;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static com.redhat.ceylon.eclipse.util.CeylonHelper.toJavaStringArray;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.antlr.runtime.CommonToken;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.Statement;
import com.redhat.ceylon.ide.common.refactoring.DefaultRegion;
import com.redhat.ceylon.ide.common.util.OccurrenceLocation;
import com.redhat.ceylon.ide.common.util.nodes_;
import com.redhat.ceylon.model.typechecker.model.Declaration;
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
//        int regionOffset = region.getOffset();
//        int regionLength = region.getLength();
//        if (regionLength<=0) {
//            return Collections.<CommonToken>emptyList().iterator();
//        }
//        int regionEnd = regionOffset + regionLength - 1;
//        if (tokens==null) {
//            return null;
//        }
//        else {
//            int firstTokIdx = 
//                    getTokenIndexAtCharacter(tokens, regionOffset);
//            // getTokenIndexAtCharacter() answers the negative of the index of the
//            // preceding token if the given offset is not actually within a token.
//            if (firstTokIdx < 0) {
//                firstTokIdx= -firstTokIdx + 1;
//            }
//            int lastTokIdx = 
//                    getTokenIndexAtCharacter(tokens, regionEnd);
//            if (lastTokIdx < 0) {
//                lastTokIdx= -lastTokIdx;
//            }
//            return tokens.subList(firstTokIdx, lastTokIdx+1).iterator();
//        }
        return delegate.getTokenIterator(tokens, 
                new DefaultRegion(region.getOffset(), region.getLength()));
    }

    //
    // This function returns the index of the token element
    // containing the offset specified. If such a token does
    // not exist, it returns the negation of the index of the 
    // element immediately preceding the offset.
    //
    public static int getTokenIndexAtCharacter(List<CommonToken> tokens, int offset) {
        //search using bisection
//        int low = 0,
//                high = tokens.size();
//        while (high > low)
//        {
//            int mid = (high + low) / 2;
//            CommonToken midElement = (CommonToken) tokens.get(mid);
//            if (offset >= midElement.getStartIndex() &&
//                    offset <= midElement.getStopIndex())
//                return mid;
//            else if (offset < midElement.getStartIndex())
//                high = mid;
//            else low = mid + 1;
//        }
//        
//        return -(low - 1);
        return (int) delegate.getTokenIndexAtCharacter(tokens, offset);
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
        return delegate.getReferencedNode(model); 
    }
    
    /** 
     * Get the Node referenced by the given model, searching
     * in all relevant compilation units.
     */
    public static Node getReferencedNode(Referenceable model, Tree.CompilationUnit rootNode) {
        return delegate.getReferencedNode(model, rootNode);
    }

    public static Referenceable getReferencedModel(Node node) {
        return delegate.getReferencedModel(node);
    }

    public static Referenceable getReferencedExplicitDeclaration(Node node, 
            Tree.CompilationUnit rn) {
        return delegate.getReferencedExplicitDeclaration(node, rn);
    }

    public static Referenceable getReferencedDeclaration(Node node) {
        return delegate.getReferencedDeclaration(node);
    }
    
    public static Node findReferencedNode(
            Tree.CompilationUnit rootNode, Referenceable model) {
        return delegate.findReferencedNode(rootNode, model);
    }
    
    public static String text(Node term, List<CommonToken> tokens) {
//        int start = term.getStartIndex();
//        int length = term.getDistance();
//        Region region = new Region(start, length);
//        StringBuilder exp = new StringBuilder();
//        for (Iterator<CommonToken> ti = 
//                getTokenIterator(tokens, region); 
//                ti.hasNext();) {
//            CommonToken token = ti.next();
//            int type = token.getType();
//            String text = token.getText();
//            if (type==LIDENTIFIER &&
//                    getTokenLength(token)>text.length()) {
//                exp.append("\\i");
//            }
//            else if (type==UIDENTIFIER &&
//                    getTokenLength(token)>text.length()) {
//                exp.append("\\I"); 
//            }
//            exp.append(text);
//        }
//        return exp.toString();
        return delegate.text(tokens, term);
    }

    public static int getTokenLength(CommonToken token) {
//        return token.getStopIndex()-token.getStartIndex()+1;
        return (int) delegate.getTokenLength(token);
    }

    public static String[] nameProposals(Node node) {
        if (node instanceof Tree.Term || node instanceof Tree.Type) {
            return nameProposals(node, false);
        }
        else {
            return new String[] {"it"};
        }
    }
    public static String[] nameProposals(Node node, boolean unplural) {
    	return toJavaStringArray(delegate.nameProposals(node, null, unplural));
    }

    public static void addNameProposals(Set<String> names, 
            boolean plural, String tn) {
        delegate.addNameProposals(names, plural, tn);
    }

    public static Tree.SpecifierOrInitializerExpression getDefaultArgSpecifier(
            Tree.Parameter p) {
//        if (p instanceof Tree.ValueParameterDeclaration) {
//            Tree.AttributeDeclaration pd = (Tree.AttributeDeclaration)
//                    ((Tree.ValueParameterDeclaration) p).getTypedDeclaration();
//            return pd.getSpecifierOrInitializerExpression();
//        }
//        else if (p instanceof Tree.FunctionalParameterDeclaration) {
//            Tree.MethodDeclaration pd = (Tree.MethodDeclaration)
//                    ((Tree.FunctionalParameterDeclaration) p).getTypedDeclaration();
//            return pd.getSpecifierExpression();
//        }
//        else if (p instanceof Tree.InitializerParameter) {
//            return ((Tree.InitializerParameter) p).getSpecifierExpression();
//        }
//        else {
//            return null;
//        }
        return delegate.getDefaultArgSpecifier(p);
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
        delegate.appendParameters(result, fa, unit, tokens);
    }

    public static OccurrenceLocation getOccurrenceLocation(Tree.CompilationUnit cu, 
            Node node, int offset) {
        return delegate.getOccurrenceLocation(cu, node, offset);
    }

    public static String getImportedName(Tree.ImportModule im) {
        ceylon.language.String name = delegate.getImportedModuleName(im);
		return name==null ? null : name.toString();
    }

    public static String getImportedName(Tree.Import i) {
        ceylon.language.String name = delegate.getImportedPackageName(i);
		return  name==null ? null : name.toString();
    }

}
