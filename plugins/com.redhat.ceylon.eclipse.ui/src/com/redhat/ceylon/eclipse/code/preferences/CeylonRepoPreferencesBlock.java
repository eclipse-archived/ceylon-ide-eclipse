package com.redhat.ceylon.eclipse.code.preferences;

import static com.redhat.ceylon.compiler.typechecker.TypeChecker.LANGUAGE_MODULE_VERSION;
import static org.eclipse.jface.layout.GridDataFactory.swtDefaults;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;

public class CeylonRepoPreferencesBlock {

    public interface ValidationCallback {

        void validationResultChange(boolean isValid, String message);

    }

    private IProject project;
    private ValidationCallback validationCallback;

    private List<String> globalLookupRepos;
    private List<String> projectLookupRepos;

    private Text systemRepoText;
    private Text outputRepoText;
    private Table lookupRepoTable;
    private Button systemRepoBrowseButton;
    private Button outputRepoBrowseButton;
    private Button addRepoButton;
    private Button addExternalRepoButton;
    private Button addRemoteRepoButton;
    private Button removeRepoButton;

    public CeylonRepoPreferencesBlock(ValidationCallback validationCallback) {
        this.validationCallback = validationCallback;
    }

    public String getSystemRepo() {
        return systemRepoText.getText();
    }

    public String getOutputRepo() {
        return outputRepoText.getText();
    }

    public List<String> getProjectLookupRepos() {
        return projectLookupRepos;
    }

    public void performDefaults() {
        systemRepoText.setText("${ceylon.repo}");
        outputRepoText.setText("./modules");

        projectLookupRepos = new ArrayList<String>();
        lookupRepoTable.removeAll();
        if( globalLookupRepos != null ) {
            for (String repo : globalLookupRepos) {
                addLookupRepo(repo, true);
            }
        }
        
        validate();
    }
    
    public void initState(IProject project, boolean isCeylonNatureEnabled) {
        this.project = project;
        
        if (isCeylonNatureEnabled) {
            projectLookupRepos = CeylonProjectConfig.get(project).getProjectLookupRepos();
            globalLookupRepos = CeylonProjectConfig.get(project).getGlobalLookupRepos();
        }
        
        systemRepoText.setText(CeylonBuilder.getCeylonSystemRepo(project));
        systemRepoText.setEnabled(isCeylonNatureEnabled);
        systemRepoBrowseButton.setEnabled(isCeylonNatureEnabled);
        
        outputRepoText.setText(CeylonProjectConfig.get(project).getOutputRepo());
        outputRepoText.setEnabled(isCeylonNatureEnabled);
        outputRepoBrowseButton.setEnabled(isCeylonNatureEnabled);
        
        lookupRepoTable.setEnabled(isCeylonNatureEnabled);
        lookupRepoTable.removeAll();
        if( projectLookupRepos != null ) {
            for (String repo : projectLookupRepos) {
                addLookupRepo(repo, false);
            }
        }
        if( globalLookupRepos != null ) {
            for (String repo : globalLookupRepos) {
                addLookupRepo(repo, true);
            }
        }
        
        addRepoButton.setEnabled(isCeylonNatureEnabled);
        addExternalRepoButton.setEnabled(isCeylonNatureEnabled);
        addRemoteRepoButton.setEnabled(isCeylonNatureEnabled);
        removeRepoButton.setEnabled(isCeylonNatureEnabled);
    }

