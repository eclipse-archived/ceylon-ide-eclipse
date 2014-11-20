package com.redhat.ceylon.eclipse.core.debug;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import com.sun.jdi.ThreadReference;

public class CeylonJDIThread extends JDIThread {
    public CeylonJDIThread(CeylonJDIDebugTarget debugTarget, ThreadReference reference) {
        super(debugTarget, reference);
    }

    @Override
    protected synchronized List<IJavaStackFrame> computeStackFrames(
            boolean refreshChildren) throws DebugException {
        List<IJavaStackFrame> stackFrames = super.computeStackFrames(refreshChildren);
        return stackFrames;
    }

    @Override
    public synchronized IStackFrame getTopStackFrame() throws DebugException {
        IStackFrame topStackFrame = super.getTopStackFrame();
        return topStackFrame;
    }
    
    
}
