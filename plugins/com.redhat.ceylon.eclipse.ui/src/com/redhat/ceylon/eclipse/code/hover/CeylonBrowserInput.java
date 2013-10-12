package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.core.runtime.Assert;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.browser.BrowserInput;

/**
 * Browser input for Javadoc hover.
 *
 * @since 3.4
 */
class CeylonBrowserInput extends BrowserInput {

	private final Object model;
	private final String html;
	
	/**
	 * Creates a new browser information control input.
	 *
	 * @param previous previous input, or <code>null</code> if none available
	 * @param model the model object, or <code>null</code> if none available
	 * @param html HTML contents, must not be null
	 * @param leadingImageWidth the indent required for the element image
	 */
	public CeylonBrowserInput(BrowserInput previous, 
			Object model, String html) {
		super(previous);
		Assert.isNotNull(html);
		this.model = model;
		this.html = html;
	}

	@Override
	public String getHtml() {
		return html;
	}

	@Override
	public Object getInputElement() {
		return model == null ? (Object) html : model;
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