package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;
import com.redhat.ceylon.eclipse.util.Indents;

public class ControlStructureCompletions {
	
    static void addForProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (d instanceof Value) {
            TypedDeclaration td = (TypedDeclaration) d;
            if (td.getType()!=null && 
                    d.getUnit().isIterableType(td.getType())) {
                String elemName;
                if (d.getName().length()==1) {
                    elemName = "element";
                }
                else if (d.getName().endsWith("s")) {
                    elemName = d.getName().substring(0, d.getName().length()-1);
                }
                else {
                    elemName = d.getName().substring(0, 1);
                }
                result.add(new DeclarationCompletionProposal(offset, prefix, 
                        "for (" + elemName + " in " + getDescriptionFor(dwp) + ")", 
                        "for (" + elemName + " in " + getTextFor(dwp) + ") {}",
                        true, cpc, d));
            }
        }
    }

    static void addIfExistsProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        d.getUnit().isOptionalType(v.getType()) && 
                        !v.isVariable()) {
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            "if (exists " + getDescriptionFor(dwp) + ")", 
                            "if (exists " + getTextFor(dwp) + ") {}", 
                            true, cpc, d));
                }
            }
        }
    }

    static void addSwitchProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Node node, 
            IDocument doc) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        v.getType().getCaseTypes()!=null && 
                        !v.isVariable()) {
                    StringBuilder body = new StringBuilder();
                    String indent = getIndent(node, doc);
                    for (ProducedType pt: v.getType().getCaseTypes()) {
                        body.append(indent).append("case (");
                        if (!pt.getDeclaration().isAnonymous()) {
                            body.append("is ");
                        }
                        body.append(pt.getProducedTypeName(node.getUnit()))
                            .append(") {}")
                            .append(Indents.getDefaultLineDelimiter(doc));
                    }
                    body.append(indent);
                    result.add(new DeclarationCompletionProposal(offset, prefix, 
                            "switch (" + getDescriptionFor(dwp) + ")", 
                            "switch (" + getTextFor(dwp) + ")" + 
                            		Indents.getDefaultLineDelimiter(doc) + body, 
                            true, cpc, d));
                }
            }
        }
    }


}
