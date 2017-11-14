/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.compileToJava;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

import org.eclipse.ceylon.ide.common.modulesearch.ModuleNode;

public class ModuleCategoryNode {
    private String name;
    private String description;
    private List<ModuleNode> modules;

    public ModuleCategoryNode(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static List<ModuleCategoryNode> getCategoryNodes(
            IProject project) {
        List<ModuleCategoryNode> list = 
                new ArrayList<ModuleCategoryNode>();
        list.add(new ModuleCategoryNode(".", 
                "Workspace Modules"));
        list.add(new ModuleCategoryNode("ceylon.", 
                "Ceylon Platform Modules"));
        if (project==null || compileToJava(project)) {
            list.add(new ModuleCategoryNode("java.|javax.", 
                    "Java SE Modules"));
        }
        list.add(new ModuleCategoryNode("", 
                "All Repository Modules"));
        return list;
    }
    
    public List<ModuleNode> getModules() {
        return modules;
    }
    
    public void setModules(List<ModuleNode> modules) {
        this.modules = modules;
    }
    
}
