/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.core.launch {
    ICeylonLaunchConfigurationConstants {
        ...
    }
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonPlugin
}

import java.io {
    File
}
import java.lang {
    ObjectArray,
    Types
}
import java.util.jar {
    JarFile,
    Attributes
}

import org.eclipse.core.runtime {
    CoreException,
    IProgressMonitor,
    Status,
    IStatus
}
import org.eclipse.debug.core {
    ILaunchConfiguration,
    DebugEvent,
    ILaunch,
    DebugPlugin {
        debugPlugin=default,
        newProcess
    },
    ILaunchListener
}
import org.eclipse.debug.internal.ui {
    DebugUIPlugin
}
import org.eclipse.jdt.internal.launching {
    LaunchingMessages
}
import org.eclipse.jdt.launching {
    JavaLaunchDelegate,
    IJavaLaunchConfigurationConstants {
        ...
    }
}
import org.eclipse.ui.console {
    ConsolePlugin {
        consolePlugin=default
    }
}

String getLaunchConfigurationName(ILaunchConfiguration config)
    => let (attr = (String s) => config.getAttribute(s,""))
        buildLaunchConfigurationName {
            projectName = attr(attrProjectName);
            moduleName = attr(attrModuleName);
            runName = attr(attrToplevelName);
            jarPackagingToolName => attr(attrJarCreationToolName);
        };

String buildLaunchConfigurationName(
        String projectName,
        String moduleName,
        String runName,
        String jarPackagingToolName)
    => let (launchManager = debugPlugin.launchManager)
    " \{#2014} ".join {
        for (name in
                { projectName,
                  moduleName,
                  runName,
                  jarPackagingToolName})
        launchManager.generateLaunchConfigurationName(name)
    };
    
