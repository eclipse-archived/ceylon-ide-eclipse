package com.redhat.ceylon.eclipse.code.propose;

import static com.redhat.ceylon.eclipse.code.hover.DocHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.quickfix.CeylonQuickFixAssistant.importEdit;

import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.graphics.Point;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.parse.CeylonParseController;

final class AutocompletionProposal extends Proposal {
	private final CeylonParseController cpc;
	private final Declaration d;
	private final boolean addimport;

	AutocompletionProposal(int offset, String prefix, 
			String desc, String text, boolean selectParams,
			CeylonParseController cpc, Declaration d, 
			boolean addimport) {
		super(offset, prefix, CeylonLabelProvider.getImage(d), 
				"", desc, text, selectParams);
		this.cpc = cpc;
		this.d = d;
		this.addimport = addimport;
	}

	@Override
	public void apply(IDocument document) {
		super.apply(document);
		if (addimport) {
			try {
				importEdit(cpc.getRootNode(), 
						d.getUnit().getPackage().getNameAsString(), 
						d.getName())
						.apply(document);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getAdditionalProposalInfo() {
		return getDocumentationFor(cpc, d);	
	}
	
	@Override
	public Point getSelection(IDocument document) {
		int importLength = importEdit(cpc.getRootNode(), 
				d.getUnit().getPackage().getNameAsString(), 
				d.getName()).getText().length();
		Point selection = super.getSelection(document);
		selection.x+=importLength;
		return selection;
	}
}