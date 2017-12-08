/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.ui.test;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swtbot.swt.finder.utils.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.core.model.JDTModelLoader;
import org.eclipse.ceylon.ide.eclipse.ui.test.Utils.CeylonBuildSummary;

@SuppressWarnings("deprecation")
public abstract class AbstractMultiProjectTest {

    protected static String projectGroup = "model-and-phased-units";
    protected static String referencedCeylonProjectName = "referenced-ceylon-project";
    protected static String mainProjectName = "main-ceylon-project";
    protected static IPath projectPathPrefix = new Path(System.getProperty("user.dir")).append("resources/" + projectGroup + "/");

    protected final static IWorkspace workspace = ResourcesPlugin.getWorkspace();

    protected static AssertionError compilationError = null;
    protected static IProject mainProject;
    protected static IJavaProject mainProjectJDT;
    protected static IProject referencedCeylonProject;
    protected static IJavaProject referencedCeylonProjectJDT;
    protected static TypeChecker typeChecker = null;
    protected static JDTModelLoader modelLoader = null;

    
    @BeforeClass
    public static void beforeClass() throws InterruptedException {
        importAndBuild();
    }

    @AfterClass
    public static void afterClass() throws CoreException {
        try {
            if (mainProject != null) mainProject.delete(true, true, null);
        } catch(CoreException e) {
            e.printStackTrace();
        }
        try {
            if (referencedCeylonProject != null) referencedCeylonProject.delete(true, true, null);
        } catch(CoreException e) {
            e.printStackTrace();
        }
    }
    
    public static IFile copyFileFromResources(String testPrefix, String path, IProject destinationProject, String destinationPath) throws CoreException {
        IPath pathToCopy = projectPathPrefix.append("test-source-files/" + testPrefix + "/" + path);
        IFile result = destinationProject.getFile(destinationPath + "/" + path); 
        result.create(new ByteArrayInputStream(FileUtils.read(pathToCopy.toFile()).getBytes(Charset.forName("UTF-8"))), true, null);
        destinationProject.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
        return result;
    }
    
    public static void importAndBuild() throws InterruptedException {
        try {
            IPath projectDescriptionPath = null;
            
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            
            CeylonBuildSummary summary = new CeylonBuildSummary(workspace.getRoot().getProject(mainProjectName));
            summary.install();

            try {
                projectDescriptionPath = projectPathPrefix.append(referencedCeylonProjectName + "/.project");
                referencedCeylonProject = Utils.importProject(workspace, projectGroup, projectDescriptionPath);
                referencedCeylonProjectJDT = JavaCore.create(referencedCeylonProject);
            }
            catch(Exception e) {
                Assert.fail("Import of the referenced ceylon project failed with the exception : \n" + e.toString());
            }
    
                
            try {
                projectDescriptionPath = projectPathPrefix.append(mainProjectName + "/.project");
                mainProject = Utils.importProject(workspace, projectGroup,
                        projectDescriptionPath);
                mainProjectJDT = JavaCore.create(mainProject);
            }
            catch(Exception e) {
                Assert.fail("Build of the main project failed with the exception : \n" + e.toString());
            }
            

            assertTrue("A build should have been started after import", summary.waitForBuildEnd(120));
            
            Assert.assertNotNull("Referenced ceylon project compilation didn't produce a Car file", 
                    referencedCeylonProject.exists(new Path("modules/referencedCeylonProject/1.0.0/referencedCeylonProject-1.0.0.car")));
            Assert.assertNotNull("Referenced ceylon project compilation didn't produce a Src file", 
                    referencedCeylonProject.exists(new Path("modules/referencedCeylonProject/1.0.0/referencedCeylonProject-1.0.0.src")));
    
            Assert.assertNotNull("Main ceylon project compilation didn't produce the main module Car file", 
                    mainProject.exists(new Path("modules/mainModule/1.0.0/mainModule-1.0.0.car")));
            Assert.assertNotNull("Main ceylon project compilation didn't produce the main module Src file", 
                    mainProject.exists(new Path("modules/mainModule/1.0.0/mainModule-1.0.0.src")));
            Assert.assertNotNull("Main ceylon project compilation didn't produce the used module Car file", 
                    mainProject.exists(new Path("modules/usedModule/1.0.0/usedModule-1.0.0.car")));
            Assert.assertNotNull("Main ceylon project compilation didn't produce the used module Src file", 
                    mainProject.exists(new Path("modules/usedModule/1.0.0/usedModule-1.0.0.src")));
    
            typeChecker = CeylonBuilder.getProjectTypeChecker(mainProject);
            modelLoader = (JDTModelLoader) CeylonBuilder.getProjectModelLoader(mainProject);
        }
        catch(AssertionError e) {
            compilationError = e;
        }
    }

    public AbstractMultiProjectTest() {
        super();
    }

}