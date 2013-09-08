package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.AUTO_ACTIVATION_CHARS;
import static java.lang.Character.isDigit;
import static java.lang.Character.isLetter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ContextInformationValidator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.InvocationExpression;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class CompletionProcessor implements IContentAssistProcessor {
	
	private final IContextInformation[] NO_CONTEXTS= new IContextInformation[0];
	private ICompletionProposal[] NO_COMPLETIONS= new ICompletionProposal[0];

    private CeylonContentProposer contentProposer;
    
    private CeylonEditor editor;
    
    private boolean filter;
    private int lastOffset=-1;
    
    public void sessionStarted() {
    	filter = false;
    	lastOffset=-1;
    }

    // private HippieProposalProcessor hippieProcessor= new HippieProposalProcessor();

    public CompletionProcessor(CeylonEditor editor) {
        contentProposer= new CeylonContentProposer();
        this.editor=editor;
    }

    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
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
    	try {
    		return contentProposer.getContentProposals(editor.getParseController(), 
    				offset, viewer, filter);
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

    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
    	final List<IContextInformation> infos = new ArrayList<IContextInformation>();
    	editor.getParseController().getRootNode().visit(new Visitor() {
    		@Override
    		public void visit(InvocationExpression that) {
    			Tree.MemberOrTypeExpression mte = (Tree.MemberOrTypeExpression) that.getPrimary();
    			String text = CeylonContentProposer.getPositionalInvocationTextFor(
    					new DeclarationWithProximity(mte.getDeclaration(), 0), 
    					OccurrenceLocation.EXPRESSION, mte.getTarget(), true);
    			String str = text.substring(text.indexOf('(')+1, text.indexOf(')'));
    			infos.add(new ContextInformation(text, str));
    			super.visit(that);
    		}
		});
        return infos.toArray(NO_CONTEXTS);
    }

    public char[] getCompletionProposalAutoActivationCharacters() {
        return editor.getPrefStore().getString(AUTO_ACTIVATION_CHARS).toCharArray();
    }

    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    public IContextInformationValidator getContextInformationValidator() {
        return new ContextInformationValidator(this);
    }

    public String getErrorMessage() {
        return "No completions available";
    }
    
}
