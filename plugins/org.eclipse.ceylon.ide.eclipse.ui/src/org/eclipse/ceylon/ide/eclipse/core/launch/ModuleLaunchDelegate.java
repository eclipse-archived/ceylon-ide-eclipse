/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.launch;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getInterpolatedCeylonSystemRepo;
import static org.eclipse.ceylon.ide.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_LAUNCH_VERBOSE;
import static org.eclipse.ceylon.ide.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME;
import static org.eclipse.ceylon.ide.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME;
import static org.eclipse.ceylon.ide.eclipse.core.launch.ICeylonLaunchConfigurationConstants.DEFAULT_RUN_MARKER;
import static org.eclipse.ceylon.ide.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVASCRIPT_MODULE;
import static org.eclipse.ceylon.ide.eclipse.core.launch.LaunchHelper.getStartLocation;
import static org.eclipse.ceylon.ide.eclipse.util.CeylonHelper.toJavaStringList;
import static org.eclipse.ceylon.ide.eclipse.util.InteropUtils.toJavaString;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaMethodBreakpoint;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.JDIDebugPlugin;
import org.eclipse.jdt.internal.launching.LaunchingPlugin;
import org.eclipse.jdt.internal.launching.StandardVMDebugger;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import org.eclipse.ceylon.common.Constants;
import org.eclipse.ceylon.common.Versions;
import org.eclipse.ceylon.ide.eclipse.core.debug.model.CeylonJDIDebugTarget;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.common.model.CeylonIdeConfig;
import org.eclipse.ceylon.ide.common.model.CeylonProject;
import com.sun.jdi.VirtualMachine;

