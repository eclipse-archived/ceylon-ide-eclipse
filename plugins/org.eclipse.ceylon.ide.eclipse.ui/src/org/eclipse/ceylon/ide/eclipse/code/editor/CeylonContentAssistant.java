/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import java.lang.ref.WeakReference;

import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.swt.custom.VerifyKeyListener;

public class CeylonContentAssistant extends ContentAssistant {
    private WeakReference<VerifyKeyListener> autoAssistListener = new WeakReference<VerifyKeyListener>(null);
    private boolean incompleteResults = false;
    
    public static String secondLevelStatusMessage = "";
    public static String retrieveCompleteResultsStatusMessage = "";
    
    
    protected AutoAssistListener createAutoAssistListener() {
        AutoAssistListener listener = super.createAutoAssistListener();
        autoAssistListener = new WeakReference<VerifyKeyListener>(listener);
        return listener;            
    }
    
    public VerifyKeyListener getAutoAssistListener() {
        return autoAssistListener.get();
    }
    
    @Override
    public void setStatusMessage(String message) {
        super.setStatusMessage(message);
        if (! secondLevelStatusMessage.equals(message)) {
            incompleteResults = true;
        } else {
            incompleteResults = false;
        }
    }
    
    public boolean areResultIncomplete() {
        return incompleteResults;
    }
}