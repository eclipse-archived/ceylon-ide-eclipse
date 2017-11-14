/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import com.github.rjeschke.txtmark.SpanEmitter;

public final class UnlinkedSpanEmitter implements SpanEmitter {
    @Override
    public void emitSpan(StringBuilder out, String content) {
    	int bar = content.indexOf('|');
    	if (bar>0) {
    		out.append(content.substring(0, bar));
    	}
    	else {
    		out.append("<code>").append(content).append("</code>");
    	}
    }
}