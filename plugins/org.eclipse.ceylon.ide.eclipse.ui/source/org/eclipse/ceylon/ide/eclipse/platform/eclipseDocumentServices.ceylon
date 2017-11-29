/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import org.eclipse.ceylon.ide.common.platform {
    DocumentServices,
    CommonDocument
}

import org.eclipse.jface.preference {
    IPreferenceStore
}
import org.eclipse.ui.editors.text {
    EditorsUI
}
import org.eclipse.ui.texteditor {
    AbstractDecoratedTextEditorPreferenceConstants {
        editorTabWidth,
        editorSpacesForTabs
    }
}

object eclipseDocumentServices satisfies DocumentServices {
    createTextChange(String desc, CommonDocument|PhasedUnit input)
            => EclipseTextChange(desc, input);
    
    createCompositeChange(String desc) => EclipseCompositeChange(desc);

    indentSpaces 
            =>let(IPreferenceStore? store = EditorsUI.preferenceStore)
              (store?.getInt(editorTabWidth) else 4);
    
    indentWithSpaces
            => let(IPreferenceStore? store = EditorsUI.preferenceStore)
               (store?.getBoolean(editorSpacesForTabs) else false);
    
}