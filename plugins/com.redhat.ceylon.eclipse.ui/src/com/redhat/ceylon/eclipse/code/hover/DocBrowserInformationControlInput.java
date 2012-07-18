package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;

import com.redhat.ceylon.compiler.typechecker.tree.Tree;

/**
 * Browser input for Javadoc hover.
 *
 * @since 3.4
 */
class DocBrowserInformationControlInput extends BrowserInformationControlInput {

	private final Tree.Declaration node;
	private final String fHtml;
	private final int fLeadingImageWidth;
	/**
	 * Creates a new browser information control input.
	 *
	 * @param previous previous input, or <code>null</code> if none available
	 * @param element the element, or <code>null</code> if none available
	 * @param html HTML contents, must not be null
	 * @param leadingImageWidth the indent required for the element image
	 */
	public DocBrowserInformationControlInput(DocBrowserInformationControlInput previous, Tree.Declaration node, String html, int leadingImageWidth) {
		super(previous);
		Assert.isNotNull(html);
		this.node= node;
		fHtml= html;
		fLeadingImageWidth= leadingImageWidth;
	}

	/*
	 * @see org.eclipse.jface.internal.text.html.BrowserInformationControlInput#getLeadingImageWidth()
	 * @since 3.4
	 */
	@Override
	public int getLeadingImageWidth() {
		return fLeadingImageWidth;
	}

	/*
	 * @see org.eclipse.jface.internal.text.html.BrowserInput#getHtml()
	 */
	@Override
	public String getHtml() {
		return fHtml;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.BrowserInput#getInputElement()
	 */
	@Override
	public Object getInputElement() {
		return node == null ? (Object) fHtml : node;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.BrowserInput#getInputName()
	 */
	@Override
	public String getInputName() {
		return node == null ? "" : node.getIdentifier().getText();
	}

}