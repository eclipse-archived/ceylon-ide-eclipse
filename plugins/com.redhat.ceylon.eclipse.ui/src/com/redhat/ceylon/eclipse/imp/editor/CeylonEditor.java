package com.redhat.ceylon.eclipse.imp.editor;

import java.lang.reflect.Field;

import org.eclipse.imp.editor.GenerateActionGroup;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.ui.DefaultPartListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class CeylonEditor extends UniversalEditor {
    static Field refreshContributionsField;
    static Field generateActionGroupField;
    static {
        try {
            refreshContributionsField = UniversalEditor.class.getDeclaredField("fRefreshContributions");
            refreshContributionsField.setAccessible(true);
            generateActionGroupField = UniversalEditor.class.getDeclaredField("fGenerateActionGroup");
            generateActionGroupField.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        getSite().getPage().hideActionSet(IMP_CODING_ACTION_SET);
        //getSite().getPage().hideActionSet(IMP_OPEN_ACTION_SET);
        try {
            getSite().getPage().removePartListener((DefaultPartListener) refreshContributionsField.get(this));
            generateActionGroupField.set(this, new CeylonGenerateActionGroup(this));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    class CeylonGenerateActionGroup extends GenerateActionGroup {
        public CeylonGenerateActionGroup(UniversalEditor editor) {
            super(editor, "");
        }
        @Override
        public void fillContextMenu(IMenuManager menu) {}
    }
    
    private SourceArchiveDocumentProvider sourceArchiveDocumentProvider;
    
    @Override
    public IDocumentProvider getDocumentProvider() {
        if (SourceArchiveDocumentProvider.canHandle(getEditorInput())) {
            if (sourceArchiveDocumentProvider == null) {
                sourceArchiveDocumentProvider= new SourceArchiveDocumentProvider();
            }
            return sourceArchiveDocumentProvider;
        }
        return super.getDocumentProvider();
    }

    public CeylonParseController getParseController() {
        return (CeylonParseController) super.getParseController();
    }
    
}
