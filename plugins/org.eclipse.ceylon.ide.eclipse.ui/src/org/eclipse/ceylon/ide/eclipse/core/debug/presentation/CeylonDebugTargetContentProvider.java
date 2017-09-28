package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IViewerUpdate;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaThreadGroup;
import org.eclipse.jdt.internal.debug.ui.monitors.JavaElementContentProvider;
import org.eclipse.jdt.internal.debug.ui.threadgroups.JavaDebugTargetContentProvider;

import org.eclipse.ceylon.ide.eclipse.core.debug.model.CeylonJDIDebugTarget;
import org.eclipse.ceylon.ide.common.debug.agent.CeylonDebugEvaluationThread;

public class CeylonDebugTargetContentProvider extends
        JavaDebugTargetContentProvider {
    
    protected int getChildCount(Object element, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
        int childCount = super.getChildCount(element, context, monitor);
        if (childCount > 0) {
            if (IDebugUIConstants.ID_DEBUG_VIEW.equals(context.getId())) {
                if (element instanceof CeylonJDIDebugTarget) {
                    CeylonJDIDebugTarget target = (CeylonJDIDebugTarget) element;
                    if (JavaElementContentProvider.isDisplayThreadGroups()) {
                        if (target.hasCeylonEvaluationThreadGroup()) {
                            childCount --;
                        }
                    } else {
                        if (target.hasCeylonEvaluationThread()) {
                            childCount --;
                        }
                    }
                }
            }
        }
        return childCount;
    }

    @Override
    protected Object[] getChildren(Object parent, int index, int length, IPresentationContext context, IViewerUpdate monitor) throws CoreException {
        if (IDebugUIConstants.ID_DEBUG_VIEW.equals(context.getId())) {
            if (JavaElementContentProvider.isDisplayThreadGroups()) {
                List<IJavaThreadGroup> groups = new ArrayList<>();
                for (IJavaThreadGroup group : ((IJavaDebugTarget)parent).getRootThreadGroups()) {
                    if (! CeylonDebugEvaluationThread.name.equals(group.getName())) {
                        groups.add(group);
                    }
                }
                return getElements(groups.toArray(new IJavaThreadGroup[groups.size()]), index, length);
            } else {
                List<IThread> threads = new ArrayList<>();
                for (IThread thread : ((IJavaDebugTarget)parent).getThreads()) {
                    if (! CeylonDebugEvaluationThread.name.equals(thread.getName())) {
                        threads.add(thread);
                    }
                }
                return getElements(threads.toArray(new IThread[threads.size()]), index, length);
            }
        } else {
            return super.getChildren(parent, index, length, context, monitor);
        }
    }    

}
