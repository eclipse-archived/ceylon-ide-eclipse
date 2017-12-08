/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.external;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.filesystem.provider.FileSystem;
import org.eclipse.core.runtime.*;

/**
 * 
 */
public class CeylonArchiveFileSystem extends FileSystem {
	/**
	 * Scheme constant (value "zip") indicating the zip file system scheme.
	 */
	public static final String SCHEME_CEYLON_ARCHIVE = "ceylonArchive"; //$NON-NLS-1$
    public static final String JAR_SUFFIX = "!/";

	public IFileStore getStore(URI uri) {
		if (SCHEME_CEYLON_ARCHIVE.equals(uri.getScheme())) {
		    String fullPath = uri.getPath();
		    if (uri.getPath() != null) {
	            String[] splittedSsp = fullPath.split(JAR_SUFFIX);
	            IPath archivePath = new Path(splittedSsp[0]);
	            IPath entryPath = splittedSsp.length > 1 ? new Path(splittedSsp[1]) : Path.EMPTY;
	            try {
	                return new CeylonArchiveFileStore(EFS.getStore(URIUtil.toURI(archivePath)), entryPath);
	            } catch (CoreException e) {
	                //ignore and fall through below
	            }
		    }
		}
		return EFS.getNullFileSystem().getStore(URI.create(SCHEME_CEYLON_ARCHIVE+ ":/unknown/ceylon/archive"));
	}

    public static URI toCeylonArchiveURI(IPath archivePath, IPath entryPath) {
        try {
            if (entryPath == null)
                entryPath = Path.EMPTY;
            //must deconstruct the input URI to obtain unencoded strings, and then pass to URI constructor that will encode the entry path
            URI archiveFileURI = URIUtil.toURI(archivePath.toString());
            String uriPath =  archiveFileURI.getPath() + JAR_SUFFIX + entryPath.makeRelative().toString();
            return new URI(SCHEME_CEYLON_ARCHIVE, null, uriPath, null);
        } catch (URISyntaxException e) {
            //should never happen
            throw new RuntimeException(e);
        }
    }
}
