/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.ide.eclipse.util.Nodes.getReferencedDeclaration;
import static org.eclipse.ceylon.model.typechecker.model.ModelUtil.isConstructor;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.common.model.ModifiableSourceFile;
import org.eclipse.ceylon.ide.common.typechecker.ModifiablePhasedUnit;
import org.eclipse.ceylon.ide.common.util.FindDeclarationNodeVisitor;
import org.eclipse.ceylon.model.typechecker.model.ClassOrInterface;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Referenceable;
import org.eclipse.ceylon.model.typechecker.model.Scope;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;

class RemoveAnnotionProposal extends CorrectionProposal {
    
    private final Declaration dec;
    private final String annotation;
    
    RemoveAnnotionProposal(Declaration dec, String annotation,
            int offset, String desc, TextChange change) {
        super(desc, change, new Region(offset, 0));
        this.dec = dec;
        this.annotation = annotation;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RemoveAnnotionProposal) {
            RemoveAnnotionProposal that = 
                    (RemoveAnnotionProposal) obj;
            return that.dec.equals(dec) && 
                    that.annotation.equals(annotation);
        }
        else {
            return super.equals(obj);
        }
    }
    
    @Override
    public int hashCode() {
        return dec.hashCode();
    }
    
    static void addRemoveAnnotationProposal(Node node, 
            String annotation,
            Collection<ICompletionProposal> proposals, 
            IProject project) {
        Referenceable dec = getReferencedDeclaration(node);
        if (dec instanceof Declaration) {
            addRemoveAnnotationProposal(node, annotation, 
                    "Make Non" + annotation,  
                    (Declaration) dec, proposals, project);
        }
    }

    static void addMakeContainerNonfinalProposal(
            Collection<ICompletionProposal> proposals, 
            IProject project, Node node) {
        Declaration dec;
        if (node instanceof Tree.Declaration) {
            Tree.Declaration decNode = 
                    (Tree.Declaration) node;
            Scope container = 
                    decNode.getDeclarationModel()
                        .getContainer();
            if (container instanceof Declaration) {
                dec = (Declaration) container;
            }
            else {
                return;
            }
        }
        else {
            dec = (Declaration) node.getScope();
        }
        addRemoveAnnotationProposal(node, 
                "final", "Make Nonfinal", 
                dec, proposals, project);
    }

    static void addRemoveAnnotationProposal(Node node, 
            String annotation, String desc, 
            Declaration dec, 
            Collection<ICompletionProposal> proposals, 
            IProject project) {
        if (dec!=null && dec.getName()!=null) {
            Unit u = dec.getUnit();
            if (u instanceof ModifiableSourceFile) {
                ModifiableSourceFile<IProject,IResource,IFolder,IFile> cu = 
                        (ModifiableSourceFile<IProject,IResource,IFolder,IFile>) u;
                ModifiablePhasedUnit<IProject,IResource,IFolder,IFile> unit = cu.getPhasedUnit();
                //TODO: "object" declarations?
                FindDeclarationNodeVisitor fdv = 
                        new FindDeclarationNodeVisitor(dec);
                unit.getCompilationUnit().visit(fdv);
                Tree.Declaration decNode = 
                        (Tree.Declaration) 
                        fdv.getDeclarationNode();
                if (decNode!=null) {
                    addRemoveAnnotationProposal(
                            annotation, desc, dec,
                            proposals, unit, decNode);
                }
            }
        }
    }

    private static void addRemoveAnnotationProposal(
            String annotation, String desc, 
            Declaration dec,
            Collection<ICompletionProposal> proposals, 
            ModifiablePhasedUnit<IProject,IResource,IFolder,IFile> unit, Tree.Declaration decNode) {
        IFile file = unit.getResourceFile();
        if (file == null) {
            return;
        }
        TextFileChange change = 
                new TextFileChange(desc, file);
        change.setEdit(new MultiTextEdit());
        Integer offset = decNode.getStartIndex();
        for (Tree.Annotation a: 
                decNode.getAnnotationList()
                    .getAnnotations()) {
            Tree.BaseMemberExpression bme = 
                    (Tree.BaseMemberExpression)
                        a.getPrimary();
            Tree.Identifier id = bme.getIdentifier();
            if (id!=null) {
                if (id.getText().equals(annotation)) {
                    Tree.PositionalArgumentList pal = 
                            a.getPositionalArgumentList();
                    boolean args = 
                            pal!=null && 
                                pal.getToken()!=null ||
                            a.getNamedArgumentList()!=null;
                    change.addEdit(new DeleteEdit(a.getStartIndex(), 
                            a.getDistance() + (args?0:1))); //get rid of the trailing space
                }
            }
        }
        RemoveAnnotionProposal p = 
                new RemoveAnnotionProposal(dec, annotation, 
                        offset, description(annotation, dec), 
                        change);
        if (!proposals.contains(p)) {
            proposals.add(p);
        }
    }

    private static String description(
            String annotation, Declaration dec) {
        String name = dec.getName();
        if (name==null) {
            if (isConstructor(dec)) {
                name = "default constructor ";
            }
            else {
                name = "";
            }
        }
        else {
            name = "'" + name + "' ";
        }
        String descr = "Make " + name + "non-" + annotation;
        Scope container = dec.getContainer();
        if (container instanceof ClassOrInterface) {
            TypeDeclaration td = (ClassOrInterface) container;
            descr += " in '" + td.getName() + "'";
        }
        return descr;
    }
    
    static void addRemoveAnnotationDecProposal(
            Collection<ICompletionProposal> proposals, 
            String annotation, IProject project, Node node) {
        if (node instanceof Tree.Declaration) {
            Tree.Declaration decNode = (Tree.Declaration) node;
            addRemoveAnnotationProposal(node, annotation, 
                    "Make Non" + annotation,  
                    decNode.getDeclarationModel(), 
                    proposals, project);
        }
    }
    
}