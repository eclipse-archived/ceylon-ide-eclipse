package com.redhat.ceylon.eclipse.core.launch;

import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.code.editor.Util;

public class CeylonModuleLaunchShortcut extends CeylonApplicationLaunchShortcut {

    protected ILaunchConfigurationType getConfigurationType() {
        return getLaunchManager().getLaunchConfigurationType(ICeylonLaunchConfigurationConstants.ATTR_CEYLON_MODULE);        
    }
    
    protected ILaunchConfiguration createConfiguration(Declaration declarationToRun, IFile file) {
        ILaunchConfiguration config = null;
        ILaunchConfigurationWorkingCopy wc = null;
        try {
            ILaunchConfigurationType configType = getConfigurationType();
            String configurationName = "";

            Module mod = declarationToRun.getUnit().getPackage().getModule();
            String ceylonModule = mod.isDefault() ? "default" : mod.getNameAsString();
            if (!mod.isDefault()) {
                ceylonModule = ceylonModule + "/" + mod.getVersion();
            }
            
            configurationName = ceylonModule.replaceAll("[\u00c0-\ufffe]", "_");
            
            wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(configurationName));
            
            wc.setAttribute(ATTR_PROJECT_NAME, file.getProject().getName());

            wc.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, ceylonModule);

            wc.setMappedResources(new IResource[] {file});
            
            config = wc.doSave();
        } catch (CoreException exception) {
            MessageDialog.openError(Util.getShell(), "Ceylon Module Launcher Error", 
                    exception.getStatus().getMessage()); 
        }
        return config;
    }
}
