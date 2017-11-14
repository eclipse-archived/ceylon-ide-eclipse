/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.browser;

import org.eclipse.jface.text.DefaultInformationControl;


/**
 * A browser input contains an input element and
 * a previous and a next input, if available.
 *
 * The browser input also provides a human readable
 * name of its input element.
 *
 * @since 3.4
 */
public abstract class BrowserInput {

    private final BrowserInput fPrevious;
    private BrowserInput fNext;

    /**
     * Create a new Browser input.
     *
     * @param previous the input previous to this or <code>null</code> if this is the first
     */
    public BrowserInput(BrowserInput previous) {
        fPrevious= previous;
        if (previous != null)
            previous.fNext= this;
    }

    /**
     * The previous input or <code>null</code> if this
     * is the first.
     *
     * @return the previous input or <code>null</code>
     */
    public BrowserInput getPrevious() {
        return fPrevious;
    }

    /**
     * The next input or <code>null</code> if this
     * is the last.
     *
     * @return the next input or <code>null</code>
     */
    public BrowserInput getNext() {
        return fNext;
    }

    /**
     * @return the HTML contents
     */
    public abstract String getHtml();
    
    /**
     * A human readable name for the input.
     *
     * @return the input name
     */
    public abstract String getInputName();

    /**
     * Returns the HTML from {@link #getHtml()}.
     * This is a fallback mode for platforms where the {@link BrowserInformationControl}
     * is not available and this input is passed to a {@link DefaultInformationControl}.
     *
     * @return {@link #getHtml()}
     */
    public String toString() {
        return getHtml();
    }

}
