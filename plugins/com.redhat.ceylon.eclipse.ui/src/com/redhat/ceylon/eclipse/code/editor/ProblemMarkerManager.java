package com.redhat.ceylon.eclipse.code.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.swt.widgets.Display;

/**
 * Listens to resource deltas and filters for marker changes of type IMarker.PROBLEM. 
 * Viewers showing error ticks should register as listener to this type. Based on 
 * class of the same name from JDT/UI.
 * 
 * @author Robert M. Fuhrer
 */
public class ProblemMarkerManager implements IResourceChangeListener, 
        IAnnotationModelListener, IAnnotationModelListenerExtension {
    
    private ListenerList<IProblemChangedListener> fListeners;

    public ProblemMarkerManager() {
        fListeners= new ListenerList<IProblemChangedListener>();
    }

    /**
     * Visitor used to determine whether the resource delta contains a marker change.
     */
    private static class ProjectErrorVisitor implements IResourceDeltaVisitor {
        private Set<IResource> fChangedElements;

        public ProjectErrorVisitor(Set<IResource> changedElements) {
            fChangedElements= changedElements;
        }

        public boolean visit(IResourceDelta delta) throws CoreException {
            IResource res= delta.getResource();
            if (res instanceof IProject && delta.getKind()==IResourceDelta.CHANGED) {
                IProject project= (IProject) res;
                if (!project.isAccessible()) {
                    // only track open Java projects
                    return false;
                }
            }
            checkInvalidate(delta, res);
            return true;
        }

        private void checkInvalidate(IResourceDelta delta, IResource resource) {
            int kind= delta.getKind();
            if (kind==IResourceDelta.REMOVED || kind==IResourceDelta.ADDED || 
                    (kind==IResourceDelta.CHANGED && isErrorDelta(delta))) {
                // invalidate the resource and all parents
                while (resource.getType()!=IResource.ROOT && fChangedElements.add(resource)) {
                    resource= resource.getParent();
                }
            }
        }

        private boolean isErrorDelta(IResourceDelta delta) {
            if ((delta.getFlags() & IResourceDelta.MARKERS)!=0) {
                IMarkerDelta[] markerDeltas= delta.getMarkerDeltas();
                for(int i= 0; i < markerDeltas.length; i++) {
                    IMarkerDelta markerDelta= markerDeltas[i];
                    if (markerDelta.isSubtypeOf(IMarker.PROBLEM)) {
                        int kind= markerDelta.getKind();
                        if (kind==IResourceDelta.ADDED || kind==IResourceDelta.REMOVED)
                            return true;
                        int severity= markerDelta.getAttribute(IMarker.SEVERITY, -1);
                        int newSeverity= markerDelta.getMarker().getAttribute(IMarker.SEVERITY, -1);
                        if (newSeverity!=severity)
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public void resourceChanged(IResourceChangeEvent event) {
        Set<IResource> changedElements= new HashSet<IResource>();
        try {
            IResourceDelta delta= event.getDelta();
            if (delta != null)
                delta.accept(new ProjectErrorVisitor(changedElements));
        } 
        catch (CoreException e) {
            e.printStackTrace();
        }

        if (!changedElements.isEmpty()) {
            IResource[] changes= (IResource[]) changedElements.toArray(new IResource[changedElements.size()]);
            fireChanges(changes, true);
        }
    }

    public void modelChanged(IAnnotationModel model) {}

    public void modelChanged(AnnotationModelEvent event) {}

    /**
     * Adds a listener for problem marker changes.
     */
    public void addListener(IProblemChangedListener listener) {
        if (fListeners.isEmpty()) {
            ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
        }
        fListeners.add(listener);
    }

    /**
     * Removes a <code>IProblemChangedListener</code>.
     */
    public void removeListener(IProblemChangedListener listener) {
        fListeners.remove(listener);
        if (fListeners.isEmpty()) {
            ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
        }
    }

    private void fireChanges(final IResource[] changes, final boolean isMarkerChange) {
        Display display = Display.getCurrent();
        if (display==null)
            display= Display.getDefault();

        if (display!=null && !display.isDisposed()) {
            display.asyncExec(new Runnable() {
                public void run() {
                    for (IProblemChangedListener curr: fListeners) {
                        curr.problemsChanged(changes, isMarkerChange);
                    }
                }
            });
        }
    }
}
