package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.areAstAwareIncrementalBuildsEnabled;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJava;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.compileToJs;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonSystemRepo;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getVerbose;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isExplodeModulesEnabled;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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

public class CeylonProjectPropertiesPage extends PropertyPage {
    
    private boolean explodeModules = true;
    private boolean builderEnabled = false;
    private boolean backendJs = false;
    private boolean backendJava = false;
    private boolean astAwareIncrementalBuids = true;
    private Boolean offlineOption = null;
    private String verbose = null;

    private Button compileToJs;
    private Button astAwareIncrementalBuidsButton;
    private Button compileToJava;
    private Button enableExplodeModules;
    private Button offlineButton;
    private Combo verboseText;
    private Button enableBuilderButton;
    
    private IResourceChangeListener encodingListener;
    
    @Override
    public boolean performOk() {
        store();
        return true;
    }
    
    @Override
    protected void performDefaults() {
        explodeModules=true;
        enableExplodeModules.setSelection(true);
        backendJs = false;
        backendJava = true;
        compileToJs.setSelection(false);
        compileToJava.setSelection(true);
        astAwareIncrementalBuidsButton.setSelection(true);
        offlineOption = null;
        verboseText = null;
        super.performDefaults();
    }
    
    private void store() {
        IProject project = getSelectedProject();
        if (CeylonNature.isEnabled(project)) {
            String systemRepo = getCeylonSystemRepo(project);
            new CeylonNature(systemRepo, explodeModules, 
                    backendJava, backendJs, astAwareIncrementalBuids, verbose)
                    .addToProject(project);

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
    
    void addControls(final Composite parent) {
        Label desc = new Label(parent, SWT.LEFT | SWT.WRAP);
        desc.setText("The Ceylon builder compiles Ceylon source contained in the project.");

        enableBuilderButton = new Button(parent, SWT.PUSH);
        enableBuilderButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
        enableBuilderButton.setText("Enable Ceylon Builder");
        enableBuilderButton.setEnabled(!builderEnabled && getSelectedProject().isOpen());
        enableBuilderButton.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CeylonResources.ELE32));
        //enableBuilder.setSize(40, 40);

        Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sgd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        sep.setLayoutData(sgd);

        Composite composite = new Composite(parent, SWT.NONE);
        GridData gdb = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gdb.grabExcessHorizontalSpace=true;
        composite.setLayoutData(gdb);
        GridLayout layoutb = new GridLayout();
        layoutb.numColumns = 1;
        layoutb.marginBottom = 1;
        composite.setLayout(layoutb);
        
        addCharacterEncodingLabel(composite);
        
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
            }
        });
        
        final Group platformGroup = new Group(parent, SWT.NONE);
        platformGroup.setText("Target virtual machine");
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        platformGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        
        layout.numColumns = 1;
        layout.marginBottom = 1;
        platformGroup.setLayout(layout);
        
        compileToJava = new Button(platformGroup, SWT.CHECK);
        compileToJava.setText("Compile project for JVM");
        compileToJava.setSelection(backendJava);
        compileToJava.setEnabled(builderEnabled);
        
        compileToJs = new Button(platformGroup, SWT.CHECK);
        compileToJs.setText("Compile project to JavaScript");
        compileToJs.setSelection(backendJs);
        compileToJs.setEnabled(builderEnabled);
        
        Group troubleGroup = new Group(parent, SWT.NONE);
        troubleGroup.setText("Troubleshooting");
        troubleGroup.setLayout(new GridLayout(1, false));
        GridData gd3 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd3.grabExcessHorizontalSpace=true;
        troubleGroup.setLayoutData(gd3);
        
        enableExplodeModules = new Button(troubleGroup, SWT.CHECK);
        enableExplodeModules.setText("Disable Java classes calling Ceylon");
        enableExplodeModules.setSelection(!explodeModules);
        enableExplodeModules.setEnabled(builderEnabled&&backendJava);
        
        astAwareIncrementalBuidsButton = new Button(troubleGroup, SWT.CHECK);
        astAwareIncrementalBuidsButton.setText("Disable structure-aware incremental compilation");
        astAwareIncrementalBuidsButton.setSelection(!astAwareIncrementalBuids);
        astAwareIncrementalBuidsButton.setEnabled(builderEnabled);

        final Button logButton = new Button(troubleGroup, SWT.CHECK);
        logButton.setText("Log compiler activity to Eclipse console");
        boolean loggingEnabled = verbose!=null && !verbose.isEmpty();
        logButton.setSelection(loggingEnabled);
        logButton.setEnabled(builderEnabled);

        final Composite verbosityOptions = new Composite(troubleGroup, SWT.NONE);
        verbosityOptions.setLayout(new GridLayout(2, false));
        final GridData gd4 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd4.grabExcessHorizontalSpace=true;
        verbosityOptions.setLayoutData(gd4);
        gd4.exclude = !loggingEnabled;
        verbosityOptions.setVisible(loggingEnabled);
        verbosityOptions.setEnabled(loggingEnabled);
        
        final Label verbosityLabel = new Label(verbosityOptions, SWT.NONE);
        verbosityLabel.setText("Verbosity level");
        
        verboseText = new Combo(verbosityOptions, SWT.DROP_DOWN);
        verboseText.add("code");
        verboseText.add("ast");
        verboseText.add("loader");
        verboseText.add("cmr");
        verboseText.add("all");
        GridData vgd = new GridData();
        vgd.grabExcessHorizontalSpace = true;
        vgd.minimumWidth = 75;
        verboseText.setLayoutData(vgd);
        verboseText.setTextLimit(20);
        if (loggingEnabled) {
            verboseText.setText(verbose);
        }
        verboseText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                String str = verboseText.getText();
                if (str==null || str.isEmpty()) {
                    verbose = null;
                }
                else {
                    verbose = str.trim();
                }
            }
        });
        
        logButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                boolean selected = logButton.getSelection();
                verbose = selected ? verboseText.getText() : null;
                verboseText.setEnabled(selected);
                ((GridData) verbosityOptions.getLayoutData()).exclude = !selected;
                verbosityOptions.setVisible(selected);
                verbosityOptions.setEnabled(selected);
                verboseText.setVisible(selected);
                parent.layout();
            }
        });
        
        enableBuilderButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new CeylonNature().addToProject(getSelectedProject());
                enableBuilderButton.setEnabled(false);
                enableExplodeModules.setEnabled(true);
                astAwareIncrementalBuidsButton.setEnabled(true);
                compileToJs.setEnabled(true);
                compileToJava.setEnabled(true);
                offlineButton.setEnabled(true);
                logButton.setEnabled(true);
                builderEnabled=true;
            }
        });
    
        astAwareIncrementalBuidsButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                astAwareIncrementalBuids = !astAwareIncrementalBuids;
            }
        });
        
        enableExplodeModules.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                explodeModules = !explodeModules;
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
        buildPathsPageLink.setText("See '<a>Build Paths</a>' to configure project build paths.");
        buildPathsPageLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
                container.openPage(CeylonBuildPathsPropertiesPage.ID, null);
            }
        });
        
        Link openRepoPageLink = new Link(parent, 0);
        openRepoPageLink.setText("See '<a>Module Repositories</a>' to configure project module repositores.");
        openRepoPageLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
                container.openPage(CeylonRepoPropertiesPage.ID, null);
            }
        });
        
        Link warningsPageLink = new Link(parent, 0);
        warningsPageLink.setText("See '<a>Compilation Warnings</a>' to enable or disable warnings.");
        warningsPageLink.addSelectionListener(new SelectionAdapter() {
          @Override
          public void widgetSelected(SelectionEvent e) {
              IWorkbenchPreferenceContainer container = (IWorkbenchPreferenceContainer) getContainer();
              container.openPage(CeylonWarningsPropertiesPage.ID, null);
          }
      });
    }

    private void addCharacterEncodingLabel(Composite composite) {
        final Link link = new Link(composite, SWT.NONE);
        try {
            link.setText("Default source file encoding:  " +
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
                astAwareIncrementalBuids = areAstAwareIncrementalBuildsEnabled(project);
                explodeModules = isExplodeModulesEnabled(project);
                backendJs = compileToJs(project);
                backendJava = compileToJava(project);
                verbose = getVerbose(project);
                offlineOption = CeylonProjectConfig.get(project).isProjectOffline();
            }
        }

        addControls(composite);
        return composite;
    }
        
    @Override
    public void dispose() {
        if (encodingListener!=null) {
            getWorkspace().removeResourceChangeListener(encodingListener);
            encodingListener = null;
        }
        super.dispose();
    }
    
}