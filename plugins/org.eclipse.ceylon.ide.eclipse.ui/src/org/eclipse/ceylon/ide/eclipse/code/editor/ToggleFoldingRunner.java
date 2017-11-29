/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * Runner that will toggle folding either instantly (if the editor is
 * visible) or the next time it becomes visible. If a runner is started when
 * there is already one registered, the registered one is canceled as
 * toggling folding twice is a no-op.
 * <p>
 * The access to the fFoldingRunner field is not thread-safe, it is assumed
 * that <code>runWhenNextVisible</code> is only called from the UI thread.
 * </p>
 *
 * @since 3.1
 */
final class ToggleFoldingRunner implements IPartListener2 {
    /**
     * 
     */
    private final CeylonEditor editor;

    /**
     * @param ceylonEditor
     */
    ToggleFoldingRunner(CeylonEditor ceylonEditor) {
        editor = ceylonEditor;
    }
    /**
     * The workbench page we registered the part listener with, or
     * <code>null</code>.
     */
    private IWorkbenchPage fPage;

    /**
     * Does the actual toggling of projection.
     */
    private void toggleFolding() {
        ProjectionViewer pv= editor.getCeylonSourceViewer();
        if (pv.isProjectionMode() != editor.isFoldingEnabled()) {
            if (pv.canDoOperation(ProjectionViewer.TOGGLE)) {
                pv.doOperation(ProjectionViewer.TOGGLE);
            }
        }
    }

    /**
     * Makes sure that the editor's folding state is correct the next time
     * it becomes visible. If it already is visible, it toggles the folding
     * state. If not, it either registers a part listener to toggle folding
     * when the editor becomes visible, or cancels an already registered
     * runner.
     */
    public void runWhenNextVisible() {
        // if there is one already: toggling twice is the identity
        if (editor.fFoldingRunner != null) {
            editor.fFoldingRunner.cancel();
            return;
        }
        IWorkbenchPartSite site= editor.getSite();
        if (site != null) {
            IWorkbenchPage page= site.getPage();
            if (!page.isPartVisible(editor)) {
                // if we're not visible - defer until visible
                fPage= page;
                editor.fFoldingRunner= this;
                page.addPartListener(this);
                return;
            }
        }
        // we're visible - run now
        toggleFolding();
    }

    /**
     * Remove the listener and clear the field.
     */
    private void cancel() {
        if (fPage != null) {
            fPage.removePartListener(this);
            fPage= null;
        }
        if (editor.fFoldingRunner == this)
            editor.fFoldingRunner= null;
    }

    /*
     * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partVisible(IWorkbenchPartReference partRef) {
        if (editor.equals(partRef.getPart(false))) {
            cancel();
            toggleFolding();
        }
    }

    /*
     * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.IWorkbenchPartReference)
     */
    public void partClosed(IWorkbenchPartReference partRef) {
        if (editor.equals(partRef.getPart(false))) {
            cancel();
        }
    }

    public void partActivated(IWorkbenchPartReference partRef) {}
    public void partBroughtToTop(IWorkbenchPartReference partRef) {}
    public void partDeactivated(IWorkbenchPartReference partRef) {}
    public void partOpened(IWorkbenchPartReference partRef) {}
    public void partHidden(IWorkbenchPartReference partRef) {}
    public void partInputChanged(IWorkbenchPartReference partRef) {}
}