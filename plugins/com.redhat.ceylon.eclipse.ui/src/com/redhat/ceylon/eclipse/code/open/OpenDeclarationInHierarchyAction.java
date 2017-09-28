package org.eclipse.ceylon.ide.eclipse.code.open;

import static org.eclipse.ceylon.ide.eclipse.code.outline.HierarchyView.showHierarchyView;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getSelectionText;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.ceylon.model.typechecker.model.Declaration;

public class OpenDeclarationInHierarchyAction extends Action {
    
    private final IEditorPart editor;
    
    public OpenDeclarationInHierarchyAction(IEditorPart editor) {
        this("Open in Type Hierarchy View...", editor);
    }
    
    public OpenDeclarationInHierarchyAction(String text, IEditorPart editor) {
        super(text);
        this.editor = editor;
        setActionDefinitionId(PLUGIN_ID 
                + ".action.openDeclarationInHierarchy");
//        setImageDescriptor(CeylonPlugin.getInstance().getImageRegistry()
//                .getDescriptor(CeylonResources.CEYLON_OPEN_DECLARATION));
    }
    
    @Override
    public void run() {
        OpenDeclarationDialog dialog = 
                new OpenDeclarationDialog(false, true, 
                        getWorkbench()
                            .getActiveWorkbenchWindow()
                            .getShell(),
                        "Open in Type Hierarchy View",
                        "&Type part of a name, with wildcard *, or a camel hump pattern, with . to display members:",
                        "&Select a declaration to open in hierarchy view:");
        if (editor instanceof ITextEditor) {
            dialog.setInitialPattern(getSelectionText((ITextEditor) editor));
        }
        dialog.open();
        Declaration[] results = dialog.getResult();
        if (results!=null && results.length>0) {
            try {
                showHierarchyView().focusOn(results[0]);
            }
            catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }

}