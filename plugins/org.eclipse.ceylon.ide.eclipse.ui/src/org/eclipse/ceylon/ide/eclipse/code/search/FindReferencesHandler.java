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
