package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public final class ExtractParameterLinkedMode 
        extends ExtractLinkedMode {
        
    private final ExtractParameterRefactoring refactoring;
    
    public ExtractParameterLinkedMode(CeylonEditor editor) {
        super(editor);
        this.refactoring = new ExtractParameterRefactoring(editor);
    }
    
    @Override
    protected int performInitialChange(IDocument document) {
        try {
            DocumentChange change = 
                    new DocumentChange("Extract Parameter", document);
            refactoring.extractInFile(change);
            change.perform(new NullProgressMonitor());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
    
    @Override
    public void start() {
        if (!refactoring.isEnabled()) return;
        super.start();
    }
    
    @Override
    public String getHintTemplate() {
        return "Enter name for new parameter declaration {0}";
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
        return PLUGIN_ID + ".action.extractParameter";
    }
    
    void openPreview() {
        new RenameRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return ExtractParameterLinkedMode.this.refactoring;
            }
            @Override
            public RefactoringWizard createWizard(AbstractRefactoring refactoring) {
                return new ExtractValueWizard((ExtractValueRefactoring) refactoring) {
                    @Override
                    protected void addUserInputPages() {}
                };
            }
        }.run();
    }

    void openDialog() {
        new ExtractParameterRefactoringAction(editor) {
            @Override
            public AbstractRefactoring createRefactoring() {
                return ExtractParameterLinkedMode.this.refactoring;
            }
        }.run();
    }
    
    @Override
    protected void updatePopupLocation() {
        LinkedPosition currentLinkedPosition = getCurrentLinkedPosition();
        if (currentLinkedPosition==null) {
            getInfoPopup().setHintTemplate(getHintTemplate());
        }
        else if (currentLinkedPosition.getSequenceNumber()==2) {
            getInfoPopup().setHintTemplate("Enter type for new parameter declaration {0}");
        }
        else {
            getInfoPopup().setHintTemplate("Enter name for new parameter declaration {0}");
        }
    }
    
}
