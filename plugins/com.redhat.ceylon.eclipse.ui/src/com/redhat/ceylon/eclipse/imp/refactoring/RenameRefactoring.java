package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

public class RenameRefactoring extends Refactoring {
	private final IProject fProject;
	//private final Node fNode;
	//private final ITextEditor fEditor;
	//private final CeylonParseController parseController;
	private String newName;
	private final Declaration dec;
	private int count;

	public RenameRefactoring(ITextEditor editor) {

		IASTFindReplaceTarget frt = (IASTFindReplaceTarget) editor;
		IEditorInput input = editor.getEditorInput();

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			fProject = fileInput.getFile().getProject();
			Node node = findNode((CeylonParseController) frt.getParseController(), frt);
			dec = CeylonReferenceResolver.getReferencedDeclaration(node);
			newName = dec.getName();
            FindReferenceVisitor frv = new FindReferenceVisitor(dec);
            count = 0;
            for (PhasedUnit pu: CeylonBuilder.getUnits(fProject)) {
                pu.getCompilationUnit().visit(frv);
                count += frv.getNodes().size();
                frv.getNodes().clear();
            }
		} 
		else {
		    fProject = null;
			dec = null;
			count = 0;
		}
	}
	
	public int getCount() {
		return count;
	}

	private Node findNode(CeylonParseController cpc, IASTFindReplaceTarget frt) {
		return cpc.getSourcePositionLocator()
				.findNode(cpc.getRootNode(), frt.getSelection().x, 
						frt.getSelection().x+frt.getSelection().y);
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
        for (PhasedUnit pu: CeylonBuilder.getUnits(fProject)) {
    		TextFileChange tfc = new TextFileChange("Rename", CeylonBuilder.getFile(pu));
    		tfc.setEdit(new MultiTextEdit());
    		if (dec!=null) {
    			FindReferenceVisitor frv = new FindReferenceVisitor(dec);
    			pu.getCompilationUnit().visit(frv);
    			for (Node node: frv.getNodes()) {
    	            renameNode(tfc, node);
    			}
    			FindDeclarationVisitor fdv = new FindDeclarationVisitor(dec);
    			pu.getCompilationUnit().visit(fdv);
    			if (fdv.getDeclarationNode()!=null) {
    			    renameNode(tfc, fdv.getDeclarationNode());
    			}
    		}
    		if (tfc.getEdit().hasChildren()) {
    		    cc.add(tfc);
    		}
        }
		return cc;
	}

	private void renameNode(TextFileChange tfc, Node node) {
		node = CeylonSourcePositionLocator.getIdentifyingNode(node);
		tfc.addEdit(new ReplaceEdit(node.getStartIndex(), 
				node.getText().length(), newName));
	}

	public void setNewName(String text) {
		newName = text;
	}
	
	public Declaration getDeclaration() {
		return dec;
	}
}
