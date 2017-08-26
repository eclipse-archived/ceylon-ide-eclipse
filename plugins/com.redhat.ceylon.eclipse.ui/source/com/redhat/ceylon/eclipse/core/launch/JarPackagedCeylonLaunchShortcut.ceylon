import com.redhat.ceylon.eclipse.code.navigator {
    SourceModuleNode
}
import com.redhat.ceylon.eclipse.core.builder {
    CeylonBuilder
}
import com.redhat.ceylon.eclipse.core.launch {
    ICeylonLaunchConfigurationConstants {
        ...
    },
    LaunchHelper {
        ...
    }
}
import com.redhat.ceylon.eclipse.ui {
    CeylonPlugin
}
import com.redhat.ceylon.eclipse.util {
    EditorUtil
}
import com.redhat.ceylon.ide.common.model {
    AnyIdeModule
}
import com.redhat.ceylon.model.typechecker.model {
    Module,
  Function
}

import java.lang {
    Types,
    ObjectArray,
    overloaded
}

import org.eclipse.core.resources {
    IResource,
    IProject,
    IFile
}
import org.eclipse.core.runtime {
    IAdaptable,
    CoreException,
    IStatus
}
import org.eclipse.debug.core {
    ILaunchConfiguration,
    DebugPlugin {
        debugPlugin=default
    }
}
import org.eclipse.debug.ui {
    ILaunchShortcut,
    DebugUITools
}
import org.eclipse.jdt.launching {
    IJavaLaunchConfigurationConstants {
        ...
    }
}
import org.eclipse.jface.dialogs {
    MessageDialog
}
import org.eclipse.jface.viewers {
    ISelection,
    IStructuredSelection
}
import org.eclipse.jface.window {
    Window
}
import org.eclipse.ui {
    IEditorPart
}
import org.eclipse.ui.dialogs {
    ElementListSelectionDialog
}
import com.redhat.ceylon.eclipse.code.editor {
  CeylonEditor
}
import com.redhat.ceylon.ide.common.util {
  nodes
}
import com.redhat.ceylon.compiler.typechecker.tree {
  Tree
}

shared abstract class JarPackagedCeylonLaunchShortcut(String jarPackagingToolName) satisfies ILaunchShortcut {
    value launchManager = debugPlugin.launchManager;
    assert(exists configType = launchManager.getLaunchConfigurationType(idCeylonJarPackagedModule));
    
    function projectNameFromModule(Module m)
        => if (is AnyIdeModule m)
        then m.ceylonProject?.name
        else null;
    
    function getLaunchConfigurations(Module moduleToLaunch) {
        value projectName = projectNameFromModule(moduleToLaunch);
        if (! exists projectName) {
            return [];
        }
        
        try {
            String moduleName 
                = moduleToLaunch.defaultModule
                then moduleToLaunch.nameAsString
                else LaunchHelper.getFullModuleName(moduleToLaunch);
            
            return [
                for (config in launchManager.getLaunchConfigurations(configType))
                if (config.getAttribute(attrProjectName, "") == projectName &&
                    config.getAttribute(attrModuleName, "") == moduleName &&
                    config.getAttribute(attrJarCreationToolName, "") == jarPackagingToolName)
                config
            ];
        }
        catch (CoreException e) {
            CeylonPlugin.log(IStatus.warning, "", e);
        }
        return [];
    }
    
    "Choose a pre-defined configuration if there is more than one defined configuration
     for the same combination of project, module and runnable"
    function chooseConfiguration({ILaunchConfiguration*} configs) {
        value labelProvider = 
                DebugUITools.newDebugModelPresentation();
        value dialog = 
                ElementListSelectionDialog(EditorUtil.shell, labelProvider);
        dialog.setElements(ObjectArray.with(configs));
        dialog.setTitle("Ceylon ``jarPackagingToolName`` Launcher");  
        dialog.setMessage("Please choose a configuration to start the Ceylon application");
        dialog.setMultipleSelection(false);
        value result = dialog.open();
        labelProvider.dispose();
        if (result == Window.ok,
            is ILaunchConfiguration chosenConf = dialog.firstResult) {
            return chosenConf;
        }
        return null;        
    }

    function findLaunchConfiguration(Module moduleToLaunch)
        => let(candidateConfigs = 
                getLaunchConfigurations(moduleToLaunch))
        if (candidateConfigs.rest.empty)
        then candidateConfigs.first 
        else chooseConfiguration(candidateConfigs);
    
    function createConfiguration(Module moduleToLaunch, Function? run) {
        try {
            value projectName = projectNameFromModule(moduleToLaunch);
            if (! exists projectName) {
                return null;
            }
            
            value moduleName = getFullModuleName(moduleToLaunch);
            value runName = run?.qualifiedNameString else moduleName+"::run";
            
            value configurationName = buildLaunchConfigurationName {
                projectName = projectName;
                moduleName = moduleName;
                runName = runName;
                jarPackagingToolName = jarPackagingToolName;
            };
            
            value wc = configType.newInstance(null, configurationName);
            wc.setAttribute(attrProjectName, projectName);
            wc.setAttribute(attrModuleName, moduleName);
            wc.setAttribute(attrToplevelName, runName);
            wc.setAttribute(attrJarCreationToolName, jarPackagingToolName);
            return wc.doSave();
        } catch (CoreException exception) {
            MessageDialog.openError(EditorUtil.shell, "Ceylon Launcher Error", 
                exception.status.message);
            return null;
        } 
    }
    
    shared void launchModule(Module existingModule, Function? run, String mode) {
        variable ILaunchConfiguration? config = 
                findLaunchConfiguration(existingModule);
        if (! config exists) {
            config = createConfiguration(existingModule, run);
        }
        if (exists configToUse = config) {
            DebugUITools.launch(config, mode);
        }           
        
    }
    
    shared actual overloaded void launch(ISelection selection, String mode) {
        if (! is IStructuredSelection selection) {
            return;
        }
        
        if (selection.size() > 1) {
            return;
        }
        
        value selectedItem = selection.firstElement else null;
        if (! exists selectedItem) {
            return;
        }
        
        variable Module? mod = null;
        if (is SourceModuleNode selectedItem) {
            mod = selectedItem.\imodule;
        } else if (is IAdaptable selectedItem,
            is IProject project = 
                    selectedItem.getAdapter(Types.classForType<IResource>())) {
            mod = LaunchHelper.getDefaultOrOnlyModule(project, true);
            if (! mod exists) {
                mod = LaunchHelper.chooseModule(project, true);
            }
        }
        if (exists existingModule = mod) {
            launchModule(existingModule, null, mode);
        }
    }
    
    shared actual overloaded void launch(IEditorPart editor, String mode) {
        IFile file = EditorUtil.getFile(editor.editorInput);
        if (exists moduleToLaunch = CeylonBuilder.getModule(file)) {
            if (is CeylonEditor editor,
                exists root = editor.parseController.lastCompilationUnit,
                exists node = editor.selectedNode,
                is Tree.AnyMethod fun = nodes.findTopLevelStatement(root, node)) {
                launchModule(moduleToLaunch, fun.declarationModel, mode);
            }
            else {
                launchModule(moduleToLaunch, null, mode);
            }
        }
    }
}



shared class FatJarPackagedCeylonLaunchShortcut() extends JarPackagedCeylonLaunchShortcut("Fat Jar") {}

shared class AssemblyPackagedCeylonLaunchShortcut() extends JarPackagedCeylonLaunchShortcut("Assembly") {}

shared class SwarmPackagedCeylonLaunchShortcut() extends JarPackagedCeylonLaunchShortcut("Swarm") {}
