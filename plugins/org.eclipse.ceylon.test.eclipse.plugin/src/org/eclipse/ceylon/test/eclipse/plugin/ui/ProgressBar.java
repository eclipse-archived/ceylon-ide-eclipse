/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.test.eclipse.plugin.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import org.eclipse.ceylon.test.eclipse.plugin.model.TestRun;

public class ProgressBar extends Canvas {

    private static final int DEFAULT_WIDTH = 160;
    private static final int DEFAULT_HEIGHT = 18;

    private Color successColor;
    private Color failureColor;
    private Color interruptedColor;

    private boolean isSuccess;
    private boolean isInterrupted;
    private int value;
    private int total;

    public ProgressBar(Composite parent) {
        super(parent, SWT.NONE);
        initColors();
        initPaintListener();
        initDisposeListener();
    }

    private void initColors() {
        Display display = getDisplay();
        successColor = new Color(display, 95, 191, 95);
        failureColor = new Color(display, 159, 63, 63);
        interruptedColor = new Color(display, 120, 120, 120);
    }

    private void initPaintListener() {
        addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                paint(e);
            }
        });
    }

    private void initDisposeListener() {
        addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                successColor.dispose();
                failureColor.dispose();
                interruptedColor.dispose();
            }
        });
    }

    public void updateView(TestRun currentTestRun) {
        if (currentTestRun != null) {
            isSuccess = currentTestRun.isSuccess();
            isInterrupted = currentTestRun.isInterrupted();
            if (currentTestRun.isRunning()) {
                value = currentTestRun.getFinishedCount();
                total = currentTestRun.getTotalCount();
            } else {
                value = currentTestRun.getTotalCount();
                total = currentTestRun.getTotalCount();
            }
        } else {
            isSuccess = false;
            isInterrupted = false;
            value = 0;
            total = 0;
        }
        redraw();
    }

    private void paint(PaintEvent e) {
        Rectangle r = getClientArea();
        e.gc.fillRectangle(r);
        paintBorder(e.gc, r);
        paintBar(e.gc, r);
    }

    private void paintBorder(GC gc, Rectangle r) {
        Display display = getDisplay();
        Color topleft = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
        Color bottomright = display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);

        int w = r.width - 1;
        int h = r.height - 1;

        gc.setForeground(topleft);
        gc.drawLine(r.x, r.y, r.x + w - 1, r.y);
        gc.drawLine(r.x, r.y, r.x, r.y + h - 1);

        gc.setForeground(bottomright);
        gc.drawLine(r.x + w, r.y, r.x + w, r.y + h);
        gc.drawLine(r.x, r.y + h, r.x + w, r.y + h);
    }

    private void paintBar(GC gc, Rectangle r) {
        int barWidth = computeBarWidth(r);
        Color barColor = computeBarColor();
        gc.setBackground(barColor);
        gc.fillRectangle(1, 1, barWidth, r.height - 2);
    }

    private Color computeBarColor() {
        if (isInterrupted) {
            return interruptedColor;
        } else if (isSuccess) {
            return successColor;
        } else {
            return failureColor;
        }
    }

    private int computeBarWidth(Rectangle r) {
        int barWidth = 0;
        if (value > 0 && total > 0 && r.width > 0) {
            barWidth = Math.min(r.width - 2, Math.max(0, value * (r.width - 2) / total));
        }
        return barWidth;
    }

    @Override
    public Point computeSize(int wHint, int hHint, boolean changed) {
        checkWidget();
        Point size = new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        if (wHint != SWT.DEFAULT) {
            size.x = wHint;
        }
        if (hHint != SWT.DEFAULT) {
            size.y = hHint;
        }
        return size;
    }

}