package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

public class RenameRefactoring extends AbstractRefactoring {
    
	private static class FindReferencesVisitor extends FindReferenceVisitor {
	    private final Declaration declaration;
        private FindReferencesVisitor(Declaration declaration) {
            super(declaration);
            this.declaration = declaration;
        }
        @Override
        protected boolean isReference(Declaration ref) {
            return super.isReference(ref) ||
                    ref!=null && ref.refines(declaration);
        }
    }

	private String newName;
	private final Declaration declaration;
	private int count = 0;

	public RenameRefactoring(ITextEditor editor) {
	    super(editor);
	    
		Declaration refDec = getReferencedDeclaration(node);
		if (refDec!=null) {
            declaration = refDec.getRefinedDeclaration();
    		newName = declaration.getName();
            for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                FindReferencesVisitor frv = new FindReferencesVisitor(declaration);
                FindRefinementsVisitor fdv = new FindRefinementsVisitor(declaration);
                pu.getCompilationUnit().visit(frv);
                pu.getCompilationUnit().visit(fdv);
                count += frv.getNodes().size() + fdv.getDeclarationNodes().size();
            }
		}
		else {
		    declaration = null;
		}
	}
	
	@Override
	boolean isEnabled() {
	    return declaration!=null;
	}
	
	public int getCount() {
		return count;
	}

	public String getName() {
		return "Rename";
	}

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		// Check parameters retrieved from editor context
		return new RefactoringStatus();
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
        CompositeChange cc = new CompositeChange("Rename");
        for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
    		TextFileChange tfc = new TextFileChange("Rename", CeylonBuilder.getFile(pu));
    		tfc.setEdit(new MultiTextEdit());
    		if (declaration!=null) {
    			FindReferencesVisitor frv = new FindReferencesVisitor(declaration);
    			pu.getCompilationUnit().visit(frv);
    			for (Node node: frv.getNodes()) {
    	            renameNode(tfc, node);
    			}
    			FindRefinementsVisitor fdv = new FindRefinementsVisitor(declaration);
    			pu.getCompilationUnit().visit(fdv);
    			for (Tree.Declaration node: fdv.getDeclarationNodes()) {
    			    renameNode(tfc, node);
    			}
    		}
    		if (tfc.getEdit().hasChildren()) {
    		    cc.add(tfc);
    		}
        }
		return cc;
	}

	private void renameNode(TextFileChange tfc, Node node) {
	    Node identifyingNode = getIdentifyingNode(node);
		tfc.addEdit(new ReplaceEdit(identifyingNode.getStartIndex(), 
		        identifyingNode.getText().length(), newName));
	}

	public void setNewName(String text) {
		newName = text;
	}
	
	public Declaration getDeclaration() {
		return declaration;
	}
}
