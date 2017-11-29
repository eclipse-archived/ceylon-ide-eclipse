/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.ASTRING_LITERAL;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.AVERBATIM_STRING;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.LINE_COMMENT;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.MULTI_COMMENT;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.STRING_LITERAL;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.VERBATIM_STRING;
import static org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer.WS;
import static org.eclipse.ceylon.ide.eclipse.code.parse.TreeLifecycleListener.Stage.SYNTACTIC_ANALYSIS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_FOLD_COMMENTS;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.AUTO_FOLD_IMPORTS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.code.parse.TreeLifecycleListener;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class ProjectionAnnotationManager 
        implements TreeLifecycleListener, 
                   IProjectionListener {
    
    private static final Annotation[] NO_ANNOTATIONS = 
            new Annotation[0];

    private CeylonEditor editor;

    private boolean firstTime = true;
    
    private final HashMap<Annotation,Position> newAnnotations = 
            new HashMap<Annotation, Position>();
    private HashMap<Annotation,Position> oldAnnotations = 
            new HashMap<Annotation, Position>();
    
    public ProjectionAnnotationManager(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public void projectionEnabled() {
        reset();
//        editor.scheduleParsing();
        CeylonParseController pc = 
                editor.getParseController();
        if (pc.getDocument() 
                == editor.getCeylonSourceViewer()
                        .getDocument()) {
            update(pc, new NullProgressMonitor());
        }
    }
    
    @Override
    public void projectionDisabled() {}

    public Stage getStage() {
        return SYNTACTIC_ANALYSIS;
    }

    public void update(CeylonParseController parseController, 
            IProgressMonitor monitor) {
        if (parseController.getStage().ordinal() 
                >= getStage().ordinal()) {
            Tree.CompilationUnit rn = 
                    parseController.getParsedRootNode();
            if (rn!=null) { // can be null if file is outside workspace
                try {
                    updateFoldingStructure(rn, 
                            parseController.getTokens());
                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void reset() {
        firstTime=true;
        oldAnnotations.clear();
    }

    /**
     * Make a folding annotation that corresponds to the extent of text
     * represented by a given program entity. Usually, this will be an
     * AST node, but it can be anything for which the language's
     * ISourcePositionLocator can produce an offset/end offset.
     * 
     * @param n an Object representing a program entity
     */
//    public void makeAnnotation(Object n) {
//        makeAnnotation(n, false);
//    }
    
    /**
     * Make a folding annotation that corresponds to the extent of text
     * represented by a given program entity. Usually, this will be an
     * AST node, but it can be anything for which the language's
     * ISourcePositionLocator can produce an offset/end offset.
     * 
     * @param n an Object representing a program entity
     */
//    public void makeAnnotation(Object n, boolean collapsed) {
//        makeAnnotation(getStartOffset(n), getLength(n), collapsed);
//    }

    /**
     * Make a folding annotation that corresponds to the given range of text.
     * 
     * @param start        The starting offset of the text range
     * @param len        The length of the text range
     */
    private ProjectionAnnotation makeAnnotation(
            int start, int len, int tokenType) {
        ProjectionAnnotation annotation = 
                new CeylonProjectionAnnotation(tokenType);
        newAnnotations.put(annotation, 
                new Position(start, len));
        return annotation;
    }
    
    protected int advanceToEndOfLine(int offset, int len) {
        IDocument doc = 
                editor.getCeylonSourceViewer()
                    .getDocument();
        try {
            int line = doc.getLineOfOffset(offset+len);
            while (offset+len<doc.getLength() && 
//                    Character.isWhitespace(doc.getChar(offset+len)) &&
                    doc.getLineOfOffset(offset+len)==line) {
                len++;
            }
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }
        return len;
    }
    
    /**
     * Update the folding structure for a source text, where the text and its
     * AST are represented by a given parse controller and the folding structure
     * is represented by annotations in a given annotation model.
     * 
     * This is the principal routine of the folding updater.
     * 
     * The implementation provided here makes use of a local class
     * FoldingUpdateStrategy, to which the task of updating the folding
     * structure is delegated.
     * 
     * updateFoldingStructure is synchronized because, at least on file opening,
     * it can be called more than once before the first invocation has completed.
     * This can lead to inconsistent calculations resulting in the absence of
     * folding annotations in newly opened files.
     * 
     * @param ast                    The AST for the source text
     * @param annotationModel        A structure of projection annotations that
     *                                represent the foldable elements in the source
     *                                text
     */
    public synchronized void updateFoldingStructure(
            Tree.CompilationUnit ast, 
            List<CommonToken> tokens) {
        try {
            
            ProjectionAnnotationModel annotationModel = 
                    editor.getCeylonSourceViewer()
                        .getProjectionAnnotationModel(); 
            if (ast==null||annotationModel==null) {
                // We can't create annotations without an AST
                return;
            }
        
            // But, since here we have the AST ...
            createAnnotations(ast, tokens);
            
            /*
            // Update the annotation model if there have been changes
            // but not otherwise (since update leads to redrawing of the    
            // source in the editor, which is likely to be unwelcome if
            // there haven't been any changes relevant to folding)
            boolean updateNeeded = false;
            if (firstTime) {
                // Should just be the first time through
                updateNeeded = true;
            } 
            else {
                // Check to see whether the current and previous annotations
                // differ in any significant way; if not, then there's no
                // reason to update the annotation model.
                // Note:  This test may be implemented in various ways that may
                // be more or less simple, efficient, correct, etc.  (The
                // default test provided below is simplistic although quick and
                // usually effective.)
                updateNeeded = differ(oldAnnotations, newAnnotations);
            }
            
            // Need to curtail calls to modifyAnnotations() because these lead to calls
            // to fireModelChanged(), which eventually lead to calls to updateFoldingStructure,
            // which lead back here, which would lead to another call to modifyAnnotations()
            // (unless those were curtailed)
            if (updateNeeded) {*/
                List<Annotation> deletions = 
                        new ArrayList<Annotation>
                            (oldAnnotations.size());
                for (Map.Entry<Annotation,Position> e: 
                        oldAnnotations.entrySet()) {
                    if (!newAnnotations.containsValue(e.getValue())) {
                        deletions.add(e.getKey());
                    }
                }
                Map<Annotation, Position> additions = 
                        new HashMap<Annotation,Position>
                            (newAnnotations.size());
                for (Map.Entry<Annotation,Position> e: 
                        newAnnotations.entrySet()) {
                    if (!oldAnnotations.containsValue(e.getValue())) {
                        additions.put(e.getKey(), e.getValue());
                    }
                }
                if (!deletions.isEmpty() || !additions.isEmpty()) {
                    annotationModel.modifyAnnotations(
                            deletions.toArray(NO_ANNOTATIONS), 
                            additions, null);
                }
                // Capture the latest set of annotations in a form that can be used the next
                // time that it is necessary to modify the annotations
                for (Annotation a: deletions) {
                    oldAnnotations.remove(a);
                }
                oldAnnotations.putAll(additions);
            //}

            newAnnotations.clear();        
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }    
    
    /**
     * Send a visitor to an AST representing a program in order to construct the
     * folding annotations.  Both the visitor type and the AST node type are language-
     * dependent, so this method is abstract.
     * 
     * @param newAnnotations    A map of annotations to text positions
     * @param annotations        A listing of the annotations in newAnnotations, that is,
     *                             a listing of keys to the map of text positions
     * @param ast                An Object that will be taken to represent an AST node
     */
    public void createAnnotations(Tree.CompilationUnit ast, 
            List<CommonToken> tokens) {
        final boolean autofoldImports;
        final boolean autofoldComments;
        if (firstTime) {
            IPreferenceStore store = 
                    CeylonPlugin.getPreferences();
            autofoldImports = 
                    store.getBoolean(AUTO_FOLD_IMPORTS);
            autofoldComments = 
                    store.getBoolean(AUTO_FOLD_COMMENTS);
            firstTime = false;
        }
        else {
            autofoldImports = false;
            autofoldComments = false;
        }
        for (int i=0; i<tokens.size(); i++) {
            CommonToken token = tokens.get(i);
            int type = token.getType();
            if (type==MULTI_COMMENT ||
                type==STRING_LITERAL ||
                type==ASTRING_LITERAL ||
                type==VERBATIM_STRING ||
                type==AVERBATIM_STRING) {
                if (isMultilineToken(token)) {
                    ProjectionAnnotation ann = 
                            makeAnnotation(token, token);
                    if (autofoldComments && ann!=null 
                            && type==MULTI_COMMENT) {
                        ann.markCollapsed();
                    }
                }
            }
            if (type==LINE_COMMENT) {
                CommonToken until = token;
                int j=i+1;
                CommonToken next = tokens.get(j);
                while (next.getType()==LINE_COMMENT ||
                        next.getType()==WS) {
                    if (next.getType()==LINE_COMMENT) {
                        until = next;
                        i = j;
                    }
                    next = tokens.get(++j);
                }
                ProjectionAnnotation ann = 
                        foldIfNecessary(token, until);
                if (ann!=null && autofoldComments) {
                    ann.markCollapsed();
                }
            }
        }
        Tree.CompilationUnit cu = 
                (Tree.CompilationUnit) ast;
        new Visitor() {
            @Override 
            public void visit(Tree.ImportList importList) {
                super.visit(importList);
                if (!importList.getImports().isEmpty()) {
                    ProjectionAnnotation ann = 
                            foldIfNecessary(importList);
                    if (autofoldImports && ann!=null) {
                        ann.markCollapsed();
                    }
                }
            }
            /*@Override 
            public void visit(Tree.Import that) {
                super.visit(that);
                foldIfNecessary(that);
            }*/
            @Override 
            public void visit(Tree.Body that) {
                super.visit(that);
                if (that.getToken()!=null) { //for "else if"
                    foldIfNecessary(that);
                }
            }
            @Override 
            public void visit(Tree.NamedArgumentList that) {
                super.visit(that);
                foldIfNecessary(that);
            }
            @Override 
            public void visit(Tree.ModuleDescriptor that) {
                super.visit(that);
                foldIfNecessary(that);
            }
        }.visit(cu);
    }

    private ProjectionAnnotation foldIfNecessary(Node node) {
        CommonToken token = 
                (CommonToken) node.getToken();
        CommonToken endToken = 
                (CommonToken) node.getEndToken();
        if (token!=null && endToken!=null &&
                endToken.getLine()-token.getLine()>0) {
            return makeAnnotation(token, endToken);
        }
        else {
            return null;
        }
    }
    
    private ProjectionAnnotation foldIfNecessary(
            CommonToken start, CommonToken end) {
        if (end.getLine()>start.getLine()) {
            return makeAnnotation(start, end);
        }
        else {
            return null;
        }
    }
    
    private boolean isMultilineToken(CommonToken token) {
        return token.getText().indexOf('\n')>0 ||
                token.getText().indexOf('\r')>0;
    }

    private ProjectionAnnotation makeAnnotation(
            CommonToken start, CommonToken end) {
        int offset = start.getStartIndex();
        int len = end.getStopIndex()-start.getStartIndex()+1;
        if (end.getType()!=CeylonLexer.LINE_COMMENT) {
            len = advanceToEndOfLine(offset, len);
        }
        return makeAnnotation(offset, len, start.getType());
    }
}