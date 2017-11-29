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
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.containsCeylonTestImport;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getModule;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getPackage;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getProject;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getShell;
import static org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.test.eclipse.plugin.CeylonTestMessages;
import org.eclipse.ceylon.test.eclipse.plugin.CeylonTestPlugin;
import org.eclipse.ceylon.test.eclipse.plugin.util.AddCeylonTestImport;

public class CeylonTestDependencyStatusHandler implements IStatusHandler {

    public static final IStatus CODE = new Status(IStatus.ERROR, CeylonTestPlugin.PLUGIN_ID, 1001, "", null);

    @Override
    public Object handleStatus(IStatus status, Object source) throws CoreException {
        return validateCeylonTestDependency((ILaunchConfiguration) source);
    }

    private boolean validateCeylonTestDependency(ILaunchConfiguration config) throws CoreException {
        IProject project = getProject(config.getAttribute(ATTR_PROJECT_NAME, (String) null));
        Set<Module> modules = getTestedModules(config);
        Set<Module> modulesWithoutDependency = getTestedModulesWithoutDependency(modules);

        if (modules.size() == modulesWithoutDependency.size()) {
            boolean answer = showMissingCeylonTestDependencyDialog(project, modulesWithoutDependency);
            if (answer) {
                addCeylonTestImport(project, modulesWithoutDependency);
            }
            return answer;
        }

        return true;
    }

    private Set<Module> getTestedModules(ILaunchConfiguration config) throws CoreException {
        Set<Module> modules = new HashSet<Module>();
    
        List<CeylonTestLaunchConfigEntry> entries = CeylonTestLaunchConfigEntry.buildFromLaunchConfig(config);
        for (CeylonTestLaunchConfigEntry entry : entries) {
            IProject project = getProject(entry.getProjectName());
            switch (entry.getType()) {
            case PROJECT:
                modules.addAll(getProjectDeclaredSourceModules(project));
                break;
            case MODULE:
                Module module = getModule(project, entry.getModPkgDeclName());
                modules.add(module);
                break;
            case PACKAGE:
                Package pkg = getPackage(project, entry.getModPkgDeclName());
                modules.add(pkg.getModule());
                break;
            case CLASS:
            case CLASS_LOCAL:
            case METHOD:
            case METHOD_LOCAL:
                String[] split = entry.getModPkgDeclName().split("::");
                String pkgName = split[0];
                Package pkg2 = getPackage(project, pkgName);
                modules.add(pkg2.getModule());
                break;
            }
        }
    
        return modules;
    }

    private Set<Module> getTestedModulesWithoutDependency(Set<Module> modules) {
        Set<Module> modulesWithoutCeylonTestImport = new HashSet<Module>();
        for (Module module : modules) {
            if( !containsCeylonTestImport(module) ) {
                modulesWithoutCeylonTestImport.add(module);
            }
        }
        return modulesWithoutCeylonTestImport;
    }

    private boolean showMissingCeylonTestDependencyDialog(final IProject project, final Set<Module> modulesWithoutDependency) {
        final StringBuilder moduleNames = new StringBuilder();
        moduleNames.append(System.getProperty("line.separator"));
        for(Module module : modulesWithoutDependency) {
            moduleNames.append(System.getProperty("line.separator"));
            moduleNames.append(module.getNameAsString());
        }
        
        boolean answer = MessageDialog.openQuestion(getShell(), 
                CeylonTestMessages.errorDialogTitle, 
                CeylonTestMessages.errorMissingCeylonTestImport + moduleNames.toString());
        
        return answer;
    }

    private void addCeylonTestImport(IProject project, Set<Module> modulesWithoutDependency) throws CoreException {
        for (Module module : modulesWithoutDependency) {
            AddCeylonTestImport.addCeylonTestImport(project, module);
        }
    }

}