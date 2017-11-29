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

import static org.eclipse.ceylon.ide.eclipse.util.Highlights.getCurrentThemeColor;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.swt.SWT;
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

public class CeylonRangeIndicator 
        extends Annotation implements IAnnotationPresentation {
    
    /*private StyledString styledString = new StyledString();
    
    public StyledString getStyledString() {
        return styledString;
    }*/

    CeylonRangeIndicator() {
        /*styledString.append("Current declaration body", 
                new Styler() {
            @Override
            public void applyStyles(TextStyle textStyle) {
                textStyle.font = bold(getHoverFont());
            }

        });
        setText(styledString.getString());*/
    }

    @Override
    public int getLayer() {
        return DEFAULT_LAYER;
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

        Color color = 
                getCurrentThemeColor("rangeIndicatorAnnotation");
        gc.setBackground(color);
        
        Image patternImage = getPatternImage(canvas, color);
        gc.drawImage(patternImage, 0, 0, w, h, x, y, w, h);
        patternImage.dispose();
        
//        gc.setAlpha(85);
//        gc.fillRectangle(x, y, w, h);
        
        gc.setAlpha(255);
        gc.fillRectangle(x, bounds.y, w, 1);
        gc.fillRectangle(x, bounds.y + bounds.height - 1, w, 1);
    }

    public static Image getPatternImage(Control control, Color color) {
        Point size = control.getSize();
        Display display = control.getDisplay();
        Color bgColor = 
                Display.getCurrent()
                    .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

        RGB rgbs[] = new RGB[] {
                new RGB(color.getRed(), 
                        color.getGreen(), 
                        color.getBlue()),
                new RGB(bgColor.getRed(), 
                        bgColor.getGreen(), 
                        bgColor.getBlue()) };

        ImageData imageData = 
                new ImageData(size.x, size.y, 1, 
                        new PaletteData(rgbs));

        for (int y = 0; y < size.y; y++) {
            for (int x = 0; x < size.x; x++) {
                imageData.setPixel(x, y, (x + y) % 2);
            }
        }

        return new Image(display, imageData);
    }

}