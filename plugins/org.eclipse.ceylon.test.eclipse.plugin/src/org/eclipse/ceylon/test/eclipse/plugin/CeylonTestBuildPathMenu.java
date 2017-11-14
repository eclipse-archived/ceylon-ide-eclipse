/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin;

import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.containsCeylonTestImport;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.getModule;
import static org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil.isCeylonProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.actions.ActionGroup;

import org.eclipse.ceylon.ide.eclipse.code.explorer.PackageExplorerActionGroup;
import org.eclipse.ceylon.ide.eclipse.code.explorer.PackageExplorerPart;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.test.eclipse.plugin.util.AddCeylonTestImport;
import org.eclipse.ceylon.test.eclipse.plugin.util.CeylonTestUtil;

public class CeylonTestBuildPathMenu {
    
    private static final String BUILD_PATH_MENU_ID = "org.eclipse.jdt.ui.buildpath.menu";
    
    public static void install() {
        IWorkbenchPage page = CeylonTestUtil.getActivePage();
        
        IViewPart part = page.findView(PackageExplorerPart.VIEW_ID);
        if (part != null) {
            PackageExplorerPart packageExplorerPart = (PackageExplorerPart) part;
            install(packageExplorerPart);
        }
        
        page.addPartListener(new IPartListener2() {
            @Override
            public void partOpened(IWorkbenchPartReference partRef) {
                if (partRef.getId().equals(PackageExplorerPart.VIEW_ID)) {
                    IWorkbenchPart part = partRef.getPart(false);
                    if (part != null) {
                        PackageExplorerPart packageExplorerPart = (PackageExplorerPart) part;
                        install(packageExplorerPart);
                    }
                }
            }
            @Override
            public void partVisible(IWorkbenchPartReference partRef) {}
            @Override
            public void partInputChanged(IWorkbenchPartReference partRef) {}
            @Override
            public void partHidden(IWorkbenchPartReference partRef) {}
            @Override
            public void partDeactivated(IWorkbenchPartReference partRef) {}
            @Override
            public void partClosed(IWorkbenchPartReference partRef) {}
            @Override
            public void partBroughtToTop(IWorkbenchPartReference partRef) {}
            @Override
            public void partActivated(IWorkbenchPartReference partRef) {}
        });
    }
    
    @SuppressWarnings("restriction")
    private static void install(PackageExplorerPart packageExplorerPart) {
        ISelectionProvider selectionProvider = packageExplorerPart.getSite().getSelectionProvider();
        PackageExplorerActionGroup packageExplorerActionGroup = packageExplorerPart.getPackageExplorerActionGroup();
        packageExplorerActionGroup.addGroup(new AddCeylonTestImportActionGroup(selectionProvider));
    }

    private static class AddCeylonTestImportActionGroup extends ActionGroup {
        
        private final ISelectionProvider selectionProvider;
        
        public AddCeylonTestImportActionGroup(ISelectionProvider selectionProvider) {
            this.selectionProvider = selectionProvider;
        }
        
        @Override
        public void fillContextMenu(IMenuManager menu) {
            IMenuManager buildPathMenu = (IMenuManager) menu.find(BUILD_PATH_MENU_ID);
            if (buildPathMenu != null) {
                buildPathMenu.addMenuListener(new IMenuListener() {
                    @Override
                    public void menuAboutToShow(IMenuManager manager) {
                        ISelection selection = selectionProvider.getSelection();
                        if (selection instanceof IStructuredSelection) {
                            IStructuredSelection structuredSelection = (IStructuredSelection) selection;
                            if (structuredSelection.size() == 1) {
                                Object firstElement = structuredSelection.getFirstElement();
                                if (firstElement instanceof IPackageFragment) {
                                    IPackageFragment packageFragment = (IPackageFragment) firstElement;
                                    IProject project = packageFragment.getJavaProject().getProject();
                                    if (isCeylonProject(project)) {
                                        Module module = getModule(project, packageFragment.getElementName());
                                        if (module != null) {
                                            manager.add(new AddCeylonTestImportAction(project, module));
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
        
    }
    
    private static class AddCeylonTestImportAction extends Action {

        private final IProject project;
        private final Module module;

        public AddCeylonTestImportAction(IProject project, Module module) {
            super(CeylonTestMessages.addCeylonTestImport);
            this.project = project;
            this.module = module;
            setDescription(CeylonTestMessages.addCeylonTestImport);
            setEnabled(!containsCeylonTestImport(module));
        }

        @Override
        public void run() {
            try {
                AddCeylonTestImport.addCeylonTestImport(project, module);
            } catch (CoreException e) {
                CeylonTestPlugin.logError("", e);
            }
        }

    }   

}
