package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;

public class CeylonOpenReceivingTypeAction extends CeylonOpenStackFrameAction {
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction#getTypeToOpen(org.eclipse.debug.core.model.IDebugElement)
     */
    @Override
    protected IJavaType getTypeToOpen(IDebugElement element) throws CoreException {
        if (element instanceof IJavaStackFrame) {
            IJavaStackFrame frame = (IJavaStackFrame) element;
            if (frame.isStatic()) {
                return frame.getReferenceType();
            }
            return frame.getThis().getJavaType();
        }
        return null;
    }
}
