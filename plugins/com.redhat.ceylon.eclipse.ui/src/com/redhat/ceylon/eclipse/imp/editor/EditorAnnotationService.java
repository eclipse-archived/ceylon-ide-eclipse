package com.redhat.ceylon.eclipse.imp.editor;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.findScope;

import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.base.EditorServiceBase;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.parser.CeylonLexer;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;

/**
 * Responsible for adding refinement annotations to 
 * the vertical ruler, and updating the highlight
 * range in the vertical ruler.
 * 
 * @author gavin
 *
 */
public class EditorAnnotationService extends EditorServiceBase {
    
    @Override
    public AnalysisRequired getAnalysisRequired() {
        return AnalysisRequired.NONE;
    }
    
    @Override
    public void update(IParseController parseController, IProgressMonitor monitor) {
        final CeylonParseController cpc = (CeylonParseController) parseController;
        if (cpc.getRootNode()==null) return;
        final IAnnotationModel model = getEditor().getDocumentProvider()
                .getAnnotationModel(getEditor().getEditorInput());
        for (Iterator<Annotation> iter = model.getAnnotationIterator(); 
                iter.hasNext();) {
            Annotation a = iter.next();
            if (a instanceof RefinementAnnotation) {
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
                        addRefinementAnnotation(cpc.getSourcePositionLocator(), 
                                model, that, dec);
                    }
                }
            }

        }.visit(cpc.getRootNode());
        
        for (CommonToken token: (List<CommonToken>) cpc.getTokenStream().getTokens()) {
            if (token.getType()==CeylonLexer.LINE_COMMENT) {
                String comment = token.getText().toLowerCase();
                if (comment.startsWith("//todo")||comment.startsWith("//fix")) {
                    addTodoAnnotation(token, model);
                }
            }
        }
    }
    
    private void addRefinementAnnotation(CeylonSourcePositionLocator spl,
            IAnnotationModel model, Tree.Declaration that, Declaration dec) {
        //TODO: improve this:
        Declaration refined = ((TypeDeclaration) dec.getContainer())
                .getExtendedTypeDeclaration().getMember(dec.getName());
        if (refined==null) {
            refined = dec.getRefinedDeclaration();
        }
        //IFile file = cpc.getProject().getRawProject().getFile(cpc.getPath());
        //don't include hover description because it will hide the doc hover
        /*String desc = "refines '" + CeylonContentProposer.getDescriptionFor(refined) + 
                "' declared by " + refined.getContainer().getName() + 
                " [" + getPackageLabel(dec) + "]";*/
        RefinementAnnotation ra = new RefinementAnnotation(null, refined, 
                that.getIdentifier().getToken().getLine());
        model.addAnnotation(ra, new Position(spl.getStartOffset(that), 
                        spl.getLength(that)+1));
    }
    
    private void addTodoAnnotation(CommonToken token, IAnnotationModel model) {
        model.addAnnotation(new Annotation("com.redhat.ceylon.eclipse.ui.todo", false, null), 
                new Position(token.getStartIndex(), token.getStopIndex()-token.getStartIndex()+1));
    }
    
    @Override
    public void setEditor(UniversalEditor editor) {
        super.setEditor(editor);
        System.out.println("Adding SelectionListener to editor " + editor);
        ((IPostSelectionProvider) editor.getSelectionProvider())
            .addPostSelectionChangedListener(new SelectionListener());
    }
    
    class SelectionListener implements ISelectionChangedListener {
        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            final CeylonParseController cpc = (CeylonParseController) editor.getParseController();
            if (cpc.getRootNode()==null) return;
            Node node = findScope(cpc.getRootNode(), (ITextSelection) event.getSelection());
            if (node!=null) {
                editor.setHighlightRange(node.getStartIndex(), 
                        node.getStopIndex()-node.getStartIndex()+1, false);
            }
            else {
                editor.resetHighlightRange();
            }
        }
    }
    
}
