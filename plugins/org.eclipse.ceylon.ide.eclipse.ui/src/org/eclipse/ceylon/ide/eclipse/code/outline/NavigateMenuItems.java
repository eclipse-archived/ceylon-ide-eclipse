/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.outline;


import static org.eclipse.ceylon.ide.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.imageRegistry;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.LAST_EDIT;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.NEXT_ANN;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.PREV_ANN;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_BACK;
import static org.eclipse.ui.ISharedImages.IMG_TOOL_FORWARD;
import static org.eclipse.ui.PlatformUI.getWorkbench;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.internal.texteditor.TextEditorPlugin;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.editor.DynamicMenuItem;
import org.eclipse.ceylon.ide.eclipse.code.open.SelectedDeclarationMenuItems;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class NavigateMenuItems extends CompoundContributionItem {
    
    private static final ImageDescriptor EDIT = 
            imageRegistry().getDescriptor(LAST_EDIT);
    private static final ImageDescriptor NEXT = 
            imageRegistry().getDescriptor(NEXT_ANN);
    private static final ImageDescriptor PREV = 
            imageRegistry().getDescriptor(PREV_ANN);
    
    public NavigateMenuItems() {}
    
    public NavigateMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        IContributionItem[] items = getItems(getCurrentEditor());
        if (collapseMenuItems(getParent())) {
            MenuManager submenu = new MenuManager("Navigate");
            submenu.setActionDefinitionId(CeylonEditor.NAVIGATE_MENU_ID);
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
        boolean lastEditEnabled = TextEditorPlugin.getDefault().getLastEditPosition() != null;
        IContributionItem[] items = new SelectedDeclarationMenuItems().getContributionItems();
        ISharedImages images = getWorkbench().getSharedImages();
        return new IContributionItem[] {
                items[0], items[1], items[2], items[3], items[4], items[5], items[6],
                new Separator(),
                new DynamicMenuItem(IWorkbenchCommandConstants.NAVIGATE_NEXT, 
                        "Ne&xt Annotation", true, NEXT),
                new DynamicMenuItem(IWorkbenchCommandConstants.NAVIGATE_PREVIOUS, 
                        "Pre&vious Annotation", true, PREV),
                new Separator(),
                new DynamicMenuItem(IWorkbenchCommandConstants.NAVIGATE_BACKWARD_HISTORY, 
                        "&Back", true, images.getImageDescriptor(IMG_TOOL_BACK)),
                new DynamicMenuItem(IWorkbenchCommandConstants.NAVIGATE_FORWARD_HISTORY, 
                        "&Forward", true, images.getImageDescriptor(IMG_TOOL_FORWARD)),
                new Separator(),
                new DynamicMenuItem(ITextEditorActionDefinitionIds.GOTO_LAST_EDIT_POSITION, 
                        "Last Edit Lo&cation", lastEditEnabled, EDIT),
                new DynamicMenuItem(ITextEditorActionDefinitionIds.LINE_GOTO, 
                        "&Go to Line...", true),
                new DynamicMenuItem(CeylonPlugin.PLUGIN_ID + ".editor.gotoMatchingFence", 
                        "Go to &Matching Bracket", true)
        };
    }

}
