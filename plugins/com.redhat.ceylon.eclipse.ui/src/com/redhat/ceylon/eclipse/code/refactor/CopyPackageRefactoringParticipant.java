package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.compiler.typechecker.tree.Util.formatPath;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static org.eclipse.search.ui.text.FileTextSearchScope.newSearchScope;

import java.util.HashMap;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportPath;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

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
                newPackage = dest.getPackageFragment(newName).getResource();
                IResource[] roots = { newPackage };  // limit to files in the tree being pasted!
                String[] fileNamePatterns = { "*.ceylon" };
                FileTextSearchScope scope = newSearchScope(roots , fileNamePatterns, false);
                final String oldName = javaPackageFragment.getElementName();
                final IProject project = javaPackageFragment.getJavaProject().getProject();
                
                final HashMap<IFile,Change> changes= new HashMap<IFile,Change>();
                TextSearchRequestor collector= new TextSearchRequestor() {
                    public boolean acceptPatternMatch(final TextSearchMatchAccess matchAccess) throws CoreException {
                        String relPath = javaPackageFragment.getResource().getProjectRelativePath().removeFirstSegments(1)
                                .append(matchAccess.getFile().getProjectRelativePath()
                                        .removeFirstSegments(newPackage.getProjectRelativePath().segmentCount()))
                                .toPortableString();
                        PhasedUnit phasedUnit= getProjectTypeChecker(project).getPhasedUnitFromRelativePath(relPath);
                        phasedUnit.getCompilationUnit().visit(new Visitor() {
                            @Override
                            public void visit(ImportPath that) {
                                super.visit(that);
                                if (formatPath(that.getIdentifiers()).equals(oldName)) {
                                    IFile file = matchAccess.getFile();
                                    TextFileChange change = (TextFileChange) changes.get(file);
                                    if (change == null) {
                                        TextChange textChange= getTextChange(file);
                                        if (textChange != null) {
                                            return;
                                            //return false; // don't try to merge changes
                                        }
                                        change= new TextFileChange(file.getName(), file);
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
                Pattern pattern = Pattern.compile("\\b"+oldName.replace(".", "\\.")+"\\b");
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

}
