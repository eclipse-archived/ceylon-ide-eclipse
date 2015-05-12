package com.redhat.ceylon.eclipse.code.hover;

import org.eclipse.core.runtime.Assert;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.eclipse.code.browser.BrowserInput;
import com.redhat.ceylon.eclipse.code.html.HTML;

/**
 * Browser input for Javadoc hover.
 *
 * @since 3.4
 */
class CeylonBrowserInput extends BrowserInput {

    private final String html;
    private final String moduleName;
    private final String address;
    private final String name;
    
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
        this.html = html;
        this.address = HTML.getAddress(model);
        if (model instanceof Module) {
            moduleName = model.getNameAsString();
            name = moduleName;
        }
        else if (model instanceof Package) {
            moduleName = ((Package) model).getModule().getNameAsString();
            name = model.getNameAsString();
        }
        else if (model instanceof Declaration) {
            moduleName = model.getUnit().getPackage().getModule().getNameAsString();
            name = ((Declaration) model).getName();
        }
        else {
            moduleName = null;
            name = null;
        }
    }
    
    @Override
    public String getHtml() {
        return html;
    }
    
    public String getModuleName() {
        return moduleName;
    }
    
    public String getAddress() {
        return address;
    }
    
    @Override
    public String getInputName() {
        return name;
    }

}