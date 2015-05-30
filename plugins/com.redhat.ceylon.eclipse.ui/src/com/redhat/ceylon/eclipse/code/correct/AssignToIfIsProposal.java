package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.LinkedModeCompletionProposal.getCaseTypeProposals;
import static com.redhat.ceylon.eclipse.code.correct.LinkedModeCompletionProposal.getNameProposals;

import java.util.Collection;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.LinkedMode;

class AssignToIfIsProposal extends LocalProposal {

    protected DocumentChange createChange(IDocument document, Node expanse,
            Integer stopIndex) {
        DocumentChange change = 
                new DocumentChange("Assign to If Is", document);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(offset, "if (is Nothing " + initialName + " = "));

        String terminal = expanse.getEndToken().getText();
        if (!terminal.equals(";")) {
            change.addEdit(new InsertEdit(stopIndex+1, ") {}"));
            exitPos = stopIndex+13;
        }
        else {
            change.addEdit(new ReplaceEdit(stopIndex, 1, ") {}"));
            exitPos = stopIndex+12;
        }
        return change;
    }
    
    public AssignToIfIsProposal(Tree.CompilationUnit cu, 
            Node node, int currentOffset) {
        super(cu, node, currentOffset);
    }
    
    protected void addLinkedPositions(IDocument document, Unit unit)
            throws BadLocationException {
        
        ProposalPosition typePosition = 
        		new ProposalPosition(document, offset+7, 7, 1,
        		        getCaseTypeProposals(offset+7, unit, type));
        
        ProposalPosition namePosition = 
        		new ProposalPosition(document, offset+15, initialName.length(), 0, 
        				getNameProposals(offset+15, 2, nameProposals));
        
        LinkedMode.addLinkedPosition(linkedModeModel, typePosition);
        LinkedMode.addLinkedPosition(linkedModeModel, namePosition);
    }
    
    @Override
    String[] computeNameProposals(Node expression) {
        return super.computeNameProposals(expression);
    }
    
    @Override
    public String getDisplayString() {
        return "Assign expression to 'if (is)' condition";
    }

    @Override
    boolean isEnabled(Type resultType) {
        return true;
    }

    static void addAssignToIfIsProposal(Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node, int currentOffset) {
        AssignToIfIsProposal prop = 
                new AssignToIfIsProposal(cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}