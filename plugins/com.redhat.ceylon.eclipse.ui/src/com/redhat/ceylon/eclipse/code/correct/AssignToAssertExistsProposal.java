package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.eclipse.code.correct.LinkedModeCompletionProposal.getNameProposals;

import java.util.Collection;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.LinkedMode;
import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;

class AssignToAssertExistsProposal extends LocalProposal {

    protected DocumentChange createChange(IDocument document, Node expanse,
            int endIndex) {
        DocumentChange change = 
                new DocumentChange("Assign to Assert Exists", document);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(offset, "assert (exists " + initialName + " = "));

        String terminal = expanse.getEndToken().getText();
        if (!terminal.equals(";")) {
            change.addEdit(new InsertEdit(endIndex, ");"));
            exitPos = endIndex+12;
        }
        else {
            change.addEdit(new InsertEdit(endIndex-1, ")"));
            exitPos = endIndex+11;
        }
        return change;
    }
    
    public AssignToAssertExistsProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Node node, int currentOffset) {
        super(ceylonEditor, cu, node, currentOffset);
    }
    
    protected void addLinkedPositions(IDocument document, Unit unit)
            throws BadLocationException {
//        ProposalPosition typePosition = 
//        		new ProposalPosition(document, offset, 5, 1, 
//        				getSupertypeProposals(offset, unit, 
//        						type, true, "value"));
        
        ProposalPosition namePosition = 
        		new ProposalPosition(document, offset+15, initialName.length(), 0, 
        				getNameProposals(offset+15, 0, nameProposals));
        
//        LinkedMode.addLinkedPosition(linkedModeModel, typePosition);
        LinkedMode.addLinkedPosition(linkedModeModel, namePosition);
    }
    
    @Override
    String[] computeNameProposals(Node expression) {
        return super.computeNameProposals(expression);
    }
    
    @Override
    public String getDisplayString() {
        return "Assign expression to 'assert (exists)' assertion";
    }

    @Override
    boolean isEnabled(Type resultType) {
        return resultType!=null &&
                rootNode.getUnit().isOptionalType(resultType);
    }

    static void addAssignToAssertExistsProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node, int currentOffset) {
        AssignToAssertExistsProposal prop = 
                new AssignToAssertExistsProposal(ceylonEditor, cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}