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
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.LinkedMode;

class AssignToIfExistsProposal extends LocalProposal {

    protected DocumentChange createChange(IDocument document, Node expanse,
            int endIndex) {
        DocumentChange change = 
                new DocumentChange("Assign to If Exists", document);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(offset, "if (exists " + initialName + " = "));

        String terminal = expanse.getEndToken().getText();
        if (!terminal.equals(";")) {
            change.addEdit(new InsertEdit(endIndex, ") {}"));
            exitPos = endIndex+8;
        }
        else {
            change.addEdit(new ReplaceEdit(endIndex-1, 1, ") {}"));
            exitPos = endIndex+7;
        }
        return change;
    }
    
    public AssignToIfExistsProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
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
        		new ProposalPosition(document, offset+11, initialName.length(), 0, 
        				getNameProposals(offset+11, 0, nameProposals));
        
//        LinkedMode.addLinkedPosition(linkedModeModel, typePosition);
        LinkedMode.addLinkedPosition(linkedModeModel, namePosition);
    }
    
    @Override
    String[] computeNameProposals(Node expression) {
        return super.computeNameProposals(expression);
    }
    
    @Override
    public String getDisplayString() {
        return "Assign expression to 'if (exists)' condition";
    }

    @Override
    boolean isEnabled(Type resultType) {
        return resultType!=null &&
                rootNode.getUnit().isOptionalType(resultType);
    }

    static void addAssignToIfExistsProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node, int currentOffset) {
        AssignToIfExistsProposal prop = 
                new AssignToIfExistsProposal(ceylonEditor, cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}