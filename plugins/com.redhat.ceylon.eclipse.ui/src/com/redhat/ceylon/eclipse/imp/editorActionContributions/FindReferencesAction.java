package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.action.Action;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

class FindReferencesAction extends Action {
	private final UniversalEditor editor;

	FindReferencesAction(UniversalEditor editor) {
		super("Find References");
		this.editor = editor;
		setAccelerator(SWT.CONTROL | SWT.ALT | 'G');
	}

	@Override
	public void run() {
		CeylonParseController cpc = (CeylonParseController) editor.getParseController();
		Node node = cpc.getSourcePositionLocator().findNode(cpc.getRootNode(), 
				editor.getSelection().x, editor.getSelection().x+editor.getSelection().y);
		Declaration referencedDeclaration = CeylonReferenceResolver.getReferencedDeclaration(node);
		NewSearchUI.runQueryInBackground(new FindReferencesSearchQuery(cpc, referencedDeclaration, 
				((IFileEditorInput) editor.getEditorInput()).getFile()));
	}
}