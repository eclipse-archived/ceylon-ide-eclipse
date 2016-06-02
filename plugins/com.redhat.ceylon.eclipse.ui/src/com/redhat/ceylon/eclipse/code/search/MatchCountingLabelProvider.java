package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.FULL_LOC_SEARCH_RESULTS;
import static org.eclipse.search.ui.text.AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Highlights;

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