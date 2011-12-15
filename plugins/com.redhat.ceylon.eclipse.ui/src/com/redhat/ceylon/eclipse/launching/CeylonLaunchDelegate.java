package com.redhat.ceylon.eclipse.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.io.ArtifactProvider;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;
import com.redhat.ceylon.compiler.typechecker.io.impl.FileSystemVirtualFile;
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
        RepositoryLister archiveLister = new RepositoryLister();
        
        //first add modules in the current project
        //(we want them first in the classpath)
        File outputDirectory = CeylonBuilder.getOutputDirectory(javaProject);
        if (outputDirectory != null) {
            archiveLister.list(outputDirectory, new RepositoryLister.Actions() {
                @Override
                public void doWithFile(File path) {
                    classpathList.add(path.getAbsolutePath());
                }
            });
        }
        
        //then add modules in the module repo
        //TODO: don't add modules with the same name as
        //      as a module belonging to the project
        for (ArtifactProvider provider : typeChecker.getContext().getArtifactProviders()) {
            VirtualFile repository = provider.getHomeRepo();
            if (repository instanceof FileSystemVirtualFile) {
                archiveLister.list(((FileSystemVirtualFile)repository).getFile(), new RepositoryLister.Actions() {
                    @Override
                    public void doWithFile(File path) {
                        classpathList.add(path.getAbsolutePath());
                    }
                });
            }
            else {
                System.out.println("Ignoring non-filesystem repositories for launching classpath");
            }
        }
                
        // Also add the language car
        classpathList.add(languageCar); 

        return classpathList.toArray(new String [classpathList.size()]);
    }
}
