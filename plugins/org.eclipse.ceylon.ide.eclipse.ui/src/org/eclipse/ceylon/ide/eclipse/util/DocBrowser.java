/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.util;

import static org.eclipse.jface.text.TextPresentation.applyTextPresentation;

import org.eclipse.jface.text.TextPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;

import org.eclipse.ceylon.ide.eclipse.code.html.HTMLTextPresenter;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;

public class DocBrowser {
    
    private TextPresentation presentation = 
            new TextPresentation();
    
    private Browser browser;
    private StyledText styledText;
    
    private String text;
    private boolean visible;

    private ProgressListener progressListener;
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (browser!=null) {
            browser.setVisible(visible);
        }
        if (styledText!=null) {
            styledText.setVisible(visible);
        }
    }
    
    public void addLocationListener(LocationListener listener) {
        if (browser!=null) {
            browser.addLocationListener(listener);
        }
    }

    public void setText(String text) {
        if (text!=null) {
            if (this.text==null || !text.equals(this.text)) {
                this.text = text;
                internalSetText(text);
            }
        }
        if (progressListener!=null && styledText!=null) {
            progressListener.completed(
                    new ProgressEvent(styledText));
        }
    }
    
    private void internalSetText(String text) {
        if (browser!=null) {
            browser.setText(text);
        }
        if (styledText!=null) {
            presentation.clear();
            Rectangle area = styledText.getClientArea();
            String content = 
                    new HTMLTextPresenter() //TODO: should be new HTMLTextPresenter(false) but that's crashing SWT for some reason
                        .updatePresentation(styledText, text, 
                                presentation, area.width-2, 
                                Integer.MAX_VALUE);
            styledText.setText(content);
            applyTextPresentation(presentation, styledText);
        }
    }
    
    public DocBrowser(Composite parent, int style) {
        Display display = parent.getDisplay();
        Color fg = display.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
        Color bg = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
        Font font = CeylonPlugin.getHoverFont();
        if (isAvailable(parent)) {
            browser = new Browser(parent, style);
            browser.setJavascriptEnabled(false);
            browser.setForeground(fg);
            browser.setBackground(bg);
            browser.setFont(font);
            if (parent.getLayout() instanceof GridLayout) {
                browser.setLayoutData(
                        new GridData(GridData.FILL_BOTH));
            }
            browser.addOpenWindowListener(
                    new OpenWindowListener() {
                @Override
                public void open(WindowEvent event) {
                    event.required = true; //Cancel opening of new windows
                }
            });
        }
        else {
            styledText = 
                    new StyledText(parent, 
                            SWT.MULTI | SWT.READ_ONLY | 
                            SWT.V_SCROLL | style);
            styledText.setForeground(fg);
            styledText.setBackground(bg);
            if (parent.getLayout() instanceof GridLayout) {
                styledText.setLayoutData(
                        new GridData(GridData.FILL_BOTH));
            }
            styledText.setFont(font);
            styledText.addControlListener(new ControlAdapter() {
                @Override
                public void controlResized(ControlEvent e) {
                    internalSetText(text);
                }
            });
        }
    }
    
    private static Boolean available;
    
    private static boolean isAvailable(Composite parent) {
        if (available==null) {
            try {
                Browser browser = 
                        new Browser(parent, SWT.NONE);
                browser.dispose();
                available = true;
            }
            catch (SWTError e) {
                available = false;
            }
        }
        return available.booleanValue();
    }

    public void close() {
        if (browser!=null) browser.close();
    }

    public void setMenu(Menu menu) {
        if (browser!=null) {
            browser.setMenu(menu);
        }
        if (styledText!=null) {
            styledText.setMenu(menu);
        }
    }

    public void addVisibilityWindowListener(
            final VisibilityWindowListener listener) {
        if (browser!=null) {
            browser.addVisibilityWindowListener(listener);
        }
        if (styledText!=null) {
            styledText.addListener(SWT.Show, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    listener.show(new WindowEvent(styledText));
                }
            });
            styledText.addListener(SWT.Hide, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    listener.hide(new WindowEvent(styledText));
                }
            });
        }
    }

    public void setProgressListener(ProgressListener listener) {
        this.progressListener = listener;
        if (browser!=null) {
            browser.addProgressListener(listener);
        }
    }

    public void setRedraw(boolean b) {
        if (browser!=null) {
            browser.setRedraw(b);
        }
        if (styledText!=null) {
            styledText.setRedraw(b);
        }
    }

    public Device getDisplay() {
        if (browser!=null) {
            return browser.getDisplay();
        }
        if (styledText!=null) {
            return styledText.getDisplay();
        }
        return null;
    }
    
    public Drawable getDrawable() {
        if (browser!=null) {
            return browser;
        }
        if (styledText!=null) {
            return styledText;
        }
        return null;
    }

    public void setForeground(Color foreground) {
        if (browser!=null) {
            browser.setForeground(foreground);
        }
        if (styledText!=null) {
            styledText.setForeground(foreground);
        }
    }

    public void setBackground(Color background) {
        if (browser!=null) {
            browser.setBackground(background);
        }
        if (styledText!=null) {
            styledText.setBackground(background);
        }
    }
}
