package org.eclipse.ceylon.ide.eclipse.code.navigator;

import org.eclipse.ceylon.ide.common.model.BaseIdeModule;

public interface ModuleNode {
    BaseIdeModule getModule();
    String getSignature();
}