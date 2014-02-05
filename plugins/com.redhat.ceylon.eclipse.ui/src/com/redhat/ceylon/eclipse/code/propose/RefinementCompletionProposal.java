package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.applyImports;
import static com.redhat.ceylon.eclipse.code.correct.ImportProposals.importSignatureTypes;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.propose.RefinementCompletions.DEFAULT_REFINEMENT;
import static com.redhat.ceylon.eclipse.code.propose.RefinementCompletions.FORMAL_REFINEMENT;

import java.util.HashSet;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.DocumentChange;
import org.eclipse.text.edits.MultiTextEdit;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
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
		HashSet<Declaration> decs = new HashSet<Declaration>();
		CompilationUnit cu = cpc.getRootNode();
		importSignatureTypes(declaration, cu, decs);
		applyImports(tc, decs, cu, document);
		return tc;
	}

	public String getAdditionalProposalInfo() {
		return getDocumentationFor(cpc, declaration);	
	}
	
}