package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.preferences.ModuleImportSelectionDialog.selectModules;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getModuleSearchResults;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModules;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.swt.layout.GridData.FILL_HORIZONTAL;
import static org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_FILL;
import static org.eclipse.swt.layout.GridData.VERTICAL_ALIGN_BEGINNING;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.wizards.IWizardDescriptor;

import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.imports.ModuleImportUtil;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.wizard.NewPackageWizard;

public class CeylonModulePropertiesPage extends PropertyPage 
        implements IWorkbenchPropertyPage {

//    private IResourceChangeListener encodingListener;
    private Table moduleImportsTable;
    private IProject project;
    private IPackageFragment packageFragment;
    
//    @Override
//    public void dispose() {
//        if (encodingListener!=null) {
//            getWorkspace().removeResourceChangeListener(encodingListener);
//            encodingListener = null;
//        }
//        super.dispose();
//    }
  
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
        
    @Override
    protected Control createContents(Composite parent) {
        initProjectAndModule();
        if (packageFragment==null) return parent;
        createModuleInfoBlock(parent);
        createPackagesBlock(parent);
        createModulesBlock(parent);
        createModuleDescriptorLink(parent);
        return parent;
    }

    public void createModuleDescriptorLink(Composite parent) {
        final IFile moduleDescriptor = ((IFolder) packageFragment.getResource()).getFile("module.ceylon");
        Link openDescriptorLink = new Link(parent, 0);
        openDescriptorLink.setLayoutData(GridDataFactory.swtDefaults()
                .align(SWT.FILL, SWT.CENTER).indent(0, 6).create());
        openDescriptorLink.setText("<a>Edit module descriptor...</a>");
        openDescriptorLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getShell().close();
                Util.gotoLocation(moduleDescriptor, 0);
            }
        });
    }

    private void initProjectAndModule() {
        packageFragment = (IPackageFragment) getElement()
                .getAdapter(IPackageFragment.class);
        if (packageFragment!=null) {
            project = packageFragment.getJavaProject().getProject();
        }
    }

    public Module getModule() {
        if (project==null) return null;
        Modules projectModules = getProjectModules(project);
        if (projectModules==null) return null;
        for (Module m: projectModules.getListOfModules()) {
            if (m.getNameAsString().equals(packageFragment.getElementName())) {
                return m; 
            }
        }
        return null;
    }
    
    private void createModulesBlock(Composite parent) {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Imported modules:");
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridData cgd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        cgd.grabExcessHorizontalSpace = true;
        cgd.grabExcessVerticalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout layout = new GridLayout(3, true);
        layout.marginWidth=0;
        composite.setLayout(layout);
        
        moduleImportsTable = new Table(composite, 
                SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.FILL_VERTICAL);
        gd.horizontalSpan=2;
        gd.verticalSpan=4;
        gd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        gd.heightHint = 100;
        gd.widthHint = 250;
        moduleImportsTable.setLayoutData(gd);
        for (ModuleImport mi: getModule().getImports()) {
            TableItem item = new TableItem(moduleImportsTable, SWT.NONE);
            item.setImage(CeylonLabelProvider.ARCHIVE);
            item.setText(mi.getModule().getNameAsString() + "/" + 
                    mi.getModule().getVersion());
        }
        
        Button addButton = new Button(composite, SWT.PUSH);
        addButton.setText("Add imports...");
        GridData bgd = new GridData(VERTICAL_ALIGN_BEGINNING|HORIZONTAL_ALIGN_FILL);
        bgd.grabExcessHorizontalSpace=false;
        bgd.widthHint = 50;
        addButton.setLayoutData(bgd);
        addButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                selectAndAddModules();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        Button removeButton = new Button(composite, SWT.PUSH);
        removeButton.setText("Remove selected");
        removeButton.setLayoutData(bgd);
        removeButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedModules();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });

    }

    private void createPackagesBlock(Composite parent) {
        Label  label = new Label(parent, SWT.NONE);
        label.setText("Packages:");
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridData cgd = new GridData(FILL_HORIZONTAL);
        cgd.grabExcessHorizontalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout layout = new GridLayout(3, true);
        layout.marginWidth=0;
        composite.setLayout(layout);
        
        final Table packagesTable = new Table(composite, 
                SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
        GridData gd = new GridData(FILL_HORIZONTAL);
        gd.horizontalSpan=2;
        gd.grabExcessHorizontalSpace = true;
//        gd.grabExcessVerticalSpace = true;
        gd.heightHint = 100;
        gd.widthHint = 250;
        packagesTable.setLayoutData(gd);
        for (Package p: getModule().getPackages()) {
            TableItem item = new TableItem(packagesTable, SWT.NONE);
            item.setImage(CeylonLabelProvider.PACKAGE);
            item.setText(p.getNameAsString());
        }
        
        Button createPackageButton = new Button(composite, SWT.PUSH);
        createPackageButton.setText("Create package...");
        GridData bgd = new GridData(VERTICAL_ALIGN_BEGINNING|HORIZONTAL_ALIGN_FILL);
        bgd.grabExcessHorizontalSpace=false;
        bgd.widthHint = 50;
        createPackageButton.setLayoutData(bgd);
        createPackageButton.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                NewPackageWizard wiz = openPackageWizard();
                if (wiz.isCreated()) {
                    IPackageFragment pfr = wiz.getPackageFragment();
                    TableItem item = new TableItem(packagesTable, SWT.NONE);
                    item.setImage(CeylonLabelProvider.PACKAGE);
                    item.setText(pfr.getElementName());
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
    }

    private void createModuleInfoBlock(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData cgd = new GridData(FILL_HORIZONTAL);
//        cgd.grabExcessHorizontalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout layout = new GridLayout(2, true);
        layout.marginWidth=0;
        composite.setLayout(layout);
        Label label = new Label(composite, SWT.NONE);
        label.setText("Module name: ");
        label = new Label(composite, SWT.NONE);
        label.setText(packageFragment.getElementName());
        
        /*Label img = new Label(composite, SWT.BORDER);
        Image image = CeylonPlugin.getInstance()
                .getImageRegistry().get(CEYLON_NEW_MODULE);
        img.setImage(image);
        img.setSize(image.getBounds().width, image.getBounds().height);
        GridData igd = new GridData(HORIZONTAL_ALIGN_END|VERTICAL_ALIGN_END);
        igd.verticalSpan=4;
//        igd.horizontalSpan=2;
        igd.grabExcessHorizontalSpace=true;
        img.setLayoutData(igd);*/
        
        label = new Label(composite, SWT.NONE);
        label.setText("Project name: ");
        label = new Label(composite, SWT.NONE);
        label.setText(project.getName());
        
        /*label = new Label(composite, SWT.NONE);
        label.setText("Module workspace path: ");
        label = new Label(composite, SWT.NONE);
        label.setText(pf.getResource().getFullPath().toPortableString());
        
        label = new Label(composite, SWT.NONE);
        label.setText("Default source file encoding: ");
        final Link link = new Link(composite, SWT.NONE);
        try {
            final IFolder f = (IFolder) pf.getResource();
            link.setText(f.getDefaultCharset() + " <a>(Change...)</a>");
            link.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
                    container.openPage("org.eclipse.ui.propertypages.info.file", null);
                }
            });
            getWorkspace().addResourceChangeListener(encodingListener=new IResourceChangeListener() {
                @Override
                public void resourceChanged(IResourceChangeEvent event) {
                    if (event.getType()==IResourceChangeEvent.POST_CHANGE) {
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                try {
                                    if (!link.isDisposed()) {
                                        link.setText(f.getDefaultCharset() + 
                                                " <a>(Change...)</a>");
                                    }
                                }
                                catch (CoreException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }
        catch (CoreException e) {
            e.printStackTrace();
        }*/
        
        new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
    }
    
    @Override
    public void createControl(Composite parent) {
        noDefaultAndApplyButton();
        super.createControl(parent);
    }

    private void selectAndAddModules() {
        Map<String, String> added = selectModules(new ModuleImportSelectionDialog(getShell(), 
                new ModuleImportContentProvider(getModule()) {
            @Override
            public ModuleSearchResult getModules(String prefix) {
                return getModuleSearchResults(prefix, 
                        getProjectTypeChecker(project), project);
            }
        }));
        ModuleImportUtil.addModuleImports(project, getModule(), added);
        for (Map.Entry<String, String> entry: added.entrySet()) {
            TableItem item = new TableItem(moduleImportsTable, SWT.NONE);
            item.setImage(CeylonLabelProvider.ARCHIVE);
            item.setText(entry.getKey() + "/" + entry.getValue());
        }
    }

    private void removeSelectedModules() {
        int[] selection = moduleImportsTable.getSelectionIndices();
        List<String> names = new ArrayList<String>();
        List<Integer> removed = new ArrayList<Integer>();
        for (int index: selection) {
            TableItem item = moduleImportsTable.getItem(index);
            String name = item.getText().substring(0, 
                    item.getText().indexOf('/'));
            if (!name.equals(Module.LANGUAGE_MODULE_NAME)) {
                names.add(name);
                removed.add(index);
            }
        }
        ModuleImportUtil.removeModuleImports(project, getModule(), names);
        int[] indices = new int[removed.size()];
        for (int i=0; i<removed.size(); i++) {
            indices[i] = removed.get(i);
        }
        moduleImportsTable.remove(indices);
    }
    
}
