package com.redhat.ceylon.eclipse.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaLaunchDelegate;

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
        List<File> carFiles = CeylonBuilder.retrieveCarFiles(javaProject);

        List<String> resultList = new ArrayList<String>(Arrays.asList(javaClasspath));
        for (File file : carFiles) {
            resultList.add(file.getAbsolutePath());
        }
        
        // Also add the language car
        resultList.add(languageCar); 

        return resultList.toArray(new String [resultList.size()]);
    }
}
