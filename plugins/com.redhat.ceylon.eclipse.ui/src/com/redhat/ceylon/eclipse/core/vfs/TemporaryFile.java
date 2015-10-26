package com.redhat.ceylon.eclipse.core.vfs;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;

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