package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.styleProposal;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension4;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.editor.EditorUtil;


public class CompletionProposal implements ICompletionProposal, 
        /*ICompletionProposalExtension,*/ ICompletionProposalExtension4, 
        ICompletionProposalExtension6 {
    
    protected final String text;
    private final Image image;
    protected final String prefix;
    private final String description;
    protected int offset;
    
    private IEditingSupport editingSupport;
    
    CompletionProposal(int offset, String prefix, Image image,
            String desc, String text) {
        this.text=text;
        this.image = image;
        this.offset = offset;
        this.prefix = prefix;
        this.description = desc;
        Assert.isNotNull(description);
    }
    
    @Override
    public Image getImage() {
        return image;
    }
    
    @Override
    public Point getSelection(IDocument document) {
        return new Point(offset + text.length() - prefix.length(), 0);

    }
    
    public void apply(IDocument document) {
        try {
            document.replace(offset-prefix.length(), 
                    prefix.length(), text);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    public String getDisplayString() {
        return description;
    }

    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public boolean isAutoInsertable() {
    	return true;
    }

	@Override
	public StyledString getStyledDisplayString() {
		StyledString result = new StyledString();
		styleProposal(result, getDisplayString());
		return result;
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}
	
    private final class ProposalLinkedModeListener implements
            ILinkedModeListener {
        private final CeylonEditor editor;
        
        ProposalLinkedModeListener(CeylonEditor editor) {
            this.editor = editor;
        }
        
        @Override
        public void left(LinkedModeModel model, int flags) {
            editor.clearLinkedMode();
            //linkedModeModel.exit(ILinkedModeListener.NONE);
            CeylonSourceViewer viewer= editor.getCeylonSourceViewer();
            if (viewer instanceof IEditingSupportRegistry) {
                ((IEditingSupportRegistry) viewer).unregister(editingSupport);
            }
            editor.getSite().getPage().activate(editor);
            if ((flags&EXTERNAL_MODIFICATION)==0 && viewer!=null) {
                viewer.invalidateTextPresentation();
            }
        }
        
        @Override
        public void suspend(LinkedModeModel model) {
            editor.clearLinkedMode();
        }
        
        @Override
        public void resume(LinkedModeModel model, int flags) {
            editor.setLinkedMode(model, this);
        }
    }

    void addLinkedPosition(final LinkedModeModel linkedModeModel,
            ProposalPosition linkedPosition) 
                    throws BadLocationException {
        LinkedPositionGroup linkedPositionGroup = new LinkedPositionGroup();
        linkedPositionGroup.addPosition(linkedPosition);
        linkedModeModel.addGroup(linkedPositionGroup);
    }

    void installLinkedMode(IDocument document, 
            LinkedModeModel linkedModeModel, 
            int exitSequenceNumber, int exitPosition)
                    throws BadLocationException {
        linkedModeModel.forceInstall();
        CeylonEditor editor = (CeylonEditor) EditorUtil.getCurrentEditor();
        linkedModeModel.addLinkingListener(new ProposalLinkedModeListener(editor));
        editor.setLinkedMode(linkedModeModel, this);
        CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        EditorLinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
        ui.setExitPosition(viewer, exitPosition, 0, exitSequenceNumber);
        ui.setExitPolicy(new DeleteBlockingExitPolicy(document));
        ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
        ui.setDoContextInfo(true);
        ui.enter();
        
        registerEditingSupport(editor, viewer);
    }

    private void registerEditingSupport(final CeylonEditor editor,
            CeylonSourceViewer viewer) {
        if (viewer instanceof IEditingSupportRegistry) {
            editingSupport = new IEditingSupport() {
                public boolean ownsFocusShell() {
                    Shell editorShell= editor.getSite().getShell();
                    Shell activeShell= editorShell.getDisplay().getActiveShell();
                    if (editorShell == activeShell)
                        return true;
                    return false;
                }
                public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
                    return false; //leave on external modification outside positions
                }
            };
            ((IEditingSupportRegistry) viewer).register(editingSupport);
        }
    }
    
}