package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.compiler.typechecker.analyzer.Util.getLastExecutableStatement;
import static com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil.addTaskAnnotation;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findScope;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getLength;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getStartOffset;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;

/**
 * Responsible for adding refinement annotations to the 
 * vertical ruler, and updating the highlight range in 
 * the vertical ruler.
 *
 */
public class AdditionalAnnotationCreator implements TreeLifecycleListener {
    
    public static final String TODO_ANNOTATION_TYPE = PLUGIN_ID + ".todo";

    private CeylonEditor editor;
    CeylonInitializerAnnotation initializerAnnotation;
    
    public AdditionalAnnotationCreator(CeylonEditor editor) {
    	this.editor = editor;
        ((IPostSelectionProvider) editor.getSelectionProvider())
                .addPostSelectionChangedListener(new SelectionListener());
	}

	@Override
    public Stage getStage() {
        return TYPE_ANALYSIS;
    }
    
    @Override
    public void update(CeylonParseController parseController, IProgressMonitor monitor) {
        final CeylonParseController cpc = (CeylonParseController) parseController;
        if (cpc.getRootNode()==null) return;
        final IAnnotationModel model = editor.getDocumentProvider()
                .getAnnotationModel(editor.getEditorInput());
        for (@SuppressWarnings("unchecked")
        Iterator<Annotation> iter = model.getAnnotationIterator(); 
                iter.hasNext();) {
            Annotation a = iter.next();
            if (a instanceof RefinementAnnotation ||
            		a.getType().equals(TODO_ANNOTATION_TYPE)) {
                model.removeAnnotation(a);
            }
        }
        //model.addAnnotation(new DefaultRangeIndicator(), new Position(50, 100));
        new Visitor() {
            @Override
            public void visit(Tree.Declaration that) {
                super.visit(that);
                Declaration dec = that.getDeclarationModel();
                if (dec!=null) {
                    if (dec.isActual()) {
                        addRefinementAnnotation(model, that, dec);
                    }
                }
            }
        }.visit(cpc.getRootNode());
        
        for (CommonToken token : (List<CommonToken>) cpc.getTokens()) {
            int type = token.getType();
			if (type == CeylonLexer.LINE_COMMENT || 
            	type == CeylonLexer.MULTI_COMMENT) {
                addTaskAnnotation(token, model);
            }
        }
    }
    
    public static Declaration getRefinedDeclaration(Declaration dec) {
        if (!dec.isClassOrInterfaceMember()) {
            return null;
        }
        else {
            //TODO: this algorithm is a bit arbitrary
        	//first get the superclass of the declaring class 
            ClassOrInterface td = (ClassOrInterface) dec.getContainer();
			ClassOrInterface etd = td.getExtendedTypeDeclaration();
            if (etd==null) {
                return null;
            }
            else {
            	//then the declaration might refine a member 
            	//of a superclass or satisfied interface
            	List<Declaration> allRefined = td.getInheritedMembers(dec.getName());
            	if (allRefined.isEmpty()) {
            		//the declaration does not refine
            		//anything
            		return null;
            	}
            	else if (allRefined.size()==1) {
            		//the declaration refines exactly one
            		//member of a superclass or satisfied 
            		//interface 
            		return allRefined.get(0);
            	}
            	else {
            		//the declaration directly refines two 
            		//different supertype members
            		//look for a member declared or inherited by 
            		//the superclass
            		Declaration refined = etd.getMember(dec.getName(), null, false); //TODO: pass signature?
            		if (refined==null) {
            			//nothing; they are all declared by 
            			//satisfied interfaces :-(
            			//lets just return the topmost refined
            			//declaration, because at least we know
            			//it is something unique!                        	
            			refined = dec.getRefinedDeclaration();
            			if (refined!=null && refined.equals(dec)) {
            				refined = null;
            			}
            		}
            		return refined;
            	}
            }
        }
    }
    
    private void addRefinementAnnotation(IAnnotationModel model, 
    		Tree.Declaration that, Declaration dec) {
        Declaration refined = getRefinedDeclaration(dec);
        if (refined!=null) {
            RefinementAnnotation ra = new RefinementAnnotation(null, refined, 
                    that.getIdentifier().getToken().getLine());
            model.addAnnotation(ra, new Position(getStartOffset(that), 
            		getLength(that)+1));
        }
    }
    
    /**
     * Updates the highlighted range in the vertical ruler
     * (the blue bar indicating the current containing
     * declaration).
     */
    class SelectionListener implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
        	final CeylonParseController cpc = editor.getParseController();
        	if (cpc.getRootNode()==null) return;
        	Node node = findScope(cpc.getRootNode(), (ITextSelection) event.getSelection());
        	if (node!=null) {
        		editor.setHighlightRange(node.getStartIndex(), 
        				node.getStopIndex()-node.getStartIndex()+1, false);
        	}
        	else {
        		editor.resetHighlightRange();
        	}
            IAnnotationModel model= editor.getDocumentProvider()
                    .getAnnotationModel(editor.getEditorInput());
            if (model!=null) {
                model.removeAnnotation(initializerAnnotation);
            }
            initializerAnnotation = null;
            if (node!=null) {
                node.visit(new InitializerVisitor());
                if (initializerAnnotation!=null) {
                    model.addAnnotation(initializerAnnotation, 
                            initializerAnnotation.getInitializerPosition());
                }
            }
        }
    }
    
    class InitializerVisitor extends Visitor {
        @Override
        public void visit(Tree.ClassDefinition that) {
            if (that.getClassBody()==null||that.getIdentifier()==null) return;
            createAnnotation(that, that.getClassBody(), that.getIdentifier().getText());
        }
        @Override
        public void visit(Tree.ObjectDefinition that) {
            if (that.getClassBody()==null||that.getIdentifier()==null) return;
            createAnnotation(that, that.getClassBody(), that.getIdentifier().getText());
        }
        private void createAnnotation(Node that, Tree.ClassBody body, String name) {
//          int offset = editor.getSelection().getOffset();
//          if (offset>that.getStartIndex()&&offset<that.getStopIndex()) {
            Tree.Statement les = getLastExecutableStatement(body);
            if (les != null) {
                int startIndex = body.getStartIndex() + 2;
                int stopIndex = les.getStopIndex();
                Position initializerPosition = new Position(startIndex, stopIndex - startIndex + 1);
                initializerAnnotation = new CeylonInitializerAnnotation(name, initializerPosition, 1);
            }
//          }
        }
    }
    
}
