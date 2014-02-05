package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.AIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.CHAR_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.EOF;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.FLOAT_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LBRACE;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LINE_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.NATURAL_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.PIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.RBRACE;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.SEMICOLON;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_END;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_MID;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_START;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.UIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.WS;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.keywords;
import static com.redhat.ceylon.eclipse.code.propose.ArgumentListCompletions.addArgumentListProposal;
import static com.redhat.ceylon.eclipse.code.propose.CompletionUtil.getOccurrenceLocation;
import static com.redhat.ceylon.eclipse.code.propose.CompletionUtil.isEmptyModuleDescriptor;
import static com.redhat.ceylon.eclipse.code.propose.CompletionUtil.isEmptyPackageDescriptor;
import static com.redhat.ceylon.eclipse.code.propose.CompletionUtil.isModuleDescriptor;
import static com.redhat.ceylon.eclipse.code.propose.CompletionUtil.isPackageDescriptor;
import static com.redhat.ceylon.eclipse.code.propose.CompletionUtil.overloads;
import static com.redhat.ceylon.eclipse.code.propose.ControlStructureCompletions.addForProposal;
import static com.redhat.ceylon.eclipse.code.propose.ControlStructureCompletions.addIfExistsProposal;
import static com.redhat.ceylon.eclipse.code.propose.ControlStructureCompletions.addSwitchProposal;
import static com.redhat.ceylon.eclipse.code.propose.DocLink.getDocLink;
import static com.redhat.ceylon.eclipse.code.propose.ImportCompletions.addImportProposal;
import static com.redhat.ceylon.eclipse.code.propose.InlineFunctionCompletions.addInlineFunctionProposal;
import static com.redhat.ceylon.eclipse.code.propose.KeywordCompletions.addKeywordProposals;
import static com.redhat.ceylon.eclipse.code.propose.MemberNameCompletions.addMemberNameProposal;
import static com.redhat.ceylon.eclipse.code.propose.MemberNameCompletions.addMemberNameProposals;
import static com.redhat.ceylon.eclipse.code.propose.ModuleCompletions.addModuleCompletions;
import static com.redhat.ceylon.eclipse.code.propose.ModuleCompletions.addModuleDescriptorCompletion;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.CLASS_ALIAS;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.DOCLINK;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.EXPRESSION;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.EXTENDS;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.IMPORT;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.OF;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.PARAMETER_LIST;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.SATISFIES;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.TYPE_ALIAS;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.TYPE_ARGUMENT_LIST;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.TYPE_PARAMETER_LIST;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.UPPER_BOUND;
import static com.redhat.ceylon.eclipse.code.propose.PackageCompletions.addCurrentPackageNameCompletion;
import static com.redhat.ceylon.eclipse.code.propose.PackageCompletions.addPackageCompletions;
import static com.redhat.ceylon.eclipse.code.propose.PackageCompletions.addPackageDescriptorCompletion;
import static com.redhat.ceylon.eclipse.code.propose.ParameterContextInformation.addFakeShowParametersCompletion;
import static com.redhat.ceylon.eclipse.code.propose.ReferenceCompletions.addBasicProposal;
import static com.redhat.ceylon.eclipse.code.propose.ReferenceCompletions.addDocLinkProposal;
import static com.redhat.ceylon.eclipse.code.propose.ReferenceCompletions.addInvocationProposals;
import static com.redhat.ceylon.eclipse.code.propose.ReferenceCompletions.addNamedArgumentProposal;
import static com.redhat.ceylon.eclipse.code.propose.RefinementCompletions.addRefinementProposal;
import static com.redhat.ceylon.eclipse.util.Types.getResultType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.ImportList;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnnotationList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberLiteral;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.RequiredTypeVisitor;

public class CeylonContentProposer {
    
