package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;

public interface IResourceAware extends IProjectAware {
    IFolder getRootFolderResource();
    IFile getFileResource();
}
