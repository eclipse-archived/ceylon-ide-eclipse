package com.redhat.ceylon.eclipse.code.preferences;

import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;

public class CeylonModuleResolutionBlock {

    private IProject project;

    private Boolean flatClasspath;
    private Boolean autoExportMavenDependencies;

    private Text overridesText;
    private Button overridesBrowseButton;
    private Button flatClasspathButton;
    private Button autoExportMavenDependenciesButton;
    

    public CeylonModuleResolutionBlock() {
    }
    
    public IProject getProject() {
        return project;
    }

    public String getOverrides() {
        String overrides = overridesText.getText();
        return overrides.isEmpty() ? null : overrides;
    }

    public Boolean getFlatClasspath() {
        return flatClasspath;
    }

    public Boolean getAutoExportMavenDependencies() {
        return autoExportMavenDependencies;
    }

    public void performDefaults() {
        overridesText.setText("");

    }
    
    public void initState(IProject project, boolean isCeylonNatureEnabled) {
        this.project = project;
        
        String overrides = null;
        if (isCeylonNatureEnabled) {
            overrides = CeylonProjectConfig.get(project).getProjectOverrides();
            flatClasspath = CeylonProjectConfig.get(project).isProjectFlatClasspath();
            autoExportMavenDependencies = CeylonProjectConfig.get(project).isProjectAutoExportMavenDependencies();
        }
        
        flatClasspathButton.setSelection(flatClasspath!=null&&flatClasspath);
        flatClasspathButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (flatClasspath == null) {
                    flatClasspath = true;
                } else {
                    flatClasspath = !flatClasspath;
                }
            }
        });

        autoExportMavenDependenciesButton.setSelection(autoExportMavenDependencies!=null&&autoExportMavenDependencies);
        autoExportMavenDependenciesButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                if (autoExportMavenDependencies == null) {
                    autoExportMavenDependencies = true;
                } else {
                    autoExportMavenDependencies = !autoExportMavenDependencies;
                }
            }
        });

        if (overrides != null) {
            overridesText.setText(overrides.trim());
        } else {
            overridesText.setText("");
        }
    }

    public void initContents(Composite parent) {
        final Group resolutionGroup = new Group(parent, SWT.NONE);
        resolutionGroup.setText("Module Resolution");
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gd.grabExcessHorizontalSpace=true;
        resolutionGroup.setLayoutData(gd);
        GridLayout layout = new GridLayout();
        
        layout.numColumns = 1;
        layout.marginBottom = 1;
        resolutionGroup.setLayout(layout);
        
        Label overridesLabel = new Label(resolutionGroup, SWT.LEFT | SWT.WRAP);
                
        overridesLabel.setText("Overrides XML file");
        overridesLabel.setToolTipText("Specifies the xml file to use to load module overrides");
        overridesLabel.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());

        Composite overridesComposite = new Composite(resolutionGroup, SWT.NONE);
        overridesComposite.setLayout(new GridLayout(2, false));
        overridesComposite.setLayoutData(swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());

        overridesText = new Text(overridesComposite, SWT.SINGLE | SWT.BORDER);
        overridesText.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
                
        initOverridesBrowseButton(overridesComposite);

        Composite checkBoxesComposite = new Composite(resolutionGroup, SWT.NONE);
        checkBoxesComposite.setLayout(new GridLayout(2, false));
        checkBoxesComposite.setLayoutData(swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());

        flatClasspathButton = new Button(checkBoxesComposite, SWT.CHECK);
        flatClasspathButton.setText("Use a flat classpath");
        flatClasspathButton.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).span(1, 1).grab(true, false).create());

        autoExportMavenDependenciesButton = new Button(checkBoxesComposite, SWT.CHECK);
        autoExportMavenDependenciesButton.setText("Automatically export Maven dependencies");
        autoExportMavenDependenciesButton.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).span(1, 1).grab(true, false).create());


        performDefaults();
    }


    private void initOverridesBrowseButton(final Composite composite) {
        overridesBrowseButton = new Button(composite, SWT.PUSH);
        overridesBrowseButton.setText("Browse...");
        overridesBrowseButton.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).create());
        overridesBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                 FileDialog fileDialog = new FileDialog(composite.getShell(), SWT.SHEET);
                 fileDialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
                 fileDialog.setFileName("overrides.xml");
                 String result = fileDialog.open();
                 if (result != null) {
                    overridesText.setText(result);
                }
            }
        });
    }


}