    public ICompletionProposal[] getContentProposals(CeylonParseController cpc,
            final int offset, ITextViewer viewer, boolean filter, 
            boolean returnedParamInfo) {
        
        if (cpc==null || viewer==null || 
                cpc.getRootNode()==null || 
                cpc.getTokens()==null) {
            return null;
        }
        
        cpc.parse(viewer.getDocument(), new NullProgressMonitor(), null);
        cpc.getHandler().updateAnnotations();
        List<CommonToken> tokens = cpc.getTokens(); 
        Tree.CompilationUnit rn = cpc.getRootNode();
        
        //compensate for the fact that we get sent an old
        //tree that doesn't contain the characters the user
        //just typed
        PositionedPrefix result = compensateForMissingCharacter(offset, viewer,
                tokens);
        if (result==null) {
            return null;
        }
        
        //adjust the token to account for unclosed blocks
        //we search for the first non-whitespace/non-comment
        //token to the left of the caret
        int tokenIndex = getTokenIndexAtCharacter(tokens, result.start);
        if (tokenIndex<0) tokenIndex = -tokenIndex;
        CommonToken adjustedToken = adjust(tokenIndex, offset, tokens);
        int tt = adjustedToken.getType();
        
        if (offset<=adjustedToken.getStopIndex() && 
            offset>=adjustedToken.getStartIndex()) {
            if (tt==MULTI_COMMENT||tt==LINE_COMMENT) {
                return null;
            }
            if (tt==STRING_LITERAL ||
                tt==STRING_END ||
                tt==STRING_MID ||
                tt==STRING_START ||
                tt==VERBATIM_STRING ||
                tt==CHAR_LITERAL ||
                tt==FLOAT_LITERAL ||
                tt==NATURAL_LITERAL) {
                return null;
            }
        }
        int line=-1;
        try {
            line = viewer.getDocument().getLineOfOffset(offset);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        if (tt==LINE_COMMENT &&
            offset>=adjustedToken.getStartIndex() && 
            adjustedToken.getLine()==line+1) {
            return null;
        }
        
        //find the node at the token
        Node node = getTokenNode(adjustedToken.getStartIndex(), 
                adjustedToken.getStopIndex()+1, 
                tt, rn, offset);
                
        //find the type that is expected in the current
        //location so we can prioritize proposals of that
        //type
        //TODO: this breaks as soon as the user starts typing
        //      an expression, since RequiredTypeVisitor
        //      doesn't know how to search up the tree for
        //      the containing InvocationExpression
        RequiredTypeVisitor rtv = new RequiredTypeVisitor(node, adjustedToken);
        rtv.visit(rn);
        ProducedType requiredType = rtv.getType();
        
        Scope scope = getRealScope(node, rn);
 
        // special handling for doc links
        boolean inDoc = false;
        String qual = ""; //empty for all except
        if ((tt==ASTRING_LITERAL || tt==AVERBATIM_STRING) &&
                offset>adjustedToken.getStartIndex() &&
                offset<adjustedToken.getStopIndex()) {

            inDoc = true;
            int qualMarker = result.prefix.lastIndexOf("::");
            if (qualMarker > -1 && offset > (adjustedToken.getStartIndex() + qualMarker +1) ) {
                qual = result.prefix.substring(0, qualMarker + 2);
                result.prefix = result.prefix.substring(qualMarker + 2);
            }
            if (result.isMemberOp && result.type != null) {
                qual = result.type + ".";
            } 
        }
        
        //construct completions when outside ordinary code
        ICompletionProposal[] completions = constructCompletions(offset, result.prefix, cpc, node, 
        		adjustedToken, scope, returnedParamInfo, result.isMemberOp, viewer.getDocument());
        if (completions==null) {
            //finally, construct and sort proposals
            Map<String, DeclarationWithProximity> proposals = getProposals(node, scope, 
            		result.prefix, result.isMemberOp, rn);
            filterProposals(filter, rn, requiredType, proposals);
            Set<DeclarationWithProximity> sortedProposals = sortProposals(result.prefix, 
            		requiredType, proposals);
            completions = constructCompletions(offset, qual+result.prefix, sortedProposals, cpc,
                             scope, node, adjustedToken, result.isMemberOp, viewer.getDocument(), 
                             filter, inDoc);
        }
        return completions;
        
    }

    private void filterProposals(boolean filter, Tree.CompilationUnit rn,
            ProducedType requiredType, Map<String, DeclarationWithProximity> proposals) {
        if (filter) {
            Iterator<Map.Entry<String, DeclarationWithProximity>> iter = proposals.entrySet().iterator();
            while (iter.hasNext()) {
                Declaration d = iter.next().getValue().getDeclaration();
                ProducedType type = getResultType(d);
                ProducedType fullType = d.getReference().getFullType();
                if (requiredType!=null && (type==null ||
                        (!type.isSubtypeOf(requiredType) && !fullType.isSubtypeOf(requiredType)) || 
                        type.isSubtypeOf(rn.getUnit().getNullDeclaration().getType()))) {
                    iter.remove();
                }
            }
        }
    }

    private static PositionedPrefix compensateForMissingCharacter(final int offset,
            ITextViewer viewer, List<CommonToken> tokens) {

        //What is going on here is that when I have a list of proposals open
        //and then I type a character, IMP sends us the old syntax tree and
        //doesn't bother to even send us the character I just typed, except
        //in the ITextViewer. So we need to do some guessing to figure out
        //that there is a missing character in the token stream and take
        //corrective action. This should be fixed in IMP!
        
        return getPositionedPrefix(offset, viewer, 
                getTokenAtCaret(offset, viewer, tokens));
    }

    private static PositionedPrefix getPositionedPrefix(final int offset,
            ITextViewer viewer, CommonToken token) {
        String text = viewer.getDocument().get();
        if (token==null || offset==0) {
            //no earlier token, so we're typing at the 
            //start of an empty file
            return new PositionedPrefix(text.substring(0, offset), 0);
        }
        
        int offsetInToken = offset-1-token.getStartIndex();
        
        //then we're not missing the typed character 
        //from the tree we were passed by IMP
        if (isIdentifierOrKeyword(token)) {
        	return new PositionedPrefix(
        			token.getText().substring(0, offsetInToken+1),
        			token.getStartIndex());
        }
        else if (isStringLiteral(token)) {
        	DocLink docLink = getDocLink(token.getText(), 
        			offset - token.getStartIndex(), 
        			offsetInToken);
        	if (docLink != null) {
        		// if caret is after '|'
        		if (offsetInToken >= docLink.declStart-1) {
        			return positionedPrefixForDocLink(token, offsetInToken,
        					docLink);
        		}
        	}
        	return null; // in literal but no docRef
        }
        else {
        	return new PositionedPrefix(offset, isMemberOperator(token));
        }
    }

	private static boolean isStringLiteral(CommonToken token) {
	    int type = token.getType();
		return type == ASTRING_LITERAL || 
        		type == AVERBATIM_STRING;
    }

	private static PositionedPrefix positionedPrefixForDocLink(
            CommonToken token, int offsetInToken, DocLink docLink) {
	    // if caret is after the '.' and the '.' is a member lookup
	    int start = docLink.declStart;
		String name = docLink.declName;
		int offset = token.getStartIndex();
		int dotLoc = name.lastIndexOf('.');
		int scopeLoc = name.lastIndexOf("::");
		if ((offsetInToken > start + dotLoc - 1) 
	            && dotLoc > scopeLoc) {
	        PositionedPrefix pp = new PositionedPrefix(offset+start, true);
	        if (start + dotLoc <= offsetInToken) {
	            pp.prefix = name.substring(dotLoc + 1, 
	            		offsetInToken - start +1);
	            pp.type = name.substring(0, dotLoc);
	        }
	        return pp;
	    }
	    else { // type lookup, handle package later
	        return new PositionedPrefix(name, offset+start);
	    }
    }

    private static CommonToken getTokenAtCaret(final int offset, ITextViewer viewer,
            List<CommonToken> tokens) {
        if (offset==0) {
            return null;
        }
        int index = getTokenIndexAtCharacter(tokens, offset-1);
        if (index>=0) {
            return (CommonToken) tokens.get(index);
        }
        return null;
    }
    
    static final class ParameterInfo 
            extends DeclarationCompletionProposal {
		ParameterInfo(int offset, String prefix, String desc,
				String text, boolean selectParams, CeylonParseController cpc,
				Declaration d, boolean addimport,
				ProducedReference producedReference, Scope scope) {
			super(offset, prefix, desc, text, selectParams, cpc, d, addimport,
					producedReference, scope, true);
		}
		@Override
		public Point getSelection(IDocument document) {
			return null;
		}
		@Override
		public void apply(IDocument document) {}
	}

	private static class PositionedPrefix {
        String type;
        String prefix;
        int start;
        boolean isMemberOp;
        PositionedPrefix(String prefix, int start) {
            this.prefix=prefix;
            this.start=start;
            this.isMemberOp=false;
        }
        PositionedPrefix(int start, boolean isMemberOp) {
            this.prefix="";
            this.isMemberOp=isMemberOp;
            this.start=start;
        }
    }
    
    private static CommonToken adjust(int tokenIndex, int offset, List<CommonToken> tokens) {
        CommonToken adjustedToken = tokens.get(tokenIndex); 
        while (--tokenIndex>=0 && 
                (adjustedToken.getType()==WS //ignore whitespace
                || adjustedToken.getType()==EOF
                || adjustedToken.getStartIndex()==offset)) { //don't consider the token to the right of the caret
            adjustedToken = tokens.get(tokenIndex);
            if (adjustedToken.getType()!=WS &&
                    adjustedToken.getType()!=EOF &&
                    adjustedToken.getChannel()!=CommonToken.HIDDEN_CHANNEL) { //don't adjust to a ws token
                break;
            }
        }
        return adjustedToken;
    }
    
    private static Boolean isDirectlyInsideBlock(Node node,
            CeylonParseController cpc, Scope scope,
            CommonToken token) {
        if (scope instanceof Interface || 
                scope instanceof Package) {
            return false;
        }
        else {
            //TODO: check that it is not the opening/closing 
            //      brace of a named argument list!
            return !(node instanceof Tree.SequenceEnumeration) && 
                    occursAfterBraceOrSemicolon(token, cpc.getTokens());
        }
    }

    private static Boolean occursAfterBraceOrSemicolon(CommonToken token,
            List<CommonToken> tokens) {
        if (token.getTokenIndex()==0) {
            return false;
        }
        else {
            int tokenType = token.getType();
            if (tokenType==LBRACE || 
                    tokenType==RBRACE || 
                    tokenType==SEMICOLON) {
                return true;
            }
            int previousTokenType = adjust(token.getTokenIndex()-1, 
                    token.getStartIndex(), tokens).getType();
            return previousTokenType==LBRACE || 
                    previousTokenType==RBRACE || 
                    previousTokenType==SEMICOLON;
        }
    }

    private static Node getTokenNode(int adjustedStart, int adjustedEnd,
            int tokenType, Tree.CompilationUnit rn, int offset) {
        Node node = findNode(rn, adjustedStart, adjustedEnd);
        if (node instanceof Tree.StringLiteral && 
                !((Tree.StringLiteral) node).getDocLinks().isEmpty()) {
            node = findNode(node, offset, offset);
        }
        if (tokenType==RBRACE || tokenType==SEMICOLON) {
            //We are to the right of a } or ;
            //so the returned node is the previous
            //statement/declaration. Look for the
            //containing body.
            class BodyVisitor extends Visitor {
                Node node, currentBody, result;
                BodyVisitor(Node node, Node root) {
                    this.node = node;
                    currentBody = root;
                }
                @Override
                public void visitAny(Node that) {
                    if (that==node) {
                        result = currentBody;
                    }
                    else {
                        Node cb = currentBody;
                        if (that instanceof Tree.Body) {
                            currentBody = that;
                        }
                        if (that instanceof Tree.NamedArgumentList) {
                            currentBody = that;
                        }
                        super.visitAny(that);
                        currentBody = cb;
                    }
                }
            }
            BodyVisitor mv = new BodyVisitor(node, rn);
            mv.visit(rn);
            node = mv.result;
        }
        
        if (node==null) node = rn; //we're in whitespace at the start of the file
        return node;
    }
    
    private static boolean isIdentifierOrKeyword(Token token) {
        int type = token.getType();
        return type==LIDENTIFIER || 
                type==UIDENTIFIER ||
                type==AIDENTIFIER ||
                type==PIDENTIFIER ||
                keywords.contains(token.getText());
    }
    
    private static ICompletionProposal[] constructCompletions(final int offset, final String prefix, 
            final CeylonParseController cpc, final Node node, final CommonToken token, final Scope scope,
            boolean returnedParamInfo, boolean memberOp, final IDocument document) {
        
        final List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
        
        if (!returnedParamInfo &&
        		node instanceof Tree.PositionalArgumentList && 
        		(token.getType()==CeylonLexer.LPAREN ||
        		token.getType()==CeylonLexer.COMMA)) {
        	addFakeShowParametersCompletion(cpc, node, token, result);
        }
        else if (node instanceof Tree.PackageLiteral) {
        	addPackageCompletions(cpc, offset, prefix, null, node, result, false);
        }
        else if (node instanceof Tree.ModuleLiteral) {
        	addModuleCompletions(cpc, offset, prefix, null, node, result, false);
        }
        else if (node instanceof Tree.ModuleDescriptor && 
                (((Tree.ModuleDescriptor) node).getImportPath()==null ||
                ((Tree.ModuleDescriptor) node).getImportPath().getIdentifiers().isEmpty())) {
            addCurrentPackageNameCompletion(cpc, offset, prefix, result);
        }
        else if (node instanceof Tree.PackageDescriptor && 
                (((Tree.PackageDescriptor) node).getImportPath()==null ||
                ((Tree.PackageDescriptor) node).getImportPath().getIdentifiers().isEmpty())) {
            addCurrentPackageNameCompletion(cpc, offset, prefix, result);
        }
        else if (node instanceof Tree.Import && offset>token.getStopIndex()+1) {
        	addPackageCompletions(cpc, offset, prefix, null, node, result, 
            		nextTokenType(cpc, token)!=CeylonLexer.LBRACE);
        }
        else if (node instanceof Tree.ImportModule && offset>token.getStopIndex()+1) {
        	addModuleCompletions(cpc, offset, prefix, null, node, result, 
            		nextTokenType(cpc, token)!=CeylonLexer.STRING_LITERAL);
        }
        else if (node instanceof Tree.ImportPath) {
            new ImportVisitor(prefix, token, offset, node, cpc, result).visit(cpc.getRootNode());
        }
        else if (isEmptyModuleDescriptor(cpc)) {
            addModuleDescriptorCompletion(cpc, offset, prefix, result);
            addKeywordProposals(cpc, offset, prefix, result, node);
        }
        else if (isEmptyPackageDescriptor(cpc)) {
            addPackageDescriptorCompletion(cpc, offset, prefix, result);
            addKeywordProposals(cpc, offset, prefix, result, node);
        }
        else if (node instanceof Tree.TypeArgumentList && 
        		token.getType()==CeylonLexer.LARGER_OP) {
        	if (offset==token.getStopIndex()+1) {
        		addArgumentListProposal(offset, cpc, node, scope, document, result);
        	}
        	else if (isMemberNameProposable(offset, node, prefix, memberOp)) {
        		addMemberNameProposals(offset, cpc, node, result);
        	}
        	else {
        		return null;
        	}
        }
        else {
            return null;
        }
        return result.toArray(new ICompletionProposal[result.size()]);
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
    
    private static boolean isMemberNameProposable(int offset, Node node, String prefix, boolean memberOp) {
        return !memberOp &&
                node.getEndToken()!=null && 
               ((CommonToken)node.getEndToken()).getStopIndex()>=offset-2;
    }
    
    /**
     * BaseMemberExpressions in Annotations have funny lying
     * scopes, but we can extract the real scope out of the
     * identifier! (Yick)
     */
    private static Scope getRealScope(final Node node, CompilationUnit cu) {
    	
        class FindScopeVisitor extends Visitor {
            Scope scope;
            public void visit(Tree.Declaration that) {
                super.visit(that);
                AnnotationList al = that.getAnnotationList();
                if (al!=null) {
                    for (Tree.Annotation a: al.getAnnotations()) {
                        Integer i = a.getPrimary().getStartIndex();
                        Integer j = node.getStartIndex();
                        if (i.intValue()==j.intValue()) {
                            scope = that.getDeclarationModel().getScope();
                        }
                    }
                }
            }
            
            public void visit(Tree.DocLink that) {
                super.visit(that);
                scope = ((Tree.DocLink)node).getPkg();
            }
        };
        FindScopeVisitor fsv = new FindScopeVisitor();
        fsv.visit(cu);
        return fsv.scope==null?node.getScope():fsv.scope;
    }
    
    private static ICompletionProposal[] constructCompletions(final int offset, final String prefix, 
            Set<DeclarationWithProximity> set, final CeylonParseController cpc, Scope scope, Node node, 
            CommonToken token, boolean memberOp, IDocument doc, boolean filter, boolean inDoc) {
        
        final List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
        OccurrenceLocation ol = getOccurrenceLocation(cpc.getRootNode(), node);
        
        if (node instanceof Tree.TypeConstraint) {
            for (DeclarationWithProximity dwp: set) {
                Declaration dec = dwp.getDeclaration();
                if (isTypeParameterOfCurrentDeclaration(node, dec)) {
                    addBasicProposal(offset, prefix, cpc, result, dwp, dec, scope);
                }
            }
        }
        else if ((node instanceof Tree.SimpleType || 
        		node instanceof Tree.BaseTypeExpression ||
        		node instanceof Tree.QualifiedTypeExpression) 
        		&& prefix.isEmpty() && 
        		isMemberNameProposable(offset, node, prefix, memberOp)) {
            //member names we can refine
            ProducedType t=null;
            if (node instanceof Tree.Type) {
            	t = ((Tree.Type) node).getTypeModel();
            }
            else if (node instanceof Tree.BaseTypeExpression) {
            	t = ((Tree.BaseTypeExpression) node).getTarget().getType();
            }
            else if (node instanceof Tree.QualifiedTypeExpression) {
            	t = ((Tree.BaseTypeExpression) node).getTarget().getType();
            }
            if (t!=null) {
                for (DeclarationWithProximity dwp: set) {
                    Declaration dec = dwp.getDeclaration();
                    if (isRefinementProposable(dec, ol, scope) && !filter && 
                    		dec instanceof MethodOrValue) {
                    	MethodOrValue m = (MethodOrValue) dec;
                        for (Declaration d: overloads(dec)) {
                            if ((d.isDefault() || d.isFormal()) &&
                            		t.isSubtypeOf(m.getType())) {
                                try {
                                	//sucks: we can't replace the annotations to add "shared actual" :-(
	                                String pfx = doc.get(node.getStartIndex(), offset-node.getStartIndex());
	                            	addRefinementProposal(offset, pfx, cpc, scope, node, result, d, doc, false);
                                }
                                catch (BadLocationException e) {
	                                e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            //otherwise guess something from the type
            addMemberNameProposal(offset, prefix, node, result);
        }
        else if (node instanceof Tree.TypedDeclaration && 
        		!(node instanceof Tree.Variable && 
        				((Tree.Variable) node).getType() instanceof Tree.SyntheticVariable) &&
        		!(node instanceof Tree.InitializerParameter) &&
        		isMemberNameProposable(offset, node, prefix, memberOp)) {
            //member names we can refine
            Tree.Type dnt = ((Tree.TypedDeclaration) node).getType();
            if (dnt!=null && dnt.getTypeModel()!=null) {
            	ProducedType t = dnt.getTypeModel();
                for (DeclarationWithProximity dwp: set) {
                    Declaration dec = dwp.getDeclaration();
                    if (isRefinementProposable(dec, ol, scope) && !filter && 
                    		dec instanceof MethodOrValue) {
                    	MethodOrValue m = (MethodOrValue) dec;
                        for (Declaration d: CompletionUtil.overloads(dec)) {
                            if ((d.isDefault() || d.isFormal()) &&
                            		t.isSubtypeOf(m.getType())) {
                                try {
	                                String pfx = doc.get(node.getStartIndex(), offset-node.getStartIndex());
	                            	addRefinementProposal(offset, pfx, cpc, scope, node, result, d, doc, true);
                                }
                                catch (BadLocationException e) {
	                                e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            //otherwise guess something from the type
            addMemberNameProposal(offset, prefix, node, result);
        }
        else {
        	boolean isMember = node instanceof Tree.QualifiedMemberOrTypeExpression;
        	
            if (!filter && !inDoc && !isMember) {
                addKeywordProposals(cpc, offset, prefix, result, node);
                //addTemplateProposal(offset, prefix, result);
            }
            
            boolean isPackageOrModuleDescriptor = isModuleDescriptor(cpc) || isPackageDescriptor(cpc);
            for (DeclarationWithProximity dwp: set) {
                Declaration dec = dwp.getDeclaration();
                
                if (isPackageOrModuleDescriptor && !inDoc) {
                    if (!dec.isAnnotation()||!(dec instanceof Method)) continue;
                }
                
                if (isParameterOfNamedArgInvocation(scope, dwp)) {
                    if (isDirectlyInsideNamedArgumentList(cpc, node, token)) {
                        addNamedArgumentProposal(offset, prefix, cpc, result, dwp, dec);
                        addInlineFunctionProposal(offset, prefix, cpc, node, result, dec, doc);
                    }
                }
                
                CommonToken nextToken = getNextToken(cpc, token);
                boolean noParamsFollow = noParametersFollow(nextToken);
                if (isInvocationProposable(dwp, ol) && 
                        !isQualifiedType(node) && 
                        !inDoc && noParamsFollow) {
                    for (Declaration d: CompletionUtil.overloads(dec)) {
                        ProducedReference pr = isMember? 
                                getQualifiedProducedReference(node, d) :
                                RefinementCompletions.getRefinedProducedReference(scope, d);
                        addInvocationProposals(offset, prefix, cpc, result, 
                                new DeclarationWithProximity(d, dwp), 
                                pr, scope, ol, null);
                    }
                }
                
                if (isProposable(dwp, ol, scope) && 
                        (definitelyRequiresType(ol) || noParamsFollow || 
                        		dwp.getDeclaration() instanceof Functional)) {
                	if (ol==DOCLINK) {
                        addDocLinkProposal(offset, prefix, cpc, result, dwp, dec, scope);
                	}
                	else if (ol==IMPORT) {
                		addImportProposal(offset, prefix, cpc, result, dwp, dec, scope);
                	}
                	else {
                		addBasicProposal(offset, prefix, cpc, result, dwp, dec, scope);
                	}
                }
                
                if (isProposable(dwp, ol, scope) && ol!=IMPORT &&
                        isDirectlyInsideBlock(node, cpc, scope, token) && 
                        !memberOp && !filter) {
                    addForProposal(offset, prefix, cpc, result, dwp, dec);
                    addIfExistsProposal(offset, prefix, cpc, result, dwp, dec);
                    addSwitchProposal(offset, prefix, cpc, result, dwp, dec, node, doc);
                }
                
                if (isRefinementProposable(dec, ol, scope) && !memberOp && !isMember && !filter) {
                    for (Declaration d: CompletionUtil.overloads(dec)) {
                        if (d.isDefault() || d.isFormal()) {
                        	addRefinementProposal(offset, prefix, cpc, scope, node, result, d, doc, true);
                        }
                    }
                }
            }
        }
        return result.toArray(new ICompletionProposal[result.size()]);
    }

    private static boolean isQualifiedType(Node node) {
        return (node instanceof Tree.QualifiedType) ||
               (node instanceof Tree.QualifiedMemberOrTypeExpression &&
                       ((Tree.QualifiedMemberOrTypeExpression) node).getStaticMethodReference());
    }

    private static boolean noParametersFollow(CommonToken nextToken) {
        return nextToken==null ||
                //should we disable this, since a statement
                //can in fact begin with an LPAREN??
                nextToken.getType()!=CeylonLexer.LPAREN
                //disabled now because a declaration can
                //begin with an LBRACE (an Iterable type)
                /*&& nextToken.getType()!=CeylonLexer.LBRACE*/;
    }
    
    private static boolean definitelyRequiresType(OccurrenceLocation ol) {
        return ol==SATISFIES || ol==OF || ol==UPPER_BOUND || ol==TYPE_ALIAS;
    }

    private static CommonToken getNextToken(final CeylonParseController cpc,
            CommonToken token) {
        int i = token.getTokenIndex();
        CommonToken nextToken=null;
        List<CommonToken> tokens = cpc.getTokens();
        do {
            if (++i<tokens.size()) {
                nextToken = tokens.get(i);
            }
            else {
                break;
            }
        }
        while (nextToken.getChannel()==CommonToken.HIDDEN_CHANNEL);
        return nextToken;
    }

    private static boolean isDirectlyInsideNamedArgumentList(
            CeylonParseController cpc, Node node, CommonToken token) {
        return node instanceof Tree.NamedArgumentList ||
                (!(node instanceof Tree.SequenceEnumeration) &&
                        occursAfterBraceOrSemicolon(token, cpc.getTokens()));
    }
    
    private static boolean isMemberOperator(Token token) {
        int type = token.getType();
        return type==CeylonLexer.MEMBER_OP || 
                type==CeylonLexer.SPREAD_OP ||
                type==CeylonLexer.SAFE_MEMBER_OP;
    }

    /*private static boolean isKeywordProposable(OccurrenceLocation ol) {
        return ol==null || ol==EXPRESSION;
    }*/
    
    private static boolean isRefinementProposable(Declaration dec, OccurrenceLocation ol, Scope scope) {
        return ol==null && (dec instanceof MethodOrValue || dec instanceof Class) && 
        		scope instanceof ClassOrInterface &&
                ((ClassOrInterface) scope).isInheritedFromSupertype(dec);
    }
    
    private static boolean isInvocationProposable(DeclarationWithProximity dwp, OccurrenceLocation ol) {
        Declaration dec = dwp.getDeclaration();
        return dec instanceof Functional && 
                //!((Functional) dec).getParameterLists().isEmpty() &&
                (ol==null || 
                 ol==EXPRESSION && (!(dec instanceof Class) || !((Class)dec).isAbstract()) || 
                 ol==EXTENDS && dec instanceof Class && !((Class)dec).isFinal() && 
                         ((Class)dec).getTypeParameters().isEmpty() ||
                 ol==CLASS_ALIAS && dec instanceof Class ||
                 ol==PARAMETER_LIST && dec instanceof Method && 
                         dec.isAnnotation()) &&
                dwp.getNamedArgumentList()==null &&
                (!dec.isAnnotation() || !(dec instanceof Method) || 
                        !((Method)dec).getParameterLists().isEmpty() &&
                        !((Method)dec).getParameterLists().get(0).getParameters().isEmpty());
    }

    private static boolean isProposable(DeclarationWithProximity dwp, OccurrenceLocation ol, Scope scope) {
        Declaration dec = dwp.getDeclaration();
        return (ol!=EXTENDS || dec instanceof Class && !((Class)dec).isFinal()) && 
               (ol!=CLASS_ALIAS || dec instanceof Class) &&
               (ol!=SATISFIES || dec instanceof Interface) &&
               (ol!=OF || dec instanceof Class || (dec instanceof Value && ((Value) dec).getTypeDeclaration()!=null && 
                        ((Value) dec).getTypeDeclaration().isAnonymous())) && 
               ((ol!=TYPE_ARGUMENT_LIST && ol!=UPPER_BOUND && ol!=TYPE_ALIAS) || dec instanceof TypeDeclaration) &&
               (ol!=PARAMETER_LIST ||
                        dec instanceof TypeDeclaration || 
                        dec instanceof Method && dec.isAnnotation() || //i.e. an annotation 
                        dec instanceof Value && dec.getContainer().equals(scope)) && //a parameter ref
               (ol!=IMPORT || !dwp.isUnimported()) &&
               ol!=TYPE_PARAMETER_LIST && 
               dwp.getNamedArgumentList()==null;
    }

    private static boolean isTypeParameterOfCurrentDeclaration(Node node, Declaration d) {
        //TODO: this is a total mess and totally error-prone - figure out something better!
        return d instanceof TypeParameter && (((TypeParameter) d).getContainer()==node.getScope() ||
                        ((Tree.TypeConstraint) node).getDeclarationModel()!=null &&
                        ((TypeParameter) d).getContainer()==((Tree.TypeConstraint) node).getDeclarationModel().getContainer());
    }
    
    /*private static boolean isParameterOfNamedArgInvocation(Node node, Declaration d) {
        if (node instanceof Tree.NamedArgumentList) {
            ParameterList pl = ((Tree.NamedArgumentList) node).getNamedArgumentList()
                    .getParameterList();
            return d instanceof Parameter && pl!=null &&
                    pl.getParameters().contains(d);
        }
        else if (node.getScope() instanceof NamedArgumentList) {
            ParameterList pl = ((NamedArgumentList) node.getScope()).getParameterList();
            return d instanceof Parameter && pl!=null &&
                    pl.getParameters().contains(d);
        }
        else {
            return false;
        }
    }*/
    
    private static boolean isParameterOfNamedArgInvocation(Scope scope, DeclarationWithProximity d) {
        return scope==d.getNamedArgumentList();
    }

    private static ProducedReference getQualifiedProducedReference(Node node, Declaration d) {
        ProducedType pt = ((Tree.QualifiedMemberOrTypeExpression) node)
                    .getPrimary().getTypeModel();
        if (pt!=null && d.isClassOrInterfaceMember()) {
            pt = pt.getSupertype((TypeDeclaration)d.getContainer());
        }
        return d.getProducedReference(pt, Collections.<ProducedType>emptyList());
    }

    private static Set<DeclarationWithProximity> sortProposals(final String prefix, 
            final ProducedType type, Map<String, DeclarationWithProximity> proposals) {
        Set<DeclarationWithProximity> set = new TreeSet<DeclarationWithProximity>(
                new ProposalComparator(prefix, type));
        set.addAll(proposals.values());
        return set;
    }
    
    public static Map<String, DeclarationWithProximity> getProposals(Node node, 
    		Scope scope, Tree.CompilationUnit cu) {
       return getProposals(node, scope, "", false, cu); 
    }
    
    private static Map<String, DeclarationWithProximity> getProposals(Node node, 
    		Scope scope, String prefix, boolean memberOp, Tree.CompilationUnit cu) {
        if (node instanceof MemberLiteral) { //this case is rather ugly!
            Tree.StaticType mlt = ((Tree.MemberLiteral) node).getType();
            if (mlt!=null) {
                ProducedType type = mlt.getTypeModel();
                if (type!=null) {
                    return type.resolveAliases().getDeclaration()
                            .getMatchingMemberDeclarations(scope, prefix, 0);
                }
                else {
                    return Collections.emptyMap();
                }
            }
        }
        if (node instanceof Tree.QualifiedMemberOrTypeExpression) {
            Tree.QualifiedMemberOrTypeExpression qmte = (Tree.QualifiedMemberOrTypeExpression) node;
            ProducedType type = getPrimaryType((Tree.QualifiedMemberOrTypeExpression) node);
            if (qmte.getStaticMethodReference()) {
                type = node.getUnit().getCallableReturnType(type);
            }
            if (type!=null) {
                return type.resolveAliases().getDeclaration()
                        .getMatchingMemberDeclarations(scope, prefix, 0);
            }
            else if (qmte.getPrimary() instanceof Tree.MemberOrTypeExpression) {
                //it might be a qualified type or even a static method reference
                Declaration pmte = ((Tree.MemberOrTypeExpression) qmte.getPrimary()).getDeclaration();
                if (pmte instanceof TypeDeclaration) {
                    type = ((TypeDeclaration) pmte).getType();
                    if (type!=null) {
                        return type.resolveAliases().getDeclaration()
                                .getMatchingMemberDeclarations(scope, prefix, 0);
                    }
                }
            }
            return Collections.emptyMap();
        } 
        else if (node instanceof Tree.QualifiedType) {
            ProducedType type = ((Tree.QualifiedType) node).getOuterType().getTypeModel();
            if (type!=null) {
                return type.resolveAliases().getDeclaration()
                        .getMatchingMemberDeclarations(scope, prefix, 0);
            }
            else {
                return Collections.emptyMap();
            }
        }
        else if (memberOp && (node instanceof Tree.Term || node instanceof Tree.DocLink)) {
            ProducedType type = null;
            if (node instanceof Tree.DocLink) {
                Declaration d = ((Tree.DocLink)node).getBase();
                if (d != null) {
                    type = getResultType(d);
                    if (type == null) {
                        type = d.getReference().getFullType();
                    }
                }
            }
            else if (node instanceof Tree.StringLiteral) {
                type = null;
            }
            else if (node instanceof Tree.Term) {
                type = ((Tree.Term)node).getTypeModel();
            } 

        	
            if (type!=null) {
                return type.resolveAliases().getDeclaration()
                        .getMatchingMemberDeclarations(scope, prefix, 0);
            }
            else {
                return Collections.emptyMap();
            }
        }
        else {
            if (scope instanceof ImportList) {
                return ((ImportList) scope).getMatchingDeclarations(null, prefix, 0);
            }
            else {
                return scope==null ? //a null scope occurs when we have not finished parsing the file
                        getUnparsedProposals(cu, prefix) :
                        scope.getMatchingDeclarations(node.getUnit(), prefix, 0);
            }
        }
    }

    private static ProducedType getPrimaryType(Tree.QualifiedMemberOrTypeExpression qme) {
        ProducedType type = qme.getPrimary().getTypeModel();
        if (type==null) return null;
        if (qme.getMemberOperator() instanceof Tree.SafeMemberOp) {
            return qme.getUnit().getDefiniteType(type);
        }
        else if (qme.getMemberOperator() instanceof Tree.SpreadOp) {
            return qme.getUnit().getIteratedType(type);
        }
        else {
            return type;
        }
    }
    
    private static Map<String, DeclarationWithProximity> getUnparsedProposals(Node node, 
            String prefix) {
        if (node == null) {
            return new TreeMap<String,DeclarationWithProximity>();
        }
        Unit unit = node.getUnit();
        if (unit == null) {
            return new TreeMap<String,DeclarationWithProximity>();
        }
        Package pkg = unit.getPackage();
        if (pkg == null) {
            return new TreeMap<String,DeclarationWithProximity>();
        }
        return pkg.getModule().getAvailableDeclarations(prefix);
    }
    
}
