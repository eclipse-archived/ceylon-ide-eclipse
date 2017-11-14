/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.modulesearch;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.ide.common.modulesearch.ModuleNode;
import org.eclipse.ceylon.ide.common.modulesearch.ModuleVersionNode;

public class ModuleSearchViewLabelProvider extends StyledCellLabelProvider {
    
    @Override
    public void update(ViewerCell cell) {
        if (cell.getElement() instanceof ModuleNode) {
            updateModuleNode(cell, (ModuleNode) cell.getElement());
        } else if (cell.getElement() instanceof ModuleVersionNode) {
            updateVersionNode(cell, (ModuleVersionNode) cell.getElement());
        }
        super.update(cell);
    }

    private void updateModuleNode(ViewerCell cell, ModuleNode moduleNode) {
        ModuleVersionNode lastVersion = moduleNode.getLastVersion();
    
        StyledString styledText = new StyledString();
        styledText.append(moduleNode.getName()); //really should be: CeylonLabelProvider.PACKAGE_STYLER
        styledText.append(" \"", Highlights.STRING_STYLER);
        styledText.append(lastVersion.getVersion(), Highlights.STRING_STYLER);
        styledText.append("\"", Highlights.STRING_STYLER);
    
//        if (lastVersion.getAuthors() != null && !lastVersion.getAuthors().isEmpty()) {
//            styledText.append(" (by ", StyledString.QUALIFIER_STYLER);
//            styledText.append(lastVersion.getAuthorsCommaSeparated(), StyledString.QUALIFIER_STYLER);
//            styledText.append(")", StyledString.QUALIFIER_STYLER);
//        }
//    
        cell.setText(styledText.toString());
        cell.setStyleRanges(styledText.getStyleRanges());
        cell.setImage(CeylonResources.MODULE);
    }

    private void updateVersionNode(ViewerCell cell, ModuleVersionNode versionNode) {
        StyledString styledText = new StyledString();
        styledText.append(" \"", Highlights.STRING_STYLER);
        styledText.append(versionNode.getVersion(), Highlights.STRING_STYLER);
        styledText.append("\"", Highlights.STRING_STYLER);
        cell.setText(styledText.toString());
        cell.setStyleRanges(styledText.getStyleRanges());
        cell.setImage(CeylonResources.VERSION);
    }

}