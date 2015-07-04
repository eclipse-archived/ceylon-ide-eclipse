package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.FULL_LOC_SEARCH_RESULTS;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getPreferences;
import static org.eclipse.search.ui.text.AbstractTextSearchViewPage.FLAG_LAYOUT_FLAT;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

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
        return getPreferences()
                .getBoolean(FULL_LOC_SEARCH_RESULTS);
    }

    @Override
    public StyledString getStyledText(Object element) {
        StyledString label = super.getStyledText(element);
        int matchCount = page.getDisplayedMatchCount(element);
        if (matchCount>1) {
            label.append(" (" + matchCount + " matches)", 
                    Highlights.ARROW_STYLER);
        }
        /*else if (element instanceof CeylonElement) {
            return label.append(":" + ((CeylonElement)element).getLocation(), 
                    StyledString.COUNTER_STYLER);
        }*/
//        for (StyleRange range: label.getStyleRanges()) {
//            range.font = CeylonPlugin.getOutlineFont();
//        }
        return label;
    }
    
}