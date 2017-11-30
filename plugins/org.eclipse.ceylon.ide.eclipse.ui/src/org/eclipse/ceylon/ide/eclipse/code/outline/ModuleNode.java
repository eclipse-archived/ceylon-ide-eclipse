/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.outline;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;

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
