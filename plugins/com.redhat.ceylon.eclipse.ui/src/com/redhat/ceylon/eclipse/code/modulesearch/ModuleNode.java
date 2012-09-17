package com.redhat.ceylon.eclipse.code.modulesearch;

import java.util.List;

public class ModuleNode {

    private final String name;
    private final List<ModuleVersionNode> versions;

    public ModuleNode(String name, List<ModuleVersionNode> versions) {
        this.name = name;
        this.versions = versions;
    }

    public String getName() {
        return name;
    }

    public List<ModuleVersionNode> getVersions() {
        return versions;
    }

    public ModuleVersionNode getLastVersion() {
        return versions.get(0);
    }

}