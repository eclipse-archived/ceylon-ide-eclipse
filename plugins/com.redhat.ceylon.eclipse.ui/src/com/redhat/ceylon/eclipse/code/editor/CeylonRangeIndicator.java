package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.getCurrentThemeColor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class CeylonRangeIndicator extends AbstractAnnotationWithPatternPresentation {

    @Override
    protected Color getPatternForegroundColor() {
        return getCurrentThemeColor("rangeIndicatorAnnotation");
    }

    @Override
    protected Color getPatternBackgroundColor() {
        return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    }

}