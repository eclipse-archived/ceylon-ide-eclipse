package com.redhat.ceylon.eclipse.imp.editorActionContributions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.SelectMarkerRulerAction;

//TODO: Max look here
public class CeylonSelectAnnotationRulerAction extends SelectMarkerRulerAction {

    public CeylonSelectAnnotationRulerAction(ResourceBundle bundle, String prefix,
            ITextEditor editor, IVerticalRulerInfo ruler) {
        super(bundle, prefix, editor, ruler);
    }
    
    @Override
    public void run() {
        System.out.println("why doesn't anymore call me?");
        super.run();
    }
    
    @Override
    public void runWithEvent(Event event) {
        System.out.println("why doesn't anymore call me?");
        super.runWithEvent(event);
    }
    
    @Override
    public void update() {
        System.out.println("why doesn't anymore call me?");
        super.update();
    }
    
}
