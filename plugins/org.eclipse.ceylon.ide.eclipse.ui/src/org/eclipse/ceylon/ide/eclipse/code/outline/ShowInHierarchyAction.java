/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.outline;

import static org.eclipse.ceylon.ide.eclipse.code.outline.HierarchyView.showHierarchyView;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.util.Nodes;

public class ShowInHierarchyAction extends Action implements IObjectActionDelegate {
    
    private IWorkbenchPartSite site;
    protected Declaration declaration;
    private ContentOutline outlineView;
    
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (outlineView==null) return;
        try {
            final ITreeSelection oultineSelection = 
                    (ITreeSelection) outlineView.getSelection();
            CeylonOutlineNode on = 
                    (CeylonOutlineNode) oultineSelection.getFirstElement();
            if (on!=null) {
                IEditorPart currentEditor = getCurrentEditor();
                if (currentEditor instanceof CeylonEditor) {
                    CeylonParseController parseController = 
                            ((CeylonEditor) currentEditor).getParseController();
                    Tree.CompilationUnit rootNode = 
                            parseController.getLastCompilationUnit();
                    if (rootNode!=null) {
                        Node node = 
                                Nodes.findNode(rootNode, 
                                        on.getStartOffset(),
                                        on.getEndOffset());
                        if (node instanceof Tree.Declaration) {
                            declaration = 
                                    ((Tree.Declaration) node).getDeclarationModel();
                            action.setEnabled(isValidSelection());
                            return; //early exit
                        }
                    }
                }
            }
            declaration=null;
            action.setEnabled(false);
        }
        catch (Exception e) {
            action.setEnabled(false);
        }
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        outlineView = (ContentOutline) targetPart;
        site = targetPart.getSite();
    }
    
    @Override
    public void run(IAction action) {
        run();
    }
    
    @Override
    public void run() {
        if (isValidSelection()) {
            try {
                showHierarchyView().focusOn(declaration);
            }
            catch (PartInitException e) {
                e.printStackTrace();
            }
        }
        else {
            MessageDialog.openWarning(site.getShell(), 
                    "Ceylon Find Error", 
                    "No appropriate declaration name selected");
        }
    }
    
    private boolean isValidSelection() {
        return declaration!=null;
    }

}
