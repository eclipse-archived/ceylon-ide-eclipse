package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener.Stage.SYNTACTIC_ANALYSIS;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.source.projection.IProjectionListener;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.code.parse.TreeLifecycleListener;

public class FoldingController implements TreeLifecycleListener, IProjectionListener{
	
    private final FoldingUpdater foldingUpdater;
    private CeylonEditor editor;
    private boolean foldingEnabled;

    public FoldingController(CeylonEditor editor, boolean foldingEnabled) {
        this.editor = editor;
        this.foldingEnabled = foldingEnabled;
        foldingUpdater = new FoldingUpdater(editor.getCeylonSourceViewer());
    }
    
    @Override
    public void projectionEnabled() {
        foldingEnabled=true;
        foldingUpdater.reset();
//        editor.scheduleParsing();
        update(editor.getParseController(), new NullProgressMonitor());
    }
    
    @Override
    public void projectionDisabled() {
        foldingEnabled=false;
    }

    public Stage getStage() {
        return SYNTACTIC_ANALYSIS;
    }

    public void update(CeylonParseController parseController, 
    		IProgressMonitor monitor) {
        if (foldingEnabled) {
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
}