package com.redhat.ceylon.eclipse.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.cmr.impl.FileContentStore;
import com.redhat.ceylon.cmr.impl.SimpleRepositoryManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.EclipseLogger;

public class CeylonLaunchDelegate extends JavaLaunchDelegate {

    @Override
    public String[] getClasspath(ILaunchConfiguration configuration)
            throws CoreException {
        String[] javaClasspath = super.getClasspath(configuration);
        
        // Also add the car files of the output directory
        IJavaProject javaProject = getJavaProject(configuration);
        final List<String> classpathList = new ArrayList<String>(Arrays.asList(javaClasspath));

        TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(javaProject.getProject());

        Context context = typeChecker.getContext();
        Modules projectModules = context.getModules();

        Set<Module> modulesToAdd = new HashSet<Module>();
        modulesToAdd.add(projectModules.getLanguageModule());
        for (Module module : projectModules.getListOfModules()) {
            if (!module.equals(projectModules.getDefaultModule())) {
                modulesToAdd.add(module); 
            }
        }
        
        List<RepositoryManager> repositoryManagers = new ArrayList<RepositoryManager>();
        
        File outputDirectory = CeylonBuilder.getOutputDirectory(javaProject);
        if (outputDirectory != null) {
        	RepositoryManager outputRepo = new SimpleRepositoryManager(new FileContentStore(outputDirectory), new EclipseLogger());
            repositoryManagers.add(outputRepo);
        }
        
        classpathList.add(new File(new File(outputDirectory, "default"), "default.car").getAbsolutePath());

        repositoryManagers.add(context.getRepositoryManager());
        
        for (Module module : modulesToAdd) {
            boolean artifactFound = false;
            ArtifactContext ctx = new ArtifactContext(module.getNameAsString(), module.getVersion());
            for (RepositoryManager provider : repositoryManagers) {
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
                    String modulePath = moduleArtifact.getPath();
                    File moduleFile = new File(modulePath);
                    if (moduleFile.exists()) {
                        classpathList.add(moduleFile.getAbsolutePath());
                        artifactFound = true;
                        break;
                    } 
                    else {
                        System.out.println("Ignoring non-existing module artifact (" + modulePath + ") for launching classpath");
                    }
                }
            }
            if (!artifactFound) {
                System.out.println("Artifact not found for module '" + module.getNameAsString() + "/" + module.getVersion() + "' for launching classpath");
            }
        }

        return classpathList.toArray(new String [classpathList.size()]);
    }
}
