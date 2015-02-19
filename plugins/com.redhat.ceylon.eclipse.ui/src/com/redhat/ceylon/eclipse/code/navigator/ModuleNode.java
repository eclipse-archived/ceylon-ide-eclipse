package com.redhat.ceylon.eclipse.code.navigator;

import com.redhat.ceylon.eclipse.core.model.JDTModule;

public interface ModuleNode {
    JDTModule getModule();
    String getSignature();
}