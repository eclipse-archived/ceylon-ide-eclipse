package com.redhat.ceylon.eclipse.imp.editor;

import java.util.Iterator;

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
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;

public class CeylonAnnotationCreator extends EditorServiceBase {
    
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
    
}
