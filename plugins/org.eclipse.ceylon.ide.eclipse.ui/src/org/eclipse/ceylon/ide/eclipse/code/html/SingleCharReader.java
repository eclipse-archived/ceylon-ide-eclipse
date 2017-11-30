/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.html;

import java.io.IOException;
import java.io.Reader;


/**
 * <p>
 * Moved into this package from <code>org.eclipse.jface.internal.text.revisions</code>.</p>
 */
public abstract class SingleCharReader extends Reader {

    /**
     * @see Reader#read()
     */
    public abstract int read() throws IOException;

    /**
     * @see Reader#read(char[],int,int)
     */
    public int read(char cbuf[], int off, int len) throws IOException {
        int end= off + len;
        for (int i= off; i < end; i++) {
            int ch= read();
            if (ch == -1) {
                if (i == off)
                    return -1;
                return i - off;
            }
            cbuf[i]= (char)ch;
        }
        return len;
    }

    /**
     * @see Reader#ready()
     */
    public boolean ready() throws IOException {
        return true;
    }

    /**
     * Returns the readable content as string.
     * @return the readable content as string
     * @exception IOException in case reading fails
     */
    public String getString() throws IOException {
        StringBuffer buf= new StringBuffer();
        int ch;
        while ((ch= read()) != -1) {
            buf.append((char)ch);
        }
        return buf.toString();
    }
}
