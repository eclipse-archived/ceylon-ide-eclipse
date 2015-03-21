package com.redhat.ceylon.eclipse.code.navigator;

import static com.redhat.ceylon.eclipse.code.refactor.MoveUtil.getImportText;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.actions.CopyQualifiedNameAction;
import org.eclipse.jdt.internal.ui.refactoring.reorg.CopyToClipboardAction;
import org.eclipse.jdt.internal.ui.refactoring.reorg.CutAction;
import org.eclipse.jdt.internal.ui.refactoring.reorg.DeleteAction;
import org.eclipse.jdt.internal.ui.refactoring.reorg.PasteAction;
//import org.eclipse.jdt.internal.ui.refactoring.reorg.PasteAction;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.wizards.IWizardDescriptor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.editor.ImportsTransfer;
import com.redhat.ceylon.eclipse.code.editor.SourceTransfer;
import com.redhat.ceylon.eclipse.code.wizard.NewUnitWizard;

/**
 * Action group that adds copy, cut, paste, and delete actions to a view part's context
 * menu and installs handlers for the corresponding global menu actions.
 *
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 *
 * @since 2.0
 *
 * @noextend This class is not intended to be subclassed by clients.
 */
public class CCPActionGroup extends ActionGroup {

 	private final SelectionDispatchAction[] fActions;

 	private final SelectionDispatchAction fDeleteAction;
	private final SelectionDispatchAction fCopyAction;
	private final SelectionDispatchAction fCopyQualifiedNameAction;
	private final SelectionDispatchAction fPasteAction;
	private final SelectionDispatchAction fCutAction;
	private final ISelectionProvider fSelectionProvider;

    private IWorkbenchSite site;


	/**
	 * Creates a new <code>CCPActionGroup</code>. The group requires that the selection provided by
	 * the view part's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param part the view part that owns this action group
	 * @param includeOnlyCopyActions <code>true</code> if the group only includes the copy actions,
	 *            <code>false</code> to include all actions
	 * @since 3.7
	 */
	public CCPActionGroup(IViewPart part, boolean includeOnlyCopyActions) {
		this(part.getSite(), null, includeOnlyCopyActions);
	}

	/**
	 * Creates a new <code>CCPActionGroup</code>. The group requires that
	 * the selection provided by the view part's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 *
	 * @param part the view part that owns this action group
	 */
	public CCPActionGroup(IViewPart  part) {
		this(part.getSite(), null, false);
	}

	/**
	 * Creates a new <code>CCPActionGroup</code>.  The group requires that
	 * the selection provided by the page's selection provider is of type
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 *
	 * @param page the page that owns this action group
	 */
	public CCPActionGroup(Page page) {
		this(page.getSite(), null, false);
	}

	/**
	 * Creates a new <code>CCPActionGroup</code>. The group requires
	 * that the selection provided by the given selection provider is of type
	 * {@link IStructuredSelection}.
	 *
	 * @param site the site that will own the action group.
	 * @param specialSelectionProvider the selection provider used instead of the
	 *  sites selection provider.
	 *
	 * @since 3.4
	 */
	public CCPActionGroup(IWorkbenchSite site, ISelectionProvider specialSelectionProvider) {
		this(site, specialSelectionProvider, false);
	}

