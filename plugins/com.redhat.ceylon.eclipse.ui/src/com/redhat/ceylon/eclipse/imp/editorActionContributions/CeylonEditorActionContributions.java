package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.ILanguageActionsContributor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFileEditorInput;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.refactoring.RefactoringContributor;

public class CeylonEditorActionContributions implements
		ILanguageActionsContributor {

	public void contributeToEditorMenu(final UniversalEditor editor,
			IMenuManager menuManager) {
		//IMenuManager languageMenu = new MenuManager("Search");
		menuManager.add(new Action("Find References") {
			@Override
			public void run() {
				CeylonParseController cpc = (CeylonParseController) editor.getParseController();
				Node node = cpc.getSourcePositionLocator().findNode(cpc.getRootNode(), 
						editor.getSelection().x, editor.getSelection().x+editor.getSelection().y);
				Declaration d = CeylonReferenceResolver.getReferencedDeclaration(node);
				NewSearchUI.runQueryInBackground(new FindReferencesSearchQuery(cpc, d, node, 
						((IFileEditorInput) editor.getEditorInput()).getFile()));
			}
		});
	}

	public void contributeToMenuBar(UniversalEditor editor, IMenuManager menu) {
		//languageMenu = new MenuManager("ceylon");
		IMenuManager refactor = /*editor.getEditorSite().getActionBars()
				.getMenuManager()*/menu.findMenuUsingPath("refactorMenuId");
		if (refactor.getItems().length==0) {
			for (IAction action: RefactoringContributor.getActions(editor)) {
				refactor.add(action);
			}
		}
	}

	public void contributeToStatusLine(final UniversalEditor editor,
			IStatusLineManager statusLineManager) {
		// TODO add ControlContribution objects to the statusLineManager
	}

	public void contributeToToolBar(UniversalEditor editor,
			IToolBarManager toolbarManager) {
		// add ControlContribution objects to the toolbarManager
	}
}
