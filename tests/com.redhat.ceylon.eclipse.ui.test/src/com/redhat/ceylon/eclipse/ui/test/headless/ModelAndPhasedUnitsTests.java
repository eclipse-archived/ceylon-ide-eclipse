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
package org.eclipse.ceylon.ide.eclipse.ui.test.headless;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.junit.Assert;
import org.junit.Test;

import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.ui.test.AbstractMultiProjectTest;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.ide.common.model.CeylonBinaryUnit;
import org.eclipse.ceylon.ide.common.model.CeylonUnit;
import org.eclipse.ceylon.ide.common.model.CrossProjectBinaryUnit;
import org.eclipse.ceylon.ide.common.model.CrossProjectSourceFile;
import org.eclipse.ceylon.ide.common.model.ExternalSourceFile;
import org.eclipse.ceylon.ide.common.model.ICrossProjectReference;
import org.eclipse.ceylon.ide.common.model.ICrossProjectCeylonReference;
import org.eclipse.ceylon.ide.common.model.JavaClassFile;
import org.eclipse.ceylon.ide.common.model.JavaCompilationUnit;
import org.eclipse.ceylon.ide.common.model.ProjectSourceFile;
import org.eclipse.ceylon.ide.common.typechecker.CrossProjectPhasedUnit;
import org.eclipse.ceylon.ide.common.typechecker.ExternalPhasedUnit;
import org.eclipse.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import org.eclipse.ceylon.model.cmr.JDKUtils;
import org.eclipse.ceylon.model.loader.AbstractModelLoader;
import org.eclipse.ceylon.model.loader.ModelLoader.DeclarationType;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Unit;

@SuppressWarnings("restriction")
public class ModelAndPhasedUnitsTests extends AbstractMultiProjectTest {

    @SuppressWarnings("unchecked")
    private <T extends PhasedUnit> T checkProjectPhasedUnitClass(String phasedUnitPath, Class<T> phasedUnitClass) {
        PhasedUnit pu = null;
        
        pu = typeChecker.getPhasedUnitFromRelativePath(phasedUnitPath);
        Assert.assertNotNull("No phased unit for path : " + phasedUnitPath, pu);
        Assert.assertEquals(pu.getUnitFile().getName(), phasedUnitClass, pu.getClass());
        
        return (T) pu;
    }

    @SuppressWarnings("unchecked")
    private <T extends PhasedUnit> T checkExternalPhasedUnitClass(String moduleName, String phasedUnitPath, Class<T> expectedPhasedUnitClass) {
        PhasedUnit pu = null;
        
        BaseIdeModule module = (BaseIdeModule) modelLoader.getLoadedModule(moduleName, null);
        pu = module.getPhasedUnitFromRelativePath(phasedUnitPath);
        Assert.assertNotNull("No phased unit for path : " + phasedUnitPath, pu);
        Class<? extends PhasedUnit> phasedUnitClass = pu.getClass();
        if (phasedUnitClass.isAnonymousClass()) {
            phasedUnitClass = (Class<? extends PhasedUnit>) phasedUnitClass.getSuperclass();
        }
        Assert.assertEquals(pu.getUnitFile().getName(), expectedPhasedUnitClass, phasedUnitClass);
        
        return (T) pu;
    }

    @SuppressWarnings("unchecked")
    private <T extends Unit> T checkDeclarationUnit(Module module, String declarationName, 
            Class<T> unitClass, 
            String fullPath, 
            String relativePath, 
            String fileName) {
        Declaration declaration = modelLoader.getDeclaration(module, declarationName, DeclarationType.VALUE);
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
        
        checkProjectPhasedUnitClass("usedModule/CeylonDeclarations_Main_Ceylon_Project.ceylon", 
                ProjectPhasedUnit.class);
    }
    
