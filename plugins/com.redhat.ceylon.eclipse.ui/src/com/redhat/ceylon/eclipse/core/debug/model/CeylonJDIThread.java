package com.redhat.ceylon.eclipse.core.debug.model;

import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.PatchedForCeylonJDIThread;

import com.redhat.ceylon.eclipse.core.debug.DebugUtils;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.StepRequest;

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
    
    boolean steppingThroughLocation = false;
    boolean originalLocationIsAnAncestor = false;
        
    private int changeSecondaryStepRequestKindForCeylon(int currentStepKind) throws DebugException {
        if (steppingThroughLocation) {
            steppingThroughLocation = false;
            return StepRequest.STEP_INTO;
        } else {
            return currentStepKind;
        }
    }

    @Override
    protected StepIntoHandler newStepIntoHandler() {
        return new StepIntoHandler() {
            @Override
            protected boolean locationIsFiltered(Method method) {
                return DebugUtils.isMethodFiltered(method);
            }

            @SuppressWarnings("unused")
            protected boolean locationIsFiltered(Method method, boolean orig) {
                return DebugUtils.isMethodFiltered(method);
            }

            @Override
            protected boolean locationShouldBeFiltered(Location location)
                    throws DebugException {
                storeAdditionalLocationData(location);
                return super.locationShouldBeFiltered(location);
            }
            @Override
            protected void createSecondaryStepRequest() throws DebugException {
                createSecondaryStepRequest(changeSecondaryStepRequestKindForCeylon(getStepKind()));
            }
        };
        
    }

    @Override
    protected StepOverHandler newStepOverHandler() {
        return new StepOverHandler() {
            @Override
            protected boolean locationIsFiltered(Method method) {
                return DebugUtils.isMethodFiltered(method);
            }
            @SuppressWarnings("unused")
            protected boolean locationIsFiltered(Method method, boolean orig) {
                return DebugUtils.isMethodFiltered(method);
            }
            @Override
            protected boolean locationShouldBeFiltered(Location location)
                    throws DebugException {
                storeAdditionalLocationData(location);
                return super.locationShouldBeFiltered(location);
            }
            @Override
            protected void createSecondaryStepRequest() throws DebugException {
                createSecondaryStepRequest(changeSecondaryStepRequestKindForCeylon(getStepKind()));
            }
        };
    }

    @Override
    protected StepReturnHandler newStepReturnHandler() {
        return new StepReturnHandler() {
            @Override
            protected boolean locationIsFiltered(Method method) {
                return DebugUtils.isMethodFiltered(method);
            }
            @SuppressWarnings("unused")
            protected boolean locationIsFiltered(Method method, boolean orig) {
                return DebugUtils.isMethodFiltered(method);
            }
            @Override
            protected boolean locationShouldBeFiltered(Location location)
                    throws DebugException {
                storeAdditionalLocationData(location);
                return super.locationShouldBeFiltered(location);
            }
            @Override
            protected void createSecondaryStepRequest() throws DebugException {
                createSecondaryStepRequest(changeSecondaryStepRequestKindForCeylon(getStepKind()));
            }
        };
    }
    
    
    protected boolean shouldDoStepReturn() throws DebugException {
        boolean shouldDoStepReturn = super.shouldDoStepReturn();
        if (shouldDoStepReturn) {
            try {
                StackFrame previousFrame = getUnderlyingThread().frame(1);
                if (DebugUtils.isMethodToStepThrough(previousFrame.location().method())) {
                    return false;
                }
            } catch (IncompatibleThreadStateException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    
    @Override
    protected boolean shouldDoExtraStepInto(Location location)
            throws DebugException {
        return super.shouldDoExtraStepInto(location) && originalLocationIsAnAncestor;
    }

    public void storeAdditionalLocationData(Location location)
            throws DebugException {
        steppingThroughLocation = DebugUtils.isCeylonGeneratedMethodToStepThrough(location.method());
        if (getUnderlyingFrameCount() < getOriginalStepStackDepth()) {
            originalLocationIsAnAncestor = false;
        }
    }
    
    @Override
    protected void setOriginalStepLocation(Location location) {
        originalLocationIsAnAncestor = true;
        super.setOriginalStepLocation(location);
    }
}
