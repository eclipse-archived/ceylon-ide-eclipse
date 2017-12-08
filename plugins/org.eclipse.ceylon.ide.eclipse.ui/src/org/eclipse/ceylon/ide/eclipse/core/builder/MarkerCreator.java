/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.builder;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.MODULE_DEPENDENCY_PROBLEM_MARKER_ID;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

import org.eclipse.ceylon.compiler.typechecker.analyzer.ModuleSourceMapper;
import org.eclipse.ceylon.compiler.typechecker.tree.Message;
import org.eclipse.ceylon.ide.eclipse.util.ErrorVisitor;

/**
 * This class provides a message handler that creates markers in
 * response to received messages.
 * 
 * MarkerCreators are instantiated with a file (IFile) and a parse
 * controller (IParseController).  The parse controller should
 * be parsing the file and generating the messages that are
 * received by the MarkerCreator.  The MarkerCreator, in turn,
 * creates a problem marker for each error message received,
 * uses the parse controller to compute a line number for the
 * token provided with each message, and attaches the marker to
 * the given file at the computed line.
 */
public class MarkerCreator extends ErrorVisitor {
    
    protected IFile file;
    public static final String ERROR_CODE_KEY= "errorCode";

    public MarkerCreator(IFile file) {
        this.file = file;
    }

    @Override
    public void handleMessage(int startOffset, int endOffset,
            int startCol, int startLine, Message message) {
        
        String[] attributeNames= new String[] {
                IMarker.LINE_NUMBER, 
                IMarker.CHAR_START, IMarker.CHAR_END, 
                IMarker.MESSAGE, 
                IMarker.PRIORITY, 
                IMarker.SEVERITY,
                ERROR_CODE_KEY,
                IMarker.SOURCE_ID
            };
        Object[] values = new Object[] {
                startLine, 
                startOffset, endOffset, 
                message.getMessage(), 
                IMarker.PRIORITY_HIGH, 
                getSeverity(message, getWarnForErrors()),
                message.getCode(),
                CeylonBuilder.SOURCE
            };
        try {
            file.createMarker(
                    message instanceof ModuleSourceMapper.ModuleDependencyAnalysisError ?
                            MODULE_DEPENDENCY_PROBLEM_MARKER_ID :
                            PROBLEM_MARKER_ID)
                .setAttributes(attributeNames, values);
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
