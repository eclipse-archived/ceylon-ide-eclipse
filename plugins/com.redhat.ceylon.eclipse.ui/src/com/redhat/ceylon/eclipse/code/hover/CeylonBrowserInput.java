package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.core.runtime.Assert;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.eclipse.code.browser.BrowserInput;

/**
 * Browser input for Javadoc hover.
 *
 * @since 3.4
 */
class CeylonBrowserInput extends BrowserInput {

	private final Referenceable model;
	private final String html;
	private final boolean declaration;
	
	/**
	 * Creates a new browser information control input.
	 *
	 * @param previous previous input, or <code>null</code> if none available
	 * @param model the model object, or <code>null</code> if none available
	 * @param html HTML contents, must not be null
	 * @param leadingImageWidth the indent required for the element image
	 */
	public CeylonBrowserInput(BrowserInput previous, 
	        Referenceable model, String html) {
		super(previous);
		Assert.isNotNull(html);
		this.model = model;
		this.html = html;
		this.declaration = model instanceof Declaration;
	}
	
	public boolean isDeclaration() {
        return declaration;
    }
	
	@Override
	public String getHtml() {
		return html;
	}

	public Referenceable getModel() {
		return model;
	}

	@Override
	public String getInputName() {
		return model==null ? null : model.getNameAsString();
	}

}