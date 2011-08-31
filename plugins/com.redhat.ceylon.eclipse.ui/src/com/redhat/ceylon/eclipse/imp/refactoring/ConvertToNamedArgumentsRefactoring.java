package com.redhat.ceylon.eclipse.imp.refactoring;

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
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class ConvertToNamedArgumentsRefactoring extends Refactoring {

	private final IFile fSourceFile;
	private final Node fNode;
	private final ITextEditor fEditor;
	private final CeylonParseController parseController;

	public ConvertToNamedArgumentsRefactoring(ITextEditor editor) {

		fEditor = editor;

		IASTFindReplaceTarget frt = (IASTFindReplaceTarget) fEditor;
		IEditorInput input = editor.getEditorInput();
		parseController = (CeylonParseController) frt.getParseController();

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			fSourceFile = fileInput.getFile();
			fNode = findNode(frt);
		} 
		else {
			fSourceFile = null;
			fNode = null;
		}
	}

	private Node findNode(IASTFindReplaceTarget frt) {
		return parseController.getSourcePositionLocator()
				.findNode(parseController.getRootNode(), frt.getSelection().x, 
						frt.getSelection().x+frt.getSelection().y);
	}

	public String getName() {
		return "Convert to named arguments";
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
		TextFileChange tfc = new TextFileChange("Convert to named arguments", fSourceFile);
		tfc.setEdit(new MultiTextEdit());
		Tree.PositionalArgumentList argList = (Tree.PositionalArgumentList) fNode;
		Integer start = fNode.getStartIndex();
		int length = fNode.getStopIndex()-start+1;
		StringBuilder result = new StringBuilder().append(" {");
		boolean sequencedArgs = false;
		for (Tree.PositionalArgument arg: argList.getPositionalArguments()) {
			if (arg.getParameter().isSequenced() && argList.getEllipsis()==null) {
				if (sequencedArgs) result.append(",");
				sequencedArgs=true;
				result.append(" " + toString(arg.getExpression().getTerm()));
			}
			else {
				result.append(" " + arg.getParameter().getName() + "=" + 
						toString(arg.getExpression().getTerm()) + ";");
			}
		}
		result.append(" }");
		tfc.addEdit(new ReplaceEdit(start, length, result.toString()));			
		return tfc;
	}

	private String toString(final Tree.Term t) {
		Integer start = t.getStartIndex();
		int length = t.getStopIndex()-start+1;
		Region region = new Region(start, length);
		StringBuilder exp = new StringBuilder();
		for (Iterator<Token> ti = parseController.getTokenIterator(region); 
				ti.hasNext();) {
			exp.append(ti.next().getText());
		}
		return exp.toString();
	}
	
}
