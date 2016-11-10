package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.compiler.typechecker.TypeChecker.LANGUAGE_MODULE_VERSION;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonSystemRepo;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.interpolateVariablesInRepositoryPath;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;
import static org.eclipse.ui.views.navigator.ResourceComparator.NAME;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.redhat.ceylon.common.Constants;
import com.redhat.ceylon.common.config.CeylonConfigFinder;
import com.redhat.ceylon.common.config.Repositories;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.ide.common.configuration.CeylonRepositoryConfigurator;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;

@SuppressWarnings("serial")
public class CeylonRepoConfigBlock extends CeylonRepositoryConfigurator {

    public interface ValidationCallback {
        void validationResultChange(boolean isValid, String message);
    }

    private IProject project;
    private ValidationCallback validationCallback;

    private Text systemRepoText;
    private Text outputRepoText;
    private Table lookupRepoTable;
    private Button systemRepoBrowseButton;
    private Button outputRepoBrowseButton;
    private Button addRepoButton;
    private Button addExternalRepoButton;
    private Button addAetherRepoButton;
    private Button addRemoteRepoButton;
    private Button removeRepoButton;
    private Button upButton;
    private Button downButton;
    
    private CeylonModuleResolutionBlock moduleResolutionBlock;

    public CeylonRepoConfigBlock(ValidationCallback validationCallback) {
        this.validationCallback = validationCallback;
        moduleResolutionBlock = new CeylonModuleResolutionBlock();
    }
    
    public IProject getProject() {
        return project;
    }

    public Boolean getFlatClasspath() {
        return moduleResolutionBlock.getFlatClasspath();
    }

    public String getOverrides() {
        return moduleResolutionBlock.getOverrides();
    }

    public Boolean getAutoExportMavenDependencies() {
        return moduleResolutionBlock.getAutoExportMavenDependencies();
    }

    public Boolean getFullyExportMavenDependencies() {
        return moduleResolutionBlock.getFullyExportMavenDependencies();
    }

    public String getSystemRepo() {
        return systemRepoText.getText();
    }

    public String getOutputRepo() {
        return outputRepoText.getText();
    }

    public void performDefaults() {
        systemRepoText.setText("");
        outputRepoText.setText(Repositories.withConfig(
                CeylonConfigFinder.loadDefaultConfig(null))
                        .getOutputRepository()
                        .getUrl());

        lookupRepoTable.removeAll();
        
        moduleResolutionBlock.performDefaults();

        validate();
    }
    
    public void initState(IProject project, 
            boolean isCeylonNatureEnabled) {
        this.project = project;
        String systemRepo = getCeylonSystemRepo(project);
        if( systemRepo == null || 
                systemRepo.equals("${ceylon.repo}") ) {
        	systemRepoText.setText("");
        } else {
        	systemRepoText.setText(systemRepo);
        }
        systemRepoText.setEnabled(isCeylonNatureEnabled);
        systemRepoBrowseButton.setEnabled(isCeylonNatureEnabled);
        
        outputRepoText.setEnabled(isCeylonNatureEnabled);
        outputRepoBrowseButton.setEnabled(isCeylonNatureEnabled);
        
        lookupRepoTable.setEnabled(isCeylonNatureEnabled);
        lookupRepoTable.removeAll();
        
        if (isCeylonNatureEnabled) {
            CeylonProjectConfig config = 
                    modelJ2C().ceylonModel()
                        .getProject(project)
                        .getConfiguration();

            loadFromConfiguration(config);
            outputRepoText.setText(config.getOutputRepo());
        } else {
            outputRepoText.setText(Constants.DEFAULT_MODULE_DIR);
        }
        
        addRepoButton.setEnabled(isCeylonNatureEnabled);
        addExternalRepoButton.setEnabled(isCeylonNatureEnabled);
        addAetherRepoButton.setEnabled(isCeylonNatureEnabled);
        addRemoteRepoButton.setEnabled(isCeylonNatureEnabled);
        
        moduleResolutionBlock.initState(project, 
                isCeylonNatureEnabled);
    }

