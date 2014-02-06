package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.util.Escaping.escapeName;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class ImportCompletions {
	
    static void addImportProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Scope scope) {
        result.add(new DeclarationCompletionProposal(offset, prefix,
				d.getName(), escapeName(d), true, cpc, d, dwp.isUnimported(), 
				d.getReference(), scope, true));
    }

}
