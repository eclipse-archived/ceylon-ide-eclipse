package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.util.CeylonHelper.toJavaStringArray;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.correct.correctJ2C;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.platform.platformJ2C;
import com.redhat.ceylon.ide.common.refactoring.ExtractParameterRefactoring;
import com.redhat.ceylon.model.typechecker.model.Type;

public final class ExtractParameterLinkedMode 
        extends ExtractLinkedMode {
        
    private final ExtractParameterRefactoring<IRegion> refactoring;
    
    public ExtractParameterLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = refactorJ2C().newExtractParameterRefactoring(editor);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        com.redhat.ceylon.ide.common.platform.TextChange change
            = new platformJ2C().newChange("Extract Parameter", 
                        new correctJ2C().newDocument(document));
        refactoring.build(change);
        change.apply();
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
    	return toJavaStringArray(refactoring.getNameProposals());
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
        return refactoring.getForceWizardMode();
    }
    
    @Override
    protected String getActionName() {
        return PLUGIN_ID + ".action.extractParameter";
    }
    
    @Override
    protected void openPreview() {
        new RenameRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) ExtractParameterLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new ExtractParameterWizard(refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    @Override
    protected void openDialog() {
        new ExtractParameterRefactoringAction(editor) {
            @Override
            public Refactoring createRefactoring() {
                return (Refactoring) ExtractParameterLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    protected String getKind() {
        return "parameter";
    }

    public static void selectExpressionAndStart(
            final CeylonEditor editor) {
        if (editor.getSelection().getLength()>0) {
            new ExtractParameterLinkedMode(editor).start();
        }
        else {
            Shell shell = editor.getSite().getShell();
            new SelectExpressionPopup(shell, 0, editor,
                    "Extract Parameter") {
                @Override void finish() {
                    new ExtractParameterLinkedMode(editor).start();
                }
            }
            .open();
        }
    }
    
    @Override
    protected void setReturnType(Type type) {
        this.refactoring.setType(type);
    }

}
