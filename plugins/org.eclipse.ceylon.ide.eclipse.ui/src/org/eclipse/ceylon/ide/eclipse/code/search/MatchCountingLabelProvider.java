/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.FULL_LOC_SEARCH_RESULTS;
import static org.eclipse.search.ui.text.AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;

final class MatchCountingLabelProvider extends SearchResultsLabelProvider {
    
    private final AbstractTextSearchViewPage page;

    MatchCountingLabelProvider(AbstractTextSearchViewPage page) {
        this.page = page;
    }
    
    @Override
    boolean appendMatchPackage() {
        return page.getLayout()==FLAG_LAYOUT_FLAT;
    }
    
    @Override
    boolean appendSourceLocation() {
        return CeylonPlugin.getPreferences()
                .getBoolean(FULL_LOC_SEARCH_RESULTS);
    }

    @Override
    public StyledString getStyledText(Object element) {
        StyledString label = super.getStyledText(element);
        int matchCount = page.getDisplayedMatchCount(element);
        if (matchCount>1) {
            return new StyledString().append(label)
                    .append(" (" + matchCount + " matches)", 
                            Highlights.ARROW_STYLER);
        }
        else {
            return label;
        }
    }
    
}