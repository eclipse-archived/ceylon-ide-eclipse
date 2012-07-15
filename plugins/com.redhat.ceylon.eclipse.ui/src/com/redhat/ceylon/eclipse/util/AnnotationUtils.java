package com.redhat.ceylon.eclipse.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.AnnotationBag;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.MarkOccurrencesAction;
import com.redhat.ceylon.eclipse.code.parse.IMessageHandler;

public class AnnotationUtils {
    // Following is just used for debugging to discover new annotation types to filter out
    //  private static Set<String> fAnnotationTypes= new HashSet<String>();

    private static Set<String> sAnnotationTypesToFilter= new HashSet<String>();

    static {
        sAnnotationTypesToFilter.add("org.eclipse.ui.workbench.texteditor.quickdiffUnchanged");
        sAnnotationTypesToFilter.add("org.eclipse.ui.workbench.texteditor.quickdiffChange");
        sAnnotationTypesToFilter.add("org.eclipse.ui.workbench.texteditor.quickdiffAddition");
        sAnnotationTypesToFilter.add("org.eclipse.ui.workbench.texteditor.quickdiffDeletion");
        sAnnotationTypesToFilter.add("org.eclipse.debug.core.breakpoint");
        sAnnotationTypesToFilter.add(MarkOccurrencesAction.OCCURRENCE_ANNOTATION);
        sAnnotationTypesToFilter.add(ProjectionAnnotation.TYPE);
    }

    private AnnotationUtils() { }

    /**
     * @return a nicely-formatted plain text string for the given set of annotations
     */
    public static String formatAnnotationList(List<Annotation> annotations) {
        if (annotations != null) {
            if (annotations.size() == 1) {
                // optimization
                Annotation annotation= (Annotation) annotations.get(0);
                String message= annotation.getText();
                if (message != null && message.trim().length() > 0)
                    return HTMLPrinter.formatSingleMessage(message);
            } else {
                List<String> messages= new ArrayList<String>();
                for(Annotation annotation : annotations) {
                    String message= annotation.getText();
                    if (message != null && message.trim().length() > 0)
                        messages.add(message.trim());
                }
                if (messages.size() == 1)
                    return HTMLPrinter.formatSingleMessage((String) messages.get(0));
                if (messages.size() > 1)
                    return HTMLPrinter.formatMultipleMessages(messages);
            }
        }
        return null;
    }

    /**
     * @return true, if the given Annotation and Position are redundant, given the annotation
     * information in the given Map
     */
	public static boolean addAndCheckDuplicateAnnotation(Map<Integer, List<Object>> map, 
			Annotation annotation, Position position) {
	    List<Object> annotationsAtPosition;

	    if (!map.containsKey(position.offset)) {
			annotationsAtPosition = new ArrayList<Object>();
			map.put(position.offset, annotationsAtPosition);
		} else {
		    annotationsAtPosition = map.get(position.offset);
		}

		// TODO this should call out to a language extension point first to see if the language can resolve duplicates
		
		// Check to see if an error code is present on the marker / annotation
		Integer errorCode = -1;

		if (annotation instanceof CeylonAnnotation) {
			errorCode = ((CeylonAnnotation) annotation).getId();
		} 
		else if (annotation instanceof MarkerAnnotation) {
			errorCode = ((MarkerAnnotation) annotation).getMarker()
					.getAttribute(IMessageHandler.ERROR_CODE_KEY, -1);
		}
		
		// Fall back to comparing the text associated with this annotation
		if (errorCode == -1) {
			if (!annotationsAtPosition.contains(annotation.getText())) {
			    annotationsAtPosition.add(annotation.getText());
				return false;
			}			
		} else if (!annotationsAtPosition.contains(errorCode)) {
		    annotationsAtPosition.add(errorCode);
			return false;	
		}

		return true;
	}

	/**
	 * @return the list of Annotations that reside at the given line for the given ISourceViewer
	 */
    public static List<Annotation> getAnnotationsForLine(ISourceViewer viewer, final int line) {
        final IDocument document= viewer.getDocument();
        IPositionPredicate posPred= new IPositionPredicate() {
            public boolean matchPosition(Position p) {
                return AnnotationUtils.offsetIsAtLine(p, document, line);
            }
        };
        return getAnnotations(viewer, posPred);
    }

