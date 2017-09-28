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