package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importType;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.findStatement;
import static com.redhat.ceylon.eclipse.util.Indents.getIndent;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import java.util.HashSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.texteditor.ITextEditor;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.Indents;

public class ExtractValueRefactoring extends AbstractRefactoring {
	
	private String newName;
	private boolean explicitType;
	private boolean getter;

	public ExtractValueRefactoring(ITextEditor editor) {
	    super(editor);
	    newName = guessName(node);
	}
	
	/*public ExtractValueRefactoring(IQuickFixInvocationContext context) {
        super(context);
        newName = guessName();
    }*/
	
	@Override
	boolean isEnabled() {
	    return node instanceof Tree.Term;
	}

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
        Declaration existing = node.getScope()
                .getMemberOrParameter(node.getUnit(), newName, null, false);
        if (null!=existing) {
            return createWarningStatus("An existing declaration named '" +
                    newName + "' already exists in the same scope");
        }
		return new RefactoringStatus();
	}

	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		TextChange tfc = newLocalChange();
		extractInFile(tfc);
		return tfc;
	}

    private void extractInFile(TextChange tfc) throws CoreException {
        tfc.setEdit(new MultiTextEdit());
		IDocument doc = tfc.getCurrentDocument(null);
		
		Tree.Term term = (Tree.Term) node;
		Integer start = node.getStartIndex();
		int length = node.getStopIndex()-start+1;
		String exp = toString(unparenthesize(term));
		Tree.Statement statement = findStatement(rootNode, node);
		String typeDec;
		ProducedType tm = term.getTypeModel();
		if (tm==null || tm.isUnknown()) {
			typeDec = "dynamic";
		}
		else if (explicitType) {
			ProducedType type = node.getUnit().denotableType(tm);
			typeDec = type.getProducedTypeName();
			HashSet<Declaration> decs = new HashSet<Declaration>();
			importType(decs, type, rootNode);
			applyImports(tfc, decs, rootNode, doc);
		}
		else {
			 typeDec = "value";
		}
		String dec = typeDec + " " +  newName + 
				(getter ? " { return " + exp  + "; } " : " = " + exp + ";");
        tfc.addEdit(new InsertEdit(statement.getStartIndex(),
				dec + Indents.getDefaultLineDelimiter(doc) + getIndent(statement, doc)));
		tfc.addEdit(new ReplaceEdit(start, length, newName));
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
