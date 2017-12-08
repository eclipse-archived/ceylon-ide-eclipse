/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.IMPORT;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedModel;
import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedNodeInUnit;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.ModuleImport;
import org.eclipse.ceylon.model.typechecker.model.Type;
import org.eclipse.ceylon.model.typechecker.model.Unit;

abstract class ExportModuleImportProposal implements ICompletionProposal, 
        ICompletionProposalExtension6 {
    
    private String desc;
    
    ExportModuleImportProposal(String desc) {
        this.desc = desc;
    }
    
//    @Override
//    public void apply(IDocument document) {
//        importsJ2C().importUtil().exportModuleImports(project, 
//                unit.getPackage().getModule(), 
//                name);
//    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return desc;
    }

    @Override
    public Image getImage() {
        return IMPORT;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), true);
    }

    @Deprecated
    static void addExportModuleImportProposalForSupertypes(Collection<ICompletionProposal> proposals, 
            IProject project, Node node, Tree.CompilationUnit rootNode) {
        Unit unit = node.getUnit();
        if (node instanceof Tree.InitializerParameter) {
            node = getReferencedNodeInUnit(getReferencedModel(node), rootNode);
        }
        if (node instanceof Tree.TypedDeclaration) {
            node = ((Tree.TypedDeclaration) node).getType();
        }
        if (node instanceof Tree.ClassOrInterface) {
            Tree.ClassOrInterface c = (Tree.ClassOrInterface) node;
            Type extendedType = 
                    c.getDeclarationModel().getExtendedType();
            if (extendedType!=null) {
                addExportModuleImportProposal(proposals, project, 
                        unit, extendedType.getDeclaration());
                for (Type typeArgument:
                        extendedType.getTypeArgumentList()) {
                    addExportModuleImportProposal(proposals, project, 
                            unit, typeArgument.getDeclaration());
                }
            }
            
            List<Type> satisfiedTypes = 
                    c.getDeclarationModel().getSatisfiedTypes();
            if (satisfiedTypes!=null) {
                for (Type satisfiedType: satisfiedTypes) {
                    addExportModuleImportProposal(proposals, project, 
                            unit, satisfiedType.getDeclaration());
                    for (Type typeArgument: 
                            satisfiedType.getTypeArgumentList()) {
                        addExportModuleImportProposal(proposals, project, 
                                unit, typeArgument.getDeclaration());
                    }
                }
            }
        }
        else if (node instanceof Tree.Type) {
            Type type = ((Tree.Type) node).getTypeModel();
            addExportModuleImportProposal(proposals, project, 
                    unit, type.getDeclaration());
            for (Type typeArgument:
                    type.getTypeArgumentList()) {
                addExportModuleImportProposal(proposals, project, 
                        unit, typeArgument.getDeclaration());
            }
        }
    }
    
    @Deprecated
    static void addExportModuleImportProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        if (node instanceof Tree.SimpleType) {
            Declaration dec = ((Tree.SimpleType) node).getDeclarationModel();
            addExportModuleImportProposal(proposals, project, node.getUnit(), dec);
        }
    }

    @Deprecated
    private static void addExportModuleImportProposal(Collection<ICompletionProposal> proposals, 
            IProject project, Unit unit, Declaration dec) {
        Module decModule = dec.getUnit().getPackage().getModule();
        for (ModuleImport mi: unit.getPackage().getModule().getImports()) {
            if (mi.getModule().equals(decModule)) {
                if (mi.isExport()) {
                    return;
                }
            }
        }
//        proposals.add(new ExportModuleImportProposal(project, unit, 
//                decModule.getNameAsString(), decModule.getVersion()));
    }

}
