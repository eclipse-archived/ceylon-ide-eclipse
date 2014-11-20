package com.redhat.ceylon.eclipse.core.debug;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jdt.debug.core.IJavaVariable;

public class CeylonContentProviderFilter {
    public static Object[] filterVariables(Object[] variables, IPresentationContext context) throws DebugException {
        List<Object> keep = new ArrayList<Object>(variables.length);
        for (int i = 0; i < variables.length; i++) {
            boolean filter = false;
            if (variables[i] instanceof IJavaVariable){
                IJavaVariable var = (IJavaVariable)variables[i];
                if (var.getName().contains("$")){
                    filter = true;
                }
            }
            if (!filter){
                keep.add(variables[i]);
            }
        }
        return keep.toArray(new Object[keep.size()]);
    }

}
