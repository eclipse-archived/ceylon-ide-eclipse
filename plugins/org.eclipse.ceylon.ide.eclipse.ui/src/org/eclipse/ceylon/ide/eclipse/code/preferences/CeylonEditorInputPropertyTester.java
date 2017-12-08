/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.preferences;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;

public class CeylonEditorInputPropertyTester extends PropertyTester {

    @Override
    public boolean test(Object receiver, String property, Object[] args,
            Object expectedValue) {
        IEditorInput input = (IEditorInput) receiver;
        IFile file = (IFile) input.getAdapter(IFile.class);
        if (file==null) {
            return false;
        }
        else {
            return new CeylonFilePropertyTester()
                .test(file, property, args, expectedValue);
        }
    }

}
