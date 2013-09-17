package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.DEFAULT_RUN_MARKER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;

import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class ModuleLaunchDelegate extends JavaLaunchDelegate {

    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
        return "com.redhat.ceylon.launcher.Launcher";
    }
    
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        
        super.launch(configuration, mode, launch, monitor);
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
            public void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor) throws CoreException {

                try {
                    List<String> newArgs = new ArrayList<String>();
                    
                    newArgs.add("run");

                    for (IPath repo : workingRepos) {
                        newArgs.add("--rep");
                        newArgs.add(repo.toOSString());
                    }
                    if (CeylonProjectConfig.get(project).isOffline()) {
                        newArgs.add("--offline");
                    }
                    if (launch.getLaunchMode().equals("debug")) {
                        newArgs.add("--verbose");
                    }

                    String topLevel = launch.getLaunchConfiguration()
                        .getAttribute(ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME, "");
                    int def = topLevel.indexOf(DEFAULT_RUN_MARKER);
                    if (def != -1) {
                        topLevel = topLevel.substring(0, def);
                    }
                    if (!"".equals(topLevel) && def == -1) { // default run not found
                        newArgs.add("--run");
                        newArgs.add(topLevel);
                    }
                    
                    newArgs.add("--");
                    
                    newArgs.add(launch.getLaunchConfiguration()
                        .getAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, ""));

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
                	MessageDialog.openError(Util.getShell(), "Ceylon Module Launcher Error", 
                            "Internal Error");
                }
            }
        };
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
