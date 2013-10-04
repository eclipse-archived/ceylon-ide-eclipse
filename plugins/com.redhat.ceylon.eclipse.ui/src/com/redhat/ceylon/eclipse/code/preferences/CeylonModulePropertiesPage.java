package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModules;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;

public class CeylonModulePropertiesPage extends PropertyPage implements
        IWorkbenchPropertyPage {

    private IPackageFragment getSelectedPackageFragment() {
        return (IPackageFragment) getElement().getAdapter(IPackageFragment.class);
    }
    
    @Override
    protected Control createContents(Composite parent) {
        final IPackageFragment pf = getSelectedPackageFragment();
        final IProject project = pf.getJavaProject().getProject();
        Module module = null;
        for (Module m: getProjectModules(project).getListOfModules()) {
            if (m.getNameAsString().equals(pf.getElementName())) {
                module = m; break;
            }
        }
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, true);
        layout.marginWidth=0;
        composite.setLayout(layout);
        Label label = new Label(composite, SWT.NONE);
        label.setText("Module: ");
        label = new Label(composite, SWT.NONE);
        label.setText(pf.getElementName());
        label = new Label(composite, SWT.NONE);
        label.setText("Defined in project: ");
        label = new Label(composite, SWT.NONE);
        label.setText(project.getName());
        
//        Label sep = new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL);
//        GridData sgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//        sep.setLayoutData(sgd);
        
        label = new Label(parent, SWT.NONE);
        label.setText("Imported modules:");
        Table table = new Table(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        gd.heightHint = 50;
        gd.widthHint = 250;
        table.setLayoutData(gd);
        for (ModuleImport mi: module.getImports()) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setImage(CeylonLabelProvider.ARCHIVE);
            item.setText(mi.getModule().getNameAsString() + "/" + 
                    mi.getModule().getVersion());
        }
        label = new Label(parent, SWT.NONE);
        
        label.setText("Packages:");
        table = new Table(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        gd.heightHint = 50;
        gd.widthHint = 250;
        table.setLayoutData(gd);
        for (Package p: module.getPackages()) {
            TableItem item = new TableItem(table, SWT.NONE);
            item.setImage(CeylonLabelProvider.PACKAGE);
            item.setText(p.getNameAsString());
        }
        
        Link openDescriptorLink = new Link(parent, 0);
        openDescriptorLink.setLayoutData(GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.CENTER).indent(0, 6).create());
        openDescriptorLink.setText("<a>Edit module descriptor...</a>");
        openDescriptorLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getShell().close();
                Util.gotoLocation(((IFolder)pf.getResource()).getFile("module.ceylon"), 0);
            }
        });
        return parent;
    }
    
    @Override
    public void createControl(Composite parent) {
        noDefaultAndApplyButton();
        super.createControl(parent);
    }

}
