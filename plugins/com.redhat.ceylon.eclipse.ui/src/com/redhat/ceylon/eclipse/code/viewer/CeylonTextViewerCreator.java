package org.eclipse.ceylon.ide.eclipse.code.viewer;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

public class CeylonTextViewerCreator implements IViewerCreator {

    @Override
    public Viewer createViewer(Composite parent, CompareConfiguration config) {
        return new CeylonTextViewer(parent);
    }

}