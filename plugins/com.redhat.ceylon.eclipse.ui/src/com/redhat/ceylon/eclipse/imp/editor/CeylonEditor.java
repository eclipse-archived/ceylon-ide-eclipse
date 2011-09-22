package com.redhat.ceylon.eclipse.imp.editor;

import java.lang.reflect.Field;

import org.eclipse.imp.editor.GenerateActionGroup;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.ui.DefaultPartListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Composite;

public class CeylonEditor extends UniversalEditor {
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        getSite().getPage().hideActionSet(IMP_CODING_ACTION_SET);
        //getSite().getPage().hideActionSet(IMP_OPEN_ACTION_SET);
        try {
            Field frc = UniversalEditor.class.getDeclaredField("fRefreshContributions");
            frc.setAccessible(true);
            Field fgag = UniversalEditor.class.getDeclaredField("fGenerateActionGroup");
            fgag.setAccessible(true);
            getSite().getPage().removePartListener((DefaultPartListener) frc.get(this));
            fgag.set(this, new CeylonGenerateActionGroup(this));
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

}
