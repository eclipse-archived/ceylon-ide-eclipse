package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.BaseType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.ImportMemberOrType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedMemberOrTypeExpression;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.QualifiedType;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.core.vfs.IFileVirtualFile;

public class RenameJavaElementRefactoringParticipant extends RenameParticipant {

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
		
        final IProject project = javaDeclaration.getJavaProject().getProject();
		final String newName = getArguments().getNewName();
        final String oldName = javaDeclaration.getElementName();
		
        final HashMap<IFile,Change> changes= new HashMap<IFile,Change>();
        for (PhasedUnit phasedUnit: getProjectTypeChecker(project).getPhasedUnits().getPhasedUnits()) {
            final List<ReplaceEdit> edits = new ArrayList<ReplaceEdit>();
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
                        edits.add(new ReplaceEdit(node.getStartIndex(), oldName.length(), newName));
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
            if (!edits.isEmpty()) {
                try {
                    IFile file = ((IFileVirtualFile) phasedUnit.getUnitFile()).getFile();
                    TextFileChange change= new TextFileChange(file.getName(), file);
                    change.setEdit(new MultiTextEdit());
                    changes.put(file, change);
                    for (ReplaceEdit edit: edits) {
                        change.addEdit(edit);
                    }
                }       
                catch (Exception e) { 
                    e.printStackTrace(); 
                }
            }
		}
		
		if (changes.isEmpty())
			return null;
		
		CompositeChange result= new CompositeChange("Ceylon source changes");
		for (Iterator<Change> iter= changes.values().iterator(); iter.hasNext();) {
			result.add((Change) iter.next());
		}
		return result;
	}
}