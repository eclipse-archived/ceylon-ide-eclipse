package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.ui.CeylonResources.COMPOSITE_CHANGE;
import static com.redhat.ceylon.eclipse.util.Nodes.getContainer;

import java.util.Collection;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.MoveOutRefactoringAction;
import com.redhat.ceylon.eclipse.util.Highlights;

public class MoveOutProposal implements ICompletionProposal, ICompletionProposalExtension6 {

    private final MoveOutRefactoringAction action;
    private String name;
    
    public MoveOutProposal(CeylonEditor editor, Node node) {
        action = new MoveOutRefactoringAction(editor);
        if (node instanceof Tree.Declaration) {
            Declaration dec = ((Tree.Declaration) node).getDeclarationModel();
            if (dec!=null) {
                Tree.Declaration container = getContainer(editor.getParseController().getRootNode(), 
                        dec);
                if (container!=null) {
                    name = container.getDeclarationModel().getName();
                }
            }
        }
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
        return "Move out of '" + name + "'";
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), false);
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
        MoveOutProposal prop = new MoveOutProposal(editor, node);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}