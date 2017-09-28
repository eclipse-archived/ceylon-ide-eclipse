package org.eclipse.ceylon.ide.eclipse.java2ceylon;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.ceylon.ide.eclipse.util.Indents;
import org.eclipse.ceylon.ide.common.util.Path;
import org.eclipse.ceylon.ide.common.util.ProgressMonitor;

public interface UtilJ2C {

    Indents indents();

    IPath toEclipsePath(Path commonPath);

    Path fromEclipsePath(IPath eclipsePath);

    ProgressMonitor<IProgressMonitor> wrapProgressMonitor(IProgressMonitor monitor);
}