package com.redhat.ceylon.eclipse.code.editor;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

public class CeylonInitializerAnnotation extends Annotation implements IAnnotationPresentation {
    
    private Position position;
    private Color patternColor;
    private Image patternImage;

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
        
        gc.drawImage(getPatternImage(canvas), 0, 0, w, h, x, y, w, h);
        gc.setBackground(patternColor);
        gc.fillRectangle(x, bounds.y, w, 1);
        gc.fillRectangle(x, bounds.y + bounds.height - 1, w, 1);
    }
    
    private Image getPatternImage(Control control) {
        if (patternImage == null) {
            initPattern(control.getDisplay(), control.getSize());
            control.addDisposeListener(new DisposeListener() {
                @Override
                public void widgetDisposed(DisposeEvent e) {
                    disposePattern();
                }
            });
        } else {
            Rectangle imageRectangle = patternImage.getBounds();
            Point controlSize = control.getSize();
            if (imageRectangle.width < controlSize.x || imageRectangle.height < controlSize.y) {
                disposePattern();
                initPattern(control.getDisplay(), controlSize);
            }
        }
        return patternImage;
    }

    private void initPattern(Display display, Point size) {
        Color patternColor1 = new Color(display, 173, 216, 230);
        Color patternColor2 = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        RGB rgbs[] = new RGB[] {
                new RGB(patternColor1.getRed(), patternColor1.getGreen(), patternColor1.getBlue()),
                new RGB(patternColor2.getRed(), patternColor2.getGreen(), patternColor2.getBlue()) };

        ImageData imageData = new ImageData(size.x, size.y, 1, new PaletteData(rgbs));

        for (int y = 0; y < size.y; y++) {
            for (int x = 0; x < size.x; x++) {
                imageData.setPixel(x, y, (x + y) % 2);
            }
        }

        patternColor = patternColor1;
        patternImage = new Image(display, imageData);
    }

    private void disposePattern() {
        if (patternImage != null && !patternImage.isDisposed()) {
            patternImage.dispose();
            patternImage = null;
        }
        if (patternColor != null && !patternColor.isDisposed()) {
            patternColor.dispose();
            patternColor = null;
        }
    }

}