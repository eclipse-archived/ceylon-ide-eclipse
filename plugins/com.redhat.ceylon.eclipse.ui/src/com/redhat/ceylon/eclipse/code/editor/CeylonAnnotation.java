package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.IMessageHandler.ERROR_CODE_KEY;
import static com.redhat.ceylon.eclipse.code.parse.IMessageHandler.SEVERITY_KEY;

import java.util.Map;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.quickassist.IQuickFixableAnnotation;
import org.eclipse.jface.text.source.Annotation;

public class CeylonAnnotation extends Annotation implements IQuickFixableAnnotation {
	private static int counter = 0;
	
	private Map<String, Object> attributes;
	private CeylonEditor editor;
	private Boolean fixable;
	private final int count;

	public CeylonAnnotation(String type, boolean isPersistent, String text, 
			CeylonEditor editor, Map<String, Object> attributes) {
		super(type, isPersistent, text);
		this.editor = editor;
		this.attributes = attributes;
		count = counter++;
	}

	public CeylonAnnotation(boolean isPersistent) {
		super(isPersistent);
		count = counter++;
	}

	public int getId() {
		if (attributes.containsKey(ERROR_CODE_KEY)) {
			return (Integer) attributes.get(ERROR_CODE_KEY);
		}
		return -1;
	}

	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	public int getSeverity() {
		if (attributes.containsKey(SEVERITY_KEY)) {
			return (Integer) attributes.get(SEVERITY_KEY);
		}
		return IStatus.ERROR;
	}

	public CeylonEditor getEditor() {
		return editor;
	}
	
	@Override
	public boolean isQuickFixable() throws AssertionFailedException {
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