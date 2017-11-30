/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import static org.eclipse.core.resources.IMarker.SEVERITY_ERROR;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;

import org.eclipse.ceylon.compiler.typechecker.analyzer.UsageWarning;
import org.eclipse.ceylon.compiler.typechecker.tree.Message;

public abstract class ErrorVisitor 
    extends org.eclipse.ceylon.ide.common.util.ErrorVisitor {
    
    protected int getSeverity(Message error, boolean expected) {
        return expected || error instanceof UsageWarning ? 
            SEVERITY_WARNING : SEVERITY_ERROR;
    }

    @Override
    public Object handleMessage(long startOffset, long endOffset,
            long startCol, long startLine, Message error) {
        
        handleMessage((int) startOffset, (int) endOffset, (int) startCol,
                (int) startLine, error);
        return null;
    }
    
    protected abstract void handleMessage(int startOffset, int endOffset, 
            int startCol, int startLine, Message error);
}