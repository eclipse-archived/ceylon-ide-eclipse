package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.corext.refactoring.rename.RenamePackageProcessor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.DocLink;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;

public class RenamePackageRefactoringParticipant extends RenameParticipant {

    private IPackageFragment javaPackageFragment;
    
    private static Map<IPackageFragment,String> allPackagesMoving = new HashMap<IPackageFragment,String>();

    @Override
    protected boolean initialize(Object element) {
        javaPackageFragment= (IPackageFragment) element;
        final String newName = getArguments().getNewName();
        RefactoringProcessor processor = getProcessor();
        if (processor instanceof RenamePackageProcessor) {
            RenamePackageProcessor renamePackageProcessor = 
                    (RenamePackageProcessor) processor;
            String patterns = renamePackageProcessor.getFilePatterns();
            if (renamePackageProcessor.getUpdateQualifiedNames() &&
                    (patterns.equals("*") || patterns.contains("*.ceylon"))) {
                return false;
            }
        }
        else {
            return false;
        }
        allPackagesMoving.put(javaPackageFragment, newName);
        return getProjectTypeChecker(javaPackageFragment.getJavaProject().getProject())!=null;
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
    public Change createChange(IProgressMonitor pm) throws CoreException {
        try {
            final String newName = getArguments().getNewName();
            final String oldName = javaPackageFragment.getElementName();
            final IProject project = javaPackageFragment.getJavaProject().getProject();

            final List<Change> changes = new ArrayList<Change>();
            TypeChecker tc = getProjectTypeChecker(project);
            if (tc==null) return null;
            for (PhasedUnit phasedUnit: tc.getPhasedUnits().getPhasedUnits()) {

                final List<ReplaceEdit> edits = new ArrayList<ReplaceEdit>();
                phasedUnit.getCompilationUnit().visit(new Visitor() {
                    @Override
                    public void visit(ImportPath that) {
                        super.visit(that);
                        if (formatPath(that.getIdentifiers()).equals(oldName)) {
                            edits.add(new ReplaceEdit(that.getStartIndex(), 
                                    oldName.length(), newName));
                        }
                    }
                    @Override
                    public void visit(DocLink that) {
                        super.visit(that);
                        //TODO: fix copy/paste from RenameRefactoring
                        String text = that.getText();
                        int offset = that.getStartIndex();
                        
                        int pipeIndex = text.indexOf("|");
                        if (pipeIndex > -1) {
                            text = text.substring(pipeIndex + 1);
                            offset += pipeIndex + 1;
                        }
                        
                        int scopeIndex = text.indexOf("::");
                        if (scopeIndex>=0 && text.substring(scopeIndex).endsWith(oldName)) {
                            edits.add(new ReplaceEdit(offset, 
                                    oldName.length(), newName));
                        }
                    }
                });

                if (!edits.isEmpty()) {
                    try {
                        final IFile file = ((IFileVirtualFile) phasedUnit.getUnitFile()).getFile();
                        IFile movedFile = getMovedFile(file);
                        TextFileChange change = 
                                new MovingTextFileChange(movedFile.getName(), movedFile, file);
                        change.setEdit(new MultiTextEdit());
                        for (ReplaceEdit edit: edits) {
                            change.addEdit(edit);
                        }
                        changes.add(change);
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
                CompositeChange result = new CompositeChange("Ceylon source changes") {
                    @Override
                    public Change perform(IProgressMonitor pm) throws CoreException {
                        allPackagesMoving.clear();
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
    
    private static IFile getMovedFile(IFile file) {
        for (Map.Entry<IPackageFragment,String> e: allPackagesMoving.entrySet()) {
            IPackageFragment javaPackageFragment = e.getKey();
            String newName = e.getValue();
            String oldPath = javaPackageFragment.getElementName().replace('.', '/');
            String newPath = newName.replace('.', '/');
            IPath pathInSourceFolder = file.getParent().getProjectRelativePath()
                    .removeFirstSegments(1); //TODO: lame, it assumes a the source folder belongs directly to the project
            if (pathInSourceFolder.toPortableString().equals(oldPath)) {
                return file.getProject().getFile(file.getParent().getProjectRelativePath()
                        .removeLastSegments(pathInSourceFolder.segmentCount())
                        .append(newPath).append(file.getName()));
            }
        }
        return file;
    }
}