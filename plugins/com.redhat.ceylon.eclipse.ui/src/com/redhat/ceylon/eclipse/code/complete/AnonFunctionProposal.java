package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CeylonCompletionProcessor.LARGE_CORRECTION_IMAGE;
import static com.redhat.ceylon.eclipse.code.complete.CompletionUtil.anonFunctionHeader;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.model.typechecker.model.Type;
import com.redhat.ceylon.model.typechecker.model.Unit;

class AnonFunctionProposal {

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
