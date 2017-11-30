/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.compiler.typechecker.context {
    Context
}
import org.eclipse.ceylon.ide.common.model {
    IdeModuleManager,
    IdeModuleSourceMapper,
    BaseIdeModule
}
import org.eclipse.core.resources {
    IProject,
    ResourcesPlugin,
    IResource,
    IFolder,
    IFile
}
import org.eclipse.ceylon.ide.common.platform {
    platformUtils,
    Status
}

shared class JDTModuleSourceMapper(
            Context context, 
            IdeModuleManager<IProject,IResource,IFolder,IFile> moduleManager)
        extends IdeModuleSourceMapper<IProject, IResource, IFolder,IFile>(context, moduleManager) {
    
    shared actual String defaultCharset => ResourcesPlugin.workspace.root.defaultCharset;
    
    shared actual void logModuleResolvingError(BaseIdeModule theModule, Exception e) {
        platformUtils.log(Status._ERROR, "Failed resolving module " + theModule.signature, e);
    }
    
}
