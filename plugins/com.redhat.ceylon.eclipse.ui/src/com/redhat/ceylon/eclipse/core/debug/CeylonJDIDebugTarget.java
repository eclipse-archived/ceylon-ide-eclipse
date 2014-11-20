package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.internal.debug.core.model.JDIDebugTarget;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ObjectCollectedException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public class CeylonJDIDebugTarget extends JDIDebugTarget {
    private IProject project = null;
    
    public CeylonJDIDebugTarget(ILaunch launch, VirtualMachine jvm, String name,
            boolean supportTerminate, boolean supportDisconnect,
            IProcess process, boolean resume) {
        super(launch, jvm, name, supportTerminate, supportDisconnect, process, resume);
        try {
            ILaunchConfiguration config = launch.getLaunchConfiguration();
            String projectName;
            projectName = config.getAttribute("org.eclipse.jdt.launching.PROJECT_ATTR", "");
            if (projectName != null) {
                IProject theProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
                if (theProject.exists()) {
                    project = theProject;
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JDIThread newThread(ThreadReference reference) {
        try {
            return new CeylonJDIThread(this, reference);
        } catch (ObjectCollectedException exception) {
            // ObjectCollectionException can be thrown if the thread has already
            // completed (exited) in the VM.
        }
        return null;
    }

    public IProject getProject() {
        return project;
    }
}