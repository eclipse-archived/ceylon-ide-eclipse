package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.core.CeylonReferenceResolver;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;
import com.redhat.ceylon.eclipse.util.FindDeclarationVisitor;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

public class RenameRefactoring extends Refactoring {
	private final IFile fSourceFile;
	private final Node fNode;
	private final ITextEditor fEditor;
	private final CeylonParseController parseController;
	private String newName;
	private final Declaration dec;
	private final int count;

	public RenameRefactoring(ITextEditor editor) {

		fEditor = editor;

		IASTFindReplaceTarget frt = (IASTFindReplaceTarget) fEditor;
		IEditorInput input = editor.getEditorInput();
		parseController = (CeylonParseController) frt.getParseController();

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			fSourceFile = fileInput.getFile();
			fNode = findNode(frt);
			dec = CeylonReferenceResolver.getReferencedDeclaration(fNode);
			newName = dec.getName();
			FindReferenceVisitor frv = new FindReferenceVisitor(dec);
			parseController.getRootNode().visit(frv);
			count = frv.getNodes().size();
		} 
		else {
			fSourceFile = null;
			fNode = null;
			dec = null;
			count = 0;
		}
	}
	
	public int getCount() {
		return count;
	}

	private Node findNode(IASTFindReplaceTarget frt) {
		return parseController.getSourcePositionLocator()
				.findNode(parseController.getRootNode(), frt.getSelection().x, 
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
		TextFileChange tfc = new TextFileChange("Rename", fSourceFile);
		tfc.setEdit(new MultiTextEdit());
		if (dec!=null) {
			FindReferenceVisitor frv = new FindReferenceVisitor(dec) {
				@Override
				public void visit(Tree.ExtendedTypeExpression that) {}
			};
			parseController.getRootNode().visit(frv);
			for (Node node: frv.getNodes()) {
	            renameNode(tfc, node);
			}
			FindDeclarationVisitor fdv = new FindDeclarationVisitor(dec);
			parseController.getRootNode().visit(fdv);
			renameNode(tfc, fdv.getDeclarationNode());
		}
		return tfc;
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
