package com.redhat.ceylon.eclipse.code.editor;

/*******************************************************************************
* Copyright (c) 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*    Robert Fuhrer (rfuhrer@watson.ibm.com) - initial API and implementation
*******************************************************************************/

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.redhat.ceylon.eclipse.code.parse.IMessageHandler;

/**
 * An implementation of the IMessageHandler interface that creates editor annotations
 * directly from messages. Used for live parsing within a source editor (cf. building,
 * which uses the class MarkerCreator to create markers).
 * @author rmfuhrer
 */
public class AnnotationCreator implements IMessageHandler {
    private static class PositionedMessage {
        public final String message;
        /**
         * Textual position expressed as an offset and length
         */
        public final Position pos;
        /**
         * Additional attributes, if any, like severity and error code
         */
        public final Map<String, Object> attributes;

        public PositionedMessage(String msg, Position pos, Map<String, Object> attributes) {
            this.message= msg;
            this.pos= pos;
            this.attributes= attributes;
        }

        public PositionedMessage(String msg, Position pos) {
            this(msg, pos, null);
        }
        
        @Override
        public String toString() {
        	return pos.toString() + " - "+ message;
        }
    }

    private final CeylonEditor fEditor;
    private final List<PositionedMessage> fMessages= new LinkedList<PositionedMessage>();
    private final List<Annotation> fAnnotations= new LinkedList<Annotation>();

    public AnnotationCreator(CeylonEditor textEditor) {
        fEditor= textEditor;
    }

    public void clearMessages() {
        removeAnnotations();
        fMessages.clear();
    }

    public void startMessageGroup(String groupName) { }
    public void endMessageGroup() { }


    public void handleSimpleMessage(String msg, int startOffset, int endOffset,
            int startCol, int endCol, int startLine, int endLine) {
        Position pos= new Position(startOffset, endOffset - startOffset + 1);

        fMessages.add(new PositionedMessage(msg, pos));
    }

    public void handleSimpleMessage(String message, int startOffset, int endOffset,
            int startCol, int endCol, int startLine, int endLine,
            Map<String, Object> attributes) {
        Position pos= new Position(startOffset, endOffset - startOffset + 1);

        fMessages.add(new PositionedMessage(message, pos, attributes));
    }

    public void endMessages() {
        IDocumentProvider docProvider= fEditor.getDocumentProvider();

        if (docProvider != null) {
            IAnnotationModel model= docProvider.getAnnotationModel(fEditor.getEditorInput());
            if (model instanceof IAnnotationModelExtension) {
                IAnnotationModelExtension modelExt= (IAnnotationModelExtension) model;
                Annotation[] oldAnnotations= fAnnotations.toArray(new Annotation[fAnnotations.size()]);
                Map<Annotation, Position> newAnnotations= new HashMap<Annotation, Position>(fMessages.size());
                for(PositionedMessage pm: fMessages) {
                    Annotation anno= createAnnotation(pm);
                    newAnnotations.put(anno, pm.pos);
                    fAnnotations.add(anno);
                }
                modelExt.replaceAnnotations(oldAnnotations, newAnnotations);
            } 
            else if (model != null) { // model could be null if, e.g., we're directly browsing a file version in a src repo
                for(Iterator i= model.getAnnotationIterator(); i.hasNext(); ) {
                    Annotation a= (Annotation) i.next();
                    if (CeylonEditor.isParseAnnotation(a)) {
                        model.removeAnnotation(a);
                    }
                }
                for(PositionedMessage pm: fMessages) {
                    Annotation annotation= createAnnotation(pm);
                    model.addAnnotation(annotation, pm.pos);
                    fAnnotations.add(annotation);
                }
            }
            // System.out.println("Annotation model updated.");
        }
        fMessages.clear();
    }

    private Annotation createAnnotation(PositionedMessage pm) {
        return new CeylonAnnotation(getAnnotationType(pm), false, 
        		pm.message, fEditor, pm.attributes);
    }

    private String getAnnotationType(PositionedMessage pm) {
    	if (pm.attributes==null) {
    		return CeylonEditor.PARSE_ANNOTATION_TYPE;
    	}
        if (pm.attributes.containsKey(SEVERITY_KEY)) {
            int severity= (Integer) pm.attributes.get(SEVERITY_KEY);
            switch (severity) {
                case IStatus.ERROR:
                    return CeylonEditor.PARSE_ANNOTATION_TYPE_ERROR;
                case IStatus.WARNING:
                    return CeylonEditor.PARSE_ANNOTATION_TYPE_WARNING;
                case IStatus.INFO:
                    return CeylonEditor.PARSE_ANNOTATION_TYPE_INFO;
            }
        }
        return CeylonEditor.PARSE_ANNOTATION_TYPE;
    }

    private void removeAnnotations() {
        final IDocumentProvider docProvider= fEditor.getDocumentProvider();

        if (docProvider == null) {
            return;
        }

        IAnnotationModel model= docProvider.getAnnotationModel(fEditor.getEditorInput());

        if (model == null)
            return;

        if (model instanceof IAnnotationModelExtension) {
            IAnnotationModelExtension modelExt= (IAnnotationModelExtension) model;
            Annotation[] oldAnnotations= fAnnotations.toArray(new Annotation[fAnnotations.size()]);

            modelExt.replaceAnnotations(oldAnnotations, Collections.EMPTY_MAP);
        } else {
            for(Iterator i= model.getAnnotationIterator(); i.hasNext(); ) {
                Annotation a= (Annotation) i.next();

                if (CeylonEditor.isParseAnnotation(a)) {
                    model.removeAnnotation(a);
                }
            }
        }
//      System.out.println("Annotations removed.");
        fAnnotations.clear();
    }
}

