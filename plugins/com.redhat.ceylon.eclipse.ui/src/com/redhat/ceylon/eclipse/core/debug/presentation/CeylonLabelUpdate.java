package com.redhat.ceylon.eclipse.core.debug.presentation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

public final class CeylonLabelUpdate implements ILabelUpdate {
    private final ILabelUpdate update;
    private IPresentationContext presentationContext;

    public CeylonLabelUpdate(ILabelUpdate update) {
        this.update = update;
        this.presentationContext = CeylonPresentationContext.toCeylonContextIfNecessary(update.getPresentationContext(), this);
    }

    @Override
    public IPresentationContext getPresentationContext() {
        return presentationContext;
    }

    @Override
    public Object getElement() {
        return update.getElement();
    }

    @Override
    public TreePath getElementPath() {
        return update.getElementPath();
    }

    @Override
    public Object getViewerInput() {
        return update.getViewerInput();
    }

    @Override
    public void setStatus(IStatus status) {
        update.setStatus(status);
    }

    @Override
    public IStatus getStatus() {
        return update.getStatus();
    }

    @Override
    public void done() {
        update.done();
    }

    @Override
    public void cancel() {
        update.cancel();
    }

    @Override
    public boolean isCanceled() {
        return update.isCanceled();
    }

    @Override
    public String[] getColumnIds() {
        return update.getColumnIds();
    }

    @Override
    public void setLabel(String text, int columnIndex) {
        update.setLabel(text, columnIndex);
    }

    @Override
    public void setFontData(FontData fontData, int columnIndex) {
        update.setFontData(fontData, columnIndex);
    }

    @Override
    public void setImageDescriptor(ImageDescriptor image,
            int columnIndex) {
        update.setImageDescriptor(image, columnIndex);
    }

    @Override
    public void setForeground(RGB foreground, int columnIndex) {
        update.setForeground(foreground, columnIndex);
    }

    @Override
    public void setBackground(RGB background, int columnIndex) {
        update.setBackground(background, columnIndex);
    }
}