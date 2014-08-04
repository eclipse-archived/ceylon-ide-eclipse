package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_LAUNCH_VERBOSE;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.DEFAULT_RUN_MARKER;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ID_CEYLON_JAVASCRIPT_MODULE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;

import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class ModuleLaunchDelegate extends JavaLaunchDelegate {

    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
        return "com.redhat.ceylon.launcher.Launcher";
    }
    
    @Override
    public IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode) throws CoreException {
        final IVMRunner runner = super.getVMRunner(configuration, mode);
        
        IProject[] requiredProjects = getBuildOrder(configuration, mode);       
        final List<IPath> workingRepos = new ArrayList<IPath>();
        final IProject project = getJavaProject(configuration).getProject();
        
        for(int i = requiredProjects.length -1; i>=0 ; i--) { //contains current as well, reversed
            try {// projects may not exist in workspace
                IPath test =getCeylonModulesOutputFolder(requiredProjects[i]).getLocation();
                workingRepos.add(test);
            } catch (Exception e) {
                continue;
            }
        }
        
        return new IVMRunner(){

            @Override
            public void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor) 
                    throws CoreException {
                try {
                    List<String> newArgs = new ArrayList<String>();
                    
                    boolean runAsJs = DebugPlugin.getDefault().getLaunchManager()
                            .getLaunchConfigurationType(ID_CEYLON_JAVASCRIPT_MODULE)
                                .equals(launch.getLaunchConfiguration().getType());
                    
                    prepareArguments(newArgs, workingRepos, project, launch, runAsJs);

                    List<String> args = Arrays.asList(config.getProgramArguments());
                    newArgs.addAll(args);
                    
                    config.setProgramArguments(newArgs.toArray(new String[]{}));
                    
                    //user values at the end although JVMs behave differently
                    List<String> vmArgs = new ArrayList<String>();
                    vmArgs.add("-Dceylon.system.version="+Versions.CEYLON_VERSION_NUMBER);
                    vmArgs.add("-Dceylon.system.repo="+CeylonPlugin.getCeylonPluginRepository(
                        System.getProperty("ceylon.repo", "")).getAbsolutePath());
                    
                    vmArgs.addAll(Arrays.asList(config.getVMArguments()));
                    config.setVMArguments(vmArgs.toArray(new String[]{}));

                    runner.run(config, launch, monitor);
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageDialog.openError(EditorUtil.getShell(), "Ceylon Module Launcher Error", 
                            "Internal Error");
                }
            }
        };
    }
    
    protected void prepareArguments(List<String> args, List<IPath> workingRepos, IProject project, 
            ILaunch launch, boolean runAsJs) throws CoreException {
        if (runAsJs) {
            args.add("run-js");
        } else {
            args.add("run");
        }
        
        prepareRepositoryArguments(args, project, workingRepos);
        prepareOfflineArgument(args, project);
        if (launch.getLaunchConfiguration().getAttribute(ATTR_LAUNCH_VERBOSE, false)) {
            prepareVerboseArgument(args, launch, runAsJs);
        }
        
        String topLevel = launch.getLaunchConfiguration()
                .getAttribute(ATTR_TOPLEVEL_NAME, "");
        int def = topLevel.indexOf(DEFAULT_RUN_MARKER);
        if (def != -1) {
            topLevel = topLevel.substring(0, def);
        }
        if (!"".equals(topLevel) && def == -1) { // default run not found
            args.add("--run");
            args.add(topLevel);
        }
        
        args.add("--");
        args.add(launch.getLaunchConfiguration()
                .getAttribute(ATTR_MODULE_NAME, ""));
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

    protected void prepareVerboseArgument(List<String> args, ILaunch launch, boolean runAsJs) {
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
