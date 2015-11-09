package com.redhat.ceylon.eclipse.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;

import com.redhat.ceylon.ide.common.util.Indents;
import com.redhat.ceylon.ide.common.util.ProgressMonitor;

public class utilJ2C {
    public static Indents<IDocument> indents() {
        return eclipseIndents_.get_();
    }
    
    public static ProgressMonitor newProgressMonitor(IProgressMonitor wrapped) {
        return new EclipseProgressMonitor(wrapped);
    }
}
