/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.redhat.ceylon.eclipse.core.classpath;



import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

/** 
 * Ceylon Runtime Environment Container - resolves a classpath container variable to a JRE
 */
public class CeylonRuntimeContainer implements IClasspathContainer {

    public static final String CONTAINER_ID = PLUGIN_ID + ".cpcontainer.RUNTIME_CONTAINER";

    /**
     * Container path used to resolve to this CRE
     */
    private IPath fPath = null;
    
    /**
     * The project this container is for
     */
    private IJavaProject fProject = null;
    
    IClasspathEntry[] entries = null;
    
    /**
     * Constructs a JRE classpath container on the given VM install
     * 
     * @param vm vm install - cannot be <code>null</code>
     * @param path container path used to resolve this JRE
     */
    public CeylonRuntimeContainer(IJavaProject project) {
        fPath = new Path(CeylonRuntimeContainer.CONTAINER_ID + "/default");
        fProject = project;
    }
    
    public IClasspathEntry[] constructModifiedClasspath() 
            throws JavaModelException {
        IClasspathEntry runtimeEntry = JavaCore.newContainerEntry(fPath, null, 
                new IClasspathAttribute[0], false);
        IClasspathEntry[] entries = fProject.getRawClasspath();
        List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(Arrays.asList(entries));
        int indexFirstContainer = -1;
        int indexSecondContainer = -1;
        int index = 0;
        IClasspathEntry ceylonClasspathEntry = null;
        for (IClasspathEntry entry: newEntries) {
            boolean containerToReplace = false;
            if (entry.getPath().equals(runtimeEntry.getPath()) ) {
                containerToReplace = true;
            }
            if (entry.getPath().segment(0).equals(CeylonClasspathContainer.CONTAINER_ID) ) {
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
                new IClasspathAttribute[0], false);

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
        if (entries == null) {
            RepositoryManager repoManager;
            try {
                repoManager = CeylonBuilder.getProjectRepositoryManager(fProject.getProject());
                IPath ceylonLanguageBinaries = new Path(repoManager.getArtifact(new ArtifactContext("ceylon.language",TypeChecker.LANGUAGE_MODULE_VERSION, ArtifactContext.CAR)).getAbsolutePath());
                IPath ceylonLanguageSources = new Path(repoManager.getArtifact(new ArtifactContext("ceylon.language",TypeChecker.LANGUAGE_MODULE_VERSION, ArtifactContext.SRC)).getAbsolutePath());
                entries = new IClasspathEntry[] { JavaCore.newLibraryEntry(ceylonLanguageBinaries, ceylonLanguageSources, null) };
            } catch (CoreException e) {
                e.printStackTrace();
                return new IClasspathEntry[] {};
            }
        }
        
        return entries;
    }

    public String getRuntimeVersion() {
        return TypeChecker.LANGUAGE_MODULE_VERSION;
    }
    
    /**
     * @see IClasspathContainer#getDescription()
     */
    public String getDescription() {
        return "Ceylon Runtime Library - " + getRuntimeVersion();
    }

    /**
     * @see IClasspathContainer#getKind()
     */
    public int getKind() {
        return IClasspathContainer.K_DEFAULT_SYSTEM;
    }

    /**
     * @see IClasspathContainer#getPath()
     */
    public IPath getPath() {
        return fPath;
    }
}
