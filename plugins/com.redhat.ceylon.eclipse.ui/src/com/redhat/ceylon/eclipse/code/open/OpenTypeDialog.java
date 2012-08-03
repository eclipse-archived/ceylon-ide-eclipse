package com.redhat.ceylon.eclipse.code.open;

import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class OpenTypeDialog extends OpenCeylonDeclarationDialog {

	public OpenTypeDialog(Shell shell, IEditorPart editor) {
		super(shell, editor);
	}
	
	@Override
	boolean isPresentable(Declaration d) {
		return super.isPresentable(d) && 
				d instanceof TypeDeclaration;
	}
	
	@Override
	boolean includeJava() {
		return true;
	}
	
    @Override
    protected IDialogSettings getDialogSettings() {
        return DialogSettings.getOrCreateSection(CeylonPlugin.getInstance()
        		.getDialogSettings(), 
        		"openTypeDialog");
    }
    
}
