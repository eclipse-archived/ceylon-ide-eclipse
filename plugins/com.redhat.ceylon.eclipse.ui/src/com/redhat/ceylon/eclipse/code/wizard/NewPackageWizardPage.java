package org.eclipse.ceylon.ide.eclipse.code.wizard;

import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.CEYLON_NEW_PACKAGE;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

final class NewPackageWizardPage extends NewUnitWizardPage {
    
    NewPackageWizardPage(boolean shared) {
        super("New Ceylon Package", 
                "Create a Ceylon package with a package descriptor.", 
                CEYLON_NEW_PACKAGE);
        this.shared = shared;
    }
    
    @Override
    String getPackageLabel() {
        return "Package name: ";
    }

    @Override
    void createControls(Composite composite) {
        Text name = createPackageField(composite);
        createSharedField(composite);
        createSeparator(composite);
        createFolderField(composite);
        name.forceFocus();
    }

    @Override
    boolean isComplete() {
        return super.isComplete() && 
                !getPackageFragment().isDefaultPackage();
    }

    @Override
    boolean packageNameIsLegal(String packageName) {
        return !packageName.isEmpty() && 
                super.packageNameIsLegal(packageName);
    }

    @Override
    boolean unitIsNameLegal(String unitName) {
        return true;
    }

    @Override
    String[] getFileNames() {
        return new String[] { "package" };
    }
}