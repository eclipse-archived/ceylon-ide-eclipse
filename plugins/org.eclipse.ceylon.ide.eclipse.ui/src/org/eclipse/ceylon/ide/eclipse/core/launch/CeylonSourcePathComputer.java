/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.launch;

import static org.eclipse.ceylon.ide.eclipse.util.InteropUtils.toJavaString;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.ExternalArchiveSourceContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.sourcelookup.containers.PackageFragmentRootSourceContainer;

import org.eclipse.ceylon.cmr.api.ArtifactContext;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;

public class CeylonSourcePathComputer implements ISourcePathComputerDelegate {

    @Override
    public ISourceContainer[] computeSourceContainers(
            ILaunchConfiguration configuration, IProgressMonitor monitor)
            throws CoreException {
        IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedSourceLookupPath(configuration);
        IRuntimeClasspathEntry[] resolved = JavaRuntime.resolveSourceLookupPath(entries, configuration);
        List<IRuntimeClasspathEntry> resolvedEntries = new ArrayList<>(resolved.length);
        for (IRuntimeClasspathEntry entry : resolved) {
            // Don't add the exploded Ceylon classes directory to the source containers !
            if (! entry.getPath().lastSegment().equals(CeylonBuilder.CEYLON_CLASSES_FOLDER_NAME)) {
                resolvedEntries.add(entry);
            }
        }
        List<ISourceContainer> containers = new ArrayList<ISourceContainer>(resolvedEntries.size());

        // When it's a Ceylon CAR archive that has a SRC attachment, 
        // also add the SRC archive as an archive container not only a PackageFragmentRoot-based container
        
        for (ISourceContainer container : JavaRuntime.getSourceContainers(resolvedEntries.toArray(new IRuntimeClasspathEntry[0]))) {
            containers.add(container);
            if (container instanceof PackageFragmentRootSourceContainer) {
                PackageFragmentRootSourceContainer pfrSourceContainer = (PackageFragmentRootSourceContainer) container;
                IPackageFragmentRoot pfr = pfrSourceContainer.getPackageFragmentRoot();
                if (pfr != null) {
                    IPath sourceAttachment = pfr.getSourceAttachmentPath();
                    if (sourceAttachment != null) {
                        if (sourceAttachment.lastSegment().endsWith(ArtifactContext.SRC)) {
                            containers.add(new ExternalArchiveSourceContainer(sourceAttachment.toOSString(), true));
                        } else if (sourceAttachment.lastSegment().endsWith("javaSources.zip")) {
                            IJavaProject javaProject = pfr.getJavaProject();
                            IProject project = javaProject != null ? javaProject.getProject() : null;
                            Collection<BaseIdeModule> modules = project != null ? CeylonBuilder.getProjectExternalModules(project) : null;
                            File archiveFile = pfr.getPath().toFile();
                            if (archiveFile != null 
                                    && modules != null) {
                                for (BaseIdeModule m : modules) {
                                    if (m.getIsCeylonBinaryArchive() 
                                            && archiveFile.equals(m.getArtifact())) {
                                        String sourceArchivePath = toJavaString(m.getSourceArchivePath());
                                        if (sourceArchivePath != null) {
                                            containers.add(new ExternalArchiveSourceContainer(sourceArchivePath, true));
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return containers.toArray(new ISourceContainer[containers.size()]);
    }
}
