/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.wizard;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class CeylonSourceStream extends InputStream {
    private final InputStream header;
    private final InputStream body;
    
    public CeylonSourceStream(IProject project,
            boolean includePreamble, String contents) {
        this.header = getHeader(project, includePreamble);
        this.body = new ByteArrayInputStream(contents.getBytes());
    }
    
    @Override
    public int read() throws IOException {
        int result = header.read();
        if (result<0) {
            result = body.read();
        }
        return result;
    }
    
    static InputStream getHeader(IProject project, boolean includePreamble) {
        IFile header = project.getFile("header.ceylon");
        InputStream his = new ByteArrayInputStream(new byte[0]);
        if (includePreamble && header.exists() && 
                header.isAccessible()) {
            try {
                his = header.getContents();
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
            
        }
        return his;
    }
}