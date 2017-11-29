/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.swt.graphics {
    Image
}
import org.eclipse.ceylon.ide.common.doc {
    Icons
}
import org.eclipse.ceylon.ide.eclipse.ui {
    CeylonResources,
    CeylonPlugin
}
import org.eclipse.ceylon.ide.eclipse.code.outline {
    CeylonLabelProvider
}

shared object eclipseIcons {
    
    shared Image? fromIcons(Icons? icon) {
         return switch(icon)
         case(Icons.modules) CeylonResources.\imodule
         case(Icons.packages) CeylonResources.\ipackage
         case(Icons.classes) CeylonResources.\iclass
         case(Icons.interfaces) CeylonResources.\iinterface
         case(Icons.attributes) CeylonResources.attribute
         case(Icons.localClass) CeylonResources.localClass
         case(Icons.localMethod) CeylonResources.localMethod
         case(Icons.localAttribute) CeylonResources.localName
         case(Icons.imports) CeylonResources.\iimport
         case(Icons.reveal) CeylonResources.reveal
         case(Icons.refinement) CeylonPlugin.imageRegistry()
                 .get(CeylonResources.ceylonDefaultRefinement)
         case(Icons.formalRefinement) CeylonPlugin.imageRegistry()
                 .get(CeylonResources.ceylonFormalRefinement)
         case(Icons.ceylonLiteral) CeylonLabelProvider
                 .getDecoratedImage(CeylonResources.ceylonLiteral, 0, false)
         case(Icons.correction) CeylonLabelProvider.getDecoratedImage(
             CeylonResources.ceylonCorrection, 0, false)
         case(Icons.addCorrection) CeylonResources.addCorr
         case(Icons.suppressWarnings) CeylonResources.suppressWarning
         case(Icons.remove) CeylonResources.removeCorr
         else null;
    }
}