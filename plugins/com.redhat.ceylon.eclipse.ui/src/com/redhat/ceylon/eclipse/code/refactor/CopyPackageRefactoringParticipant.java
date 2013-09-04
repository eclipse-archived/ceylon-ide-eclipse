package com.redhat.ceylon.eclipse.code.refactor;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.CopyParticipant;
import org.eclipse.ltk.core.refactoring.participants.ReorgExecutionLog;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;

public class CopyPackageRefactoringParticipant extends CopyParticipant {

    private IPackageFragment javaPackageFragment;

    @Override
    protected boolean initialize(Object element) {
        javaPackageFragment= (IPackageFragment) element;
        return true;
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
                
                //TODO: this just does a text search/replace - of 
                //      course it should use the lexer instead!
                
                newPackage = dest.getPackageFragment(newName).getResource();
                IResource[] roots = { newPackage };  // limit to source dirs in the current project
                String[] fileNamePatterns= { "*.ceylon" }; // all files with file suffix '.ceylon'
                FileTextSearchScope scope= FileTextSearchScope.newSearchScope(roots , fileNamePatterns, false);
                final String oldName = javaPackageFragment.getElementName();
                Pattern pattern= Pattern.compile("\\b(package|module|import)\\s+"+oldName.replace(".", "\\.")+"\\b[^.]");
                
                final HashMap<IFile,Change> changes= new HashMap<IFile,Change>();
                TextSearchRequestor collector= new TextSearchRequestor() {
                    public boolean acceptPatternMatch(TextSearchMatchAccess matchAccess) throws CoreException {
                        IFile file= matchAccess.getFile();
                        TextFileChange change= (TextFileChange) changes.get(file);
                        if (change == null) {
                            TextChange textChange= getTextChange(file);
                            if (textChange != null) {
                                return false; // don't try to merge changes
                            }
                            change= new TextFileChange(file.getName(), file);
                            change.setEdit(new MultiTextEdit());
                            changes.put(file, change);
                        }
                        ReplaceEdit edit= new ReplaceEdit(matchAccess.getMatchOffset()+matchAccess.getMatchLength()-1-oldName.length(), 
                                oldName.length(), newName);
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
                result.perform(pm);
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

    private IFile getMovedFile(final String newName, IFile file) {
        return file.getProject()
                .getFile(file.getProjectRelativePath().toString()
                        .replace(javaPackageFragment.getElementName().replace('.', '/'), newName.replace('.', '/')));
    }
}
