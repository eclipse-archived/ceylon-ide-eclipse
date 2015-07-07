package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public final class ExtractFunctionLinkedMode 
        extends ExtractLinkedMode {
        
    private final ExtractFunctionRefactoring refactoring;
    
    public ExtractFunctionLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = new ExtractFunctionRefactoring(editor);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        try {
            DocumentChange change = 
                    new DocumentChange("Extract Function", 
                            document);
            refactoring.extractInFile(change);
            change.perform(new NullProgressMonitor());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    
    @Override
    protected boolean canStart() {
        return refactoring.isEnabled();
    }
    
    @Override
    protected int getNameOffset() {
        return refactoring.decRegion.getOffset();
    }
    
    @Override
    protected int getTypeOffset() {
        return refactoring.typeRegion.getOffset();
    }
    
    @Override
    protected int getExitPosition(int selectionOffset, int adjust) {
        return refactoring.refRegion.getOffset();
    }
    
    @Override
    protected String[] getNameProposals() {
    	return refactoring.getNameProposals();
    }
    
    @Override
    protected void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust) {
        
        addNamePosition(document, 
                refactoring.refRegion.getOffset(),
                refactoring.refRegion.getLength());
        
        Type type = refactoring.getType();
        if (!isTypeUnknown(type)) {
            addTypePosition(document, type, 
                    refactoring.typeRegion.getOffset(), 
                    refactoring.typeRegion.getLength());
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
            public AbstractRefactoring createRefactoring() {
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

}
