package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getNameProposals;

import java.util.Collection;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.LinkedMode;
import com.redhat.ceylon.eclipse.util.Nodes;

class AssignToForProposal extends LocalProposal {

    protected DocumentChange createChange(IDocument document, Node expanse,
            Integer stopIndex) {
        DocumentChange change = 
                new DocumentChange("Assign to For", document);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(offset, "for (" + initialName + " in "));

        String terminal = expanse.getEndToken().getText();
        if (!terminal.equals(";")) {
            change.addEdit(new InsertEdit(stopIndex+1, ") {}"));
            exitPos = stopIndex+4;
        }
        else {
            change.addEdit(new ReplaceEdit(stopIndex, 1, ") {}"));
            exitPos = stopIndex+3;
        }
        return change;
    }
    
    public AssignToForProposal(Tree.CompilationUnit cu, 
            Node node, int currentOffset) {
        super(cu, node, currentOffset);
    }
    
    protected void addLinkedPositions(IDocument document, Unit unit)
            throws BadLocationException {
//        ProposalPosition typePosition = 
//        		new ProposalPosition(document, offset, 5, 1, 
//        				getSupertypeProposals(offset, unit, 
//        						type, true, "value"));
        
        ProposalPosition namePosition = 
        		new ProposalPosition(document, offset+5, initialName.length(), 0, 
        				getNameProposals(offset+5, 0, nameProposals));
        
//        LinkedMode.addLinkedPosition(linkedModeModel, typePosition);
        LinkedMode.addLinkedPosition(linkedModeModel, namePosition);
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
    boolean isEnabled(ProducedType resultType) {
        return resultType!=null &&
                rootNode.getUnit().isIterableType(resultType);
    }

    static void addAssignToForProposal(Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node, int currentOffset) {
        AssignToForProposal prop = 
                new AssignToForProposal(cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}