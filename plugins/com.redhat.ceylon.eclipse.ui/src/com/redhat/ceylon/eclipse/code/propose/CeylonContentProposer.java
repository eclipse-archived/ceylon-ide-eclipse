package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.compiler.loader.AbstractModelLoader.JDK_MODULE_VERSION;
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
import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationForModule;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ARCHIVE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.LOCAL_NAME;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.PACKAGE;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.keywords;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getInlineFunctionDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getInlineFunctionTextFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getNamedInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getNamedInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getPositionalInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getPositionalInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getRefinementDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getRefinementTextFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getTextForDocLink;
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
import static com.redhat.ceylon.eclipse.code.propose.ProposalUtil.overloads;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackageName;
import static com.redhat.ceylon.eclipse.util.Escaping.escape;
import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;
import static com.redhat.ceylon.eclipse.util.Escaping.escapePackageName;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static com.redhat.ceylon.eclipse.util.ModuleQueries.getModuleSearchResults;
import static com.redhat.ceylon.eclipse.util.Types.getResultType;
import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.cmr.api.ModuleVersionDetails;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.ImportList;
import com.redhat.ceylon.compiler.typechecker.model.Interface;
import com.redhat.ceylon.compiler.typechecker.model.IntersectionType;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.NothingType;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.AnnotationList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.MemberLiteral;
import com.redhat.ceylon.compiler.typechecker.tree.Util;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.Indents;
import com.redhat.ceylon.eclipse.util.RequiredTypeVisitor;

public class CeylonContentProposer {
    
    public static Image DEFAULT_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(CeylonResources.CEYLON_DEFAULT_REFINEMENT);
    public static Image FORMAL_REFINEMENT = CeylonPlugin.getInstance()
            .getImageRegistry().get(CeylonResources.CEYLON_FORMAL_REFINEMENT);
    
    
    /**
     * Returns an array of content proposals applicable relative to the AST of the given
     * parse controller at the given position.
     * 
     * (The provided ITextViewer is not used in the default implementation provided here
     * but but is stipulated by the IContentProposer interface for purposes such as accessing
     * the IDocument for which content proposals are sought.)
     * 
     * @param controller  A parse controller from which the AST of the document being edited
     *             can be obtained
     * @param int      The offset for which content proposals are sought
     * @param viewer    The viewer in which the document represented by the AST in the given
     *             parse controller is being displayed (may be null for some implementations)
     * @param returnedParamInfo 
     * @return        An array of completion proposals applicable relative to the AST of the given
     *             parse controller at the given position
     */
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

    public static OccurrenceLocation getOccurrenceLocation(Tree.CompilationUnit cu, Node node) {
        if (node.getToken()==null) return null;
        FindOccurrenceLocationVisitor visitor = new FindOccurrenceLocationVisitor(node);
        cu.visit(visitor);
        return visitor.getOccurrenceLocation();
    }

    private static class DocLink {
//        String linkName;
    	String declName;
    	int declStart;
    }
    
