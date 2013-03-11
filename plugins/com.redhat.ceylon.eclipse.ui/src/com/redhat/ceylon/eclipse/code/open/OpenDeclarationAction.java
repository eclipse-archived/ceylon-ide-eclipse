package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class OpenDeclarationAction extends Action {
    private final IEditorPart editor;
    
    public OpenDeclarationAction(IEditorPart editor) {
        this("Open Ceylon Declaration...", editor);
    }
    
    public OpenDeclarationAction(String text, IEditorPart editor) {
        super(text);
        this.editor = editor;
        setActionDefinitionId(PLUGIN_ID + ".action.openDeclaration");
        setImageDescriptor(CeylonPlugin.getInstance().getImageRegistry()
                .getDescriptor(CeylonResources.CEYLON_OPEN_DECLARATION));
    }
    
    @Override
    public void run() {
        Shell shell = CeylonPlugin.getInstance().getWorkbench()
                .getActiveWorkbenchWindow().getShell();
        OpenCeylonDeclarationDialog dialog = new OpenCeylonDeclarationDialog(shell, editor);
        dialog.setTitle("Open Ceylon Declaration");
        dialog.setMessage("Select a Ceylon declaration to open:");
        if (editor instanceof ITextEditor) {
            dialog.setInitialPattern(Util.getSelectionText((ITextEditor) editor));
        }
        dialog.open();
        Object[] types = dialog.getResult();
        if (types != null && types.length > 0) {
            gotoDeclaration((DeclarationWithProject) types[0]);
        }
    }

    private void gotoDeclaration(DeclarationWithProject dwp) {
        IProject project = dwp.getProject();
        Declaration dec = dwp.getDeclaration();
        CeylonSourcePositionLocator.gotoDeclaration(dec, project, editor);
    }

}