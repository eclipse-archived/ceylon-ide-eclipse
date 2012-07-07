package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static java.util.Arrays.asList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import com.redhat.ceylon.cmr.api.ArtifactContext;
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
        Context context = getProjectTypeChecker(javaProject.getProject()).getContext();

        IPath modulesFolder = getCeylonModulesOutputFolder(javaProject).getLocation();
        classpathList.add(modulesFolder.append("default").append("default.car").toOSString());

		IPath projectLoc = javaProject.getProject().getLocation();
        RepositoryManager provider = context.getRepositoryManager();
        Set<Module> modulesToAdd = new HashSet<Module>(context.getModules().getListOfModules());
        //modulesToAdd.add(projectModules.getLanguageModule());        
    	for (Module module: modulesToAdd) {
    		if (module.getNameAsString().equals("default") ||
    				module.getNameAsString().equals("java")) {
    			continue;
    		}
            boolean artifactFound = false;
            ArtifactContext ctx = new ArtifactContext(module.getNameAsString(), module.getVersion());
            // try first with car
            ctx.setSuffix(ArtifactContext.CAR);
            File moduleArtifact = null;
            moduleArtifact = provider.getArtifact(ctx);
            if (moduleArtifact == null){
            	// try with .jar
            	ctx.setSuffix(ArtifactContext.JAR);
            	moduleArtifact = provider.getArtifact(ctx);
            }
            if (moduleArtifact != null) {
            	IPath modulePath = new Path(moduleArtifact.getPath());
            	if (modulePath.toFile().exists()) {
            		artifactFound = true;
					if (projectLoc.isPrefixOf(modulePath)) {
            			classpathList.add(modulePath.toOSString());
            		}
            	} 
            	else {
            		System.err.println("ignoring nonexistent module artifact for launch classpath: " + 
            				modulePath);
            	}
            }
            if (!artifactFound) {
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
