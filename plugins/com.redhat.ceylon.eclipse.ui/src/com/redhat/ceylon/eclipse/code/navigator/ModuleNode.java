package com.redhat.ceylon.eclipse.code.navigator;

import java.util.Collection;

import com.redhat.ceylon.eclipse.core.model.JDTModule;

public abstract class ModuleNode {
    protected String moduleSignature;
    
    public ModuleNode(String moduleSignature) {
        this.moduleSignature = moduleSignature;
    }

    protected abstract JDTModule searchBySignature(String signature);
    
    public JDTModule getModule() {
        return searchBySignature(moduleSignature);
    }
}