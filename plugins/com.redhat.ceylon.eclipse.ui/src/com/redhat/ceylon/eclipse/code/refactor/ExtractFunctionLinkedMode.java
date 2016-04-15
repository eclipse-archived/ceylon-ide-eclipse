package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.refactorJ2C;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.util.CeylonHelper.toJavaStringArray;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isTypeUnknown;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.redhat.ceylon.ide.common.refactoring.DeprecatedExtractFunctionRefactoring;
import com.redhat.ceylon.model.typechecker.model.Type;

public final class ExtractFunctionLinkedMode 
        extends ExtractLinkedMode {
        
    private final DeprecatedExtractFunctionRefactoring<IFile, ICompletionProposal, IDocument, InsertEdit, TextEdit, TextChange, CompositeChange, IRegion> refactoring;
    
    public ExtractFunctionLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = refactorJ2C().newExtractFunctionRefactoring(editor);
    }
    
    public ExtractFunctionLinkedMode(CeylonEditor editor, Tree.Declaration target) {
        super(editor);
        this.refactoring = refactorJ2C().newExtractFunctionRefactoring(editor, target);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        DocumentChange change = 
                new DocumentChange("Extract Function", 
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
                refactoring.getRefRegion().getLength(),
                refactoring.getDupeRegions());
        
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
        try {
            return refactoring.getForceWizardMode() ||
                    //yew, truly terrible hack!!
                    ((Refactoring) refactoring)
                        .createChange(null) 
                            instanceof CompositeChange;
        } catch (Exception e) {
            return false;
        }
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
                return (Refactoring) ExtractFunctionLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(Refactoring refactoring) {
                return new ExtractFunctionWizard(refactoring) {
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
                return (Refactoring) ExtractFunctionLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    public boolean canBeInferred() {
        return refactoring.getCanBeInferred();
    }
    
    @Override
    protected String getKind() {
        return "function";
    }

    public static void selectExpressionAndStart(
            final CeylonEditor editor) {
        final Shell shell = editor.getSite().getShell();
        if (editor.getSelection().getLength()>0) {
            new SelectContainerPopup(shell, 0, editor,
                    "Extract Function To") {
                @Override void finish() {
                    new ExtractFunctionLinkedMode(editor, getResult()).start();
                }
                @Override boolean isEnabled() {
                    return new refactorJ2C().newExtractFunctionRefactoring(editor).getEnabled();
                }
            }
            .open();
        }
        else {
            new SelectExpressionPopup(shell, 0, editor,
                    "Extract Function") {
                @Override void finish() {
                    new SelectContainerPopup(shell, 0, editor,
                            "Extract Function To") {
                        @Override void finish() {
                            new ExtractFunctionLinkedMode(editor, getResult()).start();
                        }
                        @Override boolean isEnabled() {
                            return new refactorJ2C().newExtractFunctionRefactoring(editor).getEnabled();
                        }
                    }
                    .open();
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
