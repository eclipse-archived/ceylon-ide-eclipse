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

import junit.framework.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.BeforeClass;
import org.junit.Ignore;

import com.redhat.ceylon.compiler.java.test.model.ModelLoaderTest;
import com.redhat.ceylon.compiler.java.test.model.RunnableTest;
import com.redhat.ceylon.compiler.loader.ModelLoader.DeclarationType;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModelLoader;

/**
 * 
 * @author david
 * 
 */
public class ModelLoaderTests extends ModelLoaderTest {

    static private AssertionError compilationError = null;
    static private IProject projectDeclarations = null;
    static private IProject projectReferences = null;
    
	@BeforeClass
	public static void compileDeclarationsAndReferences() {
	    try {
	        IPath projectDescriptionPath = null;
	        IPath userDirPath = new Path(System.getProperty("user.dir"));
            final IWorkspace workspace = ResourcesPlugin.getWorkspace();
            
	        try {
        		projectDescriptionPath = userDirPath.append("resources/model-loader-tests/declarations/.project");
        		projectDeclarations = Utils.importProject(workspace, "model-loader-tests", projectDescriptionPath);
	        }
            catch(Exception e) {
                Assert.fail("Import of the declarations project failed with the exception : \n" + e.toString());
            }
	        try {
        		projectDeclarations.build(IncrementalProjectBuilder.FULL_BUILD, null);
        		IFile carFile = projectDeclarations.getFile("modules/declarations/1.0.0/declarations-1.0.0.car");
        		carFile.refreshLocal(0, null);
                Assert.assertTrue("declarations Project should compile",
                        carFile.exists());
                
                projectDeclarations.getFile("modules/declarations/1.0.0/declarations-1.0.0.src").getLocation().toFile().delete();
                projectDeclarations.getFile("modules/declarations/1.0.0/declarations-1.0.0.src.sha1").getLocation().toFile().delete();
	        }
            catch(Exception e) {
                Assert.fail("Build of the declarations project failed with the exception : \n" + e.toString());
            }
	        
	        
	        
	        try {
                projectDescriptionPath = userDirPath.append("resources/model-loader-tests/references/.project");
                projectReferences = Utils.importProject(workspace, "model-loader-tests",
                        projectDescriptionPath);
            }
            catch(Exception e) {
                Assert.fail("Build of the references project failed with the exception : \n" + e.toString());
            }
            
            try {
                projectReferences.build(IncrementalProjectBuilder.FULL_BUILD, null);
                IFile carFile = projectReferences.getFile("modules/references/1.0.0/references-1.0.0.car");
                carFile.refreshLocal(0, null);
                Assert.assertTrue("references Project should compile", 
                        carFile.exists());
            }
            catch(Exception e) {
                Assert.fail("Build of the references project failed with the exception : \n" + e.toString());
            }
	    }
	    catch(AssertionError e) {
	        compilationError = e;
	    }
	}

    @Override
    protected void verifyClassLoading(String ceylon) {
        if (compilationError != null) {
            throw compilationError;
        }
        
        PhasedUnit phasedUnit = CeylonBuilder.getProjectTypeChecker(projectDeclarations)
                                            .getPhasedUnitFromRelativePath("declarations/" + ceylon);
        final Map<String,Declaration> decls = new HashMap<String,Declaration>();
        for(Declaration decl : phasedUnit.getUnit().getDeclarations()){
            if(decl.isToplevel()){
                decls.put(getQualifiedPrefixedName(decl), decl);
            }
        }
        
        JDTModelLoader modelLoader = CeylonBuilder.getProjectModelLoader(projectReferences);
        
        for(Entry<String, Declaration> entry : decls.entrySet()){
            String quotedQualifiedName = entry.getKey().substring(1);
            Declaration modelDeclaration = modelLoader.getDeclaration(quotedQualifiedName, 
                    entry.getValue() instanceof Value ? DeclarationType.VALUE : DeclarationType.TYPE);
            Assert.assertNotNull(modelDeclaration);
            // make sure we loaded them exactly the same
            compareDeclarations(entry.getValue(), modelDeclaration);
        }
    }

    
    @Override
    protected void compareDeclarations(Declaration validDeclaration,
            Declaration modelDeclaration) {
        if (modelDeclaration.getUnit() != null && 
                modelDeclaration.getUnit().getFilename() != null && 
                modelDeclaration.getUnit().getFilename().endsWith(".ceylon")) {
            return;
        }
        super.compareDeclarations(validDeclaration, modelDeclaration);
    }
    
    @Override
    @Ignore
    public void testTypeParserUsingSourceModel(){
    }

    @Override
    protected void verifyClassLoading(String ceylon, RunnableTest test,
            List<String> options) {
        test.test(CeylonBuilder.getProjectModelLoader(projectReferences));
    }

    
}
