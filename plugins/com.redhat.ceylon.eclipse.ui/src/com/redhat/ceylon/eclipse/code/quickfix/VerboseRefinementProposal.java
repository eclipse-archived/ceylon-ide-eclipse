package com.redhat.ceylon.eclipse.code.quickfix;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isTypeUnknown;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.CHANGE;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.InsertEdit;

import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;

class VerboseRefinementProposal extends ChangeCorrectionProposal {

	private VerboseRefinementProposal(Change change) {
		super("Convert to verbose refinement", change, CHANGE);
	}

	static void addVerboseRefinementProposal(
			Collection<ICompletionProposal> proposals, IFile file,
			Tree.Statement statement) {
		if (statement instanceof Tree.SpecifierStatement) {
			Tree.SpecifierStatement ss = (Tree.SpecifierStatement) statement;
			if (ss.getRefinement()) {
				TextFileChange change = new TextFileChange("Convert to Verbose Refinement", file);
				Tree.Expression e = ss.getSpecifierExpression().getExpression();
				if (e!=null && !isTypeUnknown(e.getTypeModel())) {
					Unit unit = ss.getUnit();
					String type = unit.denotableType(e.getTypeModel())
							.getProducedTypeName(unit);
					change.setEdit(new InsertEdit(statement.getStartIndex(), 
							"shared actual " + type + " "));
					proposals.add(new VerboseRefinementProposal(change));
				}
			}
		}
	}

}