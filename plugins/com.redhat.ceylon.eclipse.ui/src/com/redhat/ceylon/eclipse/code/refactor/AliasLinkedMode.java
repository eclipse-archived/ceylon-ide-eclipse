package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_RENAME;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_RENAME_SELECT;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeLength;
import static com.redhat.ceylon.eclipse.util.Nodes.getNodeStartOffset;

import org.eclipse.jdt.internal.ui.refactoring.RefactoringExecutionHelper;
import org.eclipse.jdt.ui.refactoring.RefactoringSaveHelper;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.eclipse.util.Escaping;

public final class AliasLinkedMode
        extends RefactorLinkedMode {
        
    private final AliasRefactoring refactoring;
    protected LinkedPosition namePosition;
    protected LinkedPositionGroup linkedPositionGroup;
    
    public AliasLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = new AliasRefactoring(editor);
    }
    
    public static boolean useLinkedMode() {
        return EditorUtil.getPreferences()
                .getBoolean(LINKED_MODE_RENAME);
    }
    
    @Override
    protected boolean canStart() {
        return refactoring.isEnabled();
    }
        
    private boolean isEnabled() {
        String newName = getNewNameFromNamePosition();
        return !getInitialName().equals(newName) &&
                newName.matches("^\\w(\\w|\\d)*$") &&
                !Escaping.KEYWORDS.contains(newName);
    }

    @Override
    public void done() {
        if (isEnabled()) {
            try {
//                hideEditorActivity();
                setName(getNewNameFromNamePosition());
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
//            finally {
//                unhideEditorActivity();
//            }
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
        
        Node selectedNode = refactoring.getNode();
        namePosition = 
                new LinkedPosition(document, 
                    getNodeStartOffset(selectedNode), 
                    getNodeLength(selectedNode), 
                    0);
        linkedPositionGroup.addPosition(namePosition);
        
        int i=1;
        for (Tree.Type type: refactoring.getNodesToRename(rootNode)) {
            try {
                linkedPositionGroup.addPosition(
                        new LinkedPosition(document, 
                            getNodeStartOffset(type), 
                            getNodeLength(type), 
                            i++));
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String getName() {
        return "Alias"; //TODO but what!?
    }
    
    @Override
    protected void setName(String name) {
        refactoring.setNewName(name);
    }
    
    @Override
    protected String getActionName() {
        return PLUGIN_ID + ".action.createAlias";
    }
    
    @Override
    protected void openPreview() {
        new AliasRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return AliasLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new AliasWizard((AliasRefactoring) refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    @Override
    protected void openDialog() {
        new AliasRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return AliasLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    protected String getNewNameFromNamePosition() {
        try {
            return namePosition.getContent();
        }
        catch (BadLocationException e) {
            return getInitialName();
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
    
    @Override
    protected void enterLinkedMode(IDocument document, int exitSequenceNumber,
            int exitPosition) throws BadLocationException {
        super.enterLinkedMode(document, exitSequenceNumber, exitPosition);
        if (!EditorUtil.getPreferences()
                .getBoolean(LINKED_MODE_RENAME_SELECT)) {
            // by default, full word is selected; restore original selection
            editor.getCeylonSourceViewer()
                .setSelectedRange(originalSelection.x, 
                        originalSelection.y); 
        }
    }
    
    /*@Override
    protected void openPopup() {
        super.openPopup();
        getInfoPopup().getMenuManager()
                .addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new Separator());
                Action renameLocals = 
                        new Action("Rename Values And Functions", 
                                IAction.AS_CHECK_BOX) {
                    @Override
                    public void run() {
                        refactoring.setRenameValuesAndFunctions(isChecked());
                    }
                };
                renameLocals.setChecked(refactoring.isRenameValuesAndFunctions());
                renameLocals.setEnabled(refactoring.getDeclaration() instanceof TypeDeclaration);
                manager.add(renameLocals);
            }
        });
    }*/

//  private Image image= null;
//  private Label label= null;
    
//    private void hideEditorActivity() {
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
//    }
    
//    private void unhideEditorActivity() {
//        if (label != null)
//            label.dispose();
//        if (image != null)
//            image.dispose();
//    }
    
}
