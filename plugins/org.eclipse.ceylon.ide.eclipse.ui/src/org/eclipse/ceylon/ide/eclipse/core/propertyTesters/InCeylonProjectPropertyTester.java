/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
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