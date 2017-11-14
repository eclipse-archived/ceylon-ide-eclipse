/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.outline;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin.imageRegistry;
import static java.util.Arrays.asList;

import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DocumentRangeNode;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Image;

public class CeylonDocumentRangeNode extends DocumentRangeNode 
        implements ITypedElement {
    
    private final CeylonOutlineNode node;
    
    public CeylonDocumentRangeNode(DocumentRangeNode parent, 
            String id, CeylonOutlineNode outlineNode, 
            IDocument document) {
        super(parent, 1, id, document,
                outlineNode.getRealStartOffset(), 
                outlineNode.getRealEndOffset()-outlineNode.getRealStartOffset());
        node = outlineNode;
    }
    
    @Override
    public Image getImage() {
        return imageRegistry().get(node.getImageKey());
    }
    
    @Override
    public String getName() {
        if (node.getParent()==null) {
            return "Ceylon Source File";
        }
        else {
            return node.getLabel().toString();
        }
    }
    
    @Override
    public String getType() {
        return "ceylon";
    }

    @Override
    public String toString() {
        return node.getIdentifier() + asList(getChildren());
    }
    
}