import ceylon.interop.java {
    createJavaStringArray,
    javaString,
    createJavaObjectArray
}

import com.redhat.ceylon.eclipse.core.debug.model {
    CeylonJDIDebugTarget
}
import com.redhat.ceylon.eclipse.ui {
    CeylonPlugin {
        ceylonPlugin=instance,
        ceylonPluginId=pluginId
    }
}
import com.sun.jdi {
    VirtualMachine
}

import java.io {
    File
}
import java.lang {
    ObjectArray,
    JString=String
}
import java.util {
    HashMap
}
import java.util.jar {
    JarFile,
    Attributes
}

import org.eclipse.core.resources {
    IWorkspaceRunnable,
    ResourcesPlugin {
        workspace
    }
}
import org.eclipse.core.runtime {
    CoreException,
    IProgressMonitor,
    Status,
    IStatus
}
import org.eclipse.debug.core {
    ILaunchConfiguration,
    IDebugEventSetListener,
    DebugEvent,
    ILaunch,
    DebugPlugin {
        renderArguments,
        parseArguments,
        debugPlugin = default,
        newProcess
    },
    ILaunchListener
}
import org.eclipse.debug.core.model {
    IDebugTarget,
    IProcess
}
import org.eclipse.debug.internal.ui {
    DebugUIPlugin
}
import org.eclipse.jdt.debug.core {
    IJavaDebugTarget,
    JDIDebugModel,
    IJavaMethodBreakpoint
}
import org.eclipse.jdt.internal.debug.core {
    JDIDebugPlugin
}
import org.eclipse.jdt.internal.launching {
    LaunchingMessages,
    StandardVMDebugger,
    LaunchingPlugin
}
import org.eclipse.jdt.launching {
    JavaLaunchDelegate,
    IJavaLaunchConfigurationConstants { ... },
    IVMRunner,
    AbstractJavaLaunchConfigurationDelegate,
    IVMInstall,
    VMRunnerConfiguration
}
import com.redhat.ceylon.eclipse.core.launch {
    ICeylonLaunchConfigurationConstants {
        ...
    }
}

import org.eclipse.ui.console {
    ConsolePlugin{
        consolePlugin = default
    }
}

String getLaunchConfigurationName(ILaunchConfiguration config)
    => let (attr = (String s) => (config.getAttribute(s,"")))
        buildLaunchConfigurationName {
        projectName => attr(attrProjectName);
        moduleName => attr(attrModuleName);
        jarPackagingToolName => attr(attrJarCreationToolName);
    };

String buildLaunchConfigurationName(
        String projectName,
        String moduleName,
        String jarPackagingToolName)
    => let (launchManager = debugPlugin.launchManager)
    " \{#2014} ".join {
        for (name in { 
            projectName,
            moduleName,
            jarPackagingToolName})
            launchManager.generateLaunchConfigurationName(name)
        };
    
