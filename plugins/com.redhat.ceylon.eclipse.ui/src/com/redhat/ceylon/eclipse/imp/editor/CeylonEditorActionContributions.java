package com.redhat.ceylon.eclipse.imp.editor;

import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.services.ILanguageActionsContributor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;

import com.redhat.ceylon.eclipse.imp.open.OpenDeclarationAction;
import com.redhat.ceylon.eclipse.imp.refactoring.ConvertToNamedArgumentsRefactoringAction;
import com.redhat.ceylon.eclipse.imp.refactoring.ExtractFunctionRefactoringAction;
import com.redhat.ceylon.eclipse.imp.refactoring.ExtractValueRefactoringAction;
import com.redhat.ceylon.eclipse.imp.refactoring.InlineRefactoringAction;
import com.redhat.ceylon.eclipse.imp.refactoring.RenameRefactoringAction;
import com.redhat.ceylon.eclipse.imp.search.FindReferencesAction;
import com.redhat.ceylon.eclipse.imp.search.FindRefinementsAction;
import com.redhat.ceylon.eclipse.imp.search.FindSubtypesAction;

public class CeylonEditorActionContributions implements
		ILanguageActionsContributor {

	public void contributeToEditorMenu(final UniversalEditor editor,
			IMenuManager menuManager) {
		//IMenuManager languageMenu = new MenuManager("Search");
        menuManager.add(new OpenDeclarationAction(editor));
        menuManager.add(new Separator());
		menuManager.add(new FindReferencesAction(editor));
		menuManager.add(new FindRefinementsAction(editor));
        menuManager.add(new FindSubtypesAction(editor));
        menuManager.add(new Separator());
        menuManager.add(new RenameRefactoringAction(editor));
        menuManager.add(new InlineRefactoringAction(editor));
        menuManager.add(new ExtractFunctionRefactoringAction(editor));
        menuManager.add(new ExtractValueRefactoringAction(editor));
        menuManager.add(new Separator());
        menuManager.add(new ConvertToNamedArgumentsRefactoringAction(editor));
	}

	public void contributeToMenuBar(final UniversalEditor editor, IMenuManager menu) {
		//languageMenu = new MenuManager("ceylon");
		/*IMenuManager refactor = menu.findMenuUsingPath("refactorMenuId");
		if (refactor.getItems().length==0) {
			for (IAction action: RefactoringContributor.getActions(editor)) {
				refactor.add(action);
			}
		}
		IMenuManager search = menu.findMenuUsingPath("navigate");
		search.add(new Separator());
		search.add(new FindReferencesAction(editor));
        search.add(new FindRefinementsAction(editor));
        search.add(new FindSubtypesAction(editor));
        search.add(new Separator());
        search.add(new OpenDeclarationAction("Open Declaration...", editor));*/
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
