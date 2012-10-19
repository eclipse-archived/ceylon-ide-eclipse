package com.redhat.ceylon.eclipse.core.launch;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.dialogs.MessageDialog;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.code.editor.Util;

public class JsLaunchShortcut extends CeylonApplicationLaunchShortcut {

    @Override
    protected String canLaunch(Declaration declarationToRun, IFile file, String mode) {
        if (!CeylonBuilder.compileToJs(file.getProject())) {
            return "JavaScript compilation is not enabled for this project";
        }
        if (!declarationToRun.isShared()) {
            //TODO check that the container hyerarchy is fully shared
            return "JavaScript launcher can only run shared methods";
        }
        return null;
    }

    protected ILaunchConfigurationType getConfigurationType() {
        return getLaunchManager().getLaunchConfigurationType(ICeylonLaunchConfigurationConstants.ID_JS_APPLICATION);        
    }
    
    protected ILaunchConfiguration createConfiguration(Declaration declarationToRun, IFile file) {
        ILaunchConfiguration config = null;
        ILaunchConfigurationWorkingCopy wc = null;
        try {
            ILaunchConfigurationType configType = getConfigurationType();
            String configurationName = "";
            if (declarationToRun instanceof Class) {
                configurationName += "class ";
            }
            else {
                if (declarationToRun instanceof Method) {
                    Method method = (Method) declarationToRun;
                    if (method.isDeclaredVoid()) {
                        configurationName += "void ";
                    }
                    else {
                        configurationName += "function ";
                    }
                }
            }
            configurationName += declarationToRun.getName() + "() - ";
            String packageName = declarationToRun.getContainer().getQualifiedNameString();
            configurationName += packageName.isEmpty() ? "default package" : packageName;
            
            wc = configType.newInstance(null, getLaunchManager().generateLaunchConfigurationName(configurationName));
            wc.setAttribute(ATTR_MAIN_TYPE_NAME, declarationToRun.getQualifiedNameString());
            wc.setAttribute(ATTR_PROJECT_NAME, file.getProject().getName());
            Module mod = declarationToRun.getUnit().getPackage().getModule();
            String ceylonModule = mod.isDefault() ? "default" : mod.getNameAsString();
            if (!mod.isDefault()) {
                ceylonModule = ceylonModule + "/" + mod.getVersion();
            }
            wc.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_CEYLON_MODULE, ceylonModule);
            wc.setAttribute(ICeylonLaunchConfigurationConstants.ATTR_JS_DEBUG, false);
            wc.setMappedResources(new IResource[] {file});
            config = wc.doSave();
        } catch (CoreException exception) {
            MessageDialog.openError(Util.getShell(), "Ceylon JS Launcher Error", 
                    exception.getStatus().getMessage()); 
        } 
        return config;
    }
}
