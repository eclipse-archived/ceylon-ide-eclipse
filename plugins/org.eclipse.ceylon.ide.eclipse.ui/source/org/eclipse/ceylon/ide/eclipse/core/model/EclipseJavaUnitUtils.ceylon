/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.util {
    withJavaModel
}
import org.eclipse.ceylon.ide.common.model {
    JavaUnitUtils
}

import org.eclipse.core.resources {
    IFolder,
    IFile
}
import org.eclipse.jdt.core {
    IPackageFragmentRoot,
    IJavaElement,
    ITypeRoot
}

shared interface EclipseJavaUnitUtils
        satisfies JavaUnitUtils<IFolder, IFile, ITypeRoot> {
    shared actual IFile? javaClassRootToNativeFile(ITypeRoot javaClassRoot) =>
            withJavaModel {
                do() => if (is IFile file = javaClassRoot.correspondingResource)
                            then file
                            else null;
            };
    
    shared actual IFolder? javaClassRootToNativeRootFolder(ITypeRoot javaClassRoot) =>
            withJavaModel {
                do() => 
                        if (is IPackageFragmentRoot root = javaClassRoot.getAncestor(IJavaElement.\iPACKAGE_FRAGMENT_ROOT),
                            is IFolder folder=root.correspondingResource)
                        then folder
                        else null;
            };
}