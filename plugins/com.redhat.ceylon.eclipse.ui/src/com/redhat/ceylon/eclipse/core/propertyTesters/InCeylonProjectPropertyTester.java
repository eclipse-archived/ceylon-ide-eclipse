package org.eclipse.ceylon.ide.eclipse.core.propertyTesters;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jdt.core.IJavaElement;

import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;

public class InCeylonProjectPropertyTester extends PropertyTester {

    private static final String IS_IN_CEYLON_PROJECT_PROPERTY = "isInCeylonProject";

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        if (!(receiver instanceof IJavaElement)) {
            return false;
        }
        IJavaElement javaElement = (IJavaElement) receiver;
        
        if (IS_IN_CEYLON_PROJECT_PROPERTY.equals(property)) {
            return CeylonNature.isEnabled(javaElement.getJavaProject().getProject());
        }
        return false;
    }

}