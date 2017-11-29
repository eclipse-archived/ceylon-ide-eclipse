/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.presentation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.internal.ui.DefaultLabelProvider;
import org.eclipse.jdt.debug.core.IJavaArray;
import org.eclipse.jdt.debug.core.IJavaFieldVariable;
import org.eclipse.jdt.debug.core.IJavaObject;
import org.eclipse.jdt.debug.core.IJavaType;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JDIAllInstancesValue;
import org.eclipse.jdt.internal.debug.core.model.JDILocalVariable;
import org.eclipse.jdt.internal.debug.core.model.JDINullValue;
import org.eclipse.jdt.internal.debug.core.model.JDIReferenceListValue;
import org.eclipse.jdt.internal.debug.ui.DebugUIMessages;
import org.eclipse.jdt.internal.debug.ui.JDIModelPresentation;
import org.eclipse.osgi.util.NLS;

import org.eclipse.ceylon.common.JVMModuleUtil;
import org.eclipse.ceylon.compiler.java.codegen.Naming;
import org.eclipse.ceylon.ide.eclipse.core.debug.DebugUtils;
import org.eclipse.ceylon.ide.eclipse.core.debug.presentation.CeylonPresentationContext.PresentationType;

public class CeylonJDIModelPresentation extends JDIModelPresentation {
    private static final String ceylonStringTypeName = ceylon.language.String.class.getName();
    private static final String ceylonStringValueFieldName = "value";

    @Override
    public String getValueText(IJavaValue value) throws DebugException {
        if (!CeylonPresentationContext.isCeylonContext(value, PresentationType.LABEL)) {
            return super.getValueText(value);
        }

        String refTypeName= value.getReferenceTypeName();
        String valueString= value.getValueString();
        boolean isString= refTypeName.equals(fgStringName);
        if (isString) {
            return super.getValueText(value);
        }
        
        if (refTypeName.equals(ceylonStringTypeName)) {
            isString = true;
            IJavaFieldVariable javaStringValueField = ((IJavaObject)value).getField(ceylonStringValueFieldName, false);
            if (javaStringValueField != null) {
                IValue javaStringValue = javaStringValueField.getValue();
                if (javaStringValue != null) {
                    valueString = javaStringValue.getValueString();
                }
            }
        }
        
        IJavaType type= value.getJavaType();
        String signature= null;
        if (type != null) {
            signature= type.getSignature();
        }

        if (!isObjectValue(signature)) {
            return super.getValueText(value);
        }
        
        boolean isArray= value instanceof IJavaArray;
        StringBuffer buffer= new StringBuffer();
        if (!isString && (refTypeName.length() > 0)) {
            // Don't show type name for instances and references
            if (!(value instanceof JDIReferenceListValue || value instanceof JDIAllInstancesValue)){
                String qualTypeName= getCeylonReifiedTypeName(value);
                if (isArray) {
                    qualTypeName= adjustTypeNameForArrayIndex(qualTypeName, ((IJavaArray)value).getLength());
                }
                buffer.append(qualTypeName);
                buffer.append(' ');
            }
        }
        
        // Put double quotes around Strings
        if (valueString != null && (isString || valueString.length() > 0)) {
            if (isString) {
                buffer.append('"');
            }
            buffer.append(DefaultLabelProvider.escapeSpecialChars(valueString));
            if (isString) {
                buffer.append('"');
                if(value instanceof IJavaObject){
                    buffer.append(" "); //$NON-NLS-1$
                    buffer.append(NLS.bind(DebugUIMessages.JDIModelPresentation_118, new String[]{String.valueOf(((IJavaObject)value).getUniqueId())})); 
                }
            }
            
        }
        return buffer.toString().trim();
    }

    public String getCeylonReifiedTypeName(IValue value) throws DebugException {
        if (value instanceof JDINullValue) {
            return "Null";
        }
        
        String result = DebugUtils.getTypeName(value, DebugUtils.producedTypeFromInstance);
        if(result == null || result.isEmpty()) {
            result = getQualifiedName(value.getReferenceTypeName());
        }
        return result;
    }
    
    @Override
    public String getVariableText(IJavaVariable var) {
        boolean isCeylonContext = CeylonPresentationContext.isCeylonContext(var, PresentationType.LABEL);
        String varLabel= DebugUIMessages.JDIModelPresentation_unknown_name__1; 
        try {
            varLabel= var.getName();
            if (isCeylonContext) {
                varLabel = CeylonJDIModelPresentation.fixVariableName(varLabel, 
                        var instanceof JDILocalVariable,
                        var.isSynthetic());
            }
        } catch (DebugException exception) {
        }

        
        IJavaValue javaValue= null;
        try {
            javaValue = (IJavaValue) var.getValue();
        } catch (DebugException e1) {
        }
        boolean showTypes= isShowVariableTypeNames();
        StringBuffer buff= new StringBuffer();
        String typeName= DebugUIMessages.JDIModelPresentation_unknown_type__2; 
        try {
            typeName= var.getReferenceTypeName();
            if (isCeylonContext) {
                typeName = CeylonJDIModelPresentation.fixObjectTypeName(typeName);
            }
            if (showTypes) {
                typeName= getQualifiedName(typeName);
            }
        } catch (DebugException exception) {
        }
        if (showTypes) {
            buff.append(typeName);
            buff.append(' ');
        }
        buff.append(varLabel);

        // add declaring type name if required
        if (var instanceof IJavaFieldVariable) {
            IJavaFieldVariable field = (IJavaFieldVariable)var;
            if (isDuplicateName(field)) {
                try {
                    String decl = field.getDeclaringType().getName();
                    if (isCeylonContext) {
                        decl = CeylonJDIModelPresentation.fixObjectTypeName(decl);
                    }
                    buff.append(NLS.bind(" ({0})", new String[]{getQualifiedName(decl)})); //$NON-NLS-1$
                } catch (DebugException e) {
                }
            }
        }
        
        String valueString= getFormattedValueText(javaValue);

        //do not put the equal sign for array partitions
        if (valueString.length() != 0) {
            buff.append("= "); //$NON-NLS-1$
            buff.append(valueString);
        }
        return buff.toString();
    }

    
    private final static Pattern localVariablePattern = Pattern.compile("([^$]+)\\$[0-9]+");
    public static String fixVariableName(String name, boolean isLocalVariable, boolean isSynthetic) {
        if (isSynthetic 
                && name.startsWith("val$")) {
            name = name.substring(4);
        }
        if (name.charAt(0) == '$') {
            if (JVMModuleUtil.isJavaKeyword(name, 1, name.length())) {
                name = name.substring(1);
            }
        }
        if (isLocalVariable || isSynthetic 
                && name.contains("$")) {
            if(name.endsWith(Naming.Suffix.$param$.name())) {
                return name.substring(0, name.length() - Naming.Suffix.$param$.name().length());
            }
            Matcher matcher = localVariablePattern.matcher(name);
            if (matcher.matches()) {
                name = matcher.group(1);
            }
        }
        
        return name;
    }

    static String fixObjectTypeName(String typeName)
            throws DebugException {
        if (typeName.isEmpty()) {
            return typeName;
        }
        int index = typeName.lastIndexOf('.');
        if (index > 0) {
            typeName = typeName.substring(index+1);
        }
        
        if (!Character.isUpperCase(typeName.codePointAt(0)) &&
                typeName.endsWith("_")) {
            typeName = typeName.substring(0, typeName.length() - 1);
        }
        return typeName;
    }
    
}
