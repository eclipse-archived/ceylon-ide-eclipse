package org.eclipse.ceylon.ide.eclipse.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class MarkerUtils {
    private MarkerUtils() { }

    /**
     * Returns the maximum problem marker severity for the given resource, and, if
     * depth is IResource.DEPTH_INFINITE, its children. The return value will be
     * one of IMarker.SEVERITY_ERROR, IMarker.SEVERITY_WARNING, IMarker.SEVERITY_INFO
     * or 0, indicating that no problem markers exist on the given resource.
     * @param depth TODO
     */
    public static int getMaxProblemMarkerSeverity(IResource res, int depth) {
        if (res == null || !res.isAccessible())
            return 0;
    
        boolean hasWarnings= false; // if resource has errors, will return error image immediately
        IMarker[] markers= null;
    
        try {
            markers= res.findMarkers(IMarker.PROBLEM, true, depth);
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }
        if (markers == null)
            return 0; // don't know - say no errors/warnings/infos
    
        for(int i= 0; i < markers.length; i++) {
            IMarker m= markers[i];
            int priority= m.getAttribute(IMarker.SEVERITY, -1);
    
            if (priority == IMarker.SEVERITY_WARNING) {
            hasWarnings= true;
            } else if (priority == IMarker.SEVERITY_ERROR) {
            return IMarker.SEVERITY_ERROR;
            }
        }
        return hasWarnings ? IMarker.SEVERITY_WARNING : 0;
    }

    public static IFileEditorInput getInput(IMarker marker) {
        IResource res = marker.getResource();
        if (res instanceof IFile && res.isAccessible()) {
            IFile file = (IFile) res;
            return new FileEditorInput(file);
        }

        return null;
    }
}
