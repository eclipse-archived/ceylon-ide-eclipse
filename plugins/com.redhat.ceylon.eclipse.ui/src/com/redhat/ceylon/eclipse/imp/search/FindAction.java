package com.redhat.ceylon.eclipse.imp.search;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.search.ui.NewSearchUI;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.util.Util;

abstract class FindAction extends Action {
    
    private final UniversalEditor editor;

    FindAction(String text, UniversalEditor editor) {
        super(text);
        this.editor = editor;
    }
    
    @Override
    public void run() {
        CeylonParseController cpc = (CeylonParseController) editor.getParseController();
        Node node = CeylonSourcePositionLocator.findNode(cpc.getRootNode(), 
                editor.getSelection().x, editor.getSelection().x+editor.getSelection().y);
        NewSearchUI.runQueryInBackground(createSearchQuery(
                CeylonReferenceResolver.getReferencedDeclaration(node), 
                Util.getProject(editor.getEditorInput())));
    }

    public abstract FindSearchQuery createSearchQuery(Declaration declaration, IProject project);
    
}