    /**
     * @return the list of Annotations that reside at the given offset for the given ISourceViewer
     */
    public static List<Annotation> getAnnotationsForOffset(ISourceViewer viewer, final int offset) {
        IPositionPredicate posPred= new IPositionPredicate() {
            public boolean matchPosition(Position p) {
                return offset >= p.offset && offset < p.offset + p.length;
            }
        };
        return getAnnotations(viewer, posPred);
    }

    /**
     * @return true, if the given Position resides at the given line of the given IDocument
     */
    public static boolean offsetIsAtLine(Position position, IDocument document, int line) {
        if (position.getOffset() > -1 && position.getLength() > -1) {
            try {
            	// RMF 11/10/2006 - This used to add 1 to the line computed by the document,
                // which appears to be bogus. First, it didn't work right (annotation hovers
                // never appeared); second, the line passed in comes from the Eclipse
                // framework, so it should be consistent (wrt the index base) with what the
                // IDocument API provides.
                int posLine= document.getLineOfOffset(position.getOffset());
                return line == posLine;
            } catch (BadLocationException x) {
            }
        }
        return false;
    }

    /**
     * @return the IAnnotationModel for the given ISourceViewer, if any
     */
    // TODO get rid of this one-line wrapper
    public static IAnnotationModel getAnnotationModel(ISourceViewer viewer) {
        // if (viewer instanceof ISourceViewerExtension2) {
        // ISourceViewerExtension2 extension= (ISourceViewerExtension2) viewer;
        //
        // return extension.getVisualAnnotationModel();
        // }
        return viewer.getAnnotationModel();
    }

    /**
     * @return the list of Annotations on the given ISourceViewer that satisfy the given
     * IPositionPredicate and that are worth showing to the user as text (e.g., ignoring
     * debugger breakpoint annotations and source folding annotations)
     */
    public static List<Annotation> getAnnotations(ISourceViewer viewer, IPositionPredicate posPred) {
		IAnnotationModel model = getAnnotationModel(viewer);
		if (model == null)
			return null;
		List<Annotation> annotations = new ArrayList<Annotation>();
		Iterator<?> iterator = model.getAnnotationIterator();

		Map<Integer, List<Object>> map = new HashMap<Integer, List<Object>>();

		while (iterator.hasNext()) {
			Annotation annotation = (Annotation) iterator.next();
			Position position = model.getPosition(annotation);
			
			if (position == null)
				continue;
			if (!posPred.matchPosition(position))
				continue;
			if (annotation instanceof AnnotationBag) {
				AnnotationBag bag = (AnnotationBag) annotation;
				for (Iterator<?> e = bag.iterator(); e.hasNext(); ) {
					Annotation bagAnnotation = (Annotation) e.next();
					position = model.getPosition(bagAnnotation);
					if (position != null
							&& includeAnnotation(bagAnnotation, position)
							&& !addAndCheckDuplicateAnnotation(map, bagAnnotation, position))
						annotations.add(bagAnnotation);

				}
			} else {
				if (includeAnnotation(annotation, position)
						&& !addAndCheckDuplicateAnnotation(map, annotation, position)) {
					annotations.add(annotation);
				}
			}
		}
		return annotations;
	}

    /**
     * Check preferences, etc., to determine whether this annotation is actually showing.
     * (Don't want to show a hover for a non-visible annotation.)
     * @param annotation
     * @param position
     * @return
     */
    private static boolean includeAnnotation(Annotation annotation, Position position) {
        return !sAnnotationTypesToFilter.contains(annotation.getType());
    }

    /**
     * @see IVerticalRulerHover#getHoverInfo(ISourceViewer, int)
     */
    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
        List<Annotation> annotations = getAnnotationsForLine(sourceViewer, lineNumber);

        return formatAnnotationList(annotations);
    }
}

/**
 * Interface that represents a single-argument predicate taking a textual Position.
 * Used by AnnotationUtils to detect annotations associated with a particular range
 * or location in source text.
 * @author rfuhrer
 */
interface IPositionPredicate {
    boolean matchPosition(Position p);
}

