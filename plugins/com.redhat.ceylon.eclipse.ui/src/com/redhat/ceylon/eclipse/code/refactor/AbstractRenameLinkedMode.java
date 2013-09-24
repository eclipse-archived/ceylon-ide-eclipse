package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.text.correction.proposals.LinkedNamesAssistProposal.DeleteBlockingExitPolicy;
import org.eclipse.jface.action.IMenuManager;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewer;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public abstract class AbstractRenameLinkedMode {

    private final class LinkedModeListener implements
			ILinkedModeListener {
		@Override
		public void left(LinkedModeModel model, int flags) {
		    if ((flags&UPDATE_CARET)!=0) {
		        done();
		    }
		    else {
                if ((flags&EXTERNAL_MODIFICATION)==0) {
                	editor.getCeylonSourceViewer().invalidateTextPresentation();
                }
		    	cancel();
		    }
		}

		@Override
		public void suspend(LinkedModeModel model) {
		    editor.setLinkedMode(linkedModeModel);
		}

		@Override
		public void resume(LinkedModeModel model, int flags) {
		    editor.setLinkedMode(linkedModeModel);
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

    protected final CeylonEditor editor;
    
    private RenameInformationPopup infoPopup;

    private Point originalSelection;
    private String originalName;

    protected LinkedPosition namePosition;
    protected LinkedModeModel linkedModeModel;
    protected LinkedPositionGroup linkedPositionGroup;
    private final FocusEditingSupport focusEditingSupport;
    
    protected String openDialogKeyBinding= "";
    
    public AbstractRenameLinkedMode(CeylonEditor editor) {
        this.editor = editor;
        focusEditingSupport = new FocusEditingSupport();
    }
    
    protected abstract String getName();
    
    protected int init(IDocument document) {
    	return 0;
    }
    
    protected abstract int getIdentifyingOffset();
    
    public void start() {
        ISourceViewer viewer = editor.getCeylonSourceViewer();
        final IDocument document = viewer.getDocument();
        originalSelection = viewer.getSelectedRange();
        int offset= originalSelection.x;
        final int adjust = init(document);        
        originalName = getName();
                
        
        try {
            
            linkedPositionGroup = new LinkedPositionGroup();
            namePosition = new LinkedPosition(document, getIdentifyingOffset(), 
            		originalName.length(), 0);
            linkedPositionGroup.addPosition(namePosition);
            
            addLinkedPositions(document, editor.getParseController().getRootNode(), 
            		adjust, linkedPositionGroup);

            linkedModeModel = new LinkedModeModel();
            linkedModeModel.addGroup(linkedPositionGroup);
            linkedModeModel.forceInstall();
            linkedModeModel.addLinkingListener(new LinkedModeListener());
            editor.setLinkedMode(linkedModeModel);
            
            LinkedModeUI ui= new EditorLinkedModeUI(linkedModeModel, viewer);
//            ui.setExitPosition(viewer, offset, 0, Integer.MAX_VALUE);
            ui.setExitPosition(viewer, offset, 0, LinkedPositionGroup.NO_STOP);
            ui.setExitPolicy(new DeleteBlockingExitPolicy(document));
            ui.enter();

//            viewer.setSelectedRange(fOriginalSelection.x, fOriginalSelection.y); // by default, full word is selected; restore original selection
            
            if (viewer instanceof IEditingSupportRegistry) {
                ((IEditingSupportRegistry) viewer).register(focusEditingSupport);
            }

            // Must cache here, since editor context is not available in menu from popup shell:
            openDialogKeyBinding = getOpenDialogBinding();
            infoPopup = new RenameInformationPopup(editor, this);
			infoPopup.open();

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

	protected abstract void addLinkedPositions(IDocument document, 
			Tree.CompilationUnit rootNode, int adjust, 
			LinkedPositionGroup linkedPositionGroup);

    public void cancel() {
        linkedModeLeft();
    }
    
    public void done() {
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
    	CeylonSourceViewer viewer = editor.getCeylonSourceViewer();
        editor.setLinkedMode(null);

//        if (linkedModeModel != null) {
//            linkedModeModel.exit(ILinkedModeListener.NONE);
//            linkedModeModel = null;
//        }
                
        if (infoPopup != null) {
            infoPopup.close();
            infoPopup=null;
        }
        
        if (viewer instanceof IEditingSupportRegistry) {
            ((IEditingSupportRegistry) viewer).unregister(focusEditingSupport);
        }
        
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

	public abstract String getHintTemplate();

    /**
     * WARNING: only works in workbench window context!
     * @return the keybinding for Refactor &gt; Rename
     */
    private static String getOpenDialogBinding() {
        IBindingService bindingService= (IBindingService)PlatformUI.getWorkbench()
                .getAdapter(IBindingService.class);
        if (bindingService == null) return "";
        String binding= bindingService.getBestActiveBindingFormattedFor(PLUGIN_ID + ".action.rename");
        return binding == null ? "" : binding;
    }

    void addMenuItems(IMenuManager manager) {}
    
}