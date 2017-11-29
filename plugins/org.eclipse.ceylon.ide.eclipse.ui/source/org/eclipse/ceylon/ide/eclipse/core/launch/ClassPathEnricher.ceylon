/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import ceylon.collection {
    HashSet
}

import org.eclipse.ceylon.cmr.ceylon {
    CeylonUtils {
        CeylonRepoManagerBuilder
    }
}
import org.eclipse.ceylon.common {
    Versions
}
import org.eclipse.ceylon.ide.eclipse.core.builder {
    CeylonBuilder
}
import org.eclipse.ceylon.ide.eclipse.core.model {
    ceylonModel
}
import org.eclipse.ceylon.ide.common.platform {
    platformUtils
}
import org.eclipse.ceylon.tools.classpath {
    CeylonClasspathTool
}
import org.eclipse.ceylon.tools.moduleloading {
    ToolModuleLoader
}

import java.io {
    File
}
import java.lang {
    ObjectArray,
    JString=String,
    Types
}
import java.util {
    Arrays
}

import org.eclipse.debug.core {
    ILaunchConfiguration
}
import org.eclipse.jdt.core {
    IJavaProject
}

shared interface ClassPathEnricher {
    
    shared ObjectArray<JString> enrichClassPath(ObjectArray<JString> original, 
            ILaunchConfiguration launchConfig) {
        
        IJavaProject? javaProject = getTheJavaProject(launchConfig);
        if (!exists javaProject) {
            return original;
        }
        value project = javaProject.project;
        
        value classpathEntries = HashSet<String>();
        value ceylonProjects 
                = { for (p in project.referencedProjects)
                    if (exists cp = ceylonModel.getProject(p))
                    cp };
        for (referencedProject in ceylonProjects) {
            
            value repoManagerBuilder = CeylonRepoManagerBuilder()
                    .offline(referencedProject.configuration.offline)
                        .cwd(referencedProject.rootDirectory)
                        .systemRepo(referencedProject.systemRepository)
                        .outRepo(CeylonBuilder.getCeylonModulesOutputDirectory(
                            referencedProject.ideArtifact).absolutePath)
                        .extraUserRepos(Arrays.asList(
                            for (p in referencedProject.referencedCeylonProjects)
                            Types.nativeString(p.ceylonModulesOutputDirectory.absolutePath)))
                        .logger(platformUtils.cmrLogger)
                        .isJDKIncluded(false);
            
            if (exists modules = referencedProject.modules) {
                object tool extends CeylonClasspathTool() {
                    shared ToolModuleLoader theLoader => super.loader;
                    loadModule(String? namespace, String? moduleName, String? moduleVersion) => 
                            super.loadModule(namespace, moduleName, moduleVersion);
                    createRepositoryManagerBuilder() => repoManagerBuilder;
                }
                tool.initialize(null);
                tool.loadModule(null, "org.eclipse.ceylon.java.main", Versions.ceylonVersionNumber);
                for (m in modules) {
                    if (m.isProjectModule && !m.defaultModule) {
                        tool.loadModule(m.namespace, m.nameAsString, m.version);
                    }
                }
                tool.theLoader.resolve();
                tool.theLoader.visitModules((m) { 
                    if (exists file = m.artifact.artifact()) {
                        classpathEntries.add(file.absolutePath);
                    }
                });
                value defaultCar 
                        = File(CeylonBuilder.getCeylonModulesOutputDirectory(
                    referencedProject.ideArtifact), "default.car");
                if (defaultCar.\iexists()) {
                    classpathEntries.add(defaultCar.absolutePath);
                }
            }
        }
        classpathEntries.addAll { for (cp in original) cp.string };
        return ObjectArray.with(classpathEntries.map(Types.nativeString));
    }
    
    shared formal IJavaProject getTheJavaProject(ILaunchConfiguration launchConfiguration);
}