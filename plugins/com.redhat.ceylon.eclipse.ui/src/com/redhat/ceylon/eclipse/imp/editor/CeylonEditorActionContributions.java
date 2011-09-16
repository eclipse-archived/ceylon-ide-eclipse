package com.redhat.ceylon.eclipse.imp.editor;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.ILanguageActionsContributor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;

import com.redhat.ceylon.eclipse.imp.open.OpenDeclarationAction;

public class CeylonEditorActionContributions implements
		ILanguageActionsContributor {

	public void contributeToEditorMenu(final UniversalEditor editor,
			IMenuManager menuManager) {
		//IMenuManager languageMenu = new MenuManager("Search");
        menuManager.add(new OpenDeclarationAction(editor));
        /*menuManager.add(new Separator());
		menuManager.add(new FindReferencesAction(editor));
		menuManager.add(new FindRefinementsAction(editor));
        menuManager.add(new FindSubtypesAction(editor));
        menuManager.add(new Separator());
        menuManager.add(new RenameRefactoringAction(editor));
        menuManager.add(new InlineRefactoringAction(editor));
        menuManager.add(new ExtractValueRefactoringAction(editor));
        menuManager.add(new ExtractFunctionRefactoringAction(editor));
        //menuManager.add(new Separator());
        menuManager.add(new ConvertToNamedArgumentsRefactoringAction(editor));*/
	}

	public void contributeToMenuBar(final UniversalEditor editor, IMenuManager menu) {
	    //menu.findMenuUsingPath("sourceMenuId");
		//languageMenu = new MenuManager("ceylon");
		/*IMenuManager refactor = menu.findMenuUsingPath("refactorMenuId");
		refactor.removeAll();
		refactor.add(new RenameRefactoringAction(editor));
		refactor.add(new InlineRefactoringAction(editor));
		refactor.add(new ExtractValueRefactoringAction(editor));
		refactor.add(new ExtractFunctionRefactoringAction(editor));
        refactor.add(new Separator());
		refactor.add(new ConvertToNamedArgumentsRefactoringAction(editor));
		IMenuManager search = menu.findMenuUsingPath("navigate");
		search.removeAll();
        //search.add(new Separator());
        //search.add(new OpenDeclarationAction(editor));
		search.add(new Separator());
		search.add(new FindReferencesAction(editor));
        search.add(new FindRefinementsAction(editor));
        search.add(new FindSubtypesAction(editor));*/
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
