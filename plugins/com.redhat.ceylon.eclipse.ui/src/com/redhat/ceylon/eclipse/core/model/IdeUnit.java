package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;

public abstract class IdeUnit extends TypecheckerUnit implements IUnit {
    public JDTModule getModule() {
        return (JDTModule) getPackage().getModule();
    }
}
