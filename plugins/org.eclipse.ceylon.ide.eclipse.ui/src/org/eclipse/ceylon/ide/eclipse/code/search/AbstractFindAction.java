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

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getUnit;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getCurrentEditor;
import static org.eclipse.ceylon.ide.eclipse.util.EditorUtil.getProject;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.findNode;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedExplicitDeclaration;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import org.eclipse.ceylon.compiler.typechecker.io.VirtualFile;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.code.editor.CeylonEditor;
import org.eclipse.ceylon.ide.eclipse.code.outline.CeylonOutlineNode;
import org.eclipse.ceylon.ide.common.model.CeylonUnit;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;

abstract class AbstractFindAction extends Action 
        implements IObjectActionDelegate {
    
    private Shell shell;
    protected Referenceable declaration;
    protected IProject project;
    private ContentOutline outlineView;
    
    AbstractFindAction(String name) {
        super(name);
    }
    
    AbstractFindAction(String name, 
            CeylonSearchResultPage page, 
            ISelection selection) {
        super(name);
        shell = page.getSite().getShell();
        IStructuredSelection ss = 
                (IStructuredSelection) selection;
        Object firstElement = ss.getFirstElement();
        if (firstElement instanceof CeylonElement) {
            CeylonElement element = 
                    (CeylonElement) firstElement;
            VirtualFile vf = element.getVirtualFile();
            if (vf!=null) {
                CeylonUnit unit = getUnit(vf);
                Tree.CompilationUnit rn = 
                        unit.getCompilationUnit();
                Node node = 
                        findNode(rn,
                                element.getStartOffset(), 
                                element.getEndOffset());
                if (node instanceof Tree.Declaration) {
                    Tree.Declaration d = 
                            (Tree.Declaration) node;
                    declaration = d.getDeclarationModel();
                }
            }
        }
    }
    
    @Override
    public void selectionChanged(IAction action, 
            ISelection selection) {
        if (outlineView==null) return;
        try {
            ITreeSelection ts = 
                    (ITreeSelection) 
                        outlineView.getSelection();
            CeylonOutlineNode on = 
                    (CeylonOutlineNode) 
                        ts.getFirstElement();
            if (on!=null) {
                IEditorPart editor = getCurrentEditor();
                if (editor instanceof CeylonEditor) {
                    CeylonEditor ce = 
                            (CeylonEditor) editor;
                    Tree.CompilationUnit rootNode = 
                            ce.getParseController()
                                .getLastCompilationUnit();
                    if (rootNode!=null) {
                        Node node = 
                                findNode(rootNode, 
                                        on.getStartOffset(),
                                        on.getEndOffset());
                        if (node instanceof Tree.Declaration) {
                            Tree.Declaration d = 
                                    (Tree.Declaration) node;
                            declaration = 
                                    d.getDeclarationModel();
                        }
                        else if (node instanceof Tree.ImportPath) {
                            Tree.ImportPath ip = 
                                    (Tree.ImportPath) node;
                            declaration = ip.getModel();
                        }
                        else {
                            return; //early exit
                        }
                        project = getProject(editor);
                        action.setEnabled(isValidSelection());
                    }
                }
            }
            action.setEnabled(false);
        }
        catch (Exception e) {
            action.setEnabled(false);
        }
    }

    @Override
    public void setActivePart(IAction action, 
            IWorkbenchPart targetPart) {
        outlineView = (ContentOutline) targetPart;
        shell = targetPart.getSite().getShell();
    }
    
    AbstractFindAction(String text, IEditorPart editor) {
        super(text);
        shell = editor.getSite().getShell();
        project = editor==null ? null : getProject(editor);
        if (editor instanceof CeylonEditor) {
            CeylonEditor ce = (CeylonEditor) editor;
            declaration = 
                    getReferencedExplicitDeclaration(
                            ce.getSelectedNode(), 
                            ce.getParseController()
                                .getLastCompilationUnit());
            setEnabled(isValidSelection());
        }
        else {
            setEnabled(false);
        }
    }
    
    AbstractFindAction(String text, IEditorPart editor, 
            Referenceable dec) {
        super(text);
        shell = editor.getSite().getShell();
        project = editor==null ? null : getProject(editor);
        declaration = dec;
        setEnabled(true);
    }
    
    @Override
    public void run(IAction action) {
        run();
    }
    
    @Override
    public void run() {
        if (isValidSelection()) {
            NewSearchUI.runQueryInBackground(
                    createSearchQuery());
        }
        else {
            MessageDialog.openWarning(shell, 
                    "Ceylon Find Error", 
                    "No appropriate declaration name selected");
        }
    }

    abstract boolean isValidSelection();

    public abstract FindSearchQuery createSearchQuery();
    
}
