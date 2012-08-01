package com.redhat.ceylon.eclipse.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.internal.text.html.HTMLPrinter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.AnnotationBag;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.MarkOccurrencesAction;
import com.redhat.ceylon.eclipse.code.editor.RefinementAnnotation;
import com.redhat.ceylon.eclipse.code.hover.DocHover;
import com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer;
import com.redhat.ceylon.eclipse.core.builder.MarkerCreator;

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
        if (annotations!=null && !annotations.isEmpty()) {
        	if (annotations.size()==1) {
        		return AnnotationUtils.formatSingleMessage(annotations.get(0));
        	}
        	else {
        		return AnnotationUtils.formatMultipleMessages(annotations);
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
					.getAttribute(MarkerCreator.ERROR_CODE_KEY, -1);
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
			
			if (annotation instanceof MarkerAnnotation)
				continue;
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

	public static void addMessageImageAndLabel(Annotation message,
			StringBuffer buffer) {
		URL icon = null;
		String text = null;
	    if (message instanceof CeylonAnnotation) {
	    	text = HTMLPrinter.convertToHTMLContent(message.getText());
	    	int sev = ((CeylonAnnotation) message).getSeverity();
	    	if (sev==IStatus.ERROR) {
	    		icon = DocHover.fileUrl("error_obj.gif");
	    	}
	    	else if (sev==IStatus.WARNING) {
	    		icon = DocHover.fileUrl("warning_obj.gif");
	    	}
	    }
	    else if (message instanceof RefinementAnnotation) {
	    	Declaration dec = ((RefinementAnnotation) message).getDeclaration();
	    	icon = dec.isFormal() ? DocHover.fileUrl("implm_co.gif") : DocHover.fileUrl("over_co.gif");
			text = "refines&nbsp;&nbsp;<tt>" + HTMLPrinter.convertToHTMLContent(CeylonContentProposer.getDescriptionFor(dec))
					+ "</tt>&nbsp;&nbsp;declared by&nbsp;&nbsp;<tt><b>" + ((TypeDeclaration) dec.getContainer()).getName() + 
					"</b></tt>";
	    }
	    if (icon!=null) {
	    	DocHover.addImageAndLabel(buffer, null, icon.toExternalForm(), 16, 16, text, 20, 2);
	    }
	}

	/**
	 * Formats a message as HTML text.
	 */
	public static String formatSingleMessage(Annotation message) {
	    StringBuffer buffer= new StringBuffer();
	    HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
	    addMessageImageAndLabel(message, buffer);
	    HTMLPrinter.addPageEpilog(buffer);
	    return buffer.toString();
	}

	/**
	 * Formats several messages as HTML text.
	 */
	public static String formatMultipleMessages(List<Annotation> messages) {
	    StringBuffer buffer= new StringBuffer();
	    HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
	    DocHover.addImageAndLabel(buffer, null, DocHover.fileUrl("errorwarning_tab.gif").toExternalForm(),
	    		16, 16, "Multiple messages at this line:", 20, 2);
	    buffer.append("<hr/>");
	    for(Annotation message: messages) {
	    	addMessageImageAndLabel(message, buffer);
	    }
	    HTMLPrinter.addPageEpilog(buffer);
	    return buffer.toString();
	}
	
	private static String fgStyleSheet;

	public static String getStyleSheet() {
		if (fgStyleSheet == null)
			fgStyleSheet= DocHover.loadStyleSheet() ;
		//Color c = CeylonTokenColorer.getCurrentThemeColor("messageHover");
		//String color = toHexString(c.getRed()) + toHexString(c.getGreen()) + toHexString(c.getBlue());
		String css= fgStyleSheet; //+ "body { background-color: #" + color + " }";
		if (css != null) {
			FontData fontData= JFaceResources.getFontRegistry()
					.getFontData(PreferenceConstants.APPEARANCE_JAVADOC_FONT)[0];
			css= HTMLPrinter.convertTopLevelFont(css, fontData);
		}

		return css;
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

