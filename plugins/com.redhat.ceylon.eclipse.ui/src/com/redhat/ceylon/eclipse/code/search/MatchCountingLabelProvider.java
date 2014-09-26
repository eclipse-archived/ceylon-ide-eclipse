package com.redhat.ceylon.eclipse.code.search;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;

import com.redhat.ceylon.eclipse.util.Highlights;

final class MatchCountingLabelProvider extends SearchResultsLabelProvider {
    
    private final AbstractTextSearchViewPage page;

    MatchCountingLabelProvider(AbstractTextSearchViewPage page) {
        this.page = page;
    }

    @Override
    public StyledString getStyledText(Object element) {
        StyledString label = super.getStyledText(element);
        int matchCount = page.getDisplayedMatchCount(element);
        if (matchCount>1) {
            return label.append(" (" + matchCount + " matches)", 
                    Highlights.ARROW_STYLER);
        }
        /*else if (element instanceof CeylonElement) {
            return label.append(":" + ((CeylonElement)element).getLocation(), 
                    StyledString.COUNTER_STYLER);
        }*/
        return label;
    }
    
}