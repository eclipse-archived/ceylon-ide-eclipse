package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.CopyParticipant;
import org.eclipse.ltk.core.refactoring.participants.CopyProcessor;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.eclipse.core.vfs.vfsJ2C;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class CopyPackageRefactoringParticipant extends CopyParticipant {

    private IPackageFragment javaPackageFragment;

    @Override
    protected boolean initialize(Object element) {
        javaPackageFragment = (IPackageFragment) element;
        return getProcessor() instanceof CopyProcessor &&
                getProjectTypeChecker(javaPackageFragment.getJavaProject().getProject())!=null;
    }
    
    @Override
    public String getName() {
        return "Copy package participant for Ceylon source";
    }

    @Override
    public RefactoringStatus checkConditions(IProgressMonitor pm,
            CheckConditionsContext context) throws OperationCanceledException {
        return new RefactoringStatus();
    }

    public Change createChange(IProgressMonitor pm) throws CoreException {
        
        Change change = new Change() {
            IResource newPackage;
            
            @Override
            public Change perform(IProgressMonitor pm) throws CoreException {
                IPackageFragmentRoot dest = (IPackageFragmentRoot) getArguments().getDestination();
                ReorgExecutionLog executionLog = getArguments().getExecutionLog();
                final String newName = executionLog.getNewName(javaPackageFragment);
                if (newName == null || newName.isEmpty()) return null;
                newPackage = dest.getPackageFragment(newName).getResource();
                final String oldName = javaPackageFragment.getElementName();
                final IProject project = javaPackageFragment.getJavaProject().getProject();
                
                final List<Change> changes= new ArrayList<Change>();
                TypeChecker tc = getProjectTypeChecker(project);
                if (tc==null) return null;
                for (PhasedUnit phasedUnit: tc.getPhasedUnits().getPhasedUnits()) {                    
                    if (phasedUnit.getPackage().getNameAsString().equals(oldName)) {
                        updateRefsInCopiedFile(newName, oldName, changes, phasedUnit);
                    }
                }
                if (!changes.isEmpty()) {
                    CompositeChange result= new CompositeChange("Ceylon source changes");
                    for (Change change: changes) {
                        result.add(change);
                    }
                    result.perform(pm);
                }
                
                //no undo
                return null;
            }

            @Override
            public String getName() {
                return "Copy Ceylon Package";
            }

            @Override
            public void initializeValidationData(IProgressMonitor pm) {}

            @Override
            public RefactoringStatus isValid(IProgressMonitor pm)
                    throws CoreException, OperationCanceledException {
                return new RefactoringStatus();
            }

            @Override
            public Object getModifiedElement() {
                return newPackage;
            }
        };
        change.setEnabled(true);
        return change;
        
    }

    private void updateRefsInCopiedFile(final String newName, final String oldName,
            final List<Change> changes, PhasedUnit phasedUnit) {
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
        });
        if (!edits.isEmpty()) {
            try {
                IFile file = vfsJ2C.getIFileVirtualFile(phasedUnit.getUnitFile()).getNativeResource();
                IFile newFile = getMovedFile(newName, file);
                TextFileChange change= new TextFileChange(newFile.getName(), newFile);
                change.setEdit(new MultiTextEdit());
                changes.add(change);
                for (ReplaceEdit edit: edits) {
                    change.addEdit(edit);
                }
            }       
            catch (Exception e) { 
                e.printStackTrace(); 
            }
        }
    }

    private IFile getMovedFile(final String newName, IFile file) {
        String oldPath = javaPackageFragment.getElementName().replace('.', '/');
        String newPath = newName.replace('.', '/');
        IPath pathInSourceFolder = file.getParent().getProjectRelativePath()
                .removeFirstSegments(1); //TODO: lame, it assumes a the source folder belongs directly to the project
        if (pathInSourceFolder.toPortableString().equals(oldPath)) {
            return file.getProject().getFile(file.getParent().getProjectRelativePath()
                    .removeLastSegments(pathInSourceFolder.segmentCount())
                    .append(newPath).append(file.getName()));
        }
        return file;
    }
}
