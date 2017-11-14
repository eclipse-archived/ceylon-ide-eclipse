/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.eclipse.core.model {
    EclipseCeylonBinaryUnit,
    EclipseJavaClassFile,
    EclipseJavaCompilationUnit,
    EclipseCrossProjectBinaryUnit,
    EclipseCrossProjectJavaCompilationUnit
}
import org.eclipse.ceylon.ide.eclipse.core.model.mirror {
    JDTClass
}
import org.eclipse.ceylon.ide.common.model {
    BaseCeylonProject
}
import org.eclipse.ceylon.ide.common.platform {
    JavaModelServices
}
import org.eclipse.ceylon.model.loader.mirror {
    ClassMirror
}
import org.eclipse.ceylon.model.loader.model {
    LazyPackage
}
import org.eclipse.ceylon.model.typechecker.model {
    Unit
}

import org.eclipse.jdt.core {
    ITypeRoot,
    IType
}

object eclipseJavaModelServices 
        satisfies JavaModelServices<ITypeRoot> {
    shared actual ITypeRoot? getJavaClassRoot(ClassMirror classMirror) {
        if (is JDTClass jdtClass=classMirror) {
            IType? type = jdtClass.type;
            if (exists type) {
                return type.typeRoot;
            }
        }
        return null;
    }
    
    shared actual Unit newCeylonBinaryUnit(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseCeylonBinaryUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newCrossProjectBinaryUnit(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseCrossProjectBinaryUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newJavaClassFile(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseJavaClassFile(typeRoot, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newJavaCompilationUnit(ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseJavaCompilationUnit(typeRoot, fileName, relativePath, fullPath, pkg);
    
    shared actual Unit newCrossProjectJavaCompilationUnit(BaseCeylonProject ceylonProject, ITypeRoot typeRoot, String relativePath, String fileName, String fullPath, LazyPackage pkg) => 
            EclipseCrossProjectJavaCompilationUnit(ceylonProject, typeRoot, fileName, relativePath, fullPath, pkg);

}