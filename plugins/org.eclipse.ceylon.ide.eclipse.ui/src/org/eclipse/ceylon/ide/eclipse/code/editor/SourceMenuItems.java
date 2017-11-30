/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;


import static org.eclipse.ceylon.ide.eclipse.code.editor.DynamicMenuItem.collapseMenuItems;
import static org.eclipse.ceylon.ide.eclipse.code.editor.DynamicMenuItem.isContextMenu;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.imageRegistry;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_DELETE;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_DELETE_IMPORT;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_REVEAL;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.QUICK_ASSIST;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.TERMINATE_STATEMENT;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;

import java.util.Arrays;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import org.eclipse.ceylon.ide.eclipse.code.imports.CleanImportsHandler;
import org.eclipse.ceylon.ide.eclipse.code.refactor.DeleteRefactoring;
import org.eclipse.ceylon.ide.eclipse.code.refactor.RevealInferredTypeHandler;

public class SourceMenuItems extends CompoundContributionItem {
    
    private static ImageDescriptor REVEAL = 
            imageRegistry().getDescriptor(CEYLON_REVEAL);
    private static ImageDescriptor TERMINATE = 
            imageRegistry().getDescriptor(TERMINATE_STATEMENT);
    private static ImageDescriptor FIX = 
            imageRegistry().getDescriptor(QUICK_ASSIST);
    private static ImageDescriptor DELETE = 
            imageRegistry().getDescriptor(CEYLON_DELETE);
    private static ImageDescriptor DELETE_IMPORT = 
            imageRegistry().getDescriptor(CEYLON_DELETE_IMPORT);
//    private static ImageDescriptor FORMAT = imageRegistry.getDescriptor(FORMAT_BLOCK);
//    private static ImageDescriptor ADD = imageRegistry.getDescriptor(ADD_COMMENT);
//    private static ImageDescriptor REMOVE = imageRegistry.getDescriptor(REMOVE_COMMENT);
//    private static ImageDescriptor TOGGLE = imageRegistry.getDescriptor(TOGGLE_COMMENT);
//    private static ImageDescriptor CORRECT = imageRegistry.getDescriptor(CORRECT_INDENT);
//    private static ImageDescriptor LEFT = imageRegistry.getDescriptor(SHIFT_LEFT);
//    private static ImageDescriptor RIGHT = imageRegistry.getDescriptor(SHIFT_RIGHT);
    
    public SourceMenuItems() {}
    
    public SourceMenuItems(String id) {
        super(id);
    }
    
    @Override
    public IContributionItem[] getContributionItems() {
        IContributionItem[] items = getItems(getCurrentEditor());
        if (collapseMenuItems(getParent())) {
            MenuManager submenu = new MenuManager("Source");
            submenu.setActionDefinitionId(CeylonEditor.SOURCE_MENU_ID);
            for (IContributionItem item: items) {
                submenu.add(item);
            }
            return new IContributionItem[] { submenu };
        }
        else if (isContextMenu(getParent())) {
            IContributionItem[] copy = Arrays.copyOf(items, items.length+1);
            copy[items.length] = new Separator();
            return copy;
        }
        else {
            return items;            
        }
    }

    private IContributionItem[] getItems(IEditorPart editor) {
        return new IContributionItem[] {
                new DynamicMenuItem(PLUGIN_ID + ".action.cleanImports", 
                        "&Organize Imports", 
                        new CleanImportsHandler().isEnabled(), DELETE_IMPORT),
                new DynamicMenuItem(PLUGIN_ID + ".action.delete", 
                        "Safe &Delete...",
                        editor!=null && new DeleteRefactoring(editor).getEnabled(), DELETE),
                new DynamicMenuItem(PLUGIN_ID + ".action.revealInferredType", 
                        "Reveal Inferred &Types",
                        editor!=null && new RevealInferredTypeHandler().isEnabled(), REVEAL),
                new Separator(),	
                new DynamicMenuItem(PLUGIN_ID + ".action.pasteAsCeylon",
                		"&Paste Java as Ceylon",
                		true),		
                new Separator(),
                new DynamicMenuItem(ITextEditorActionDefinitionIds.QUICK_ASSIST, 
                        "&Quick Fix/Assist", 
                        true, FIX),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".editor.terminateStatement", 
                        "Terminate &Statement", 
                        true, TERMINATE),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".editor.correctIndentation", 
                        "Correct &Indentation", 
                        true),
                new DynamicMenuItem(PLUGIN_ID + ".editor.formatSource",
                        "&Format",
                        true),
                new DynamicMenuItem(PLUGIN_ID + ".editor.formatBlock", 
                        "Format &Block", 
                        true),
                new Separator(),
                new DynamicMenuItem(ITextEditorActionDefinitionIds.SHIFT_LEFT, 
                        "Shift &Left", 
                        true),
                new DynamicMenuItem(ITextEditorActionDefinitionIds.SHIFT_RIGHT, 
                        "Shift &Right", 
                        true),
                new Separator(),
                new DynamicMenuItem(PLUGIN_ID + ".editor.toggleComment", 
                        "Toggle &Comment", 
                        true),
                new DynamicMenuItem(PLUGIN_ID + ".editor.addBlockComment", 
                        "&Add Block Comment", 
                        true),
                new DynamicMenuItem(PLUGIN_ID + ".editor.removeBlockComment", 
                        "Re&move Block Comment", 
                        true),
                new Separator(),
                new DynamicMenuItem(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, 
                        "Content Proposals", 
                        true),
                new DynamicMenuItem(ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION, 
                        "Parameter Context Information", 
                        true)
        };
    }

}
