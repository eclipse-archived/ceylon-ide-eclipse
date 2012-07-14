package com.redhat.ceylon.eclipse.code.hover;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;


/**
 * Filters problems based on their types.
 */
@SuppressWarnings({"unchecked"})
public class AnnotationIterator implements Iterator {

	private Iterator fIterator;
	private Annotation fNext;
	private boolean fReturnAllAnnotations;


	/**
	 * Returns a new JavaAnnotationIterator.
	 * @param parent the parent iterator to iterate over annotations
	 * @param returnAllAnnotations whether to return all annotations or just problem annotations
	 */
	public AnnotationIterator(Iterator parent, boolean returnAllAnnotations) {
		fReturnAllAnnotations= returnAllAnnotations;
		fIterator= parent;
		skip();
	}

	private void skip() {
		while (fIterator.hasNext()) {
			Annotation next= (Annotation) fIterator.next();

			if (next.isMarkedDeleted())
				continue;

			if (fReturnAllAnnotations || next instanceof CeylonAnnotation || 
					isProblemMarkerAnnotation(next)) {
				fNext= next;
				return;
			}
		}
		fNext= null;
	}

	private static boolean isProblemMarkerAnnotation(Annotation annotation) {
		if (!(annotation instanceof MarkerAnnotation))
			return false;
		try {
			return ((MarkerAnnotation)annotation).getMarker().isSubtypeOf(IMarker.PROBLEM);
		} 
		catch (CoreException e) {
			return false;
		}
	}

	/*
	 * @see Iterator#hasNext()
	 */
	public boolean hasNext() {
		return fNext != null;
	}

	/*
	 * @see Iterator#next()
	 */
	public Object next() {
		try {
			return fNext;
		} finally {
			skip();
		}
	}

	/*
	 * @see Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
