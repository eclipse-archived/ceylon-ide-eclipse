package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.eclipse.code.outline.HierarchyView.showHierarchyView;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getSelectionText;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;

public class OpenDeclarationInHierarchyAction extends Action {
    
    private final IEditorPart editor;
    
    public OpenDeclarationInHierarchyAction(IEditorPart editor) {
        this("Open in Type Hierarchy View...", editor);
    }
    
    public OpenDeclarationInHierarchyAction(String text, IEditorPart editor) {
        super(text);
        this.editor = editor;
        setActionDefinitionId(PLUGIN_ID + ".action.openDeclarationInHierarchy");
//        setImageDescriptor(CeylonPlugin.getInstance().getImageRegistry()
//                .getDescriptor(CeylonResources.CEYLON_OPEN_DECLARATION));
    }
    
    @Override
    public void run() {
        Shell shell = getWorkbench().getActiveWorkbenchWindow().getShell();
        OpenDeclarationDialog dialog = 
                new OpenDeclarationDialog(false, shell, "Open in Type Hierarchy View",
                        "&Type part of a name, with wildcard *, or a camel hump pattern:",
                        "&Select a declaration to open in hierarchy view:");
        if (editor instanceof ITextEditor) {
            dialog.setInitialPattern(getSelectionText((ITextEditor) editor));
        }
        dialog.open();
        Object[] types = dialog.getResult();
        if (types != null && types.length > 0) {
            DeclarationWithProject dwp = 
                    (DeclarationWithProject) types[0];
            try {
                showHierarchyView().focusOn(dwp.getProject(), 
                        dwp.getDeclaration());
            }
            catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }

}