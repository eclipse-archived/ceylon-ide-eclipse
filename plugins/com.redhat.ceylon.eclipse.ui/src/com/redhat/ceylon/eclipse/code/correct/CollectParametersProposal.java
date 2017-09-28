package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.COMPOSITE_CHANGE;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.CollectParametersRefactoring;
import org.eclipse.ceylon.ide.eclipse.code.refactor.CollectParametersRefactoringAction;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;

class CollectParametersProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private final CeylonEditor editor;
        
    CollectParametersProposal(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return COMPOSITE_CHANGE;
    }

    @Override
    public String getDisplayString() {
        return "Collect selected parameters into new class";
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
        new CollectParametersRefactoringAction(editor).run();
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.collectParameters");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

    public static void add(Collection<ICompletionProposal> proposals,
            CeylonEditor editor) {
        CollectParametersRefactoring cpr = new CollectParametersRefactoring(editor);
        if (cpr.getEnabled()) {
            proposals.add(new CollectParametersProposal(editor));
        }
    }

}