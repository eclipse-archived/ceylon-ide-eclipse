package com.redhat.ceylon.eclipse.code.refactor;

import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.getIndent;
import static org.eclipse.ltk.core.refactoring.RefactoringStatus.createWarningStatus;

import org.antlr.runtime.CommonToken;
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
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

public class ConvertToClassRefactoring extends AbstractRefactoring {

	private String newName;
	private final Declaration declaration;

	public ConvertToClassRefactoring(ITextEditor editor) {
	    super(editor);
	    if (node instanceof Tree.ObjectDefinition) {
		    declaration = ((Tree.ObjectDefinition) node).getDeclarationModel();
    		String name = declaration.getName();
    		newName = Character.toUpperCase(name.charAt(0))+name.substring(1);
	    }
		else {
		    declaration = null;
		}
	}
	
	@Override
	boolean isEnabled() {
	    return node instanceof Tree.ObjectDefinition;
	}
	
	public String getName() {
		return "Convert To Class";
	}
	
	public String getNewName() {
        return newName;
    }

	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		// Check parameters retrieved from editor context
		return new RefactoringStatus();
	}

	public RefactoringStatus checkFinalConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
	    Declaration existing = declaration.getContainer()
                        .getMemberOrParameter(declaration.getUnit(), newName, null, false);
        if (null!=existing && !existing.equals(declaration)) {
	        return createWarningStatus("An existing declaration named '" +
	            newName + "' already exists in the same scope");
	    }
		return new RefactoringStatus();
	}
    
	public Change createChange(IProgressMonitor pm) 
	        throws CoreException, OperationCanceledException {
	    TextChange tfc = newLocalChange();
	    convertInFile(tfc);
	    return tfc;
	}

	private void convertInFile(TextChange tfc) throws CoreException {
	    tfc.setEdit(new MultiTextEdit());
	    IDocument doc = tfc.getCurrentDocument(null);
	    Tree.ObjectDefinition od = (Tree.ObjectDefinition) node;
	    int dstart = ((CommonToken) od.getMainToken()).getStartIndex();
	    tfc.addEdit(new ReplaceEdit(dstart, 6, "class"));
        int start = od.getIdentifier().getStartIndex();
        int length = od.getIdentifier().getStopIndex()-start+1;
        tfc.addEdit(new ReplaceEdit(start, length, newName + "()"));
        int offset = od.getStopIndex()+1;
        //TODO: handle actual object declarations
        String mods = declaration.isShared() ? "shared " : "";
        tfc.addEdit(new InsertEdit(offset, 
        		System.lineSeparator() + getIndent(od, doc) + 
                mods + newName + " " + declaration.getName() + 
                " = " + newName + "();"));
	}

	public void setNewName(String text) {
		newName = text;
	}
	
	public Declaration getDeclaration() {
		return declaration;
	}
}
