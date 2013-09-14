package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ltk.core.refactoring.participants.RenameProcessor;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;

public class RenamePackageRefactoringParticipant extends RenameParticipant {

	private IPackageFragment javaPackageFragment;

    @Override
	protected boolean initialize(Object element) {
		javaPackageFragment= (IPackageFragment) element;
		return getProcessor() instanceof RenameProcessor &&
		        getProjectTypeChecker(javaPackageFragment.getJavaProject().getProject())!=null;
	}

    @Override
	public String getName() {
		return "Rename participant for Ceylon source";
	}
	
    @Override
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) {
		return new RefactoringStatus();
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException {
		
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
            });
            
            if (!edits.isEmpty()) {
                try {
                    final IFile file = ((IFileVirtualFile) phasedUnit.getUnitFile()).getFile();
                    IFile movedFile = getMovedFile(newName, file);
                    TextFileChange change= new MovingTextFileChange(movedFile.getName(), movedFile, file);
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
		    CompositeChange result = new CompositeChange("Ceylon source changes");
		    for (Change change: changes) {
		        result.add(change);
		    }
		    return result;
		}
	}
	
	private IFile getMovedFile(final String newName, IFile file) {
		String oldPath = javaPackageFragment.getElementName().replace('.', '/');
        String newPath = newName.replace('.', '/');
        String replaced = file.getProjectRelativePath().toString()
        		.replace(oldPath, newPath);
        return file.getProject().getFile(replaced);
	}
}