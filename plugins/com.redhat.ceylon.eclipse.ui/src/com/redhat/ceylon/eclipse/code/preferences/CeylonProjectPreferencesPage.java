package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJava;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJs;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonSystemRepo;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.showWarnings;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class CeylonProjectPreferencesPage extends PropertyPage {

    private boolean explodeModules;
    private boolean showCompilerWarnings = true;
    private boolean builderEnabled = false;
    private boolean backendJs = false;
    private boolean backendJava = false;
    private Boolean offlineOption = null;

    private Button showWarnings;
    private Button compileToJs;
    private Button compileToJava;
    private Button enableExplodeModules;
    private Button offlineButton;
    
    @Override
    public boolean performOk() {
        store();
        return true;
    }
    
    @Override
    protected void performDefaults() {
        explodeModules=false;
        enableExplodeModules.setSelection(false);
        showCompilerWarnings=true;
        showWarnings.setSelection(true);
        backendJs = false;
        backendJava = true;
        compileToJs.setSelection(false);
        compileToJava.setSelection(true);
        offlineOption = null;
        updateOfflineButton();
        super.performDefaults();
    }
    
    private void store() {
        IProject project = getSelectedProject();
        if (CeylonNature.isEnabled(project)) {
            String systemRepo = getCeylonSystemRepo(project);
            new CeylonNature(systemRepo, explodeModules, !showCompilerWarnings, 
                    backendJava, backendJs).addToProject(project);

            CeylonProjectConfig config = CeylonProjectConfig.get(project);
            if (offlineOption!=null) {
                config.setProjectOffline(offlineOption);
            }
            config.save();
        }
    }

    private IProject getSelectedProject() {
        return (IProject) getElement().getAdapter(IProject.class);
    }
    
    //TODO: fix copy/paste!
    void addControls(Composite parent) {
        Label desc = new Label(parent, SWT.LEFT | SWT.WRAP);
        desc.setText("The Ceylon builder compiles Ceylon source contained in the project");

        final Button enableBuilder = new Button(parent, SWT.PUSH);
        enableBuilder.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        enableBuilder.setText("Enable Ceylon Builder");
        enableBuilder.setEnabled(!builderEnabled && getSelectedProject().isOpen());
        enableBuilder.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CeylonResources.ELE32));
        //enableBuilder.setSize(40, 40);

        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sep.setLayoutData(sgd);

//        Label misc = new Label(parent, SWT.LEFT | SWT.WRAP);
//        misc.setText("Ceylon compiler settings");

        Group group = new Group(parent, SWT.NONE);
        Composite composite = group;
        group.setText("Platform");
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        
        layout.numColumns = 1;
        layout.marginBottom = 1;
        composite.setLayout(layout);
        
        compileToJava = new Button(composite, SWT.CHECK);
        compileToJava.setText("Compile project for JVM");
        compileToJava.setSelection(backendJava);
        compileToJava.setEnabled(builderEnabled);
        
        compileToJs = new Button(composite, SWT.CHECK);
        compileToJs.setText("Compile project to JavaScript");
        compileToJs.setSelection(backendJs);
        compileToJs.setEnabled(builderEnabled);
        
        composite = new Composite(parent, SWT.NONE);
        GridData gdb = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gdb.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gdb);
        GridLayout layoutb = new GridLayout();
        layoutb.numColumns = 1;
        layoutb.marginBottom = 1;
        composite.setLayout(layoutb);
        
        addCharacterEncodingLabel(composite);
        
        showWarnings = new Button(composite, SWT.CHECK);
        showWarnings.setText("Show compiler warnings (for unused declarations and use of deprecated declarations)");
        showWarnings.setSelection(showCompilerWarnings);
        showWarnings.setEnabled(builderEnabled);

        enableExplodeModules = new Button(composite, SWT.CHECK);
        enableExplodeModules.setText("Enable Java classes calling Ceylon");
        enableExplodeModules.setSelection(explodeModules);
        enableExplodeModules.setEnabled(builderEnabled&&backendJava);
        
        offlineButton = new Button(composite, SWT.CHECK);
        offlineButton.setText("Work offline (disable connection to remote module repositories)");
        offlineButton.setEnabled(builderEnabled);
        offlineButton.setSelection(offlineOption!=null&&offlineOption);
        offlineButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (offlineOption == null) {
                    offlineOption = true;
                } else {
                    offlineOption = !offlineOption;
                }
                updateOfflineButton();
            }
        });
        updateOfflineButton();
        
        enableBuilder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new CeylonNature().addToProject(getSelectedProject());
                enableBuilder.setEnabled(false);
                enableExplodeModules.setEnabled(true);
                showWarnings.setEnabled(true);
                compileToJs.setEnabled(true);
                compileToJava.setEnabled(true);
                offlineButton.setEnabled(true);
                builderEnabled=true;
            }
        });
    
        enableExplodeModules.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                explodeModules = !explodeModules;
            }
        });
        
        showWarnings.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showCompilerWarnings = !showCompilerWarnings;
            }
        });

        compileToJava.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                backendJava = !backendJava;
                enableExplodeModules.setEnabled(backendJava);
            }
        });
        
        compileToJs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                backendJs = !backendJs;
            }
        });
        
        Link buildPathsPageLink = new Link(parent, 0);
