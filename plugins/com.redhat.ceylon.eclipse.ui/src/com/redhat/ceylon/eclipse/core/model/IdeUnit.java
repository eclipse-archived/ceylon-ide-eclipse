package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.compiler.typechecker.model.Unit;

public abstract class IdeUnit extends Unit implements IUnit {
    public JDTModule getModule() {
        return (JDTModule) getPackage().getModule();
    }
}
