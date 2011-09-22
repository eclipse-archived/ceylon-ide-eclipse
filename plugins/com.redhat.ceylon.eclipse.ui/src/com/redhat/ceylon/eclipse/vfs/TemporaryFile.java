package com.redhat.ceylon.eclipse.vfs;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;

public class TemporaryFile implements VirtualFile {
    @Override
    public boolean isFolder() {
        return true;
    }
    
    @Override
    public String getPath() {
        return "";
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