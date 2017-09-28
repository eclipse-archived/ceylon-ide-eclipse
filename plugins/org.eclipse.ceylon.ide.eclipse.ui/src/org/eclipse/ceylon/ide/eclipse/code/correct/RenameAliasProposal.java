package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.refactor.RenameLinkedMode.useLinkedMode;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.RENAME;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.refactor.EnterAliasLinkedMode;
import org.eclipse.ceylon.ide.eclipse.code.refactor.EnterAliasRefactoringAction;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

class RenameAliasProposal implements ICompletionProposal, 
        ICompletionProposalExtension6 {
    
    private final Tree.Alias alias;
    private final Declaration dec;
    private final CeylonEditor editor;
    
    private RenameAliasProposal(Tree.Alias alias, 
            Declaration dec, CeylonEditor editor) {
        this.alias = alias;
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

    static void addRenameAliasProposal(Tree.ImportMemberOrType imt,  
            Collection<ICompletionProposal> proposals, 
            CeylonEditor editor) {
        if (imt!=null) {
            Declaration dec = imt.getDeclarationModel();
            Tree.Alias a = imt.getAlias();
            if (dec!=null && a!=null) {
                proposals.add(new RenameAliasProposal(a, dec, editor));
            }
        }
    }

    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.enterAlias");
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
        return "Rename alias '" + 
                alias.getIdentifier().getText() + 
                "' of '" + dec.getName() + "'";
    }

    @Override
    public Image getImage() {
        return RENAME;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }
    
}
