package com.redhat.ceylon.eclipse.imp.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.imp.parser.IParseController;
import org.eclipse.imp.services.base.EditorServiceBase;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class CeylonAnnotationCreator extends EditorServiceBase {
    
    @Override
    public AnalysisRequired getAnalysisRequired() {
        return AnalysisRequired.NONE;
    }
    
    @Override
    public void update(IParseController parseController, IProgressMonitor monitor) {
        final CeylonParseController cpc = (CeylonParseController) parseController;
        final IAnnotationModel model = getEditor().getDocumentProvider()
                .getAnnotationModel(getEditor().getEditorInput());
        /*model.addAnnotation( new DefaultRangeIndicator(), 
                new Position(50, 100));*/
        new Visitor() {
            @Override
            public void visit(Tree.Declaration that) {
                super.visit(that);
                if (that.getDeclarationModel()!=null) {
                    if (that.getDeclarationModel().isActual()) {
                        Declaration refined = that.getDeclarationModel().getRefinedDeclaration();
                        TypeDeclaration supertype = (TypeDeclaration) refined.getContainer();
                        String pkg = supertype.getUnit().getPackage().getQualifiedNameString();
                        if (pkg.isEmpty()) pkg="defaut package";
                        model.addAnnotation(new Annotation("com.redhat.ceylon.eclipse.ui.refinement", 
                                    false, null/*"refines " + refined.getName() + " declared by " + 
                                    supertype.getName() + " [" + pkg + "]"*/), 
                                new Position(cpc.getSourcePositionLocator().getStartOffset(that), 
                                        cpc.getSourcePositionLocator().getLength(that)+1));
                    }
                }
            }
        }.visit(cpc.getRootNode());
    }
    
}
