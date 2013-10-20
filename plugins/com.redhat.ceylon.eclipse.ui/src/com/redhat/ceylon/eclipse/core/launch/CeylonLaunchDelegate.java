package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathContainers;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer.getModuleArchive;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer.isProjectModule;
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
import org.eclipse.jdt.core.IClasspathContainer;
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
import com.redhat.ceylon.eclipse.core.classpath.CeylonProjectModulesContainer;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonLaunchDelegate extends JavaLaunchDelegate {

    @Override
    public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
        // ignore the return value but still verify it
        super.verifyMainTypeName(configuration);
        // we currently run everything via a launcher that sets up the runtime module system using info we pass it
        // by overriding it here for our superclass, as it converts a LaunchConfiguration to a VMRunnerConfiguration, we ensure
        // that the launch configuration is not modified and saved properly, one per main class/method, rather than a single one
        // point to our launcher for every main class/method
        return "com.redhat.ceylon.compiler.java.runtime.ide.Launcher";
    }
    
    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
        try {
            // before we launch anything, we create a module descriptor file for the runtime module system, where we store the info
            // for required modules
            File tmpFile = File.createTempFile("eclipse-ceylon-launcher", ".txt");
            writeModuleInfoFile(tmpFile, configuration);
            // then we save the path to the file and the main method to invoke in the launch, to be accessed in getVMRunner
            launch.setAttribute("CEYLON_MODULE_DESCRIPTOR", tmpFile.getAbsolutePath());
            launch.setAttribute("CEYLON_MAIN", configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String)null));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        super.launch(configuration, mode, launch, monitor);
    }

    /**
     * Prints a list of module name, version, path to the given file, to be used by the runtime launcher, because passing it
     * as an argument/environment would easily exceed the OS size limits.
     */
    private void writeModuleInfoFile(File tmpFile, ILaunchConfiguration configuration) throws IOException, CoreException {
        FileWriter writer = new FileWriter(tmpFile);
        IJavaProject javaProject = getJavaProject(configuration);
        IProject project = javaProject.getProject();
        Context context = getProjectTypeChecker(project).getContext();

        RepositoryManager provider = context.getRepositoryManager();
        Set<Module> modulesToAdd = context.getModules().getListOfModules();
        boolean seenDefault = false;
        for (Module module: modulesToAdd) {
            String name = module.getNameAsString(); 
            if (JDKUtils.isJDKModule(name) ||
                    JDKUtils.isOracleJDKModule(name)) {
                continue;
            }
            if(module.isDefault())
                seenDefault = true;
            IPath modulePath = getModuleArchive(provider, module);
            
            // If CMR did not find and it exists in the project, 
            // assume it is in output folder (for corner cases like beginning with "ceylon" etc.)
            // there is a file exists check right after this, so it can't hurt
            if (modulePath == null && isProjectModule(javaProject, module)) {
                IPath modulesFolder = getCeylonModulesOutputFolder(project).getLocation();
                modulePath = modulesFolder
                        .append(module.getNameAsString().replace(".", File.separator))
                        .append(module.getVersion())
                        .append(module.getNameAsString()+"-"+module.getVersion()+".car");                
            }
            
            if (modulePath != null && modulePath.toFile().exists()) {
                String path = modulePath.toOSString();
                System.err.println("Adding module: "+module.getNameAsString()+"/"+module.getVersion()+": "+path);
                // print module name + NL (+ version + NL)? + path + NL
                writer.append(module.getNameAsString());
                writer.append(System.lineSeparator());
                if(!module.isDefault()){
                    writer.append(module.getVersion());
                    writer.append(System.lineSeparator());
                }
                writer.append(path);
                writer.append(System.lineSeparator());
            }
        }
        // for some reason the default module can be missing from the list of modules
        if(!seenDefault){
            IPath modulesFolder = getCeylonModulesOutputFolder(project).getLocation();
            IPath defaultCar = modulesFolder.append("default").append("default.car");
            if(defaultCar.toFile().exists()){
                String path = defaultCar.toOSString();
                Module module = context.getModules().getDefaultModule();
                System.err.println("Adding default module: "+module.getNameAsString()+": "+path);
                // print module name + NL + path + NL
                writer.append(module.getNameAsString());
                writer.append(System.lineSeparator());
                writer.append(path);
                writer.append(System.lineSeparator());
            }
        }
        writer.flush();
        writer.close();
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
                newArgs[0] = launch.getAttribute("CEYLON_MODULE_DESCRIPTOR");
                newArgs[1] = launch.getAttribute("CEYLON_MAIN");
                config.setProgramArguments(newArgs);

                runner.run(config, launch, monitor);
            }
        };
    }
    
    @Override
    public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {
        IJavaProject javaProject = getJavaProject(configuration);

        String[] javaClasspath = getJavaClasspath(configuration);
        String[] ceylonProjectClasspath = getCeylonProjectClasspath(javaProject);

        List<String> classpathList = new ArrayList<String>();
        classpathList.addAll(asList(javaClasspath));
        classpathList.addAll(asList(ceylonProjectClasspath));
        // at runtime, we need the compiler/common/typechecker/cmr jars to be present for the runtime module system
        classpathList.addAll(CeylonPlugin.getRuntimeRequiredJars());

        return classpathList.toArray(new String[classpathList.size()]);
    }
    
    // overridden by test subclass
    protected String[] getJavaClasspath(ILaunchConfiguration configuration) throws CoreException {
        return super.getClasspath(configuration);
    }
    
    // overridden by test subclass
    protected String[] getCeylonProjectClasspath(IJavaProject javaProject) throws JavaModelException {
        final List<String> classpathList = new ArrayList<String>();
        
        for (IClasspathContainer container : getCeylonClasspathContainers(javaProject)) {
            if (container instanceof CeylonProjectModulesContainer) {
                CeylonProjectModulesContainer applicationModulesContainer = (CeylonProjectModulesContainer) container;
                boolean changed = applicationModulesContainer.resolveClasspath(new NullProgressMonitor(), false);
                if(changed) {
                    applicationModulesContainer.refreshClasspathContainer(new NullProgressMonitor());
                }
            }
        }
        
        //add the car files of the output directory
        IProject project = javaProject.getProject();
        Context context = getProjectTypeChecker(project).getContext();

        IPath modulesFolder = getCeylonModulesOutputFolder(project).getLocation();
        classpathList.add(modulesFolder.append("default").append("default.car").toOSString());

        RepositoryManager provider = context.getRepositoryManager();
        Set<Module> modulesToAdd = context.getModules().getListOfModules();
        //modulesToAdd.add(projectModules.getLanguageModule());        
        for (Module module: modulesToAdd) {
            String name = module.getNameAsString(); 
            if (name.equals(Module.DEFAULT_MODULE_NAME) ||
                    JDKUtils.isJDKModule(name) ||
                    JDKUtils.isOracleJDKModule(name) ||
                    !isProjectModule(javaProject, module)) {
                continue;
            }
            IPath modulePath = getModuleArchive(provider, module);
            if (modulePath!=null) {
                if (modulePath.toFile().exists()) {
                    //if (project.getLocation().isPrefixOf(modulePath)) {
                    //if (!classpathList.contains(modulePath.toOSString())) {
                        classpathList.add(modulePath.toOSString());
                    //}
                    //}
                } 
                else {
                    System.err.println("ignoring nonexistent module artifact for launch classpath: " + 
                            modulePath);
                }
            }
            else {
                System.err.println("no module archive found for launch classpath: " + 
                        module.getNameAsString() + "/" + module.getVersion());
            }
        }

        return classpathList.toArray(new String [classpathList.size()]);
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
