package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModule;

public abstract class IdeUnit extends Unit {
    public JDTModule getModule() {
        return (JDTModule) getPackage().getModule();
    }
}
