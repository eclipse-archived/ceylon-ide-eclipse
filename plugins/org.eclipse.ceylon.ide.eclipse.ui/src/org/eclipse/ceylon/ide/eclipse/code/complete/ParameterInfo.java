package org.eclipse.ceylon.ide.eclipse.code.complete;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.ide.common.typechecker.LocalAnalysisResult;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Reference;
import org.eclipse.ceylon.model.typechecker.model.Scope;

public final class ParameterInfo 
        extends InvocationCompletionProposal {
    public ParameterInfo(int offset, Declaration dec, 
            Reference producedReference,
            Scope scope, LocalAnalysisResult cpc, 
            boolean namedInvocation) {
        super(offset, "", "show parameters", "", dec, 
                producedReference, scope, ((EclipseCompletionContext) cpc).getCpc(), true, 
                true, namedInvocation, false, false,
                null);
    }
    @Override
    boolean isParameterInfo() {
        return true;
    }
    @Override
    public Point getSelection(IDocument document) {
        return null;
    }
    @Override
    public void apply(IDocument document) {}
}