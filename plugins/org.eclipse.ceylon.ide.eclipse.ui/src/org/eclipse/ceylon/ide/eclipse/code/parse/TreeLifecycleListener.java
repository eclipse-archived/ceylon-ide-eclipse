/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.parse;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A language service that needs to be notified in order to 
 * update in response to source code changes.
 * 
 * @author Claffra
 * @author rfuhrer@watson.ibm.com
 */
public interface TreeLifecycleListener  {

    public enum Stage { NONE, LEXICAL_ANALYSIS, SYNTACTIC_ANALYSIS, FOR_OUTLINE, TYPE_ANALYSIS }

    /**
     * @return The stage at which the listener should be
     *         notified
     */
    public Stage getStage();
    
    /**
     * Notify the listener that the document has been updated 
     * and a new AST has been computed
     * 
     * @param parseController the parse controller that, among 
     *        other things, provides the most recent AST
     * @param monitor the progress monitor; listener should 
     *        cancel when monitor.isCanceled() is true
     */
    public void update(CeylonParseController parseController, 
            IProgressMonitor monitor);
    
}
