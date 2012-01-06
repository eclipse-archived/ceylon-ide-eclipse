package com.redhat.ceylon.eclipse.imp.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class ConvertToNamedArgumentsRefactoring extends AbstractRefactoring {

	public ConvertToNamedArgumentsRefactoring(ITextEditor editor) {
	    super(editor);
	}

    @Override
    boolean isEnabled() {
        return node instanceof Tree.PositionalArgumentList;
    }

	public String getName() {
		return "Convert to Named Arguments";
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
		TextChange tfc = newLocalChange();
		convertInFile(tfc);			
		return tfc;
	}

    private void convertInFile(TextChange tfc) {
        tfc.setEdit(new MultiTextEdit());
		Tree.PositionalArgumentList argList = (Tree.PositionalArgumentList) node;
		Integer start =node.getStartIndex();
		int length = node.getStopIndex()-start+1;
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
    }
	
}