    public void initContents(Composite parent) {
        Composite composite = 
                new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(swtDefaults()
                .grab(true, true)
                .align(SWT.FILL, SWT.FILL)
                .create());

        initSystemRepoInput(composite);
        initSystemRepoBrowseButton(composite);
        initOutputRepoInput(composite);
        initOutputRepoBrowseButton(composite);
        initLookupRepoTable(composite);

        Composite lookupRepoButtons = 
                new Composite(composite, SWT.NONE);
        lookupRepoButtons.setLayout(new GridLayout(1, false));
        lookupRepoButtons.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.TOP)
                .span(1, 4)
                .create());

        initAddRepoButton(lookupRepoButtons);
        initAddExternalRepoButton(lookupRepoButtons);
        initAddAetherRepoButton(lookupRepoButtons);
        initAddRemoteRepoButton(lookupRepoButtons);
        initRemoveRepoButton(lookupRepoButtons);
        initUpDownButtons(lookupRepoButtons);
        
        moduleResolutionBlock.initContents(composite);
        performDefaults();
    }

    private void initSystemRepoInput(Composite composite) {
        Label systemRepoLabel = 
                new Label(composite, SWT.LEFT | SWT.WRAP);
        systemRepoLabel.setText(
                "System repository (contains language module)");
        systemRepoLabel.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .span(2, 1)
                .grab(true, false)
                .create());

        systemRepoText = 
                new Text(composite, SWT.SINGLE | SWT.BORDER);
        systemRepoText.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .grab(true, false)
                .create());
        systemRepoText.setMessage("IDE System Modules");
        systemRepoText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
            	String systemRepoUrl = systemRepoText.getText();
            	if( systemRepoUrl==null || systemRepoUrl.isEmpty() ) {
            		String path = 
            		        CeylonPlugin.getInstance()
            		            .getCeylonRepository()
            		            .getAbsolutePath();
                    systemRepoText.setToolTipText(path);
            	} else {
            		systemRepoText.setToolTipText("");
            	}
                validate();
            }
        });
    }

    private void initSystemRepoBrowseButton(final Composite composite) {
        systemRepoBrowseButton = 
                new Button(composite, SWT.PUSH);
        systemRepoBrowseButton.setText("Browse...");
        systemRepoBrowseButton.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        systemRepoBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String result = 
                        new DirectoryDialog(composite.getShell(), 
                                SWT.SHEET).open();
                if (result != null) {
                    systemRepoText.setText(result);
                }
            }
        });
    }

    private void initOutputRepoInput(Composite composite) {
        Label outputRepoLabel = 
                new Label(composite, SWT.LEFT | SWT.WRAP);
        outputRepoLabel.setText(
                "Output repository (contains compiled module archives)");
        outputRepoLabel.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .span(2, 1)
                .grab(true, false)
                .indent(0, 10)
                .create());

        outputRepoText = 
                new Text(composite, SWT.SINGLE | SWT.BORDER);
        outputRepoText.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .grab(true, false)
                .create());
        outputRepoText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });    
    }

    private void initOutputRepoBrowseButton(final Composite composite) {
        outputRepoBrowseButton = 
                new Button(composite, SWT.PUSH);
        outputRepoBrowseButton.setText("Browse...");
        outputRepoBrowseButton.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        outputRepoBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IResource result = 
                        openSelectRelativeFolderDialog(composite, 
                                "Output Repository",
                                "Select a workspace folder for compiled module archives");
                if (result != null) {
                    String outputRepoUrl = 
                            "." + File.separator + 
                            result.getFullPath()
                                .removeFirstSegments(1);
                    outputRepoText.setText(outputRepoUrl);
                }
            }
        });
    }

    private void initLookupRepoTable(Composite composite) {
        Label lookupRepoLabel = 
                new Label(composite, SWT.LEFT | SWT.WRAP);
        lookupRepoLabel.setText("Lookup repositories on build path:");
        lookupRepoLabel.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .span(2, 1)
                .grab(true, false)
                .indent(0, 10)
                .create());

        lookupRepoTable = 
                new Table(composite, 
                        SWT.MULTI | SWT.BORDER | 
                        SWT.V_SCROLL | SWT.H_SCROLL);
        lookupRepoTable.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.FILL)
                .grab(true, true)
                .hint(250, 220)
                .create());
        lookupRepoTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateButtonState();
            }
        });
    }

    private void initAddRepoButton(final Composite buttons) {
        addRepoButton = new Button(buttons, SWT.PUSH);
        addRepoButton.setText("Add Repository...");
        addRepoButton.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        addRepoButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IResource result = 
                        openSelectRelativeFolderDialog(buttons, 
                                "Add Repository",
                                "Select a module repository in the workspace");
                if( result != null ) {
                    String repo = 
                            "." + File.separator + 
                            result.getFullPath()
                                .removeFirstSegments(1);
                    addExternalRepo(repo);
                    validate();
                }
            }
        });
    }

    private void initAddExternalRepoButton(final Composite buttons) {
        addExternalRepoButton = 
                new Button(buttons, SWT.PUSH);
        addExternalRepoButton.setText("Add External Repository...");
        addExternalRepoButton.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        addExternalRepoButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String result = 
                        new DirectoryDialog(buttons.getShell(), 
                                SWT.SHEET).open();
                if (result != null) {
                    addExternalRepo(result);
                    validate();
                }
            }
        });
    }

    private void initAddAetherRepoButton(final Composite buttons) {
        addAetherRepoButton = 
                new Button(buttons, SWT.PUSH);
        addAetherRepoButton.setText("Add Maven Repository...");
        addAetherRepoButton.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        addAetherRepoButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                AetherRepositoryDialog dlg = 
                        new AetherRepositoryDialog(buttons.getShell());
                int result = dlg.open();
                if (result == InputDialog.OK) {
                    addAetherRepo(dlg.getValue());
                }
            }
        });
    }
    
    private void initAddRemoteRepoButton(final Composite buttons) {
        addRemoteRepoButton = new Button(buttons, SWT.PUSH);
        addRemoteRepoButton.setText("Add Remote Repository...");
        addRemoteRepoButton.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        addRemoteRepoButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IInputValidator inputValidator = 
                        new IInputValidator() {
                    @Override
                    public String isValid(String val) {
                        try {
                            // FIXME: we might want to validate it more than that: for example to check that it's http/https
                            URI uri = new URI(val);
                            if (!uri.isAbsolute()) {
                                return "Unsupported URI: missing scheme";
                            }
                            return null;
                        } catch (URISyntaxException e) {
                            return "Invalid URI: " + 
                                    e.getReason();
                        }
                    }
                };
                InputDialog input = 
                        new InputDialog(buttons.getShell(), 
                                "Add Remote Repository", 
                                "Enter URI of remote module repository", 
                                "http://", 
                                inputValidator);
                int result = input.open();
                if (result == InputDialog.OK) {
                    addRemoteRepo(input.getValue());
                }
            }
        });
    }

    private void initRemoveRepoButton(Composite buttons) {
        removeRepoButton = new Button(buttons, SWT.PUSH);
        removeRepoButton.setText("Remove Repository");
        removeRepoButton.setEnabled(false);
        removeRepoButton.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER).create());
        removeRepoButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeSelectedRepo();
                lookupRepoTable.deselectAll();
            }
        });
    }
    
    private void initUpDownButtons(Composite buttons) {
        upButton = new Button(buttons, SWT.PUSH);
        upButton.setText("Up");
        upButton.setEnabled(false);
        upButton.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .indent(0, 10)
                .create());
        upButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveSelectedReposUp();
            }
        });
        
        downButton = new Button(buttons, SWT.PUSH);
        downButton.setText("Down");
        downButton.setEnabled(false);
        downButton.setLayoutData(swtDefaults()
                .align(SWT.FILL, SWT.CENTER)
                .create());
        downButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                moveSelectedReposDown();
            }
        });
    }

    private void validate() {
        if (!isSystemRepoValid()) {
            validationCallback.validationResultChange(false, 
                    "Please select a system module repository containing the language module");
        } else if (!isOutputRepoValid()) {
            validationCallback.validationResultChange(false, 
                    "Please select a output module repository inside project");
        } else {
            validationCallback.validationResultChange(true, null);
        }
    }

    private boolean isSystemRepoValid() {
        String systemRepoUrl = systemRepoText.getText();
        if( systemRepoUrl == null || systemRepoUrl.isEmpty() ) {
        	return true;
        }
        
        systemRepoUrl = 
                interpolateVariablesInRepositoryPath(systemRepoUrl);

        String ceylonLanguageSubdir = 
                File.separator + "ceylon"+
                File.separator + "language"+ 
                File.separator + LANGUAGE_MODULE_VERSION;
        String ceylonLanguageFileName = 
                File.separator + "ceylon.language-" + 
                        LANGUAGE_MODULE_VERSION + ".car";
        IPath systemRepoPath = new Path(systemRepoUrl);
        if (! systemRepoPath.isAbsolute()) {
            systemRepoPath = 
                    project.getLocation()
                        .append(systemRepoPath);
        }
        File systemRepoFile = systemRepoPath.toFile();
        File ceylonLanguageFile = 
                new File(systemRepoFile + 
                        ceylonLanguageSubdir + 
                        ceylonLanguageFileName);
        if (ceylonLanguageFile.exists() && 
                ceylonLanguageFile.isFile()) {
        	return true;
        }
            
        return false;
    }

    private boolean isOutputRepoValid() {
        String outputRepoUrl = outputRepoText.getText();
        if (outputRepoUrl.startsWith("./") || 
                outputRepoUrl.startsWith(".\\")) {
            return true;
        }
        return false;
    }

    private IResource openSelectRelativeFolderDialog(Control control, 
            String title, String description) {
        IWorkspaceRoot root = 
                project.getWorkspace().getRoot();

        Class<?>[] acceptedClasses = 
                new Class[] { IProject.class, IFolder.class };
        ISelectionStatusValidator validator = 
                new TypedElementSelectionValidator(acceptedClasses, 
                        false);
        IProject[] allProjects = root.getProjects();
        ArrayList<IProject> rejectedElements = 
                new ArrayList<IProject>
                    (allProjects.length);
        for (int i= 0; i < allProjects.length; i++) {
            if (!allProjects[i].equals(project)) {
                rejectedElements.add(allProjects[i]);
            }
        }
        ViewerFilter filter = 
                new TypedViewerFilter(acceptedClasses, 
                        rejectedElements.toArray());

        IResource container = null;

        FolderSelectionDialog dialog = 
                new FolderSelectionDialog(control.getShell(), 
                        new WorkbenchLabelProvider(), 
                        new WorkbenchContentProvider());
        dialog.setTitle(title);
        dialog.setValidator(validator);
        dialog.setMessage(description);
        dialog.addFilter(filter);
        dialog.setInput(root);
        dialog.setInitialSelection(container);
        dialog.setComparator(new ResourceComparator(NAME));

        if (dialog.open() == Window.OK) {
            return (IResource) dialog.getFirstResult();
        }
        return null;
    }

    @Override
    public Object addAllRepositoriesToList(String[] repos) {
        for (String repo : repos) {
            TableItem tableItem = 
                    new TableItem(lookupRepoTable, SWT.NONE);
            tableItem.setText(repo);
            tableItem.setImage(CeylonResources.REPO);
        }
        return null;
    }

    @Override
    public Object addRepositoryToList(long index, String repo) {
       TableItem tableItem = 
               new TableItem(lookupRepoTable, SWT.NONE, (int) index);
       tableItem.setText(repo);
       tableItem.setImage(CeylonResources.REPO);
       lookupRepoTable.setSelection((int) index);

       return null;
    }

    @Override
    public Object enableDownButton(boolean enabled) {
        downButton.setEnabled(enabled);
        return null;
    }

    @Override
    public Object enableRemoveButton(boolean enabled) {
        removeRepoButton.setEnabled(enabled);
        return null;
    }

    @Override
    public Object enableUpButton(boolean enabled) {
        upButton.setEnabled(enabled);
        return null;
    }

    @Override
    public int[] getSelection() {
        return lookupRepoTable.getSelectionIndices();
    }

    @Override
    public String removeRepositoryFromList(long index) {
        String repo = 
                lookupRepoTable.getItem((int) index)
                    .getText();
        lookupRepoTable.deselectAll();
        lookupRepoTable.remove((int) index);
        return repo;
    }
}
