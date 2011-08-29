package com.redhat.ceylon.eclipse.imp.parser;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;


public class SourceDirectoryVirtualFile implements VirtualFile {
  IPath path;
  
  public SourceDirectoryVirtualFile(IPath path) {
    super();
    this.path = path;
  }

  @Override
  public boolean isFolder() {
    return true;
  }

  @Override
  public String getName() {
    return path.toString();
  }

  @Override
  public String getPath() {
    return path.toString();
  }

  @Override
  public InputStream getInputStream() {
    return null;
  }

  @Override
  public List<VirtualFile> getChildren() {
    return null;
  }
}
