package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getNamedInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getNamedInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getPositionalInvocationDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getPositionalInvocationTextFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getTextForDocLink;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.CLASS_ALIAS;
import static com.redhat.ceylon.eclipse.code.propose.OccurrenceLocation.EXTENDS;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.Scope;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

public class ReferenceCompletions {
	
    static void addBasicProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Scope scope) {
        result.add(new DeclarationCompletionProposal(offset, prefix,
                getDescriptionFor(dwp), getTextFor(dwp), 
                true, cpc, d, dwp.isUnimported(), d.getReference(), 
                scope, true));
    }

    static void addDocLinkProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Scope scope) {
        ProducedReference pr = d.getProducedReference(null,
                		Collections.<ProducedType>emptyList());
		result.add(new DeclarationCompletionProposal(offset, prefix,
				d.getName(), getTextForDocLink(cpc, dwp),
                true, cpc, d, false, pr, scope, true));
    }

    static void addNamedArgumentProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        result.add(new DeclarationCompletionProposal(offset, prefix, 
                getDescriptionFor(dwp), 
                getTextFor(dwp) + " = nothing;", 
                true, cpc, d));
    }

    static void addInvocationProposals(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, ProducedReference pr, Scope scope,
            OccurrenceLocation ol, String typeArgs) {
        Declaration d = pr.getDeclaration();
        Unit unit = cpc.getRootNode().getUnit();
        if (!(d instanceof Functional)) return;
        boolean isAbstractClass = d instanceof Class && ((Class) d).isAbstract();
        Functional fd = (Functional) d;
        List<ParameterList> pls = fd.getParameterLists();
        if (!pls.isEmpty()) {
            List<Parameter> ps = pls.get(0).getParameters();
            boolean hasDefaulted = ps.size()!=CompletionUtil.getParameters(false, ps).size();
			if (!isAbstractClass ||
            		ol==EXTENDS || ol==CLASS_ALIAS) {
                if (hasDefaulted) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            getPositionalInvocationDescriptionFor(dwp, ol, pr, unit, false, null), 
                            getPositionalInvocationTextFor(dwp, ol, pr, unit, false, null), true,
                            cpc, d, dwp.isUnimported(), pr, scope, false));
                }
                result.add(new DeclarationCompletionProposal(offset, prefix, 
                        getPositionalInvocationDescriptionFor(dwp, ol, pr, unit, true, typeArgs), 
                        getPositionalInvocationTextFor(dwp, ol, pr, unit, true, typeArgs), true,
                        cpc, d, dwp.isUnimported(), pr, scope, true));
            }
            if (!isAbstractClass &&
            		ol!=EXTENDS && ol!=CLASS_ALIAS &&
                    !fd.isOverloaded() && typeArgs==null) {
                //if there is at least one parameter, 
                //suggest a named argument invocation
                if (hasDefaulted) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            getNamedInvocationDescriptionFor(dwp, pr, unit, false), 
                            getNamedInvocationTextFor(dwp, pr, unit, false), true,
                            cpc, d, dwp.isUnimported(), pr, scope, false));
                }
                if (!ps.isEmpty()) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            getNamedInvocationDescriptionFor(dwp, pr, unit, true), 
                            getNamedInvocationTextFor(dwp, pr, unit, true), true,
                            cpc, d, dwp.isUnimported(), pr, scope, true));
                }
            }
        }
    }
    

}
