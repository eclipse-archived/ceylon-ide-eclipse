package com.redhat.ceylon.eclipse.code.navigator;

import java.util.Collection;

import com.redhat.ceylon.eclipse.core.model.JDTModule;

public abstract class ModuleNode {
    protected String moduleSignature;
    
    public ModuleNode(String moduleSignature) {
        this.moduleSignature = moduleSignature;
    }

    protected abstract Collection<JDTModule> modulesToSearchIn();
    
    public JDTModule getModule() {
        for (JDTModule module : modulesToSearchIn()) {
            if (module.getSignature().equals(moduleSignature)) {
                return module;
            }
        }
        return null;
    }
}