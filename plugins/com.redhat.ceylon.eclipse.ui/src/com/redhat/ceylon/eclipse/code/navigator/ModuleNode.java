package com.redhat.ceylon.eclipse.code.navigator;

import com.redhat.ceylon.ide.common.model.BaseIdeModule;

public interface ModuleNode {
    BaseIdeModule getModule();
    String getSignature();
}