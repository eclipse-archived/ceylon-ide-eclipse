/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Package;

public class JavaClassFile extends JavaUnit {
    IClassFile classFileElement;
    CeylonToJavaMatcher ceylonToJavaMatcher;
    
    public JavaClassFile(IClassFile typeRoot, String fileName, String relativePath, String fullPath, Package pkg) {
        super(fileName, relativePath, fullPath, pkg);
        classFileElement = typeRoot;
        ceylonToJavaMatcher = new CeylonToJavaMatcher(typeRoot);
    }

    @Override
    public IClassFile getTypeRoot() {
        return classFileElement;
    }

    @Override
    public IJavaElement toJavaElement(Declaration ceylonDeclaration, IProgressMonitor monitor) {
        return ceylonToJavaMatcher.searchInClass(ceylonDeclaration, monitor);
    }

    @Override
    public IJavaElement toJavaElement(Declaration ceylonDeclaration) {
        return ceylonToJavaMatcher.searchInClass(ceylonDeclaration, null);
    }

    @Override
    public String getSourceFileName() {
        String sourceRelativePath = getSourceRelativePath();
        if (sourceRelativePath == null) {
            return null;
        }
        String[] pathElements = sourceRelativePath.split("/");
        return pathElements[pathElements.length-1];
    }
    
    @Override
    public String getSourceRelativePath() {
        return getModule().toSourceUnitRelativePath(getRelativePath());
    }
    
    @Override
    public String getSourceFullPath() {
        String sourceArchivePath = getModule().getSourceArchivePath();
        if (sourceArchivePath == null) {
            return null;
        }
        return sourceArchivePath + "!/" + getSourceRelativePath();
    }
}