    public void initContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());

        initSystemRepoInput(composite);
        initSystemRepoBrowseButton(composite);
        initOutputRepoInput(composite);
        initOutputRepoBrowseButton(composite);
        initLookupRepoTable(composite);

        Composite lookupRepoButtons = new Composite(composite, SWT.NONE);
        lookupRepoButtons.setLayout(new GridLayout(1, false));
        lookupRepoButtons.setLayoutData(swtDefaults().align(SWT.FILL, SWT.TOP).span(1, 4).create());

        initAddRepoButton(lookupRepoButtons);
        initAddExternalRepoButton(lookupRepoButtons);
        initAddRemoteRepoButton(lookupRepoButtons);
        initRemoveRepoButton(lookupRepoButtons);
        
        performDefaults();
    }

    private void initSystemRepoInput(Composite composite) {
        Label systemRepoLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        systemRepoLabel.setText("System repository (contains language module)");
        systemRepoLabel.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false).create());

        systemRepoText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        systemRepoText.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
        systemRepoText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });    
    }

    private void initSystemRepoBrowseButton(final Composite composite) {
        systemRepoBrowseButton = new Button(composite, SWT.PUSH);
        systemRepoBrowseButton.setText("Browse...");
        systemRepoBrowseButton.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).create());
        systemRepoBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String result = new DirectoryDialog(composite.getShell(), SWT.SHEET).open();
                if (result != null) {
                    systemRepoText.setText(result);
                }
            }
        });
    }

    private void initOutputRepoInput(Composite composite) {
        Label outputRepoLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        outputRepoLabel.setText("Output repository (contains compiled module archives)");
        outputRepoLabel.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false).indent(0, 10).create());

        outputRepoText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        outputRepoText.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).create());
        outputRepoText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                validate();
            }
        });    
    }

    private void initOutputRepoBrowseButton(final Composite composite) {
        outputRepoBrowseButton = new Button(composite, SWT.PUSH);
        outputRepoBrowseButton.setText("Browse...");
        outputRepoBrowseButton.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).create());
        outputRepoBrowseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IResource result = openSelectRelativeFolderDialog(composite);
                if (result != null) {
                    String outputRepoUrl = "./" + result.getFullPath().removeFirstSegments(1);
                    outputRepoText.setText(outputRepoUrl);
                }
            }
        });
    }

    private void initLookupRepoTable(Composite composite) {
        Label lookupRepoLabel = new Label(composite, SWT.LEFT | SWT.WRAP);
        lookupRepoLabel.setText("Lookup repositories on build path:");
        lookupRepoLabel.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).grab(true, false).indent(0, 10).create());

        lookupRepoTable = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        lookupRepoTable.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).hint(250, 200).create());
    }

    private void initAddRepoButton(final Composite buttons) {
        addRepoButton = new Button(buttons, SWT.PUSH);
        addRepoButton.setText("Add Repository...");
        addRepoButton.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).create());
        addRepoButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IResource result = openSelectRelativeFolderDialog(buttons);
                if( result != null ) {
                    String repo = "./" + result.getFullPath().removeFirstSegments(1);
                    addProjectLookupRepo(repo);
                }
            }
        });
    }

    private void initAddExternalRepoButton(final Composite buttons) {
        addExternalRepoButton = new Button(buttons, SWT.PUSH);
        addExternalRepoButton.setText("Add External Repository...");
        addExternalRepoButton.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).create());
        addExternalRepoButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String result = new DirectoryDialog(buttons.getShell(), SWT.SHEET).open();
                if (result != null) {
                    addProjectLookupRepo(result);
                }
            }
        });
    }

    private void initAddRemoteRepoButton(final Composite buttons) {
        addRemoteRepoButton = new Button(buttons, SWT.PUSH);
        addRemoteRepoButton.setText("Add Remote Repository...");
        addRemoteRepoButton.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).create());
        addRemoteRepoButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                IInputValidator inputValidator = new IInputValidator() {
                    @Override
                    public String isValid(String val) {
                        try {
                            // FIXME: we might want to validate it more than that: for example to check that it's http/https
                            new URI(val);
                            return null;
                        } catch (URISyntaxException e) {
                            return "Invalid URI: " + e.getReason();
                        }
                    }
                };
                InputDialog input = new InputDialog(buttons.getShell(), "Add Remote Repository", "Enter a remote repository URI", "http://", inputValidator);
                int result = input.open();
                if (result == InputDialog.OK) {
                    addProjectLookupRepo(input.getValue());
                }
            }
        });
    }

    private void initRemoveRepoButton(Composite buttons) {
        removeRepoButton = new Button(buttons, SWT.PUSH);
        removeRepoButton.setText("Remove Repository");
        removeRepoButton.setLayoutData(swtDefaults().align(SWT.FILL, SWT.CENTER).create());
        removeRepoButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] selection = lookupRepoTable.getSelectionIndices();
                for(int index : selection) {
                    if( index < projectLookupRepos.size() ) {
                        lookupRepoTable.remove(index);
                        projectLookupRepos.remove(index);
                    }
                }
                lookupRepoTable.deselectAll();
            }
        });
        lookupRepoTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int[] selectionIndices = lookupRepoTable.getSelectionIndices();
                for(int index : selectionIndices) {
                    if( index < projectLookupRepos.size() ) {
                        removeRepoButton.setEnabled(true);
                        return;
                    }
                }
                removeRepoButton.setEnabled(false);
            }
        });
    }

    private void validate() {
        if (!isSystemRepoValid()) {
            validationCallback.validationResultChange(false, "Please select a system module repository containing the language module");
        } else if (!isOutputRepoValid()) {
            validationCallback.validationResultChange(false, "Please select a output module repository inside project");
        } else {
            validationCallback.validationResultChange(true, null);
        }
    }

    private boolean isSystemRepoValid() {
        String systemRepoUrl = systemRepoText.getText();
        if (systemRepoUrl != null && !systemRepoUrl.isEmpty()) {
            systemRepoUrl = CeylonBuilder.interpolateVariablesInRepositoryPath(systemRepoUrl);

            String ceylonLanguageSubdir = "/ceylon/language/" + LANGUAGE_MODULE_VERSION;
            String ceylonLanguageFileName = "/ceylon.language-" + LANGUAGE_MODULE_VERSION + ".car";

            File ceylonLanguageFile = new File(systemRepoUrl + ceylonLanguageSubdir + ceylonLanguageFileName);
            if (ceylonLanguageFile.exists() && ceylonLanguageFile.isFile()) {
                return true;
            }
        }
        return false;
    }

    private boolean isOutputRepoValid() {
        String outputRepoUrl = outputRepoText.getText();
        if (outputRepoUrl != null && outputRepoUrl.startsWith("./")) {
            return true;
        }
        return false;
    }

    private IResource openSelectRelativeFolderDialog(Control control) {
        IWorkspaceRoot root = project.getWorkspace().getRoot();

        Class<?>[] acceptedClasses= new Class[] { IProject.class, IFolder.class };
        ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);
        IProject[] allProjects= root.getProjects();
        ArrayList<IProject> rejectedElements= new ArrayList<IProject>(allProjects.length);
        for (int i= 0; i < allProjects.length; i++) {
            if (!allProjects[i].equals(project)) {
                rejectedElements.add(allProjects[i]);
            }
        }
        ViewerFilter filter= new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

        ILabelProvider lp= new WorkbenchLabelProvider();
        ITreeContentProvider cp= new WorkbenchContentProvider();

        IResource container= null;
        try {
            container = getOutputFolder();
            if (container.exists() && container.isHidden()) {
                container.setHidden(false); // TODO: whooaah awful hack!
            }
        } catch(IllegalArgumentException iae) {
            // noop (IllegalArgumentException: Path must include project and resource name)
        } catch (CoreException ce) {
            ce.printStackTrace();
        }

        try {
            FolderSelectionDialog dialog= new FolderSelectionDialog(control.getShell(), lp, cp);
            dialog.setTitle(NewWizardMessages.BuildPathsBlock_ChooseOutputFolderDialog_title);
            dialog.setValidator(validator);
            dialog.setMessage(NewWizardMessages.BuildPathsBlock_ChooseOutputFolderDialog_description);
            dialog.addFilter(filter);
            dialog.setInput(root);
            dialog.setInitialSelection(container);
            dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

            if (dialog.open() == Window.OK) {
                return (IResource)dialog.getFirstResult();
            }
            return null;
        }
        finally {
            if (container != null && container.exists()) {
                try {
                    container.setHidden(true);
                } 
                catch (Exception ce) {
                    ce.printStackTrace();
                }
            }
        }
    }

    private void addProjectLookupRepo(String repo) {
        if (!globalLookupRepos.contains(repo) && !projectLookupRepos.contains(repo)) {
            projectLookupRepos.add(0, repo);

            TableItem tableItem = new TableItem(lookupRepoTable, SWT.NONE, 0);
            tableItem.setText(repo);
            tableItem.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CeylonResources.RUNTIME_OBJ));
            lookupRepoTable.setSelection(tableItem);

            validate();
        }
    }

    private void addLookupRepo(String repo, boolean isGlobalRepo) {
        TableItem tableItem = new TableItem(lookupRepoTable, SWT.NONE);
        tableItem.setText(repo);
        tableItem.setImage(CeylonPlugin.getInstance().getImageRegistry().get(CeylonResources.RUNTIME_OBJ));
        if (isGlobalRepo) {
            tableItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
        }
    }

    private IFolder getOutputFolder() {
        String outputRepoUrl = outputRepoText.getText();
        if (outputRepoUrl.startsWith("./")) {
            outputRepoUrl = outputRepoUrl.substring(2);
        }
        return project.getFolder(outputRepoUrl);
    }

}