/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.jdt.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStep;

import com.sun.jdi.ThreadReference;

public class PatchedForCeylonJDIThread extends JDIThread {
    public PatchedForCeylonJDIThread(JDIDebugTarget debugTarget, ThreadReference reference) {
        super(debugTarget, reference);
    }

    /**
     * This method is synchronized, such that the step request begins before a
     * background evaluation can be performed.
     * 
     * @see IStep#stepInto()
     */
    public void stepInto() throws DebugException {
        synchronized (this) {
            if (!canStepInto()) {
                return;
            }
        }
        StepHandler handler = newStepIntoHandler();
        handler.step();
    }

    /**
     * This method is synchronized, such that the step request begins before a
     * background evaluation can be performed.
     * 
     * @see IStep#stepOver()
     */
    public void stepOver() throws DebugException {
        synchronized (this) {
            if (!canStepOver()) {
                return;
            }
        }
        StepHandler handler = newStepOverHandler();
        handler.step();
    }

    /**
     * This method is synchronized, such that the step request begins before a
     * background evaluation can be performed.
     * 
     * @see IStep#stepReturn()
     */
    public void stepReturn() throws DebugException {
        synchronized (this) {
            if (!canStepReturn()) {
                return;
            }
        }
        StepHandler handler = newStepReturnHandler();
        handler.step();
    }

    protected class StepOverHandler extends JDIThread.StepOverHandler {}
    protected class StepIntoHandler extends JDIThread.StepIntoHandler {}
    protected class StepReturnHandler extends JDIThread.StepReturnHandler {}
    
    protected StepIntoHandler newStepIntoHandler() {
        return new StepIntoHandler();
    }

    protected StepOverHandler newStepOverHandler() {
        return new StepOverHandler();
    }

    protected StepReturnHandler newStepReturnHandler() {
        return new StepReturnHandler();
    }
}
