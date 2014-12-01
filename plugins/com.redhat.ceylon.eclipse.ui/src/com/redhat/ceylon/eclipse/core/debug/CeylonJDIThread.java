package com.redhat.ceylon.eclipse.core.debug;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.PatchedForCeylonJDIThread;

import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ThreadReference;

public class CeylonJDIThread extends PatchedForCeylonJDIThread {
    public CeylonJDIThread(CeylonJDIDebugTarget debugTarget, ThreadReference reference) {
        super(debugTarget, reference);
    }

    private IJavaStackFrame[] startStackFrame = new IJavaStackFrame[1];
    
    @Override
    protected synchronized List<IJavaStackFrame> computeStackFrames(
            boolean refreshChildren) throws DebugException {
        List<IJavaStackFrame> stackFrames = super.computeStackFrames(refreshChildren);
        if (refreshChildren) {
            synchronized (startStackFrame) {
                try {
                    CeylonJDIDebugTarget target = getDebugTarget();
                    if (target.isMainThread(this)) {
                        for (int i=0; i<stackFrames.size(); i++) {
                            JDIStackFrame frame = (JDIStackFrame)stackFrames.get(i);
                            Location location = frame.getUnderlyingMethod().location();
                            String locationString = new StringBuilder()
                                                .append(location.declaringType().name())
                                                .append('/')
                                                .append(location.method().name())
                                                .toString();
                            if (locationString.equals(target.getStartLocation())) {
                                startStackFrame[0] = frame;
                                break;
                            }
                        }
                    }
                } catch(Throwable t) {
                    t.printStackTrace();
                    startStackFrame[0] = null;
                }
            }
        }
        return stackFrames;
    }

    @Override
    public CeylonJDIDebugTarget getDebugTarget() {
        return (CeylonJDIDebugTarget) super.getDebugTarget();
    }
    
    public boolean isBeforeStart(IJavaStackFrame aFrame) {
        CeylonJDIDebugTarget target = getDebugTarget();
        if (target.isMainThread(this)) {
            try {
                List<IJavaStackFrame> stackFrames = computeStackFrames();
                synchronized (startStackFrame) {
                    if (startStackFrame[0] != null) {
                        for (int i=stackFrames.size() - 1; i >= 0; i--) {
                            if (stackFrames.get(i).equals(startStackFrame[0])) {
                                return false;
                            }
                            if (stackFrames.get(i).equals(aFrame)) {
                                return true;
                            }
                        }
                        return true;
                    }
                }
                
            } catch (DebugException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public synchronized IStackFrame getTopStackFrame() throws DebugException {
        IStackFrame topStackFrame = super.getTopStackFrame();
        return topStackFrame;
    }
    
    @Override
    protected StepIntoHandler newStepIntoHandler() {
        return new StepIntoHandler() {
            @Override
            protected boolean locationIsFiltered(Method method) {
                return super.locationIsFiltered(method) || DebugUtils.isMethodFilteredForCeylon(method);
            }
        };
    }

    @Override
    protected StepOverHandler newStepOverHandler() {
        return new StepOverHandler() {
            @Override
            protected boolean locationIsFiltered(Method method) {
                return super.locationIsFiltered(method) || DebugUtils.isMethodFilteredForCeylon(method);
            }
        };
    }

    @Override
    protected StepReturnHandler newStepReturnHandler() {
        return new StepReturnHandler() {
            @Override
            protected boolean locationIsFiltered(Method method) {
                return super.locationIsFiltered(method) || DebugUtils.isMethodFilteredForCeylon(method);
            }
        };
    }
}
