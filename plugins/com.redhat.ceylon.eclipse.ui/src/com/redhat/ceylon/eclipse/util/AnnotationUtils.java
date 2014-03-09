package com.redhat.ceylon.eclipse.util;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.editor.AdditionalAnnotationCreator.TODO_ANNOTATION_TYPE;
import static com.redhat.ceylon.eclipse.code.editor.MarkOccurrencesAction.ASSIGNMENT_ANNOTATION;
import static com.redhat.ceylon.eclipse.code.editor.MarkOccurrencesAction.OCCURRENCE_ANNOTATION;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.addPageEpilog;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.convertToHTMLContent;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.convertTopLevelFont;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.insertPageProlog;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static org.eclipse.core.resources.IMarker.SEVERITY_ERROR;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;
import static org.eclipse.jface.resource.JFaceResources.getFontRegistry;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.projection.AnnotationBag;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation;
import com.redhat.ceylon.eclipse.code.editor.CeylonInitializerAnnotation;
import com.redhat.ceylon.eclipse.code.editor.RefinementAnnotation;
import com.redhat.ceylon.eclipse.code.html.HTML;
import com.redhat.ceylon.eclipse.core.builder.MarkerCreator;

public class AnnotationUtils {
    
    public static final String SEARCH_ANNOTATION_TYPE = 
            NewSearchUI.PLUGIN_ID + ".results";

    private static Set<String> sAnnotationTypesToFilter = 
            new HashSet<String>();

    static {
        String prefix = "org.eclipse.ui.workbench.texteditor.";
        sAnnotationTypesToFilter.add(prefix + "quickdiffUnchanged");
        sAnnotationTypesToFilter.add(prefix + "quickdiffChange");
        sAnnotationTypesToFilter.add(prefix + "quickdiffAddition");
        sAnnotationTypesToFilter.add(prefix + "quickdiffDeletion");
        sAnnotationTypesToFilter.add("org.eclipse.debug.core.breakpoint");
        sAnnotationTypesToFilter.add(OCCURRENCE_ANNOTATION);
        sAnnotationTypesToFilter.add(ASSIGNMENT_ANNOTATION);
        sAnnotationTypesToFilter.add(ProjectionAnnotation.TYPE);
    }

    /**
     * @return a nicely-formatted plain text string for the given set of annotations
     */
    public static String formatAnnotationList(List<Annotation> annotations) {
        if (annotations == null || annotations.isEmpty()) {
            return null;
        }
        else {
            if (annotations.size()==1) {
                return formatSingleMessage(annotations.get(0));
            }
            else {
                return formatMultipleMessages(annotations);
            }
        }
    }

    /**
     * @return true, if the given Annotation and Position are redundant, 
     * given the annotation information in the given Map
     */
    public static boolean addAndCheckDuplicateAnnotation(Map<Integer, 
            List<Object>> map, Annotation annotation, Position position) {
        List<Object> annotationsAtPosition;
        if (!map.containsKey(position.offset)) {
            annotationsAtPosition = new ArrayList<Object>();
            map.put(position.offset, annotationsAtPosition);
        }
        else {
            annotationsAtPosition = map.get(position.offset);
        }

        // TODO this should call out to a language extension point first 
        //      to see if the language can resolve duplicates
        
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
        } 
        else if (!annotationsAtPosition.contains(errorCode)) {
            annotationsAtPosition.add(errorCode);
            return false;    
        }

