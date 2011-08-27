package com.redhat.ceylon.eclipse.imp.refactoring;

import org.antlr.runtime.CommonToken;
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
import com.redhat.ceylon.compiler.typechecker.ui.FindDeclarationVisitor;
import com.redhat.ceylon.compiler.typechecker.ui.FindReferenceVisitor;
import com.redhat.ceylon.eclipse.imp.occurrenceMarker.CeylonOccurrenceMarker;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;
import com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator;

public class RenameRefactoring extends Refactoring {
	private final IFile fSourceFile;
	private final Node fNode;
	private final ITextEditor fEditor;
	private final CeylonParseController parseController;
	private String name;
	private final Declaration dec;

	public RenameRefactoring(ITextEditor editor) {

		fEditor = editor;

		IASTFindReplaceTarget frt = (IASTFindReplaceTarget) fEditor;
		IEditorInput input = editor.getEditorInput();
		parseController = (CeylonParseController) frt.getParseController();

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			fSourceFile = fileInput.getFile();
			fNode = findNode(frt);
			dec = CeylonOccurrenceMarker.getDeclaration(fNode);
		} else {
			fSourceFile = null;
			fNode = null;
			dec = null;
		}
	}

	private Node findNode(IASTFindReplaceTarget frt) {
		return parseController.getSourcePositionLocator()
				.findNode(getRootNode(), frt.getSelection().x);
	}

	public String getName() {
		return "Rename...";
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
		FindReferenceVisitor frv = new FindReferenceVisitor(dec);
		getRootNode().visit(frv);
		for (Node node: frv.getNodes()) {
            renameNode(tfc, node);

		}
		FindDeclarationVisitor fdv = new FindDeclarationVisitor(dec);
		getRootNode().visit(fdv);
		renameNode(tfc, fdv.getDeclarationNode());
		}
		return tfc;
	}

	private Node getRootNode() {
		return (Node) parseController.getCurrentAst();
	}

	private void renameNode(TextFileChange tfc, Node node) {
		CommonToken token = CeylonSourcePositionLocator.getToken(node);
		if (token!=null) {
		tfc.addEdit(new ReplaceEdit(token.getStartIndex(), token.getText().length(),
				name));
		System.out.println(token.getStartIndex() + " to " + token.getStopIndex());
		}
	}

	public void setName(String text) {
		name = text;
	}
	
	public Declaration getDeclaration() {
		return dec;
	}
}
