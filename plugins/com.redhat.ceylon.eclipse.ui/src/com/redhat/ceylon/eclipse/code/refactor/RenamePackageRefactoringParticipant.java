package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class RenamePackageRefactoringParticipant extends RenameParticipant {

	private IPackageFragment javaPackageFragment;

    @Override
	protected boolean initialize(Object element) {
		javaPackageFragment= (IPackageFragment) element;
		return true;
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
		
		final String newName= getArguments().getNewName();
		IResource[] roots = getSourceDirs(javaPackageFragment);  // limit to source dirs in the current project
		String[] fileNamePatterns= { "*.ceylon" }; // all files with file suffix '.ceylon'
		FileTextSearchScope scope= FileTextSearchScope.newSearchScope(roots , fileNamePatterns, false);
		final String oldName = javaPackageFragment.getElementName();
        final IProject project = javaPackageFragment.getJavaProject().getProject();
		
        final HashMap<IFile,Change> changes= new HashMap<IFile,Change>();
		TextSearchRequestor collector= new TextSearchRequestor() {
			public boolean acceptPatternMatch(final TextSearchMatchAccess matchAccess) throws CoreException {
			    String relPath = matchAccess.getFile().getProjectRelativePath().removeFirstSegments(1).toPortableString();
                PhasedUnit phasedUnit= getProjectTypeChecker(project).getPhasedUnitFromRelativePath(relPath);
			    phasedUnit.getCompilationUnit().visit(new Visitor() {
			        @Override
			        public void visit(ImportPath that) {
			            super.visit(that);
			            if (formatPath(that.getIdentifiers()).equals(oldName)) {
		                    IFile file= matchAccess.getFile();
		                    TextFileChange change= (TextFileChange) changes.get(file);
		                    if (change == null) {
		                        TextChange textChange= getTextChange(file);
		                        if (textChange != null) {
		                            return;
		                            //return false; // don't try to merge changes
		                        }
		                        change= new TextFileChange(file.getName(), getMovedFile(newName, file));
		                        change.setEdit(new MultiTextEdit());
		                        changes.put(file, change);
		                    }
		                    ReplaceEdit edit = new ReplaceEdit(that.getStartIndex(), oldName.length(), newName);
		                    change.addEdit(edit);
		                    //display the change in the UI
		                    change.addTextEditGroup(new TextEditGroup("Rename package reference '" + 
		                            oldName + "' to '" + newName + "' in " + file.getName(), edit));
			            }
			        }
                });
				return true;
			}
		};
        Pattern pattern= Pattern.compile("\\b(package|module|import)\\s+"+oldName.replace(".", "\\.")+"\\b");
		TextSearchEngine.create().search(scope, collector, pattern, pm);
		
		if (changes.isEmpty())
			return null;
		
		CompositeChange result= new CompositeChange("Ceylon source changes");
		for (Change change: changes.values()) {
			result.add(change);
		}
		return result;
	}

    static IResource[] getSourceDirs(IJavaElement java) throws JavaModelException {
        IProject project = java.getJavaProject().getProject();
		IPackageFragmentRoot[] paths = JavaCore.create(project).getAllPackageFragmentRoots();
        List<IResource> list = new ArrayList<IResource>();
		for (int i=0; i<paths.length; i++) {
		    if (paths[i].getKind()==IPackageFragmentRoot.K_SOURCE) {
		        IResource r = paths[i].getResource();
		        if (r.exists()) list.add(r);
		    }
		}
		return list.toArray(new IResource[0]);
    }

	private IFile getMovedFile(final String newName, IFile file) {
		String oldPath = javaPackageFragment.getElementName().replace('.', '/');
        String newPath = newName.replace('.', '/');
        String replaced = file.getProjectRelativePath().toString()
        		.replace(oldPath, newPath);
        return file.getProject().getFile(replaced);
	}
}