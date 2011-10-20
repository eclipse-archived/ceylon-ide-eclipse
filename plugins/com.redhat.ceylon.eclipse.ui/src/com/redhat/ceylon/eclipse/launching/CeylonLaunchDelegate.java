package com.redhat.ceylon.eclipse.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

public class CeylonLaunchDelegate extends JavaLaunchDelegate {

    private static String languageVersion = "0.1";
    private final static String languageCar = System.getProperty("user.home")+"/.ceylon/repo/ceylon/language/"+languageVersion +"/ceylon.language-"+languageVersion+".car";

    @Override
    public String[] getClasspath(ILaunchConfiguration configuration)
            throws CoreException {
        String[] javaClasspath = super.getClasspath(configuration);
        
        // Also add the car files of the output directory
        IJavaProject javaProject = getJavaProject(configuration);
        IProject project = javaProject.getProject();
        File outputDirectory = project.getFolder(javaProject.getOutputLocation().makeRelativeTo(project.getFullPath())).getRawLocation().toFile();
        List<String> carFiles = new ArrayList<String>(); 
        retrieveCarFiles(outputDirectory, carFiles);

        // Also add the language car
        carFiles.add(languageCar); 

        List<String> resultList = new ArrayList<String>(Arrays.asList(javaClasspath));
        resultList.addAll(carFiles);
        return resultList.toArray(new String [resultList.size()]);
    }

    private void retrieveCarFiles(File path, List<String> files) {
        if (path.isDirectory()) {
            for (File f : path.listFiles()) {
                retrieveCarFiles(f, files);
            }
        }
        else if (path.isFile() && 
                (path.getName().endsWith(".car") || path.getName().endsWith(".jar"))) {
            files.add(path.getAbsolutePath());
        }
    }
    
}
