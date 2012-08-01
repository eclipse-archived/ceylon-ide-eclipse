package com.redhat.ceylon.eclipse.code.quickfix;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.core.builder.MarkerCreator;

public class ProblemLocation {
	private final int fId;
	// private final String[] fArguments;
	private final int fOffset;
	private final int fLength;
	// private final boolean fIsError;
	private final String fMarkerType;

	//IMarker marker;
	//CeylonAnnotation annotation;

	public ProblemLocation(MarkerAnnotation annotation) throws CoreException {
		this(annotation.getMarker());
	}

	public ProblemLocation(int offset, int length, CeylonAnnotation annotation) {
		fId = annotation.getId();
		// fArguments= annotation.getArguments();
		fOffset = offset;
		fLength = length;
		// fIsError= annotation.getMarker().get
		fMarkerType = IMarker.PROBLEM;
		//this.annotation = annotation;
	}

	public ProblemLocation(IMarker marker) throws CoreException {
		fId = marker.getAttribute(MarkerCreator.ERROR_CODE_KEY, 0);
		// fArguments= annotation.getArguments();
		fOffset = marker.getAttribute(IMarker.CHAR_START, 0);
		fLength = marker.getAttribute(IMarker.CHAR_END, 0) - fOffset;
		// fIsError= annotation.getMarker().get
		fMarkerType = marker.getType();
		//this.marker = marker;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getProblemId
	 * ()
	 */
	public int getProblemId() {
		return fId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getLength ()
	 */
	public int getLength() {
		return fLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getOffset ()
	 */
	public int getOffset() {
		return fOffset;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.text.java.IProblemLocation#getMarkerType()
	 */
	public String getMarkerType() {
		return fMarkerType;
	}

	/*public Object getAttribute(String key) throws CoreException {
		if (marker != null) {
			return marker.getAttribute(key);
		}

		return annotation.getAttribute(key);
	}

	public String getAttribute(String key, String def) {
		if (marker != null) {
			return marker.getAttribute(key, def);
		}

		return (String) annotation.getAttribute(key);
	}

	public int getAttribute(String key, int def) {
		if (marker != null) {
			return marker.getAttribute(key, def);
		}

		return (Integer) annotation.getAttribute(key);
	}*/

	// /*
	// * (non-Javadoc)
	// * @see
	// org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getCoveringNode(org.eclipse.jdt.core.dom.CompilationUnit)
	// */
	// public ASTNode getCoveringNode(CompilationUnit astRoot) {
	// NodeFinder finder= new NodeFinder(fOffset, fLength);
	// astRoot.accept(finder);
	// return finder.getCoveringNode();
	// }
	//
	// /*
	// * (non-Javadoc)
	// * @see
	// org.eclipse.jdt.internal.ui.text.correction.IProblemLocation#getCoveredNode(org.eclipse.jdt.core.dom.CompilationUnit)
	// */
	// public ASTNode getCoveredNode(CompilationUnit astRoot) {
	// NodeFinder finder= new NodeFinder(fOffset, fLength);
	// astRoot.accept(finder);
	// return finder.getCoveredNode();
	// }

	// public String toString() {
	// StringBuffer buf= new StringBuffer();
	//				buf.append("Id: ").append(getErrorCode(fId)).append('\n'); //$NON-NLS-1$
	//				buf.append('[').append(fOffset).append(", ").append(fLength).append(']').append('\n'); //$NON-NLS-1$
	// String[] arg= fArguments;
	// if (arg != null) {
	// for (int i= 0; i < arg.length; i++) {
	// buf.append(arg[i]);
	// buf.append('\n');
	// }
	// }
	// return buf.toString();
	// }

	// private String getErrorCode(int code) {
	// StringBuffer buf= new StringBuffer();
	//
	// if ((code & IProblem.TypeRelated) != 0) {
	//					buf.append("TypeRelated + "); //$NON-NLS-1$
	// }
	// if ((code & IProblem.FieldRelated) != 0) {
	//					buf.append("FieldRelated + "); //$NON-NLS-1$
	// }
	// if ((code & IProblem.ConstructorRelated) != 0) {
	//					buf.append("ConstructorRelated + "); //$NON-NLS-1$
	// }
	// if ((code & IProblem.MethodRelated) != 0) {
	//					buf.append("MethodRelated + "); //$NON-NLS-1$
	// }
	// if ((code & IProblem.ImportRelated) != 0) {
	//					buf.append("ImportRelated + "); //$NON-NLS-1$
	// }
	// if ((code & IProblem.Internal) != 0) {
	//					buf.append("Internal + "); //$NON-NLS-1$
	// }
	// if ((code & IProblem.Syntax) != 0) {
	//					buf.append("Syntax + "); //$NON-NLS-1$
	// }
	// if ((code & IProblem.Javadoc) != 0) {
	//					buf.append("Javadoc + "); //$NON-NLS-1$
	// }
	// buf.append(code & IProblem.IgnoreCategoriesMask);
	//
	// return buf.toString();
	// }

}