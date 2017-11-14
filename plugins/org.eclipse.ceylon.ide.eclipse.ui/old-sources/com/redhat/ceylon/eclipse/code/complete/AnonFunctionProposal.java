/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.ide.eclipse.code.complete.CeylonCompletionProcessor.LARGE_CORRECTION_IMAGE;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CompletionUtil.anonFunctionHeader;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.Unit;

@Deprecated
class AnonFunctionProposal {

    @Deprecated
    static void addAnonFunctionProposal(int offset, 
            Type requiredType, 
            List<ICompletionProposal> result, 
            Unit unit) {
        String text = anonFunctionHeader(requiredType, unit);
        String funtext = text + " => nothing";
        result.add(new CompletionProposal(offset, "", 
                LARGE_CORRECTION_IMAGE, funtext, funtext) {
            @Override
            public Point getSelection(IDocument document) {
                return new Point(offset + text.indexOf("nothing"), 7);
            }
        });
        if (unit.getCallableReturnType(requiredType).isAnything()) {
            String voidtext = "void " + text + " {}";
            result.add(new CompletionProposal(offset, "", 
                    LARGE_CORRECTION_IMAGE, voidtext, voidtext) {
                @Override
                public Point getSelection(IDocument document) {
                    return new Point(offset + text.length()-1, 0);
                }
            });
        }
    }

}
