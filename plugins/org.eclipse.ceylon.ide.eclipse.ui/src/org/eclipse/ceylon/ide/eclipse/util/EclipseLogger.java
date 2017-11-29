/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import org.eclipse.ceylon.common.log.Logger;


public class EclipseLogger implements Logger {

    private int errors;
    
    public int getErrors(){
        return errors;
    }

    @Override
    public void error(String str) {
        errors++;
        System.err.println("Error: "+str);
    }

    @Override
    public void warning(String str) {
        System.err.println("Warning: "+str);
    }

    @Override
    public void info(String str) {
        System.err.println("Note: "+str);
    }

    @Override
    public void debug(String str) {
        //System.err.println("Debug: "+str);
    }

}
