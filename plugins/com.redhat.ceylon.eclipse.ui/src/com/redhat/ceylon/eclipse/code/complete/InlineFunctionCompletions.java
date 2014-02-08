package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getInlineFunctionDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getInlineFunctionTextFor;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class InlineFunctionCompletions {
	
    static void addInlineFunctionProposal(int offset, String prefix, CeylonParseController cpc,
            Node node, List<ICompletionProposal> result, Declaration d, IDocument doc) {
        //TODO: type argument substitution using the ProducedReference of the primary node
        if (d.isParameter()) {
            Parameter p = ((MethodOrValue) d).getInitializerParameter();
            Unit unit = node.getUnit();
            result.add(new RefinementCompletionProposal(offset, prefix,
                    getInlineFunctionDescriptionFor(p, null, unit),
                    getInlineFunctionTextFor(p, null, unit, 
                    		getDefaultLineDelimiter(doc) + 
                    		getIndent(node, doc)),
                    cpc, d));
        }
    }


}
