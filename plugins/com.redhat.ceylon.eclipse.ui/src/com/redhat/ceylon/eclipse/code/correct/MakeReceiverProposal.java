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

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.refactor.MakeReceiverRefactoringAction;
import com.redhat.ceylon.eclipse.util.Highlights;
import com.redhat.ceylon.model.typechecker.model.Declaration;

public class MakeReceiverProposal implements ICompletionProposal, ICompletionProposalExtension6 {

    private final MakeReceiverRefactoringAction action;
    private String name;
    
    public MakeReceiverProposal(CeylonEditor editor, Node node) {
        action = new MakeReceiverRefactoringAction(editor);
        if (node instanceof Tree.Declaration) {
            Tree.Declaration d = (Tree.Declaration) node;
            Declaration dec = d.getDeclarationModel();
            if (dec!=null) {
                Tree.CompilationUnit rootNode = 
                        editor.getParseController()
                            .getLastCompilationUnit();
                Tree.Declaration container = 
                        getContainer(rootNode, dec);
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
        return "Make receiver of '" + name + "'";
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "com.redhat.ceylon.eclipse.ui.action.makeReceiver");
        return Highlights.styleProposal(getDisplayString(), false)
                .append(hint, StyledString.QUALIFIER_STYLER);
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
        MakeReceiverProposal prop = new MakeReceiverProposal(editor, node);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}