package com.redhat.ceylon.eclipse.core.debug.presentation;

import static com.redhat.ceylon.eclipse.core.debug.presentation.CeylonJDIModelPresentation.fixVariableName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jdt.debug.core.IJavaVariable;
import org.eclipse.jdt.internal.debug.core.model.JDILocalVariable;

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
        return keep.toArray(new Object[keep.size()]);
    }

}
