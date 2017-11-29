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

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationPresentation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;

public class CeylonInitializerAnnotation 
        extends Annotation implements IAnnotationPresentation {

    private final Position initializerPosition;
    private final int depth;
    private StyledString styledString = new StyledString();
    
    public StyledString getStyledString() {
        return styledString;
    }

    static Font bold(Font font) {
        FontData data = font.getFontData()[0];
        return new Font(Display.getDefault(), 
                new FontData(data.getName(), 
                        data.getHeight(), 
                        SWT.BOLD));
    }
    
    public CeylonInitializerAnnotation(String name, 
            Position initializerPosition, int depth) {
        this.initializerPosition = initializerPosition;
        this.depth = depth;
        styledString.append("Initializer section of ", 
                new Styler() {
            @Override
            public void applyStyles(TextStyle textStyle) {
                textStyle.font = bold(CeylonPlugin.getHoverFont());
            }

        });
        styledString.append(name, 
                new Styler() {
            @Override
            public void applyStyles(TextStyle textStyle) {
                Highlights.TYPE_ID_STYLER.applyStyles(textStyle);
                textStyle.font = bold(CeylonPlugin.getEditorFont());
            }
        });
        styledString.append("\nThe initial part of the body of a class is called the initializer "
                + "of the class\nand contains executable code that initializes references.\n"
                + "The initializer is executed every time the class is instantiated.");

        setText(styledString.getString());
    }

    public Position getInitializerPosition() {
        return initializerPosition;
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
        /*if( !isCursorInBody() ) {
            return;
        }*/
        
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
                getCurrentThemeColor("initializerAnnotation");
        gc.setBackground(color);

        Image patternImage = getPatternImage(canvas, color);
        gc.setAlpha(90);
        gc.drawImage(patternImage, 0, 0, w, h, x, y, w, h);
        patternImage.dispose();
        
//        gc.setAlpha(50);
//        gc.fillRectangle(x, y, w, h);
        
        gc.setAlpha(255);
        gc.fillRectangle(x, bounds.y, w, 1);
        gc.fillRectangle(x, bounds.y + bounds.height - 1, w, 1);
    }

    public static Image getPatternImage(Control control, Color color) {
        Point size = control.getSize();
        Display display = control.getDisplay();
        Color bgColor = control.getBackground();

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
                imageData.setPixel(x, y, (x + y+1) % 2);
            }
        }

        return new Image(display, imageData);
    }

    /*private boolean isCursorInBody() {
        int caretOffset = editor.getCeylonSourceViewer().getTextWidget().getCaretOffset();
        if (caretOffset > bodyPosition.getOffset() && caretOffset < bodyPosition.getOffset() + bodyPosition.getLength()) {
            return true;
        }
        return false;
    }*/

}