package com.redhat.ceylon.eclipse.code.editor;

import java.util.Map;

import org.eclipse.imp.editor.quickfix.IAnnotation;
import org.eclipse.imp.parser.IMessageHandler;
import org.eclipse.jface.text.source.Annotation;

public class DefaultAnnotation extends Annotation {
	private Map<String, Object> attributes;
	private CeylonEditor editor;

	public DefaultAnnotation(String type, boolean isPersistent, String text, CeylonEditor editor,
			Map<String, Object> attributes) {
		super(type, isPersistent, text);
		this.editor = editor;
		this.attributes = attributes;
	}

	public DefaultAnnotation(boolean isPersistent) {
		super(isPersistent);
	}

	public int getId() {
		if (attributes.containsKey(IMessageHandler.ERROR_CODE_KEY)) {
			return (Integer) attributes.get(IMessageHandler.ERROR_CODE_KEY);
		}
		return -1;
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public int getSeverity() {
		if (attributes.containsKey(IMessageHandler.SEVERITY_KEY)) {
			return (Integer) attributes.get(IMessageHandler.SEVERITY_KEY);
		}
		return IAnnotation.ERROR;
	}

	public CeylonEditor getEditor() {
		return editor;
	}
}