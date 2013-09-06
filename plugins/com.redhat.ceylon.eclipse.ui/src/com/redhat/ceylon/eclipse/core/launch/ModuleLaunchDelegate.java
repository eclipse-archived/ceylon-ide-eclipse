package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer.getModuleArchive;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer.isProjectModule;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathContainers;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaLaunchDelegate;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class ModuleLaunchDelegate extends JavaLaunchDelegate {

    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
        return "ceylon.modules.bootstrap.CeylonRunTool";
    }
    
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        
        super.launch(configuration, mode, launch, monitor);
    }

    
    @Override
    public IVMRunner getVMRunner(ILaunchConfiguration configuration, String mode) throws CoreException {
        final IVMRunner runner = super.getVMRunner(configuration, mode);
        // return a wrapper that does some tricks
        return new IVMRunner(){

            @Override
            public void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor) throws CoreException {
                // in order to not modify the LaunchConfiguration, we replace the program arguments by inserting the path to
                // the module descriptor and the main class/method name before the user program arguments
                
                // insert our args: descriptor file + main class
                String[] args = config.getProgramArguments();
                String[] newArgs = new String[args != null ? args.length + 2 : 2];
                if(args != null)
                    System.arraycopy(args, 0, newArgs, 2, args.length);
                newArgs[0] = "ceylon.modules.bootstrap.CeylonRunTool.class";
                newArgs[1] = "test.html/1.0.0";
                config.setProgramArguments(newArgs);

                runner.run(config, launch, monitor);
            }
        };
    }
    
    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {

        List<String> classpathList = new ArrayList<String>();

        // at runtime, we need the compiler/common/typechecker/cmr jars to be present for the runtime module system
        classpathList.addAll(CeylonPlugin.getRuntimeRequiredJars());

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
