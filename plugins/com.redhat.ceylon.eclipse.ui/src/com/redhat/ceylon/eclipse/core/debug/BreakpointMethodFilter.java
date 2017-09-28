package org.eclipse.ceylon.ide.eclipse.core.debug;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.core.dom.Message;
import org.eclipse.jdt.debug.core.IJavaBreakpoint;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaInterfaceType;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;

import org.eclipse.ceylon.ide.eclipse.code.editor.ToggleBreakpointAdapter;
import org.eclipse.ceylon.ide.eclipse.core.debug.model.CeylonJDIThread;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import com.sun.jdi.Method;

public class BreakpointMethodFilter implements org.eclipse.jdt.debug.core.IJavaBreakpointListener {
    
    @Override
    public void addingBreakpoint(IJavaDebugTarget target,
            IJavaBreakpoint breakpoint) {
    }

    @Override
    public int installingBreakpoint(IJavaDebugTarget target,
            IJavaBreakpoint breakpoint, IJavaType type) {
        IMarker marker = breakpoint.getMarker();
        if (marker != null) {
            try {
                if (CeylonPlugin.PLUGIN_ID.equals(marker.getAttribute(ToggleBreakpointAdapter.ORIGIN))) {
                    if (type instanceof IJavaInterfaceType) {
                        return DONT_INSTALL;
                    }
                    return INSTALL;
                }
            } catch (CoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return DONT_CARE;
    }

    @Override
    public void breakpointInstalled(IJavaDebugTarget target,
            IJavaBreakpoint breakpoint) {
    }

    @Override
    public int breakpointHit(IJavaThread thread, IJavaBreakpoint breakpoint) {
        if (thread instanceof CeylonJDIThread) {
            IStackFrame frame = null;
            try {
                frame = thread.getTopStackFrame();
            } catch (DebugException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (frame instanceof JDIStackFrame 
                    && DebugUtils.isCeylonFrame((IJavaStackFrame) frame)) {
                JDIStackFrame jdiFrame = (JDIStackFrame) frame;
                Method method = jdiFrame.getUnderlyingMethod();
                if (method != null 
                        && DebugUtils.isInternalCeylonMethod(method)) {
                    return DONT_SUSPEND;
                }
            }
        }
        return DONT_CARE;
    }

    @Override
    public void breakpointRemoved(IJavaDebugTarget target,
            IJavaBreakpoint breakpoint) {
    }

    @Override
    public void breakpointHasRuntimeException(IJavaLineBreakpoint breakpoint,
            DebugException exception) {
    }

    @Override
    public void breakpointHasCompilationErrors(IJavaLineBreakpoint breakpoint,
            Message[] errors) {
    }

}
