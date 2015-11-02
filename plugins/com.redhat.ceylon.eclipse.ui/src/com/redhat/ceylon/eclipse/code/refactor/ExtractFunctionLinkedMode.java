package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.model.typechecker.model.Type;

public final class ExtractFunctionLinkedMode 
        extends ExtractLinkedMode {
        
    private final ExtractFunctionRefactoring refactoring;
    
    public ExtractFunctionLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = new ExtractFunctionRefactoring(editor);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        DocumentChange change = 
                new DocumentChange("Extract Function", 
                        document);
        refactoring.extractInFile(change);
        EditorUtil.performChange(change);
        return 0;
    }
    
    @Override
    protected boolean canStart() {
        return refactoring.getEnabled();
    }
    
    @Override
    protected int getNameOffset() {
        return refactoring.getDecRegion().getOffset();
    }
    
    @Override
    protected int getTypeOffset() {
        return refactoring.getTypeRegion().getOffset();
    }
    
    @Override
    protected int getExitPosition(int selectionOffset, int adjust) {
        return refactoring.getRefRegion().getOffset();
    }
    
    @Override
    protected String[] getNameProposals() {
    	return refactoring.getNameProposals();
    }
    
    @Override
    protected void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust) {
        
        addNamePosition(document, 
                refactoring.getRefRegion().getOffset(),
                refactoring.getRefRegion().getLength());
        
        Type type = refactoring.getType();
        if (!isTypeUnknown(type)) {
            addTypePosition(document, type, 
                    refactoring.getTypeRegion().getOffset(), 
                    refactoring.getTypeRegion().getLength());
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
        return PLUGIN_ID + ".action.extractFunction";
    }
    
    @Override
    protected void openPreview() {
        new ExtractFunctionRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return ExtractFunctionLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new ExtractFunctionWizard((ExtractFunctionRefactoring) refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    @Override
    protected void openDialog() {
        new ExtractFunctionRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return ExtractFunctionLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    public boolean canBeInferred() {
        return refactoring.canBeInferred();
    }
    
    @Override
    protected String getKind() {
        return "function";
    }

    public static void selectExpressionAndStart(
            final CeylonEditor editor) {
        if (editor.getSelection().getLength()>0) {
            new ExtractFunctionLinkedMode(editor).start();
        }
        else {
            Shell shell = editor.getSite().getShell();
            new SelectExpressionPopup(shell, 0, editor,
                    "Extract Function") {
                ExtractLinkedMode linkedMode() {
                    return new ExtractFunctionLinkedMode(editor);
                }
            }
            .open();
        }
    }

}
