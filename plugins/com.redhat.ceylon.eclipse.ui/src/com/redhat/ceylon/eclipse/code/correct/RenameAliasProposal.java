package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.RENAME;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

class RenameAliasProposal implements ICompletionProposal, 
        ICompletionProposalExtension6 {
    
    private final Tree.ImportMemberOrType node;
    private final Declaration dec;
    private final CeylonEditor editor;
    
    private RenameAliasProposal(Tree.ImportMemberOrType node, 
            Declaration dec, CeylonEditor editor) {
        this.node = node;
        this.dec = dec;
        this.editor = editor;
    }
    
    @Override
    public void apply(IDocument document) {
        new EnterAliasLinkedMode(node, dec, editor).start();
    }
    
    static void addRenameAliasProposal(Tree.ImportMemberOrType node,  
            Collection<ICompletionProposal> proposals, 
            Declaration dec, CeylonEditor editor) {
        proposals.add(new RenameAliasProposal(node, dec, editor));
    }

    @Override
    public StyledString getStyledDisplayString() {
        return CorrectionUtil.styleProposal(getDisplayString());
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
        return "Rename alias '" + node.getAlias().getIdentifier().getText() + 
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
