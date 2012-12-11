package com.redhat.ceylon.eclipse.code.refactor;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IPackageFragment;
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

public class RenamePackageRefactoringParticipant extends RenameParticipant {

	private IPackageFragment javaPackageFragment;

	protected boolean initialize(Object element) {
		javaPackageFragment= (IPackageFragment) element;
		return true;
	}

	public String getName() {
		return "Rename participant for Ceylon source";
	}
	
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException {
		
		final HashMap<IFile,Change> changes= new HashMap<IFile,Change>();
		final String newName= getArguments().getNewName();
		
		//TODO: this just does a text search/replace - of 
		//      course it should use the model instead!
		
		IResource[] roots= { javaPackageFragment.getJavaProject().getProject() };  // limit to the current project
		String[] fileNamePatterns= { "*.ceylon" }; // all files with file suffix '.ceylon'
		FileTextSearchScope scope= FileTextSearchScope.newSearchScope(roots , fileNamePatterns, false);
		Pattern pattern= Pattern.compile(javaPackageFragment.getElementName()); // only find the simple name of the type
		
		TextSearchRequestor collector= new TextSearchRequestor() {
			public boolean acceptPatternMatch(TextSearchMatchAccess matchAccess) throws CoreException {
				IFile file= matchAccess.getFile();
				TextFileChange change= (TextFileChange) changes.get(file);
				if (change == null) {
					TextChange textChange= getTextChange(file);
					if (textChange != null) {
						return false; // don't try to merge changes
					}
					change= new TextFileChange(file.getName(), getMovedFile(newName, file));
					change.setEdit(new MultiTextEdit());
					changes.put(file, change);
				}
				ReplaceEdit edit= new ReplaceEdit(matchAccess.getMatchOffset(), matchAccess.getMatchLength(), newName);
				change.addEdit(edit);
				change.addTextEditGroup(new TextEditGroup("Rename package reference to '" + newName + "'", edit));
				return true;
			}
		};
		TextSearchEngine.create().search(scope, collector, pattern, pm);
		
		if (changes.isEmpty())
			return null;
		
		CompositeChange result= new CompositeChange("Ceylon source changes");
		for (Change change: changes.values()) {
			result.add(change);
		}
		return result;
	}

	private IFile getMovedFile(final String newName, IFile file) {
		return file.getProject()
				.getFile(file.getProjectRelativePath().toString()
						.replace(javaPackageFragment.getElementName().replace('.', '/'), newName.replace('.', '/')));
	}
}