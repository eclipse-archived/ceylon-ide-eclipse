/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.propertyTesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.RootFolderType;

public class InCeylonRootFolderPropertyTester extends PropertyTester {

    private static final String IS_IN_CEYLON_SOURCE_FOLDER_PROPERTY = "isInCeylonSourceFolder";
    private static final String IS_IN_CEYLON_RESOURCE_FOLDER_PROPERTY = "isInCeylonResourceFolder";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        RootFolderType rootFolderType = null;
        IResource resource = null;
        if (receiver instanceof IResource) {
            resource = (IResource) receiver;
        }
        
        if (receiver instanceof IJavaElement) {
            resource = ((IJavaElement) receiver).getResource();
        }
        
        if (resource instanceof IFolder) {
            rootFolderType= CeylonBuilder.getRootFolderType((IFolder)resource);            
        }
        if (resource instanceof IFile) {
            rootFolderType= CeylonBuilder.getRootFolderType((IFile)resource);
        }
        
        if (rootFolderType != null) {
            if (IS_IN_CEYLON_SOURCE_FOLDER_PROPERTY.equals(property)) {
                return rootFolderType.equals(RootFolderType.SOURCE);
            }
            if (IS_IN_CEYLON_RESOURCE_FOLDER_PROPERTY.equals(property)) {
                return rootFolderType.equals(RootFolderType.RESOURCE);
            }
        }
        
        return false;
    }

}