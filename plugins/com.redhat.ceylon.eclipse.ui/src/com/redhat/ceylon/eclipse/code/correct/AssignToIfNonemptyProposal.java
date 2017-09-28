package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.correct.LinkedModeCompletionProposal.getNameProposals;

import java.util.Collection;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.Unit;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.util.LinkedMode;

class AssignToIfNonemptyProposal extends LocalProposal {

    protected DocumentChange createChange(IDocument document, Node expanse,
            int endIndex) {
        DocumentChange change = 
                new DocumentChange("Assign to If Nonempty", document);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(offset, "if (nonempty " + initialName + " = "));

        String terminal = expanse.getEndToken().getText();
        if (!terminal.equals(";")) {
            change.addEdit(new InsertEdit(endIndex, ") {}"));
            exitPos = endIndex+10;
        }
        else {
            change.addEdit(new ReplaceEdit(endIndex-1, 1, ") {}"));
            exitPos = endIndex+9;
        }
        return change;
    }
    
    public AssignToIfNonemptyProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
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
        		new ProposalPosition(document, offset+13, initialName.length(), 0, 
        				getNameProposals(offset+13, 0, nameProposals));
        
//        LinkedMode.addLinkedPosition(linkedModeModel, typePosition);
        LinkedMode.addLinkedPosition(linkedModeModel, namePosition);
    }
    
    @Override
    String[] computeNameProposals(Node expression) {
        return super.computeNameProposals(expression);
    }
    
    @Override
    public String getDisplayString() {
        return "Assign expression to 'if (nonempty)' condition";
    }

    @Override
    boolean isEnabled(Type resultType) {
        return resultType!=null &&
                rootNode.getUnit().isPossiblyEmptyType(resultType);
    }

    static void addAssignToIfNonemptyProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node, int currentOffset) {
        AssignToIfNonemptyProposal prop = 
                new AssignToIfNonemptyProposal(ceylonEditor, cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}