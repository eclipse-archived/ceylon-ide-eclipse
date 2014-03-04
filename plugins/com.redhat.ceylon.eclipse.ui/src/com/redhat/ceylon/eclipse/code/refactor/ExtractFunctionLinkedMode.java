package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE_EXTRACT;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.jface.text.link.ILinkedModeListener.NONE;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public final class ExtractFunctionLinkedMode
            extends RefactorLinkedMode {
        
    private final ExtractFunctionRefactoring refactoring;
    
    public ExtractFunctionLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = new ExtractFunctionRefactoring(editor);
    }
    
    public static boolean useLinkedMode() {
        return EditorsUI.getPreferenceStore()
                .getBoolean(LINKED_MODE_EXTRACT);
    }
    
    @Override
    protected int init(IDocument document) {
        try {
            DocumentChange change = 
                    new DocumentChange("Extract Function", document);
            refactoring.extractInFile(change);
            change.perform(new NullProgressMonitor());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    
    @Override
    public void start() {
//        if (refactoring.forceWizardMode()) {
//            openDialog();
//        }
//        else {
            if (!refactoring.isEnabled()) return;
            editor.doSave(new NullProgressMonitor());
            saveEditorState();
            super.start();
//        }
    }
    
    @Override
    public void done() {
        if (isEnabled()) {
            refactoring.setNewName(getNewName());
            if (showPreview || refactoring.forceWizardMode()) {
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
    
    @Override
    public boolean isEnabled() {
        String newName = getNewName();
        return newName.matches("^\\w(\\w|\\d)*$") &&
                !CeylonTokenColorer.keywords.contains(newName);
    }
    
    @Override
    public String getHintTemplate() {
        return "Enter name for new function declaration {0}";
    }

    @Override
    protected int getIdentifyingOffset() {
        return refactoring.decRegion.getOffset();
    }
    
    @Override
    protected int getExitPosition(int selectionOffset, int adjust) {
        return refactoring.refRegion.getOffset();
    }
    
    @Override
    protected void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust,
            LinkedPositionGroup linkedPositionGroup) {
        try {
            linkedPositionGroup.addPosition(new LinkedPosition(document, 
                    refactoring.refRegion.getOffset(), 
                    refactoring.refRegion.getLength(), 1));
        } 
        catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void addAdditionalLinkedPositionGroups(IDocument document) {
        ProducedType type = refactoring.getType();
        if (!isTypeUnknown(type)) {
            List<ProducedType> supertypes = type.getSupertypes();
            int offset = refactoring.typeRegion.getOffset();
            int length = refactoring.typeRegion.getLength();
            addTypeProposals(document, supertypes, offset, length);
        }
    }
    
    @Override
    protected String getName() {
        return refactoring.getNewName();
    }
    
    @Override
    protected String getActionName() {
        return PLUGIN_ID + ".action.extractFunction";
    }
    
    void enterDialogMode() {
        refactoring.setNewName(getNewName());
        revertChanges();
        linkedModeModel.exit(NONE);
    }
    
    void openPreview() {
        new ExtractFunctionRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return ExtractFunctionLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
                return new ExtractFunctionWizard((ExtractFunctionRefactoring) refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    void openDialog() {
        new ExtractFunctionRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return ExtractFunctionLinkedMode.this.refactoring;
            }
        }.run();
    }
    
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

    protected Action createPreviewAction() {
        return new Action("Preview...") {
            @Override
            public void run() {
                enterDialogMode();
                openPreview();
            }
        };
    }
    
    @Override
    protected void updatePopupLocation() {
        LinkedPosition currentLinkedPosition = getCurrentLinkedPosition();
        if (currentLinkedPosition==null) {
            getInfoPopup().setHintTemplate(getHintTemplate());
        }
        else if (currentLinkedPosition.getSequenceNumber()==2) {
            getInfoPopup().setHintTemplate("Enter type for new function declaration {0}");
        }
        else {
            getInfoPopup().setHintTemplate("Enter name for new function declaration {0}");
        }
    }
    
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

}
