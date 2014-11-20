package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_LAUNCH_VERBOSE;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.DEFAULT_RUN_MARKER;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVASCRIPT_MODULE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.internal.debug.core.JDIDebugPlugin;
import org.eclipse.jdt.internal.launching.StandardVMDebugger;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;

import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.core.debug.CeylonJDIDebugTarget;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;
import com.sun.jdi.VirtualMachine;

public class ModuleLaunchDelegate extends JavaLaunchDelegate {

    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
        return "com.redhat.ceylon.launcher.Launcher";
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
        //user values at the end although JVMs behave differently
        List<String> vmArgs = new ArrayList<String>();
        vmArgs.add("-Dceylon.system.version="+Versions.CEYLON_VERSION_NUMBER);
        vmArgs.add("-Dceylon.system.repo="+CeylonPlugin.getCeylonPluginRepository(
            System.getProperty("ceylon.repo", "")).getAbsolutePath());
        
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
                    } catch (Exception e) {
                        e.printStackTrace();
                        MessageDialog.openError(EditorUtil.getShell(), "Ceylon Module Launcher Error", 
                                "Internal Error");
                    }
                }
                
                @Override
                protected IDebugTarget createDebugTarget(
                        final VMRunnerConfiguration config, final ILaunch launch, final int port,
                        final IProcess process, final VirtualMachine vm) {
                    final IJavaDebugTarget[] target = new IJavaDebugTarget[1];
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
        
        prepareRepositoryArguments(args, project, workingRepos);
        prepareOfflineArgument(args, project);
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
            IProject project, List<IPath> workingRepos) {
        for (IPath repo : workingRepos) {
            args.add("--rep");
            args.add(repo.toOSString());
        }
        
        for (String repo: CeylonProjectConfig.get(project)
                .getProjectLocalRepos()) {
            args.add("--rep");
            args.add(repo);                      
        }
    }

    protected void prepareOfflineArgument(List<String> args, IProject project) {
        if (CeylonProjectConfig.get(project).isOffline()) {
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
}
