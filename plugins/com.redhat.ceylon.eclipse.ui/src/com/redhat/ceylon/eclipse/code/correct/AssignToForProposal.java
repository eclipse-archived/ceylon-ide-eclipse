package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.DestructureProposal.getItemProposals;
import static com.redhat.ceylon.eclipse.code.correct.DestructureProposal.getKeyProposals;
import static com.redhat.ceylon.eclipse.code.correct.LinkedModeCompletionProposal.getNameProposals;

import java.util.Collection;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.LinkedMode;
import com.redhat.ceylon.eclipse.util.Nodes;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;

class AssignToForProposal extends LocalProposal {

    protected DocumentChange createChange(IDocument document, Node expanse,
            int endIndex) {
        DocumentChange change = 
                new DocumentChange("Assign to For", document);
        change.setEdit(new MultiTextEdit());
        Unit unit = expanse.getUnit();
        String text;
        int adjust = 0;
        if (getEntryType(unit)==null) {
            text = "for (" + initialName + " in ";
        }
        else {
            text = "for (key -> item in ";
            adjust = 4;
        }
        change.addEdit(new InsertEdit(offset, text));

        String terminal = expanse.getEndToken().getText();
        if (!terminal.equals(";")) {
            change.addEdit(new InsertEdit(endIndex, ") {}"));
            exitPos = endIndex+3+adjust;
        }
        else {
            change.addEdit(new ReplaceEdit(endIndex-1, 1, ") {}"));
            exitPos = endIndex+2+adjust;
        }
        return change;
    }

    Type getEntryType(Unit unit) {
        Class ed = unit.getEntryDeclaration();
        Type est = unit.getIteratedType(type).getSupertype(ed);
        return est;
    }
    
    public AssignToForProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Node node, int currentOffset) {
        super(ceylonEditor, cu, node, currentOffset);
    }
    
    protected void addLinkedPositions(IDocument document, Unit unit)
            throws BadLocationException {
        Type entryType = getEntryType(unit);
        if (entryType==null) {
            ProposalPosition namePosition = 
            		new ProposalPosition(document, offset+5, initialName.length(), 0,
            				getNameProposals(offset+5, 0, nameProposals));
            LinkedMode.addLinkedPosition(linkedModeModel, namePosition);
        }
        else {
            ProposalPosition keyPosition = 
                    new ProposalPosition(document, offset+5, 3, 0,
                            getNameProposals(offset+5, 0, 
                                    getKeyProposals(unit, entryType)));
            LinkedMode.addLinkedPosition(linkedModeModel, keyPosition);
            ProposalPosition itemPosition = 
                    new ProposalPosition(document, offset+12, 4, 1,
                            getNameProposals(offset+5, 1, 
                                    getItemProposals(unit, entryType)));
            LinkedMode.addLinkedPosition(linkedModeModel, itemPosition);
        }
    }
    
    @Override
    String[] computeNameProposals(Node expression) {
        return Nodes.nameProposals(expression, true);
    }
    
    @Override
    public String getDisplayString() {
        return "Assign expression to 'for' loop";
    }
    
    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "com.redhat.ceylon.eclipse.ui.action.assignToFor");
        return new StyledString(getDisplayString())
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

    @Override
    boolean isEnabled(Type resultType) {
        return resultType!=null &&
                rootNode.getUnit().isIterableType(resultType);
    }

    static void addAssignToForProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node, int currentOffset) {
        AssignToForProposal prop = 
                new AssignToForProposal(ceylonEditor, cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}