    private static DocLink getDocLink(String text, int offsetInToken, final int offset) {     
        
        Matcher docLinkMatcher = Pattern.compile("\\[\\[(.*?)\\]\\]").matcher(text);
        if (text==null || offset==0 || !docLinkMatcher.find()) {
            return null;
        }

        DocLink docLink = null;
        docLinkMatcher.reset();
        while (docLinkMatcher.find()) {
            for (int i = 1; i <= docLinkMatcher.groupCount(); i++) { // loop for safety
                if (offsetInToken >= docLinkMatcher.start(i) && 
                        offsetInToken <= docLinkMatcher.end(i)) {
                    docLink = new DocLink();
                    String docLinkText = docLinkMatcher.group(i);
                    int separatorIndex = docLinkText.indexOf("|");
                    if( separatorIndex > -1 ) {
//                        docLink.linkName = docLinkText.substring(0, separatorIndex);
                        docLink.declName = docLinkText.substring(separatorIndex+1);
                        docLink.declStart = docLinkMatcher.start(i)+separatorIndex+1;
                    } else {
                        docLink.declName = docLinkText;
                        docLink.declStart = docLinkMatcher.start(i);
                    }
                    break;
                }
            }
        }
        
        if (docLink == null) { // it will be empty string if we are in a wiki ref
            return null;
        }
        
        return docLink;
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
        //try to guess the character the user just typed
        //(it would be the character immediately behind
        //the caret, i.e. at offset-1)
        char charAtOffset = text.charAt(offset-1);
        int offsetInToken = offset-1-token.getStartIndex();
        boolean inToken = offsetInToken>=0 && 
                offsetInToken<token.getText().length();
        
        //int end = offset;
        if (inToken && 
                charAtOffset==token.getText().charAt(offsetInToken)) {
            //then we're not missing the typed character 
            //from the tree we were passed by IMP
            if (isIdentifierOrKeyword(token)) {
                return new PositionedPrefix(
                     token.getText().substring(0, offsetInToken+1),
                     token.getStartIndex());
            }
            else if (token.getType() == ASTRING_LITERAL || token.getType() == AVERBATIM_STRING) {
                DocLink docLink = getDocLink(token.getText(), offset - token.getStartIndex(), offsetInToken);
                if (docLink != null) {
                    // if caret is after '|'
                    if( offsetInToken >= docLink.declStart-1 ) {
                        // if caret is after the '.' and the '.' is a member lookup
                        if ((offsetInToken > (docLink.declStart + docLink.declName.lastIndexOf('.') -1) ) 
                                && docLink.declName.lastIndexOf('.') > docLink.declName.lastIndexOf("::")) {
                            PositionedPrefix pp = new PositionedPrefix(token.getStartIndex()+docLink.declStart, true);
                            if (docLink.declStart + docLink.declName.lastIndexOf('.') <= offsetInToken) {
                                pp.prefix = docLink.declName.substring(docLink.declName.lastIndexOf('.') + 1, offsetInToken - docLink.declStart +1);
                                pp.type = docLink.declName.substring(0, docLink.declName.lastIndexOf('.'));
                            }
                            return pp;
                        } else { // type lookup, handle package later
                            return new PositionedPrefix(docLink.declName, token.getStartIndex()+docLink.declStart);
                        }
                    }
                }
                return null; // in literal but no docRef
            }
            else {
                return new PositionedPrefix(offset, isMemberOperator(token));
            }
        } 
        else {
            //then we are missing the typed character from
            //the tree, along with possibly some other
            //previously typed characters
            boolean isIdentifierChar = isJavaIdentifierPart(charAtOffset);
            if (isIdentifierChar) {
                if (token.getType()==CeylonLexer.WS) {
                    //we are typing in or after whitespace
                    String prefix = text.substring(token.getStartIndex(), offset).trim();
                    return new PositionedPrefix(prefix, offset-prefix.length()-1);
                }
                else if (isIdentifierOrKeyword(token)) {
                    //we are typing in or after within an 
                    //identifier or keyword
                    String prefix = text.substring(token.getStartIndex(), offset);
                    return new PositionedPrefix(prefix, token.getStartIndex());
                }
                else if (offset<=token.getStopIndex()+1) {
                    //we are typing in or after a comment 
                    //block or strings, etc - not much 
                    //useful compensation we can do here
                    return new PositionedPrefix(
                            Character.toString(charAtOffset), 
                            offset-1);
                }
                else {
                    //after a member dereference and other
                    //misc cases of punctuation, etc
                    return new PositionedPrefix(
                            text.substring(token.getStopIndex()+1, offset),
                            token.getStopIndex());
                }
            }
            //disable this for now cos it causes problem in
            //import statements
            /*else if (charAtOffset=='.') {
                return new PositionedPrefix(offset-2, true);
            }
            else {*/
                return new PositionedPrefix(offset-1, false);
            //}
        }
    }

    private static CommonToken getTokenAtCaret(final int offset, ITextViewer viewer,
            List<CommonToken> tokens) {
        //find the token behind the caret, adjusting to an 
        //earlier token if the token we find is not at the 
        //same position in the current text (in which case 
        //it is probably a token that actually comes after 
        //what we are currently typing)
        if (offset==0) {
            return null;
        }
        int index = getTokenIndexAtCharacter(tokens, offset-1);
        if (index<0) index = -index;
        while (index>=0) {
            CommonToken token = (CommonToken) tokens.get(index);
            String text = viewer.getDocument().get();
            boolean tokenHasMoved = text.charAt(token.getStartIndex())!=
                    token.getText().charAt(0);
            if (!tokenHasMoved) {
                return token;
            }
            index--;
        }
        return null;
    }
    
