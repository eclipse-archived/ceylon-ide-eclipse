/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import java.util.Collections;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

public class DynamicMenuItem extends CommandContributionItem {
    
    private boolean enabled;
    
    public DynamicMenuItem(String id, String label, boolean enabled) {
        super(new CommandContributionItemParameter(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
                id + ".cci", id, Collections.emptyMap(), null, null, null, 
                label, null, null, CommandContributionItem.STYLE_PUSH, null, 
                false));
        this.enabled = enabled;
    }
    
    public DynamicMenuItem(String id, String label, boolean enabled, 
            ImageDescriptor image) {
        super(new CommandContributionItemParameter(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
                id + ".cci", id, Collections.emptyMap(), image, null, null, 
                label, null, null, CommandContributionItem.STYLE_PUSH, null, 
                false));
        this.enabled = enabled;
    }
    
    @Override
    public boolean isEnabled() { 
        return super.isEnabled() && enabled; 
    }
    
    public static boolean collapseMenuItems(IContributionManager parent) {
        return isContextMenu(parent) /*&& 
                Display.getCurrent().getBounds().height < 2048*/;
    }
    
    static boolean isContextMenu(IContributionManager parent) {
        return parent instanceof IContributionItem && 
                ((IContributionItem) parent).getId().equals("#TextEditorContext");
    }

}