public class ModuleLaunchDelegate extends JavaLaunchDelegate {

    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
        return "org.eclipse.ceylon.launcher.Launcher";
    }
    
    @Override
    public String getProgramArguments(ILaunchConfiguration configuration)
            throws CoreException {
        final List<IPath> workingRepos = new ArrayList<IPath>();
        final IProject project = getJavaProject(configuration).getProject();
        ArrayList<IProject> projects = new ArrayList<>(project.getReferencedProjects().length+1);
        projects.add(project);
        projects.addAll(Arrays.asList(project.getReferencedProjects()));
        for(IProject p : projects) {
            try {// projects may not exist in workspace
                IPath test = getCeylonModulesOutputFolder(p).getLocation();
                workingRepos.add(test);
            } catch (Exception e) {
                continue;
            }
        }

        boolean runAsJs = DebugPlugin.getDefault().getLaunchManager()
                .getLaunchConfigurationType(ID_CEYLON_JAVASCRIPT_MODULE)
                    .equals(configuration.getType());
        
        List<String> newArgs = new ArrayList<String>();
        prepareArguments(newArgs, workingRepos, project, configuration, runAsJs);

        List<String> args = Arrays.asList(DebugPlugin.parseArguments(super.getProgramArguments(configuration)));
        newArgs.addAll(args);

        return DebugPlugin.renderArguments(newArgs.toArray(new String[0]), null);
    }



    @Override
    public String getVMArguments(ILaunchConfiguration configuration)
            throws CoreException {
        IProject project = getJavaProject(configuration).getProject();
        
        //user values at the end although JVMs behave differently
        List<String> vmArgs = new ArrayList<String>();
        vmArgs.add("-Dceylon.system.version="+Versions.CEYLON_VERSION_NUMBER);
        vmArgs.add("-Dceylon.system.repo="+getInterpolatedCeylonSystemRepo(project));
        File javaDebugAgentPath = CeylonPlugin.getInstance().getDebugAgentJar();
        if (javaDebugAgentPath != null) {
            vmArgs.add("-javaagent:" + javaDebugAgentPath.getAbsolutePath());
        }
        
        CeylonIdeConfig ideConfig = modelJ2C().ideConfig(project);
        if (ideConfig != null) {
            ceylon.language.String nodePath = ideConfig.getNodePath();
            if (nodePath != null) {
                vmArgs.add("-D" + Constants.PROP_CEYLON_EXTCMD_NODE + "=" + nodePath.toString());
            }
        }
        vmArgs.addAll(Arrays.asList(DebugPlugin.parseArguments(super.getVMArguments(configuration))));
        return DebugPlugin.renderArguments(vmArgs.toArray(new String[0]), null);
    }



    @Override
    public IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode) throws CoreException {
        final IVMRunner runner = super.getVMRunner(configuration, mode);
        
        if (runner instanceof StandardVMDebugger) {
            IVMInstall vmInstall = getVMInstall(configuration);
            return new StandardVMDebugger(vmInstall){
                @Override
                public void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor) 
                        throws CoreException {
                    try {
                        super.run(config, launch, monitor);
                    } catch (final Exception e) {
                        e.printStackTrace();
                        throw new CoreException(new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID, "Ceylon Module Launcher Error", e));
                    }
                }
                
                @Override
                protected IDebugTarget createDebugTarget(
                        final VMRunnerConfiguration config, final ILaunch launch, final int port,
                        final IProcess process, final VirtualMachine vm) {
                    final IDebugTarget[] target = new IDebugTarget[1];
                    IWorkspaceRunnable r = new IWorkspaceRunnable() {
                        public void run(IProgressMonitor m) {
                            target[0] = new CeylonJDIDebugTarget(launch, vm, 
                                    renderDebugTarget(config.getClassToLaunch(), port),
                                    true, false, process, config.isResumeOnStartup());
                        }
                    };
                    try {
                        ResourcesPlugin.getWorkspace().run(r, null, 0, null);
                    } catch (CoreException e) {
                        JDIDebugPlugin.log(e);
                    }
                    return target[0];
                }
            };
        } else {
            return runner;
        }
    }
    
    protected void prepareArguments(List<String> args, List<IPath> workingRepos, IProject project, 
            ILaunchConfiguration configuration, boolean runAsJs) throws CoreException {
        if (runAsJs) {
            args.add("run-js");
        } else {
            args.add("run");
        }
        
        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = modelJ2C().ceylonModel().getProject(project);
        prepareRepositoryArguments(args, ceylonProject, workingRepos);
        prepareOverridesArgument(args, ceylonProject);
        prepareFlatClasspathArgument(args, ceylonProject);
        prepareAutoExportMavenDependencies(args, ceylonProject);
        prepareFullyExportMavenDependencies(args, ceylonProject);
        prepareOfflineArgument(args, ceylonProject);
        if (configuration.getAttribute(ATTR_LAUNCH_VERBOSE, false)) {
            prepareVerboseArgument(args, runAsJs);
        }
        
        String topLevel = configuration.getAttribute(ATTR_TOPLEVEL_NAME, "");
        int def = topLevel.indexOf(DEFAULT_RUN_MARKER);
        if (def != -1) {
            topLevel = topLevel.substring(0, def);
        }
        if (!"".equals(topLevel) && def == -1) { // default run not found
            args.add("--run");
            args.add(topLevel);
        }
        
        args.add("--");
        args.add(configuration.getAttribute(ATTR_MODULE_NAME, ""));
    }

    protected void prepareRepositoryArguments(List<String> args, 
            CeylonProject<IProject,IResource,IFolder,IFile> project, List<IPath> workingRepos) {
        for (IPath repo : workingRepos) {
            args.add("--rep");
            args.add(repo.toOSString());
        }
        
        for (String repo: toJavaStringList(project.getConfiguration()
                .getProjectLocalRepos())) {
            args.add("--rep");
            args.add(repo);                      
        }
    }

    protected void prepareOverridesArgument(List<String> args, CeylonProject<IProject,IResource,IFolder,IFile> project) {
        String overrides = toJavaString(project.getConfiguration().getOverrides());
        if (overrides != null) {
            args.add("--overrides=" + overrides);
        }
    }

    protected void prepareFlatClasspathArgument(List<String> args, CeylonProject<IProject,IResource,IFolder,IFile> project) {
        boolean flatClasspath = project.getConfiguration().getFlatClasspath();
        if (flatClasspath) {
            args.add("--flat-classpath");
        }
    }

    protected void prepareAutoExportMavenDependencies(List<String> args, CeylonProject<IProject,IResource,IFolder,IFile> project) {
        boolean autoExportMavenDependencies = project.getConfiguration().getAutoExportMavenDependencies();
        if (autoExportMavenDependencies) {
            args.add("--auto-export-maven-dependencies");
        }
    }

    protected void prepareFullyExportMavenDependencies(List<String> args, CeylonProject<IProject,IResource,IFolder,IFile> project) {
        boolean fullyExportMavenDependencies = project.getConfiguration().getFullyExportMavenDependencies();
        if (fullyExportMavenDependencies) {
            args.add("--fully-export-maven-dependencies");
        }
    }

    protected void prepareOfflineArgument(List<String> args, CeylonProject<IProject,IResource,IFolder,IFile> project) {
        if (project.getConfiguration().getOffline()) {
            args.add("--offline");
        }
    }


    
    protected void prepareVerboseArgument(List<String> args, boolean runAsJs) {
        args.add("--verbose");        
        if (runAsJs) {
            args.add("--debug");
            args.add("debug"); //
        }
    }
    
    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {

        List<String> classpathList = new ArrayList<String>();
        // at runtime, we just need ceylon-bootstrap; everything else should be from the system repo
        classpathList.addAll(CeylonPlugin.getModuleLauncherJars());

        return classpathList.toArray(new String[classpathList.size()]);
    }   

    @Override
    protected boolean isLaunchProblem(IMarker problemMarker)
            throws CoreException {
        if (super.isLaunchProblem(problemMarker)) {
            return true; 
        }
        if (!problemMarker.getType().equals(PROBLEM_MARKER_ID)) {
            return false;
        }
        Integer severity = (Integer) problemMarker.getAttribute(IMarker.SEVERITY);
        if (severity!=null) {
            return severity.intValue()>=IMarker.SEVERITY_ERROR;
        }
        return false;
    }
    
    @Override
    public void handleDebugEvents(DebugEvent[] events) {
        for (int i = 0; i < events.length; i++) {
            DebugEvent event = events[i];
            if (event.getKind() == DebugEvent.CREATE
                    && event.getSource() instanceof IJavaDebugTarget) {
                IJavaDebugTarget target = (IJavaDebugTarget) event.getSource();
                ILaunch launch = target.getLaunch();
                if (launch != null) {
                    ILaunchConfiguration configuration = launch
                            .getLaunchConfiguration();
                    if (configuration != null) {
                        try {
                            if (isStopInMain(configuration)) {
                                String location=getStartLocation(configuration);
                                
                                String type = null;
                                String method = null;
                                if (location != null) {
                                    String[] parts = location.split("/");
                                    type = parts[0];
                                    if (parts.length > 1)
                                    method = parts[1];
                                }
                                if (type != null) {
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map
                                            .put(
                                                    IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN,
                                                    IJavaLaunchConfigurationConstants.ATTR_STOP_IN_MAIN);
                                    IJavaMethodBreakpoint bp = JDIDebugModel
                                            .createMethodBreakpoint(
                                                    ResourcesPlugin
                                                            .getWorkspace()
                                                            .getRoot(),
                                                    type, method, //$NON-NLS-1$
                                                    "()V",
                                                    true, false, false, -1, -1,
                                                    -1, 1, false, map); 
                                    bp.setPersisted(false);
                                    target.breakpointAdded(bp);
                                    DebugPlugin.getDefault()
                                            .removeDebugEventListener(this);
                                }
                            }
                        } catch (CoreException e) {
                            LaunchingPlugin.log(e);
                        }
                    }
                }
            }
        }
    }
    
}
