package com.redhat.ceylon.eclipse.util;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.eclipse.java2ceylon.UtilJ2C;
import com.redhat.ceylon.ide.common.util.Indents;
import com.redhat.ceylon.ide.common.util.Path;
import com.redhat.ceylon.ide.common.util.ProgressMonitor;

public class utilJ2C implements UtilJ2C {
    @Override
    public Indents<IDocument> indents() {
        return eclipseIndents_.get_();
    }
    
    @Override
    public ProgressMonitor<IProgressMonitor> wrapProgressMonitor(IProgressMonitor monitor) {
        return wrapProgressMonitor_.wrapProgressMonitor(monitor);
    }
    
    @Override
    public IPath toEclipsePath(Path commonPath) {
        return toEclipsePath_.toEclipsePath(commonPath);
    }

    @Override
    public Path fromEclipsePath(IPath eclipsePath) {
        return fromEclipsePath_.fromEclipsePath(eclipsePath);
    }
}
