/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.editor;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.SelectMarkerRulerAction;

public class CeylonSelectAnnotationRulerAction extends SelectMarkerRulerAction {
    
    IVerticalRulerInfo ruler;
    CeylonEditor editor;
    
    public CeylonSelectAnnotationRulerAction(ResourceBundle bundle, String prefix,
            ITextEditor editor, IVerticalRulerInfo ruler) {
        super(bundle, prefix, editor, ruler);
        this.ruler = ruler;
        this.editor = (CeylonEditor) editor;
    }
    
    @Override
    public void update() {
        //don't let super.update() be called!
    }
    
    @Override
    public void run() {
        //super.run();
        int line = ruler.getLineOfLastMouseButtonActivity()+1;
        IAnnotationModel model= editor.getDocumentProvider()
                .getAnnotationModel(editor.getEditorInput());
        for (Iterator<Annotation> iter = 
                    model.getAnnotationIterator(); 
                iter.hasNext();) {
            Annotation ann = iter.next();
            if (ann instanceof RefinementAnnotation) {
                RefinementAnnotation ra = (RefinementAnnotation) ann;
                if (ra.getLine()==line) {
                    ra.gotoRefinedDeclaration(editor);
                }
            }
        }
    }

}
