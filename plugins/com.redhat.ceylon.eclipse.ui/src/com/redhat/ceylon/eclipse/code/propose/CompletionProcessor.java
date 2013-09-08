package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_ACTIVATION_CHARS;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.InvocationExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.PositionalArgumentList;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class CompletionProcessor implements IContentAssistProcessor {
	
	private static final IContextInformation[] NO_CONTEXTS= new IContextInformation[0];
	static ICompletionProposal[] NO_COMPLETIONS= new ICompletionProposal[0];

    private CeylonContentProposer contentProposer;
    private ParameterContextValidator validator;
    private CeylonEditor editor;
    
    private boolean filter;
    private boolean returnedParamInfo;
    private int lastOffsetAcrossSessions=-1;
    private int lastOffset=-1;
    
    public void sessionStarted() {
    	filter = false;
    	lastOffset=-1;
    }
    
    public CompletionProcessor(CeylonEditor editor) {
        contentProposer= new CeylonContentProposer();
        this.editor=editor;
    }
    
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
    	if (offset!=lastOffsetAcrossSessions) {
			returnedParamInfo = false;
			filter = false;
		}
    	try {
			if (lastOffset>=0 && offset>0 && offset!=lastOffset &&
					!isIdentifierCharacter(viewer, offset)) {
				//user typed a whitespace char with an open
				//completions window, so close the window
				return NO_COMPLETIONS;
			}
		} 
    	catch (BadLocationException ble) {
			ble.printStackTrace();
    		return NO_COMPLETIONS;
		}
		if (offset==lastOffset) {
			filter = !filter;
		}
		lastOffset = offset;
		lastOffsetAcrossSessions = offset;
    	try {
    		System.out.println(returnedParamInfo);
    		ICompletionProposal[] contentProposals = contentProposer.getContentProposals(editor.getParseController(), 
    				offset, viewer, filter, returnedParamInfo);
    		if (contentProposals.length==1 && 
    				contentProposals[0] instanceof CeylonContentProposer.ParameterInfo) {
    			returnedParamInfo = true;
    		}
			return contentProposals;
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    		return NO_COMPLETIONS;
    	}
    }

	private boolean isIdentifierCharacter(ITextViewer viewer, int offset)
			throws BadLocationException {
		char ch = viewer.getDocument().get(offset-1, 1).charAt(0);
		return isLetter(ch) || isDigit(ch) || ch=='_' || ch=='.';
	}

    public IContextInformation[] computeContextInformation(final ITextViewer viewer, final int offset) {
    	final List<IContextInformation> infos = new ArrayList<IContextInformation>();
    	CeylonParseController cpc = editor.getParseController();
		cpc.parse(viewer.getDocument(), new NullProgressMonitor(), null);
    	cpc.getRootNode().visit(new Visitor() {
    		@Override
    		public void visit(InvocationExpression that) {
    			PositionalArgumentList pal = that.getPositionalArgumentList();
				if (pal!=null) {
					//TODO: should reuse logic for adjusting tokens
					//      from CeylonContentProposer!!
    				Integer start = pal.getStartIndex();
					Integer stop = pal.getStopIndex();
					if (start!=null && stop!=null && offset>start) { 
						String string = "";
						if (offset>stop) {
							try {
								string = viewer.getDocument().get(stop+1, offset-stop-1).trim();
							} 
							catch (BadLocationException e) {}
						}
						if (string.isEmpty()) {
	    					Tree.MemberOrTypeExpression mte = (Tree.MemberOrTypeExpression) that.getPrimary();
		    				Declaration declaration = mte.getDeclaration();
		    				if (declaration instanceof Functional) {
		    					List<ParameterList> pls = ((Functional) declaration).getParameterLists();
		    					if (!pls.isEmpty()) {
		    						infos.add(new ParameterContextInformation(declaration, 
		    								mte.getTarget(), pls.get(0), that.getStartIndex()));
		    					}
		    				}
    	                }
    				}
    			}
    			super.visit(that);
    		}
		});
        return infos.toArray(NO_CONTEXTS);
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        return editor.getPrefStore().getString(AUTO_ACTIVATION_CHARS).toCharArray();
    }

    public char[] getContextInformationAutoActivationCharacters() {
        return new char[]{',','('};
    }

    public IContextInformationValidator getContextInformationValidator() {
		if (validator == null) {
			validator= new ParameterContextValidator();
		}
        return validator;
    }

    public String getErrorMessage() {
        return "No completions available";
    }
    
}
