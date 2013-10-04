package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getModuleSearchResults;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModules;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.wizards.IWizardDescriptor;

import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.imports.AddModuleImportUtil;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.wizard.NewPackageWizard;

public class CeylonModulePropertiesPage extends PropertyPage implements
        IWorkbenchPropertyPage {

    private IPackageFragment getSelectedPackageFragment() {
        return (IPackageFragment) getElement().getAdapter(IPackageFragment.class);
    }
    
    private NewPackageWizard openPackageWizard() {
        IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry()
                .findWizard(PLUGIN_ID + ".newPackageWizard");
        if (descriptor!=null) {
            try {
                NewPackageWizard wizard = (NewPackageWizard) descriptor.createWizard();
                wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(getElement()));
                WizardDialog wd = new WizardDialog(Display.getCurrent().getActiveShell(), 
                        wizard);
                wd.setTitle(wizard.getWindowTitle());
                wd.open();
                return wizard;
            }
            catch (CoreException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    Module module = null;
    
    @Override
    protected Control createContents(Composite parent) {
        final IPackageFragment pf = getSelectedPackageFragment();
        final IProject project = pf.getJavaProject().getProject();
        for (Module m: getProjectModules(project).getListOfModules()) {
            if (m.getNameAsString().equals(pf.getElementName())) {
                module = m; break;
            }
        }
        if (module==null) return parent;
        
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
        label.setText("Packages:");
        composite = new Composite(parent, SWT.NONE);
        GridData cgd = new GridData(GridData.FILL_HORIZONTAL);
        cgd.grabExcessHorizontalSpace = true;
        composite.setLayoutData(cgd);
        layout = new GridLayout(3, true);
        layout.marginWidth=0;
        composite.setLayout(layout);
        
        final Table packages = new Table(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan=2;
        gd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        gd.heightHint = 100;
        gd.widthHint = 250;
        packages.setLayoutData(gd);
        for (Package p: module.getPackages()) {
            TableItem item = new TableItem(packages, SWT.NONE);
            item.setImage(CeylonLabelProvider.PACKAGE);
            item.setText(p.getNameAsString());
        }
        Button button = new Button(composite, SWT.PUSH);
        button.setText("Create package...");
        GridData bgd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING|GridData.HORIZONTAL_ALIGN_FILL);
        bgd.grabExcessHorizontalSpace=false;
        bgd.widthHint = 50;
        button.setLayoutData(bgd);
        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                NewPackageWizard wiz = openPackageWizard();
                if (wiz.isCreated()) {
                    IPackageFragment pfr = wiz.getPackageFragment();
                    TableItem item = new TableItem(packages, SWT.NONE);
                    item.setImage(CeylonLabelProvider.PACKAGE);
                    item.setText(pfr.getElementName());
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        
        label = new Label(parent, SWT.NONE);
        label.setText("Imported modules:");
        composite = new Composite(parent, SWT.NONE);
        cgd = new GridData(GridData.FILL_HORIZONTAL);
        cgd.grabExcessHorizontalSpace = true;
        composite.setLayoutData(cgd);
        layout = new GridLayout(3, true);
        layout.marginWidth=0;
        composite.setLayout(layout);
        
        final Table imports = new Table(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan=2;
        gd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        gd.heightHint = 100;
        gd.widthHint = 250;
        imports.setLayoutData(gd);
        for (ModuleImport mi: module.getImports()) {
            TableItem item = new TableItem(imports, SWT.NONE);
            item.setImage(CeylonLabelProvider.ARCHIVE);
            item.setText(mi.getModule().getNameAsString() + "/" + 
                    mi.getModule().getVersion());
        }
        button = new Button(composite, SWT.PUSH);
        button.setText("Add import...");
        bgd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING|GridData.HORIZONTAL_ALIGN_FILL);
        bgd.grabExcessHorizontalSpace=false;
        bgd.widthHint = 50;
        button.setLayoutData(bgd);
        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), 
                        new LabelProvider() {
                    @Override
                    public Image getImage(Object element) {
                        return CeylonLabelProvider.ARCHIVE;
                    }
                    @Override
                    public String getText(Object element) {
                        ModuleDetails md = (ModuleDetails) element;
                        return md.getName() + "/"
                                + md.getLastVersion().getVersion();
                    }
                });
                dialog.setTitle("Module Selection");
                dialog.setMessage("Select a module to import:");
                ModuleSearchResult searchResults = getModuleSearchResults("", 
                        getProjectTypeChecker(project), project);
                List<ModuleDetails> list = new ArrayList<ModuleDetails>(searchResults.getResults());
                for (Iterator<ModuleDetails> it = list.iterator(); it.hasNext();) {
                    String name = it.next().getName();
                    if (module.getNameAsString().equals(name)) {
                        it.remove();
                    }
                    else {
                        for (ModuleImport mi: module.getImports()) {
                            if (mi.getModule().getNameAsString().equals(name)) {
                                it.remove();
                                break;
                            }
                        }
                    }
                }
                dialog.setElements(list.toArray());
                dialog.open();
                Object[] result = dialog.getResult();
                if (result!=null && result.length==1) {
                    ModuleDetails md = (ModuleDetails) result[0];
                    AddModuleImportUtil.addModuleImport(project, module, 
                            md.getName(), md.getLastVersion().getVersion());
                    TableItem item = new TableItem(imports, SWT.NONE);
                    item.setImage(CeylonLabelProvider.ARCHIVE);
                    item.setText(md.getName() + "/" + 
                            md.getLastVersion().getVersion());
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
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
