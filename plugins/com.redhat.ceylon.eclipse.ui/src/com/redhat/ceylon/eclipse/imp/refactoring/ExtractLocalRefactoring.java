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
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Visitor;
import com.redhat.ceylon.eclipse.imp.parser.CeylonParseController;

public class ExtractLocalRefactoring extends Refactoring {
	private static final class FindStatementVisitor extends Visitor {
		Tree.Term term;
		boolean found = false;
		Tree.Statement statement;
		public Tree.Statement getStatement() {
			return statement;
		}
		FindStatementVisitor(Tree.Term term) {
			this.term=term;
		}
		@Override
		public void visit(Tree.Term that) {
			super.visit(that);
			if (that==term) {
				found=true;
			}
		}
		@Override
		public void visit(Tree.Statement that) {
			super.visit(that);
			if (found) {
				found=false;
				statement = that;
			}
		}
	}

	private final IFile fSourceFile;
	private final Node fNode;
	private final ITextEditor fEditor;
	private final CeylonParseController parseController;
	private String name;
	private boolean explicitType;

	public ExtractLocalRefactoring(ITextEditor editor) {

		fEditor = editor;

		IASTFindReplaceTarget frt = (IASTFindReplaceTarget) fEditor;
		IEditorInput input = editor.getEditorInput();
		parseController = (CeylonParseController) frt.getParseController();

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput = (IFileEditorInput) input;
			fSourceFile = fileInput.getFile();
			fNode = findNode(frt);
			name = "temp";
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
		String indent = getIndent(node);
		tfc.addEdit(new InsertEdit(node.getStartIndex(),
				( explicitType ? term.getTypeModel().getProducedTypeName() : "value") + 
				" " + name + " = " + exp + ";" + indent));
		tfc.addEdit(new ReplaceEdit(start, length, name));
		return tfc;
	}

	private String getIndent(Node node) {
		int prevIndex = node.getToken().getTokenIndex()-1;
		if (prevIndex>=0) {
			Token prevToken = parseController.getTokenStream().get(prevIndex);
			if (prevToken.getChannel()==Token.HIDDEN_CHANNEL) {
				return prevToken.getText();
			}
		}
		return "";
	}

	public void setName(String text) {
		name = text;
	}
	
	public void setExplicitType() {
		this.explicitType = !explicitType;
	}
	
}
