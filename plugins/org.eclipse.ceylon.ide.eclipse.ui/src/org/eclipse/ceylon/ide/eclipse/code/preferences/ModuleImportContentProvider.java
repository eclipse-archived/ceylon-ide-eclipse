/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.compileToJava;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.compileToJs;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectDeclaredSourceModules;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;

import org.eclipse.ceylon.cmr.api.ModuleSearchResult;
import org.eclipse.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import org.eclipse.ceylon.common.Backend;
import org.eclipse.ceylon.common.Backends;
import org.eclipse.ceylon.ide.eclipse.code.modulesearch.ModuleSearchManager;
import org.eclipse.ceylon.ide.eclipse.code.modulesearch.ModuleSearchViewContentProvider;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.common.modulesearch.ModuleNode;
import org.eclipse.ceylon.ide.common.modulesearch.ModuleVersionNode;
import org.eclipse.ceylon.model.cmr.JDKUtils;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.ModuleImport;

public abstract class ModuleImportContentProvider extends ModuleSearchViewContentProvider {
    
    private final Module module;
    private IProject project;
    
    public ModuleImportContentProvider(Module module, IProject project) {
        this.module = module;
        this.project = project;
    }
    
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof ModuleCategoryNode) {
            ModuleCategoryNode cat = (ModuleCategoryNode) parentElement;
            List<ModuleNode> result = cat.getModules();
            if (result==null) {
                result = getImportableModuleNodes(module, cat.getName());
                cat.setModules(result);
            }
            return result.toArray();
        }
        return super.getChildren(parentElement);
    }
    
    @Override
    public boolean hasChildren(Object element) {
        return super.hasChildren(element) || 
                element instanceof ModuleCategoryNode;
    }
    
    private List<ModuleNode> getImportableModuleNodes(Module module, String prefix) {
        if (prefix.equals(".")) {
            List<ModuleNode> list = new ArrayList<ModuleNode>();
            TreeMap<String, Module> map = new TreeMap<String, Module>();
            for (IProject p: CeylonBuilder.getProjects()) {
                if (project==null ||
                        compileToJava(p) && compileToJs(p) ||
                        compileToJava(p) && compileToJava(project) || 
                        compileToJs(p) && compileToJs(project)) {
                    for (Module m: getProjectDeclaredSourceModules(p)) {
                        if (!excluded(module, m.getNameAsString())) {
                            map.put(m.getNameAsString(), m);
                        }
                    }
                }
            }
            for (Map.Entry<String, Module> entry: map.entrySet()) {
                ModuleNode moduleNode = new ModuleNode(entry.getKey(), new ArrayList<ModuleVersionNode>(1));
                Module m = entry.getValue();
                ModuleVersionNode moduleVersion = new ModuleVersionNode(moduleNode, m.getVersion());
                moduleVersion.setNativeBackend(m.getNativeBackends());
                moduleNode.getVersions().add(moduleVersion);
                list.add(moduleNode);
            }
            return list;
        }
        else if (prefix.startsWith("java.") || prefix.equals("java.|javax.")) {
            List<ModuleNode> list = new ArrayList<ModuleNode>();
            for (String name: new TreeSet<String>(JDKUtils.getJDKModuleNames())) {
                if ((prefix.equals("java.|javax.")||name.startsWith(prefix)) && !excluded(module, name)) {
                    ModuleNode moduleNode = new ModuleNode(name, new ArrayList<ModuleVersionNode>(1));
                    ModuleVersionNode versionNode = new ModuleVersionNode(moduleNode, JDKUtils.jdk.version);
                    versionNode.setNativeBackend(Backends.fromAnnotation(Backend.Java.nativeAnnotation));
                    moduleNode.getVersions().add(versionNode);
                    list.add(moduleNode);
                }
            }
            return list;
        }
        else {
            ModuleSearchResult searchResults = getModules(prefix);
            List<ModuleDetails> list = new ArrayList<ModuleDetails>(searchResults.getResults());
            for (Iterator<ModuleDetails> it = list.iterator(); it.hasNext();) {
                String name = it.next().getName();
                if (excluded(module, name)) {
                    it.remove();
                }
            }
            return ModuleSearchManager.convertResult(list);
        }
    }

    public abstract ModuleSearchResult getModules(String prefix);
        
    private static boolean excluded(Module module, String name) {
        if (module == null) {
            //i.e. New Ceylon Module wizard
            if (name.equals(Module.LANGUAGE_MODULE_NAME)) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            //i.e. Ceylon Module properties page
            if (module.getNameAsString().equals(name)) {
                return true;
            }
            else {
                for (ModuleImport mi: module.getImports()) {
                    if (mi.getModule().getNameAsString().equals(name)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }
    
}