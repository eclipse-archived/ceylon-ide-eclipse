package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE_RENAME;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.complete.LinkedModeCompletionProposal;
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
    
//  private Image image= null;
//  private Label label= null;
    
    protected abstract int getIdentifyingOffset();
    
    protected void addNamePosition(IDocument document, int offset2, int length) {
        linkedPositionGroup = new LinkedPositionGroup();
        int offset1 = getIdentifyingOffset();
        namePosition = new LinkedPosition(document, offset1, 
                getOriginalName().length(), 0);
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
            List<ProducedType> supertypes, 
            int offset, int length) {
        Unit unit = editor.getParseController().getRootNode().getUnit();
        ICompletionProposal[] proposals = 
                new ICompletionProposal[supertypes.size()];
        for (int i=0; i<supertypes.size(); i++) {
            ProducedType type = supertypes.get(i);
            String typeName = type.getProducedTypeName(unit);
            proposals[i] = new LinkedModeCompletionProposal(offset, typeName, 0,
                    getImageForDeclaration(type.getDeclaration()));
        }
        ProposalPosition linkedPosition = 
                new ProposalPosition(document, offset, length, 1, proposals);
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
    protected void setupLinkedPositions(final IDocument document, final int adjust)
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
            getInfoPopup().setHintTemplate("Enter type for extracted " + getKind() + 
                    " declaration {0}");
        }
        else {
            getInfoPopup().setHintTemplate("Enter name for extracted " + getKind() + 
                    " declaration {0}");
        }
    }

    protected abstract String getKind();
    
}
