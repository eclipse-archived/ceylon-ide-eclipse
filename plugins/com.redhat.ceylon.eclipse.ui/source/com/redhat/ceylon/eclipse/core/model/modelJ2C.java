package com.redhat.ceylon.eclipse.core.model;

import org.eclipse.core.resources.IProject;

import com.redhat.ceylon.ide.common.model.CeylonProjects;

public class modelJ2C {
    static public CeylonProjects<IProject> ceylonModel() {
        return ceylonModel_.get_();
    }
}