    private NewUnitWizard openPackageWizard(IStructuredSelection selection, 
            String pastedText, String importText) {
        IWizardDescriptor descriptor = 
                getWorkbench().getNewWizardRegistry()
                    .findWizard(PLUGIN_ID + ".newUnitWizard");
        if (descriptor!=null) {
            try {
                NewUnitWizard wizard = 
                        (NewUnitWizard) descriptor.createWizard();
                wizard.setPastedText(pastedText);
                wizard.setImports(importText);
                wizard.init(site.getWorkbenchWindow().getWorkbench(), 
                        selection);
                WizardDialog wd = 
                        new WizardDialog(site.getShell(), wizard);
                wizard.setWindowTitle("Paste Ceylon Snippet");
                wizard.setTitleAndDescription("Paste Ceylon Snippet",
                        "Paste code fragment into a new Ceylon source file.");
                wizard.setDefaultUnitName("snippet");
                wd.open();
                return wizard;
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
	/**
	 * Creates a new <code>CCPActionGroup</code>. The group requires that the selection provided by
	 * the given selection provider is of type {@link IStructuredSelection}.
	 * 
	 * @param site the site that will own the action group.
	 * @param specialSelectionProvider the selection provider used instead of the sites selection
	 *            provider.
	 * @param includeOnlyCopyActions <code>true</code> if the group only included the copy actions,
	 *            <code>false</code> otherwise
	 * @since 3.7
	 */
	private CCPActionGroup(IWorkbenchSite site, ISelectionProvider specialSelectionProvider, boolean includeOnlyCopyActions) {
		this.site = site;
        fSelectionProvider= specialSelectionProvider == null ? site.getSelectionProvider() : specialSelectionProvider;

		fCopyAction= new CopyToClipboardAction(site);
		fCopyAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);

		fCopyQualifiedNameAction= new CopyQualifiedNameAction(site);
		fCopyQualifiedNameAction.setActionDefinitionId(CopyQualifiedNameAction.ACTION_DEFINITION_ID);


		if (!includeOnlyCopyActions) {
			fPasteAction= new PasteAction(site) {
			    @Override
			    public void run(IStructuredSelection selection) {
			        if (selection.size()==1) {
			            Object firstElement = selection.getFirstElement();
			            if (firstElement instanceof IPackageFragmentRoot || 
			                    firstElement instanceof IPackageFragment ||
			                    firstElement instanceof IProject) {
			                Clipboard clipboard = 
			                        new Clipboard(getShell().getDisplay());
			                try {
			                    String text = (String) clipboard.getContents(SourceTransfer.INSTANCE);
			                    if (text==null) {
			                        text = (String) clipboard.getContents(TextTransfer.getInstance());
			                    }
			                    if (text!=null) {
			                        @SuppressWarnings({"unchecked", "rawtypes"})
			                        Map<Declaration,String> imports = 
			                                (Map) clipboard.getContents(ImportsTransfer.INSTANCE);
			                        String importText = null;
			                        if (imports!=null) {
			                            Set<String> packages = new HashSet<String>();
			                            for (Declaration d: imports.keySet()) {
			                                packages.add(d.getUnit().getPackage().getNameAsString());
			                            }
			                            importText = getImportText(packages, imports, 
			                                    System.lineSeparator());
			                        }
			                        openPackageWizard(selection, text.substring(1), importText);
			                        return;
			                    }
			                }
			                catch (Exception e) {
			                    e.printStackTrace();
			                }
			            }
			            super.run(selection);
			        }
			    }
			};
			fPasteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);
			fDeleteAction= new DeleteAction(site);
			fDeleteAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_DELETE);
			fCutAction= new CutAction(site);
			fCutAction.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_CUT);
			fActions= new SelectionDispatchAction[] { fCutAction, fCopyAction, fCopyQualifiedNameAction, fPasteAction, fDeleteAction };
		} else {
			fPasteAction= null;
			fDeleteAction= null;
			fCutAction= null;
			fActions= new SelectionDispatchAction[] { fCopyAction, fCopyQualifiedNameAction };
		}

		if (specialSelectionProvider != null) {
			for (int i= 0; i < fActions.length; i++) {
				fActions[i].setSpecialSelectionProvider(specialSelectionProvider);
			}
		}

		registerActionsAsSelectionChangeListeners();
	}

	private void registerActionsAsSelectionChangeListeners() {
		ISelectionProvider provider= fSelectionProvider;
		ISelection selection= provider.getSelection();
		for (int i= 0; i < fActions.length; i++) {
			SelectionDispatchAction action= fActions[i];
			action.update(selection);
			provider.addSelectionChangedListener(action);
		}
	}

	private void deregisterActionsAsSelectionChangeListeners() {
		ISelectionProvider provider= fSelectionProvider;
		for (int i= 0; i < fActions.length; i++) {
			provider.removeSelectionChangedListener(fActions[i]);
		}
	}


	/**
	 * Returns the delete action managed by this action group.
	 *
	 * @return the delete action. Returns <code>null</code> if the group
	 * 	doesn't provide any delete action
	 */
	public IAction getDeleteAction() {
		return fDeleteAction;
	}

	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		if (fDeleteAction != null)
			actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), fDeleteAction);
		actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), fCopyAction);
		actionBars.setGlobalActionHandler(CopyQualifiedNameAction.ACTION_HANDLER_ID, fCopyQualifiedNameAction);
		if (fCopyAction != null)
			actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), fCutAction);
		if (fPasteAction != null)
			actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), fPasteAction);
	}

	/* (non-Javadoc)
	 * Method declared in ActionGroup
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		for (int i= 0; i < fActions.length; i++) {
			SelectionDispatchAction action= fActions[i];
			if (action == fCutAction && !fCutAction.isEnabled())
				continue;
			menu.appendToGroup(ICommonMenuConstants.GROUP_EDIT, action);
		}
	}

	/*
	 * @see ActionGroup#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		deregisterActionsAsSelectionChangeListeners();
	}

}
