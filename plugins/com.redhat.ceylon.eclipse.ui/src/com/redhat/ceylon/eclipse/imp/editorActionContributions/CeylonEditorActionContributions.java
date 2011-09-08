package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.ILanguageActionsContributor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;

import com.redhat.ceylon.eclipse.imp.refactoring.RefactoringContributor;

public class CeylonEditorActionContributions implements
		ILanguageActionsContributor {

	public void contributeToEditorMenu(final UniversalEditor editor,
			IMenuManager menuManager) {
		//IMenuManager languageMenu = new MenuManager("Search");
		menuManager.add(new FindReferencesAction(editor));
		menuManager.add(new FindRefinementsAction(editor));
        menuManager.add(new FindSubtypesAction(editor));
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
		IMenuManager search = /*editor.getEditorSite().getActionBars()
				.getMenuManager()*/menu.findMenuUsingPath("navigate");
		search.add(new Separator());
		search.add(new FindReferencesAction(editor));
        search.add(new FindRefinementsAction(editor));
        search.add(new FindSubtypesAction(editor));
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
