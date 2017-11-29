/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.launch;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectDeclaredSourceModules;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.IFileEditorInput;

import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil;

public class CeylonTestPropertyTester extends PropertyTester {

    private static final String CAN_LAUNCH_AS_CEYLON_TEST_PROPERTY = "canLaunchAsCeylonTest";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
    	if (CAN_LAUNCH_AS_CEYLON_TEST_PROPERTY.equals(property)) {
    	    if (receiver instanceof IProject) {
    	        return isProjectWithAtLeastOneTestableModule((IProject) receiver);
    	    } else if (receiver instanceof IJavaProject) {
    	        return isProjectWithAtLeastOneTestableModule(((IJavaProject) receiver).getProject());
    	    } else if (receiver instanceof IPackageFragmentRoot) {
    	        return isFolderWithAtLeastOneTestableModule((IPackageFragmentRoot) receiver);
    	    } else if (receiver instanceof IPackageFragment) {
    	        return isPackageFromTestableModule((IPackageFragment) receiver);
    	    } else if (receiver instanceof IFile) {
    	        return isFileFromTestableModule((IFile) receiver);
    	    } else if (receiver instanceof IFileEditorInput) {
    	        return isFileFromTestableModule(((IFileEditorInput) receiver).getFile());
    	    }
    	}
    	return false;
    }

    private boolean isProjectWithAtLeastOneTestableModule(IProject project) {
        for (Module module : getProjectDeclaredSourceModules(project)) {
            if (CeylonTestUtil.containsCeylonTestImport(module)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isFolderWithAtLeastOneTestableModule(IPackageFragmentRoot packageFragmentRoot) {
        try {
            IJavaElement[] children = packageFragmentRoot.getChildren();
            for (IJavaElement child : children) {
                if (child instanceof IPackageFragment) {
                    IPackageFragment packageFragment = (IPackageFragment) child;
                    if (isPackageFromTestableModule(packageFragment)) {
                        return true;
                    }
                }
            }
        } catch (JavaModelException e) {
            return false;
        }
        return false;
    }

    private boolean isPackageFromTestableModule(IPackageFragment packageFragment) {
        IProject project = packageFragment.getJavaProject().getProject();
        String packageFragmentName = packageFragment.getElementName();

        for (Module module : getProjectDeclaredSourceModules(project)) {
            String moduleName = module.getNameAsString();
            if (packageFragmentName.equals(moduleName) || packageFragmentName.startsWith(moduleName + ".")) {
                return CeylonTestUtil.containsCeylonTestImport(module);
            }
        }

        return false;
    }

    private boolean isFileFromTestableModule(IFile file) {
        try {
            IJavaProject javaProject = JavaCore.create(file.getProject());
            IPackageFragment packageFragment = javaProject.findPackageFragment(file.getParent().getFullPath());
            return packageFragment!=null && isPackageFromTestableModule(packageFragment);
        } catch (JavaModelException e) {
            CeylonTestPlugin.logError("", e);
        }
        return false;
    }

}