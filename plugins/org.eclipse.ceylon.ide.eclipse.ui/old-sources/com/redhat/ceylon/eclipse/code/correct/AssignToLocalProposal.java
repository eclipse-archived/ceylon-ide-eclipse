package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.code.correct.LinkedModeCompletionProposal.getNameProposals;
import static org.eclipse.ceylon.ide.eclipse.code.correct.LinkedModeCompletionProposal.getSupertypeProposals;

import java.util.Collection;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.util.LinkedMode;
import org.eclipse.ceylon.model.typechecker.model.Unit;

class AssignToLocalProposal extends LocalProposal {

    protected DocumentChange createChange(IDocument document, Node expanse,
            int endIndex) {
        DocumentChange change = 
                new DocumentChange("Assign to Local", document);
        change.setEdit(new MultiTextEdit());
        change.addEdit(new InsertEdit(offset, "value " + initialName + " = "));

        String terminal = expanse.getEndToken().getText();
        if (!terminal.equals(";")) {
            change.addEdit(new InsertEdit(endIndex, ";"));
            exitPos = endIndex+1;
        }
        else {
            exitPos = endIndex;
        }
        return change;
    }
    
    public AssignToLocalProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Node node, int currentOffset) {
        super(ceylonEditor, cu, node, currentOffset);
    }
    
    protected void addLinkedPositions(IDocument document, Unit unit)
            throws BadLocationException {
        
        LinkedModeImporter importer = new LinkedModeImporter(document, editor);
        linkedModeModel.addLinkingListener(importer);
        
        ProposalPosition typePosition = 
        		new ProposalPosition(document, offset, 5, 1, 
        				getSupertypeProposals(offset, unit, 
        						type, true, "value", importer));
        
        ProposalPosition namePosition = 
        		new ProposalPosition(document, offset+6, initialName.length(), 0, 
        				getNameProposals(offset, 1, nameProposals));
        
        LinkedMode.addLinkedPosition(linkedModeModel, typePosition);
        LinkedMode.addLinkedPosition(linkedModeModel, namePosition);
    }
    
    @Override
    public String getDisplayString() {
        return "Assign expression to new local";
    }
    
//    @Override
//    public Point getSelection(IDocument document) {
//        return new Point(exitPos, 0);
//    }
    
    @Override
    public StyledString getStyledDisplayString() {
        String hint = 
                CorrectionUtil.shortcut(
                        "org.eclipse.ceylon.ide.eclipse.ui.action.assignToLocal");
        return new StyledString(getDisplayString())
                .append(hint, StyledString.QUALIFIER_STYLER);
    }

    static void addAssignToLocalProposal(CeylonEditor ceylonEditor, Tree.CompilationUnit cu, 
            Collection<ICompletionProposal> proposals,
            Node node, int currentOffset) {
        AssignToLocalProposal prop = 
                new AssignToLocalProposal(ceylonEditor, cu, node, currentOffset);
        if (prop.isEnabled()) {
            proposals.add(prop);
        }
    }

}

