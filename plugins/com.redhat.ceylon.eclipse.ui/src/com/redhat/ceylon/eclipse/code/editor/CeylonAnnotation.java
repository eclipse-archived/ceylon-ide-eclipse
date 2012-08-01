package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.jface.text.quickassist.IQuickFixableAnnotation;
import org.eclipse.jface.text.source.Annotation;

public class CeylonAnnotation extends Annotation 
        implements IQuickFixableAnnotation {
	
    private static int counter = 0;
	
	private CeylonEditor editor;
	private Boolean fixable;
	private final int count;
	private final int code;
	private final int severity;

	public CeylonAnnotation(String type, String text, 
			CeylonEditor editor, int code, int severity) {
		super(type, false, text);
		this.editor = editor;
		this.code = code;
		this.severity = severity;
		count = counter++;
	}

	public int getId() {
		return code;
	}

	public int getSeverity() {
		return severity;
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