package com.redhat.ceylon.eclipse.core.model;

import com.redhat.ceylon.compiler.typechecker.context.TypecheckerUnit;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;

public abstract class IdeUnit extends TypecheckerUnit implements IUnit {
    public JDTModule getModule() {
        Package p = getPackage();
        return p != null ? (JDTModule) p.getModule() : null;
    }

    abstract public String getSourceFileName();
    abstract public String getSourceRelativePath();
    abstract public String getSourceFullPath();
    @Override
    public Package getJavaLangPackage() {
        JDTModule currentModule = getModule();
        if (currentModule != null) {
            for (Module m : currentModule.getModuleSourceMapper()
                    .getContext().getModules().getListOfModules()) {
                if ("java.base".equals(m.getNameAsString())) {
                    return m.getPackage("java.lang");
                }
            }
        }
        return super.getJavaLangPackage();
    }
}
