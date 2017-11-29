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

import static org.eclipse.ceylon.ide.eclipse.code.editor.CeylonTaskUtil.addTaskAnnotation;
import static org.eclipse.ceylon.ide.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getIdentifyingNode;

import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;

import org.eclipse.ceylon.compiler.typechecker.parser.CeylonLexer;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.code.parse.TreeLifecycleListener;
import org.eclipse.ceylon.ide.common.util.types_;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;

/**
 * Responsible for adding refinement annotations to the 
 * vertical ruler, and updating the highlight range in 
 * the vertical ruler.
 *
 */
public class RefinementAnnotationCreator 
        implements TreeLifecycleListener {
    
    public static final String TODO_ANNOTATION_TYPE = 
            PLUGIN_ID + ".todo";
//    public static final String RUN_ANNOTATION_TYPE = 
//            PLUGIN_ID + ".run";


    private CeylonEditor editor;
    
    public RefinementAnnotationCreator(CeylonEditor editor) {
        this.editor = editor;
    }

    @Override
    public Stage getStage() {
        return TYPE_ANALYSIS;
    }
    
    @Override
    public void update(
            CeylonParseController parseController, 
            IProgressMonitor monitor) {
        if (editor.isBackgroundParsingPaused() || 
                monitor.isCanceled()) {
            return;
        }
        
        final CeylonParseController cpc = parseController;
        if (cpc.getStage().ordinal() >= getStage().ordinal()) {
            final Tree.CompilationUnit rootNode = 
                    cpc.getLastCompilationUnit();
            List<CommonToken> tokens = cpc.getTokens();
            if (rootNode == null) {
                return;
            }
            
            IEditorInput editorInput = 
                    editor.getEditorInput();
            final IAnnotationModel model =
                    editor.getDocumentProvider()
                        .getAnnotationModel(editorInput);
            if (model==null) {
                return;
            }
            
            for (Iterator<Annotation> iter = 
                        model.getAnnotationIterator(); 
                    iter.hasNext();) {
                Annotation a = iter.next();
                if (a instanceof RefinementAnnotation 
//                        || a.getType().equals(RUN_ANNOTATION_TYPE)
                        || a.getType().equals(TODO_ANNOTATION_TYPE)) {
                    model.removeAnnotation(a);
                }
            }
            
            new Visitor() {
                @Override
                public void visit(Tree.Declaration that) {
                    super.visit(that);
                    addRefinementAnnotation(
                            model, that, 
                            that.getIdentifier(), 
                            that.getDeclarationModel());
                }
                @Override
                public void visit(Tree.SpecifierStatement that) {
                    super.visit(that);
                    if (that.getRefinement()) {
                        addRefinementAnnotation(
                                model, that, 
                                that.getBaseMemberExpression(), 
                                that.getDeclaration());
                    }
                }
                //Disable until we have a good icon
                /*@Override
                public void visit(Tree.AnyMethod that) {
                    super.visit(that);
                    if (!that.getParameterLists().isEmpty()) {
                        addRunAnnotation(model, that, 
                                that.getDeclarationModel(), 
                                that.getParameterLists()
                                    .get(0));
                    }
                }
                @Override
                public void visit(Tree.ClassDefinition that) {
                    super.visit(that);
                    addRunAnnotation(model, that, 
                            that.getDeclarationModel(), 
                            that.getParameterList());
                }*/
            }.visit(rootNode);
            
            for (CommonToken token : tokens) {
                int type = token.getType();
                if (type == CeylonLexer.LINE_COMMENT || 
                    type == CeylonLexer.MULTI_COMMENT) {
                    addTaskAnnotation(token, model);
                }
            }
        }
    }
    
    //Disable until we have a good icon
    /*private void addRunAnnotation(IAnnotationModel model, 
            Tree.Declaration that, Declaration dec,
            Tree.ParameterList parameterList) {
        if (dec!=null 
                && dec.isShared() 
                && dec.isToplevel()
                && parameterList!=null //TODO: not quite right, what about default constructors?
                && parameterList.getParameters()
                    .isEmpty()) {
            Annotation ra = 
                    new Annotation(RUN_ANNOTATION_TYPE, 
                            false, "Runnable function");
            Node identifyingNode = getIdentifyingNode(that);
            model.addAnnotation(ra, 
                    new Position(
                            identifyingNode.getStartIndex(), 
                            identifyingNode.getDistance()));
        }
    }*/
    
    private void addRefinementAnnotation(
            IAnnotationModel model, 
            Tree.StatementOrArgument that, 
            Node node, Declaration dec) {
        if (dec!=null && dec.isActual()) {
            Declaration refined = 
                    types_.get_()
                        .getRefinedDeclaration(dec);
            if (refined!=null) {
                Declaration container = 
                        (Declaration) 
                            refined.getContainer();
                Unit unit = that.getUnit();
                String description = 
                        "refines " + 
                        container.getName(unit) + 
                        "." + refined.getName(unit);
                int line = node.getToken().getLine();
                RefinementAnnotation ra = 
                        new RefinementAnnotation(description,  
                                refined, line);
                Node identifyingNode = getIdentifyingNode(that);
                model.addAnnotation(ra, 
                        new Position(
                                identifyingNode.getStartIndex(), 
                                identifyingNode.getDistance()));
            }
        }
    }
    
}
