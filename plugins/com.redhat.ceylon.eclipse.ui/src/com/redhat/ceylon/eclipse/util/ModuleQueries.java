package com.redhat.ceylon.eclipse.util;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public class ModuleQueries {

	public static ModuleQuery.Type getModuleQueryType(IProject project) {
	    if (project!=null) {
	        boolean compileToJava = CeylonBuilder.compileToJava(project);
	        boolean compileToJs = CeylonBuilder.compileToJs(project);
	        if (compileToJava&&!compileToJs) {
	            return ModuleQuery.Type.JVM;
	        }
	        if (compileToJs&&!compileToJava) {
	            return ModuleQuery.Type.JS;
	        }
	    }
	    return ModuleQuery.Type.CODE;
	}

}
