package com.redhat.ceylon.eclipse.code.complete;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getTextFor;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.util.Indents.getDefaultLineDelimiter;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationWithProximity;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.model.Value;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

class ControlStructureCompletionProposal extends CompletionProposal {
    
    static void addForProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (d instanceof Value) {
            TypedDeclaration td = (TypedDeclaration) d;
            if (td.getType()!=null && 
                    d.getUnit().isIterableType(td.getType())) {
                String elemName;
                String name = d.getName();
                if (name.length()==1) {
                    elemName = "element";
                }
                else if (name.endsWith("s")) {
                    elemName = name.substring(0, name.length()-1);
                }
                else {
                    elemName = name.substring(0, 1);
                }
                Unit unit = cpc.getRootNode().getUnit();
                result.add(new ControlStructureCompletionProposal(offset, prefix, 
                        "for (" + elemName + " in " + getDescriptionFor(d, unit) + ")", 
                        "for (" + elemName + " in " + getTextFor(d, unit) + ") {}",
                        d, cpc));
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
                    Unit unit = cpc.getRootNode().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "if (exists " + getDescriptionFor(d, unit) + ")", 
                            "if (exists " + getTextFor(d, unit) + ") {}", 
                            d, cpc));
                }
            }
        }
    }

    static void addIfNonemptyProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        d.getUnit().isPossiblyEmptyType(v.getType()) && 
                        !v.isVariable()) {
                    Unit unit = cpc.getRootNode().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "if (nonempty " + getDescriptionFor(d, unit) + ")", 
                            "if (nonempty " + getTextFor(d, unit) + ") {}", 
                            d, cpc));
                }
            }
        }
    }
    
    static void addTryProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        v.getType().getDeclaration()
                            .inherits(d.getUnit().getObtainableDeclaration()) && 
                        !v.isVariable()) {
                    Unit unit = cpc.getRootNode().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "try (" + getDescriptionFor(d, unit) + ")", 
                            "try (" + getTextFor(d, unit) + ") {}", 
                            d, cpc));
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
                            .append(getDefaultLineDelimiter(doc));
                    }
                    body.append(indent);
                    Unit unit = cpc.getRootNode().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "switch (" + getDescriptionFor(d, unit) + ")", 
                            "switch (" + getTextFor(d, unit) + ")" + 
                                    getDefaultLineDelimiter(doc) + body, 
                            d, cpc));
                }
            }
        }
    }
    
    private final CeylonParseController cpc;
    private final Declaration declaration;
    
    private ControlStructureCompletionProposal(int offset, String prefix, 
            String desc, String text, Declaration dec, 
            CeylonParseController cpc) {
        super(offset, prefix, CeylonLabelProvider.MINOR_CHANGE, 
                desc, text);
        this.cpc = cpc;
        this.declaration = dec;
    }
    
    public String getAdditionalProposalInfo() {
        return getDocumentationFor(cpc, declaration);    
    }

    @Override
    public Point getSelection(IDocument document) {
        return new Point(offset + text.indexOf('}') - prefix.length(), 0);
    }
    
}