/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.core.debug.model {
    CeylonJDIDebugTarget
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonPlugin {
        ceylonPluginId=pluginId,
        ceylonPlugin=instance
    }
}
import com.sun.jdi {
    VirtualMachine
}

import java.lang {
    ObjectArray,
    JString=String,
    Types
}
import java.util {
    HashMap
}

import org.eclipse.core.resources {
    IWorkspaceRunnable,
    ResourcesPlugin {
        workspace
    }
}
import org.eclipse.core.runtime {
    IStatus,
    IProgressMonitor,
    Status,
    CoreException
}
import org.eclipse.debug.core {
    IDebugEventSetListener,
    DebugEvent,
    ILaunch,
    ILaunchConfiguration,
    DebugPlugin {
        parseArguments,
        renderArguments,
        debugPlugin=default
    }
}
import org.eclipse.debug.core.model {
    IDebugTarget,
    IProcess
}
import org.eclipse.jdt.debug.core {
    JDIDebugModel,
    IJavaDebugTarget,
    IJavaMethodBreakpoint
}
import org.eclipse.jdt.internal.debug.core {
    JDIDebugPlugin
}
import org.eclipse.jdt.internal.launching {
    StandardVMDebugger,
    LaunchingPlugin
}
import org.eclipse.jdt.launching {
    IVMRunner,
    IJavaLaunchConfigurationConstants,
    IVMInstall,
    VMRunnerConfiguration
}

shared interface CeylonDebuggingSupportEnabled satisfies IDebugEventSetListener {
    shared formal String getOriginalVMArguments(ILaunchConfiguration configuration);
    shared formal IVMRunner getOriginalVMRunner(ILaunchConfiguration configuration, String mode);
    
    "Should return the Java type and method"
    shared formal [String, String]? getStartLocation(ILaunchConfiguration configuration);
    
    shared formal Boolean shouldStopInMain(ILaunchConfiguration configuration);
    shared formal IVMInstall? getOriginalVMInstall(ILaunchConfiguration configuration);
    
    shared default actual void handleDebugEvents(ObjectArray<DebugEvent> events) {
        for (event in events) {
            if (event.kind == DebugEvent.create,
                is IJavaDebugTarget target = event.source,
                exists launch = target.launch,
                exists configuration = launch.launchConfiguration) {

                try {
                    if (shouldStopInMain(configuration),
                        exists [type, method] = getStartLocation(configuration)) {

                        HashMap<JString, Object> attrs = HashMap<JString, Object>();
                        value attr = Types.nativeString(IJavaLaunchConfigurationConstants.attrStopInMain);
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
            then renderArguments(ObjectArray.with {
                    Types.nativeString("-javaagent:" + javaDebugAgentPath.absolutePath),
                    for(arg in parseArguments(getOriginalVMArguments(configuration))) 
                    Types.nativeString(arg.string)
            }, null)
            else getOriginalVMArguments(configuration);
    }

    throws(`class CoreException`)
    shared IVMRunner getOverridenVMRunner(ILaunchConfiguration configuration, String mode) {
        value runner = getOriginalVMRunner(configuration, mode);
        
        if (is StandardVMDebugger runner) {
            IVMInstall? vmInstall = getOriginalVMInstall(configuration);
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