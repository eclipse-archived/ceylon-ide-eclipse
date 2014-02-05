package com.redhat.ceylon.eclipse.code.correct;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.refactor.ExtractValueRefactoringAction;

public class ExtractValueProposal implements ICompletionProposal {

    private ExtractValueRefactoringAction action;
    
    public ExtractValueProposal(CeylonEditor editor) {
        action = new ExtractValueRefactoringAction(editor);
    }
    
    @Override
    public Point getSelection(IDocument doc) {
    	return null;
    }

    @Override
    public Image getImage() {
    	return CeylonLabelProvider.CHANGE;
    }

    @Override
    public String getDisplayString() {
    	return "Extract value";
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
        action.run();
    }
    
    boolean isEnabled() {
        return action.isEnabled();
    }
    
    public static void add(Collection<ICompletionProposal> proposals, 
            CeylonEditor editor, Node node) {
        if (node instanceof Tree.BaseMemberExpression) {
            Tree.Identifier id = ((Tree.BaseMemberExpression) node).getIdentifier();
            if (id==null || id.getToken().getType()==CeylonLexer.AIDENTIFIER) {
                return;
            }
        }
        ExtractValueProposal prop = new ExtractValueProposal(editor);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}