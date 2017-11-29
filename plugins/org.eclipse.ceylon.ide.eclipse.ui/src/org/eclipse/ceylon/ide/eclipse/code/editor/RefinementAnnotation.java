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

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoDeclaration;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.jface.text.source.Annotation;

import org.eclipse.ceylon.model.typechecker.model.Declaration;

public class RefinementAnnotation extends Annotation {

    private Declaration declaration;
    private int line;
    
    public RefinementAnnotation(String text, 
            Declaration dec, int line) {
        super(PLUGIN_ID + ".refinement", false, text);
        this.declaration = dec;
        this.line = line;
    }
    
    public Declaration getDeclaration() {
        return declaration;
    }
    
    public int getLine() {
        return line;
    }
    
    public void gotoRefinedDeclaration(CeylonEditor editor) {
        gotoDeclaration(declaration);
    }
    
}
