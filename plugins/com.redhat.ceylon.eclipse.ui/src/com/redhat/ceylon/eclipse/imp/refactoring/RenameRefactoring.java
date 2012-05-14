package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getIdentifyingNode;
import static com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver.getReferencedDeclaration;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
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
        private FindReferencesVisitor(Declaration declaration) {
            super(declaration);
        }
        @Override
        protected boolean isReference(Declaration ref) {
            return super.isReference(ref) ||
                    ref!=null && ref.refines(getDeclaration());
        }
        @Override
        protected boolean isReference(Declaration ref, String id) {
            return isReference(ref) && id!=null &&
                    getDeclaration().getName().equals(id); //TODO: really lame way to tell if its an alias!
        }
    }

	private String newName;
	private final Declaration declaration;

	public RenameRefactoring(ITextEditor editor) {
	    super(editor);
		Declaration refDec = getReferencedDeclaration(node);
		if (refDec!=null) {
            declaration = refDec.getRefinedDeclaration();
    		newName = declaration.getName();
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
	    if (declaration==null) {
	        return 0;
	    }
	    else {
            int count = 0;
            for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                if (searchInFile(pu)) {
                    count += countReferences(pu.getCompilationUnit());
                }
            }
            if (searchInEditor()) {
                count += countReferences(editor.getParseController().getRootNode());
            }
    		return count;
	    }
	}
	
	private int countReferences(Tree.CompilationUnit cu) {
        FindReferencesVisitor frv = new FindReferencesVisitor(declaration);
        FindRefinementsVisitor fdv = new FindRefinementsVisitor(frv.getDeclaration());
        cu.visit(frv);
        cu.visit(fdv);
        return frv.getNodes().size() + fdv.getDeclarationNodes().size();
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
	    Declaration existing = declaration.getContainer()
                        .getMemberOrParameter(declaration.getUnit(), newName, null);
        if (null!=existing && !existing.equals(declaration)) {
	        return createWarningStatus("An existing declaration named '" +
	            newName + "' already exists in the same scope");
	    }
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
        CompositeChange cc = new CompositeChange(getName());
        List<PhasedUnit> units = CeylonBuilder.getUnits(project);
        pm.beginTask(getName(), units.size());
        int i=0;
        for (PhasedUnit pu: units) {
            if (searchInFile(pu)) {
                TextFileChange tfc = newTextFileChange(pu);
                renameInFile(tfc, cc, pu.getCompilationUnit());
                pm.worked(i++);
            }
        }
        if (searchInEditor()) {
            DocumentChange dc = newDocumentChange();
            renameInFile(dc, cc, editor.getParseController().getRootNode());
            pm.worked(i++);
        }
        pm.done();
		return cc;
	}

    private void renameInFile(TextChange tfc, CompositeChange cc, Tree.CompilationUnit root) {
        tfc.setEdit(new MultiTextEdit());
        if (declaration!=null) {
        	FindReferencesVisitor frv = new FindReferencesVisitor(declaration);
        	root.visit(frv);
        	for (Node node: frv.getNodes()) {
                renameNode(tfc, node);
        	}
        	FindRefinementsVisitor fdv = new FindRefinementsVisitor(frv.getDeclaration());
        	root.visit(fdv);
        	for (Tree.Declaration node: fdv.getDeclarationNodes()) {
        	    renameNode(tfc, node);
        	}
        }
        if (tfc.getEdit().hasChildren()) {
            cc.add(tfc);
        }
    }

	private void renameNode(TextChange tfc, Node node) {
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
