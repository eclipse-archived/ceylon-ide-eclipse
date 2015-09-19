package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.refactor.RenameLinkedMode.useLinkedMode;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.ADD_CORR;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.EnterAliasLinkedMode;
import com.redhat.ceylon.eclipse.code.refactor.EnterAliasRefactoringAction;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.model.typechecker.model.Declaration;

class UseAliasProposal implements ICompletionProposal, ICompletionProposalExtension6 {
    
    private final Declaration dec;
    private final CeylonEditor editor;
    
    private UseAliasProposal(Declaration dec, CeylonEditor editor) {
        this.dec = dec;
        this.editor = editor;
    }
    
    @Override
    public void apply(IDocument document) {
        if (useLinkedMode()) {
            new EnterAliasLinkedMode(editor).start();
        }
        else {
            new EnterAliasRefactoringAction(editor).run();
        }
    }
    
    static void addUseAliasProposal(Tree.ImportMemberOrType imt,  
            Collection<ICompletionProposal> proposals,
            CeylonEditor editor) {
        if (imt!=null) {
            Declaration dec = imt.getDeclarationModel();
            if (dec!=null && imt.getAlias()==null) {
                proposals.add(new UseAliasProposal(dec, editor));
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "com.redhat.ceylon.eclipse.ui.action.enterAlias");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Enter alias for '" + dec.getName() + "'";
    }

    @Override
    public Image getImage() {
        return ADD_CORR;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
}
