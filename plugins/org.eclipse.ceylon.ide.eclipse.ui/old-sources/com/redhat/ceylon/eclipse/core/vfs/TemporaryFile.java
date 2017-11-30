/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.vfs;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;

public class TemporaryFile implements VirtualFile {
    private String path;
    
    public TemporaryFile() {
        this("");
    }

    public TemporaryFile(String path) {
        this.path = path;
    }
    
    @Override
    public boolean exists() {
        return true;
    }
    
    @Override
    public boolean isFolder() {
        return true;
    }
    
    @Override
    public String getPath() {
        return path;
    }
    
    @Override
    public int compareTo(VirtualFile other) {
        return getPath().compareTo(other.getPath());
    }

    @Override
    public String getName() {
        return "";
    }
    
    @Override
    public InputStream getInputStream() {
        return null;
    }
    
    @Override
    public List<VirtualFile> getChildren() {
        return Collections.emptyList();
    }
}