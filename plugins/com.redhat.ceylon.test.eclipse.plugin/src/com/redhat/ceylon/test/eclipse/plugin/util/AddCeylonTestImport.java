package com.redhat.ceylon.test.eclipse.plugin.util;

import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.CEYLON_TEST_MODULE_DEFAULT_VERSION;
import static com.redhat.ceylon.test.eclipse.plugin.CeylonTestPlugin.CEYLON_TEST_MODULE_NAME;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.redhat.ceylon.cmr.api.ModuleQuery.Type;
import com.redhat.ceylon.cmr.api.ModuleVersionQuery;
import com.redhat.ceylon.cmr.api.ModuleVersionResult;
import com.redhat.ceylon.cmr.api.RepositoryManager;
import com.redhat.ceylon.eclipse.code.imports.ModuleImportUtil;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.ide.common.model.BaseCeylonProject;
import com.redhat.ceylon.model.typechecker.model.Module;

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