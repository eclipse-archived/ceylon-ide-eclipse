package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
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
 
    public void addFiles(List<IFile> files, IResource resource) {
        switch (resource.getType()) {
            case IResource.FILE:
                IFile file = (IFile) resource;
                IPath path = file.getFullPath(); //getProjectRelativePath();
                if (path!=null && "ceylon".equals(path.getFileExtension()) ) {
                    files.add(file);
                }
                break;
            case IResource.FOLDER:
            case IResource.PROJECT:
                IContainer folder = (IContainer) resource;
                try {
                    for (IResource child: folder.members()) {
                        addFiles(files, child);
                    }
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    
    protected ILaunchConfiguration createConfiguration(Declaration declarationToRun, IFile file) {
        ILaunchConfiguration config = null;
        ILaunchConfigurationWorkingCopy wc = null;
        try {
            ILaunchConfigurationType configType = getConfigurationType();
            String configurationName = "";

            LaunchHelper helper = new LaunchHelper(file.getProject().getName());
            Module mod = helper.getModule(declarationToRun);      
            String moduleName = mod.getNameAsString() + "/" + mod.getVersion();
            if (!mod.isDefault()) {
                configurationName += moduleName;
            } else {
                moduleName = mod.getNameAsString();
                configurationName += declarationToRun.getUnit().getPackage().getQualifiedNameString();
            }
            
            String topLevelName = declarationToRun.getQualifiedNameString().replace("::", ".");
            configurationName += helper.getTopLevelDisplayName(declarationToRun);
            configurationName = configurationName.replaceAll("[\u00c0-\ufffe]", "_");
            wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(configurationName));        
            wc.setAttribute(ATTR_PROJECT_NAME, file.getProject().getName());
            wc.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, moduleName);
            wc.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME, topLevelName);
            wc.setMappedResources(new IResource[] {file});
            config = wc.doSave();
        } catch (CoreException exception) {
            MessageDialog.openError(Util.getShell(), "Ceylon Module Launcher Error", 
                    exception.getStatus().getMessage()); 
        }
        return config;
    }
    
    /**
     * Finds and returns an <b>existing</b> configuration to re-launch for the given type,
     * or <code>null</code> if there is no existing configuration.
     * 
     * @return a configuration to use for launching the given type or <code>null</code> if none
     */
    protected ILaunchConfiguration findLaunchConfiguration(Declaration declaration, IFile file, 
            ILaunchConfigurationType configType) {
        List<ILaunchConfiguration> candidateConfigs = Collections.<ILaunchConfiguration>emptyList();
        try {
            ILaunchConfiguration[] configs = DebugPlugin.getDefault().getLaunchManager()
                    .getLaunchConfigurations(configType);
            candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
 
            LaunchHelper helper = new LaunchHelper(file.getProject().getName());
            Module mod = helper.getModule(declaration);
            String moduleName = mod.getNameAsString() + "/" + mod.getVersion();
            if (mod.isDefault()) {
                moduleName = mod.getNameAsString();
            }
            
            String topLevelName = declaration.getQualifiedNameString().replace("::", ".");
            
            for (int i = 0; i < configs.length; i++) {
                ILaunchConfiguration config = configs[i];
                if (config.getAttribute(ATTR_TOPLEVEL_NAME, "").equals(topLevelName) && 
                        config.getAttribute(ATTR_PROJECT_NAME, "").equals(file.getProject().getName()) &&
                        config.getAttribute(ATTR_MODULE_NAME, "").equals(moduleName)) {
                    candidateConfigs.add(config);
                }
            }
        } catch (CoreException e) {
            e.printStackTrace(); // TODO : Use a logger
        }
        int candidateCount = candidateConfigs.size();
        if (candidateCount == 1) {
            return candidateConfigs.get(0);
        } 
        else if (candidateCount > 1) {
            return chooseConfiguration(candidateConfigs);
        }
        return null;
    } 
}
