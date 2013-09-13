package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.SYNTACTIC_ANALYSIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;

import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;

public class FoldingController implements TreeLifecycleListener {
	
    private final ProjectionAnnotationModel fAnnotationModel;
    private final CeylonFoldingUpdater fFoldingUpdater;

    public FoldingController(ProjectionAnnotationModel annotationModel, CeylonSourceViewer sourceViewer) {
        this.fAnnotationModel= annotationModel;
        this.fFoldingUpdater= new CeylonFoldingUpdater(sourceViewer);
    }

    public Stage getStage() {
        return SYNTACTIC_ANALYSIS;
    }

    public void update(CeylonParseController parseController, 
    		IProgressMonitor monitor) {
        if (fAnnotationModel != null) { // can be null if file is outside workspace
            try {
                fFoldingUpdater.updateFoldingStructure(parseController, 
                		fAnnotationModel);
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}