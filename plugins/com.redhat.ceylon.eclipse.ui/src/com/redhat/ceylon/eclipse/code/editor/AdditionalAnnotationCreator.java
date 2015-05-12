package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.compiler.typechecker.analyzer.Util.getLastExecutableStatement;
import static com.redhat.ceylon.model.typechecker.model.Util.getInterveningRefinements;
import static com.redhat.ceylon.model.typechecker.model.Util.getSignature;
import static com.redhat.ceylon.model.typechecker.model.Util.isAbstraction;
import static com.redhat.ceylon.eclipse.code.editor.CeylonTaskUtil.addTaskAnnotation;
import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.TYPE_ANALYSIS;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingLength;
import static com.redhat.ceylon.eclipse.util.Nodes.getIdentifyingStartOffset;

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

import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.ProducedType;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;
import com.redhat.ceylon.eclipse.util.Nodes;

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
        final CeylonParseController cpc = parseController;
        if (cpc.getStage().ordinal() >= getStage().ordinal()) {
            final Tree.CompilationUnit rootNode = cpc.getRootNode();
            List<CommonToken> tokens = cpc.getTokens();
            if (rootNode == null) {
                return;
            }
            
            final IAnnotationModel model = editor.getDocumentProvider()
                    .getAnnotationModel(editor.getEditorInput());
            if (model==null) {
                return;
            }
            
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
                            addRefinementAnnotation(model, that, 
                                    that.getIdentifier(), dec);
                        }
                    }
                }
                @Override
                public void visit(Tree.SpecifierStatement that) {
                    super.visit(that);
                    if (that.getRefinement()) {
                        Declaration dec = that.getDeclaration();
                        if (dec!=null) {
                            if (dec.isActual()) {
                                addRefinementAnnotation(model, that, 
                                        that.getBaseMemberExpression(), dec);
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
    
    public static Declaration getRefinedDeclaration(Declaration declaration) {
        //Reproduces the algorithm used to build the type hierarchy
        //first walk up the superclass hierarchy
        if (declaration.isClassOrInterfaceMember() && declaration.isShared()) {
            TypeDeclaration dec = (TypeDeclaration) declaration.getContainer();
            List<ProducedType> signature = getSignature(declaration);
            while (dec!=null) {
                ClassOrInterface superDec = dec.getExtendedTypeDeclaration();
                if (superDec!=null) {
                    Declaration superMemberDec = 
                            superDec.getDirectMember(declaration.getName(), signature, false);
                    if (superMemberDec!=null && 
                            superMemberDec.getRefinedDeclaration()!=null &&
                            declaration.getRefinedDeclaration()!=null &&
                            !isAbstraction(superMemberDec) &&
                            superMemberDec.getRefinedDeclaration()
                                .equals(declaration.getRefinedDeclaration())) {
                        return superMemberDec;
                    }
                }
                dec = superDec;
            }
            //now look at the very top of the hierarchy, even if it is an interface
            Declaration refinedDeclaration = declaration.getRefinedDeclaration();
            if (refinedDeclaration!=null &&
                    !declaration.equals(refinedDeclaration)) {
                List<Declaration> directlyInheritedMembers = 
                        getInterveningRefinements(declaration.getName(), signature,
                                refinedDeclaration,
                                (TypeDeclaration) declaration.getContainer(), 
                                (TypeDeclaration) refinedDeclaration.getContainer());
                directlyInheritedMembers.remove(refinedDeclaration);
                //TODO: do something for the case of
                //      multiple intervening interfaces?
                if (directlyInheritedMembers.size()==1) {
                    //exactly one intervening interface
                    return directlyInheritedMembers.get(0);
                }
                else {
                    //no intervening interfaces
                    return refinedDeclaration;
                }
            }
        }
        return null;
    }
    
    private void addRefinementAnnotation(IAnnotationModel model, 
            Tree.StatementOrArgument that, Node line, Declaration dec) {
        Declaration refined = getRefinedDeclaration(dec);
        if (refined!=null) {
            Declaration container = 
                    (Declaration) refined.getContainer();
            Unit unit = that.getUnit();
            String description = 
                    "refines " + container.getName(unit) + 
                    "." + refined.getName(unit);
            RefinementAnnotation ra = 
                    new RefinementAnnotation(description,  
                            refined, line.getToken().getLine());
            model.addAnnotation(ra, 
                    new Position(getIdentifyingStartOffset(that), 
                            getIdentifyingLength(that)));
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
            Node node = Nodes.findScope(cpc.getRootNode(), (ITextSelection) event.getSelection());
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
            if (node!=null && model!=null) {
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
