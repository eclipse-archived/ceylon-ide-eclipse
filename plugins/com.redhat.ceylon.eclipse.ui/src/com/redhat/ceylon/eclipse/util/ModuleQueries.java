package com.redhat.ceylon.eclipse.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import com.redhat.ceylon.cmr.api.ModuleQuery;
import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleVersionQuery;
import com.redhat.ceylon.common.Versions;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public class ModuleQueries {

    public static ModuleQuery getModuleQuery(String prefix, IProject project) {
        if (project!=null) {
            boolean compileToJava = CeylonBuilder.compileToJava(project);
            boolean compileToJs = CeylonBuilder.compileToJs(project);
            if (compileToJava&&!compileToJs) {
                return new ModuleQuery(prefix, ModuleQuery.Type.JVM);
            }
            if (compileToJs&&!compileToJava) {
                return new ModuleQuery(prefix, ModuleQuery.Type.JS);
            }
            if (compileToJs&&compileToJava) {
                return new ModuleQuery(prefix, ModuleQuery.Type.ALL, ModuleQuery.Retrieval.ANY);
            }
        }
        return new ModuleQuery(prefix, ModuleQuery.Type.CODE);
    }

    public static ModuleVersionQuery getModuleVersionQuery(String name, String version, IProject project) {
        if (project!=null) {
            boolean compileToJava = CeylonBuilder.compileToJava(project);
            boolean compileToJs = CeylonBuilder.compileToJs(project);
            if (compileToJava&&!compileToJs) {
                return new ModuleVersionQuery(name, version, ModuleQuery.Type.JVM);
            }
            if (compileToJs&&!compileToJava) {
                return new ModuleVersionQuery(name, version, ModuleQuery.Type.JS);
            }
            if (compileToJs&&compileToJava) {
                ModuleVersionQuery mvq = new ModuleVersionQuery(name, version, ModuleQuery.Type.ALL);
                mvq.setRetrieval(ModuleQuery.Retrieval.ANY);
            }
        }
        return new ModuleVersionQuery(name, version, ModuleQuery.Type.CODE);
    }
    
    static class Runnable implements IRunnableWithProgress {
        
        private String prefix;
        private TypeChecker typeChecker;
        private IProject project;

        ModuleSearchResult result;
        
        Runnable(String prefix, TypeChecker typeChecker, IProject project) {
            this.prefix = prefix;
            this.typeChecker = typeChecker;
            this.project = project;
        }
        
        @Override
        public void run(IProgressMonitor monitor)
                throws InvocationTargetException, InterruptedException {
            monitor.beginTask("Querying module repositories...", IProgressMonitor.UNKNOWN);
            ModuleQuery query = getModuleQuery(prefix, project);
            query.setBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
            result = typeChecker.getContext().getRepositoryManager().completeModules(query);
            monitor.done();
        }
        
    }

    public static ModuleSearchResult getModuleSearchResults(String prefix,
            TypeChecker typeChecker, IProject project) {
        Runnable runnable = new Runnable(prefix, typeChecker, project);
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true, runnable);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return runnable.result;
    }

}
