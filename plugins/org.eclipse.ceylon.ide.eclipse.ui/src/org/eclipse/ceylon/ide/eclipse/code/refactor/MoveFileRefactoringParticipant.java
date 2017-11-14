/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.refactor;

import static org.eclipse.ceylon.ide.eclipse.code.correct.ImportProposals.importProposals;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.vfsJ2C;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.ltk.core.refactoring.participants.MoveProcessor;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MultiTextEdit;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.BaseType;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.code.correct.correctJ2C;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;
import org.eclipse.ceylon.ide.eclipse.util.EditorUtil;
import org.eclipse.ceylon.ide.common.model.ProjectSourceFile;
import org.eclipse.ceylon.ide.common.vfs.FileVirtualFile;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Unit;

public class MoveFileRefactoringParticipant extends MoveParticipant {

    private IFile file;
    
    private static Map<String,TextFileChange> fileChanges = 
            new HashMap<String,TextFileChange>();
    private static List<IResource> movingFiles =
            new ArrayList<IResource>();
    
    
    @Override
    protected boolean initialize(Object element) {
        file = (IFile) element;
        try {
            if (!file.getProject().hasNature(CeylonNature.NATURE_ID)) {
                return false;
            }
        }
        catch (CoreException e) {
            e.printStackTrace();
            return false;
        }
        RefactoringProcessor processor = getProcessor();
        if (processor instanceof MoveProcessor) {
            MoveProcessor moveProcessor = (MoveProcessor) processor;
            for (Object e: moveProcessor.getElements()) {
                IResource r = null;
                if (e instanceof IResource) {
                    r = (IResource) e;
                }
                else if (e instanceof ICompilationUnit) {
                    r = ((ICompilationUnit) e).getResource();
                }
                if (r!=null) {
                    movingFiles.add(r);
                }
            }
            String ext = file.getFileExtension();
            return ext!=null 
                && (ext.equals("ceylon") 
                 || ext.equals("java"));
        }
        else {
            return false;
        }
    }

    @Override
    public String getName() {
        return "Move file participant for Ceylon source";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm,
            CheckConditionsContext context) 
                    throws OperationCanceledException {
        return new RefactoringStatus();
    }
    
    @Override
    public Change createChange(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        return null;
    }
    
