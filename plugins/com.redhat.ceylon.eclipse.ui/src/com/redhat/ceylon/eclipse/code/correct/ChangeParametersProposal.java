package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.REORDER;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.ChangeParametersRefactoring;
import com.redhat.ceylon.eclipse.code.refactor.ChangeParametersRefactoringAction;

class ChangeParametersProposal implements ICompletionProposal,
		ICompletionProposalExtension6 {

    Node node;
    Declaration dec;
    CeylonEditor editor;
    IFile file;
    
    ChangeParametersProposal(IFile file, Node node, 
            Declaration dec, CeylonEditor editor) {
        this.node = node;
        this.dec = dec;
        this.file = file;
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return REORDER;
    }

    @Override
    public String getDisplayString() {
    	return "Change parameters of '" + dec.getName() + "'";
    }

    @Override
    public IContextInformation getContextInformation() {
    	return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
    	return null;
    }

    @Override
    public void apply(IDocument doc) {
    	new ChangeParametersRefactoringAction(editor).run();
    }
    
    @Override
	public StyledString getStyledDisplayString() {
		return CorrectionUtil.styleProposal(getDisplayString());
	}

	public static void add(Collection<ICompletionProposal> proposals,
			IFile file, CeylonEditor editor) {
	    ChangeParametersRefactoring cpr = new ChangeParametersRefactoring(editor);
		if (cpr.isEnabled()) {
			proposals.add(new ChangeParametersProposal(file, 
					cpr.getNode(), cpr.getDeclaration(), editor));
		}
	}

}