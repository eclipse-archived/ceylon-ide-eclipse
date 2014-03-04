package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE_RENAME;
import static com.redhat.ceylon.eclipse.code.editor.EditorUtil.addLinkedPosition;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.jface.text.link.ILinkedModeListener.NONE;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
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
    public void start() {
        editor.doSave(new NullProgressMonitor());
        saveEditorState();
        super.start();
    }
    
    @Override
    public void done() {
        if (isEnabled()) {
            setName(getNewNameFromNamePosition());
            if (isShowPreview() || forceWizardMode()) {
                try {
                    hideEditorActivity();
                    revertChanges();
                    openPreview();
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    unhideEditorActivity();
                }
            }
            super.done();
        }
        else {
            super.cancel();
        }
    }
    
    protected abstract boolean forceWizardMode();

    protected abstract void setName(String newName);

    public boolean isEnabled() {
        String newName = getNewNameFromNamePosition();
        return newName.matches("^\\w(\\w|\\d)*$") &&
                !CeylonTokenColorer.keywords.contains(newName);
    }
    
    void enterDialogMode() {
        setName(getNewNameFromNamePosition());
        revertChanges();
        linkedModeModel.exit(NONE);
    }
    
    @Override
    protected Action createOpenDialogAction() {
        return new Action("Open Dialog..." + '\t' + 
                openDialogKeyBinding) {
            @Override
            public void run() {
                enterDialogMode();
                openDialog();
            }
        };
    }
    
    @Override
    protected Action createPreviewAction() {
        return new Action("Preview...") {
            @Override
            public void run() {
                enterDialogMode();
                openPreview();
            }
        };
    }
    
    abstract void openPreview();
    abstract void openDialog();
    
//  private Image image= null;
//  private Label label= null;
    
    private void hideEditorActivity() {
//      if (viewer instanceof SourceViewer) {
//      final SourceViewer sourceViewer= (SourceViewer) viewer;
//      Control viewerControl= sourceViewer.getControl();
//      if (viewerControl instanceof Composite) {
//          Composite composite= (Composite) viewerControl;
//          Display display= composite.getDisplay();
//
//          // Flush pending redraw requests:
//          while (! display.isDisposed() && display.readAndDispatch()) {
//          }
//
//          // Copy editor area:
//          GC gc= new GC(composite);
//          Point size;
//          try {
//              size= composite.getSize();
//              image= new Image(gc.getDevice(), size.x, size.y);
//              gc.copyArea(image, 0, 0);
//          } finally {
//              gc.dispose();
//              gc= null;
//          }
//
//          // Persist editor area while executing refactoring:
//          label= new Label(composite, SWT.NONE);
//          label.setImage(image);
//          label.setBounds(0, 0, size.x, size.y);
//          label.moveAbove(null);
//      }
    }
    
    private void unhideEditorActivity() {
//        if (label != null)
//            label.dispose();
//        if (image != null)
//            image.dispose();
    }
    
    protected abstract int getIdentifyingOffset();
    
    protected void addNamePosition(IDocument document, int offset2, int length) {
        linkedPositionGroup = new LinkedPositionGroup();
        int offset1 = getIdentifyingOffset();
        namePosition = new LinkedPosition(document, offset1, 
                getOriginalName().length(), 0);
        try {
            linkedPositionGroup.addPosition(namePosition);
            linkedPositionGroup.addPosition(new LinkedPosition(document, 
                    offset2, length, 1));
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
                new ProposalPosition(document, offset, length, 2, proposals);
        try {
            addLinkedPosition(linkedModeModel, linkedPosition);
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    protected abstract void addLinkedPositions(IDocument document, 
            Tree.CompilationUnit rootNode, int adjust);

    private String getNewNameFromNamePosition() {
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
    
}
