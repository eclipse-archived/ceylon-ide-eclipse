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

import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.PARSE_ANNOTATION_TYPE;
import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.PARSE_ANNOTATION_TYPE_ERROR;
import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.PARSE_ANNOTATION_TYPE_INFO;
import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.PARSE_ANNOTATION_TYPE_WARNING;
import static com.redhat.ceylon.eclipse.code.editor.CeylonEditor.isParseAnnotation;
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

import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.RecognitionError;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ClassBody;
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
    private final List<CeylonInitializerAnnotation> initializerAnnotations = new LinkedList<CeylonInitializerAnnotation>();

    public AnnotationCreator(CeylonEditor textEditor) {
        editor= textEditor;
    }

    @Override
    public void handleMessage(int startOffset, int endOffset,
            int startCol, int startLine, Message error) {
        messages.add(new PositionedMessage(error.getMessage(), 
        		new Position(startOffset, endOffset-startOffset+1), 
        		getSeverity(error, warnForErrors), 
        		error.getCode(), error instanceof RecognitionError,
        		error.getLine()));
    }
    
    @Override
    public void visit(Tree.ClassDefinition classDefinition) {
        String name = "class " + classDefinition.getDeclarationModel().getName();
        ClassBody body = classDefinition.getClassBody();
        createInitializerAnnotation(name, body);
        super.visit(classDefinition);
    }

    @Override
    public void visit(Tree.ObjectDefinition objectDefinition) {
        String name = "object " + objectDefinition.getDeclarationModel().getName();
        ClassBody body = objectDefinition.getClassBody();
        createInitializerAnnotation(name, body);
        super.visit(objectDefinition);
    }
    
    private void createInitializerAnnotation(String name, ClassBody body) {
        if (name != null && body != null) {
            Tree.Statement les = getLastExecutableStatement(body);
            if (les != null) {
                int startIndex = body.getStartIndex() + 2;
                int stopIndex = les.getStopIndex();

                Position initializerPosition = new Position(startIndex, stopIndex - startIndex + 1);
                CeylonInitializerAnnotation initializerAnnotation = new CeylonInitializerAnnotation(name, initializerPosition);

                initializerAnnotations.add(initializerAnnotation);
            }
        }
    }
    
    public void clearMessages() {
    	messages.clear();
    	initializerAnnotations.clear();
    }

    public void updateAnnotations() {
        IDocumentProvider docProvider= editor.getDocumentProvider();
        if (docProvider!=null) {
            IAnnotationModel model= docProvider.getAnnotationModel(editor.getEditorInput());
            if (model instanceof IAnnotationModelExtension) {
                IAnnotationModelExtension modelExt= (IAnnotationModelExtension) model;
                Annotation[] oldAnnotations= annotations.toArray(new Annotation[annotations.size()]);
                Map<Annotation,Position> newAnnotations= new HashMap<Annotation,Position>(messages.size());
                for (PositionedMessage pm: messages) {
                	if (!suppressAnnotation(pm)) {
                		Annotation a= createAnnotation(pm);
                		newAnnotations.put(a, pm.pos);
                		annotations.add(a);
                	}
                }
                for (CeylonInitializerAnnotation initializerAnnotation : initializerAnnotations) {
                    newAnnotations.put(initializerAnnotation, initializerAnnotation.getPosition());
                    annotations.add(initializerAnnotation);
                }
                modelExt.replaceAnnotations(oldAnnotations, newAnnotations);
            } 
            else if (model != null) { // model could be null if, e.g., we're directly browsing a file version in a src repo
                for (Iterator i= model.getAnnotationIterator(); i.hasNext(); ) {
                    Annotation a= (Annotation) i.next();
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
                for (CeylonInitializerAnnotation initializerAnnotation : initializerAnnotations) {
                    model.addAnnotation(initializerAnnotation, initializerAnnotation.getPosition());
                    annotations.add(initializerAnnotation);
                }
            }
        }
        messages.clear();
        initializerAnnotations.clear();
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
        		pm.message, editor, pm.code, pm.severity);
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

    /*private void removeAnnotations() {
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
        fAnnotations.clear();
    }*/
    
    
    // TODO copied from com.redhat.ceylon.compiler.typechecker.analyzer.Util, we should make it public ?
    private static Tree.Statement getLastExecutableStatement(Tree.ClassBody that) {
        List<Tree.Statement> statements = that.getStatements();
        for (int i=statements.size()-1; i>=0; i--) {
            Tree.Statement s = statements.get(i);
            if (s instanceof Tree.SpecifierStatement) {
                //shortcut refinement statements with => aren't really "executable"
                Tree.SpecifierStatement ss = (Tree.SpecifierStatement) s;
                if (!(ss.getSpecifierExpression() instanceof Tree.LazySpecifierExpression) || 
                        !ss.getRefinement()) {
                    return s;
                }
            }
            else if (s instanceof Tree.ExecutableStatement) {
                return s;
            }
            else {
                if (s instanceof Tree.AttributeDeclaration) {
                    Tree.SpecifierOrInitializerExpression sie = ((Tree.AttributeDeclaration) s).getSpecifierOrInitializerExpression();
                    if (sie!=null && !(sie instanceof Tree.LazySpecifierExpression)) {
                        return s;
                    }
                }
                if (s instanceof Tree.ObjectDefinition) {
                    Tree.ObjectDefinition o = (Tree.ObjectDefinition) s;
                    if (o.getExtendedType()!=null) {
                        ProducedType et = o.getExtendedType().getType().getTypeModel();
                        Unit unit = that.getUnit();
                        if (et!=null 
                                && !et.getDeclaration().equals(unit.getObjectDeclaration())
                                && !et.getDeclaration().equals(unit.getBasicDeclaration())) {
                            return s;
                        }
                    }
                    if (o.getClassBody()!=null) {
                        if (getLastExecutableStatement(o.getClassBody())!=null) {
                            return s;
                        }
                    }
                }
            }
        }
        return null;
    }
    
}

