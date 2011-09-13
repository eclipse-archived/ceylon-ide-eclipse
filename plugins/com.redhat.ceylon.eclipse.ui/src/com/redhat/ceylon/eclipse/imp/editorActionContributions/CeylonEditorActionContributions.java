package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.imp.parser.ISourcePositionLocator;
import org.eclipse.imp.services.ILanguageActionsContributor;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.editor.FilteredTypesSelectionDialog;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.quickfix.QuickFixAssistant;
import com.redhat.ceylon.eclipse.imp.refactoring.RefactoringContributor;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonEditorActionContributions implements
		ILanguageActionsContributor {

	public void contributeToEditorMenu(final UniversalEditor editor,
			IMenuManager menuManager) {
		//IMenuManager languageMenu = new MenuManager("Search");
		menuManager.add(new FindReferencesAction(editor));
		menuManager.add(new FindRefinementsAction(editor));
        menuManager.add(new FindSubtypesAction(editor));
	}

	public void contributeToMenuBar(final UniversalEditor editor, IMenuManager menu) {
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
        search.add(new Separator());
        search.add( new Action("Open Declaration...") {
            { setImageDescriptor(JavaPluginImages.DESC_TOOL_OPENTYPE); }
            @Override
            public void run() {
                Shell shell = CeylonPlugin.getInstance().getWorkbench()
                        .getActiveWorkbenchWindow().getShell();
                FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(shell, editor);
                dialog.setTitle("Open Declaration");
                dialog.open();
                Object[] types = dialog.getResult();
                if (types != null && types.length > 0) {
                    CeylonParseController cpc = (CeylonParseController) editor.getParseController();
                    Tree.Declaration node = CeylonReferenceResolver.getDeclarationNode(cpc, 
                            (Declaration) types[0]);
                    ISourcePositionLocator locator = cpc.getSourcePositionLocator();
                    IPath path = locator.getPath(node).removeFirstSegments(1);
                    int targetOffset = locator.getStartOffset(node);
                    IResource file = cpc.getProject().getRawProject().findMember(path);
                    QuickFixAssistant.gotoChange(file, targetOffset, 0);
                }
            }
        });
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
