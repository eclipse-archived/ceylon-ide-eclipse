package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.refactor.RenamePackageRefactoringParticipant.getSourceDirs;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
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
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedType;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;

public class RenameTypeRefactoringParticipant extends RenameParticipant {

	private IMember javaDeclaration;

	protected boolean initialize(Object element) {
		javaDeclaration= (IMember) element;
		return true;
	}

	public String getName() {
		return "Rename participant for Ceylon source";
	}
	
	public RefactoringStatus checkConditions(IProgressMonitor pm, CheckConditionsContext context) {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException {
		
		final String newName = getArguments().getNewName();
		IResource[] roots= getSourceDirs(javaDeclaration);  // limit to source dirs in the current project
		String[] fileNamePatterns = { "*.ceylon" }; // all files with file suffix '.ceylon'
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePatterns, false);
		Pattern pattern = Pattern.compile("\\b"+javaDeclaration.getElementName()+"\\b"); // only find the simple name of the type
        final String oldName = javaDeclaration.getElementName();
        final IProject project = javaDeclaration.getJavaProject().getProject();
		
        final HashMap<IFile,Change> changes= new HashMap<IFile,Change>();
		TextSearchRequestor collector= new TextSearchRequestor() {
			public boolean acceptPatternMatch(final TextSearchMatchAccess matchAccess) throws CoreException {
                String relPath = matchAccess.getFile().getProjectRelativePath().removeFirstSegments(1).toPortableString();
                PhasedUnit phasedUnit= getProjectTypeChecker(project).getPhasedUnitFromRelativePath(relPath);
                phasedUnit.getCompilationUnit().visit(new Visitor() {
                    @Override
                    public void visit(ImportMemberOrType that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), that.getDeclarationModel());
                    }
                    @Override
                    public void visit(QualifiedMemberOrTypeExpression that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), that.getDeclaration());
                    }
                    @Override
                    public void visit(BaseMemberOrTypeExpression that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), that.getDeclaration());
                    }
                    @Override
                    public void visit(BaseType that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), that.getDeclarationModel());
                    }
                    @Override
                    public void visit(QualifiedType that) {
                        super.visit(that);
                        visitIt(that.getIdentifier(), that.getDeclarationModel());
                    }
                    protected void visitIt(Node node, Declaration dec) {
                        if (dec!=null && dec.getQualifiedNameString()
                                .equals(getQualifiedName(javaDeclaration))) {
                            IFile file= matchAccess.getFile();
                            TextFileChange change= (TextFileChange) changes.get(file);
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
                            ReplaceEdit edit = new ReplaceEdit(node.getStartIndex(), oldName.length(), newName);
                            change.addEdit(edit);
                            //display the change in the UI
                            change.addTextEditGroup(new TextEditGroup("Rename reference to '" + 
                                    oldName + "' to '" + newName + "' in " + file.getName(), edit));
                        }
                    }
                    protected String getQualifiedName(IMember dec) {
                        IJavaElement parent = dec.getParent();
                        if (parent instanceof ICompilationUnit) {
                            return parent.getParent().getElementName() + "::" + dec.getElementName();
                        }
                        else if (dec.getDeclaringType()!=null) {
                            return getQualifiedName(dec.getDeclaringType()) + "." + dec.getElementName();
                        }
                        else {
                            return "@";
                        }
                    }
                });
                return true;
			}
		};
		TextSearchEngine.create().search(scope, collector, pattern, pm);
		
		if (changes.isEmpty())
			return null;
		
		CompositeChange result= new CompositeChange("Ceylon source changes");
		for (Iterator<Change> iter= changes.values().iterator(); iter.hasNext();) {
			result.add((Change) iter.next());
		}
		return result;
	}
}