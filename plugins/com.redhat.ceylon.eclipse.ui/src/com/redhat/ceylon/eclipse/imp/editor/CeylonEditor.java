package com.redhat.ceylon.eclipse.imp.editor;

import java.lang.reflect.Field;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.ui.DefaultPartListener;
import org.eclipse.swt.widgets.Composite;

public class CeylonEditor extends UniversalEditor {
    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        getSite().getPage().hideActionSet(IMP_CODING_ACTION_SET);
        //getSite().getPage().hideActionSet(IMP_OPEN_ACTION_SET);
        try {
            Field field = UniversalEditor.class.getDeclaredField("fRefreshContributions");
            field.setAccessible(true);
            getSite().getPage().removePartListener((DefaultPartListener) field.get(this));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
