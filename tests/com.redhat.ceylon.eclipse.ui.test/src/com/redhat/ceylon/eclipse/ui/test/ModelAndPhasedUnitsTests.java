/*************************************************************************************
 * Copyright (c) 2011 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package com.redhat.ceylon.eclipse.ui.test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.BeforeClass;
import org.junit.Test;

import com.redhat.ceylon.compiler.loader.ModelLoader.DeclarationType;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.CeylonUnit;
import com.redhat.ceylon.eclipse.core.model.CrossProjectSourceFile;
import com.redhat.ceylon.eclipse.core.model.ExternalSourceFile;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;

import com.redhat.ceylon.eclipse.core.typechecker.CrossProjectPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ExternalPhasedUnit;
import com.redhat.ceylon.eclipse.core.typechecker.ProjectPhasedUnit;

public class ModelAndPhasedUnitsTests {
    static private AssertionError compilationError = null;
    static private IProject mainProject;
    static private IProject referencedCeylonProject;
    static private IProject referencedJavaProject;
    static private String projectGroup = "model-and-phased-units";
    static private TypeChecker typeChecker = null;
    static private JDTModelLoader modelLoader = null;
    
    @BeforeClass
    public static void importAndBuildProjects() {
        try {
            IPath projectDescriptionPath = null;
            IPath userDirPath = new Path(System.getProperty("user.dir"));
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IPath projectPathPrefix = userDirPath.append("resources/" + projectGroup + "/");
            
            try {
                projectDescriptionPath = projectPathPrefix.append("referenced-java-project/.project");
                referencedJavaProject = Utils.importProject(workspace, projectGroup, projectDescriptionPath);
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
    
    @SuppressWarnings("unchecked")
    private <T extends PhasedUnit> T checkPhasedUnitClass(String phasedUnitPath, Class<T> phasedUnitClass) {
        PhasedUnit pu = null;
        
        pu = typeChecker.getPhasedUnitFromRelativePath(phasedUnitPath);
        Assert.assertNotNull("No phased unit for path : " + phasedUnitPath, pu);
        Assert.assertEquals(pu.getUnitFile().getName(), phasedUnitClass, pu.getClass());
        
        return (T) pu;
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Unit> T checkDeclarationUnit(String declarationName, 
            Class<T> unitClass, 
            String fullPath, 
            String relativePath, 
            String fileName) {
        Declaration declaration = modelLoader.getDeclaration(declarationName, DeclarationType.VALUE);
        Assert.assertNotNull("No declaration for name = " + declarationName, declaration);
        Unit unit = declaration.getUnit();
        Assert.assertNotNull("Null Unit for declaration : " + declarationName, unit);
        Assert.assertEquals("Unit for declaration : " + declarationName, unitClass, unit.getClass());
        Assert.assertEquals("Unit Full Path for declaration : " + declarationName, fullPath, unit.getFullPath());
        Assert.assertEquals("Unit Relative Path for declaration : " + declarationName, relativePath, unit.getRelativePath());
        Assert.assertEquals("Unit Filename for declaration : " + declarationName, fileName, unit.getFilename());
        return (T) unit;
    }
    

    @Test
    public void checkMainProjectPhasedUnits() throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        checkPhasedUnitClass("usedModule/CeylonDeclarations_Main_Ceylon_Project.ceylon", 
                ProjectPhasedUnit.class);
    }
    
    @Test
	public void checkExternalPhasedUnits() throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        checkPhasedUnitClass("source_and_binary_external_module/CeylonDeclarations_External_Source_Binary.ceylon", 
                ExternalPhasedUnit.class);
    }
    
    @Test
    public void checkReferencedProjectPhasedUnits() throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        CrossProjectPhasedUnit pu;
        ProjectPhasedUnit opu;

        pu = checkPhasedUnitClass("referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon", 
                CrossProjectPhasedUnit.class);
        opu = pu.getOriginalProjectPhasedUnit();
        Assert.assertEquals("referenced-ceylon-project", opu.getProjectResource().getName());
    }
    

    @SuppressWarnings("unchecked")
    public <T extends CeylonUnit> T checkCeylonSourceUnits(Class<T> unitClass,
            String root,
            String moduleName, 
            String declarationSuffix) throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        CeylonUnit unit;
        CeylonUnit unit2;
        
        unit = checkDeclarationUnit(moduleName + ".CeylonTopLevelClass_" + declarationSuffix, 
                unitClass, 
                root + "/" + moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                "CeylonDeclarations_" + declarationSuffix + ".ceylon");

        unit2 = checkDeclarationUnit(moduleName + ".ceylonTopLevelObject_" + declarationSuffix, 
                unitClass, 
                root + "/" + moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                "CeylonDeclarations_" + declarationSuffix + ".ceylon");
        Assert.assertTrue("Different units " + Arrays.asList(unit, unit2) + " for declarations in the same Unit", unit == unit2);

        unit2 = checkDeclarationUnit(moduleName + ".ceylonTopLevelMethod_" + declarationSuffix, 
                unitClass, 
                root + "/" + moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                "CeylonDeclarations_" + declarationSuffix + ".ceylon");
        Assert.assertTrue("Different units " + Arrays.asList(unit, unit2) + " for declarations in the same Unit", unit == unit2);

        Assert.assertTrue("PhasedUnit of unit : " + unit.getFullPath() + " is not the same as the typechecker one", 
                unit.getPhasedUnit() == typeChecker.getPhasedUnitFromRelativePath(moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon"));
        return (T) unit;
    }
    

    @Test
    public void checkExternalSourceBinaryCeylonUnits() throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }

        IFile archiveFile = mainProject.getFile("imported_modules/source_and_binary_external_module/1.0.0/source_and_binary_external_module-1.0.0.src");
        Assert.assertNotNull(archiveFile);
        checkCeylonSourceUnits(ExternalSourceFile.class,
                archiveFile.getLocation().toString() + "!",
                "source_and_binary_external_module", 
                "External_Source_Binary");
    }
    
    @Test
    public void checkMainProjectCeylonUnits() throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        ProjectSourceFile unit = checkCeylonSourceUnits(ProjectSourceFile.class,
                "src",
                "usedModule",
                "Main_Ceylon_Project");
        Assert.assertEquals("Eclipse Project Resource for Unit : " + unit.getFullPath(),
                mainProject,
                unit.getProjectResource());
        Assert.assertEquals("Eclipse Root Folder Resource for Unit : " + unit.getFullPath(),
                mainProject.getFolder("src"),
                unit.getRootFolderResource());
        Assert.assertEquals("Eclipse File Resource for Unit : " + unit.getFullPath(),
                mainProject.getFile("src/" + unit.getRelativePath()),
                unit.getFileResource());
    }
    
    @Test
    public void checkReferencedProjectCeylonUnits() throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        IFile archiveFile = referencedCeylonProject.getFile("modules/referencedCeylonProject/1.0.0/referencedCeylonProject-1.0.0.src");
        Assert.assertNotNull(archiveFile);

        CrossProjectSourceFile unit = checkCeylonSourceUnits(CrossProjectSourceFile.class,
                archiveFile.getLocation().toString() + "!",
                "referencedCeylonProject",
                "Referenced_Ceylon_Project");
        Assert.assertEquals("Eclipse Project Resource for Unit : " + unit.getFullPath(),
                referencedCeylonProject,
                unit.getProjectResource());
        Assert.assertEquals("Eclipse Root Folder Resource for Unit : " + unit.getFullPath(),
                referencedCeylonProject.getFolder("src"),
                unit.getRootFolderResource());
        Assert.assertEquals("Eclipse File Resource for Unit : " + unit.getFullPath(),
                referencedCeylonProject.getFile("src/" + unit.getRelativePath()),
                unit.getFileResource());
    }
}