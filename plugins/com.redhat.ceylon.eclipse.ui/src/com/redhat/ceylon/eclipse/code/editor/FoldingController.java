package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.SYNTACTIC_ANALYSIS;

import org.eclipse.core.runtime.IProgressMonitor;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;

public class FoldingController implements TreeLifecycleListener {
	
    private final FoldingUpdater foldingUpdater;

    public FoldingController(CeylonSourceViewer sourceViewer) {
        foldingUpdater = new FoldingUpdater(sourceViewer);
    }

    public Stage getStage() {
        return SYNTACTIC_ANALYSIS;
    }

    public void update(CeylonParseController parseController, 
    		IProgressMonitor monitor) {
        Tree.CompilationUnit rn = parseController.getRootNode();
		if (rn!=null) { // can be null if file is outside workspace
            try {
                foldingUpdater.updateFoldingStructure(rn, parseController.getTokens());
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}