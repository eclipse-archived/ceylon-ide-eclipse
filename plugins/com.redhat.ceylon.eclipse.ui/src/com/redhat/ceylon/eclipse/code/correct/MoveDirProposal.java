package com.redhat.ceylon.eclipse.code.correct;

import static com.redhat.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.MINOR_CHANGE;

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
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;

final class MoveDirProposal implements ICompletionProposal {
    
    private final Shell shell;
    private final String pn;
    private final String cpn;
    private final IPath sourceDir;
    private final IProject project;

    MoveDirProposal(Shell shell, String pn, String cpn,
            IPath sourceDir, IProject project) {
        this.shell = shell;
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
        new RefactoringStarter().activate(new RenamePackageWizard(new RenameRefactoring(processor)),
                shell, "Rename Package", 4);
    }

    static void addMoveDirProposal(final IFile file, final Tree.CompilationUnit cu,
            final IProject project, Collection<ICompletionProposal> proposals, 
            final Shell shell) {
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
            proposals.add(new MoveDirProposal(shell, pn, cpn, sourceDir, project));
//        }
    }

}