/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.RulerBreakpointAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;

import org.eclipse.ceylon.ide.eclipse.core.external.ExternalSourceArchiveManager;

public class RulerEnableDisableBreakpointAction extends RulerBreakpointAction implements IUpdate, MouseListener {
    private IBreakpoint fBreakpoint;
    
    public RulerEnableDisableBreakpointAction(ITextEditor editor, IVerticalRulerInfo info) {
        super(editor, info);
        setText("&Disable Breakpoint");
        update();

        Control control= info.getControl();

        if (control != null && !control.isDisposed())
            control.addMouseListener(this);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.action.Action#run()
     */
    public void run() {
        if (fBreakpoint != null) {
            try {
                fBreakpoint.setEnabled(!fBreakpoint.isEnabled());
                if (fBreakpoint.getMarker() != null && 
                        ExternalSourceArchiveManager.isInSourceArchive(fBreakpoint.getMarker().getResource())) {
                    // Necessary since the breakpoint marker deltas will not be seen by the BreakPointManagerVisitor since 
                    // it ignores the hidden resources, and the fake project used for source archive folders *is* hidden.
                    DebugPlugin.getDefault().getBreakpointManager().fireBreakpointChanged(fBreakpoint);
                }
            } catch (CoreException e) {
                ErrorDialog.openError(getEditor().getSite().getShell(), "Error", 
                        "Failed to toggle breakpoint enablement", e.getStatus());
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.texteditor.IUpdate#update()
     */
    public void update() {
        fBreakpoint = getBreakpoint();
        setEnabled(fBreakpoint != null);
        if (fBreakpoint != null) {
            try {
                if (fBreakpoint.isEnabled()) {
                    setText("&Disable Breakpoint");
                } else {
                    setText("&Enable Breakpoint");
                }
            } catch (CoreException e) {
            }
        } else {
            setText("&Disable Breakpoint");
        }
    }

    public void mouseDoubleClick(MouseEvent e) { }

    public void mouseDown(MouseEvent e) {
        update();
    }

    public void mouseUp(MouseEvent e) { }
}
