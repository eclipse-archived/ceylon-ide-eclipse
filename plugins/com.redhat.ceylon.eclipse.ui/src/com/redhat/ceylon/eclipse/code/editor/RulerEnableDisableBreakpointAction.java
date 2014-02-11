package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.RulerBreakpointAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;

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
