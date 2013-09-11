package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IEditingSupport;
import org.eclipse.jface.text.IEditingSupportRegistry;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public abstract class AbstractRenameLinkedMode {

    private final class LinkedModeListener implements
			ILinkedModeListener {
		@Override
		public void left(LinkedModeModel model, int flags) {
		    if ((flags&ILinkedModeListener.UPDATE_CARET)!=0) {
		        done();
		    }
		    else {
		    	cancel();
		    }
		}

		@Override
		public void suspend(LinkedModeModel model) {
		    editor.setInLinkedMode(false);
		}

		@Override
		public void resume(LinkedModeModel model, int flags) {
		    editor.setInLinkedMode(true);
		}
	}

	private final class FocusEditingSupport implements IEditingSupport {
        public boolean ownsFocusShell() {
            if (infoPopup == null)
                return false;
            if (infoPopup.ownsFocusShell()) {
                return true;
            }

            Shell editorShell= editor.getSite().getShell();
            Shell activeShell= editorShell.getDisplay().getActiveShell();
            if (editorShell == activeShell)
                return true;
            return false;
        }

        public boolean isOriginator(DocumentEvent event, IRegion subjectRegion) {
            return false; //leave on external modification outside positions
        }
    }

    private final CeylonEditor editor;
    private final Node node;
    Declaration dec;

    private EnterAliasInformationPopup infoPopup;

    private Point originalSelection;
    private String originalName;

    protected LinkedPosition namePosition;
    private LinkedModeModel linkedModeModel;
    private LinkedPositionGroup linkedPositionGroup;
    private final FocusEditingSupport focusEditingSupport;
    
    public AbstractRenameLinkedMode(Node element, 
            Declaration dec, CeylonEditor editor) {
        this.editor = editor;
        node = element;
        focusEditingSupport = new FocusEditingSupport();
        this.dec = dec;
    }
    
    protected String getName(Node node) {
    	return dec.getName();
    }
    
    protected int init(Node node, IDocument document) {
    	return 0;
    }
    
    protected int getIdentifyingOffset(Node node) {
    	return getIdentifyingNode(node).getStartIndex();
    }
    
    public void start() {
        ISourceViewer viewer = editor.getCeylonSourceViewer();
        final IDocument document = viewer.getDocument();
        originalSelection = viewer.getSelectedRange();
        int offset= originalSelection.x;
        final int adjust = init(node, document);        
        originalName = getName(node);
                
        
        try {
            
            linkedPositionGroup = new LinkedPositionGroup();
            namePosition = new LinkedPosition(document, getIdentifyingOffset(node), 
            		originalName.length(), 0);
            linkedPositionGroup.addPosition(namePosition);
            
            addLinkedPositions(document, editor.getParseController().getRootNode(), 
            		adjust, linkedPositionGroup);

            linkedModeModel = new LinkedModeModel();
            linkedModeModel.addGroup(linkedPositionGroup);
            linkedModeModel.forceInstall();
            linkedModeModel.addLinkingListener(new LinkedModeListener());
            editor.setInLinkedMode(true);
            
            LinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
//            ui.setExitPosition(viewer, offset, 0, Integer.MAX_VALUE);
            ui.setExitPosition(viewer, offset, 0, LinkedPositionGroup.NO_STOP);
            ui.setExitPolicy(new DeleteBlockingExitPolicy(document));
            ui.enter();

//            viewer.setSelectedRange(fOriginalSelection.x, fOriginalSelection.y); // by default, full word is selected; restore original selection
            
            if (viewer instanceof IEditingSupportRegistry) {
                IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
                registry.register(focusEditingSupport);
            }

            infoPopup = new EnterAliasInformationPopup(editor, this);
			infoPopup.open();

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

	protected abstract void addLinkedPositions(IDocument document, 
			Tree.CompilationUnit rootNode, int adjust, 
			LinkedPositionGroup linkedPositionGroup);

    public void cancel() {
        if (linkedModeModel != null) {
            linkedModeModel.exit(ILinkedModeListener.NONE);
        }
        editor.setInLinkedMode(false);
        linkedModeLeft();
    }
    
    public void done() {
        if (linkedModeModel != null) {
            linkedModeModel.exit(ILinkedModeListener.NONE);
        }
        editor.setInLinkedMode(false);
        linkedModeLeft();
        editor.doSave(new NullProgressMonitor());
    }

    /*private void restoreFullSelection() {
        if (fOriginalSelection.y != 0) {
            int originalOffset= fOriginalSelection.x;
            LinkedPosition[] positions= fLinkedPositionGroup.getPositions();
            for (int i= 0; i < positions.length; i++) {
                LinkedPosition position= positions[i];
                if (! position.isDeleted() && position.includes(originalOffset)) {
                    fEditor.getCeylonSourceViewer().setSelectedRange(position.offset, position.length);
                    return;
                }
            }
        }
    }*/

    private void linkedModeLeft() {
        if (infoPopup != null) {
            infoPopup.close();
        }

        ISourceViewer viewer= editor.getCeylonSourceViewer();
        if (viewer instanceof IEditingSupportRegistry) {
            IEditingSupportRegistry registry= (IEditingSupportRegistry) viewer;
            registry.unregister(focusEditingSupport);
        }
        
        activateEditor();
    }

    private void activateEditor() {
        editor.getSite().getPage().activate(editor);
    }

    public boolean isCaretInLinkedPosition() {
        return getCurrentLinkedPosition() != null;
    }

    public LinkedPosition getCurrentLinkedPosition() {
        Point selection= editor.getCeylonSourceViewer().getSelectedRange();
        int start = selection.x;
        int end = start + selection.y;
        LinkedPosition[] positions = linkedPositionGroup.getPositions();
        for (int i= 0; i < positions.length; i++) {
            LinkedPosition position = positions[i];
            if (position.includes(start) && position.includes(end))
                return position;
        }
        return null;
    }
    
    protected String getOriginalName() {
		return originalName;
	}
    
    protected String getNewName() {
        try {
        	return namePosition.getContent();
        }
        catch (BadLocationException e) {
            return originalName;
        }
    }

    public boolean isEnabled() {
    	String newName = getNewName();
    	return !originalName.equals(newName) &&
    			newName.matches("^\\w(\\w|\\d)+$") &&
    			!CeylonTokenColorer.keywords.contains(newName);
    }
    
    public boolean isOriginalName() {
    	return originalName.equals(getNewName());
    }

	protected abstract String getHintTemplate();

}