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

import static org.eclipse.ceylon.ide.eclipse.code.complete.CodeCompletions.getQualifiedDescriptionFor;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static org.eclipse.ceylon.ide.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_OUTLINES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_OUTLINES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_OUTLINES;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_OUTLINES;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.MULTIPLE_TYPES_IMAGE;
import static org.eclipse.ceylon.ide.eclipse.util.Highlights.PACKAGE_STYLER;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Font;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.typechecker.model.ClassOrInterface;
import org.eclipse.ceylon.model.typechecker.model.Declaration;

abstract class CeylonHierarchyLabelProvider 
        extends StyledCellLabelProvider {
        
    @Override
    public void removeListener(ILabelProviderListener listener) {}

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void dispose() {}

    @Override
    public void addListener(ILabelProviderListener listener) {}

    Font getFont() {
        return null;
    }

    String getPrefix() {
        return null;
    }

    private StyledString getStyledText(CeylonHierarchyNode n) {
        Declaration dec = getDisplayedDeclaration(n);
        if (dec==null) {
        	return new StyledString();
        }
        IPreferenceStore prefs = CeylonPlugin.getPreferences();
        StyledString result;
        if (dec.isNamed()) {
            result = 
                    getQualifiedDescriptionFor(dec, 
                        prefs.getBoolean(TYPE_PARAMS_IN_OUTLINES),
                        prefs.getBoolean(PARAMS_IN_OUTLINES),
                        prefs.getBoolean(PARAM_TYPES_IN_OUTLINES),
                        prefs.getBoolean(RETURN_TYPES_IN_OUTLINES),
                        getPrefix(), getFont());
        }
        else {
            result =
                    new StyledString()
                        .append("anonymous ")
                        .append("object", Highlights.KW_STYLER)
                        .append(" expression");
        }
        /*if (d.isClassOrInterfaceMember()) {
            Declaration container = (Declaration) d.getContainer();
            result.append(" in ")
                  .append(container.getName(), Highlights.TYPE_ID_STYLER);
        }*/
        result.append(" \u2014 ", PACKAGE_STYLER)
              .append(getPackageLabel(dec), PACKAGE_STYLER);
        if (n.isNonUnique()) {
            result.append(" \u2014 and other supertypes")
                  .append(getViewInterfacesShortcut());
        }
        return result;
    }

    String getViewInterfacesShortcut() {
        return "";
    }
    
    abstract boolean isShowingRefinements();

    Declaration getDisplayedDeclaration(CeylonHierarchyNode node) {
        Declaration declaration = node.getDeclaration();
        if (declaration!=null && 
                isShowingRefinements() && 
                declaration.isClassOrInterfaceMember()) {
            declaration = 
                    (ClassOrInterface) 
                        declaration.getContainer();
        }
        return declaration;
    }
    
    @Override
    public void update(ViewerCell cell) {
        CeylonHierarchyNode n = 
                (CeylonHierarchyNode) cell.getElement();
        if (n.isMultiple()) {
            cell.setText("multiple supertypes" + 
                    getViewInterfacesShortcut());
            cell.setStyleRanges(new StyleRange[0]);
            cell.setImage(MULTIPLE_TYPES_IMAGE);
        }
        else {
            StyledString styledText = getStyledText(n);
            cell.setText(styledText.toString());
            cell.setStyleRanges(styledText.getStyleRanges());
            cell.setImage(getImageForDeclaration(
                    getDisplayedDeclaration(n),
                    n.isFocus()));
        }
        super.update(cell);
    }
}