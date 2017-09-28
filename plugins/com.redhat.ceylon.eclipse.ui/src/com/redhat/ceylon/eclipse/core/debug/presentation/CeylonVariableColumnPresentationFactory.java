package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IColumnPresentation;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.ui.variables.JavaVariableColumnPresentationFactory;

import org.eclipse.ceylon.ide.eclipse.core.debug.DebugUtils;

public class CeylonVariableColumnPresentationFactory extends
        JavaVariableColumnPresentationFactory {

    public IColumnPresentation createColumnPresentation(IPresentationContext context, Object element) {
        if (isApplicable(context, element)) {
            return new CeylonVariableColumnPresentation();
        }
        return null;
    }

    public String getColumnPresentationId(IPresentationContext context, Object element) {
        if (isApplicable(context, element)) {
            return CeylonVariableColumnPresentation.CEYLON_VARIABLE_COLUMN_PRESENTATION;
        }
        return null;
    }
    
    private boolean isApplicable(IPresentationContext context, Object element) {
        IJavaStackFrame frame = null;
        if (IDebugUIConstants.ID_VARIABLE_VIEW.equals(context.getId())) {
            if (element instanceof IAdaptable) {
                IAdaptable adaptable = (IAdaptable)element;
                frame = (IJavaStackFrame) adaptable.getAdapter(IJavaStackFrame.class);
                if (DebugUtils.isCeylonFrame(frame)) {
                    return true;
                }
            }
        }
        return false;
    }
}
