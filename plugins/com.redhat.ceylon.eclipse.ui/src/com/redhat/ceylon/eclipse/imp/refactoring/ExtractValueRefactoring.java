package com.redhat.ceylon.eclipse.imp.refactoring;

import static com.redhat.ceylon.eclipse.imp.parser.CeylonSourcePositionLocator.getIndent;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class ExtractValueRefactoring extends AbstractRefactoring {
	private String newName;
	private boolean explicitType;
	private boolean getter;

	public ExtractValueRefactoring(ITextEditor editor) {
	    super(editor);
	    newName = guessName();
	}
	
	/*public ExtractValueRefactoring(IQuickFixInvocationContext context) {
        super(context);
        newName = guessName();
    }*/

    public String getName() {
		return "Extract Value";
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
		TextFileChange tfc = new TextFileChange("Extract value", sourceFile);
		tfc.setEdit(new MultiTextEdit());
		Tree.Term term = (Tree.Term) node;
		Integer start = node.getStartIndex();
		int length = node.getStopIndex()-start+1;
		String exp = toString(term);
		FindStatementVisitor fsv = new FindStatementVisitor(term);
		rootNode.visit(fsv);
		Node statNode = fsv.getStatement();
		if (statNode instanceof Tree.Declaration) {
			Tree.AnnotationList anns = ((Tree.Declaration) statNode).getAnnotationList();
			if (anns!=null && !anns.getAnnotations().isEmpty()) {
				statNode = anns.getAnnotations().get(0);
			}
		}
		String indent = getIndent(tokenStream, statNode);
		tfc.addEdit(new InsertEdit(statNode.getStartIndex(),
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