//        buildPathsPageLink.setLayoutData(GridDataFactory.swtDefaults()
//                .align(SWT.FILL, SWT.CENTER).indent(0, 6).create());
        buildPathsPageLink.setText("<a>Change Project Build Paths...</a>");
        buildPathsPageLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
                container.openPage(CeylonBuildPathsPropertiesPage.ID, null);
            }
        });
        Link openRepoPageLink = new Link(parent, 0);
//        openRepoPageLink.setLayoutData(GridDataFactory.swtDefaults()
//                .align(SWT.FILL, SWT.CENTER).indent(0, 6).create());
        openRepoPageLink.setText("<a>Configure Project Module Repositories...</a>");
        openRepoPageLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
                container.openPage(CeylonRepoPropertiesPage.ID, null);
            }
        });
    }

    private void addCharacterEncodingLabel(Composite composite) {
        final Link link = new Link(composite, SWT.NONE);
        try {
            link.setText("Default source file encoding:   " +
                    getSelectedProject().getDefaultCharset() + 
                    " <a>(Change...)</a>");
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
                                        link.setText("Default source file encoding:   " +
                                                getSelectedProject().getDefaultCharset() + 
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
        }
    }
    
    @Override
    protected Control createContents(Composite composite) {
        IProject project = getSelectedProject();
        if (project.isOpen()) {
            builderEnabled = CeylonNature.isEnabled(project);
            if (builderEnabled) {
                explodeModules = isExplodeModulesEnabled(project);
                showCompilerWarnings = showWarnings(project);
                backendJs = compileToJs(project);
                backendJava = compileToJava(project);
                offlineOption = CeylonProjectConfig.get(project).isProjectOffline();
            }
        }

        addControls(composite);
        return composite;
    }
    
    private IResourceChangeListener encodingListener;
    
    @Override
    public void dispose() {
        if (encodingListener!=null) {
            getWorkspace().removeResourceChangeListener(encodingListener);
            encodingListener = null;
        }
        super.dispose();
    }
    
    private void updateOfflineButton() {
//        if (offlineOption == null) {
//            offlineButton.setGrayed(true);
//            offlineButton.setSelection(true);
//            offlineButton.setText("Offline (will use default configuration)");
//        } else if (offlineOption == true) {
//            offlineButton.setGrayed(false);
//            offlineButton.setSelection(true);
//            offlineButton.setText("Offline (will prevent connecting to remote repositories)");
//        } else {
//            offlineButton.setGrayed(false);
//            offlineButton.setSelection(false);
//            offlineButton.setText("Offline");
//        }
//        offlineButton.pack();
    }

}