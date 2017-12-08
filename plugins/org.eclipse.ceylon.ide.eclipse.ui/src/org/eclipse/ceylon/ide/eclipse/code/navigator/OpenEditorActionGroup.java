/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.navigator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jdt.ui.IContextMenuConstants;
import org.eclipse.jdt.ui.actions.JdtActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.OpenWithMenu;

public class OpenEditorActionGroup extends ActionGroup {

    private IWorkbenchSite fSite;
    private CeylonOpenAction fOpen;
    private ISelectionProvider fSelectionProvider;

    /**
     * Creates a new <code>OpenActionGroup</code>. The group requires
     * that the selection provided by the part's selection provider is of type <code>
     * org.eclipse.jface.viewers.IStructuredSelection</code>.
     *
     * @param part the view part that owns this action group
     */
    public OpenEditorActionGroup(IViewPart part) {
        this(part.getSite());
    }

    /**
     * Creates a new <code>OpenEditorActionGroup</code>. The group requires
     * that the selection provided by the given selection provider is of type
     * {@link IStructuredSelection}.
     *
     * @param site the site that will own the action group.
     * @param specialSelectionProvider the selection provider used instead of the
     *  sites selection provider.
     *
     * @since 3.4
     */
    public OpenEditorActionGroup(IWorkbenchPartSite site) {
        fSite= site;
        fOpen= new CeylonOpenAction(fSite);
//        fOpen.setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
        fSelectionProvider= fSite.getSelectionProvider();
        initialize();
    }

    /**
     * Returns the open action managed by this action group.
     *
     * @return the open action. Returns <code>null</code> if the group
     *  doesn't provide any open action
     */
    public IAction getOpenAction() {
        return fOpen;
    }

    private void initialize() {
        ISelection selection= fSelectionProvider.getSelection();
        fOpen.update(selection);
        fSelectionProvider.addSelectionChangedListener(fOpen);
    }

    /* (non-Javadoc)
     * Function declared in ActionGroup
     */
    @Override
    public void fillActionBars(IActionBars actionBar) {
        super.fillActionBars(actionBar);
        setGlobalActionHandlers(actionBar);
    }

    /* (non-Javadoc)
     * Function declared in ActionGroup
     */
    @Override
    public void fillContextMenu(IMenuManager menu) {
        super.fillContextMenu(menu);
        appendToGroup(menu, fOpen);
        addOpenWithMenu(menu);
    }

    /*
     * @see ActionGroup#dispose()
     */
    @Override
    public void dispose() {
        fSelectionProvider.removeSelectionChangedListener(fOpen);
        super.dispose();
    }

    private void setGlobalActionHandlers(IActionBars actionBars) {
        actionBars.setGlobalActionHandler(JdtActionConstants.OPEN, fOpen);
    }

    private void appendToGroup(IMenuManager menu, IAction action) {
        if (action.isEnabled())
            menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, action);
    }

    private void addOpenWithMenu(IMenuManager menu) {
        ISelection selection= getContext().getSelection();
        if (selection.isEmpty() || !(selection instanceof IStructuredSelection))
            return;
        IStructuredSelection ss= (IStructuredSelection)selection;
        if (ss.size() != 1)
            return;

        Object o= ss.getFirstElement();
        if (!(o instanceof IAdaptable))
            return;

        IAdaptable element= (IAdaptable)o;
        Object resource= element.getAdapter(IResource.class);
        if (!(resource instanceof IFile))
            return;

        // Create a menu.
        IMenuManager submenu= new MenuManager(ActionMessages.OpenWithMenu_label);
        submenu.add(new OpenWithMenu(fSite.getPage(), (IFile) resource));

        // Add the submenu.
        menu.appendToGroup(IContextMenuConstants.GROUP_OPEN, submenu);
    }
}
