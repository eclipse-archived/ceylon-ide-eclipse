package org.eclipse.ceylon.ide.eclipse.code.editor;

import static org.eclipse.ceylon.compiler.typechecker.analyzer.AnalyzerUtil.getLastExecutableStatement;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findScope;

import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IEditorInput;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;

/**
 * Responsible for adding refinement annotations to the 
 * vertical ruler, and updating the highlight range in 
 * the vertical ruler.
 *
 */
public class RangeAnnotationCreator 
        implements ISelectionChangedListener {
    
    private CeylonEditor editor;
    private CeylonInitializerAnnotation initializerAnnotation;
    
    public RangeAnnotationCreator(CeylonEditor editor) {
        this.editor = editor;
    }
    
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        CeylonParseController cpc = 
                editor.getParseController();
        Tree.CompilationUnit rootNode = 
                cpc.getLastCompilationUnit();
        if (rootNode==null) {
            return;
        }
        ITextSelection selection = 
                (ITextSelection) 
                event.getSelection();
        Node node = findScope(rootNode, selection);
        if (node!=null) {
            editor.setHighlightRange(
                    node.getStartIndex(), 
                    node.getDistance(), 
                    false);
        }
        else {
            editor.resetHighlightRange();
        }
        IEditorInput editorInput = 
                editor.getEditorInput();
        IAnnotationModel model = 
                editor.getDocumentProvider()
                .getAnnotationModel(editorInput);
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
    
    class InitializerVisitor extends Visitor {
        @Override
        public void visit(Tree.ClassDefinition that) {
            if (that.getClassBody()==null ||
                    that.getIdentifier()==null) {
                return;
            }
            createAnnotation(that, 
                    that.getClassBody(), 
                    that.getIdentifier().getText());
        }
        @Override
        public void visit(Tree.ObjectDefinition that) {
            if (that.getClassBody()==null ||
                    that.getIdentifier()==null) {
                return;
            }
            createAnnotation(that, 
                    that.getClassBody(), 
                    that.getIdentifier().getText());
        }
        private void createAnnotation(Node that, 
                Tree.ClassBody body, String name) {
//          int offset = editor.getSelection().getOffset();
//          if (offset>that.getStartIndex()&&offset<that.getStopIndex()) {
            Tree.Statement les = 
                    getLastExecutableStatement(body);
            if (les != null) {
                int startIndex = body.getStartIndex() + 2;
                int stopIndex = les.getEndIndex();
                Position initializerPosition = 
                        new Position(startIndex, 
                                stopIndex - startIndex);
                initializerAnnotation = 
                        new CeylonInitializerAnnotation(name, 
                                initializerPosition, 1);
            }
//          }
        }
    }
    
}
