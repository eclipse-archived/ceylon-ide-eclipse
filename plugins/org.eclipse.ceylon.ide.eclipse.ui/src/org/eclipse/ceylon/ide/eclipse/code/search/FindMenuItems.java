/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;


import static org.eclipse.ceylon.ide.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.imageRegistry;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_DECS;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_REFS;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.editor.DynamicMenuItem;

public class FindMenuItems extends CompoundContributionItem {
    
    private static ImageDescriptor REFS = 
            imageRegistry().getDescriptor(CEYLON_REFS);
    private static ImageDescriptor DECS = 
            imageRegistry().getDescriptor(CEYLON_DECS);

    public FindMenuItems() {}
    
    public FindMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        IContributionItem[] items = getItems(getCurrentEditor());
        if (collapseMenuItems(getParent())) {
            MenuManager submenu = new MenuManager("Find");
            submenu.setActionDefinitionId(CeylonEditor.FIND_MENU_ID);
            for (IContributionItem item: items) {
                submenu.add(item);
            }
            return new IContributionItem[] { submenu };
        }
        else {
            return items;
        }
    }

    private IContributionItem[] getItems(IEditorPart editor) {
        return new IContributionItem[] {
                new DynamicMenuItem(PLUGIN_ID + ".action.findReferences", 
                        "Find &References",
                        editor==null ? false : new FindReferencesAction(editor).isEnabled(), 
                                REFS),
                new DynamicMenuItem(PLUGIN_ID + ".action.findAssignments", 
                        "Find Assi&gnments",
                        editor==null ? false : new FindAssignmentsAction(editor).isEnabled(), 
                                REFS),
                new DynamicMenuItem(PLUGIN_ID + ".action.findRefinements", 
                        "Find Refi&nements",
                        editor==null ? false : new FindRefinementsAction(editor).isEnabled(), 
                                DECS),
                new DynamicMenuItem(PLUGIN_ID + ".action.findSubtypes", 
                        "Find &Subtypes",
                        editor==null ? false : new FindSubtypesAction(editor).isEnabled(), 
                                DECS),
                new Separator(),
                new DynamicMenuItem("org.eclipse.search.ui.performTextSearchWorkspace",
                        "Find &Text in Workspace", true)
        };
    }

}