    static final class ParameterInfo 
            extends DeclarationCompletionProposal {
		private ParameterInfo(int offset, String prefix, String desc,
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
    
    private static void addPackageCompletions(CeylonParseController cpc, 
            int offset, String prefix, Tree.ImportPath path, Node node, 
            List<ICompletionProposal> result, boolean withBody) {
        String fullPath = fullPath(offset, prefix, path);
        addPackageCompletions(offset, prefix, node, result, fullPath.length(), 
        		fullPath+prefix, cpc, withBody);
    }

    private static void addModuleCompletions(CeylonParseController cpc, 
            int offset, String prefix, Tree.ImportPath path, Node node, 
            List<ICompletionProposal> result, boolean withBody) {
        String fullPath = fullPath(offset, prefix, path);
        addModuleCompletions(offset, prefix, node, result, fullPath.length(), 
        		fullPath+prefix, cpc, withBody);
    }

    private static String fullPath(int offset, String prefix,
            Tree.ImportPath path) {
        StringBuilder fullPath = new StringBuilder();
        if (path!=null) {
            fullPath.append(Util.formatPath(path.getIdentifiers()));
            fullPath.append('.');
            fullPath.setLength(offset-path.getStartIndex()-prefix.length());
        }
        return fullPath.toString();
    }

    private static void addPackageCompletions(final int offset, final String prefix,
            Node node, List<ICompletionProposal> result, final int len, String pfp,
            final CeylonParseController cpc, final boolean withBody) {
        //TODO: someday it would be nice to propose from all packages 
        //      and auto-add the module dependency!
        /*TypeChecker tc = CeylonBuilder.getProjectTypeChecker(cpc.getProject().getRawProject());
        if (tc!=null) {
        for (Module m: tc.getContext().getModules().getListOfModules()) {*/
        //Set<Package> packages = new HashSet<Package>();
        Unit unit = node.getUnit();
        if (unit!=null) { //a null unit can occur if we have not finished parsing the file
            Module module = unit.getPackage().getModule();
            for (final Package p: module.getAllPackages()) {
                //if (!packages.contains(p)) {
                    //packages.add(p);
                //if ( p.getModule().equals(module) || p.isShared() ) {
                    final String pkg = escapePackageName(p);
                    if (!pkg.isEmpty() && pkg.startsWith(pfp)) {
                        boolean already = false;
                        if (!pfp.equals(pkg)) {
                            //don't add already imported packages, unless
                            //it is an exact match to the typed path
                            for (ImportList il: node.getUnit().getImportLists()) {
                                if (il.getImportedScope()==p) {
                                    already = true;
                                    break;
                                }
                            }
                        }
                        if (!already) {
                        	final String completed = pkg + (withBody?" { ... }":"");
                            result.add(new CompletionProposal(offset, prefix, PACKAGE, 
                            		completed, completed.substring(len), false) {
                                @Override
                                public Point getSelection(IDocument document) {
                                	if (withBody) {
                                		return new Point(offset+completed.length()-prefix.length()-len-5, 3);
                                	}
                                	else {
                                		return new Point(offset+completed.length()-prefix.length()-len, 0);
                                	}
                                }
                                @Override
                                public String getAdditionalProposalInfo() {
                                    return getDocumentationFor(cpc, p);
                                }
                            });
                        }
                    }
                //}
            }
        }
    }
    
    private static final SortedSet<String> JDK_MODULE_VERSION_SET = new TreeSet<String>();
    {
        JDK_MODULE_VERSION_SET.add(AbstractModelLoader.JDK_MODULE_VERSION);
    }
    
    private static void addModuleCompletions(int offset, String prefix, Node node, 
    		List<ICompletionProposal> result, final int len, String pfp,
            final CeylonParseController cpc, final boolean withBody) {
        if (pfp.startsWith("java.")) {
            for (final String name: new TreeSet<String>(JDKUtils.getJDKModuleNames())) {
                if (name.startsWith(pfp) &&
                        !moduleAlreadyImported(cpc, name)) {
                    String versioned = withBody ? getModuleString(name, JDK_MODULE_VERSION) + ";" : name;
                    result.add(new CompletionProposal(offset, prefix, ARCHIVE, 
                                      versioned, versioned.substring(len), false) {
                        @Override
                        public String getAdditionalProposalInfo() {
                            return getDocumentationForModule(name, JDK_MODULE_VERSION, 
                                    "This module forms part of the Java SDK.");
                        }
                    });
                }
            }
        }
        else {
            final TypeChecker tc = cpc.getTypeChecker();
            if (tc!=null) {
                IProject project = cpc.getProject();
                for (final ModuleDetails module: getModuleSearchResults(pfp, tc,project)
                        .getResults()) {
                    final String name = module.getName();
                    if (!name.equals(Module.DEFAULT_MODULE_NAME) && 
                            !moduleAlreadyImported(cpc, name)) {
                        for (final ModuleVersionDetails version : module.getVersions().descendingSet()) {
                            final String versioned = withBody ? getModuleString(name, version.getVersion()) + ";" : name;
                            result.add(new CompletionProposal(offset, prefix, ARCHIVE, 
                                    versioned, versioned.substring(len), false) {
                            	@Override
                            	public Point getSelection(
                            			IDocument document) {
                                    if (withBody) {
                                    	return new Point(offset+versioned.length()-prefix.length()-len-version.getVersion().length()-2, 
                                    			version.getVersion().length());
                                    }
                                    else {
                                    	return new Point(offset+versioned.length()-prefix.length()-len, 0);
                                    }
                            	}
                                @Override
                                public String getAdditionalProposalInfo() {
                                    return JDKUtils.isJDKModule(name) ?
                                            getDocumentationForModule(name, JDK_MODULE_VERSION,
                                                    "This module forms part of the Java SDK.") :
                                            getDocumentationFor(module, version.getVersion());
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private static boolean moduleAlreadyImported(CeylonParseController cpc, final String mod) {
        if (mod.equals(Module.LANGUAGE_MODULE_NAME)) {
            return true;
        }
        List<Tree.ModuleDescriptor> md = cpc.getRootNode().getModuleDescriptors();
		if (!md.isEmpty()) {
			Tree.ImportModuleList iml = md.get(0).getImportModuleList();
			if (iml!=null) {
				for (Tree.ImportModule im: iml.getImportModules()) {
					if (im.getImportPath()!=null) {
						if (formatPath(im.getImportPath().getIdentifiers()).equals(mod)) {
							return true;
						}
					}
				}
			}
        }
        //Disabled, because once the module is imported, it hangs around!
//        for (ModuleImport mi: node.getUnit().getPackage().getModule().getImports()) {
//            if (mi.getModule().getNameAsString().equals(mod)) {
//                return true;
//            }
//        }
        return false;
    }

    private static String getModuleString(final String name, final String version) {
        return name + " \"" + version + "\"";
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
            new Visitor() {
                @Override
                public void visit(Tree.ModuleDescriptor that) {
                    super.visit(that);
                    if (that.getImportPath()==node) {
                        addCurrentPackageNameCompletion(cpc, offset, 
                                fullPath(offset, prefix, that.getImportPath()) + prefix, 
                                result);
                    }
                }
                public void visit(Tree.PackageDescriptor that) {
                    super.visit(that);
                    if (that.getImportPath()==node) {
                        addCurrentPackageNameCompletion(cpc, offset, 
                                fullPath(offset, prefix, that.getImportPath()) + prefix, 
                                result);
                    }
                }
                @Override
                public void visit(Tree.Import that) {
                    super.visit(that);
                    if (that.getImportPath()==node) {
                        addPackageCompletions(cpc, offset, prefix, 
                        		(Tree.ImportPath) node, node, result, 
                        		nextTokenType(cpc, token)!=CeylonLexer.LBRACE);
                    }
                }
                @Override
                public void visit(Tree.PackageLiteral that) {
                    super.visit(that);
                    if (that.getImportPath()==node) {
                        addPackageCompletions(cpc, offset, prefix, 
                        		(Tree.ImportPath) node, node, result, false);
                    }
                }
                @Override
                public void visit(Tree.ImportModule that) {
                    super.visit(that);
                    if (that.getImportPath()==node) {
                        addModuleCompletions(cpc, offset, prefix, 
                        		(Tree.ImportPath) node, node, result, 
                        		nextTokenType(cpc, token)!=CeylonLexer.STRING_LITERAL);
                    }
                }
                @Override
                public void visit(Tree.ModuleLiteral that) {
                    super.visit(that);
                    if (that.getImportPath()==node) {
                        addModuleCompletions(cpc, offset, prefix, 
                        		(Tree.ImportPath) node, node, result, false);
                    }
                }
            }.visit(cpc.getRootNode());
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

    private static int nextTokenType(final CeylonParseController cpc,
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

	public static void addMemberNameProposals(final int offset,
			final CeylonParseController cpc, final Node node,
			final List<ICompletionProposal> result) {
		final Integer startIndex2 = node.getStartIndex();
		new Visitor() {
			@Override
			public void visit(Tree.StaticMemberOrTypeExpression that) {
				Integer startIndex = that.getTypeArguments().getStartIndex();
				if (startIndex!=null && startIndex2!=null &&
					startIndex.intValue()==startIndex2.intValue()) {
					addMemberNameProposal(offset, "", that, result);
				}
				super.visit(that);
			}
			public void visit(Tree.SimpleType that) {
				Integer startIndex = that.getTypeArgumentList().getStartIndex();
				if (startIndex!=null && startIndex2!=null &&
					startIndex.intValue()==startIndex2.intValue()) {
					addMemberNameProposal(offset, "", that, result);
				}
				super.visit(that);
			}
		}.visit(cpc.getRootNode());
	}

	public static void addArgumentListProposal(final int offset,
			final CeylonParseController cpc, final Node node,
			final Scope scope, final IDocument document,
			final List<ICompletionProposal> result) {
		final Integer startIndex2 = node.getStartIndex();
		final Integer stopIndex2 = node.getStopIndex();
		final String typeArgText;
		try {
			typeArgText = document.get(startIndex2, stopIndex2-startIndex2+1);
		} 
		catch (BadLocationException e) {
			e.printStackTrace();
			return;
		}
		new Visitor() {
			@Override
			public void visit(Tree.StaticMemberOrTypeExpression that) {
				Integer startIndex = that.getTypeArguments().getStartIndex();
				if (startIndex!=null && startIndex2!=null &&
					startIndex.intValue()==startIndex2.intValue()) {
					ProducedReference pr = that.getTarget();
					Declaration d = that.getDeclaration();
					if (d instanceof Functional && pr!=null) {
						try {
							String pref = document.get(that.getStartIndex(), 
									that.getStopIndex()-that.getStartIndex()+1);
				        	addInvocationProposals(offset, pref, cpc, result, 
				        			new DeclarationWithProximity(d, 0), pr, 
				        			scope, null, typeArgText);
						} 
						catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
				}
				super.visit(that);
			}
			public void visit(Tree.SimpleType that) {
				Integer startIndex = that.getTypeArgumentList().getStartIndex();
				if (startIndex!=null && startIndex2!=null &&
					startIndex.intValue()==startIndex2.intValue()) {
					Declaration d = that.getDeclarationModel();
					if (d instanceof Functional) {
						try {
							String pref = document.get(that.getStartIndex(), 
									that.getStopIndex()-that.getStartIndex()+1);
				        	addInvocationProposals(offset, pref, cpc, result, 
				        			new DeclarationWithProximity(d, 0), 
				        			    that.getTypeModel(), scope, 
				        			    null, typeArgText);
						}
						catch (BadLocationException e) {
							e.printStackTrace();
						}
					}
				}
				super.visit(that);
			}
		}.visit(cpc.getRootNode());
	}

	public static void addFakeShowParametersCompletion(
			final CeylonParseController cpc, final Node node,
			final CommonToken token, final List<ICompletionProposal> result) {
		new Visitor() {
			@Override
			public void visit(Tree.InvocationExpression that) {
				Tree.PositionalArgumentList pal = that.getPositionalArgumentList();
				if (pal!=null) {
					Integer startIndex = pal.getStartIndex();
					Integer startIndex2 = node.getStartIndex();
					if (startIndex!=null && startIndex2!=null &&
							startIndex.intValue()==startIndex2.intValue()) {
						Tree.Primary primary = that.getPrimary();
						if (primary instanceof Tree.MemberOrTypeExpression) {
							Tree.MemberOrTypeExpression mte = (Tree.MemberOrTypeExpression) primary;
							if (mte.getDeclaration()!=null && mte.getTarget()!=null) {
								result.add(new ParameterInfo(token.getStartIndex(), "", 
										"show parameters", "", false, cpc, 
										mte.getDeclaration(), false, mte.getTarget(),
										node.getScope()));
							}
						}
					}
				}
				super.visit(that);
			}
		}.visit(cpc.getRootNode());
	}
    
    /**
     * BaseMemberExpressions in Annotations have funny lying
     * scopes, but we can extract the real scope out of the
     * identifier! (Yick)
     */
    static Scope getRealScope(final Node node, CompilationUnit cu) {
    	
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
                        for (Declaration d: ProposalUtil.overloads(dec)) {
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
                    for (Declaration d: ProposalUtil.overloads(dec)) {
                        ProducedReference pr = isMember? 
                                getQualifiedProducedReference(node, d) :
                                getRefinedProducedReference(scope, d);
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
                    for (Declaration d: ProposalUtil.overloads(dec)) {
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

    private static boolean isPackageDescriptor(CeylonParseController cpc) {
        return cpc.getRootNode() != null && 
                cpc.getRootNode().getUnit() != null &&
                cpc.getRootNode().getUnit().getFilename().equals("package.ceylon"); 
    }

    private static boolean isModuleDescriptor(CeylonParseController cpc) {
        return cpc.getRootNode() != null && 
                cpc.getRootNode().getUnit() != null &&
                cpc.getRootNode().getUnit().getFilename().equals("module.ceylon"); 
    }

    private static boolean isEmptyModuleDescriptor(CeylonParseController cpc) {
        return isModuleDescriptor(cpc) && 
                cpc.getRootNode() != null && 
                cpc.getRootNode().getModuleDescriptors().isEmpty(); 
    }

    private static void addModuleDescriptorCompletion(CeylonParseController cpc, int offset, 
            String prefix, List<ICompletionProposal> result) {
        if (!"module".startsWith(prefix)) return; 
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String moduleName = getPackageName(file);
        if (moduleName!=null) {
            String moduleDesc = "module " + moduleName;
            String moduleText = "module " + moduleName + " \"1.0.0\" {}";
            final int selectionStart = offset - prefix.length() + moduleName.length() + 9;
            final int selectionLength = 5;

            result.add(new CompletionProposal(offset, prefix, ARCHIVE, moduleDesc, moduleText, false) {
                @Override
                public Point getSelection(IDocument document) {
                    return new Point(selectionStart, selectionLength);
                }});
        }
    }
    
    private static void addCurrentPackageNameCompletion(CeylonParseController cpc, int offset, 
            String prefix, List<ICompletionProposal> result) {
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String moduleName = getPackageName(file);
        if (moduleName!=null) {
            result.add(new CompletionProposal(offset, prefix, 
                    isModuleDescriptor(cpc) ? ARCHIVE : PACKAGE, 
                            moduleName, moduleName, false));
        }
    }
    
    private static boolean isEmptyPackageDescriptor(CeylonParseController cpc) {
        return cpc.getRootNode() != null &&
                cpc.getRootNode().getUnit() != null &&
                cpc.getRootNode().getUnit().getFilename().equals("package.ceylon") && 
                cpc.getRootNode().getPackageDescriptors().isEmpty();
    }

    private static void addPackageDescriptorCompletion(CeylonParseController cpc, int offset, 
            String prefix, List<ICompletionProposal> result) {
        if (!"package".startsWith(prefix)) return; 
        IFile file = cpc.getProject().getFile(cpc.getPath());
        String packageName = getPackageName(file);
        if (packageName!=null) {
            String packageDesc = "package " + packageName;
            String packageText = "package " + packageName + ";";

            result.add(new CompletionProposal(offset, prefix, PACKAGE, packageDesc, packageText, false));
        }
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
    
    private static void addInlineFunctionProposal(int offset, String prefix, CeylonParseController cpc,
            Node node, List<ICompletionProposal> result, Declaration d, IDocument doc) {
        //TODO: type argument substitution using the ProducedReference of the primary node
        if (d.isParameter()) {
            Parameter p = ((MethodOrValue) d).getInitializerParameter();
            Unit unit = node.getUnit();
            result.add(new RefinementCompletionProposal(offset, prefix,
                    getInlineFunctionDescriptionFor(p, null, unit),
                    getInlineFunctionTextFor(p, null, unit, 
                    		Indents.getDefaultLineDelimiter(doc) + 
                    		getIndent(node, doc)),
                    cpc, d));
        }
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

    private static void addRefinementProposal(int offset, String prefix, final CeylonParseController cpc,
            Scope scope, Node node, List<ICompletionProposal> result, final Declaration d, IDocument doc,
            boolean preamble) {
        boolean isInterface = scope instanceof Interface;
        ProducedReference pr = getRefinedProducedReference(scope, d);
        //TODO: if it is equals() or hash, fill in the implementation
        String delim = Indents.getDefaultLineDelimiter(doc);
		result.add(new RefinementCompletionProposal(offset, prefix,  
                getRefinementDescriptionFor(d, pr, node.getUnit()), 
                getRefinementTextFor(d, pr, node.getUnit(), isInterface, 
                		delim + getIndent(node, doc), true, preamble), 
                cpc, d));
    }
    
    private static ProducedReference getQualifiedProducedReference(Node node, Declaration d) {
        ProducedType pt = ((Tree.QualifiedMemberOrTypeExpression) node)
                    .getPrimary().getTypeModel();
        if (pt!=null && d.isClassOrInterfaceMember()) {
            pt = pt.getSupertype((TypeDeclaration)d.getContainer());
        }
        return d.getProducedReference(pt, Collections.<ProducedType>emptyList());
    }

    public static ProducedReference getRefinedProducedReference(Scope scope, Declaration d) {
        return refinedProducedReference(scope.getDeclaringType(d), d);
    }

    public static ProducedReference getRefinedProducedReference(ProducedType superType, 
            Declaration d) {
        if (superType.getDeclaration() instanceof IntersectionType) {
            for (ProducedType pt: superType.getDeclaration().getSatisfiedTypes()) {
                ProducedReference result = getRefinedProducedReference(pt, d);
                if (result!=null) return result;
            }
            return null; //never happens?
        }
        else {
            ProducedType declaringType = superType.getDeclaration().getDeclaringType(d);
            if (declaringType==null) return null;
            ProducedType outerType = superType.getSupertype(declaringType.getDeclaration());
            return refinedProducedReference(outerType, d);
        }
    }
    
    private static ProducedReference refinedProducedReference(ProducedType outerType, 
            Declaration d) {
        List<ProducedType> params = new ArrayList<ProducedType>();
        if (d instanceof Generic) {
            for (TypeParameter tp: ((Generic)d).getTypeParameters()) {
                params.add(tp.getType());
            }
        }
        return d.getProducedReference(outerType, params);
    }
    
    private static void addBasicProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Scope scope) {
        result.add(new DeclarationCompletionProposal(offset, prefix,
                getDescriptionFor(dwp), getTextFor(dwp), 
                true, cpc, d, dwp.isUnimported(), d.getReference(), 
                scope, true));
    }

    private static void addImportProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Scope scope) {
        result.add(new DeclarationCompletionProposal(offset, prefix,
				d.getName(), escapeName(d), true, cpc, d, dwp.isUnimported(), 
				d.getReference(), scope, true));
    }

    private static void addDocLinkProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Scope scope) {
        ProducedReference pr = d.getProducedReference(null,
                		Collections.<ProducedType>emptyList());
		result.add(new DeclarationCompletionProposal(offset, prefix,
				d.getName(), getTextForDocLink(cpc, dwp),
                true, cpc, d, false, pr, scope, true));
    }

    private static void addForProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (d instanceof Value) {
            TypedDeclaration td = (TypedDeclaration) d;
            if (td.getType()!=null && 
                    d.getUnit().isIterableType(td.getType())) {
                String elemName;
                if (d.getName().length()==1) {
                    elemName = "element";
                }
                else if (d.getName().endsWith("s")) {
                    elemName = d.getName().substring(0, d.getName().length()-1);
                }
                else {
                    elemName = d.getName().substring(0, 1);
                }
                result.add(new DeclarationCompletionProposal(offset, prefix, 
                        "for (" + elemName + " in " + getDescriptionFor(dwp) + ")", 
                        "for (" + elemName + " in " + getTextFor(dwp) + ") {}",
                        true, cpc, d));
            }
        }
    }

    private static void addIfExistsProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        d.getUnit().isOptionalType(v.getType()) && 
                        !v.isVariable()) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            "if (exists " + getDescriptionFor(dwp) + ")", 
                            "if (exists " + getTextFor(dwp) + ") {}", 
                            true, cpc, d));
                }
            }
        }
    }

    private static void addSwitchProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Node node, 
            IDocument doc) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        v.getType().getCaseTypes()!=null && 
                        !v.isVariable()) {
                    StringBuilder body = new StringBuilder();
                    String indent = getIndent(node, doc);
                    for (ProducedType pt: v.getType().getCaseTypes()) {
                        body.append(indent).append("case (");
                        if (!pt.getDeclaration().isAnonymous()) {
                            body.append("is ");
                        }
                        body.append(pt.getProducedTypeName(node.getUnit()))
                            .append(") {}")
                            .append(Indents.getDefaultLineDelimiter(doc));
                    }
                    body.append(indent);
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            "switch (" + getDescriptionFor(dwp) + ")", 
                            "switch (" + getTextFor(dwp) + ")" + 
                            		Indents.getDefaultLineDelimiter(doc) + body, 
                            true, cpc, d));
                }
            }
        }
    }

    private static void addNamedArgumentProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        result.add(new DeclarationCompletionProposal(offset, prefix, 
                getDescriptionFor(dwp), 
                getTextFor(dwp) + " = nothing;", 
                true, cpc, d));
    }

    private static void addInvocationProposals(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, ProducedReference pr, Scope scope,
            OccurrenceLocation ol, String typeArgs) {
        Declaration d = pr.getDeclaration();
        Unit unit = cpc.getRootNode().getUnit();
        if (!(d instanceof Functional)) return;
        boolean isAbstractClass = d instanceof Class && ((Class) d).isAbstract();
        Functional fd = (Functional) d;
        List<ParameterList> pls = fd.getParameterLists();
        if (!pls.isEmpty()) {
            List<Parameter> ps = pls.get(0).getParameters();
            boolean hasDefaulted = ps.size()!=ProposalUtil.getParameters(false, ps).size();
			if (!isAbstractClass ||
            		ol==EXTENDS || ol==CLASS_ALIAS) {
                if (hasDefaulted) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            getPositionalInvocationDescriptionFor(dwp, ol, pr, unit, false, null), 
                            getPositionalInvocationTextFor(dwp, ol, pr, unit, false, null), true,
                            cpc, d, dwp.isUnimported(), pr, scope, false));
                }
                result.add(new DeclarationCompletionProposal(offset, prefix, 
                        getPositionalInvocationDescriptionFor(dwp, ol, pr, unit, true, typeArgs), 
                        getPositionalInvocationTextFor(dwp, ol, pr, unit, true, typeArgs), true,
                        cpc, d, dwp.isUnimported(), pr, scope, true));
            }
            if (!isAbstractClass &&
            		ol!=EXTENDS && ol!=CLASS_ALIAS &&
                    !fd.isOverloaded() && typeArgs==null) {
                //if there is at least one parameter, 
                //suggest a named argument invocation
                if (hasDefaulted) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            getNamedInvocationDescriptionFor(dwp, pr, unit, false), 
                            getNamedInvocationTextFor(dwp, pr, unit, false), true,
                            cpc, d, dwp.isUnimported(), pr, scope, false));
                }
                if (!ps.isEmpty()) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            getNamedInvocationDescriptionFor(dwp, pr, unit, true), 
                            getNamedInvocationTextFor(dwp, pr, unit, true), true,
                            cpc, d, dwp.isUnimported(), pr, scope, true));
                }
            }
        }
    }
    
	protected static void addMemberNameProposal(int offset, String prefix,
            Node node, List<ICompletionProposal> result) {
        String suggestedName = null;
        if (node instanceof Tree.TypeDeclaration) {
            /*Tree.TypeDeclaration td = (Tree.TypeDeclaration) node;
            prefix = td.getIdentifier()==null ? 
                    "" : td.getIdentifier().getText();
            suggestedName = prefix;*/
            //TODO: dictionary completions?
            return;
        }
        else if (node instanceof Tree.TypedDeclaration) {
            Tree.TypedDeclaration td = (Tree.TypedDeclaration) node;
            if (td.getType() instanceof Tree.SimpleType) {
            	Tree.Identifier id = td.getIdentifier();
            	if (id==null || offset>=id.getStartIndex() && offset<=id.getStopIndex()+1) {
            		suggestedName = ((Tree.SimpleType) td.getType()).getIdentifier().getText();
            	}
            }
        }
        else if (node instanceof Tree.SimpleType) {
            suggestedName = ((Tree.SimpleType) node).getIdentifier().getText();
        }
        else if (node instanceof Tree.BaseTypeExpression) {
            suggestedName = ((Tree.BaseTypeExpression) node).getIdentifier().getText();
        }
        else if (node instanceof Tree.QualifiedTypeExpression) {
            suggestedName = ((Tree.QualifiedTypeExpression) node).getIdentifier().getText();
        }
        if (suggestedName!=null) {
        	suggestedName = lower(suggestedName);
        	if (!suggestedName.startsWith(prefix)) {
        		suggestedName = prefix + upper(suggestedName);
        	}
    		result.add(new CompletionProposal(offset, prefix, LOCAL_NAME,
    				suggestedName, escape(suggestedName), false));
        }
    }

    private static String lower(String suggestedName) {
        return Character.toLowerCase(suggestedName.charAt(0)) + 
                suggestedName.substring(1);
    }

    private static String upper(String suggestedName) {
        return Character.toUpperCase(suggestedName.charAt(0)) + 
                suggestedName.substring(1);
    }

    private static void addKeywordProposals(CeylonParseController cpc, int offset, 
    		String prefix, List<ICompletionProposal> result, Node node) {
        if( isModuleDescriptor(cpc) ) {
            if( prefix.isEmpty() || "import".startsWith(prefix) ) {
                if (node instanceof Tree.CompilationUnit) {
                    List<Tree.ModuleDescriptor> moduleDescriptors = 
                    		cpc.getRootNode().getModuleDescriptors();
                    if (!moduleDescriptors.isEmpty()) {
                        Tree.ModuleDescriptor moduleDescriptor = moduleDescriptors.get(0);
                        if (moduleDescriptor.getImportModuleList() != null && 
                            moduleDescriptor.getImportModuleList().getStartIndex() < offset ) {
                            addKeywordProposal(offset, prefix, result, "import");
                        }
                    }
                }
                else if (node instanceof Tree.ImportModuleList || 
                        node instanceof Tree.BaseMemberExpression) {
                    addKeywordProposal(offset, prefix, result, "import");
                }
            }
        }
        else if (!prefix.isEmpty()) {
            for (String keyword: keywords) {
                if (keyword.startsWith(prefix)) {
                    addKeywordProposal(offset, prefix, result, keyword);
                }
            }
        }
    }

    private static void addKeywordProposal(int offset, String prefix, List<ICompletionProposal> result, final String keyword) {
        result.add(new CompletionProposal(offset, prefix, null, keyword, keyword, true) {
            @Override
            public StyledString getStyledDisplayString() {
                return new StyledString(keyword, CeylonLabelProvider.KW_STYLER);
            }
        });
    }
    
    /*private static void addTemplateProposal(int offset, String prefix, 
            List<ICompletionProposal> result) {
        if (!prefix.isEmpty()) {
            if ("class".startsWith(prefix)) {
                String prop = "class Class() {}";
                result.add(sourceProposal(offset, prefix, null, 
                        null, prop, prop, true));
            }
            if ("interface".startsWith(prefix)) {
                String prop = "interface Interface {}";
                result.add(sourceProposal(offset, prefix, null, 
                        null, prop, prop, true));
            }
            if ("void".startsWith(prefix)) {
                String prop = "void method() {}";
                result.add(sourceProposal(offset, prefix, null, 
                        null, prop, prop, true));
            }
            if ("function".startsWith(prefix)) {
                String prop = "function method() { return nothing; }";
                result.add(sourceProposal(offset, prefix, null, 
                        null, prop, prop, true));
            }
            if ("value".startsWith(prefix)) {
                String prop = "value attribute = nothing;";
                result.add(sourceProposal(offset, prefix, null, 
                        null, prop, prop, true));
                prop = "value attribute { return nothing; }";
                result.add(sourceProposal(offset, prefix, null, 
                        null, prop, prop, true));
            }
            if ("object".startsWith(prefix)) {
                String prop = "object instance {}";
                result.add(sourceProposal(offset, prefix, null, 
                        null, prop, prop, true));
            }
        }
    }*/
    
    /*private static String getDocumentationFor(CeylonParseController cpc, Declaration d) {
        return getDocumentation(getReferencedNode(d, getCompilationUnit(cpc, d)));
    }*/
    
    private static Set<DeclarationWithProximity> sortProposals(final String prefix, 
            final ProducedType type, Map<String, DeclarationWithProximity> proposals) {
        Set<DeclarationWithProximity> set = new TreeSet<DeclarationWithProximity>(
                new Comparator<DeclarationWithProximity>() {
                    public int compare(DeclarationWithProximity x, DeclarationWithProximity y) {
                        boolean xbt = x.getDeclaration() instanceof NothingType;
                        boolean ybt = y.getDeclaration() instanceof NothingType;
                        if (xbt&&ybt) {
                            return 0;
                        }
                        if (xbt&&!ybt) {
                            return 1;
                        }
                        if (ybt&&!xbt) {
                            return -1;
                        }
                        ProducedType xtype = getResultType(x.getDeclaration());
                        ProducedType ytype = getResultType(y.getDeclaration());
                        boolean xbottom = xtype!=null && xtype.getDeclaration() instanceof NothingType;
                        boolean ybottom = ytype!=null && ytype.getDeclaration() instanceof NothingType;
                        if (xbottom && !ybottom) {
                            return 1;
                        }
                        if (ybottom && !xbottom) {
                            return -1;
                        }
                        String xName = x.getName();
                        String yName = y.getName();
                        if (!prefix.isEmpty() && isUpperCase(prefix.charAt(0))) {
                            if (isLowerCase(xName.charAt(0)) && 
                                    isUpperCase(yName.charAt(0))) {
                                return 1;
                            }
                            else if (isUpperCase(xName.charAt(0)) && 
                                    isLowerCase(yName.charAt(0))) {
                                return -1;
                            }
                        }
                        if (type!=null) {
                            boolean xassigns = xtype!=null && xtype.isSubtypeOf(type);
                            boolean yassigns = ytype!=null && ytype.isSubtypeOf(type);
                            if (xassigns && !yassigns) {
                                return -1;
                            }
                            if (yassigns && !xassigns) {
                                return 1;
                            }
                            if (xassigns && yassigns) {
                                boolean xtd = x.getDeclaration() instanceof TypedDeclaration;
                                boolean ytd = y.getDeclaration() instanceof TypedDeclaration;
                                if (xtd && !ytd) {
                                    return -1;
                                }
                                if (ytd && !xtd) {
                                    return 1;
                                }
                            }
                        }
                        if (x.getProximity()!=y.getProximity()) {
                            return new Integer(x.getProximity()).compareTo(y.getProximity());
                        }
                        //if (!prefix.isEmpty() && isLowerCase(prefix.charAt(0))) {
                        if (isLowerCase(xName.charAt(0)) && 
                                isUpperCase(yName.charAt(0))) {
                            return -1;
                        }
                        else if (isUpperCase(xName.charAt(0)) && 
                                isLowerCase(yName.charAt(0))) {
                            return 1;
                        }
                        int nc = xName.compareTo(yName);
                        if (nc==0) {
                            String xqn = x.getDeclaration().getQualifiedNameString();
                            String yqn = y.getDeclaration().getQualifiedNameString();
                            return xqn.compareTo(yqn);
                        }
                        else {
                            return nc;
                        }
                    }
                });
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
