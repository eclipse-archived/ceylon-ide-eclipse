package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getNameProposals;
import static com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal.getSupertypeProposals;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE_RENAME;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public abstract class ExtractLinkedMode extends RefactorLinkedMode {

    protected LinkedPosition namePosition;
    protected LinkedPositionGroup linkedPositionGroup;

    public ExtractLinkedMode(CeylonEditor editor) {
        super(editor);
    }
    
    @Override
    public void done() {
        if (isEnabled()) {
            setName(getNewNameFromNamePosition());
            if (isShowPreview() || forceWizardMode()) {
                try {
                    revertChanges();
                    openPreview();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.done();
        }
        else {
            super.cancel();
        }
    }
    
    protected abstract boolean forceWizardMode();

    public boolean isEnabled() {
        String newName = getNewNameFromNamePosition();
        return newName.matches("^\\w(\\w|\\d)*$") &&
                !CeylonTokenColorer.keywords.contains(newName);
    }
    
    protected abstract int getNameOffset();
    protected abstract int getTypeOffset();
    
    protected void addNamePosition(IDocument document, 
            int offset2, int length) {
        linkedPositionGroup = new LinkedPositionGroup();
        namePosition =
                new ProposalPosition(document, getNameOffset(), 
                        getOriginalName().length(), 0,
                        getNameProposals(getTypeOffset(), 1, 
                                getOriginalName()));
        try {
            linkedPositionGroup.addPosition(namePosition);
            linkedPositionGroup.addPosition(new LinkedPosition(document, 
                    offset2, length, 2));
            linkedModeModel.addGroup(linkedPositionGroup);
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    protected void addTypePosition(IDocument document,
            ProducedType type, int offset, int length) {
        Unit unit = editor.getParseController().getRootNode().getUnit();
        ProposalPosition linkedPosition = 
                new ProposalPosition(document, offset, length, 1, 
                        getSupertypeProposals(offset, unit, type));
        try {
            addLinkedPosition(linkedModeModel, linkedPosition);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    protected abstract void addLinkedPositions(IDocument document, 
            Tree.CompilationUnit rootNode, int adjust);

    @Override
    protected String getNewNameFromNamePosition() {
        try {
            return namePosition.getContent();
        }
        catch (BadLocationException e) {
            return getOriginalName();
        }
    }

    @Override
    protected void setupLinkedPositions(IDocument document, int adjust)
            throws BadLocationException {
        
        addLinkedPositions(document, 
                editor.getParseController().getRootNode(), 
                adjust);
    }

    public static boolean useLinkedMode() {
        return EditorsUI.getPreferenceStore()
                .getBoolean(LINKED_MODE_RENAME);
    }
    
    @Override
    public final String getHintTemplate() {
        return "Enter name for extracted " + getKind() +
                " declaration {0}";
    }

    @Override
    protected final void updatePopupLocation() {
        LinkedPosition currentLinkedPosition = getCurrentLinkedPosition();
        if (currentLinkedPosition==null) {
            getInfoPopup().setHintTemplate(getHintTemplate());
        }
        else if (currentLinkedPosition.getSequenceNumber()==1) {
            getInfoPopup().setHintTemplate("Enter type for extracted " + 
                    getKind() + " declaration {0}");
        }
        else {
            getInfoPopup().setHintTemplate("Enter name for extracted " + 
                    getKind() + " declaration {0}");
        }
    }

    protected abstract String getKind();
    
}
