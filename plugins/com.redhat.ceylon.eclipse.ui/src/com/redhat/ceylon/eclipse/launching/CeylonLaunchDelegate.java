package com.redhat.ceylon.eclipse.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.antlr.gunit.gUnitParser.file_return;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.compiler.typechecker.io.ArtifactProvider;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.impl.FileSystemVirtualFile;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.util.RepositoryLister;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;

public class CeylonLaunchDelegate extends JavaLaunchDelegate {

    private static String languageVersion = "0.1";
    private final static String languageCar = System.getProperty("user.home")+"/.ceylon/repo/ceylon/language/"+languageVersion +"/ceylon.language-"+languageVersion+".car";

    
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
            if (! module.equals(projectModules.getDefaultModule())) {
                modulesToAdd.add(module); 
            }
        }
        
        List<ArtifactProvider> providersToSearch = new ArrayList<ArtifactProvider>();
        
        File outputDirectory = CeylonBuilder.getOutputDirectory(javaProject);
        if (outputDirectory != null) {
            ArtifactProvider outputProvider = new ArtifactProvider(new FileSystemVirtualFile(outputDirectory), context.getVfs());
            providersToSearch.add(outputProvider);
        }
        
        classpathList.add(new File(new File(outputDirectory, "default"), "default.car").getAbsolutePath());

        providersToSearch.addAll(context.getArtifactProviders());
        
        for (Module module : modulesToAdd) {
            boolean artifactFound = false;
            for (ArtifactProvider provider : providersToSearch) {
                VirtualFile moduleArtifact = provider.getArtifact(module.getName(), module.getVersion(), Arrays.asList("car", "jar"));
                if (moduleArtifact != null) {
                    String modulePath = moduleArtifact.getPath();
                    File moduleFile = new File(modulePath);
                    if (moduleFile.exists()) {
                        classpathList.add(moduleFile.getAbsolutePath());
                        artifactFound = true;
                        break;
                    } else {
                        System.out.println("Ignoring non-existing module artifact (" + modulePath + ") for launching classpath");
                    }
                }
            }
            if (! artifactFound) {
                System.out.println("Artifact not found for module '" + module.getNameAsString() + "/" + module.getVersion() + "' for launching classpath");
            }
        }

        return classpathList.toArray(new String [classpathList.size()]);
    }
}
