package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation.isParseAnnotation;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.TASK_MARKER_ID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;

class MarkerAnnotationUpdater implements TreeLifecycleListener, IAnnotationModelListener {
    
    private CeylonEditor editor;
    
    private Map<IMarker, Annotation> markerParseAnnotations = new HashMap<IMarker, Annotation>();
    private Map<IMarker, MarkerAnnotation> markerMarkerAnnotations = new HashMap<IMarker, MarkerAnnotation>();
    
    public MarkerAnnotationUpdater(CeylonEditor editor) {
        this.editor = editor;
    }
    
    public Stage getStage() {
        return TYPE_ANALYSIS;
    }
    public void update(CeylonParseController parseController, IProgressMonitor monitor) {
        // SMS 25 Apr 2007
        // Since parsing has finished, check whether the marker annotations
        // are up-to-date with the most recent parse annotations.
        // Assuming that's often enough--i.e., don't refresh the marker
        // annotations after every update to the document annotation model
        // since there will be many of these, including possibly many that
        // don't relate to problem markers.
        refreshMarkerAnnotations(PROBLEM_MARKER_ID);
        refreshMarkerAnnotations(TASK_MARKER_ID);
    }
    
    /**
     * Refresh the marker annotations on the input document by removing any
     * that do not map to current parse annotations.  Do this for problem
     * markers, specifically; ignore other types of markers.
     * 
     * SMS 25 Apr 2007
     */
    public void refreshMarkerAnnotations(String problemMarkerType) {
        // Get current marker annotations
        IAnnotationModel model = editor.getDocumentProvider()
                .getAnnotationModel(editor.getEditorInput());
        List<MarkerAnnotation> markerAnnotations = new ArrayList<MarkerAnnotation>();
        for (Iterator iter = model.getAnnotationIterator(); iter.hasNext();) {
            Object ann = iter.next();
            if (ann instanceof MarkerAnnotation) {
                markerAnnotations.add((MarkerAnnotation) ann);
            } 
        }

        // For the current marker annotations, if any lacks a corresponding
        // parse annotation, delete the marker annotation from the document's
        // annotation model (but leave the marker on the underlying resource,
        // which presumably hasn't been changed, despite changes to the document)
        for (int i = 0; i < markerAnnotations.size(); i++) {
            MarkerAnnotation markerAnnotation = markerAnnotations.get(i);
            IMarker marker = markerAnnotation.getMarker();
            try {
                if (marker.getType().equals(problemMarkerType)) {
                    if (markerParseAnnotations.get(marker)==null) {
                        model.removeAnnotation(markerAnnotation);
                    }    
                }
            } 
            catch (CoreException e) {
                // If we get a core exception here, probably something is wrong with the
                // marker, and we probably don't want to keep any annotation that may be
                // associated with it (I don't think)
                model.removeAnnotation(markerAnnotation);
                continue;
            }
        }
    
    }

    public void modelChanged(IAnnotationModel model) {
        List<Annotation> currentParseAnnotations = new ArrayList<Annotation>();
        List<IMarker> currentMarkers = new ArrayList<IMarker>();

        markerParseAnnotations = new HashMap<IMarker,Annotation>();
        markerMarkerAnnotations = new HashMap<IMarker,MarkerAnnotation>();
        
        // Collect the current set of markers and parse annotations;
        // also maintain a map of markers to marker annotations (as    
        // there doesn't seem to be a way to get from a marker to the
        // annotations that may represent it)
        
        for (Iterator iter = model.getAnnotationIterator(); iter.hasNext();) {
            Object ann = iter.next();
            if (ann instanceof MarkerAnnotation) {
                IMarker marker = ((MarkerAnnotation)ann).getMarker();
                if (marker.exists()) {
                    currentMarkers.add(marker);
                }
                markerMarkerAnnotations.put(marker, (MarkerAnnotation) ann);
            } 
            else if (ann instanceof Annotation) {
                Annotation annotation = (Annotation) ann;
                if (isParseAnnotation(annotation)) {
                    currentParseAnnotations.add(annotation);
                }
            }
        }

        // Create a mapping between current markers and parse annotations
        for (int i = 0; i < currentMarkers.size(); i++) {
            IMarker marker = currentMarkers.get(i);
            Annotation annotation = findParseAnnotationForMarker(model, marker, 
                    currentParseAnnotations);
            if (annotation!=null) {
                markerParseAnnotations.put(marker, annotation);
            }
        }
    }

    public Annotation findParseAnnotationForMarker(IAnnotationModel model, IMarker marker, 
            List<Annotation> parseAnnotations) {
        Integer markerStartAttr = null;
        Integer markerEndAttr = null;
        try {
            // SMS 22 May 2007:  With markers created through the editor the CHAR_START
            // and CHAR_END attributes are null, giving rise to NPEs here.  Not sure
            // why this happens, but it seems to help down the line to trap the NPE.
            markerStartAttr = ((Integer) marker.getAttribute(IMarker.CHAR_START));
            markerEndAttr = ((Integer) marker.getAttribute(IMarker.CHAR_END));
            if (markerStartAttr == null || markerEndAttr == null) {
                return null;
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        int markerStart = markerStartAttr.intValue();
        int markerEnd = markerEndAttr.intValue();
        int markerLength = markerEnd - markerStart;
        for (int j = 0; j < parseAnnotations.size(); j++) {
            Annotation parseAnnotation = parseAnnotations.get(j);
            Position pos = model.getPosition(parseAnnotation);
            if (pos!=null) {
                int annotationStart = pos.offset;
                int annotationLength = pos.length;
                if (markerStart==annotationStart && 
                        markerLength==annotationLength) {
                    return parseAnnotation;
                }
            }
        }

        return null;
    }       
}