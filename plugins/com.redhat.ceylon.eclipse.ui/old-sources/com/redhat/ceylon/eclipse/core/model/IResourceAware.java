package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

public interface IResourceAware {
    IFolder getResourceRootFolder();
    IFile getResourceFile();
    IProject getResourceProject();
}
