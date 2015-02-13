package com.redhat.ceylon.eclipse.core.debug.presentation;

import static com.redhat.ceylon.eclipse.core.debug.presentation.CeylonJDIModelPresentation.fixVariableName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jdt.debug.core.IJavaValue;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.logicalstructures.JDIPlaceholderVariable;
import org.eclipse.jdt.internal.debug.core.model.JDIFieldVariable;
import org.eclipse.jdt.internal.debug.core.model.JDILocalVariable;

import com.redhat.ceylon.compiler.java.language.VariableBox;

public class CeylonContentProviderFilter {
    public static Object[] filterVariables(Object[] variables, IPresentationContext context) throws DebugException {
        List<Object> keep = new ArrayList<Object>(variables.length);
        Set<String> localVariableCeylonNames = new HashSet<>();
        
        for (int i = 0; i < variables.length; i++) {
            boolean filter = false;
            if (variables[i] instanceof IJavaVariable){
                IJavaVariable variable = (IJavaVariable) variables[i];
                boolean isLocalVariable = variable instanceof JDILocalVariable;
                do {
                    if (variable.isSynthetic()) {
                        filter = true;
                        break;
                    }
                    String fixedName = fixVariableName(variable.getName(), 
                            isLocalVariable, false);
                    if (fixedName.contains("$")){
                        filter = true;
                        break;
                    }
                    if (isLocalVariable) {
                        if (!localVariableCeylonNames.contains(fixedName)) {
                            localVariableCeylonNames.add(fixedName);
                        } else {
                            filter = true;
                            break;
                        }
                    }
                } while (false);
                
            }
            if (!filter){
                keep.add(variables[i]);
            }
        }
        for (int i=0; i<keep.size(); i++) {
            final Object element = keep.get(i);
            Object replacedVariable = unBoxIfVariableBoxed(element);
            if (replacedVariable != null) {
                keep.set(i, replacedVariable);
            }
        }
        return keep.toArray(new Object[keep.size()]);
    }

    public static <Type> Type unBoxIfVariableBoxed(final Type element)
            throws DebugException {
        return unBoxIfVariableBoxed(element, "");
    }
    
    @SuppressWarnings("unchecked")
    public static <Type> Type unBoxIfVariableBoxed(final Type element, final String namePrefix)
            throws DebugException {
        Type replacedVariable = element;
        if (element instanceof IJavaVariable) {
            if (VariableBox.class.getName().equals(((IJavaVariable) element).getJavaType().getName())) {
                IJavaValue value = (IJavaValue) ((IJavaVariable) element).getValue();
                IVariable[] children = value.getVariables();
                if (children.length > 0) {
                    for (IVariable child : children) {
                        if (child instanceof JDIFieldVariable) {
                            if ("ref".equals(child.getName())) {
                                JDIFieldVariable refVariable = (JDIFieldVariable) child;
                                replacedVariable = (Type) new JDIFieldVariable(
                                        refVariable.getJavaDebugTarget(), 
                                        refVariable.getField(), 
                                        refVariable.getObjectReference(), null) {
                                    public String getName() throws DebugException {
                                        return (namePrefix + fixVariableName(((IJavaVariable) element).getName(), 
                                                ((IJavaVariable) element).isLocal(), 
                                                ((IJavaVariable) element).isSynthetic()));
                                    }
                                };
                                break;
                            }
                        }
                    }
                }
            } else if (! namePrefix.isEmpty() && element instanceof JDIPlaceholderVariable) {
                final JDIPlaceholderVariable placeHolderVariable = (JDIPlaceholderVariable) element;
                IValue value = placeHolderVariable.getValue();
                if (value instanceof IJavaValue)
                replacedVariable = (Type) new JDIPlaceholderVariable(
                        placeHolderVariable.getName(), 
                        (IJavaValue) placeHolderVariable.getValue()) {
                    public String getName() {
                        return (namePrefix + fixVariableName(placeHolderVariable.getName(), 
                                false, 
                                false));
                    }
                };
            } else if (! namePrefix.isEmpty() && element instanceof JDIFieldVariable) {
                final JDIFieldVariable fieldVariable = (JDIFieldVariable) element;
                replacedVariable = (Type) new JDIFieldVariable(
                        fieldVariable.getJavaDebugTarget(), 
                        fieldVariable.getField(), 
                        fieldVariable.getObjectReference(), null) {
                    public String getName() throws DebugException {
                        return (namePrefix + fixVariableName(fieldVariable.getName(), 
                                fieldVariable.isLocal(), 
                                fieldVariable.isSynthetic()));
                    }
                };
            }
        }
        return replacedVariable;
    }

}
