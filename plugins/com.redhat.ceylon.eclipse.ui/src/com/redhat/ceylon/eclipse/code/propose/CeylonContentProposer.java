package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.AIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.EOF;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LBRACE;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.LIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.PIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.RBRACE;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.SEMICOLON;
import static com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer.UIDENTIFIER;
import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAutoEditStrategy.getDefaultIndent;
import static com.redhat.ceylon.eclipse.code.hover.DocHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ANN_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ARCHIVE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.ID_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.KW_STYLER;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.PACKAGE;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.TYPE_STYLER;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findNode;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getTokenIndexAtCharacter;
import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.keywords;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.EXPRESSION;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.EXTENDS;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.IMPORT;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.OF;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.PARAMETER_LIST;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.SATISFIES;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.TYPE_ARGUMENT_LIST;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.TYPE_PARAMETER_LIST;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.UPPER_BOUND;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
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

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.compiler.loader.AbstractModelLoader;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.FunctionalParameter;
import com.redhat.ceylon.compiler.typechecker.model.Generic;
import com.redhat.ceylon.compiler.typechecker.model.Getter;
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
import com.redhat.ceylon.compiler.typechecker.model.ProducedTypedReference;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.TypeAlias;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.model.ValueParameter;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

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
     * @return        An array of completion proposals applicable relative to the AST of the given
     *             parse controller at the given position
     */
    public ICompletionProposal[] getContentProposals(CeylonParseController controller,
            final int offset, ITextViewer viewer, boolean filter) {
        
        if (controller==null || viewer==null) {
            return null;
        }
        
        CeylonParseController cpc = (CeylonParseController) controller;
        cpc.parse(viewer.getDocument(), new NullProgressMonitor(), null);
        cpc.getHandler().updateAnnotations();
        List<CommonToken> tokens = cpc.getTokens(); 
        if (tokens==null) {
            return null;
        }
        
        Tree.CompilationUnit rn = cpc.getRootNode();
        if (rn==null) {
            return null;
        }
        
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

        //find the node at the token
        Node node = getTokenNode(adjustedToken.getStartIndex(), 
                adjustedToken.getStopIndex()+1, 
                adjustedToken.getType(), rn);
        
        //find the type that is expected in the current
        //location so we can prioritize proposals of that
        //type
        //TODO: this breaks as soon as the user starts typing
        //      an expression, since RequiredTypeVisitor
        //      doesn't know how to search up the tree for
        //      the containing InvocationExpression
        RequiredTypeVisitor rtv = new RequiredTypeVisitor(node);
        rtv.visit(rn);
		ProducedType requiredType = rtv.getType();
        
        //finally, construct and sort proposals
        Map<String, DeclarationWithProximity> proposals = getProposals(node, result.prefix, result.isMemberOp, rn);
        filterProposals(filter, rn, requiredType, proposals);
		Set<DeclarationWithProximity> sortedProposals = sortProposals(result.prefix, requiredType, proposals);
		ICompletionProposal[] completions = constructCompletions(offset, result.prefix, sortedProposals,
                    cpc, node, adjustedToken, result.isMemberOp, viewer.getDocument(), filter);
		return completions;
        
    }

	private void filterProposals(boolean filter, Tree.CompilationUnit rn,
			ProducedType requiredType, Map<String, DeclarationWithProximity> proposals) {
		if (filter) {
        	Iterator<Map.Entry<String, DeclarationWithProximity>> iter = proposals.entrySet().iterator();
        	while (iter.hasNext()) {
        		Declaration d = iter.next().getValue().getDeclaration();
				ProducedType type = type(d);
        		ProducedType fullType = fullType(d);
				if (requiredType!=null && (type==null ||
        				(!type.isSubtypeOf(requiredType) && !fullType.isSubtypeOf(requiredType)) || 
        				type.isSubtypeOf(rn.getUnit().getNullDeclaration().getType()))) {
        			iter.remove();
        		}
        	}
        }
	}

    private static OccurrenceLocation getOccurrenceLocation(Tree.CompilationUnit cu, Node node) {
        if (node.getToken()==null) return null;
        FindOccurrenceLocationVisitor visitor = new FindOccurrenceLocationVisitor(node);
        cu.visit(visitor);
        return visitor.getOccurrenceLocation();
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
    
    private static class PositionedPrefix {
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
                (adjustedToken.getChannel()==CommonToken.HIDDEN_CHANNEL //ignore whitespace and comments
                || adjustedToken.getType()==EOF
                || adjustedToken.getStartIndex()==offset)) { //don't consider the token to the right of the caret
            adjustedToken = tokens.get(tokenIndex);
            if (adjustedToken.getChannel()!=CommonToken.HIDDEN_CHANNEL &&
                    adjustedToken.getType()!=EOF) { //don't adjust to a ws/comment token
                break;
            }
        }
        return adjustedToken;
    }
    
    private static Boolean isDirectlyInsideBlock(Node node, CommonToken token, List<CommonToken> tokens) {
        Scope scope = node.getScope();
        if (scope instanceof Interface || 
                scope instanceof Package) {
            return false;
        }
        else {
            //TODO: check that it is not the opening/closing 
            //      brace of a named argument list!
            return !(node instanceof Tree.SequenceEnumeration) && 
                    occursAfterBraceOrSemicolon(token, tokens);
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
            int tokenType, Tree.CompilationUnit rn) {
        Node node = findNode(rn, adjustedStart, adjustedEnd);
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
            List<ICompletionProposal> result) {
        String fullPath = fullPath(offset, prefix, path);
        addPackageCompletions(offset, prefix, node, result, fullPath.length(), fullPath+prefix, cpc);
    }

    private static void addModuleCompletions(CeylonParseController cpc, 
            int offset, String prefix, Tree.ImportPath path, Node node, 
            List<ICompletionProposal> result) {
        String fullPath = fullPath(offset, prefix, path);
        addModuleCompletions(offset, prefix, node, result, fullPath.length(), fullPath+prefix, cpc);
    }

	private static String fullPath(int offset, String prefix,
			Tree.ImportPath path) {
		StringBuilder fullPath = new StringBuilder();
        if (path!=null) {
            fullPath.append(formatPath(path.getIdentifiers()));
            fullPath.setLength(offset-path.getStartIndex()-prefix.length());
        }
		return fullPath.toString();
	}

    private static void addPackageCompletions(int offset, String prefix,
            Node node, List<ICompletionProposal> result, int len, String pfp,
            final CeylonParseController cpc) {
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
                    String pkg = p.getQualifiedNameString();
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
                            result.add(new CompletionProposal(offset, prefix, PACKAGE, 
                                    pkg, pkg.substring(len), false) {
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
    
    private static void addModuleCompletions(int offset, String prefix,
    		Node node, List<ICompletionProposal> result, int len, String pfp,
    		final CeylonParseController cpc) {
    	final TypeChecker tc = cpc.getTypeChecker();
    	if (tc!=null) {
    		ModuleSearchResult results = tc.getContext().getRepositoryManager()
    				.completeModules(new ModuleQuery(pfp, ModuleQuery.Type.JVM));
    		for (final ModuleDetails module: results.getResults()) {
    		    if (!module.getName().equals(Module.DEFAULT_MODULE_NAME)) {
    		        for (final String version : module.getVersions().descendingSet()) {
    		            String versioned = getModuleString(module.getName(), version);
    		            result.add(new CompletionProposal(offset, prefix, ARCHIVE, 
    		                                              versioned, versioned.substring(len), false) {
    		                @Override
    		                public String getAdditionalProposalInfo() {
    		                    return getDocumentationFor(module, version);
    		                }
    		            });
    		        }
    		    }
    		}
    	}
    }

    private static String getModuleString(final String name, final String version) {
        return name + " '" + version + "'";
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
            Set<DeclarationWithProximity> set, final CeylonParseController cpc, final Node node, 
            CommonToken token, boolean memberOp, IDocument doc, boolean filter) {
    	if (node instanceof Tree.Literal) return null;
        final List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
        if (node instanceof Tree.Import && offset>token.getStopIndex()+1) {
            addPackageCompletions(cpc, offset, prefix, null, node, result);
        }
        else if (node instanceof Tree.ImportModule && offset>token.getStopIndex()+1) {
            addModuleCompletions(cpc, offset, prefix, null, node, result);
        }
        else if (node instanceof Tree.ImportPath) {
        	new Visitor() {
        		@Override
        		public void visit(Tree.Import that) {
        			super.visit(that);
        			if (that.getImportPath()==node) {
        	            addPackageCompletions(cpc, offset, prefix, (Tree.ImportPath) node, node, result);
        			}
        		}
        		@Override
        		public void visit(Tree.ImportModule that) {
        			super.visit(that);
        			if (that.getImportPath()==node) {
        	            addModuleCompletions(cpc, offset, prefix, (Tree.ImportPath) node, node, result);
        			}
        		}
			}.visit(cpc.getRootNode());
        }
        else if (node instanceof Tree.TypeConstraint) {
            for (DeclarationWithProximity dwp: set) {
                Declaration dec = dwp.getDeclaration();
                if (isTypeParameterOfCurrentDeclaration(node, dec)) {
                    addBasicProposal(offset, prefix, cpc, result, dwp, dec, null);
                }
            }
        }
        else {
            if ((node instanceof Tree.SimpleType || 
                 node instanceof Tree.BaseTypeExpression ||
                 node instanceof Tree.QualifiedTypeExpression) 
                    && prefix.isEmpty() && !memberOp) {
                addMemberNameProposal(offset, node, result);
            }
            else if (node instanceof Tree.Declaration && !(node instanceof Tree.Variable && 
            		((Tree.Variable)node).getType() instanceof Tree.SyntheticVariable)) {
            	addMemberNameProposal(offset, node, result);
            }
            else {
            	OccurrenceLocation ol = getOccurrenceLocation(cpc.getRootNode(), node);
            	if (//isKeywordProposable(ol) && 
            			!filter &&
            			!(node instanceof Tree.QualifiedMemberOrTypeExpression)) {
            		addKeywordProposals(offset, prefix, result);
            		//addTemplateProposal(offset, prefix, result);
            	}
            	for (DeclarationWithProximity dwp: set) {
            		Declaration dec = dwp.getDeclaration();
            		if (isParameterOfNamedArgInvocation(node, dwp)) {
            			if (isDirectlyInsideNamedArgumentList(cpc, node, token)) {
            				addNamedArgumentProposal(offset, prefix, cpc, result, dwp, dec, ol);
            				addInlineFunctionProposal(offset, prefix, cpc, 
            						node, result, dec, doc);
            			}
            		}
            		CommonToken nextToken = getNextToken(cpc, token);
            		boolean noParamsFollow = noParametersFollow(nextToken);
        			if (isInvocationProposable(dwp, ol)) {
        				if (noParamsFollow || ol==EXTENDS) {
            				for (Declaration d: overloads(dec)) {
            					ProducedReference pr = node instanceof Tree.QualifiedMemberOrTypeExpression ? 
            							getQualifiedProducedReference(node, d) :
            								getRefinedProducedReference(node, d);
            							addInvocationProposals(offset, prefix, cpc, result, 
            									new DeclarationWithProximity(d, dwp), pr, ol);
            				}
            			}
            		}
					if (isProposable(dwp, ol, node.getScope())) {
						if (noParamsFollow || ol==SATISFIES || ol==OF || ol==UPPER_BOUND ||
								dwp.getDeclaration() instanceof Functional) {
							addBasicProposal(offset, prefix, cpc, result, dwp, dec, ol);
						}
            			if (isDirectlyInsideBlock(node, token, cpc.getTokens()) && !memberOp && !filter) {
            				addForProposal(offset, prefix, cpc, result, dwp, dec, ol);
            				addIfExistsProposal(offset, prefix, cpc, result, dwp, dec, ol);
            				addSwitchProposal(offset, prefix, cpc, result, dwp, dec, ol, node, doc);
            			}
            		}
            		if (isRefinementProposable(dec, ol) && !memberOp && !filter) {
            			for (Declaration d: overloads(dec)) {
            				addRefinementProposal(offset, prefix, cpc, node, result, d, doc);
            			}
            		}
            	}
            }
        }
        return result.toArray(new ICompletionProposal[result.size()]);
    }

	private static boolean noParametersFollow(CommonToken nextToken) {
		return nextToken!=null &&
				nextToken.getType()!=CeylonLexer.LPAREN && 
				nextToken.getType()!=CeylonLexer.LBRACE;
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

    private static List<Declaration> overloads(Declaration dec) {
        List<Declaration> decs;
        if (dec instanceof Functional && ((Functional)dec).isAbstraction()) {
            decs = ((Functional)dec).getOverloads();
        }
        else {
            decs = Collections.singletonList(dec);
        }
        return decs;
    }

    /*private static boolean isKeywordProposable(OccurrenceLocation ol) {
        return ol==null || ol==EXPRESSION;
    }*/
    
    private static boolean isRefinementProposable(Declaration dec, OccurrenceLocation ol) {
        return ol==null && (dec instanceof MethodOrValue || dec instanceof Class);
    }
    
    private static boolean isInvocationProposable(DeclarationWithProximity dwp, OccurrenceLocation ol) {
        Declaration dec = dwp.getDeclaration();
        return dec instanceof Functional && 
                //!((Functional) dec).getParameterLists().isEmpty() &&
                (ol==null || ol==EXPRESSION || ol==EXTENDS && dec instanceof Class) &&
                dwp.getNamedArgumentList()==null;
    }

    private static boolean isProposable(DeclarationWithProximity dwp, OccurrenceLocation ol, Scope scope) {
        Declaration dec = dwp.getDeclaration();
        return (dec instanceof Class || ol!=EXTENDS) && 
                (dec instanceof Interface || ol!=SATISFIES) &&
                (dec instanceof Class || (dec instanceof Value && ((Value) dec).getTypeDeclaration() != null && ((Value) dec).getTypeDeclaration().isAnonymous()) || ol!=OF) && 
                (dec instanceof TypeDeclaration || (ol!=TYPE_ARGUMENT_LIST && ol!=UPPER_BOUND)) &&
                (dec instanceof TypeDeclaration || 
                        dec instanceof Method && dec.isToplevel() || //i.e. an annotation 
                        dec instanceof Value && dec.getContainer().equals(scope) ||
                        ol!=PARAMETER_LIST) &&
                (ol!=IMPORT || !dwp.isUnimported()) &&
                ol!=TYPE_PARAMETER_LIST && dwp.getNamedArgumentList()==null;
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
        if (d instanceof Parameter) {
            Parameter p = (Parameter) d;
            result.add(new RefinementCompletionProposal(offset, prefix,
                    getInlineFunctionDescriptionFor(p, null),
                    getInlineFunctionTextFor(p, null, "\n" + getIndent(node, doc)),
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
    
    private static boolean isParameterOfNamedArgInvocation(Node node, DeclarationWithProximity d) {
        return node.getScope()==d.getNamedArgumentList();
    }

    private static void addRefinementProposal(int offset, String prefix, final CeylonParseController cpc,
            Node node, List<ICompletionProposal> result, final Declaration d, IDocument doc) {
        if ((d.isDefault() || d.isFormal()) &&
                node.getScope() instanceof ClassOrInterface &&
                ((ClassOrInterface) node.getScope()).isInheritedFromSupertype(d)) {
        	boolean isInterface = node.getScope() instanceof Interface;
            ProducedReference pr = getRefinedProducedReference(node, d);
            //TODO: if it is equals() or hash, fill in the implementation
            result.add(new RefinementCompletionProposal(offset, prefix,  
                    getRefinementDescriptionFor(d, pr), 
                    getRefinementTextFor(d, pr, isInterface, "\n" + getIndent(node, doc)), 
                    cpc, d));
        }
    }
    
    public static ProducedReference getQualifiedProducedReference(Node node, Declaration d) {
        ProducedType pt = ((Tree.QualifiedMemberOrTypeExpression) node)
                    .getPrimary().getTypeModel();
        if (pt!=null && d.isClassOrInterfaceMember()) {
            pt = pt.getSupertype((TypeDeclaration)d.getContainer());
        }
        return d.getProducedReference(pt, Collections.<ProducedType>emptyList());
    }

    public static ProducedReference getRefinedProducedReference(Node node, Declaration d) {
        return refinedProducedReference(node.getScope().getDeclaringType(d), d);
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
            DeclarationWithProximity dwp, Declaration d, 
            OccurrenceLocation ol) {
    	result.add(new DeclarationCompletionProposal(offset, prefix,
    			getDescriptionFor(dwp, ol), getTextFor(dwp, ol), 
    			true, cpc, d, dwp.isUnimported()));
    }

    private static void addForProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, 
            OccurrenceLocation ol) {
        if (d instanceof Value || 
                d instanceof Getter || 
                d instanceof ValueParameter) {
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
                        "for (" + elemName + " in " + getDescriptionFor(dwp, ol) + ")", 
                        "for (" + elemName + " in " + getTextFor(dwp, ol) + ") {}",
                		true, cpc, d));
            }
        }
    }

    private static void addIfExistsProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, 
            OccurrenceLocation ol) {
    	if (!dwp.isUnimported()) {
    		if (d instanceof Value || 
    				d instanceof ValueParameter) {
    			TypedDeclaration v = (TypedDeclaration) d;
    			if (v.getType()!=null &&
    					d.getUnit().isOptionalType(v.getType()) && 
    					!v.isVariable()) {
    				result.add(new DeclarationCompletionProposal(offset, prefix, 
    						"if (exists " + getDescriptionFor(dwp, ol) + ")", 
    						"if (exists " + getTextFor(dwp, ol) + ") {}", 
    						true, cpc, d));
    			}
    		}
    	}
    }

    private static void addSwitchProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, 
            OccurrenceLocation ol, Node node, IDocument doc) {
    	if (!dwp.isUnimported()) {
    		if (d instanceof Value || 
    				d instanceof ValueParameter) {
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
    					body.append(pt.getProducedTypeName());
    					body.append(") {}\n");
    				}
    				body.append(indent);
    				result.add(new DeclarationCompletionProposal(offset, prefix, 
    						"switch (" + getDescriptionFor(dwp, ol) + ")", 
    						"switch (" + getTextFor(dwp, ol) + ")\n" + body, 
    						true, cpc, d));
    			}
    		}
    	}
    }

    private static void addNamedArgumentProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, 
            OccurrenceLocation ol) {
        result.add(new DeclarationCompletionProposal(offset, prefix, 
                getDescriptionFor(dwp, ol), 
                getTextFor(dwp, ol) + " = nothing;", 
                true, cpc, d));
    }

    private static void addInvocationProposals(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, ProducedReference pr, 
            OccurrenceLocation ol) {
        Declaration d = pr.getDeclaration();
        if (!(d instanceof Functional)) return;
        boolean isAbstractClass = d instanceof Class && ((Class) d).isAbstract();
        Functional fd = (Functional) d;
        List<ParameterList> pls = fd.getParameterLists();
        if (!pls.isEmpty()) {
            List<Parameter> ps = pls.get(0).getParameters();
            int defaulted = 0;
            for (Parameter p: ps) {
                if (p.isDefaulted()) {
                    defaulted ++;
                }
            }
            if (!isAbstractClass || ol==EXTENDS) {
                if (defaulted>0) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            getPositionalInvocationDescriptionFor(dwp, ol, pr, false), 
                            getPositionalInvocationTextFor(dwp, ol, pr, false), true,
                            cpc, d, dwp.isUnimported()));
                }
                result.add(new DeclarationCompletionProposal(offset, prefix, 
                        getPositionalInvocationDescriptionFor(dwp, ol, pr, true), 
                        getPositionalInvocationTextFor(dwp, ol, pr, true), true,
                        cpc, d, dwp.isUnimported()));
            }
            if (!isAbstractClass && ol!=EXTENDS && 
                    !fd.isOverloaded()) {
                //if there is more than one parameter, 
                //suggest a named argument invocation 
                if (defaulted>0 && ps.size()-defaulted>1) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            getNamedInvocationDescriptionFor(dwp, pr, false), 
                            getNamedInvocationTextFor(dwp, pr, false), true,
                            cpc, d, dwp.isUnimported()));
                }
                if (ps.size()>1) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            getNamedInvocationDescriptionFor(dwp, pr, true), 
                            getNamedInvocationTextFor(dwp, pr, true), true,
                            cpc, d, dwp.isUnimported()));
                }
            }
        }
    }

    protected static void addMemberNameProposal(int offset,
            Node node, List<ICompletionProposal> result) {
        String suggestedName = null;
        String prefix = "";
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
        	prefix = td.getIdentifier()==null ? 
        			"" : td.getIdentifier().getText();
        	suggestedName = prefix;
        	if (td.getType() instanceof Tree.SimpleType) {
        		String type = ((Tree.SimpleType) td.getType()).getIdentifier().getText();
                if (lower(type).startsWith(prefix)) {
                	result.add(new CompletionProposal(offset, prefix, null,
                			lower(type), escape(lower(type)), false));
                }
        		if (!suggestedName.endsWith(type)) {
        			suggestedName += type;
        		}
                result.add(new CompletionProposal(offset, prefix, null,
                		lower(suggestedName), escape(lower(suggestedName)), 
                		false));
        	}
        	return;
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
            result.add(new CompletionProposal(offset, "", null,
                    lower(suggestedName), escape(lower(suggestedName)), 
                    false));
        }
    }

	private static String lower(String suggestedName) {
		return Character.toLowerCase(suggestedName.charAt(0)) + 
				suggestedName.substring(1);
	}

	private static String escape(String suggestedName) {
		if (keywords.contains(suggestedName)) {
		    return "\\i" + suggestedName;
		}
		else {
		    return suggestedName;
		}
	}
    
    private static void addKeywordProposals(int offset, String prefix, 
            List<ICompletionProposal> result) {
        for (final String keyword: keywords) {
            if (!prefix.isEmpty() && keyword.startsWith(prefix) 
                    /*&& !keyword.equals(prefix)*/) {
                result.add(new CompletionProposal(offset, prefix, null, 
                		keyword, keyword, true) {
                	@Override
                	public StyledString getStyledDisplayString() {
                		return new StyledString(keyword, 
                				CeylonLabelProvider.KW_STYLER);
                	}
                });
            }
        }
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
                        ProducedType xtype = type(x.getDeclaration());
                        ProducedType ytype = type(y.getDeclaration());
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
                        return xName.compareTo(yName);
                    }
                });
        set.addAll(proposals.values());
        return set;
    }
    
    static ProducedType type(Declaration d) {
        if (d instanceof TypeDeclaration) {
            if (d instanceof Class) {
                if (!((Class) d).isAbstract()) {
                    return ((TypeDeclaration) d).getType();
                }
            }
            return null;
        }
        else if (d instanceof TypedDeclaration) {
            return ((TypedDeclaration) d).getType();
        }
        else {
            return null;//impossible
        }
    }
    
    static ProducedType fullType(Declaration d) {
    	//TODO: substitute type args from surrounding scope
        return d.getProducedReference(null, Collections.<ProducedType>emptyList()).getFullType();
    }
    
    public static Map<String, DeclarationWithProximity> getProposals(Node node, Tree.CompilationUnit cu) {
       return getProposals(node, "", false, cu); 
    }
    
    private static Map<String, DeclarationWithProximity> getProposals(Node node, String prefix,
            boolean memberOp, Tree.CompilationUnit cu) {
        if (node instanceof Tree.QualifiedMemberOrTypeExpression) {
            ProducedType type = getPrimaryType((Tree.QualifiedMemberOrTypeExpression) node);
            if (type!=null) {
                return type.resolveAliases().getDeclaration()
                		.getMatchingMemberDeclarations(prefix, 0);
            }
            else {
                return Collections.emptyMap();
            }
        } 
        else if (memberOp && node instanceof Tree.Term) {
            ProducedType type = ((Tree.Term)node).getTypeModel();
            if (type!=null) {
                return type.resolveAliases().getDeclaration()
                		.getMatchingMemberDeclarations(prefix, 0);
            }
            else {
                return Collections.emptyMap();
            }
        }
        else {
            Scope scope = node.getScope();
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
            return new TreeMap<String, DeclarationWithProximity>();
        }
        Unit unit = node.getUnit();
        if (unit == null) {
            return new TreeMap<String, DeclarationWithProximity>();
        }
        Package pkg = unit.getPackage();
        if (pkg == null) {
            return new TreeMap<String, DeclarationWithProximity>();
        }
        return pkg.getModule().getAvailableDeclarations(prefix);
    }
    
    private static boolean forceExplicitTypeArgs(Declaration d, OccurrenceLocation ol) {
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
                return pls.isEmpty() || pls.get(0).getParameters().isEmpty();
            }
            else {
                return false;
            }
        }
    }
    
    private static String name(DeclarationWithProximity d) {
        return name(d.getDeclaration(), d.getName());
    }
    
    private static String name(Declaration d) {
        return name(d, d.getName());
    }

    private static String name(Declaration d, String alias) {
        char c = alias.charAt(0);
        if (d instanceof TypedDeclaration &&
                (isUpperCase(c)||"object".equals(alias))) {
            return "\\i" + alias;
        }
        else if (d instanceof TypeDeclaration &&
                !(d instanceof Class && d.isAnonymous()) &&
                isLowerCase(c)) {
            return "\\I" + alias;
        }
        else {
            return alias;
        }
    }
    
    private static String getTextFor(DeclarationWithProximity d, 
            OccurrenceLocation ol) {
        StringBuilder result = new StringBuilder(name(d));
        if (ol!=IMPORT) appendTypeParameters(d.getDeclaration(), result);
        return result.toString();
    }
    
    private static String getPositionalInvocationTextFor(DeclarationWithProximity d,
            OccurrenceLocation ol, ProducedReference pr, boolean includeDefaulted) {
        StringBuilder result = new StringBuilder(name(d));
        if (forceExplicitTypeArgs(d.getDeclaration(), ol))
            appendTypeParameters(d.getDeclaration(), result);
        appendPositionalArgs(d.getDeclaration(), pr, result, includeDefaulted);
        return result.toString();
    }
    
    private static String getNamedInvocationTextFor(DeclarationWithProximity d, 
            ProducedReference pr, boolean includeDefaulted) {
        StringBuilder result = new StringBuilder(name(d));
        if (forceExplicitTypeArgs(d.getDeclaration(), null))
            appendTypeParameters(d.getDeclaration(), result);
        appendNamedArgs(d.getDeclaration(), pr, result, includeDefaulted, false);
        return result.toString();
    }
    
    private static String getDescriptionFor(DeclarationWithProximity d, 
            OccurrenceLocation ol) {
        StringBuilder result = new StringBuilder(d.getName());
        if (ol!=IMPORT) appendTypeParameters(d.getDeclaration(), result);
        return result.toString();
    }
    
    private static String getPositionalInvocationDescriptionFor(DeclarationWithProximity d, 
            OccurrenceLocation ol, ProducedReference pr, boolean includeDefaulted) {
        StringBuilder result = new StringBuilder(d.getName());
        if (forceExplicitTypeArgs(d.getDeclaration(), ol))
            appendTypeParameters(d.getDeclaration(), result);
        appendPositionalArgs(d.getDeclaration(), pr, result, includeDefaulted);
        return result.toString();
    }
    
    private static String getNamedInvocationDescriptionFor(DeclarationWithProximity d, 
            ProducedReference pr, boolean includeDefaulted) {
        StringBuilder result = new StringBuilder(d.getName());
        if (forceExplicitTypeArgs(d.getDeclaration(), null))
            appendTypeParameters(d.getDeclaration(), result);
        appendNamedArgs(d.getDeclaration(), pr, result, includeDefaulted, true);
        return result.toString();
    }
    
    public static String getRefinementTextFor(Declaration d, ProducedReference pr, 
    		boolean isInterface, String indent) {
        StringBuilder result = new StringBuilder("shared actual ");
        if (isVariable(d) && !isInterface) {
            result.append("variable ");
        }
        appendDeclarationText(d, pr, result);
        appendTypeParameters(d, result);
        appendParameters(d, pr, result);
        if (d instanceof Class) {
        	result.append(extraIndent(extraIndent(indent)))
        		.append(" extends super.").append(d.getName());
        	appendPositionalArgs(d, pr, result, true);
        }
        appendConstraints(d, pr, indent, result);
        appendImpl(d, isInterface, indent, result);
        return result.toString();
    }

	private static void appendConstraints(Declaration d, ProducedReference pr,
			String indent, StringBuilder result) {
		if (d instanceof Functional) {
        	for (TypeParameter tp: ((Functional) d).getTypeParameters()) {
        		List<ProducedType> sts = tp.getSatisfiedTypes();
        		if (!sts.isEmpty()) {
        			result.append(extraIndent(extraIndent(indent)))
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
        				result.append(st.substitute(pr.getTypeArguments()).getProducedTypeName());
        			}
        		}
        	}
        }
	}

    private static String getInlineFunctionTextFor(Parameter p, ProducedReference pr, 
            String indent) {
        StringBuilder result = new StringBuilder();
        appendNamedArgumentText(p, pr, result);
        appendTypeParameters(p, result);
        appendParameters(p, pr, result);
        appendImpl(p, false, indent, result);
        return result.toString();
    }

    private static boolean isVariable(Declaration d) {
        return d instanceof TypedDeclaration && ((TypedDeclaration) d).isVariable();
    }
        
    /*private static String getAttributeRefinementTextFor(Declaration d) {
        StringBuilder result = new StringBuilder();
        result.append("super.").append(d.getName())
            .append(" = ").append(d.getName()).append(";");
        return result.toString();
    }*/
    
    private static String getRefinementDescriptionFor(Declaration d, ProducedReference pr) {
        StringBuilder result = new StringBuilder("shared actual ");
        if (isVariable(d)) {
            result.append("variable ");
        }
        appendDeclarationText(d, pr, result);
        appendTypeParameters(d, result);
        appendParameters(d, pr, result);
        /*result.append(" - refine declaration in ") 
            .append(((Declaration) d.getContainer()).getName());*/
        return result.toString();
    }
    
    private static String getInlineFunctionDescriptionFor(Parameter p, ProducedReference pr) {
        StringBuilder result = new StringBuilder();
        appendNamedArgumentText(p, pr, result);
        appendTypeParameters(p, result);
        appendParameters(p, pr, result);
        /*result.append(" - refine declaration in ") 
            .append(((Declaration) d.getContainer()).getName());*/
        return result.toString();
    }
        
    public static String getDescriptionFor(Declaration d) {
        StringBuilder result = new StringBuilder();
        if (d!=null) {
            if (d.isFormal()) result.append("formal ");
            if (d.isDefault()) result.append("default ");
            appendDeclarationText(d, result);
            appendTypeParameters(d, result);
            appendParameters(d, result);
            /*result.append(" - refine declaration in ") 
                .append(((Declaration) d.getContainer()).getName());*/
        }
        return result.toString();
    }
    
    public static StyledString getStyledDescriptionFor(Declaration d) {
        StyledString result = new StyledString();
        if (d!=null) {
            if (d.isFormal()) result.append("formal ", ANN_STYLER);
            if (d.isDefault()) result.append("default ", ANN_STYLER);
            appendDeclarationText(d, result);
            appendTypeParameters(d, result);
            appendParameters(d, result);
            /*result.append(" - refine declaration in ") 
                .append(((Declaration) d.getContainer()).getName());*/
        }
        return result;
    }
    
    private static void appendPositionalArgs(Declaration d, ProducedReference pr, 
            StringBuilder result, boolean includeDefaulted) {
        if (d instanceof Functional) {
            List<Parameter> params = getParameters((Functional) d, includeDefaulted);
            if (params.isEmpty()) {
                result.append("()");
            }
            else {
                result.append("(");
                for (Parameter p: params) {
                    appendParameters(p, pr.getTypedParameter(p), result);
                    if (p instanceof FunctionalParameter) {
                        result.append(" => ");
                    }
                    result.append(p.getName()).append(", ");
                }
                result.setLength(result.length()-2);
                result.append(")");
            }
        }
    }

    private static List<Parameter> getParameters(Functional fd, boolean includeDefaults) {
        List<ParameterList> plists = fd.getParameterLists();
        if (plists==null || plists.isEmpty()) {
            return Collections.<Parameter>emptyList();
        }
        List<Parameter> pl = plists.get(0).getParameters();
        if (includeDefaults) {
            return pl;
        }
        else {
            List<Parameter> list = new ArrayList<Parameter>();
            for (Parameter p: pl) {
                if (!p.isDefaulted()) list.add(p);
            }
            return list;
        }
    }
    
    private static void appendNamedArgs(Declaration d, ProducedReference pr, 
            StringBuilder result, boolean includeDefaulted, 
            boolean descriptionOnly) {
        if (d instanceof Functional) {
            List<Parameter> params = getParameters((Functional) d, includeDefaulted);
            if (params.isEmpty()) {
                result.append(" {}");
            }
            else {
                result.append(" { ");
                for (Parameter p: params) {
                    if (!p.isSequenced()) {
                        if (p instanceof FunctionalParameter) {
                            result.append("function ").append(p.getName());
                            appendParameters(p, pr.getTypedParameter(p), result);
                            if (descriptionOnly) {
                            	result.append("; ");
                            }
                            else {
                            	result.append(" { return ")
	                            	//.append(CeylonQuickFixAssistant.defaultValue(p.getUnit(), p.getType()))
	                            	.append(p.getName())
	                            	.append("; } ");
                            }
                        }
                        else {
                            result.append(p.getName()).append(" = ")
                                //.append(CeylonQuickFixAssistant.defaultValue(p.getUnit(), p.getType()))
                                .append(p.getName())
                                .append("; ");
                        }
                    }
                }
                result.append("}");
            }
        }
    }
    
    private static void appendTypeParameters(Declaration d, StringBuilder result) {
        if (d instanceof Generic) {
            List<TypeParameter> types = ((Generic) d).getTypeParameters();
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
    
    private static void appendTypeParameters(Declaration d, StyledString result) {
        if (d instanceof Generic) {
            List<TypeParameter> types = ((Generic) d).getTypeParameters();
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
    
    private static void appendDeclarationText(Declaration d, StringBuilder result) {
        appendDeclarationText(d, null, result);
    }
    
    private static void appendDeclarationText(Declaration d, ProducedReference pr, 
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
            if (type!=null) {
                boolean isSequenced = (d instanceof Parameter) && 
                		((Parameter) d).isSequenced();
                if (pr!=null) {
                    type = type.substitute(pr.getTypeArguments());
                }
                if (isSequenced) {
                	type = d.getUnit().getIteratedType(type);
                }
                String typeName = type.getProducedTypeName();
                if (td instanceof Value && 
                        td.getTypeDeclaration().isAnonymous()) {
                    result.append("object");
                }
                else if (d instanceof Method || 
                        d instanceof FunctionalParameter) {
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
                	result.append("*");
                }
            }
        }
        result.append(" ").append(name(d));
    }
    
    private static void appendNamedArgumentText(Parameter p, ProducedReference pr, 
            StringBuilder result) {
        if (p instanceof FunctionalParameter) {
        	FunctionalParameter fp = (FunctionalParameter) p;
            result.append(fp.isDeclaredVoid() ? "void" : "function");
        }
        else {
            result.append("value");
        }
        result.append(" ").append(p.getName());
    }
    
    private static void appendDeclarationText(Declaration d, StyledString result) {
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
			if (type!=null) {
                boolean isSequenced = (d instanceof Parameter) && 
                		((Parameter) d).isSequenced();
                if (isSequenced) {
                	type = d.getUnit().getIteratedType(type);
                }
                String typeName = type.getProducedTypeName();
                if (td instanceof Value &&
                        td.getTypeDeclaration().isAnonymous()) {
                    result.append("object", KW_STYLER);
                }
                else if (d instanceof Method || 
                        d instanceof FunctionalParameter) {
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
        result.append(" ");
        if (d instanceof TypeDeclaration) {
            result.append(d.getName(), TYPE_STYLER);
        }
        else {
            result.append(d.getName(), ID_STYLER);
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
    
    private static void appendImpl(Declaration d, boolean isInterface, 
    		String indent, StringBuilder result) {
        if (d instanceof Method || d instanceof FunctionalParameter) {
            result.append(" {").append(extraIndent(indent));
            result.append(((Functional) d).isDeclaredVoid() ? "" : "return nothing; ");
            result.append("/* TODO auto-generated stub */");
            result.append(indent).append("}");
        }
        else if (d instanceof MethodOrValue) {
        	if (isInterface) {
        		result.append(" {" + extraIndent(indent) + "return nothing; /* TODO auto-generated stub */" + indent + "}");
        		if (isVariable(d)) {
        			result.append(indent + "assign " + d.getName() + " {" + extraIndent(indent) + " /* TODO auto-generated stub */" + indent + "}");
        		}
        	}
        	else {
        		result.append(" = nothing; /* TODO auto-generated stub */");
        	}
        }
        else if (d instanceof ValueParameter) {
            result.append(" {" + extraIndent(indent) + "return nothing;" + indent + "}");
        }
        else {
            //TODO: in the case of a class, formal member refinements!
            result.append(" {}");
        }
    }

    private static String extraIndent(String indent) {
        return indent.contains("\n") ?  indent + getDefaultIndent() : indent;
    }
    
    private static void appendParameters(Declaration d, StringBuilder result) {
        appendParameters(d, null, result);
    }
    
    private static void appendParameters(Declaration d, ProducedReference pr, 
            StringBuilder result) {
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
                            ProducedTypedReference ppr = pr==null ? 
                                    null : pr.getTypedParameter(p);
                            appendDeclarationText(p, ppr, result);
                            appendParameters(p, ppr, result);
                            /*ProducedType type = p.getType();
                            if (pr!=null) {
                                type = type.substitute(pr.getTypeArguments());
                            }
                            result.append(type.getProducedTypeName()).append(" ")
                                .append(p.getName());
                            if (p instanceof FunctionalParameter) {
                                result.append("(");
                                FunctionalParameter fp = (FunctionalParameter) p;
                                for (Parameter pp: fp.getParameterLists().get(0).getParameters()) {
                                    result.append(pp.getType().substitute(pr.getTypeArguments())
                                            .getProducedTypeName())
                                        .append(" ").append(pp.getName()).append(", ");
                                }
                                result.setLength(result.length()-2);
                                result.append(")");
                            }*/
                            if (p.isDefaulted()) result.append("=");
                            result.append(", ");
                        }
                        result.setLength(result.length()-2);
                        result.append(")");
                    }
                }
            }
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
                            appendDeclarationText(p, result);
                            appendParameters(p, result);
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
                            if (++i<len) result.append(", ");
                        }
                        result.append(")");
                    }
                }
            }
        }
    }
    
}
