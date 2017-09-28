package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.REORDER;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isConstructor;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.ChangeParametersRefactoring;
import org.eclipse.ceylon.ide.eclipse.code.refactor.ChangeParametersRefactoringAction;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Scope;

class ChangeParametersProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private final Declaration dec;
    private final CeylonEditor editor;
        
    ChangeParametersProposal(Declaration dec, CeylonEditor editor) {
        this.dec = dec;
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
        String name = dec.getName();
        if (name == null && isConstructor(dec)) {
            Scope container = dec.getContainer();
            if (container instanceof Declaration) {
                Declaration cd = (Declaration) container;
                name = cd.getName();
            }
        }
        return "Change parameters of '" + name + "'";
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
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.changeParameters");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

    public static void add(Collection<ICompletionProposal> proposals,
            CeylonEditor editor) {
        ChangeParametersRefactoring cpr = new ChangeParametersRefactoring(editor);
        if (cpr.getEnabled()) {
            proposals.add(new ChangeParametersProposal(cpr.getDeclaration(), 
                    editor));
        }
    }

}