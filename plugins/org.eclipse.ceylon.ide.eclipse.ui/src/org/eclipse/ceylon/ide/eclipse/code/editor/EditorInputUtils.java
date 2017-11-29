/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IURIEditorInput;

public class EditorInputUtils {
    /**
     * @return the IPath corresponding to the given input, or null if none
     */
    // TODO Determine whether this should always return project-relative paths when possible and document accordingly
    public static IPath getPath(IEditorInput editorInput) {
        IPath path= null;

        if (editorInput instanceof IFileEditorInput) {
            IFileEditorInput fileEditorInput= (IFileEditorInput) editorInput;
            path= fileEditorInput.getFile().getProjectRelativePath();
        } else if (editorInput instanceof IPathEditorInput) {
            IPathEditorInput pathInput= (IPathEditorInput) editorInput;
            path= pathInput.getPath();
        } else if (editorInput instanceof IStorageEditorInput) {
            IStorageEditorInput storageEditorInput= (IStorageEditorInput) editorInput;
            try {
                path= storageEditorInput.getStorage().getFullPath(); // can be null
            } catch (CoreException e) {
                // do nothing; return null;
            }
        } else if (editorInput instanceof IURIEditorInput) {
            IURIEditorInput uriEditorInput= (IURIEditorInput) editorInput;
            IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();
            path= new Path(uriEditorInput.getURI().getPath());
            if (wsRoot.getProject(path.segment(0)).exists()) {
                path= path.removeFirstSegments(1);
            }
        }
        return path;
    }

    /**
     * @return the IFile corresponding to the given input, or null if none
     */
    public static IFile getFile(IEditorInput editorInput) {
        IFile file= null;

        if (editorInput instanceof IFileEditorInput) {
            IFileEditorInput fileEditorInput= (IFileEditorInput) editorInput;
            file= fileEditorInput.getFile();
        } else if (editorInput instanceof IPathEditorInput) {
            IPathEditorInput pathInput= (IPathEditorInput) editorInput;
            IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();

            if (wsRoot.getLocation().isPrefixOf(pathInput.getPath())) {
                file= ResourcesPlugin.getWorkspace().getRoot().getFile(pathInput.getPath());
            } else {
                // Can't get an IFile for an arbitrary file on the file system; return null
            }
        } else if (editorInput instanceof IStorageEditorInput) {
            file= null; // Can't get an IFile for an arbitrary IStorageEditorInput
        } else if (editorInput instanceof IURIEditorInput) {
            IURIEditorInput uriEditorInput= (IURIEditorInput) editorInput;
            IWorkspaceRoot wsRoot= ResourcesPlugin.getWorkspace().getRoot();
            URI uri= uriEditorInput.getURI();
            String path= uri.getPath();
            // Bug 526: uri.getHost() can be null for a local file URL
            if (uri.getScheme().equals("file") && (uri.getHost() == null || uri.getHost().equals("localhost")) && path.startsWith(wsRoot.getLocation().toOSString())) {
                file= wsRoot.getFile(new Path(path));
            }
        }
        return file;
    }

    /**
     * @return the name extension (e.g., "java" or "cpp") corresponding to this
     * input, if known, or the empty string if none. Does not include a leading
     * ".".
     */
    public static String getNameExtension(IEditorInput editorInput) {
        return getPath(editorInput).getFileExtension();
    }
}
