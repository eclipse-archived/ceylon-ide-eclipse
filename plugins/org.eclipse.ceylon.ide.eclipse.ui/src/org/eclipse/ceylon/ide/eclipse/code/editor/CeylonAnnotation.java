/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.text.quickassist.IQuickFixableAnnotation;
import org.eclipse.jface.text.source.Annotation;

import org.eclipse.ceylon.compiler.typechecker.tree.Message;

public class CeylonAnnotation extends Annotation 
        implements IQuickFixableAnnotation {
    
    /** 
     * Parent annotation ID
     */
    public static final String PARSE_ANNOTATION_TYPE = PLUGIN_ID + 
            ".parseAnnotation";

    /**
     * Annotation ID for a parser annotation w/ severity = error.
     * Must match the ID of the corresponding annotationTypes 
     * extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_ERROR = PARSE_ANNOTATION_TYPE + 
            ".error";

    /**
     * Annotation ID for a parser annotation w/ severity = warning. 
     * Must match the ID of the corresponding annotationTypes 
     * extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_WARNING = PARSE_ANNOTATION_TYPE + 
            ".warning";

    /**
     * Annotation ID for a parser annotation w/ severity = info. 
     * Must match the ID of the corresponding annotationTypes 
     * extension in the plugin.xml.
     */
    public static final String PARSE_ANNOTATION_TYPE_INFO = PARSE_ANNOTATION_TYPE + 
            ".info";

    private static int counter = 0;
    
    private Boolean fixable;
    private final int count;
    private final int code;
    private final int severity;
	private final Message error;

    public static boolean isParseAnnotation(Annotation a) {
        return a.getType().startsWith(PARSE_ANNOTATION_TYPE);
    }

    public CeylonAnnotation(String type, String text, 
            int code, int severity, Message error) {
        super(type, false, text);
        this.code = code;
        this.severity = severity;
		this.error = error;
        count = counter++;
    }
    
    public boolean isFixable() {
        return getSeverity() == SEVERITY_WARNING 
                || getId() > 0;
    }

    public int getId() {
        return code;
    }

    public int getSeverity() {
        return severity;
    }

    public Message getError() {
		return error;
	}

    @Override
    public boolean isQuickFixable() 
            throws AssertionFailedException {
        return fixable;
    }
    
    @Override
    public boolean isQuickFixableStateSet() {
        return fixable!=null;
    }
    
    @Override
    public void setQuickFixable(boolean state) {
        fixable = state;
    }
    
    @Override
    public String toString() {
        return "{" + count + "}"; //"[" + getId() + "] " + getText();
    }
}