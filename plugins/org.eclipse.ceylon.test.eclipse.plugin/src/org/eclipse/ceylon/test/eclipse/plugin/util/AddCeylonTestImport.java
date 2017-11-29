/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.util;

import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin.CEYLON_TEST_MODULE_DEFAULT_VERSION;
import static org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin.CEYLON_TEST_MODULE_NAME;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.ceylon.cmr.api.ModuleQuery.Type;
import org.eclipse.ceylon.cmr.api.ModuleVersionQuery;
import org.eclipse.ceylon.cmr.api.ModuleVersionResult;
import org.eclipse.ceylon.cmr.api.RepositoryManager;
import org.eclipse.ceylon.ide.eclipse.code.imports.ModuleImportUtil;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.common.model.BaseCeylonProject;
import org.eclipse.ceylon.model.typechecker.model.Module;

public class AddCeylonTestImport {

    public static void addCeylonTestImport(IProject project, Module module) throws CoreException {
        ModuleImportUtil.addModuleImport(project, module, CEYLON_TEST_MODULE_NAME, determineCeylonTestVersion(project));
    }

    private static String determineCeylonTestVersion(IProject project) {
        ModuleVersionResult result = null;
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        if (ceylonProject != null) {
            RepositoryManager repositoryManager = ceylonProject.getRepositoryManager();
            ModuleVersionQuery query = new ModuleVersionQuery(CEYLON_TEST_MODULE_NAME, null, Type.JVM);
            result = repositoryManager.completeVersions(query);
        }
        if (result != null
                && result.getVersions() != null
                && result.getVersions().size() > 0) {
            return result.getVersions().lastEntry().getKey();
        } else {
            return CEYLON_TEST_MODULE_DEFAULT_VERSION;
        }
    }

}