    @Override
    public Change createPreChange(IProgressMonitor pm) 
            throws CoreException, OperationCanceledException {
        try {
            IProject project = file.getProject();
            Object destination = getArguments().getDestination();
            if (! (destination instanceof IFolder)) {
                return null;
            }
            IFolder folder = (IFolder) destination;
            String newName = folder.getProjectRelativePath()
                    .removeFirstSegments(1)
                    .toPortableString()
                    .replace('/', '.');
            String movedRelFilePath = file.getProjectRelativePath()
                    .removeFirstSegments(1)
                    .toPortableString();
            String movedRelPath = file.getParent()
                    .getProjectRelativePath()
                    .removeFirstSegments(1)
                    .toPortableString();
            String oldName = movedRelPath.replace('/', '.');

            List<Change> changes = new ArrayList<Change>();

            if (file.getFileExtension().equals("java")) {
                updateRefsToMovedJavaFile(project, newName, oldName, changes);
            }
            else {
                TypeChecker tc = getProjectTypeChecker(project);
                if (tc==null) return null;
                PhasedUnit movedPhasedUnit = 
                        tc.getPhasedUnitFromRelativePath(movedRelFilePath);
                if (movedPhasedUnit==null) {
                    return null;
                }
                
                List<Declaration> declarations = 
                        movedPhasedUnit.getDeclarations();
                if (newName.equals(oldName)) return null;
                updateRefsFromMovedCeylonFile(project, newName, oldName, changes, 
                        movedPhasedUnit, declarations);
                updateRefsToMovedCeylonFile(project, newName, oldName, changes, 
                        movedPhasedUnit, declarations);
            }

            if (changes.isEmpty())
                return null;

            CompositeChange result = 
                    new CompositeChange("Ceylon source changes") {
                @Override
                public Change perform(IProgressMonitor pm) 
                        throws CoreException {
                    fileChanges.clear();
                    movingFiles.clear();
                    return super.perform(pm);
                }
            };
            for (Change change: changes) {
                result.add(change);
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void updateRefsFromMovedCeylonFile(
            final IProject project,
            final String newName, final String oldName,
            final List<Change> changes, 
            final PhasedUnit movedPhasedUnit,
            final List<Declaration> declarations) {
        final Map<Declaration,String> imports = 
                new HashMap<Declaration,String>();
        movedPhasedUnit.getCompilationUnit().visit(new Visitor() {
            @Override
            public void visit(ImportMemberOrType that) {
                super.visit(that);
                visitIt(that.getIdentifier(), 
                        that.getDeclarationModel());
            }
//            @Override
//            public void visit(QualifiedMemberOrTypeExpression that) {
//                super.visit(that);
//                visitIt(that.getIdentifier(), that.getDeclaration());
//            }
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
//            @Override
//            public void visit(QualifiedType that) {
//                super.visit(that);
//                visitIt(that.getIdentifier(), that.getDeclarationModel());
//            }
            protected void visitIt(Tree.Identifier id, Declaration dec) {
                if (dec!=null && !declarations.contains(dec)) {
                    Unit unit = dec.getUnit();
                    if (unit instanceof ProjectSourceFile && 
                            movingFiles.contains(((ProjectSourceFile<IProject, IResource, IFolder, IFile>) unit).getResourceFile())) {
                        //also moving
                    }
                    else if (unit.getPackage().equals(movedPhasedUnit.getPackage())) {
                        imports.put(dec, id.getText());
                    }
                }
            }
            //TODO: DocLinks!!
        });
        collectEditsToMovedFile(newName, oldName, changes, 
                movedPhasedUnit, imports);
    }
    
    protected void updateRefsToMovedCeylonFile(final IProject project,
            final String newName, final String oldName,
            final List<Change> changes, PhasedUnit movedPhasedUnit, 
            final List<Declaration> declarations) {
        if (!getArguments().getUpdateReferences()) return;
        TypeChecker tc = getProjectTypeChecker(project);
        if (tc==null) return;
        for (PhasedUnit phasedUnit: 
                tc.getPhasedUnits().getPhasedUnits()) {
            if (phasedUnit==movedPhasedUnit ||
                    phasedUnit.getUnit() instanceof ProjectSourceFile &&
                    movingFiles.contains(((ProjectSourceFile) phasedUnit.getUnit()).getResourceFile())) {
                continue;
            }
            final Map<Declaration,String> imports = 
                    new HashMap<Declaration,String>();
            phasedUnit.getCompilationUnit().visit(new Visitor() {
                @Override
                public void visit(ImportMemberOrType that) {
                    super.visit(that);
                    visitIt(that.getIdentifier(), 
                            that.getDeclarationModel());
                }
//                    @Override
//                    public void visit(QualifiedMemberOrTypeExpression that) {
//                        super.visit(that);
//                        visitIt(that.getIdentifier(), that.getDeclaration());
//                    }
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
//                    @Override
//                    public void visit(QualifiedType that) {
//                        super.visit(that);
//                        visitIt(that.getIdentifier(), that.getDeclarationModel());
//                    }
                protected void visitIt(Tree.Identifier id, Declaration dec) {
                    if (dec!=null && declarations.contains(dec)) {
                        imports.put(dec, id.getText());
                    }
                }
              //TODO: DocLinks!!
            });
            collectEdits(newName, oldName, changes, phasedUnit, imports);
        }
    }

    protected void updateRefsToMovedJavaFile(final IProject project,
            final String newName, final String oldName,
            final List<Change> changes) throws JavaModelException {
        if (!getArguments().getUpdateReferences()) return;
        ICompilationUnit jcu = (ICompilationUnit) JavaCore.create(file);
        final IType[] types = jcu.getTypes();
        TypeChecker tc = getProjectTypeChecker(project);
        if (tc==null) return;
        for (PhasedUnit phasedUnit: tc.getPhasedUnits().getPhasedUnits()) {
            final Map<Declaration,String> imports = 
                    new HashMap<Declaration,String>();
            phasedUnit.getCompilationUnit().visit(new Visitor() {
                @Override
                public void visit(ImportMemberOrType that) {
                    super.visit(that);
                    visitIt(that.getIdentifier(), 
                            that.getDeclarationModel());
                }
//                    @Override
//                    public void visit(QualifiedMemberOrTypeExpression that) {
//                        super.visit(that);
//                        visitIt(that.getIdentifier(), that.getDeclaration());
//                    }
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
//                    @Override
//                    public void visit(QualifiedType that) {
//                        super.visit(that);
//                        visitIt(that.getIdentifier(), that.getDeclarationModel());
//                    }
                protected void visitIt(Tree.Identifier id, Declaration dec) {
                    for (IType type: types) {
                        if (dec!=null && dec.getQualifiedNameString()
                                .equals(getQualifiedName(type))) {
                           imports.put(dec, id.getText());
                        }
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
            });
            collectEdits(newName, oldName, changes, phasedUnit, imports);
        }
    }
    
    private void collectEditsToMovedFile(String newName, 
            String oldName, List<Change> changes, 
            PhasedUnit movedPhasedUnit, 
            Map<Declaration, String> imports) {
        try {
            FileVirtualFile<IProject, IResource, IFolder, IFile> virtualFile = 
                    vfsJ2C().getIFileVirtualFile( movedPhasedUnit.getUnitFile());
            IFile file = virtualFile.getNativeResource();
            String path = file.getProjectRelativePath().toPortableString();
            TextFileChange change = fileChanges.get(path);
            if (change==null) {
                change = new TextFileChange(file.getName(), file);
                change.setEdit(new MultiTextEdit());
                changes.add(change);
                fileChanges.put(path, change);
            }
            Tree.CompilationUnit cu = 
                    movedPhasedUnit.getCompilationUnit();
            if (!imports.isEmpty()) {
                new correctJ2C().importEdits(change, cu, 
                        imports.keySet(), imports.values(),
                        EditorUtil.getDocument(change));
            }
            Tree.Import toDelete = importProposals().findImportNode(cu, newName, cu.getScope());
            if (toDelete!=null) {
                change.addEdit(new DeleteEdit(toDelete.getStartIndex(), 
                        toDelete.getDistance()));
            }
        }
        catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
    
    private void collectEdits(String newName, 
            String oldName, List<Change> changes, 
            PhasedUnit phasedUnit,
            Map<Declaration, String> imports) {
        try {
            Tree.CompilationUnit cu = 
                    phasedUnit.getCompilationUnit();
            if (!imports.isEmpty()) {
                FileVirtualFile<IProject, IResource, IFolder, IFile> virtualFile = 
                        vfsJ2C().getIFileVirtualFile(phasedUnit.getUnitFile());
                IFile file = virtualFile.getNativeResource();
                String path = file.getProjectRelativePath().toPortableString();
                TextFileChange change = fileChanges.get(path);
                if (change==null) {
                    change = new TextFileChange(file.getName(), file);
                    change.setEdit(new MultiTextEdit());
                    changes.add(change);
                    fileChanges.put(path, change);
                }
                new correctJ2C().importEditForMove(change, cu, 
                        imports.keySet(), imports.values(), 
                        newName, oldName, 
                        EditorUtil.getDocument(change));
            }
        }
        catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

}
