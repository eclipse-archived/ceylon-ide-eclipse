package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getModuleSearchResults;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModules;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_NEW_MODULE;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
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
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.wizards.IWizardDescriptor;

import com.redhat.ceylon.cmr.api.ModuleSearchResult;
import com.redhat.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.ModuleImport;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.code.imports.AddModuleImportUtil;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleNode;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchManager;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleSearchViewContentProvider;
import com.redhat.ceylon.eclipse.code.modulesearch.ModuleVersionNode;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.wizard.NewPackageWizard;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonModulePropertiesPage extends PropertyPage implements
        IWorkbenchPropertyPage {

    private IResourceChangeListener encodingListener;
    
    @Override
    public void dispose() {
        super.dispose();
        if (encodingListener!=null) {
            getWorkspace().removeResourceChangeListener(encodingListener);
            encodingListener = null;
        }
    }
    
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
        GridData cgd = new GridData(GridData.FILL_HORIZONTAL);
//        cgd.grabExcessHorizontalSpace = true;
        composite.setLayoutData(cgd);
        GridLayout layout = new GridLayout(3, true);
        layout.marginWidth=0;
        composite.setLayout(layout);
        Label label = new Label(composite, SWT.NONE);
        label.setText("Module name: ");
        label = new Label(composite, SWT.NONE);
        label.setText(pf.getElementName());
        
        Label img = new Label(composite, SWT.BORDER);
        Image image = CeylonPlugin.getInstance()
                .getImageRegistry().get(CEYLON_NEW_MODULE);
        img.setImage(image);
        img.setSize(image.getBounds().width, image.getBounds().height);
        GridData igd = new GridData(GridData.HORIZONTAL_ALIGN_END|GridData.VERTICAL_ALIGN_END);
        igd.verticalSpan=4;
//        igd.horizontalSpan=2;
        igd.grabExcessHorizontalSpace=true;
        img.setLayoutData(igd);
        
        label = new Label(composite, SWT.NONE);
        label.setText("Project name: ");
        label = new Label(composite, SWT.NONE);
        label.setText(project.getName());
        
        label = new Label(composite, SWT.NONE);
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
                                    link.setText(f.getDefaultCharset() + " <a>(Change...)</a>");
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
        }
        
//        Label sep = new Label(parent, SWT.SEPARATOR|SWT.HORIZONTAL);
//        GridData sgd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//        sep.setLayoutData(sgd);
        
        Link openDescriptorLink = new Link(composite, 0);
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
        
        label = new Label(parent, SWT.NONE);
        label.setText("Packages:");
        composite = new Composite(parent, SWT.NONE);
        cgd = new GridData(GridData.FILL_HORIZONTAL);
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
        button.setText("Add imports...");
        bgd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING|GridData.HORIZONTAL_ALIGN_FILL);
        bgd.grabExcessHorizontalSpace=false;
        bgd.widthHint = 50;
        button.setLayoutData(bgd);
        button.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), 
                        new LabelProvider() {
                    @Override
                    public Image getImage(Object element) {
                        if (element instanceof ModuleNode) {
                            return CeylonLabelProvider.ARCHIVE;
                        }
                        else {
                            return CeylonLabelProvider.VERSION;
                        }
                    }
                    @Override
                    public String getText(Object element) {
                        if (element instanceof ModuleNode) {
                            ModuleNode md = (ModuleNode) element;
                            return md.getName() + " : " + md.getLastVersion().getVersion();
                        }
                        else {
                            return ((ModuleVersionNode) element).getVersion();
                        }
                    }
                }, new ModuleSearchViewContentProvider());
                dialog.setTitle("Module Selection");
                dialog.setMessage("Select modules to import:");
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
                dialog.setInput(ModuleSearchManager.convertResult(list));
                dialog.open();
                Object[] results = dialog.getResult();
                Set<String> added = new HashSet<String>();
                if (results!=null) {
                    for (Object result: results) {
                        String name; String version;
                        if (result instanceof ModuleNode) {
                            name = ((ModuleNode) result).getName();
                            version = ((ModuleNode) result).getLastVersion().getVersion();
                        }
                        else if (result instanceof ModuleVersionNode) {
                            name = ((ModuleVersionNode) result).getModule().getName();
                            version = ((ModuleVersionNode) result).getVersion();
                        }
                        else {
                            continue;
                        }
                        if (added.add(name)) {
                            AddModuleImportUtil.addModuleImport(project, module, name, version);
                            TableItem item = new TableItem(imports, SWT.NONE);
                            item.setImage(CeylonLabelProvider.ARCHIVE);
                            item.setText(name + "/" + version);
                        }
                    }
                }
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {}
        });
        
        return parent;
    }
    
    @Override
    public void createControl(Composite parent) {
        noDefaultAndApplyButton();
        super.createControl(parent);
    }

}
