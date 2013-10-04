package com.redhat.ceylon.eclipse.code.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public class CeylonModulePropertiesPage extends PropertyPage implements
        IWorkbenchPropertyPage {

    private IPackageFragment getSelectedPackageFragment() {
        return (IPackageFragment) getElement().getAdapter(IPackageFragment.class);
    }
    
    @Override
    protected Control createContents(Composite parent) {
        IPackageFragment pf = getSelectedPackageFragment();
        IProject project = pf.getJavaProject().getProject();
        Module module = null;
        for (Module m: CeylonBuilder.getProjectModules(project).getListOfModules()) {
            if (m.getNameAsString().equals(pf.getElementName())) {
                module = m; break;
            }
        }
        Label label = new Label(parent, SWT.NONE);
        label.setText("Module: " + pf.getElementName());
        label = new Label(parent, SWT.NONE);
        label.setText("Imported modules:");
        Table table = new Table(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.widthHint = 250;
        table.setLayoutData(gd);
        for (ModuleImport mi: module.getImports()) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setImage(CeylonLabelProvider.ARCHIVE);
            item.setText(mi.getModule().getNameAsString() + " \"" + 
                    mi.getModule().getVersion() + "\"");
        }
        return parent;
    }
    
    @Override
    public void createControl(Composite parent) {
        noDefaultAndApplyButton();
        super.createControl(parent);
    }

}
