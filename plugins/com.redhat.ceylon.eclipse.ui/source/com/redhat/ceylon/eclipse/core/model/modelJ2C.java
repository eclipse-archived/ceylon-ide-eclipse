package com.redhat.ceylon.eclipse.core.model;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import com.redhat.ceylon.compiler.typechecker.context.Context;
import com.redhat.ceylon.ide.common.model.BaseIdeModule;
import com.redhat.ceylon.ide.common.model.BaseIdeModuleManager;
import com.redhat.ceylon.ide.common.model.CeylonIdeConfig;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;
import com.redhat.ceylon.ide.common.model.CeylonProjects;
import com.redhat.ceylon.ide.common.model.IdeModuleManager;
import com.redhat.ceylon.ide.common.model.IdeModuleSourceMapper;
import com.redhat.ceylon.ide.common.util.IdePlatformUtils;
import com.redhat.ceylon.ide.common.util.toJavaList_;
import com.redhat.ceylon.model.typechecker.util.ModuleManager;

public class modelJ2C {
    static public CeylonProjects<IProject, IResource,IFolder,IFile> ceylonModel() {
        return ceylonModel_.get_();
    }

    static public CeylonProjectConfig ceylonConfig(IProject project) {
        CeylonProject<IProject> ceylonProject = ceylonModel_.get_().getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getConfiguration();
        }
        return null;
    }

    static public CeylonIdeConfig ideConfig(IProject project) {
        CeylonProject<IProject> ceylonProject = ceylonModel_.get_().getProject(project);
        if (ceylonProject != null) {
            return ceylonProject.getIdeConfiguration();
        }
        return null;
    }
    
    static public List<IPackageFragmentRoot> getModulePackageFragmentRoots(BaseIdeModule module) {
        toJavaList_.toJavaList(((JDTModule) module).getPackageFragmentRoots());
    }

    static public IdeModuleManager<IProject,IResource,IFolder,IFile> newModuleManager(Context context, CeylonProject<IProject> ceylonProject) {
        return new JDTModuleManager(context, ceylonProject);
    }

    static public IdeModuleSourceMapper<IProject,IResource,IFolder,IFile> newModuleSourceMapper(Context context, IdeModuleManager<IProject> moduleManager) {
        return new JDTModuleSourceMapper(context, moduleManager);
    }

    static public IdePlatformUtils platformUtils() {
        IdePlatformUtils utils = eclipsePlatformUtils_.get();
        utils.register();
        return utils;
    }
}
