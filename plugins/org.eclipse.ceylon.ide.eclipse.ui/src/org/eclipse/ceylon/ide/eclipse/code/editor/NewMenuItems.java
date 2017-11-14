/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;


import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.actions.NewWizardMenu;

public class NewMenuItems extends CompoundContributionItem {
    
    private NewWizardMenu menu;

    public NewMenuItems() {}

    public NewMenuItems(String id) {}
    
    @Override
    public IContributionItem[] getContributionItems() {
        menu = new NewWizardMenu(getWorkbench().getActiveWorkbenchWindow());
        String newId = ActionFactory.NEW.getId();
        MenuManager newMenu = new MenuManager("New", newId);
        newMenu.setActionDefinitionId("org.eclipse.ui.file.newQuickMenu");
        newMenu.add(new Separator(newId));
        newMenu.add(menu);
        return new IContributionItem[] { newMenu };
    }
    
    @Override
    public void dispose() {
        menu.dispose();
    }
    
}
