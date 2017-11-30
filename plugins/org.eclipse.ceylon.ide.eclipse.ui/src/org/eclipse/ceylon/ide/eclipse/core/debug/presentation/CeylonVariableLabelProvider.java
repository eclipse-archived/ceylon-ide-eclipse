/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

import static org.eclipse.ceylon.ide.eclipse.core.debug.presentation.CeylonJDIModelPresentation.fixVariableName;
import static org.eclipse.ceylon.ide.eclipse.core.debug.presentation.CeylonPresentationContext.isCeylonContext;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDILocalVariable;
import org.eclipse.jdt.internal.debug.ui.variables.JavaVariableLabelProvider;
import org.eclipse.jface.viewers.TreePath;

public class CeylonVariableLabelProvider extends JavaVariableLabelProvider{

    public CeylonVariableLabelProvider() {
        super();
        fLabelProvider = new CeylonJDIModelPresentation();
    }
    
    public CeylonJDIModelPresentation getCeylonJDIModelPresentation() {
        return (CeylonJDIModelPresentation) fLabelProvider;
    }

    @Override
    protected void retrieveLabel(final ILabelUpdate update) throws CoreException {
        super.retrieveLabel(new CeylonLabelUpdate(update));
    }
    
    @Override
    protected String getVariableName(IVariable variable,
            IPresentationContext context) throws CoreException {
        String name = super.getVariableName(variable, context);
        if (isCeylonContext(context)) {
            name = fixVariableName(name, 
                    variable instanceof JDILocalVariable,
                    variable instanceof IJavaVariable
                        && ((IJavaVariable) variable).isSynthetic());
        }
        return name;
    }

    @Override
    protected String getValueTypeName(IVariable variable, IValue value,
            IPresentationContext context) throws CoreException {
        String name = super.getValueTypeName(variable, value, context);

        if (isCeylonContext(context)) {
            name = CeylonJDIModelPresentation.fixObjectTypeName(name);
        }
        return name;
    }

    @Override
    protected String getLabel(TreePath elementPath,
            IPresentationContext context, String columnId) throws CoreException {
        if (columnId == null) {
            // when no columns, handle special escaping ourselves
            IDebugModelPresentation presentation = getCeylonJDIModelPresentation();
            if (presentation != null) {
                return presentation.getText(elementPath.getLastSegment());
            }
        }
        return super.getLabel(elementPath, context, columnId);
    }

    @Override
    protected String getValueText(IVariable variable, IValue value,
            IPresentationContext context) throws CoreException {
        String valueText = super.getValueText(variable, value, context);
        return valueText;
    }
    
    @Override
    protected String getVariableTypeName(IVariable variable,
            IPresentationContext context) throws CoreException {
        String name = super.getVariableTypeName(variable, context);
        if (isCeylonContext(context)) {
            name = CeylonJDIModelPresentation.fixObjectTypeName(name);
        }
        return name;
    }

    @Override
    protected String getColumnText(IVariable variable, IValue value,
            IPresentationContext context, String columnId) throws CoreException {
        if (CeylonVariableColumnPresentation.COLUMN_REIFIED_TYPE.equals(columnId) && isCeylonContext(context)) {
            return getCeylonJDIModelPresentation().getCeylonReifiedTypeName(value);
        }
        return super.getColumnText(variable, value, context, columnId);
    }
}
