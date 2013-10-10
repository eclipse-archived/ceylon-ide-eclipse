package com.redhat.ceylon.eclipse.code.preferences;

import java.util.ArrayList;
import java.util.List;

import com.redhat.ceylon.eclipse.code.modulesearch.ModuleNode;

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
    
    public static List<ModuleCategoryNode> getCategoryNodes() {
        List<ModuleCategoryNode> list = new ArrayList<ModuleCategoryNode>();
        list.add(new ModuleCategoryNode(".", "Workspace Modules"));
        list.add(new ModuleCategoryNode("ceylon.", "Ceylon Platform Modules"));
        list.add(new ModuleCategoryNode("java.|javax.", "Java SE Modules"));
        list.add(new ModuleCategoryNode("", "All Modules"));
        return list;
    }
    
    public List<ModuleNode> getModules() {
        return modules;
    }
    
    public void setModules(List<ModuleNode> modules) {
        this.modules = modules;
    }
    
}
