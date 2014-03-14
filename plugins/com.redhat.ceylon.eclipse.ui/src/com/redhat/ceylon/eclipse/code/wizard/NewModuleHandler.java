package com.redhat.ceylon.eclipse.code.wizard;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.internal.actions.NewWizardShortcutAction;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class NewModuleHandler extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbench workbench = getWorkbench();
        IWizardDescriptor descriptor = workbench.getNewWizardRegistry()
                .findWizard(PLUGIN_ID + ".newModuleWizard");
        new NewWizardShortcutAction(workbench.getActiveWorkbenchWindow(), descriptor).run();
        return null;
    }
}
