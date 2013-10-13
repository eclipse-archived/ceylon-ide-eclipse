package com.redhat.ceylon.eclipse.code.hover;

import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getHoverInfo;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getLinkedModel;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.internalGetHoverInfo;
import static org.eclipse.jdt.ui.PreferenceConstants.APPEARANCE_JAVADOC_FONT;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Region;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.ViewPart;

import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;

public class DocumentationView extends ViewPart {
    
    private static DocumentationView instance;
    
    public static DocumentationView getInstance() {
        return instance;
    }
    
    public DocumentationView() {
        instance = this;
    }
    
    private Browser control;
    private CeylonEditor editor;
    private CeylonBrowserInput info;
    
    @Override
    public void createPartControl(Composite parent) {
        control = new Browser(parent, SWT.NONE); 
        control.setJavascriptEnabled(false);
        Display display = getSite().getShell().getDisplay();
        Color fg = display.getSystemColor(SWT.COLOR_INFO_FOREGROUND);
        Color bg = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
        control.setForeground(fg);
        control.setBackground(bg);
        parent.setForeground(fg);
        parent.setBackground(bg);
        FontData fontData = JFaceResources.getFontRegistry()
                .getFontData(APPEARANCE_JAVADOC_FONT)[0];
        control.setFont(new Font(Display.getDefault(), fontData));
        control.addLocationListener(new LocationListener() {
            @Override
            public void changing(LocationEvent event) {
                String location = event.location;
                
                //necessary for windows environment (fix for blank page)
                //somehow related to this: https://bugs.eclipse.org/bugs/show_bug.cgi?id=129236
                if (!"about:blank".equals(location)) {
                    event.doit = false;
                }
                
                if (location.startsWith("dec:")) {
                    Referenceable target = getLinkedModel(info, editor, location);
                    if (target!=null) {
                        gotoDeclaration(editor, target);
                    }
                }
                else if (location.startsWith("doc:")) {
                    Referenceable target = getLinkedModel(info, editor, location);
                    if (target!=null) {
                        info = getHoverInfo(target, info, editor, null);
                        if (info!=null) control.setText(info.getHtml());
                    }
                }
            }
            @Override
            public void changed(LocationEvent event) {}
        });
    }

    @Override
    public void setFocus() {}
    
    public void update(CeylonEditor editor, int offset, int length) { 
        this.editor = editor;
        info = internalGetHoverInfo(editor, new Region(offset, length));
        if (info!=null && info.getAddress()!=null) {
            control.setText(info.getHtml());
        }
    }
    
    @Override
    public void dispose() {
        instance = null;
        super.dispose();
    }

}
