package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_RENAME;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.LINKED_MODE_RENAME_SELECT;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.ui.IWorkbenchPartSite;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.util.escaping_;

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
        return CeylonPlugin.getPreferences()
                .getBoolean(LINKED_MODE_RENAME);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        DocumentChange change = 
                new DocumentChange(
                        "Introduce Type Alias", 
                        document);
        refactoring.renameInFile(change, null,
                editor.getParseController()
                .getLastCompilationUnit());
        EditorUtil.performChange(change);
        return 0;
    }
    
    @Override
    protected boolean canStart() {
        return refactoring.getEnabled();
    }
        
    @Override
    protected boolean forceSave() {
        return true;
    }
    
    private boolean isEnabled() {
        String newName = getNewNameFromNamePosition();
        return !getInitialName().equals(newName) &&
                newName.matches("^\\w(\\w|\\d)*$") &&
                !escaping_.get_().isKeyword(newName);
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
                    IWorkbenchPartSite site = 
                            editor.getSite();
                    new RefactoringExecutionHelper(
                            refactoring,
                            RefactoringStatus.WARNING,
                            RefactoringSaveHelper.SAVE_CEYLON_REFACTORING,
                            site.getShell(),
                            site.getWorkbenchWindow())
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
        return "Enter alias for " + refactoring.getCount() + 
                " occurrences of '" + getName() + 
                "' in current package '" + 
                getPackageName() + "' {0}";
    }

    private String getPackageName() {
        return this.editor.getParseController()
                .getLastCompilationUnit()
                .getUnit()
                .getPackage()
                .getQualifiedNameString();
    }
    
    private void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust,
            LinkedPositionGroup linkedPositionGroup) 
                    throws BadLocationException {
        
        Node selectedNode = refactoring.getNode();
        int insertedLength = refactoring.getInsertedLength();
        int insertedLocation = refactoring.getInsertedLocation();
        namePosition = 
                new LinkedPosition(document, 
                        refactoring.getAliasOffset(), 
                        refactoring.getAliasLength(), 
                        0);
        linkedPositionGroup.addPosition(namePosition);
        
        linkedPositionGroup.addPosition(
                new LinkedPosition(document, 
                    selectedNode.getStartIndex()+insertedLength, 
                    selectedNode.getDistance(), 
                    1));
        int i=2;
        for (Node type: refactoring.getNodesToRename(rootNode)) {
            try {
                Integer start = type.getStartIndex();
                Integer length = type.getDistance();
                if (start!=null && length!=null) {
                    int offset = 
                            start>=insertedLocation ? 
                                    start+insertedLength : 
                                    start;
                    linkedPositionGroup.addPosition(
                            new LinkedPosition(document, 
                                    offset, length, 
                                    i++));
                }
            } 
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        
    }

    @Override
    protected String getName() {
        return refactoring.getType().asString();
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
            public RefactoringWizard createWizard(
                    Refactoring refactoring) {
                return new AliasWizard((AliasRefactoring) 
                        refactoring) {
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
    protected void setupLinkedPositions(
            final IDocument document, final int adjust)
                    throws BadLocationException {
        linkedPositionGroup = new LinkedPositionGroup();
        addLinkedPositions(document, 
                editor.getParseController().getLastCompilationUnit(), 
                adjust, linkedPositionGroup);
        linkedModeModel.addGroup(linkedPositionGroup);
    }
    
    @Override
    protected void enterLinkedMode(IDocument document, 
            int exitSequenceNumber, int exitPosition) 
                    throws BadLocationException {
        super.enterLinkedMode(document, 
                exitSequenceNumber, exitPosition);
        if (!CeylonPlugin.getPreferences()
                .getBoolean(LINKED_MODE_RENAME_SELECT)) {
            // by default, full word is selected; restore original selection
            editor.getCeylonSourceViewer()
                .setSelectedRange(getOriginalSelection().x, 
                        getOriginalSelection().y); 
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
