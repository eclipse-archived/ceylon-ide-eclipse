package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.CAN_LAUNCH_AS_CEYLON_JAVASCIPT_MODULE;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.CAN_LAUNCH_AS_CEYLON_JAVA_MODULE;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.CAN_LAUNCH_AS_CEYLON_SWARM_PACKAGED_JAVA_MODULE;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.CEYLON_FILE_EXTENSION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.eclipse.code.navigator.SourceModuleNode;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.ModuleImport;

public class CeylonRunPropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        IProject project;
        if (receiver instanceof IProject) {
            project = (IProject) receiver;
        }
        else if (receiver instanceof IJavaProject) {
            project = ((IJavaProject) receiver).getProject();
        }
        else if (receiver instanceof IPackageFragmentRoot) {
            IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot) receiver;
            project = packageFragmentRoot.getJavaProject().getProject();
        }
        else if (receiver instanceof IPackageFragment) {
            IPackageFragment packageFragment = (IPackageFragment) receiver;
            project = packageFragment.getJavaProject().getProject();
        }
        else if (receiver instanceof IFile) {
            IFile file = (IFile) receiver;
            project = file.getProject();
        }
        else if (receiver instanceof IFileEditorInput) {
            IFileEditorInput fileEditorInput = (IFileEditorInput) receiver;
            project = fileEditorInput.getFile().getProject();
        }
        else {
            return false;
        }
            
        if (! isCeylonProject(project)) {
            return false;
        }
        
        if (CAN_LAUNCH_AS_CEYLON_JAVA_MODULE.equals(property) || 
            CAN_LAUNCH_AS_CEYLON_JAVASCIPT_MODULE.equals(property)) {
        	if (! LaunchHelper.isBuilderEnabled(project, property)) {
        	    return false;
        	}
        	
            if (receiver instanceof IFile) {
                IFile file = (IFile) receiver;
                return isCeylonFile(file);
            }
            else if (receiver instanceof IFileEditorInput) {
                IFileEditorInput fileEditorInput = (IFileEditorInput) receiver;
                return isCeylonFile(fileEditorInput.getFile());
            }
            return true;
        }
        
        if (CAN_LAUNCH_AS_CEYLON_SWARM_PACKAGED_JAVA_MODULE.equals(property)) {
            List<Module> modulesToScanForJeeImport;
            if (receiver instanceof SourceModuleNode) {
                modulesToScanForJeeImport = Arrays.<Module>asList(((SourceModuleNode)receiver).getModule());
            } else {
                Collection<Module> sourceModules = CeylonBuilder.getProjectDeclaredSourceModules(project);
                modulesToScanForJeeImport = new ArrayList<Module>(sourceModules.size());
                modulesToScanForJeeImport.addAll(sourceModules);
            }
            for (Module m : modulesToScanForJeeImport) {
                for (ModuleImport mi : m.getImports()) {
                    if ("javax:javaee-api".equals(mi.getModule().getNameAsString()) || 
                            "javax.javaeeapi".equals(mi.getModule().getNameAsString())) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    
    private boolean isCeylonProject(IProject project) {
        return project.isOpen() && CeylonNature.isEnabled(project);
    }

    private boolean isCeylonFile(IFile file) {
        return isCeylonProject(file.getProject()) && 
                CEYLON_FILE_EXTENSION.equals(file.getFileExtension());
    }

}