package com.redhat.ceylon.eclipse.core.debug;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.internal.debug.ui.heapwalking.JavaWatchExpressionFilter;

public class CeylonWatchExpressionFilter extends JavaWatchExpressionFilter {

    @Override
    public String createWatchExpression(IVariable variable)
            throws CoreException {
        return super.createWatchExpression(variable);
    }

}
