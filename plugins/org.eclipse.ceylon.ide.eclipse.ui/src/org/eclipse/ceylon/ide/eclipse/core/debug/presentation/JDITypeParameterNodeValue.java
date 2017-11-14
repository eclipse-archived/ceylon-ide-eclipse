/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaArrayType;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.debug.core.JDIDebugModel;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JDIPlaceholderVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIValue;

import org.eclipse.ceylon.ide.eclipse.core.debug.DebugUtils;

public class JDITypeParameterNodeValue implements IJavaValue {

    private IJavaArrayType fType;
    private TreeMap<String, JDIValue> typeParameters = new TreeMap<String, JDIValue>();

    public JDITypeParameterNodeValue(Map<String, JDIValue> typeParameters) {
        try {
            IJavaType[] javaTypes = ((IJavaDebugTarget)getDebugTarget()).getJavaTypes("java.lang.String[]"); //$NON-NLS-1$
            if (javaTypes != null && javaTypes.length > 0) {
                fType = (IJavaArrayType) javaTypes[0];
            }
        } catch (DebugException e) {
        }
        this.typeParameters.putAll(typeParameters);
    }

    @Override
    public IVariable[] getVariables() throws DebugException {
        IVariable[] vars = new JDIPlaceholderVariable[typeParameters.size()];
        
        int i=0;
        for (Map.Entry<String, JDIValue> child : typeParameters.entrySet()) {
            vars[i++] = new JDIPlaceholderVariable(child.getKey(), child.getValue());
        }
        return vars;
    }

    @Override
    public boolean hasVariables() throws DebugException {
        return typeParameters.size() > 0;
    }

    @Override
    public boolean isAllocated() throws DebugException {
        return false;
    }

    @Override
    public IJavaType getJavaType() throws DebugException {
        return fType;
    }

    @Override
    public String getSignature() throws DebugException {
        return "";//[Ljava/lang/String;"; //$NON-NLS-1$
    };

    @Override
    public String getReferenceTypeName() throws DebugException {
        return "";//java.lang.String[]"; //$NON-NLS-1$
    }

    @Override
    public String getValueString() throws DebugException {
        return "";
    }

    public String getDetailString() {
        StringBuffer result = new StringBuffer();
        for (Map.Entry<String,JDIValue> param : typeParameters.entrySet()) {
            result.append('\n')
            .append(param.getKey()).append(" = ").append(param.getValue());
        }
        return result.toString();
    }

    @Override
    public String toString() {
        return new StringBuffer().append("Type Paramemers : ").append(typeParameters.toString()).toString();
    }

    public int getInitialOffset() {
        return 0;
    }

    public int getSize() throws DebugException {
        return getVariables().length;
    }

    public IVariable getVariable(int offset) throws DebugException {
        IVariable[] variables = getVariables();
        if (offset < variables.length) {
            return variables[offset];
        }
        return null;
    }

    public IVariable[] getVariables(int offset, int length)
            throws DebugException {
        IVariable[] variables = getVariables();
        if (offset < variables.length && (offset + length) <= variables.length) {
            IJavaVariable[] vars = new IJavaVariable[length];
            System.arraycopy(variables, offset, vars, 0, length);
            return vars;
        }
        return null;
    }

    @Override
    public String getModelIdentifier() {
        return JDIDebugModel.getPluginIdentifier();
    }

    @Override
    public IDebugTarget getDebugTarget() {
        IJavaStackFrame sf = DebugUtils.getFrame();
        if (sf != null) {
            return sf.getDebugTarget();
        }
        return null;
    }

    @Override
    public ILaunch getLaunch() {
        IJavaStackFrame sf = DebugUtils.getFrame();
        if (sf != null) {
            return sf.getLaunch();
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter.isAssignableFrom(getClass())) {
            return this;
        }
        return null;
    }

    @Override
    public String getGenericSignature() throws DebugException {
        return "java.lang.String[]"; //$NON-NLS-1$
    }

    @Override
    public boolean isNull() {
        return false;
    }

}