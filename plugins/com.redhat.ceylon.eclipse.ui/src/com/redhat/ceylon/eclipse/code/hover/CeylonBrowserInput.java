package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
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
	private final IProject project;
	private final String moduleName;
	
	/**
	 * Creates a new browser information control input.
	 *
	 * @param previous previous input, or <code>null</code> if none available
	 * @param model the model object, or <code>null</code> if none available
	 * @param html HTML contents, must not be null
	 * @param leadingImageWidth the indent required for the element image
	 */
	public CeylonBrowserInput(BrowserInput previous, 
	        Referenceable model, String html, IProject project) {
		super(previous);
		Assert.isNotNull(html);
		this.model = model;
		this.html = html;
		this.declaration = model instanceof Declaration;
		this.project = project;
		if (model instanceof Module) {
		    moduleName = model.getNameAsString();
		}
		else if (model instanceof Package) {
		    moduleName = ((Package) model).getModule().getNameAsString();
		}
		else if (model instanceof Declaration) {
		    moduleName = model.getUnit().getPackage().getModule().getNameAsString();
		}
		else {
		    moduleName = null;
		}
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
	
	public String getModuleName() {
        return moduleName;
    }
	
	public IProject getProject() {
        return project;
    }

	@Override
	public String getInputName() {
		return model==null ? null : model.getNameAsString();
	}

}