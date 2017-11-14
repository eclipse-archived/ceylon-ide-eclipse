/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.common.model {
    CeylonBinaryUnit
}
import org.eclipse.ceylon.model.typechecker.model {
    Package
}

import java.lang.ref {
    SoftReference
}

import org.eclipse.core.resources {
    IProject
}
import org.eclipse.jdt.core {
    ITypeRoot,
    IJavaElement
}
shared class EclipseCeylonBinaryUnit(
    ITypeRoot theTypeRoot,
    String theFilename,
    String theRelativePath,
    String theFullPath,
    Package pkg)
        extends CeylonBinaryUnit<IProject, ITypeRoot, IJavaElement>(theTypeRoot, theFilename, theRelativePath, theFullPath, pkg) 
        satisfies EclipseJavaModelAware {
    shared actual variable SoftReference<EclipseJavaModelAware.ResolvedElements> resolvedElementsRef = SoftReference<EclipseJavaModelAware.ResolvedElements>(null);
}