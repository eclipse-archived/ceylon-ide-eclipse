package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.getCurrentThemeColor;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class CeylonInitializerAnnotation extends AbstractAnnotationWithPatternPresentation {

    private final Position position;

    public CeylonInitializerAnnotation(String name, Position position) {
        this.position = position;

        setText("<b>Initializer section of " + name + "</b>" +
                "<p>" +
                "The initial part of the body of a class is called the initializer of the class and contains a mix of declarations, " +
                "statements and control structures. The initializer is executed every time the class is instantiated." +
                "</p>" +
                "<p>" +
                "Some rules are applied to prevent \"leaking\" uninitialized reference to a new instance, " +
                "for more information please check documentation." +
                "</p>");
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    protected Color getPatternForegroundColor() {
        return getCurrentThemeColor("initializerAnnotation");
    }

    @Override
    protected Color getPatternBackgroundColor() {
        return Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    }

}