package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.RENAME;
import static com.redhat.ceylon.eclipse.code.refactor.RenameDeclarationLinkedMode.useLinkedMode;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.RenameDeclarationLinkedMode;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoring;
import com.redhat.ceylon.eclipse.code.refactor.RenameRefactoringAction;

class RenameDeclarationProposal implements ICompletionProposal,
        ICompletionProposalExtension6 {

    private final Declaration dec;
    private final CeylonEditor editor;
    
    RenameDeclarationProposal(Declaration dec, 
            CeylonEditor editor) {
        this.dec = dec;
        this.editor = editor;
    }
    
    @Override
    public Point getSelection(IDocument doc) {
        return null;
    }

    @Override
    public Image getImage() {
        return RENAME;
    }

    @Override
    public String getDisplayString() {
        return "Rename '" + dec.getName() + "'";
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
        if (useLinkedMode()) {
            new RenameDeclarationLinkedMode(editor).start();
        }
        else {
            new RenameRefactoringAction(editor).run();
        }
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        return CorrectionUtil.styleProposal(getDisplayString());
    }

    public static void add(Collection<ICompletionProposal> proposals,
            CeylonEditor editor) {
        RenameRefactoring rr = new RenameRefactoring(editor);
        if (rr.isEnabled()) {
            proposals.add(new RenameDeclarationProposal(rr.getDeclaration(), editor));
        }
    }

}