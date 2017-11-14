/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.MINOR_CHANGE;
import static org.eclipse.ceylon.ide.eclipse.util.Highlights.styleProposal;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenamePackageProcessor;
import org.eclipse.jdt.internal.ui.refactoring.actions.RefactoringStarter;
import org.eclipse.jdt.internal.ui.refactoring.reorg.RenamePackageWizard;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ceylon.compiler.typechecker.tree.Tree;

final class MoveDirProposal 
        implements ICompletionProposal,
                   ICompletionProposalExtension6 {
    
    private final IQuickAssistInvocationContext invocationContext;
    private final String pn;
    private final String cpn;
    private final IPath sourceDir;
    private final IProject project;

    MoveDirProposal(IQuickAssistInvocationContext invocationContext, String pn, String cpn,
            IPath sourceDir, IProject project) {
        this.invocationContext = invocationContext;
        this.pn = pn;
        this.cpn = cpn;
        this.sourceDir = sourceDir;
        this.project = project;
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public Image getImage() {
        return MINOR_CHANGE; //TODO!!!!!
    }

    @Override
    public String getDisplayString() {
        return "Rename and move to '" + pn + "'";
    }

    @Override
    public StyledString getStyledDisplayString() {
        return styleProposal(getDisplayString(), true);
    }
    
    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public void apply(IDocument document) {
        IPackageFragment pfr = (IPackageFragment) JavaCore.create(project.getFolder(sourceDir.append(cpn.replace('.', '/'))));
        RenamePackageProcessor processor = new RenamePackageProcessor(pfr);
        processor.setNewElementName(pn);
        Shell shell = invocationContext.getSourceViewer().getTextWidget().getShell();
        new RefactoringStarter().activate(new RenamePackageWizard(new RenameRefactoring(processor)),
                shell, "Rename Package", 4);
    }

    static void addMoveDirProposal(final IFile file, final Tree.CompilationUnit cu,
            final IProject project, Collection<ICompletionProposal> proposals, 
            final IQuickAssistInvocationContext invocationContext) {
        Tree.ImportPath importPath;
        if (!cu.getPackageDescriptors().isEmpty()) {
            importPath = cu.getPackageDescriptors().get(0).getImportPath();
        }
        else if (!cu.getModuleDescriptors().isEmpty()) {
            importPath = cu.getModuleDescriptors().get(0).getImportPath();
        }
        else {
            return;
        }
        final String pn = formatPath(importPath.getIdentifiers());
        final String cpn = cu.getUnit().getPackage().getNameAsString();
        final IPath sourceDir = file.getProjectRelativePath()
                .removeLastSegments(file.getProjectRelativePath().segmentCount()-1);
//          final IPath relPath = sourceDir.append(pn.replace('.', '/'));
//          final IPath newPath = project.getFullPath().append(relPath);
//          if (!project.exists(newPath)) {
            proposals.add(new MoveDirProposal(invocationContext, pn, cpn, sourceDir, project));
//        }
    }

}