package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.editor.CeylonSourceViewerConfiguration.LINKED_MODE_RENAME;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.jface.text.link.ILinkedModeListener.NONE;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.editors.text.EditorsUI;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer;

public final class RenameLinkedMode
        extends RefactorLinkedMode {
        
    private final RenameRefactoring refactoring;
    protected LinkedPosition namePosition;
    protected LinkedPositionGroup linkedPositionGroup;
    
    public RenameLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = new RenameRefactoring(editor);
    }
    
    public static boolean useLinkedMode() {
        return EditorsUI.getPreferenceStore()
                .getBoolean(LINKED_MODE_RENAME);
    }
    
    @Override
    public void start() {
        if (!refactoring.isEnabled()) return;
        editor.doSave(new NullProgressMonitor());
        saveEditorState();
        super.start();
    }
    
    private boolean isEnabled() {
        String newName = getNewNameFromNamePosition();
        return !getOriginalName().equals(newName) &&
                newName.matches("^\\w(\\w|\\d)*$") &&
                !CeylonTokenColorer.keywords.contains(newName);
    }

    @Override
    public void done() {
        if (isEnabled()) {
            try {
                hideEditorActivity();
                refactoring.setNewName(getNewNameFromNamePosition());
                revertChanges();
                if (isShowPreview()) {
                    openPreview();
                }
                else {
                    new RefactoringExecutionHelper(refactoring,
                            RefactoringStatus.WARNING,
                            RefactoringSaveHelper.SAVE_ALL,
                            editor.getSite().getShell(),
                            editor.getSite().getWorkbenchWindow())
                        .perform(false, true);
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                unhideEditorActivity();
            }
            super.done();
        }
        else {
            super.cancel();
        }
    }

    @Override
    public String getHintTemplate() {
        return "Enter new name for " + refactoring.getCount() + 
                " occurrences of '" + getName()  + "' {0}";
    }
    
    private void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust,
            LinkedPositionGroup linkedPositionGroup) 
                    throws BadLocationException {
        
        int offset = getIdentifyingNode(refactoring.getNode()).getStartIndex();
        namePosition = new LinkedPosition(document, offset, 
                getOriginalName().length(), 0);
        linkedPositionGroup.addPosition(namePosition);
        
        int i=1;
        for (Node node: refactoring.getNodesToRename(rootNode)) {
            Node identifyingNode = getIdentifyingNode(node);
            try {
                linkedPositionGroup.addPosition(new LinkedPosition(document, 
                        identifyingNode.getStartIndex(), 
                        identifyingNode.getText().length(), i++));
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        for (Region region: refactoring.getStringsToReplace(rootNode)) {
            try {
                linkedPositionGroup.addPosition(new LinkedPosition(document, 
                        region.getOffset(), 
                        region.getLength(), i++));
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String getName() {
        return refactoring.getDeclaration().getName();
    }
    
    @Override
    protected String getActionName() {
        return PLUGIN_ID + ".action.rename";
    }
    
    void enterDialogMode() {
        refactoring.setNewName(getNewNameFromNamePosition());
        revertChanges();
        linkedModeModel.exit(NONE);
    }
    
    void openPreview() {
        new RenameRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return RenameLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
                return new RenameWizard((AbstractRefactoring) refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    void openDialog() {
        new RenameRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return RenameLinkedMode.this.refactoring;
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
        linkedPositionGroup = new LinkedPositionGroup();
        addLinkedPositions(document, 
                editor.getParseController().getRootNode(), 
                adjust, linkedPositionGroup);
        linkedModeModel.addGroup(linkedPositionGroup);
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
