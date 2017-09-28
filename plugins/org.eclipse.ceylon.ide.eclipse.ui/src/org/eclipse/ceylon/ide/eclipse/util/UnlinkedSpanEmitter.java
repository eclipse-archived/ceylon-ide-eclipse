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