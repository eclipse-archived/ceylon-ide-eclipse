/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.complete;

import static org.eclipse.ceylon.ide.eclipse.code.complete.CodeCompletions.getDescriptionFor;
import static org.eclipse.ceylon.ide.eclipse.code.complete.CodeCompletions.getTextFor;
import static org.eclipse.ceylon.ide.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.ide.eclipse.code.parse.CeylonParseController;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.DeclarationWithProximity;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;
import org.eclipse.ceylon.model.typechecker.model.Value;

class ControlStructureCompletionProposal extends CompletionProposal {
    
    @Deprecated
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
                Unit unit = cpc.getLastCompilationUnit().getUnit();
                result.add(new ControlStructureCompletionProposal(offset, prefix, 
                        "for (" + elemName + " in " + getDescriptionFor(d, unit) + ")", 
                        "for (" + elemName + " in " + getTextFor(d, unit) + ") {}",
                        d, cpc));
            }
        }
    }

    @Deprecated
    static void addIfExistsProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        d.getUnit().isOptionalType(v.getType()) && 
                        !v.isVariable()) {
                    Unit unit = cpc.getLastCompilationUnit().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "if (exists " + getDescriptionFor(d, unit) + ")", 
                            "if (exists " + getTextFor(d, unit) + ") {}", 
                            d, cpc));
                }
            }
        }
    }

    @Deprecated
    static void addAssertExistsProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        d.getUnit().isOptionalType(v.getType()) && 
                        !v.isVariable()) {
                    Unit unit = cpc.getLastCompilationUnit().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "assert (exists " + getDescriptionFor(d, unit) + ")", 
                            "assert (exists " + getTextFor(d, unit) + ");", 
                            d, cpc));
                }
            }
        }
    }

    @Deprecated
    static void addIfNonemptyProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        d.getUnit().isPossiblyEmptyType(v.getType()) && 
                        !v.isVariable()) {
                    Unit unit = cpc.getLastCompilationUnit().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "if (nonempty " + getDescriptionFor(d, unit) + ")", 
                            "if (nonempty " + getTextFor(d, unit) + ") {}", 
                            d, cpc));
                }
            }
        }
    }
    
    @Deprecated
    static void addAssertNonemptyProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                if (v.getType()!=null &&
                        d.getUnit().isPossiblyEmptyType(v.getType()) && 
                        !v.isVariable()) {
                    Unit unit = cpc.getLastCompilationUnit().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "assert (nonempty " + getDescriptionFor(d, unit) + ")", 
                            "assert (nonempty " + getTextFor(d, unit) + ");", 
                            d, cpc));
                }
            }
        }
    }
    
    @Deprecated
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
                    Unit unit = cpc.getLastCompilationUnit().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "try (" + getDescriptionFor(d, unit) + ")", 
                            "try (" + getTextFor(d, unit) + ") {}", 
                            d, cpc));
                }
            }
        }
    }
    
    @Deprecated
    static void addSwitchProposal(int offset, String prefix, 
            CeylonParseController cpc, List<ICompletionProposal> result, 
            DeclarationWithProximity dwp, Declaration d, Node node, 
            IDocument doc) {
        if (!dwp.isUnimported()) {
            if (d instanceof Value) {
                TypedDeclaration v = (TypedDeclaration) d;
                Type type = v.getType();
                if (type!=null &&
                        type.getCaseTypes()!=null && 
                        !v.isVariable()) {
                    StringBuilder body = new StringBuilder();
                    String indent = utilJ2C().indents().getIndent(node, doc);
                    Unit unit = node.getUnit();
                    for (Type pt: type.getCaseTypes()) {
                        body.append(indent).append("case (");
                        TypeDeclaration ctd = pt.getDeclaration();
                        if (ctd.isAnonymous()) {
                            if (!ctd.isToplevel()) {
                                TypeDeclaration td = type.getDeclaration();
                                body.append(td.getName(unit)).append('.');
                            }
                            body.append(ctd.getName(unit));
                        }
                        else {
                            body.append("is ")
                                .append(pt.asSourceCodeString(unit));
                        }
                        body.append(") {}")
                            .append(utilJ2C().indents().getDefaultLineDelimiter(doc));
                    }
                    body.append(indent);
                    Unit u = cpc.getLastCompilationUnit().getUnit();
                    result.add(new ControlStructureCompletionProposal(offset, prefix, 
                            "switch (" + getDescriptionFor(d, u) + ")", 
                            "switch (" + getTextFor(d, u) + ")" + 
                                    utilJ2C().indents().getDefaultLineDelimiter(doc) + body, 
                            d, cpc));
                }
            }
        }
    }
    
    private final CeylonParseController cpc;
    private final Declaration declaration;
    
    ControlStructureCompletionProposal(int offset, String prefix, 
            String desc, String text, Declaration dec, 
            CeylonParseController cpc) {
        super(offset, prefix, CeylonResources.MINOR_CHANGE, 
                desc, text);
        this.cpc = cpc;
        this.declaration = dec;
    }

    public String getAdditionalProposalInfo() {
        return getAdditionalProposalInfo(null);
    }

    public String getAdditionalProposalInfo(IProgressMonitor monitor) {
        return getDocumentationFor(cpc, declaration, monitor);
    }
    
    @Override
    public Point getSelection(IDocument document) {
        int loc = text.indexOf('}');
        if (loc<0) {
            loc = text.indexOf(';')+1;
        }
        return new Point(offset + loc - prefix.length(), 0);
    }
    
}