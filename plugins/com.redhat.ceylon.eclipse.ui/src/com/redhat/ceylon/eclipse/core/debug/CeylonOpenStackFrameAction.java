package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.jdt.debug.core.IJavaStackFrame;

public abstract class CeylonOpenStackFrameAction extends CeylonOpenTypeAction {
    
    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction#getDebugElement(org.eclipse.core.runtime.IAdaptable)
     */
    @Override
    protected IDebugElement getDebugElement(IAdaptable element) {
        return (IDebugElement)element.getAdapter(IJavaStackFrame.class);
    }

}