    @Test
	public void checkExternalPhasedUnits() throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        checkExternalPhasedUnitClass("source_and_binary_external_module", "source_and_binary_external_module/CeylonDeclarations_External_Source_Binary.ceylon", 
                ExternalPhasedUnit.class);
    }
    
    @Test
    public void checkReferencedProjectPhasedUnits() throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        CrossProjectPhasedUnit<IProject,IResource,IFolder,IFile> pu;
        ProjectPhasedUnit<IProject,IResource,IFolder,IFile> opu;

        pu = checkExternalPhasedUnitClass("referencedCeylonProject", "referencedCeylonProject/CeylonDeclarations_Referenced_Ceylon_Project.ceylon", 
                CrossProjectPhasedUnit.class);
        opu = pu.getOriginalProjectPhasedUnit();
        Assert.assertEquals("referenced-ceylon-project", opu.getResourceProject().getName());
    }
    

    public <T extends CeylonBinaryUnit> List<T> checkCeylonBinaryUnits(Class<T> unitClass,
            String root,
            String moduleName, 
            String declarationSuffix) throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        List<T> result = new LinkedList<>();
        
        Module module = modelLoader.findModule(moduleName, "1.0.0");
        
        result.add(checkDeclarationUnit(module, moduleName + ".CeylonTopLevelClass_" + declarationSuffix, 
                unitClass, 
                root + "/" + moduleName + "/CeylonTopLevelClass_" + declarationSuffix + ".class",  
                moduleName + "/CeylonTopLevelClass_" + declarationSuffix + ".class", 
                "CeylonTopLevelClass_" + declarationSuffix + ".class"));

        result.add(checkDeclarationUnit(module, moduleName + ".ceylonTopLevelObject_" + declarationSuffix, 
                unitClass, 
                root + "/" + moduleName + "/ceylonTopLevelObject_" + declarationSuffix + "_.class", 
                moduleName + "/ceylonTopLevelObject_" + declarationSuffix + "_.class", 
                "ceylonTopLevelObject_" + declarationSuffix + "_.class"));

        result.add(checkDeclarationUnit(module, moduleName + ".ceylonTopLevelMethod_" + declarationSuffix, 
                unitClass, 
                root + "/" + moduleName + "/ceylonTopLevelMethod_" + declarationSuffix + "_.class", 
                moduleName + "/ceylonTopLevelMethod_" + declarationSuffix + "_.class", 
                "ceylonTopLevelMethod_" + declarationSuffix + "_.class"));
        
        String ceylonFileName = "CeylonDeclarations_" + declarationSuffix + ".ceylon";
        String ceylonSourceRelativePath = moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon"; 

        for (CeylonBinaryUnit unit : result) {
            Assert.assertEquals(ceylonSourceRelativePath, unit.getCeylonSourceRelativePath());
            Assert.assertEquals(ceylonFileName, unit.getCeylonFileName());
        }
        
        return result;
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
        
        Module module = modelLoader.findModule(moduleName, "1.0.0");
        
        unit = checkDeclarationUnit(module, moduleName + ".CeylonTopLevelClass_" + declarationSuffix, 
                unitClass, 
                root + "/" + moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                "CeylonDeclarations_" + declarationSuffix + ".ceylon");

        unit2 = checkDeclarationUnit(module, moduleName + ".ceylonTopLevelObject_" + declarationSuffix, 
                unitClass, 
                root + "/" + moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                moduleName + "/CeylonDeclarations_" + declarationSuffix + ".ceylon", 
                "CeylonDeclarations_" + declarationSuffix + ".ceylon");
        Assert.assertTrue("Different units " + Arrays.asList(unit, unit2) + " for declarations in the same Unit", unit == unit2);

        unit2 = checkDeclarationUnit(module, moduleName + ".ceylonTopLevelMethod_" + declarationSuffix, 
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
        
        if (CeylonBuilder.loadDependenciesFromModelLoaderFirst(mainProject)) {
            IFile archiveFile = mainProject.getFile("imported_modules/source_and_binary_external_module/1.0.0/source_and_binary_external_module-1.0.0.car");
            Assert.assertNotNull(archiveFile);
            List<CeylonBinaryUnit> binaryUnits = checkCeylonBinaryUnits(CeylonBinaryUnit.class,
                    archiveFile.getLocation().toString() + "!",
                    "source_and_binary_external_module", 
                    "External_Source_Binary");

            for (CeylonBinaryUnit unit : binaryUnits) {
                Assert.assertEquals(unit.getProject(), mainProject);
                Assert.assertEquals(unit.getTypeRoot(), mainProjectJDT.findElement(Path.fromPortableString(unit.getRelativePath())));
            }
        } else {
            IFile archiveFile = mainProject.getFile("imported_modules/source_and_binary_external_module/1.0.0/source_and_binary_external_module-1.0.0.src");
            Assert.assertNotNull(archiveFile);
            checkCeylonSourceUnits(ExternalSourceFile.class,
                    archiveFile.getLocation().toString() + "!",
                    "source_and_binary_external_module", 
                    "External_Source_Binary");
        }
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
                unit.getResourceProject());
        Assert.assertEquals("Eclipse Root Folder Resource for Unit : " + unit.getFullPath(),
                mainProject.getFolder("src"),
                unit.getResourceRootFolder());
        Assert.assertEquals("Eclipse File Resource for Unit : " + unit.getFullPath(),
                mainProject.getFile("src/" + unit.getRelativePath()),
                unit.getResourceFile());
    }
    
    @Test
    public void checkReferencedProjectCeylonUnits() throws CoreException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (compilationError != null) {
            throw compilationError;
        }

        Map<String, ICrossProjectCeylonReference> crossProjectReferences = new HashMap<>();
        
        if (CeylonBuilder.loadDependenciesFromModelLoaderFirst(mainProject)) {
            IFile archiveFile = referencedCeylonProject.getFile("modules/referencedCeylonProject/1.0.0/referencedCeylonProject-1.0.0.car");
            Assert.assertNotNull(archiveFile);
            List<CrossProjectBinaryUnit> binaryUnits = checkCeylonBinaryUnits(CrossProjectBinaryUnit.class,
                    archiveFile.getLocation().toString() + "!",
                    "referencedCeylonProject", 
                    "Referenced_Ceylon_Project");

            for (CrossProjectBinaryUnit unit : binaryUnits) {
                Assert.assertEquals(unit.getProject(), mainProject);
                Assert.assertEquals(unit.getTypeRoot(), mainProjectJDT.findElement(Path.fromPortableString(unit.getRelativePath())));
                crossProjectReferences.put(unit.getFullPath(), unit);
            }
            
        } else {
            IFile archiveFile = referencedCeylonProject.getFile("modules/referencedCeylonProject/1.0.0/referencedCeylonProject-1.0.0.src");
            Assert.assertNotNull(archiveFile);

            CrossProjectSourceFile unit = checkCeylonSourceUnits(CrossProjectSourceFile.class,
                    archiveFile.getLocation().toString() + "!",
                    "referencedCeylonProject",
                    "Referenced_Ceylon_Project");
            crossProjectReferences.put(unit.getFullPath(), unit);
        }

        for (Map.Entry<String, ICrossProjectCeylonReference> reference : crossProjectReferences.entrySet()) {
            Assert.assertEquals("Eclipse Project Resource for Unit : " + reference.getKey(),
                    referencedCeylonProject,
                    reference.getValue().getResourceProject());
            Assert.assertEquals("Eclipse Root Folder Resource for Unit : " + reference.getKey(),
                    referencedCeylonProject.getFolder("src"),
                    reference.getValue().getResourceRootFolder());
            Assert.assertEquals("Eclipse File Resource for Unit : " + reference.getKey(),
                    referencedCeylonProject.getFile("src/" + reference.getValue().getOriginalPhasedUnit().getPathRelativeToSrcDir()),
                    reference.getValue().getResourceFile());
        }
    }
    
    @Test 
    public void checkJavaLibrayrUnits() throws CoreException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        IJavaProject javaProject = JavaCore.create(mainProject);
        
        String jarName = null;
        IClassFile javaElement = null;
        for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
            if (root instanceof JarPackageFragmentRoot) {
                JarPackageFragmentRoot jarRoot = (JarPackageFragmentRoot) root;
                IPackageFragment pkg = root.getPackageFragment("java.util.logging");
                if (pkg.exists()) {
                    javaElement = pkg.getClassFile("Logger.class");
                    jarName = jarRoot.getJar().getName();
                    break;
                }
            }
        }
        
        Module module = modelLoader.findModule(AbstractModelLoader.JAVA_BASE_MODULE_NAME, JDKUtils.jdk.version);
        
        JavaClassFile javaClass = checkDeclarationUnit(module, "java.util.logging.Logger", 
                JavaClassFile.class, 
                jarName + "!/" + "java/util/logging/Logger.class", 
                "java/util/logging/Logger.class", 
                "Logger.class");
        Assert.assertEquals("Wrong Java Element : ", javaElement, javaClass.getTypeRoot());
        Assert.assertNull("Project Resource should be null :", javaClass.getResourceProject());
        Assert.assertNull("Root Folder Resource should be null :", javaClass.getResourceRootFolder());
        Assert.assertNull("File Resource should be null :", javaClass.getResourceFile());
    }
    
    @Test 
    public void checkMainProjectJavaCeylonUnits() throws CoreException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        IJavaProject javaProject = JavaCore.create(mainProject);
        
        String rootPath = null;
        ICompilationUnit javaClassElement = null;
        ICompilationUnit javaObjectElement = null;
        ICompilationUnit javaMethodElement = null;
        for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
            IPackageFragment pkg = root.getPackageFragment("mainModule");
            if (pkg.exists() && pkg.getCompilationUnit("JavaCeylonTopLevelClass_Main_Ceylon_Project.java").exists()) {
                javaClassElement = pkg.getCompilationUnit("JavaCeylonTopLevelClass_Main_Ceylon_Project.java");
                javaObjectElement = pkg.getCompilationUnit("javaCeylonTopLevelObject_Main_Ceylon_Project_.java");
                javaMethodElement = pkg.getCompilationUnit("javaCeylonTopLevelMethod_Main_Ceylon_Project_.java");
                rootPath = root.getPath().toOSString();
                break;
            }
        }
        
        Module module = modelLoader.findModule("mainModule", "1.0.0");
        JavaCompilationUnit javaClassCompilationUnit = checkDeclarationUnit(module, "mainModule.JavaCeylonTopLevelClass_Main_Ceylon_Project", 
                JavaCompilationUnit.class, 
                rootPath + "/" + "mainModule/JavaCeylonTopLevelClass_Main_Ceylon_Project.java", 
                "mainModule/JavaCeylonTopLevelClass_Main_Ceylon_Project.java", 
                "JavaCeylonTopLevelClass_Main_Ceylon_Project.java");
        Assert.assertEquals("Wrong Java Element for Class : ", javaClassElement, javaClassCompilationUnit.getTypeRoot());
        Assert.assertNotNull("Project Resource  for Class should not be null :", javaClassCompilationUnit.getResourceProject());
        Assert.assertNotNull("Root Folder Resource  for Class should not be null :", javaClassCompilationUnit.getResourceRootFolder());
        Assert.assertNotNull("File Resource should  for Class not be null :", javaClassCompilationUnit.getResourceFile());

        JavaCompilationUnit javaObjectCompilationUnit = checkDeclarationUnit(module, "mainModule.javaCeylonTopLevelObject_Main_Ceylon_Project", 
                JavaCompilationUnit.class, 
                rootPath + "/" + "mainModule/javaCeylonTopLevelObject_Main_Ceylon_Project_.java", 
                "mainModule/javaCeylonTopLevelObject_Main_Ceylon_Project_.java", 
                "javaCeylonTopLevelObject_Main_Ceylon_Project_.java");
        Assert.assertEquals("Wrong Java Element for Object : ", javaObjectElement, javaObjectCompilationUnit.getTypeRoot());
        Assert.assertNotNull("Project Resource  for Object should not be null :", javaObjectCompilationUnit.getResourceProject());
        Assert.assertNotNull("Root Folder Resource  for Object should not be null :", javaObjectCompilationUnit.getResourceRootFolder());
        Assert.assertNotNull("File Resource should  for Object not be null :", javaObjectCompilationUnit.getResourceFile());

        JavaCompilationUnit javaMethodCompilationUnit = checkDeclarationUnit(module, "mainModule.javaCeylonTopLevelMethod_Main_Ceylon_Project_", 
                JavaCompilationUnit.class, 
                rootPath + "/" + "mainModule/javaCeylonTopLevelMethod_Main_Ceylon_Project_.java", 
                "mainModule/javaCeylonTopLevelMethod_Main_Ceylon_Project_.java", 
                "javaCeylonTopLevelMethod_Main_Ceylon_Project_.java");
        Assert.assertEquals("Wrong Java Element for Method : ", javaMethodElement, javaMethodCompilationUnit.getTypeRoot());
        Assert.assertNotNull("Project Resource  for Method should not be null :", javaMethodCompilationUnit.getResourceProject());
        Assert.assertNotNull("Root Folder Resource  for Method should not be null :", javaMethodCompilationUnit.getResourceRootFolder());
        Assert.assertNotNull("File Resource should  for Method not be null :", javaMethodCompilationUnit.getResourceFile());
    }
    
    @Test 
    public void checkMainProjectPureJavaUnits() throws CoreException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        IJavaProject javaProject = JavaCore.create(mainProject);
        
        String rootPath = null;
        ICompilationUnit javaElement = null;
        for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
            IPackageFragment pkg = root.getPackageFragment("mainModule");
            if (pkg.exists() && pkg.getCompilationUnit("JavaClassInCeylonModule_Main_Ceylon_Project.java").exists()) {
                javaElement = pkg.getCompilationUnit("JavaClassInCeylonModule_Main_Ceylon_Project.java");
                rootPath = root.getPath().toOSString();
                break;
            }
        }
        
        Module module = modelLoader.findModule("mainModule", "1.0.0");

        JavaCompilationUnit javaClassCompilationUnit = checkDeclarationUnit(module, "mainModule.JavaClassInCeylonModule_Main_Ceylon_Project", 
                JavaCompilationUnit.class, 
                rootPath + "/" + "mainModule/JavaClassInCeylonModule_Main_Ceylon_Project.java", 
                "mainModule/JavaClassInCeylonModule_Main_Ceylon_Project.java", 
                "JavaClassInCeylonModule_Main_Ceylon_Project.java");
        Assert.assertEquals("Wrong Java Element for Pure Java Class : ", javaElement, javaClassCompilationUnit.getTypeRoot());
        Assert.assertNotNull("Project Resource  for Pure Java Class should not be null :", javaClassCompilationUnit.getResourceProject());
        Assert.assertNotNull("Root Folder Resource  Pure Java for Class should not be null :", javaClassCompilationUnit.getResourceRootFolder());
        Assert.assertNotNull("File Resource should  Pure Java for Class not be null :", javaClassCompilationUnit.getResourceFile());
    }
    
    @Test 
    public void checkReferencedProjectJavaCeylonUnits() throws CoreException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        IJavaProject javaProject = JavaCore.create(referencedCeylonProject);
        
        String rootPath = null;
        ICompilationUnit javaClassElement = null;
        ICompilationUnit javaObjectElement = null;
        ICompilationUnit javaMethodElement = null;
        for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
            IPackageFragment pkg = root.getPackageFragment("referencedCeylonProject");
            if (pkg.exists() && pkg.getCompilationUnit("JavaCeylonTopLevelClass_Referenced_Ceylon_Project.java").exists()) {
                javaClassElement = pkg.getCompilationUnit("JavaCeylonTopLevelClass_Referenced_Ceylon_Project.java");
                javaObjectElement = pkg.getCompilationUnit("javaCeylonTopLevelObject_Referenced_Ceylon_Project_.java");
                javaMethodElement = pkg.getCompilationUnit("javaCeylonTopLevelMethod_Referenced_Ceylon_Project_.java");
                rootPath = root.getPath().toOSString();
                break;
            }
        }
        Module module = modelLoader.findModule("referencedCeylonProject", "1.0.0");

        JavaCompilationUnit javaClassCompilationUnit = checkDeclarationUnit(module, "referencedCeylonProject.JavaCeylonTopLevelClass_Referenced_Ceylon_Project", 
                JavaCompilationUnit.class, 
                rootPath + "/" + "referencedCeylonProject/JavaCeylonTopLevelClass_Referenced_Ceylon_Project.java", 
                "referencedCeylonProject/JavaCeylonTopLevelClass_Referenced_Ceylon_Project.java", 
                "JavaCeylonTopLevelClass_Referenced_Ceylon_Project.java");
        Assert.assertEquals("Wrong Java Element for Class : ", javaClassElement, javaClassCompilationUnit.getTypeRoot());
        Assert.assertNotNull("Project Resource  for Class should not be null :", javaClassCompilationUnit.getResourceProject());
        Assert.assertNotNull("Root Folder Resource  for Class should not be null :", javaClassCompilationUnit.getResourceRootFolder());
        Assert.assertNotNull("File Resource should  for Class not be null :", javaClassCompilationUnit.getResourceFile());

        JavaCompilationUnit javaObjectCompilationUnit = checkDeclarationUnit(module, "referencedCeylonProject.javaCeylonTopLevelObject_Referenced_Ceylon_Project", 
                JavaCompilationUnit.class, 
                rootPath + "/" + "referencedCeylonProject/javaCeylonTopLevelObject_Referenced_Ceylon_Project_.java", 
                "referencedCeylonProject/javaCeylonTopLevelObject_Referenced_Ceylon_Project_.java", 
                "javaCeylonTopLevelObject_Referenced_Ceylon_Project_.java");
        Assert.assertEquals("Wrong Java Element for Object : ", javaObjectElement, javaObjectCompilationUnit.getTypeRoot());
        Assert.assertNotNull("Project Resource  for Object should not be null :", javaObjectCompilationUnit.getResourceProject());
        Assert.assertNotNull("Root Folder Resource  for Object should not be null :", javaObjectCompilationUnit.getResourceRootFolder());
        Assert.assertNotNull("File Resource should  for Object not be null :", javaObjectCompilationUnit.getResourceFile());

        JavaCompilationUnit javaMethodCompilationUnit = checkDeclarationUnit(module, "referencedCeylonProject.javaCeylonTopLevelMethod_Referenced_Ceylon_Project_", 
                JavaCompilationUnit.class, 
                rootPath + "/" + "referencedCeylonProject/javaCeylonTopLevelMethod_Referenced_Ceylon_Project_.java", 
                "referencedCeylonProject/javaCeylonTopLevelMethod_Referenced_Ceylon_Project_.java", 
                "javaCeylonTopLevelMethod_Referenced_Ceylon_Project_.java");
        Assert.assertEquals("Wrong Java Element for Method : ", javaMethodElement, javaMethodCompilationUnit.getTypeRoot());
        Assert.assertNotNull("Project Resource  for Method should not be null :", javaMethodCompilationUnit.getResourceProject());
        Assert.assertNotNull("Root Folder Resource  for Method should not be null :", javaMethodCompilationUnit.getResourceRootFolder());
        Assert.assertNotNull("File Resource should  for Method not be null :", javaMethodCompilationUnit.getResourceFile());
    }
    
    @Test 
    public void checkReferencedProjectPureJavaUnits() throws CoreException {
        if (compilationError != null) {
            throw compilationError;
        }
        
        IJavaProject javaProject = JavaCore.create(referencedCeylonProject);
        
        String rootPath = null;
        ICompilationUnit javaElement = null;
        for (IPackageFragmentRoot root : javaProject.getPackageFragmentRoots()) {
            IPackageFragment pkg = root.getPackageFragment("referencedCeylonProject");
            if (pkg.exists() && pkg.getCompilationUnit("JavaClassInCeylonModule_Referenced_Ceylon_Project.java").exists()) {
                javaElement = pkg.getCompilationUnit("JavaClassInCeylonModule_Referenced_Ceylon_Project.java");
                rootPath = root.getPath().toOSString();
                break;
            }
        }
        
        Module module = modelLoader.findModule("referencedCeylonProject", "1.0.0");

        JavaCompilationUnit javaClassCompilationUnit = checkDeclarationUnit(module, "referencedCeylonProject.JavaClassInCeylonModule_Referenced_Ceylon_Project", 
                JavaCompilationUnit.class, 
                rootPath + "/" + "referencedCeylonProject/JavaClassInCeylonModule_Referenced_Ceylon_Project.java", 
                "referencedCeylonProject/JavaClassInCeylonModule_Referenced_Ceylon_Project.java", 
                "JavaClassInCeylonModule_Referenced_Ceylon_Project.java");
        Assert.assertEquals("Wrong Java Element for Pure Java Class : ", javaElement, javaClassCompilationUnit.getTypeRoot());
        Assert.assertNotNull("Project Resource  for Pure Java Class should not be null :", javaClassCompilationUnit.getResourceProject());
        Assert.assertNotNull("Root Folder Resource  Pure Java for Class should not be null :", javaClassCompilationUnit.getResourceRootFolder());
        Assert.assertNotNull("File Resource should  Pure Java for Class not be null :", javaClassCompilationUnit.getResourceFile());
    }
}