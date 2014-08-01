package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.AdditionalAnnotationCreator.TODO_ANNOTATION_TYPE;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation.isParseAnnotation;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.MODULE_DEPENDENCY_PROBLEM_MARKER_ID;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.TASK_MARKER_ID;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;

class MarkerAnnotationUpdater implements TreeLifecycleListener {
    
    private CeylonEditor editor;
    
    public MarkerAnnotationUpdater(CeylonEditor editor) {
        this.editor = editor;
    }
    
    public Stage getStage() {
        return TYPE_ANALYSIS;
    }
    
    /**
     * Remove any Marker annotations that don't correspond to existing problems
     * according to the most recent tree / typecheck
     */
    public void update(CeylonParseController parseController, IProgressMonitor monitor) {
        if (parseController.getStage().ordinal() >= getStage().ordinal()) {
            IAnnotationModel model = editor.getDocumentProvider()
                    .getAnnotationModel(editor.getEditorInput());
            for (@SuppressWarnings("unchecked")
            Iterator<Annotation> iter = model.getAnnotationIterator(); 
                    iter.hasNext();) {
                Annotation ann = iter.next();
                if (ann instanceof MarkerAnnotation) {
                    IMarker marker = ((MarkerAnnotation) ann).getMarker();
                    try {
                        Integer markerStart = null;
                        Integer markerEnd = null;
                        boolean isProblemMarker = marker.getType().equals(PROBLEM_MARKER_ID);
                        boolean isModuleDependencyMarker = marker.getType().equals(MODULE_DEPENDENCY_PROBLEM_MARKER_ID);
                        boolean isTaskMarker = marker.getType().equals(TASK_MARKER_ID);
                        markerStart = (Integer) marker.getAttribute(IMarker.CHAR_START);
                        markerEnd = (Integer) marker.getAttribute(IMarker.CHAR_END);
                        if (markerStart==null||markerEnd==null) continue;
                        if ((isProblemMarker && !isModuleDependencyMarker) || isTaskMarker) {
                            boolean found = false;
                            for (@SuppressWarnings("unchecked")
                            Iterator<Annotation> iter2 = model.getAnnotationIterator(); 
                                    iter2.hasNext();) {
                                Annotation ann2 = iter2.next();
                                if (isProblemMarker && isParseAnnotation(ann2) ||
                                    isTaskMarker && ann2.getType().equals(TODO_ANNOTATION_TYPE)) {
                                    Position position = model.getPosition(ann2);
                                    if (markerStart.intValue()==position.offset &&
                                        markerEnd.intValue()==position.offset+position.length) {
                                        found=true;
                                        break;
                                    }
                                }
                            }
                            if (!found) {
                                model.removeAnnotation(ann);
                            }
                        }
                    }
                    catch (CoreException e) {
                        model.removeAnnotation(ann);
                    }
                }
            }
        }
    }
}