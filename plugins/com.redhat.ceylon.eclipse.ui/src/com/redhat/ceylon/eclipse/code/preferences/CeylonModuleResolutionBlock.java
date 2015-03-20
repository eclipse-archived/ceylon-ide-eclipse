package com.redhat.ceylon.eclipse.code.preferences;

import static org.eclipse.jface.layout.GridDataFactory.fillDefaults;
import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;

import java.io.StringReader;

import org.apache.commons.io.input.ReaderInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;

public class CeylonModuleResolutionBlock {

    private IProject project;

    private Boolean flatClasspath;
    private Boolean autoExportMavenDependencies;

    private Text overridesText;
    private Button overridesBrowseButton;
    private Button overridesCreateButton;
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
//        final Group resolutionGroup = new Group(parent, SWT.NONE);
//        resolutionGroup.setText("Module Resolution");
//        resolutionGroup.setLayoutData(fillDefaults().grab(true, false).create());
//        resolutionGroup.setLayout(GridLayoutFactory.swtDefaults().create());
        
        Label overridesLabel = new Label(parent, SWT.LEFT | SWT.WRAP);
        overridesLabel.setText("Overrides XML file (customize module resolution)");
        overridesLabel.setToolTipText("Specifies the xml file to use to load module overrides");
        overridesLabel.setLayoutData(swtDefaults().span(2, 1).grab(true, false).align(SWT.FILL, SWT.CENTER).create());

        overridesText = new Text(parent, SWT.SINGLE | SWT.BORDER);
        overridesText.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).hint(250,  SWT.DEFAULT).grab(true, false).create());
                
        Composite overridesComposite = new Composite(parent, SWT.NONE);
        overridesComposite.setLayout(new GridLayout(2, true));
        overridesComposite.setLayoutData(swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());

        initOverridesBrowseButton(overridesComposite);
        initOverridesCreateButton(overridesComposite);

//        Composite checkBoxesComposite = new Composite(parent, SWT.NONE);
//        checkBoxesComposite.setLayout(GridLayoutFactory.fillDefaults().equalWidth(false).create());
//        checkBoxesComposite.setLayoutData(swtDefaults().grab(true, true).create());

        flatClasspathButton = new Button(parent, SWT.CHECK);
        flatClasspathButton.setText("Use a flat classpath");
        flatClasspathButton.setLayoutData(swtDefaults().span(2, 1).grab(true, false).create());

        autoExportMavenDependenciesButton = new Button(parent, SWT.CHECK);
        autoExportMavenDependenciesButton.setText("Automatically export Maven dependencies");
        autoExportMavenDependenciesButton.setLayoutData(fillDefaults().span(2, 1).grab(true, false).create());


        performDefaults();
    }


    private void initOverridesBrowseButton(final Composite composite) {
        overridesBrowseButton = new Button(composite, SWT.PUSH);
        overridesBrowseButton.setText("Browse...");
        overridesBrowseButton.setLayoutData(swtDefaults().grab(true, true).align(SWT.FILL, SWT.CENTER).create());
        overridesBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                 FileDialog fileDialog = new FileDialog(composite.getShell(), SWT.SHEET);
                 fileDialog.setFilterExtensions(new String[] {"*.xml", "*.*"});
                 fileDialog.setFilterPath(project.getLocation().toFile().getAbsolutePath());
                 fileDialog.setFileName("overrides.xml");
                 String result = fileDialog.open();
                 if (result != null) {
                     IPath overridesPath = new Path(result);
                     IPath projectLocation = project.getLocation();
                     if (projectLocation.isPrefixOf(overridesPath)) {
                         result = overridesPath.removeFirstSegments(projectLocation.segmentCount()).toString();
                     }
                    overridesText.setText(result);
                }
            }
        });
    }

    private void initOverridesCreateButton(final Composite composite) {
        overridesCreateButton = new Button(composite, SWT.PUSH);
        overridesCreateButton.setText("Create...");
        overridesCreateButton.setLayoutData(swtDefaults().grab(true, true).align(SWT.FILL, SWT.CENTER).create());
        overridesCreateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                try {
                    IFile overridesResource = project.getFile("overrides.xml");
                    try {
                        overridesResource.refreshLocal(IResource.DEPTH_ZERO, null);
                    } catch (CoreException e1) {
                        e1.printStackTrace();
                    }
                    if (overridesResource.exists()) {
                        MessageBox box = new MessageBox(composite.getShell(), SWT.ICON_ERROR | SWT.OK);
                        box.setText("Overrides file creation");
                        box.setMessage("The Overrides.xml already exists at the root of the project");
                        box.open();
                        return;
                    }
                    overridesResource.create(new ReaderInputStream(new StringReader(
                            "<overrides xmlns=\"http://www.ceylon-lang.org/xsd/overrides\">\n" + 
                            "</overrides>")), true, null);
                    final IFile fileToOpen = overridesResource;
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Navigation.openInEditor(fileToOpen, true);
                            } catch (PartInitException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    overridesText.setText("overrides.xml");
                    MessageBox box = new MessageBox(composite.getShell(), SWT.ICON_INFORMATION | SWT.OK);
                    box.setText("Overrides file creation");
                    box.setMessage("The Overrides.xml was created");
                    box.open();
                } catch (CoreException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

}