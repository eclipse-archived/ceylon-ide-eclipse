package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.refactoring.ExtractLinkedModeEnabled;
import com.redhat.ceylon.ide.common.refactoring.ExtractValueRefactoring;
import com.redhat.ceylon.model.typechecker.model.Type;

public final class ExtractValueLinkedMode 
        extends ExtractLinkedMode {
        
    private final ExtractValueRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, IRegion> refactoring;
    
    public ExtractValueLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = refactorJ2C().newExtractValueRefactoring(editor);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        DocumentChange change =
                new DocumentChange("Extract Value",
                        document);
        refactoring.build(change);
        EditorUtil.performChange(change);
        return 0;
    }
    
    @Override
    protected boolean canStart() {
        return refactoring.getEnabled();
    }
    
    @Override
    protected int getNameOffset() {
        return refactorJ2C().toExtractLinkedModeEnabled(refactoring)
                .getDecRegion().getOffset();
    }
    
    @Override
    protected int getTypeOffset() {
        return refactorJ2C().toExtractLinkedModeEnabled(refactoring)
                .getTypeRegion().getOffset();
    }
    
    @Override
    protected int getExitPosition(int selectionOffset, int adjust) {
        return refactorJ2C().toExtractLinkedModeEnabled(refactoring)
                .getRefRegion().getOffset();
    }
    
    @Override
    protected String[] getNameProposals() {
    	return refactorJ2C().toExtractLinkedModeEnabled(refactoring)
    	        .getNameProposals();
    }
    
    @Override
    protected void addLinkedPositions(IDocument document,
            CompilationUnit rootNode, int adjust) {
        
        ExtractLinkedModeEnabled<IRegion> elme = 
                refactorJ2C().toExtractLinkedModeEnabled(refactoring);
        
        addNamePosition(document, 
                elme.getRefRegion().getOffset(),
                elme.getRefRegion().getLength(),
                refactoring.getDupeRegions());
        
        Type type = refactoring.getType();
        if (!isTypeUnknown(type)) {
            addTypePosition(document, type, 
            elme.getTypeRegion().getOffset(), 
            elme.getTypeRegion().getLength());
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
        return refactoring.getExtractsFunction() ? "function" : "value";
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
                @Override void finish() {
                    new ExtractValueLinkedMode(editor).start();
                }
            }
            .open();
        }
    }
    
    @Override
    protected void setReturnType(Type type) {
        this.refactoring.setType(type);
        this.refactoring.setExplicitType(type!=null);
    }

}
