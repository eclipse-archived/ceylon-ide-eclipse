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

import static java.util.Collections.unmodifiableList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;

import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;


public class SourceCodeVirtualFile implements VirtualFile {
    
    public static final List<VirtualFile> EMPTY_CHILDREN = unmodifiableList( new ArrayList<VirtualFile>(0) );
    
    private InputStream stream;
    private String path;
    private String name;
    
    public SourceCodeVirtualFile(String fileContent) {
        //TODO: this is broken for non-ASCII encodings!
        stream = new ByteArrayInputStream(fileContent.getBytes());
        path = "unknown.ceylon";
        name = "unknown.ceylon";
    }
    
    public SourceCodeVirtualFile(String fileContent, IPath path) {
        //TODO: this is broken for non-ASCII encodings!
        stream = new ByteArrayInputStream(fileContent.getBytes());
        this.path = path.toString();
        name = path.toFile().getName();
    }
    
    public boolean exists() {
        return true;
    }
    
    public boolean isFolder() {
        return false;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPath() {
        return path;
    }
    
    @Override
    public int compareTo(VirtualFile other) {
        return getPath().compareTo(other.getPath());
    }

    public InputStream getInputStream() {
        return stream;
    }
    
    public List<VirtualFile> getChildren() {
        return EMPTY_CHILDREN;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SourceCodeVirtualFile");
        return sb.toString();
    }  
    
    @Override
    public int hashCode() {
        return getPath().hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VirtualFile) {
            return ((VirtualFile) obj).getPath().equals(getPath());
        }
        else {
            return super.equals(obj);
        }
    }
}
