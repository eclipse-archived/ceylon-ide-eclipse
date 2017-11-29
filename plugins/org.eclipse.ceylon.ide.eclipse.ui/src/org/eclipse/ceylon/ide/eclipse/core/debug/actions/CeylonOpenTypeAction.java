/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.debug.actions;

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoDeclaration;
import static org.eclipse.ceylon.ide.eclipse.code.outline.HierarchyView.showHierarchyView;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.isCeylonDeclaration;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.toCeylonDeclaration;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.debug.ui.actions.ActionMessages;
import org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction;
import org.eclipse.ui.PartInitException;

import org.eclipse.ceylon.model.typechecker.model.Declaration;

public abstract class CeylonOpenTypeAction extends OpenTypeAction {
    
    @Override
    protected Object resolveSourceElement(Object e) throws CoreException {
        return super.resolveSourceElement(e);
    }
    
    @Override
    protected void openInEditor(Object sourceElement) 
            throws CoreException {
        if (sourceElement instanceof IJavaElement && 
                isCeylonDeclaration((IJavaElement) sourceElement)) {
            IJavaElement javaElement = (IJavaElement) sourceElement;
            IProject project = javaElement.getJavaProject().getProject();
            if (isHierarchy()) {
                Declaration declaration = 
                        toCeylonDeclaration(project, javaElement);
                if (declaration!=null) {
                    try {
                        showHierarchyView().focusOn(declaration);
                        return;
                    }
                    catch (PartInitException e) {
                        e.printStackTrace();
                    }
                }
                typeHierarchyError();
            }
            else {
                Declaration declaration = 
                        toCeylonDeclaration(project, javaElement);
                if (declaration != null) {
                    gotoDeclaration(declaration);
                    return;
                }
                showErrorMessage(ActionMessages.OpenTypeAction_2);
            }
        } else {
            super.openInEditor(sourceElement);
        }
    }
       
}
