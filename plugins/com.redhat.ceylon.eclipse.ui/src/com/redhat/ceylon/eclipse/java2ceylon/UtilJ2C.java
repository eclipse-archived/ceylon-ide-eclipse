package com.redhat.ceylon.eclipse.java2ceylon;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.ide.common.util.IdePlatformUtils;
import com.redhat.ceylon.ide.common.util.Indents;
import com.redhat.ceylon.ide.common.util.Path;
import com.redhat.ceylon.ide.common.util.ProgressMonitor;

public interface UtilJ2C {

    Indents<IDocument> indents();

    ProgressMonitor<IProgressMonitor> newProgressMonitor(IProgressMonitor wrapped);

    IdePlatformUtils platformUtils();

    IPath toEclipsePath(Path commonPath);

    Path fromEclipsePath(IPath eclipsePath);

    
}