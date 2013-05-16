package com.redhat.ceylon.eclipse.code.editor;

import static com.redhat.ceylon.eclipse.code.parse.CeylonTokenColorer.getCurrentThemeColor;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class CeylonInitializerAnnotation extends Annotation implements IAnnotationPresentation {

    private final Position position;
    private final int depth;

    public CeylonInitializerAnnotation(String name, Position position, int depth) {
        this.position = position;
        this.depth = depth;

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
    
    public int getDepth() {
        return depth;
    }

    @Override
    public int getLayer() {
        return 1;
    }
    
    @Override
    public void paint(GC gc, Canvas canvas, Rectangle bounds) {
        Point canvasSize = canvas.getSize();
        int x = 0;
        int y = bounds.y;
        int w = canvasSize.x;
        int h = bounds.height;

        if (y + h > canvasSize.y) {
            h = canvasSize.y - y;
        }
        if (y < 0) {
            h = h + y;
            y = 0;
        }
        if (h <= 0) {
            return;
        }

        Color color = getCurrentThemeColor("initializerAnnotation");
        gc.setBackground(color);
        gc.setAlpha(70);
        gc.fillRectangle(x, y, w, h);
        gc.setAlpha(255);
        gc.fillRectangle(x, bounds.y, w, 1);
        gc.fillRectangle(x, bounds.y + bounds.height - 1, w, 1);
    }

}