package org.eclipse.ceylon.ide.eclipse.code.viewer;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;


/**
 * Required when creating a JavaMergeViewer from the plugin.xml file.
 */
public class CeylonContentViewerCreator implements IViewerCreator {

    public Viewer createViewer(Composite parent, CompareConfiguration mp) {
        return new CeylonMergeViewer(parent, SWT.NULL, mp);
    }
}
