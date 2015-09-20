package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.refactorJ2C.newExtractValueRefactoring;
import static com.redhat.ceylon.eclipse.code.refactor.refactorJ2C.toExtractLinkedModeEnabled;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.refactoring.ExtractValueRefactoring;
import com.redhat.ceylon.model.typechecker.model.Type;

public final class ExtractValueLinkedMode 
        extends ExtractLinkedMode {
        
    private final ExtractValueRefactoring refactoring;
    
    public ExtractValueLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = newExtractValueRefactoring(editor);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        DocumentChange change = 
                new DocumentChange("Extract Value", 
                        document);
        toExtractLinkedModeEnabled(refactoring)
            .extractInFile(change);
        EditorUtil.performChange(change);
        return 0;
    }
    
    @Override
    protected boolean canStart() {
        return refactoring.getEnabled();
    }
    
    @Override
    protected int getNameOffset() {
        return toExtractLinkedModeEnabled(refactoring).getDecRegion().getOffset();
    }
    
    @Override
    protected int getTypeOffset() {
        return toExtractLinkedModeEnabled(refactoring).getTypeRegion().getOffset();
    }
    
    @Override
    protected int getExitPosition(int selectionOffset, int adjust) {
        return toExtractLinkedModeEnabled(refactoring).getRefRegion().getOffset();
    }
    
    @Override
    protected String[] getNameProposals() {
    	return toExtractLinkedModeEnabled(refactoring).getNameProposals();
    }
    
    @Override
    protected void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust) {
        
        addNamePosition(document, 
                toExtractLinkedModeEnabled(refactoring).getRefRegion().getOffset(),
                toExtractLinkedModeEnabled(refactoring).getRefRegion().getLength());
        
        Type type = refactoring.getType();
        if (!isTypeUnknown(type)) {
            addTypePosition(document, type, 
                    toExtractLinkedModeEnabled(refactoring).getTypeRegion().getOffset(), 
                    toExtractLinkedModeEnabled(refactoring).getTypeRegion().getLength());
        }
        
    }
    
    @Override
    protected String getName() {
        return refactoring.getNewName();
    }
    
    @Override
    protected void setName(String name) {
        refactoring.setNewName(name);
    }
    
    @Override
    protected boolean forceWizardMode() {
        return refactoring.forceWizardMode();
    }
    
    @Override
    protected String getActionName() {
        return PLUGIN_ID + ".action.extractValue";
    }
    
    @Override
    protected void openPreview() {
        new RenameRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) ExtractValueLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new ExtractValueWizard(refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    @Override
    protected void openDialog() {
        new ExtractValueRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) ExtractValueLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    public boolean canBeInferred() {
        return refactoring.getCanBeInferred();
    }
    
    @Override
    protected String getKind() {
        return refactoring.getIsFunction() ? "function" : "value";
    }

    public static void selectExpressionAndStart(
            final CeylonEditor editor) {
        if (editor.getSelection().getLength()>0) {
            new ExtractValueLinkedMode(editor).start();
        }
        else {
            Shell shell = editor.getSite().getShell();
            new SelectExpressionPopup(shell, 0, editor,
                    "Extract Value") {
                ExtractLinkedMode linkedMode() {
                    return new ExtractValueLinkedMode(editor);
                }
            }
            .open();
        }
    }
    
}