shared class JarPackagedCeylonLaunchConfigurationDelegate() extends JavaLaunchDelegate()
        satisfies CeylonDebuggingSupportEnabled {
    
    shared JarPackagingTool? jarPackagingTool(ILaunchConfiguration config) => 
            jarCreationToolsMap[config.getAttribute(ICeylonLaunchConfigurationConstants.attrJarCreationToolName, jarCreationTools[0].type)];
    
    shouldStopInMain(ILaunchConfiguration configuration) => 
            isStopInMain(configuration);
    
    getOriginalVMInstall(ILaunchConfiguration configuration) => 
            getVMInstall(configuration);
    
    handleDebugEvents(ObjectArray<DebugEvent> _DebugEventArray) =>
            (super of CeylonDebuggingSupportEnabled).handleDebugEvents(_DebugEventArray);
    
    getOriginalVMArguments(ILaunchConfiguration configuration) => 
            (super of JavaLaunchDelegate).getVMArguments(configuration);
    
    getVMArguments(ILaunchConfiguration configuration) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMArguments(configuration);

    getOriginalVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of JavaLaunchDelegate).getVMRunner(configuration, mode);
    
    getVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMRunner(configuration, mode);

    getClasspath(ILaunchConfiguration config) => ObjectArray.with {
        if (exists jar = jarPackagingTool(config)?.outputFile(config)) 
        Types.nativeString(jar.absolutePath)
    };

    shared actual String? verifyMainTypeName(ILaunchConfiguration configuration) {
        value outputFile = jarPackagingTool(configuration)?.outputFile(configuration);
        if (! exists outputFile) {
            return null;
        }
        value jarFile = JarFile(outputFile);
        try {
            if (exists mainClass = jarFile.manifest
                .mainAttributes.getValue(Attributes.Name.mainClass)) {
                return mainClass;
            }
        } finally {
            jarFile.close();
        }
        abort(
            LaunchingMessages.abstractJavaLaunchConfigurationDelegate_Main_type_not_specified_11, 
            null,
            IJavaLaunchConfigurationConstants.errUnspecifiedMainType); 
        throw;
    }
    
    IStatus createFile(ILaunchConfiguration config, ILaunch launch, IProgressMonitor? monitor) {
        value tool = jarPackagingTool(config);
        if (! exists tool) {
            abort("No valid Jar creation tool was selected.", null, 2001);
            throw;
        }
        
        if (exists monitor) {
            monitor.subTask("Generating the ``tool.type`` archive ..." ); 
        }
        
        value outputFile = tool.outputFile(config);
        if (!exists outputFile) {
            return Status(IStatus.error, CeylonPlugin.pluginId, "The output file of the ``tool.type`` packaging tool cannot be determined.");
        }
        
        if (outputFile.\iexists()) {
            outputFile.delete();
        }
        
        value moduleToJar = tool.getModule(config);
        if (!exists moduleToJar) {
            return Status(IStatus.error, CeylonPlugin.pluginId, "The Ceylon module to package as a ``tool.type``  cannot be found.");
        }
        
        value ceylonProject = tool.getCeylonProject(config);
        if (!exists ceylonProject) {
            return Status(IStatus.error, CeylonPlugin.pluginId, "The Ceylon project of the module to package as a ``tool.type`` cannot be found.");
        }
        
        value workingDirectory = ceylonProject.rootDirectory;
        
        value ceylonCommandName = ".".join {
            "ceylon",
            if (operatingSystem.name == "windows") "bat"
        };
        
        File ceylonBinary = File(File(CeylonPlugin.embeddedCeylonRepository.parentFile, "bin"), ceylonCommandName);
        if (! ceylonBinary.canExecute()) {
            if (!ceylonBinary.setExecutable(true)) {
                return Status(IStatus.error, CeylonPlugin.pluginId, "The '`` ceylonBinary ``' command cannot be set as executable.");
            }
        }
        
        value runFunction = config.getAttribute(ICeylonLaunchConfigurationConstants.attrToplevelName, "");
        
        value processBuilder = tool.doCreateFile(ceylonBinary, outputFile, ceylonProject, moduleToJar, workingDirectory, runFunction);
        value env = processBuilder.environment();
        value vmInstall = getVMInstall(config);
        env.put(
            Types.nativeString("JAVA_HOME"), 
            Types.nativeString(vmInstall.installLocation.absolutePath));  
        value systemProcess = processBuilder.start();
        
        value process = newProcess(launch, systemProcess, "``tool.type`` packaging of module `` moduleToJar.nameAsString ``");
        try {
            systemProcess.waitFor();
        } finally {
            value console = DebugUIPlugin.default.processConsoleManager.getConsole(process);
            launch.removeProcess(process);
            consolePlugin.consoleManager.addConsoles(ObjectArray.with { console });
            
            debugPlugin.launchManager.addLaunchListener(object satisfies ILaunchListener {
                launchAdded(ILaunch launch) => noop();
                launchChanged(ILaunch launch) => noop();
                shared actual void launchRemoved(ILaunch launchToRemove) {
                    if (launchToRemove == launch) {
                        consolePlugin.consoleManager.removeConsoles(ObjectArray.with { console });
                        debugPlugin.launchManager.removeLaunchListener(this);
                    }
                }
            });
        }
        
        if (systemProcess.exitValue() != 0) {
            return Status(IStatus.error, CeylonPlugin.pluginId, 
                    "The ``tool.type`` packaging tool failed with exit code: ``systemProcess.exitValue()``.");
        }
        if (! outputFile.\iexists()) {
            return Status(IStatus.error, CeylonPlugin.pluginId, 
                    "The ``tool.type`` packaging tool didn't produce the expected archive.");
        }
        return Status.okStatus;
    }

    shared actual void launch(ILaunchConfiguration launchConfiguration, String mode, ILaunch launch, IProgressMonitor? monitor) {
        value status = createFile(launchConfiguration, launch, monitor);
        if (status != Status.okStatus) {
            throw CoreException(status);
        }

        super.launch(launchConfiguration, mode, launch, monitor);
    }
    
    shared actual String[2]? getStartLocation(ILaunchConfiguration configuration) {
        if (exists topLevelName = 
                verifyMainTypeName(configuration)) {
            value index = topLevelName.lastIndexOf(".");
            String methodToStopIn;
            if (index >= -1 && index < topLevelName.size-1) {
                if (exists typeFirstChar = 
                        topLevelName[index + 1],
                    !typeFirstChar.uppercase) {
                    // It's a top-level method
                    methodToStopIn = 
                            topLevelName.substring(index + 1, topLevelName.size-1);
                } else {
                    // It's a top-level class
                    methodToStopIn = "<init>"; // constructor
                }
            } else {
                methodToStopIn = "<unknown>";
            }
            return [topLevelName, methodToStopIn];
        }
        return null;
    }
    
    
}


