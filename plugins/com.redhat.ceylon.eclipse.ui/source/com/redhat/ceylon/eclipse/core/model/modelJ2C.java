package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;
import com.redhat.ceylon.ide.common.model.CeylonProjects;

public class modelJ2C {
    static public CeylonProjects<IProject> ceylonModel() {
        return ceylonModel_.get_();
    }

    static public CeylonProjectConfig<IProject> ceylonConfig(IProject project) {
        CeylonProject<IProject> ceylonProject = ceylonModel_.get_().getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getConfiguration();
        }
        return null;
    }

    static public CeylonIdeConfig<IProject> ideConfig(IProject project) {
        CeylonProject<IProject> ceylonProject = ceylonModel_.get_().getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getIdeConfiguration();
        }
        return null;
    }
}
