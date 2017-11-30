/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.classpath;



import static org.eclipse.ceylon.ide.eclipse.core.classpath.CeylonClasspathUtil.ceylonSourceArchiveToJavaSourceArchive;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.jdt.core.JavaCore.newLibraryEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import org.eclipse.ceylon.cmr.api.ArtifactContext;
import org.eclipse.ceylon.cmr.api.RepositoryManager;
import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.ide.common.model.BaseCeylonProject;


/** 
 * Ceylon Language Module Classpath Container - resolves a classpath container id to the ceylon.language binary archive
 */
public class CeylonLanguageModuleContainer implements IClasspathContainer {

    public static final String CONTAINER_ID = PLUGIN_ID + ".cpcontainer.RUNTIME_CONTAINER";

    /**
     * Container path used to resolve to this CPC
     */
    private IPath fPath = null;
    
    /**
     * The project this container is for
     */
    private IJavaProject fProject = null;
    
    IClasspathEntry[] entries = null;

    /**
     * Constructs a classpath container for the ceylon language runtime archive on the given project
     * 
     */
    public CeylonLanguageModuleContainer(IProject project) {
        List<IClasspathEntry> entriesList = new ArrayList<IClasspathEntry>();
		modelJ2C().ceylonModel().addProject(project);
		fPath = new Path(CeylonLanguageModuleContainer.CONTAINER_ID + "/default");
		fProject = JavaCore.create(project);
		BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
		RepositoryManager repoManager = ceylonProject != null ? ceylonProject.getRepositoryManager() : null;
		List<String> moduleNames = Arrays.asList("ceylon.language", "org.eclipse.ceylon.model", "org.eclipse.ceylon.common");
		String moduleVersion = TypeChecker.LANGUAGE_MODULE_VERSION;
	
		if (repoManager != null) {
			for (String moduleName : moduleNames) {
				File moduleArtifact = null;
				try {
					moduleArtifact = repoManager.getArtifact(new ArtifactContext(null, moduleName, moduleVersion, ArtifactContext.CAR, ArtifactContext.JAR));
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (moduleArtifact != null) {
					IPath moduleBinaries = new Path(moduleArtifact.getAbsolutePath());
					File sourceArchive = repoManager.getArtifact(new ArtifactContext(null, moduleName,moduleVersion, ArtifactContext.SRC));
					IPath moduleSources = null;
					if (moduleArtifact.getName().endsWith(ArtifactContext.CAR)) {
						File moduleJavaSources = null;
						try {
							moduleJavaSources = ceylonSourceArchiveToJavaSourceArchive(
									moduleName,
									moduleVersion,
									sourceArchive);
						} catch(Exception e) {
							e.printStackTrace();
						}
						moduleSources = moduleJavaSources != null ? 
								new Path(moduleJavaSources.getAbsolutePath()) : null;
					} else {
						moduleSources = new Path(sourceArchive.getAbsolutePath());
					}
					IClasspathEntry entry = newLibraryEntry(moduleBinaries, moduleSources, null);
					entriesList.add(entry);
				}
			}
		}
		IClasspathEntry[] entriesArray = new IClasspathEntry[entriesList.size()];
		entries = entriesList.toArray(entriesArray);
    }

    public IClasspathEntry[] constructModifiedClasspath() 
            throws JavaModelException {
        // Modifies the project classpath :
        //   Beware to always add the language module container before the application modules container 
        IClasspathEntry runtimeEntry = JavaCore.newContainerEntry(fPath, null, 
                new IClasspathAttribute[0], true);
        IClasspathEntry[] entries = fProject.getRawClasspath();
        List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(Arrays.asList(entries));
        int indexFirstContainer = -1;
        int indexSecondContainer = -1;
        int index = 0;
        boolean languageModuleEntryWasExported = false;
        IClasspathEntry ceylonClasspathEntry = null;
        for (IClasspathEntry entry: newEntries) {
            boolean containerToReplace = false;
            if (entry.getPath().equals(runtimeEntry.getPath()) ) {
                containerToReplace = true;
                languageModuleEntryWasExported = entry.isExported();
            }
            if (entry.getPath().segment(0)
                    .equals(CeylonProjectModulesContainer.CONTAINER_ID) ) {
                containerToReplace = true;
                ceylonClasspathEntry = entry;
                containerToReplace = true;
            }
            if (containerToReplace) {
                if (indexFirstContainer == -1) {
                    indexFirstContainer = index;
                }
                else {
                    indexSecondContainer = index;
                }
            }
            index++;
        }

        IClasspathEntry newEntry = JavaCore.newContainerEntry(fPath, null, 
                new IClasspathAttribute[0], indexFirstContainer == -1 || languageModuleEntryWasExported);

        if (indexFirstContainer >= 0) {
            newEntries.set(indexFirstContainer, newEntry);
        }
        else {
            newEntries.add(newEntry);
        }
        
        if (ceylonClasspathEntry != null) {
            if (indexSecondContainer >= 0) {
                newEntries.set(indexSecondContainer, ceylonClasspathEntry);
            }
            else {
                newEntries.add(ceylonClasspathEntry);
            }
        }
        
        return (IClasspathEntry[]) newEntries.toArray(new IClasspathEntry[newEntries.size()]);
    }
    
    
    
    /**
     * @see IClasspathContainer#getClasspathEntries()
     */
    public IClasspathEntry[] getClasspathEntries() {
        return entries;
    }

    public String getRuntimeVersion() {
        return TypeChecker.LANGUAGE_MODULE_VERSION;
    }
    
    /**
     * @see IClasspathContainer#getDescription()
     */
    public String getDescription() {
        return "Ceylon Language Module \u2014 " + getRuntimeVersion();
    }

    /**
     * @see IClasspathContainer#getKind()
     */
    public int getKind() {
        return IClasspathContainer.K_APPLICATION;
    }

    /**
     * @see IClasspathContainer#getPath()
     */
    public IPath getPath() {
        return fPath;
    }
    
    public void install() throws JavaModelException {
        fProject.setRawClasspath(constructModifiedClasspath(), null);
        JavaCore.setClasspathContainer(getPath(), 
                new IJavaProject[] { fProject }, 
                new IClasspathContainer[] { this }, null);
    }
}
