package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

abstract class FindAction extends Action {
    
    private final UniversalEditor editor;

    FindAction(String text, UniversalEditor editor) {
        super(text);
        this.editor = editor;
    }
    
    @Override
    public void run() {
        CeylonParseController cpc = (CeylonParseController) editor.getParseController();
        Node node = cpc.getSourcePositionLocator().findNode(cpc.getRootNode(), 
                editor.getSelection().x, editor.getSelection().x+editor.getSelection().y);
        NewSearchUI.runQueryInBackground(createSearchQuery(
                CeylonReferenceResolver.getReferencedDeclaration(node), getProject(editor)));
    }

    public static IProject getProject(UniversalEditor editor) {
        return ((IFileEditorInput) editor.getEditorInput()).getFile().getProject();
    }

    public abstract FindSearchQuery createSearchQuery(Declaration declaration, IProject project);
    
}
