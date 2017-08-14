package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil.addTaskAnnotation;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingNode;

import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;

import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;
import com.redhat.ceylon.ide.common.util.types_;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Unit;

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
                if (a instanceof RefinementAnnotation ||
                        a.getType().equals(TODO_ANNOTATION_TYPE)) {
                    model.removeAnnotation(a);
                }
            }
            
            new Visitor() {
                @Override
                public void visit(Tree.Declaration that) {
                    super.visit(that);
                    Declaration dec = 
                            that.getDeclarationModel();
                    if (dec!=null) {
                        if (dec.isActual()) {
                            addRefinementAnnotation(
                                    model, that, 
                                    that.getIdentifier(), 
                                    dec);
                        }
                    }
                }
                @Override
                public void visit(Tree.SpecifierStatement that) {
                    super.visit(that);
                    if (that.getRefinement()) {
                        Declaration dec = 
                                that.getDeclaration();
                        if (dec!=null) {
                            if (dec.isActual()) {
                                addRefinementAnnotation(
                                        model, that, 
                                        that.getBaseMemberExpression(), 
                                        dec);
                            }
                        }
                    }
                }
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
    
    private void addRefinementAnnotation(
            IAnnotationModel model, 
            Tree.StatementOrArgument that, 
            Node node, Declaration dec) {
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
