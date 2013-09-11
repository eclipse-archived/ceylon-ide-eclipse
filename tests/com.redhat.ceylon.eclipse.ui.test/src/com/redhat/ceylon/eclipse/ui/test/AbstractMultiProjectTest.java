package com.redhat.ceylon.eclipse.ui.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;

public abstract class AbstractMultiProjectTest {

    protected static AssertionError compilationError = null;
    protected static IProject mainProject;
    protected static IJavaProject mainProjectJDT;
    protected static IProject referencedCeylonProject;
    protected static IJavaProject referencedCeylonProjectJDT;
    protected static IProject referencedJavaProject;
    protected static IJavaProject referencedJavaProjectJDT;
    protected static String projectGroup = "model-and-phased-units";
    protected static TypeChecker typeChecker = null;
    protected static JDTModelLoader modelLoader = null;

    @BeforeClass
    public static void beforeClass() {
        importAndBuild();
    }

    @AfterClass
    public static void afterClass() {
        try {
            referencedJavaProject.delete(true, true, null);
        } catch(CoreException e) {
            e.printStackTrace();
        }
        try {
            referencedCeylonProject.delete(true, true, null);
        } catch(CoreException e) {
            e.printStackTrace();
        }
        try {
            mainProject.delete(true, true, null);
        } catch(CoreException e) {
            e.printStackTrace();
        }
    }
    
    public static void importAndBuild() {
        try {
            IPath projectDescriptionPath = null;
            IPath userDirPath = new Path(System.getProperty("user.dir"));
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IPath projectPathPrefix = userDirPath.append("resources/" + projectGroup + "/");
            
            try {
                projectDescriptionPath = projectPathPrefix.append("referenced-java-project/.project");
                referencedJavaProject = Utils.importProject(workspace, projectGroup, projectDescriptionPath);
                referencedJavaProjectJDT = JavaCore.create(referencedJavaProject);
            }
            catch(Exception e) {
                Assert.fail("Import of the referenced java project failed with the exception : \n" + e.toString());
            }
    
            try {
                buildProject(referencedJavaProject);
            }
            catch(Exception e) {
                Assert.fail("Build of the referenced java project failed with the exception : \n" + e.toString());
            }
    
            try {
                projectDescriptionPath = projectPathPrefix.append("referenced-ceylon-project/.project");
                referencedCeylonProject = Utils.importProject(workspace, projectGroup, projectDescriptionPath);
                referencedCeylonProjectJDT = JavaCore.create(referencedCeylonProject);
            }
            catch(Exception e) {
                Assert.fail("Import of the referenced ceylon project failed with the exception : \n" + e.toString());
            }
    
            
            try {
                buildProject(referencedCeylonProject);
            }
            catch(Exception e) {
                Assert.fail("Build of the referenced ceylon project failed with the exception : \n" + e.toString());
            }
    
            Assert.assertNotNull("Referenced ceylon project compilation didn't produce a Car file", 
                    referencedCeylonProject.exists(new Path("modules/referencedCeylonProject/1.0.0/referencedCeylonProject-1.0.0.car")));
            Assert.assertNotNull("Referenced ceylon project compilation didn't produce a Src file", 
                    referencedCeylonProject.exists(new Path("modules/referencedCeylonProject/1.0.0/referencedCeylonProject-1.0.0.src")));
    
            try {
                projectDescriptionPath = projectPathPrefix.append("main-ceylon-project/.project");
                mainProject = Utils.importProject(workspace, projectGroup,
                        projectDescriptionPath);
                mainProjectJDT = JavaCore.create(mainProject);
                mainProjectJDT = JavaCore.create(mainProject);
            }
            catch(Exception e) {
                Assert.fail("Build of the main project failed with the exception : \n" + e.toString());
            }
            
            try {
                buildProject(mainProject);
            }
            catch(Exception e) {
                Assert.fail("Build of the main project failed with the exception : \n" + e.toString());
            }
    
            Assert.assertNotNull("Main ceylon project compilation didn't produce the main module Car file", 
                    mainProject.exists(new Path("modules/mainModule/1.0.0/mainModule-1.0.0.car")));
            Assert.assertNotNull("Main ceylon project compilation didn't produce the main module Src file", 
                    mainProject.exists(new Path("modules/mainModule/1.0.0/mainModule-1.0.0.src")));
            Assert.assertNotNull("Main ceylon project compilation didn't produce the used module Car file", 
                    mainProject.exists(new Path("modules/usedModule/1.0.0/usedModule-1.0.0.car")));
            Assert.assertNotNull("Main ceylon project compilation didn't produce the used module Src file", 
                    mainProject.exists(new Path("modules/usedModule/1.0.0/usedModule-1.0.0.src")));
    
            typeChecker = CeylonBuilder.getProjectTypeChecker(mainProject);
            modelLoader = CeylonBuilder.getProjectModelLoader(mainProject);
        }
        catch(AssertionError e) {
            compilationError = e;
        }
    }

    public static void buildProject(IProject project) throws CoreException,
            InterruptedException {
                final CountDownLatch build;
                
                build = new CountDownLatch(1);
                project.build(IncrementalProjectBuilder.FULL_BUILD, new IProgressMonitor() {
                    @Override
                    public void worked(int work) {
                    }
                    @Override
                    public void subTask(String name) {
                    }
                    @Override
                    public void setTaskName(String name) {
                    }
                    @Override
                    public void setCanceled(boolean value) {
                    }
                    @Override
                    public boolean isCanceled() {
                        return false;
                    }
                    @Override
                    public void internalWorked(double work) {
                    }
                    @Override
                    public void beginTask(String name, int totalWork) {
                    }
                    @Override
                    public void done() {
                        build.countDown();
                    }
                });
                build.await(60, TimeUnit.SECONDS);
            }

    public AbstractMultiProjectTest() {
        super();
    }

}