package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.hover.DocHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.DEFAULT_REFINEMENT;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.FORMAL_REFINEMENT;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importType;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Functional;
import com.redhat.ceylon.compiler.typechecker.model.Parameter;
import com.redhat.ceylon.compiler.typechecker.model.ParameterList;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

final class RefinementCompletionProposal extends CompletionProposal {
	
	private final CeylonParseController cpc;
	private final Declaration declaration;

	RefinementCompletionProposal(int offset, String prefix, 
			String desc, String text,
			CeylonParseController cpc, Declaration d) {
		super(offset, prefix, d.isFormal() ? 
					FORMAL_REFINEMENT : DEFAULT_REFINEMENT, 
				desc, text, false);
		this.cpc = cpc;
		this.declaration = d;
	}

	@Override
	public void apply(IDocument document) {
		int originalLength = document.getLength();
		try {
			imports(document).perform(new NullProgressMonitor());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		offset += document.getLength() - originalLength;
		super.apply(document);
	}

	private DocumentChange imports(IDocument document)
			throws BadLocationException {
		DocumentChange tc = new DocumentChange("imports", document);
		tc.setEdit(new MultiTextEdit());
		if (declaration instanceof TypedDeclaration) {
			ProducedType t = ((TypedDeclaration) declaration).getType();
			importType(tc, t, cpc.getRootNode());
		}
		if (declaration instanceof Functional) {
			for (ParameterList pl: ((Functional) declaration).getParameterLists()) {
				for (Parameter p: pl.getParameters()) {
					importType(tc, p.getType(), cpc.getRootNode());
				}
			}
		}
		return tc;
	}

	public String getAdditionalProposalInfo() {
		return getDocumentationFor(cpc, declaration);	
	}
	
}