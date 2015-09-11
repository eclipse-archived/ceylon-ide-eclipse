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

import static com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation.PARSE_ANNOTATION_TYPE;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation.PARSE_ANNOTATION_TYPE_ERROR;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation.PARSE_ANNOTATION_TYPE_INFO;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation.PARSE_ANNOTATION_TYPE_WARNING;
import static com.redhat.ceylon.eclipse.code.editor.CeylonAnnotation.isParseAnnotation;
import static org.eclipse.core.resources.IMarker.SEVERITY_ERROR;
import static org.eclipse.core.resources.IMarker.SEVERITY_INFO;
import static org.eclipse.core.resources.IMarker.SEVERITY_WARNING;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.ui.texteditor.IDocumentProvider;

import com.redhat.ceylon.compiler.typechecker.parser.RecognitionError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.eclipse.util.ErrorVisitor;

/**
 * An implementation of the IMessageHandler interface that creates editor annotations
 * directly from messages. Used for live parsing within a source editor (cf. building,
 * which uses the class MarkerCreator to create markers).
 * @author rmfuhrer
 */
public class AnnotationCreator extends ErrorVisitor {
    
    private static class PositionedMessage {
        public final String message;
        public final Position pos;
        public final int code;
        public final int severity;
        public final boolean syntaxError;
        public final int line;
        
        private PositionedMessage(String msg, Position pos, 
                int severity, int code, boolean syntaxError,
                int line) {
            this.message = msg;
            this.pos = pos;
            this.code = code;
            this.severity = severity;
            this.syntaxError = syntaxError;
            this.line = line;
        }

        @Override
        public String toString() {
            return pos.toString() + " - "+ message;
        }
    }
    
    private final CeylonEditor editor;
    private final List<PositionedMessage> messages= new LinkedList<PositionedMessage>();
    private final List<Annotation> annotations= new LinkedList<Annotation>();
    
    public AnnotationCreator(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public void handleMessage(int startOffset, int endOffset,
            int startCol, int startLine, Message error) {
        messages.add(new PositionedMessage(error.getMessage(), 
                new Position(startOffset, endOffset-startOffset), 
                getSeverity(error, warnForErrors), 
                error.getCode(), error instanceof RecognitionError,
                error.getLine()));
    }
        
    public void clearMessages() {
        messages.clear();
    }

    public void updateAnnotations() {
        IDocumentProvider docProvider = editor.getDocumentProvider();
        if (docProvider!=null) {
            IAnnotationModel model = docProvider.getAnnotationModel(editor.getEditorInput());
            if (model instanceof IAnnotationModelExtension) {
                IAnnotationModelExtension modelExt = (IAnnotationModelExtension) model;
                Annotation[] oldAnnotations = annotations.toArray(new Annotation[annotations.size()]);
                Map<Annotation,Position> newAnnotations = new HashMap<Annotation,Position>(messages.size());
                for (PositionedMessage pm: messages) {
                    if (!suppressAnnotation(pm)) {
                        Annotation a = createAnnotation(pm);
                        newAnnotations.put(a, pm.pos);
                        annotations.add(a);
                    }
                }
                modelExt.replaceAnnotations(oldAnnotations, newAnnotations);
            } 
            else if (model != null) { // model could be null if, e.g., we're directly browsing a file version in a src repo
                for (@SuppressWarnings("unchecked") 
                Iterator<Annotation> i = model.getAnnotationIterator(); 
                        i.hasNext();) {
                    Annotation a = i.next();
                    if (isParseAnnotation(a)) {
                        model.removeAnnotation(a);
                    }
                }
                for (PositionedMessage pm: messages) {
                    if (!suppressAnnotation(pm)) {
                        Annotation a= createAnnotation(pm);
                        model.addAnnotation(a, pm.pos);
                        annotations.add(a);
                    }
                }
            }
        }
        messages.clear();
    }

    public boolean suppressAnnotation(PositionedMessage pm) {
        boolean suppress = false;
        if (!pm.syntaxError && pm.line>=0) {
            for (PositionedMessage m: messages) {
                if (m.syntaxError && m.line==pm.line) {
                    suppress = true;
                    break;
                }
            }
        }
        return suppress;
    }

    private Annotation createAnnotation(PositionedMessage pm) {
        return new CeylonAnnotation(getAnnotationType(pm), 
                pm.message, pm.code, pm.severity);
    }

    private String getAnnotationType(PositionedMessage pm) {
        switch (pm.severity) {
        case SEVERITY_ERROR:
            return PARSE_ANNOTATION_TYPE_ERROR;
        case SEVERITY_WARNING:
            return PARSE_ANNOTATION_TYPE_WARNING;
        case SEVERITY_INFO:
            return PARSE_ANNOTATION_TYPE_INFO;
        default:
            return PARSE_ANNOTATION_TYPE;                
        }
    }
    
}