shared class JarPackagedCeylonLaunchConfigurationDelegate() extends JavaLaunchDelegate()
        satisfies CeylonDebuggingSupportEnabled {
    
    shared JarPackagingTool? jarPackagingTool(ILaunchConfiguration config) {
        return jarCreationToolsMap[config.getAttribute(ICeylonLaunchConfigurationConstants.attrJarCreationToolName, jarCreationTools[0].type)];
    }
    
    shared actual ObjectArray<JString> getClasspath(ILaunchConfiguration config) => createJavaStringArray {
        if (exists jar = jarPackagingTool(config)?.outputFile(config)) 
            jar.absolutePath
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
    
    shared actual AbstractJavaLaunchConfigurationDelegate this_ => this;
    
    shared actual void handleDebugEvents(ObjectArray<DebugEvent> _DebugEventArray) =>
            (super of CeylonDebuggingSupportEnabled).handleDebugEvents(_DebugEventArray);
    
    shared actual String getOriginalVMArguments(ILaunchConfiguration configuration) => 
            (super of JavaLaunchDelegate).getVMArguments(configuration);
    
    shared actual String getVMArguments(ILaunchConfiguration configuration) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMArguments(configuration);

    shared actual IVMRunner getOriginalVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of JavaLaunchDelegate).getVMRunner(configuration, mode);
    
    shared actual IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode) => 
            (super of CeylonDebuggingSupportEnabled).getOverridenVMRunner(configuration, mode);

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
        
        suppressWarnings("syntaxDeprecation")
        File ceylonBinary = File(File(ceylonPlugin.embeddedCeylonRepository.parentFile, "bin"), ceylonCommandName);
        if (! ceylonBinary.canExecute()) {
            if (!ceylonBinary.setExecutable(true)) {
                return Status(IStatus.error, CeylonPlugin.pluginId, "The '`` ceylonBinary ``' command cannot be set as executable.");
            }
        }
        
        value processBuilder = tool.doCreateFile(ceylonBinary, outputFile, ceylonProject, moduleToJar, workingDirectory);
        value env = processBuilder.environment();
        value vmInstall = getVMInstall(config);
        env.put(
            javaString("JAVA_HOME"), 
            javaString(vmInstall.installLocation.absolutePath));  
        value systemProcess = processBuilder.start();
        
        value process = newProcess(launch, systemProcess, "``tool.type`` packaging of module `` moduleToJar.nameAsString ``");
        try {
            systemProcess.waitFor();
        } finally {
            value console = DebugUIPlugin.default.processConsoleManager.getConsole(process);
            launch.removeProcess(process);
            consolePlugin.consoleManager.addConsoles(createJavaObjectArray { console });
            
            debugPlugin.launchManager.addLaunchListener(object satisfies ILaunchListener {
                launchAdded(ILaunch launch) => noop();
                launchChanged(ILaunch launch) => noop();
                shared actual void launchRemoved(ILaunch launchToRemove) {
                    if (launchToRemove == launch) {
                        consolePlugin.consoleManager.removeConsoles(createJavaObjectArray { console });
                        debugPlugin.launchManager.removeLaunchListener(this);
                    }
                }
            });
        }
        
        if (systemProcess.exitValue() != 0) {
            return Status(IStatus.error, CeylonPlugin.pluginId, "The ``tool.type`` packaging tool failed with exit code: ``systemProcess.exitValue()``.");
        }
        if (! outputFile.\iexists()) {
            return Status(IStatus.error, CeylonPlugin.pluginId, "The ``tool.type`` packaging tool didn't produce the expected archive.");
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

shared interface CeylonDebuggingSupportEnabled satisfies IDebugEventSetListener {
    shared formal AbstractJavaLaunchConfigurationDelegate this_;
    shared formal String getOriginalVMArguments(ILaunchConfiguration configuration);
    shared formal IVMRunner getOriginalVMRunner(ILaunchConfiguration configuration, String mode);
    
    "Should return the Java type and method"
    shared formal [String, String]? getStartLocation(ILaunchConfiguration configuration);

    shared default actual void handleDebugEvents(ObjectArray<DebugEvent> events) {
        for (event in events) {
            if (event.kind == DebugEvent.create,
                is IJavaDebugTarget target = event.source,
                exists launch = target.launch,
                exists configuration = launch.launchConfiguration) {

                try {
                    if (this_.isStopInMain(configuration),
                        exists [type, method] = getStartLocation(configuration)) {

                        HashMap<JString, Object> attrs = HashMap<JString, Object>();
                        value attr = javaString(IJavaLaunchConfigurationConstants.attrStopInMain);
                        attrs.put(attr, attr);
                        
                        IJavaMethodBreakpoint bp = JDIDebugModel
                                .createMethodBreakpoint(
                            workspace.root,
                            type, method, //$NON-NLS-1$
                            "()V",
                            true, false, false, -1, -1,
                            -1, 1, false, attrs); 
                        bp.persisted = false;
                        target.breakpointAdded(bp);
                        debugPlugin.removeDebugEventListener(this);
                    }
                } catch (CoreException e) {
                    LaunchingPlugin.log(e);
                }
            }
        }
    }

    throws(`class CoreException`)
    shared String getOverridenVMArguments(ILaunchConfiguration configuration)
     {
        return 
            if (exists javaDebugAgentPath = ceylonPlugin.debugAgentJar)
            then renderArguments(createJavaStringArray {
                    "-javaagent:" + javaDebugAgentPath.absolutePath,
                    for(arg in parseArguments(getOriginalVMArguments(configuration))) arg.string
            }, null)
            else getOriginalVMArguments(configuration);
    }

    throws(`class CoreException`)
    shared IVMRunner getOverridenVMRunner(ILaunchConfiguration configuration, String mode) {
        value runner = getOriginalVMRunner(configuration, mode);
        
        if (is StandardVMDebugger runner) {
            IVMInstall? vmInstall = this_.getVMInstall(configuration);
            return object extends StandardVMDebugger(vmInstall) {
                variable IJavaDebugTarget? target = null;
                shared actual void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor) 
                {
                    try {
                        super.run(config, launch, monitor);
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw CoreException(Status(IStatus.error, ceylonPluginId, "Ceylon Debugging Support Error", e));
                    }
                }
                
                shared actual IDebugTarget? createDebugTarget(
                    VMRunnerConfiguration config,
                    ILaunch launch,
                    small Integer port,
                    IProcess process, 
                    VirtualMachine vm) {
                    IWorkspaceRunnable r = object satisfies IWorkspaceRunnable {
                        shared actual void run(IProgressMonitor m) {
                            target = CeylonJDIDebugTarget(launch, vm, 
                                renderDebugTarget(config.classToLaunch, port),
                                true, false, process, config.resumeOnStartup);
                        }
                    };
                    try {
                        workspace.run(r, null, 0, null);
                    } catch (CoreException e) {
                        JDIDebugPlugin.log(e);
                    }
                    return target;
                }
            };
        } else {
            return runner;
        }
    }
}
