package com.redhat.ceylon.eclipse.core.launch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.ExternalArchiveSourceContainer;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.sourcelookup.containers.PackageFragmentRootSourceContainer;

import com.redhat.ceylon.cmr.api.ArtifactContext;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

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
                        }
                    }
                }
            }
        }
        return containers.toArray(new ISourceContainer[containers.size()]);
    }
}
