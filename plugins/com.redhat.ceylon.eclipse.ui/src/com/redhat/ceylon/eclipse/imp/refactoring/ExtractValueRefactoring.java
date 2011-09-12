package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.getIndent;

import java.util.Iterator;

import org.antlr.runtime.Token;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.imp.services.IASTFindReplaceTarget;
import org.eclipse.jface.text.Region;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class ExtractValueRefactoring extends Refactoring {
	private final IFile fSourceFile;
	private final Node fNode;
	private final ITextEditor fEditor;
	private final CeylonParseController parseController;
	private String newName;
	private boolean explicitType;
	private boolean getter;

	public ExtractValueRefactoring(ITextEditor editor) {

		fEditor = editor;

		IASTFindReplaceTarget frt = (IASTFindReplaceTarget) fEditor;
		IEditorInput input = editor.getEditorInput();
		parseController = (CeylonParseController) frt.getParseController();

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			fSourceFile = fileInput.getFile();
			fNode = parseController.getSourcePositionLocator().findNode(frt);
			Node node = fNode;
			if (node instanceof Tree.Expression) {
				node = ((Tree.Expression) node).getTerm();
			}
			if (node instanceof Tree.InvocationExpression) {
				node = ((Tree.InvocationExpression) node).getPrimary();
			}
			if (node instanceof Tree.StaticMemberOrTypeExpression) {
				newName = ((Tree.StaticMemberOrTypeExpression) node).getIdentifier().getText();
				newName = Character.toLowerCase(newName.charAt(0)) + 
						newName.substring(1);
			}
			else {
				newName = "temp";
			}
		} 
		else {
			fSourceFile = null;
			fNode = null;
		}
	}

	public String getName() {
		return "Extract value";
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
		TextFileChange tfc = new TextFileChange("Extract value", fSourceFile);
		tfc.setEdit(new MultiTextEdit());
		Tree.Term term = (Tree.Term) fNode;
		Integer start = fNode.getStartIndex();
		int length = fNode.getStopIndex()-start+1;
		Region region = new Region(start, length);
		String exp = "";
		for (Iterator<Token> ti = parseController.getTokenIterator(region); ti.hasNext();) {
			exp+=ti.next().getText();
		}
		FindStatementVisitor fsv = new FindStatementVisitor(term);
		parseController.getRootNode().visit(fsv);
		Node node = fsv.getStatement();
		if (node instanceof Tree.Declaration) {
			Tree.AnnotationList anns = ((Tree.Declaration) node).getAnnotationList();
			if (!anns.getAnnotations().isEmpty()) {
				node = anns.getAnnotations().get(0);
			}
		}
		String indent = getIndent(parseController.getTokenStream(), node);
		tfc.addEdit(new InsertEdit(node.getStartIndex(),
				( explicitType ? term.getTypeModel().getProducedTypeName() : "value") + " " + 
				newName + (getter ? " { return " + exp  + "; } " : " = " + exp + ";") + 
				indent));
		tfc.addEdit(new ReplaceEdit(start, length, newName));
		return tfc;
	}

	public void setNewName(String text) {
		newName = text;
	}
	
	public String getNewName() {
		return newName;
	}
	
	public void setExplicitType() {
		this.explicitType = !explicitType;
	}
	
	public void setGetter() {
		this.getter = !getter;
	}
	
}
