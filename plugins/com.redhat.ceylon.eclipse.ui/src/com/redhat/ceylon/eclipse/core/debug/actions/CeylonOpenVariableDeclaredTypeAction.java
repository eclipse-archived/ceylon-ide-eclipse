package com.redhat.ceylon.eclipse.core.debug.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IDebugElement;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaVariable;

public class CeylonOpenVariableDeclaredTypeAction extends
        CeylonOpenVariableTypeAction {

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction#getTypeToOpen(org.eclipse.debug.core.model.IDebugElement)
     */
    @Override
    protected IJavaType getTypeToOpen(IDebugElement element) throws CoreException {
        if (element instanceof IJavaVariable) {
            IJavaVariable variable = (IJavaVariable) element;
            return variable.getJavaType();
        }
        return null;
    }
}
