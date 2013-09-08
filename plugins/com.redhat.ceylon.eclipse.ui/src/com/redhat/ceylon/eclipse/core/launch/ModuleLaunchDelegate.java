package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.redhat.ceylon.common.Versions;
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
        
        IJavaProject javaProject = getJavaProject(configuration);
        IProject project = javaProject.getProject();

        final IPath modulesFolder = getCeylonModulesOutputFolder(project).getLocation();
        
        return new IVMRunner(){

            @Override
            public void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor) throws CoreException {
                // in order to not modify the LaunchConfiguration, we replace the program arguments by inserting the path to
                // the module descriptor and the main class/method name before the user program arguments

                try {
                    String[] args = config.getProgramArguments();
                    String[] newArgs = new String[args != null ? args.length + 4 : 4];
                    if(args != null)
                        System.arraycopy(args, 0, newArgs, 4, args.length);

                    newArgs[0] = "run";
                    newArgs[2] = "--rep";
                    newArgs[3] = modulesFolder.toOSString(); 
                    newArgs[1] = launch.getLaunchConfiguration().getAttribute(ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME, ""); 
                              
                    config.setProgramArguments(newArgs);
                    config.setVMArguments(new String[]{"-Djava.util.logging.manager=java.util.logging.LogManager",
                            "-Dceylon.system.version="+Versions.CEYLON_VERSION_NUMBER,
                            "-Dceylon.system.repo="+CeylonPlugin.getCeylonPluginRepository(
                                System.getProperty("ceylon.repo", "")).getAbsolutePath()});

                    runner.run(config, launch, monitor);
                } catch (Exception e) {
                    throw new CoreException(new StatusInfo());
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
