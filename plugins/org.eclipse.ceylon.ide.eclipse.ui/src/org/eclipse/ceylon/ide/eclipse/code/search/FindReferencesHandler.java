package org.eclipse.ceylon.ide.eclipse.code.search;

import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentSearchResultPage;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.search.ui.ISearchResultPage;

public class FindReferencesHandler extends AbstractHandler {
        
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
            new FindReferencesAction(p, selection).run();
        }
        else {
            new FindReferencesAction(getCurrentEditor()).run();
        }
        return null;
    }
            
}
