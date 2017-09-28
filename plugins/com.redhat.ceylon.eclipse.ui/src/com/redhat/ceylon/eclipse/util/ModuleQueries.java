package org.eclipse.ceylon.ide.eclipse.util;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

import org.eclipse.ceylon.cmr.api.ModuleQuery;
import org.eclipse.ceylon.cmr.api.ModuleSearchResult;
import org.eclipse.ceylon.cmr.api.ModuleVersionQuery;
import org.eclipse.ceylon.common.Backend;
import org.eclipse.ceylon.common.Backends;
import org.eclipse.ceylon.common.Versions;
import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.model.typechecker.model.Module;

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
        }
        return new ModuleQuery(prefix, ModuleQuery.Type.CODE);
    }

    public static ModuleQuery getModuleQuery(String prefix, Module module, IProject project) {
        if (module!=null) {
            Backends backends = module.getNativeBackends();
            if (backends!=null) {
                boolean compileToJava = backends.supports(Backend.Java);
                boolean compileToJs = backends.supports(Backend.JavaScript);
                if (compileToJava&&!compileToJs) {
                    return new ModuleQuery(prefix, ModuleQuery.Type.JVM);
                }
                if (compileToJs&&!compileToJava) {
                    return new ModuleQuery(prefix, ModuleQuery.Type.JS);
                }
            }
        }
        return getModuleQuery(prefix, project);
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
        }
        return new ModuleVersionQuery(name, version, ModuleQuery.Type.CODE);
    }
    
    static class Runnable implements IRunnableWithProgress {
        
        private String prefix;
        private TypeChecker typeChecker;
        private IProject project;
        private Module module;

        ModuleSearchResult result;
        
        Runnable(String prefix, TypeChecker typeChecker,
                Module module, IProject project) {
            this.prefix = prefix;
            this.typeChecker = typeChecker;
            this.project = project;
            this.module = module;
        }
        
        @Override
        public void run(IProgressMonitor monitor)
                throws InvocationTargetException, InterruptedException {
            monitor.beginTask("Querying module repositories...", IProgressMonitor.UNKNOWN);
            ModuleQuery query = getModuleQuery(prefix, module, project);
            query.setJvmBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
            query.setJvmBinaryMinor(Versions.JVM_BINARY_MINOR_VERSION);
            query.setJsBinaryMajor(Versions.JS_BINARY_MAJOR_VERSION);
            query.setJsBinaryMinor(Versions.JS_BINARY_MINOR_VERSION);
            result = typeChecker.getContext().getRepositoryManager().completeModules(query);
            monitor.done();
        }
        
    }

    public static ModuleSearchResult getModuleSearchResults(
            String prefix, Module module,
            TypeChecker typeChecker, IProject project) {
        Runnable runnable = 
                new Runnable(prefix, typeChecker, 
                        module, project);
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().run(true, true, runnable);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return runnable.result;
    }

}
