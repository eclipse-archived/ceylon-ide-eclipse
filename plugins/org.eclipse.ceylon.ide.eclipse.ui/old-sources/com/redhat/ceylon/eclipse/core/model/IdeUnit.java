package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.ceylon.compiler.typechecker.context.TypecheckerUnit;

public abstract class IdeUnit extends TypecheckerUnit implements IUnit {
    public JDTModule getModule() {
        return (JDTModule) getPackage().getModule();
    }

    abstract public String getSourceFileName();
    abstract public String getSourceRelativePath();
    abstract public String getSourceFullPath();
}
