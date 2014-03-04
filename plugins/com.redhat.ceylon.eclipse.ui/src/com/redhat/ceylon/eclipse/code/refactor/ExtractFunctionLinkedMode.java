package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
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
    protected boolean canStart() {
        return refactoring.isEnabled();
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
            CompilationUnit rootNode, int adjust) {
        
        addNamePosition(document, 
                refactoring.refRegion.getOffset(),
                refactoring.refRegion.getLength());
        
        ProducedType type = refactoring.getType();
        if (!isTypeUnknown(type)) {
            List<ProducedType> supertypes = type.getSupertypes();
            int offset = refactoring.typeRegion.getOffset();
            int length = refactoring.typeRegion.getLength();
            addTypePosition(document, supertypes, offset, length);
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
    protected String getKind() {
        return "function";
    }

}
