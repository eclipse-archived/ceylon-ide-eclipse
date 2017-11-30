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

import static org.eclipse.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static org.eclipse.ceylon.ide.eclipse.code.refactor.MoveUtil.escapePackageName;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.vfsJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.DocLinks.packageName;
import static org.eclipse.ceylon.ide.eclipse.util.DocLinks.packageRegion;

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
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenamePackageProcessor;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.DocLink;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import org.eclipse.ceylon.compiler.typechecker.tree.Visitor;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;
import org.eclipse.ceylon.ide.common.vfs.FileVirtualFile;

public class RenamePackageRefactoringParticipant extends RenameParticipant {

    private IPackageFragment javaPackageFragment;
    
    private static Map<String,TextFileChange> fileChanges = 
            new HashMap<String,TextFileChange>();

    @Override
    protected boolean initialize(Object element) {
        //Note: When getRenameSubpackages() is true, this participant is
        //      called multiple times, once for each subpackage. This is,
        //      naturally, a massive PITA.
        javaPackageFragment = (IPackageFragment) element;
        IProject project = 
                javaPackageFragment.getJavaProject()
                    .getProject();
        try {
            if (!project.hasNature(CeylonNature.NATURE_ID)) {
                return false;
            }
        }
        catch (CoreException e) {
            e.printStackTrace();
            return false;
        }
        RefactoringProcessor processor = getProcessor();
        if (processor instanceof RenamePackageProcessor) {
            RenamePackageProcessor renamePackageProcessor = 
                    (RenamePackageProcessor) processor;
            String patterns = 
                    renamePackageProcessor.getFilePatterns();
            boolean messingWithCeylonFiles =
                    renamePackageProcessor.getUpdateQualifiedNames() && 
                    (patterns.equals("*") || patterns.contains("*.ceylon"));
            return !messingWithCeylonFiles;
        }
        else {
            //can't happen, I assume
            return false;
        }
    }

    @Override
    public String getName() {
        return "Rename participant for Ceylon source";
    }
    
    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm, 
            CheckConditionsContext context) {
        return new RefactoringStatus();
    }

    @Override
    public Change createChange(IProgressMonitor pm) 
            throws CoreException {
        return null;
    }
    
    @Override
    public Change createPreChange(IProgressMonitor pm) 
            throws CoreException {
        //TODO: don't ignore ((RenamePackageProcessor) getProcessor()).getUpdateTextualMatches()
        //TODO: don't ignore ((RenamePackageProcessor) processor).getUpdateReferences()
        try {
            final String newName = 
                    getArguments().getNewName();
            final String escapedName = 
                    escapePackageName(newName);
            final String oldName = 
                    javaPackageFragment.getElementName();
            final IProject project = 
                    javaPackageFragment.getJavaProject()
                            .getProject();

            final List<Change> changes = 
                    new ArrayList<Change>();
            TypeChecker tc = getProjectTypeChecker(project);
            if (tc==null) return null;
            for (PhasedUnit phasedUnit: 
                tc.getPhasedUnits().getPhasedUnits()) {

                final List<ReplaceEdit> edits = 
                        new ArrayList<ReplaceEdit>();
                phasedUnit.getCompilationUnit().visit(new Visitor() {
                    @Override
                    public void visit(ImportPath that) {
                        super.visit(that);
                        if (formatPath(that.getIdentifiers())
                                .equals(oldName)) {
                            edits.add(new ReplaceEdit(that.getStartIndex(), 
                                    that.getDistance(), escapedName));
                        }
                    }
                    @Override
                    public void visit(DocLink that) {
                        super.visit(that);
                        String packageName = packageName(that);
                        if (packageName!=null && 
                                packageName.equals(oldName)) {
                            Region region = packageRegion(that);
                            edits.add(new ReplaceEdit(region.getOffset(), 
                                    region.getLength(), newName));
                        }
                    }
                });

                if (!edits.isEmpty()) {
                    try {
                        FileVirtualFile<IProject, IResource, IFolder, IFile> virtualFile = 
                                vfsJ2C().getIFileVirtualFile(phasedUnit.getUnitFile());
                        IFile file = virtualFile.getNativeResource();
                        String path = 
                                file.getProjectRelativePath()
                                    .toPortableString();
                        TextFileChange change = 
                                fileChanges.get(path);
                        if (change==null) {
                            change = new TextFileChange(file.getName(), file);
                            change.setEdit(new MultiTextEdit());
                            changes.add(change);
                            fileChanges.put(path, change);
                        }
                        for (ReplaceEdit edit: edits) {
                            change.addEdit(edit);
                        }
                    }       
                    catch (Exception e) { 
                        e.printStackTrace(); 
                    }
                }

            }

            if (changes.isEmpty()) {
                return null;
            }
            else {
                CompositeChange result = 
                        new CompositeChange("Ceylon source changes") {
                    @Override
                    public Change perform(IProgressMonitor pm) 
                            throws CoreException {
                        fileChanges.clear();
                        return super.perform(pm);
                    }
                };
                for (Change change: changes) {
                    result.add(change);
                }
                return result;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}