package com.redhat.ceylon.eclipse.util;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
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
            if (compileToJs&&compileToJava) {
                return ModuleQuery.Type.CEYLON_CODE;
            }
        }
        return ModuleQuery.Type.CODE;
    }

    public static ModuleSearchResult getModuleSearchResults(String prefix,
            TypeChecker tc, IProject project) {
        ModuleQuery query = new ModuleQuery(prefix, getModuleQueryType(project));
        query.setBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
        return tc.getContext().getRepositoryManager().completeModules(query);
    }

}
