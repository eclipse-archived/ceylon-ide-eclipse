/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static org.eclipse.ceylon.ide.eclipse.util.DocLinks.nameRegion;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.vfsJ2C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.ide.common.vfs.FileVirtualFile;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.BaseType;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.DocLink;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.QualifiedType;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;
import org.eclipse.ceylon.ide.eclipse.util.DocLinks;

public class RenameJavaElementRefactoringParticipant extends RenameParticipant {

    private IMember javaDeclaration;

    protected boolean initialize(Object element) {
        javaDeclaration = (IMember) element;
        IProject project = 
                javaDeclaration.getJavaProject().getProject();
        try {
            if (!project.hasNature(CeylonNature.NATURE_ID)) {
                return false;
            }
        }
        catch (CoreException e) {
            e.printStackTrace();
            return false;
        }
        return getArguments().getUpdateReferences();
    }

    public String getName() {
        return "Rename participant for Ceylon source";
    }
    
    public RefactoringStatus checkConditions(IProgressMonitor pm, 
            CheckConditionsContext context) {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException {
        //TODO: don't ignore ((RenamePackageProcessor) getProcessor()).getUpdateTextualMatches()
        try {
            final IProject project = 
                    javaDeclaration.getJavaProject().getProject();
            final String newName = 
                    getArguments().getNewName();
            final String oldName = 
                    javaDeclaration.getElementName();

            final HashMap<IFile,Change> changes = 
                    new HashMap<IFile,Change>();
            TypeChecker tc = getProjectTypeChecker(project);
            if (tc==null) return null;
            for (PhasedUnit phasedUnit: 
                tc.getPhasedUnits().getPhasedUnits()) {
                final List<ReplaceEdit> edits = 
                        new ArrayList<ReplaceEdit>();
                Tree.CompilationUnit cu = 
                        phasedUnit.getCompilationUnit();
                cu.visit(new Visitor() {
                    @Override
                    public void visit(ImportMemberOrType that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), 
                                that.getDeclarationModel());
                    }
                    @Override
                    public void visit(QualifiedMemberOrTypeExpression that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), 
                                that.getDeclaration());
                    }
                    @Override
                    public void visit(BaseMemberOrTypeExpression that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), 
                                that.getDeclaration());
                    }
                    @Override
                    public void visit(BaseType that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), 
                                that.getDeclarationModel());
                    }
                    @Override
                    public void visit(QualifiedType that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), 
                                that.getDeclarationModel());
                    }
                    protected void visitIt(Tree.Identifier id, 
                            Declaration dec) {
                        visitIt(id.getText(), id.getStartIndex(), dec);
                    }
                    protected void visitIt(String name, int offset, 
                            Declaration dec) {
                        if (dec!=null && 
                                dec.getQualifiedNameString()
                                    .equals(getQualifiedName(javaDeclaration)) &&
                                name.equals(javaDeclaration.getElementName())) { //don't rename if aliased
                            edits.add(new ReplaceEdit(offset, oldName.length(), newName));
                        }
                    }
                    protected String getQualifiedName(IMember dec) {
                        IJavaElement parent = dec.getParent();
                        if (parent instanceof ICompilationUnit) {
                            return parent.getParent().getElementName() + "::" + 
                                    dec.getElementName();
                        }
                        else if (dec.getDeclaringType()!=null) {
                            return getQualifiedName(dec.getDeclaringType()) + "." + 
                                    dec.getElementName();
                        }
                        else {
                            return "@";
                        }
                    }
                    @Override
                    public void visit(DocLink that) {
                        super.visit(that);
                        Declaration base = that.getBase();
                        List<Declaration> qualified = 
                                that.getQualified();
                        if (base!=null) {
                            Region region = nameRegion(that, 0);
                            visitIt(DocLinks.name(that, 0), 
                                    region.getOffset(), base);
                            if (qualified!=null) {
                                for (int i=0; i<qualified.size(); i++) {
                                    visitIt(DocLinks.name(that, i+1), 
                                            nameRegion(that, i+1).getOffset(), 
                                            qualified.get(i));
                                }
                            }
                        }
                    }
                });
                if (!edits.isEmpty()) {
                    try {
                        FileVirtualFile<IProject, IResource, IFolder, IFile> unitFile = 
                                vfsJ2C().getIFileVirtualFile(phasedUnit.getUnitFile());
                        IFile file = unitFile.getNativeResource();
                        TextFileChange change = 
                                new TextFileChange(file.getName(), file);
                        change.setEdit(new MultiTextEdit());
                        changes.put(file, change);
                        for (ReplaceEdit edit: edits) {
                            change.addEdit(edit);
                        }
                    }       
                    catch (Exception e) { 
                        e.printStackTrace(); 
                    }
                }
            }

            if (changes.isEmpty())
                return null;

            CompositeChange result = 
                    new CompositeChange("Ceylon source changes");
            for (Iterator<Change> iter = changes.values().iterator(); 
                    iter.hasNext();) {
                result.add((Change) iter.next());
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}