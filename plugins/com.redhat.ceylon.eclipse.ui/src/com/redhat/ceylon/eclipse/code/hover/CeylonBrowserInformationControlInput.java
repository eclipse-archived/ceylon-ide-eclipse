package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.core.runtime.Assert;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;

/**
 * Browser input for Javadoc hover.
 *
 * @since 3.4
 */
class CeylonBrowserInformationControlInput extends BrowserInformationControlInput {

	private final Object model;
	private final String fHtml;
	private final int fLeadingImageWidth;
	/**
	 * Creates a new browser information control input.
	 *
	 * @param previous previous input, or <code>null</code> if none available
	 * @param model the model object, or <code>null</code> if none available
	 * @param html HTML contents, must not be null
	 * @param leadingImageWidth the indent required for the element image
	 */
	public CeylonBrowserInformationControlInput(BrowserInformationControlInput previous, 
			Object model, String html, int leadingImageWidth) {
		super(previous);
		Assert.isNotNull(html);
		this.model= model;
		fHtml= html;
		fLeadingImageWidth= leadingImageWidth;
	}

	@Override
	public int getLeadingImageWidth() {
		return fLeadingImageWidth;
	}

	@Override
	public String getHtml() {
		return fHtml;
	}

	@Override
	public Object getInputElement() {
		return model == null ? (Object) fHtml : model;
	}

	@Override
	public String getInputName() {
		if (model instanceof Declaration) {
			return ((Declaration) model).getName();
		}
		else if (model instanceof Package) {
			return ((Package) model).getQualifiedNameString();
		}
		else {
			return null;
		}
	}

}