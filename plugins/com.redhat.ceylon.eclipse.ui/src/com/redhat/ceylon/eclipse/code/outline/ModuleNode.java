package com.redhat.ceylon.eclipse.code.outline;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class ModuleNode extends Node {

    private String moduleName;
    private String version;
    
    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String packageName) {
        this.moduleName = packageName;
    }

    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }

    public ModuleNode() {
        super(null);
    }

    @Override
    public void visit(Visitor visitor) {}

    @Override
    public void visitChildren(Visitor visitor) {}

}