        return true;
    }

    /**
     * @return the list of Annotations that reside at the given 
     * line for the given ISourceViewer
     */
    public static List<Annotation> getAnnotationsForLine(final ISourceViewer viewer, 
            final int line) {
        return getAnnotations(viewer, new IPositionPredicate() {
            IDocument document = viewer.getDocument();
            public boolean matchPosition(Position p) {
                return positionIsAtLine(p, document, line);
            }
        });
    }

    /**
     * @return the list of Annotations that reside at the given 
     * offset for the given ISourceViewer
     */
    public static List<Annotation> getAnnotationsForOffset(ISourceViewer viewer, 
            final int offset) {
        return getAnnotations(viewer, new IPositionPredicate() {
            public boolean matchPosition(Position p) {
                return offset >= p.offset && 
                        offset < p.offset + p.length;
            }
        });
    }

    /**
     * @return true, if the given Position resides at the given 
     * line of the given IDocument
     */
    public static boolean positionIsAtLine(Position position, 
            IDocument document, int line) {
        int offset = position.getOffset();
        int length = position.getLength();
        if (offset > -1 && length > -1) {
            try {
                int startLine= document.getLineOfOffset(offset);
                int endLine = document.getLineOfOffset(offset+length);
                return line >= startLine && line <= endLine;
            } 
            catch (BadLocationException x) {}
        }
        return false;
    }
    
    /**
     * @return the list of Annotations on the given ISourceViewer 
     * that satisfy the given IPositionPredicate and that are worth 
     * showing to the user as text (e.g., ignoring debugger breakpoint 
     * annotations and source folding annotations)
     */
    public static List<Annotation> getAnnotations(ISourceViewer viewer, 
            IPositionPredicate posPred) {
        IAnnotationModel model = viewer.getAnnotationModel();
        if (model == null) {
            return null;
        }
        List<Annotation> annotations = new ArrayList<Annotation>();
        Iterator<?> iterator = model.getAnnotationIterator();

        Map<Integer,List<Object>> map = new HashMap<Integer,List<Object>>();

        while (iterator.hasNext()) {
            Annotation annotation = (Annotation) iterator.next();
            Position position = model.getPosition(annotation);
            
            if (annotation instanceof MarkerAnnotation) {
                try {
                    if (((MarkerAnnotation) annotation).getMarker().getType()
                            .equals(PROBLEM_MARKER_ID)) {
                        continue;
                    }
                } 
                catch (CoreException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            if (position == null || 
                    !posPred.matchPosition(position)) {
                continue;
            }
            if (annotation instanceof AnnotationBag) {
                AnnotationBag bag = (AnnotationBag) annotation;
                for (Iterator<?> e = bag.iterator(); e.hasNext(); ) {
                    Annotation bagAnnotation = (Annotation) e.next();
                    position = model.getPosition(bagAnnotation);
                    if (position != null && 
                            includeAnnotation(bagAnnotation, position) && 
                            !addAndCheckDuplicateAnnotation(map, bagAnnotation, position))
                        annotations.add(bagAnnotation);

                }
            }
            else {
                if (includeAnnotation(annotation, position) && 
                        !addAndCheckDuplicateAnnotation(map, annotation, position)) {
                    annotations.add(annotation);
                }
            }
        }
        return annotations;
    }

    /**
     * Check preferences, etc., to determine whether this 
     * annotation is actually showing. (Don't want to show a 
     * hover for a non-visible annotation.)
     */
    private static boolean includeAnnotation(Annotation annotation, 
            Position position) {
        return !sAnnotationTypesToFilter.contains(annotation.getType());
    }
    
//    public String getHoverInfo(ISourceViewer sourceViewer, int lineNumber) {
//        return formatAnnotationList(getAnnotationsForLine(sourceViewer, lineNumber));
//    }

    public static void addMessageImageAndLabel(Annotation message,
            StringBuilder buffer) {
        URL icon = null;
        String text = null;
        if (message instanceof CeylonAnnotation) {
            text = convertToHTMLContent(message.getText());
            icon = getProblemIcon(((CeylonAnnotation) message).getSeverity());
        }
        else if (message instanceof CeylonInitializerAnnotation) {
            text = message.getText();
            icon = HTML.fileUrl("information.gif");
        }
        else if (message instanceof RefinementAnnotation) {
            Declaration dec = ((RefinementAnnotation) message).getDeclaration();
            icon = dec.isFormal() ? 
                    HTML.fileUrl("implm_co.gif") : HTML.fileUrl("over_co.gif");
            text = "refines&nbsp;&nbsp;<tt>" + 
                    convertToHTMLContent(getDescriptionFor(dec)) + 
                    "</tt>&nbsp;&nbsp;declared by&nbsp;&nbsp;<tt><b>" + 
                    ((TypeDeclaration) dec.getContainer()).getName() + 
                    "</b></tt>";
        }
        else if (message instanceof MarkerAnnotation) {
            try {
                Integer sev = (Integer)((MarkerAnnotation) message).getMarker()
                        .getAttribute(IMarker.SEVERITY);
                icon = getProblemIcon(sev);
                String msg = (String)((MarkerAnnotation) message).getMarker()
                        .getAttribute(IMarker.MESSAGE);
                text = "[Backend error] " + 
                        convertToHTMLContent(msg).replace("\n", "<br/>");
            } 
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
        else if (message!=null) {
            if (SEARCH_ANNOTATION_TYPE.equals(message.getType())) {
                text = "<b>Search result</b>";
                icon = HTML.fileUrl("find_obj.gif");
            }
            else if (TODO_ANNOTATION_TYPE.equals(message.getType())) {
                text = "<b>Task</b><p>" + message.getText() + "</p>";
                icon = HTML.fileUrl("tasks_tsk.gif");
            }
        }
        if (icon!=null) {
            HTML.addImageAndLabel(buffer, null, icon.toExternalForm(), 
                    16, 16, text, 20, 2);
        }
    }

    public static URL getProblemIcon(Integer severity) {
        if (severity==null) {
            return null;
        }
        if (severity.intValue()==SEVERITY_ERROR) {
            return HTML.fileUrl("error_obj.gif");
        }
        else if (severity.intValue()==SEVERITY_WARNING) {
            return HTML.fileUrl("warning_obj.gif");
        }
        else {
            return null;
        }
    }

    /**
     * Formats a message as HTML text.
     */
    public static String formatSingleMessage(Annotation message) {
        StringBuilder buffer= new StringBuilder();
        insertPageProlog(buffer, 0, getStyleSheet());
        addMessageImageAndLabel(message, buffer);
        addPageEpilog(buffer);
        return buffer.toString();
    }

    /**
     * Formats several messages as HTML text.
     */
    public static String formatMultipleMessages(List<Annotation> messages) {
        StringBuilder buffer = new StringBuilder();
        insertPageProlog(buffer, 0, getStyleSheet());
        HTML.addImageAndLabel(buffer, null, 
                HTML.fileUrl("errorwarning_tab.gif").toExternalForm(),
                16, 16, "Multiple messages at this line:", 20, 2);
        buffer.append("<hr/>");
        for (Annotation message: messages) {
            addMessageImageAndLabel(message, buffer);
        }
        addPageEpilog(buffer);
        return buffer.toString();
    }
    
    private static String fgStyleSheet;

    public static String getStyleSheet() {
        if (fgStyleSheet == null) {
            fgStyleSheet = HTML.loadStyleSheet();
        }
        //Color c = CeylonTokenColorer.getCurrentThemeColor("messageHover");
        //String color = toHexString(c.getRed()) + toHexString(c.getGreen()) + toHexString(c.getBlue());
        String css= fgStyleSheet + "body { padding: 5px; } hr { padding: 2px; border:0; }"; //+ "body { background-color: #" + color + " }";
        if (css!=null) {
            css = convertTopLevelFont(css, getFontRegistry()
                    .getFontData(APPEARANCE_JAVADOC_FONT)[0]);
        }
        return css;
    }

}

/**
 * Interface that represents a single-argument predicate taking 
 * a textual Position. Used by AnnotationUtils to detect annotations 
 * associated with a particular range or location in source text.
 * @author rfuhrer
 */
interface IPositionPredicate {
    boolean matchPosition(Position p);
}

