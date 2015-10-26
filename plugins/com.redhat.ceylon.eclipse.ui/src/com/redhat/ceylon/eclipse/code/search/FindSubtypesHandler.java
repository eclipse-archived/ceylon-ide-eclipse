package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentSearchResultPage;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchResultPage;

public class FindSubtypesHandler extends AbstractHandler {
    
    @Override
    public Object execute(ExecutionEvent event) 
            throws ExecutionException {
        ISearchResultPage page = getCurrentSearchResultPage();
        if (page instanceof CeylonSearchResultPage) {
            CeylonSearchResultPage p = 
                    (CeylonSearchResultPage) page;
            IStructuredSelection selection = 
                    (IStructuredSelection) 
                    page.getUIState();
            new FindSubtypesAction(p, selection).run();
        }
        else {
            new FindSubtypesAction(getCurrentEditor()).run();
        }
        return null;
    }
            
}
