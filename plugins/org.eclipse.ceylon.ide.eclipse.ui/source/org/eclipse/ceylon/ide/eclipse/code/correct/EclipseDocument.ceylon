/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
import org.eclipse.ceylon.ide.common.platform {
    CommonDocument
}

import org.eclipse.jface.text {
    IDocument,
    IDocumentExtension4
}

shared class EclipseDocument(shared variable IDocument document) 
        satisfies CommonDocument {
    
    getLineContent(Integer line)
            => let (info = document.getLineInformation(line))
            document.get(info.offset, info.length);

    getLineStartOffset(Integer line)
            => document.getLineInformation(line).offset;
    
    getLineEndOffset(Integer line)
            => document.getLineInformation(line).offset
             + document.getLineInformation(line).length;
    
    getLineOfOffset(Integer offset)
            => document.getLineOfOffset(offset);
    
    getText(Integer offset, Integer length)
            => document.get(offset, length);
    
    defaultLineDelimiter
            => if (is IDocumentExtension4 d = document)
                then d.defaultLineDelimiter
                else operatingSystem.newline;

    size => document.length;
    
    shared actual Boolean equals(Object that) {
        if (is EclipseDocument that) {
            return document==that.document;
        }
        else {
            return false;
        }
    }
}
