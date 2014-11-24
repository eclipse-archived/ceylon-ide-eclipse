package com.redhat.ceylon.eclipse.core.debug;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jdt.debug.core.IJavaVariable;

import com.redhat.ceylon.compiler.java.codegen.Naming;

public class CeylonContentProviderFilter {
    public static Object[] filterVariables(Object[] variables, IPresentationContext context) throws DebugException {
        List<Object> keep = new ArrayList<Object>(variables.length);
        for (int i = 0; i < variables.length; i++) {
            boolean filter = false;
            if (variables[i] instanceof IJavaVariable){
                String name = ((IJavaVariable)variables[i]).getName();
                if (name.charAt(0) == '$') {
                    if (Naming.isJavaKeyword(name, 1, name.length())) {
                        name = name.substring(1);
                    }
                }
                if (name.contains("$")){
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
