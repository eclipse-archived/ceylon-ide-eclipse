package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer.getModuleArchive;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer.isProjectModule;
import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.model.Module;

public class CeylonLaunchDelegate extends JavaLaunchDelegate {

    @Override
    public String[] getClasspath(ILaunchConfiguration configuration)
            throws CoreException {

    	String[] javaClasspath = super.getClasspath(configuration);
        final List<String> classpathList = new ArrayList<String>(asList(javaClasspath));
        
        //add the car files of the output directory
        
        IJavaProject javaProject = getJavaProject(configuration);
        IProject project = javaProject.getProject();
		Context context = getProjectTypeChecker(project).getContext();

        IPath modulesFolder = getCeylonModulesOutputFolder(javaProject).getLocation();
        classpathList.add(modulesFolder.append("default").append("default.car").toOSString());

        RepositoryManager provider = context.getRepositoryManager();
        Set<Module> modulesToAdd = new HashSet<Module>(context.getModules().getListOfModules());
        //modulesToAdd.add(projectModules.getLanguageModule());        
    	for (Module module: modulesToAdd) {
    		if (module.getNameAsString().equals("default") ||
    				module.getNameAsString().equals("java") ||
    				!isProjectModule(javaProject, module)) {
    			continue;
    		}
    		IPath modulePath = getModuleArchive(provider, module);
            if (modulePath!=null) {
            	if (modulePath.toFile().exists()) {
					//if (project.getLocation().isPrefixOf(modulePath)) {
            			classpathList.add(modulePath.toOSString());
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
