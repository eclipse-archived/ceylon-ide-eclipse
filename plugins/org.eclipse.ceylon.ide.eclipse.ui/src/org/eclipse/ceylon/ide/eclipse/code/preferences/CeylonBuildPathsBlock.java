/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ceylon.ide.eclipse.code.preferences;


import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_RESOURCE_FOLDER;
import static org.eclipse.ceylon.ide.eclipse.code.preferences.CeylonPreferenceInitializer.DEFAULT_SOURCE_FOLDER;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static org.eclipse.ceylon.ide.eclipse.util.CeylonHelper.toJavaStringList;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.dialogs.StatusInfo;
import org.eclipse.jdt.internal.ui.dialogs.StatusUtil;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.jdt.internal.ui.viewsupport.BasicElementLabels;
import org.eclipse.jdt.internal.ui.viewsupport.ImageDisposer;
import org.eclipse.jdt.internal.ui.wizards.IStatusChangeListener;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathBasePage;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.BuildPathSupport;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListElement;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListLabelProvider;
//import org.eclipse.jdt.internal.ui.wizards.buildpaths.ClasspathOrderingWorkbookPage;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
//import org.eclipse.jdt.internal.ui.wizards.buildpaths.LibrariesWorkbookPage;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.ProjectsWorkbookPage;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.views.navigator.ResourceComparator;

import org.eclipse.ceylon.common.Constants;
import org.eclipse.ceylon.common.config.CeylonConfig;
import org.eclipse.ceylon.common.config.CeylonConfigFinder;
import org.eclipse.ceylon.ide.eclipse.code.editor.Navigation;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;
import org.eclipse.ceylon.ide.common.model.BaseCeylonProject;
import org.eclipse.ceylon.ide.common.model.CeylonProject;
import org.eclipse.ceylon.ide.common.model.CeylonProjectConfig;
import org.eclipse.ceylon.ide.common.model.resourceDirectoriesFromCeylonConfig_;
import org.eclipse.ceylon.ide.common.model.sourceDirectoriesFromCeylonConfig_;

import ceylon.interop.java.CeylonStringIterable;

public class CeylonBuildPathsBlock {

    private final class CeylonConfigNotInSyncListener extends SelectionAdapter {
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (".ceylon/config".equals(e.text)) {
                IFile configFile= fCurrJProject.getProject().getFile(".ceylon/config");
                if (configFile.exists()) {
                    if (! configFile.isSynchronized(IResource.DEPTH_ZERO)) {
                        try {
                            configFile.refreshLocal(IResource.DEPTH_ZERO, null);
                        } catch (CoreException e1) {
                        }
                    }
                    try {
                        Navigation.openInEditor(configFile);
                    } catch (PartInitException e1) {
                    }
                } else {
                    MessageDialog.openInformation(getShell(), 
                            "Ceylon Configuration File", 
                            "No configuration file exist\n"
                            + "Default vaues apply :\n"
                            + "  'source' for source files\n"
                            + "  'resource' for resource files");
                }
            } else if ("use".equals(e.text)) {
                refreshSourcePathsFromConfigFile();
                if (fSourceContainerPage != null) {
                    fSourceContainerPage.init(fCurrJProject);
                }
                updateUI();
                
            } else if ("resolve".equals(e.text)) {
                final Set<String> sourceFoldersFromCeylonConfig = new TreeSet<String>();
                final Set<String> sourceFoldersFromEclipseProject = new TreeSet<String>();
                final Set<String> resourceFoldersFromCeylonConfig = new TreeSet<String>();
                final Set<String> resourceFoldersFromEclipseProject = new TreeSet<String>();
                fillBuildPathsSetsForComparison(fCurrJProject.getProject(), sourceFoldersFromCeylonConfig,
                        sourceFoldersFromEclipseProject,
                        resourceFoldersFromCeylonConfig,
                        resourceFoldersFromEclipseProject);
                
                class BuildPathsComparisonDialog extends Dialog {
                    Set<String> resultSourcesSet = new TreeSet<>();
                    Set<String> resultResourcesSet = new TreeSet<>();
                    Tree resultPathsTree;
                    TreeItem resultSources;
                    TreeItem resultResources;

                    final String CONFLICTING = "conflicting";
                    final String RESULT_FOLDER_SET = "resultFolderSet";

                    protected BuildPathsComparisonDialog() {
                        super(fSWTWidget.getShell());
                    }
                    
                    @Override
                    protected void configureShell(Shell newShell) {
                        super.configureShell(newShell);
                        newShell.setText("Ceylon Build Paths Conflict Resolution");
                        newShell.setMinimumSize(700, 700);
                    }
                    
                    @Override
                    protected Control createDialogArea(Composite parent) {
                        Color red = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
                        Composite composite = (Composite) super.createDialogArea(parent);
                        GridLayout layout = (GridLayout) composite.getLayout();
                        layout.numColumns = 1;

                        Group comparisonArea = new Group(composite, SWT.NONE);
                        comparisonArea.setText("Conflicting build paths");
                        comparisonArea.setLayoutData(new GridData(GridData.FILL_BOTH));
                        GridLayout comparisonLayout = new GridLayout();
                        comparisonLayout.numColumns = 2;
                        comparisonLayout.makeColumnsEqualWidth= true;
                        comparisonLayout.verticalSpacing = 5;
                        comparisonArea.setLayout(comparisonLayout);

                        Label explanationLabel = new Label(comparisonArea, SWT.NONE);
                        explanationLabel.setText("The red entries show conflicts.\n"
                                + "You can decide what to do with them through the context menu.");
                        explanationLabel.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                        GridData explanationGridData = new GridData(GridData.FILL_HORIZONTAL);
                        explanationGridData.horizontalSpan = 2;
                        explanationLabel.setLayoutData(explanationGridData);
                        
                        Group resultArea = new Group(composite, SWT.NONE);
                        resultArea.setText("Merged build paths");
                        resultArea.setLayoutData(new GridData(GridData.FILL_BOTH));
                        GridLayout resultLayout = new GridLayout();
                        resultLayout.numColumns = 1;
                        resultArea.setLayout(resultLayout);

                        resultPathsTree = new Tree(resultArea, SWT.NONE);
                        resultPathsTree.setLayoutData(new GridData(GridData.FILL_BOTH));

                        Label label = new Label(comparisonArea, SWT.NONE);
                        label.setText("Eclipse project");
                        label = new Label(comparisonArea, SWT.NONE);
                        label.setText("Ceylon configuration file");
                        final Tree projectBasedPathsTree = new Tree(comparisonArea, SWT.NONE);
                        projectBasedPathsTree.setLayoutData(new GridData(GridData.FILL_BOTH));
                        projectBasedPathsTree.addFocusListener(new FocusListener() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                projectBasedPathsTree.setSelection(new TreeItem[0]);
                            }
                            
                            @Override
                            public void focusGained(FocusEvent e) {
                            }
                        });
                        final Tree configBasedPathsTree = new Tree(comparisonArea, SWT.NONE);
                        configBasedPathsTree.setLayoutData(new GridData(GridData.FILL_BOTH));
                        configBasedPathsTree.addFocusListener(new FocusListener() {
                            @Override
                            public void focusLost(FocusEvent e) {
                                configBasedPathsTree.setSelection(new TreeItem[0]);
                            }
                            
                            @Override
                            public void focusGained(FocusEvent e) {
                            }
                        });
                        
                        final TreeItem projectBasedSources = new TreeItem(projectBasedPathsTree, SWT.NONE);
                        projectBasedSources.setText("Sources");

                        final TreeItem projectBasedResources = new TreeItem(projectBasedPathsTree, SWT.NONE);
                        projectBasedResources.setText("Resources");

                        final TreeItem configBasedSources = new TreeItem(configBasedPathsTree, SWT.NONE);
                        configBasedSources.setText("Sources");

                        final TreeItem configBasedResources = new TreeItem(configBasedPathsTree, SWT.NONE);
                        configBasedResources.setText("Resources");

                        resultSources = new TreeItem(resultPathsTree, SWT.NONE);
                        resultSources.setText("Sources");

                        resultResources = new TreeItem(resultPathsTree, SWT.NONE);
                        resultResources.setText("Resources");

                        TreeItem pathItem = null;
                        for (String folder : sourceFoldersFromEclipseProject) {
                            pathItem = new TreeItem(projectBasedSources, SWT.NONE);
                            pathItem.setText(folder);
                            if (! sourceFoldersFromCeylonConfig.contains(folder)) {
                                pathItem.setForeground(red);
                                pathItem.setData(CONFLICTING, true);
                            } else {
                                resultSourcesSet.add(folder);
                                pathItem.setData(CONFLICTING, false);
                            }
                            pathItem.setData(RESULT_FOLDER_SET, resultSourcesSet);
                        }

                        for (String folder : resourceFoldersFromEclipseProject) {
                            pathItem = new TreeItem(projectBasedResources, SWT.NONE);
                            pathItem.setText(folder);
                            if (! resourceFoldersFromCeylonConfig.contains(folder)) {
                                pathItem.setForeground(red);
                                pathItem.setData(CONFLICTING, true);
                            } else {
                                resultResourcesSet.add(folder);
                                pathItem.setData(CONFLICTING, false);
                            }
                            pathItem.setData(RESULT_FOLDER_SET, resultResourcesSet);
                        }

                        for (String folder : sourceFoldersFromCeylonConfig) {
                            pathItem = new TreeItem(configBasedSources, SWT.NONE);
                            pathItem.setText(folder);
                            if (! sourceFoldersFromEclipseProject.contains(folder)) {
                                pathItem.setForeground(red);
                                pathItem.setData(CONFLICTING, true);
                            } else {
                                resultSourcesSet.add(folder);
                                pathItem.setData(CONFLICTING, false);
                            }
                            pathItem.setData(RESULT_FOLDER_SET, resultSourcesSet);
                        }

                        for (String folder : resourceFoldersFromCeylonConfig) {
                            pathItem = new TreeItem(configBasedResources, SWT.NONE);
                            pathItem.setText(folder);
                            if (! resourceFoldersFromEclipseProject.contains(folder)) {
                                pathItem.setForeground(red);
                                pathItem.setData(CONFLICTING, true);
                            } else {
                                resultResourcesSet.add(folder);
                                pathItem.setData(CONFLICTING, false);
                            }
                            pathItem.setData(RESULT_FOLDER_SET, resultResourcesSet);
                        }

                        updateResultTree();
                        
                        projectBasedSources.setExpanded(true);
                        projectBasedResources.setExpanded(true);
                        configBasedSources.setExpanded(true);
                        configBasedResources.setExpanded(true);

                        setupMenu(projectBasedPathsTree);
                        setupMenu(configBasedPathsTree);
                        
                        return composite;
                    }

                    private void setupMenu(final Tree projectBasedPathsTree) {
                        final Menu projectBasedMenu = new Menu(projectBasedPathsTree);
                        projectBasedPathsTree.setMenu(projectBasedMenu);
                        projectBasedMenu.addMenuListener(new MenuAdapter()
                        {
                            public void menuShown(MenuEvent e)
                            {
                                MenuItem[] items = projectBasedMenu.getItems();
                                for (int i = 0; i < items.length; i++)
                                {
                                    items[i].dispose();
                                }
                                TreeItem[] selection = projectBasedPathsTree.getSelection();
                                if (selection.length > 0) {
                                    final TreeItem selectedItem = selection[0];
                                    if ((Boolean)selectedItem.getData(CONFLICTING)) {
                                        MenuItem newItem = new MenuItem(projectBasedMenu, SWT.NONE);
                                        @SuppressWarnings("unchecked")
                                        final Set<String> resultFolderSet = (Set<String>)selectedItem.getData(RESULT_FOLDER_SET);
                                        final boolean alreadyInResult = resultFolderSet.contains(selectedItem.getText());
                                        newItem.setText((alreadyInResult ? "Remove from" : "Add to") 
                                                + " merged build paths");
                                        newItem.addSelectionListener(new SelectionAdapter() {
                                            @Override
                                            public void widgetSelected(
                                                    SelectionEvent e) {
                                                if (alreadyInResult) {
                                                    resultFolderSet.remove(selectedItem.getText());
                                                } else {
                                                    resultFolderSet.add(selectedItem.getText());
                                                }
                                                updateResultTree();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }

                    private void updateResultTree() {
                        TreeItem pathItem;
                        resultSources.removeAll();
                        for (String folder : resultSourcesSet) {
                            pathItem = new TreeItem(resultSources, SWT.NONE);
                            pathItem.setText(folder);
                        }
                        resultResources.removeAll();
                        for (String folder : resultResourcesSet) {
                            pathItem = new TreeItem(resultResources, SWT.NONE);
                            pathItem.setText(folder);
                        }
                        resultSources.setExpanded(true);
                        resultResources.setExpanded(true);
                    }
                }
                BuildPathsComparisonDialog dialog = new BuildPathsComparisonDialog();
                if (dialog.open() == Dialog.OK &&
                        ! (dialog.resultSourcesSet.equals(sourceFoldersFromEclipseProject) 
                                && dialog.resultResourcesSet.equals(resourceFoldersFromEclipseProject))) {
                    updateClassPathsFromConfigFile(Arrays.asList(dialog.resultSourcesSet.toArray(new String[0])), new HashSet<String>());
                    if (fSourceContainerPage != null) {
                        fSourceContainerPage.init(fCurrJProject);
                    }
                    updateUI();
                }
            }
        }
    }

    public static interface IRemoveOldBinariesQuery {

        /**
         * Do the callback. Returns <code>true</code> if .class files should be removed from the
         * old output location.
         * @param removeLocation true if the folder at oldOutputLocation should be removed, false if only its content
         * @param oldOutputLocation The old output location
         * @return Returns true if .class files should be removed.
         * @throws OperationCanceledException if the operation was canceled
         */
        boolean doQuery(boolean removeLocation, IPath oldOutputLocation) 
                throws OperationCanceledException;

    }


    private IWorkspaceRoot fWorkspaceRoot;

    private CheckedListDialogField<CPListElement> fClassPathList;
    private CheckedListDialogField<CPListElement> fResourcePathList;
    private StringButtonDialogField fJavaBuildPathDialogField;
    private Link fNotInSyncText;
    private boolean wasInSyncWhenOpening;
    private boolean someFoldersNeedToBeCreated;

    private StatusInfo fClassPathStatus;
    private StatusInfo fOutputFolderStatus;
    private StatusInfo fBuildPathStatus;

    private IJavaProject fCurrJProject;

    private IPath fJavaOutputLocationPath;

    private IStatusChangeListener fContext;
    private Control fSWTWidget;
    private TabFolder fTabFolder;

    private int fPageIndex;

    private SourceContainerWorkbookPage fSourceContainerPage;
    private ResourceContainerWorkbookPage fResourceContainerPage;
    private ProjectsWorkbookPage fProjectsPage;
//    private LibrariesWorkbookPage fLibrariesPage;

    private BuildPathBasePage fCurrPage;

    private String fUserSettingsTimeStamp;
    private long fFileTimeStamp;

    //private IRunnableContext fRunnableContext;
    //private boolean fUseNewPage;

    private final IWorkbenchPreferenceContainer fPageContainer; // null when invoked from a non-property page context

    private final static int IDX_UP= 0;
    private final static int IDX_DOWN= 1;
    private final static int IDX_TOP= 3;
    private final static int IDX_BOTTOM= 4;
    private final static int IDX_SELECT_ALL= 6;
    private final static int IDX_UNSELECT_ALL= 7;

    public CeylonBuildPathsBlock(/*IRunnableContext runnableContext,*/ 
            IStatusChangeListener context, int pageToShow, /*boolean useNewPage,*/ 
            IWorkbenchPreferenceContainer pageContainer) {
        fPageContainer= pageContainer;
        fWorkspaceRoot= JavaPlugin.getWorkspace().getRoot();
        fContext= context;
        //fUseNewPage= useNewPage;

        fPageIndex= pageToShow;

        fSourceContainerPage= null;
        fNotInSyncText = null;
        fResourceContainerPage=null;
//        fLibrariesPage= null;
        fProjectsPage= null;
        fCurrPage= null;
        //fRunnableContext= runnableContext;

        JavaBuildPathAdapter jadapter= new JavaBuildPathAdapter();

        String[] buttonLabels= new String[] {
            /* IDX_UP */ NewWizardMessages.BuildPathsBlock_classpath_up_button,
            /* IDX_DOWN */ NewWizardMessages.BuildPathsBlock_classpath_down_button,
            /* 2 */ null,
            /* IDX_TOP */ NewWizardMessages.BuildPathsBlock_classpath_top_button,
            /* IDX_BOTTOM */ NewWizardMessages.BuildPathsBlock_classpath_bottom_button,
            /* 5 */ null,
            /* IDX_SELECT_ALL */ NewWizardMessages.BuildPathsBlock_classpath_checkall_button,
            /* IDX_UNSELECT_ALL */ NewWizardMessages.BuildPathsBlock_classpath_uncheckall_button

        };

        fClassPathList= new CheckedListDialogField<CPListElement>(jadapter, buttonLabels, 
                new CPListLabelProvider());
        fClassPathList.setDialogFieldListener(jadapter);
        fClassPathList.setLabelText(NewWizardMessages.BuildPathsBlock_classpath_label);
        fClassPathList.setUpButtonIndex(IDX_UP);
        fClassPathList.setDownButtonIndex(IDX_DOWN);
        fClassPathList.setCheckAllButtonIndex(IDX_SELECT_ALL);
        fClassPathList.setUncheckAllButtonIndex(IDX_UNSELECT_ALL);
        fClassPathList.setDialogFieldListener(new IDialogFieldListener() {
            
            @Override
            public void dialogFieldChanged(DialogField field) {
                System.out.print("");
            }
        });

        fResourcePathList= new CheckedListDialogField<CPListElement>(jadapter, buttonLabels, 
                new ResourceListLabelProvider());
        fResourcePathList.setDialogFieldListener(jadapter);
        fResourcePathList.setLabelText("Build &resource path order");
        fResourcePathList.setUpButtonIndex(IDX_UP);
        fResourcePathList.setDownButtonIndex(IDX_DOWN);
        fResourcePathList.setCheckAllButtonIndex(IDX_SELECT_ALL);
        fResourcePathList.setUncheckAllButtonIndex(IDX_UNSELECT_ALL);

        fJavaBuildPathDialogField= new StringButtonDialogField(jadapter);
        fJavaBuildPathDialogField.setButtonLabel(NewWizardMessages.BuildPathsBlock_buildpath_button);
        fJavaBuildPathDialogField.setDialogFieldListener(jadapter);
        //fJavaPathDialogField.setLabelText(NewWizardMessages.BuildPathsBlock_buildpath_label);
        fJavaBuildPathDialogField.setLabelText("Default Java binary class output folder:");

        fBuildPathStatus= new StatusInfo();
        fClassPathStatus= new StatusInfo();
        fOutputFolderStatus= new StatusInfo();

        fCurrJProject= null;
    }

    public boolean wasInSyncWithCeylonConfigWhenOpening() {
        return wasInSyncWhenOpening;
    }
    
    public boolean isInSyncWithCeylonConfig() {
        if (fCurrJProject == null) {
            return true;
        }
        IProject project = fCurrJProject.getProject();
        
        Set<String> sourceFoldersFromCeylonConfig = new TreeSet<String>();
        Set<String> sourceFoldersFromEclipseProject = new TreeSet<String>();
        Set<String> resourceFoldersFromCeylonConfig = new TreeSet<String>();
        Set<String> resourceFoldersFromEclipseProject = new TreeSet<String>();
        fillBuildPathsSetsForComparison(project, sourceFoldersFromCeylonConfig,
                sourceFoldersFromEclipseProject,
                resourceFoldersFromCeylonConfig,
                resourceFoldersFromEclipseProject);
        return sourceFoldersFromCeylonConfig.equals(sourceFoldersFromEclipseProject) &&
                resourceFoldersFromCeylonConfig.equals(resourceFoldersFromEclipseProject);
    }

    private void fillBuildPathsSetsForComparison(IProject project,
            Set<String> sourceFoldersFromCeylonConfig,
            Set<String> sourceFoldersFromEclipseProject,
            Set<String> resourceFoldersFromCeylonConfig,
            Set<String> resourceFoldersFromEclipseProject) {
        CeylonProjectConfig ceylonConfig = modelJ2C().ceylonModel().getProject(project).getConfiguration();
        for (String path : toJavaStringList(ceylonConfig.getProjectSourceDirectories())) {
            sourceFoldersFromCeylonConfig.add(Path.fromOSString(path).toString());
        }
        for (String path : toJavaStringList(ceylonConfig.getProjectResourceDirectories())) {
            resourceFoldersFromCeylonConfig.add(Path.fromOSString(path).toString());
        }
        
        for (CPListElement elem : fClassPathList.getElements()) {
            if (elem.getClasspathEntry().getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                IPath path = null;
                if (elem.getLinkTarget() == null) {
                    path = elem.getPath().makeRelativeTo(project.getFullPath());
                } else {
                    path = elem.getLinkTarget();
                }
                sourceFoldersFromEclipseProject.add(path.toString());
            }
        }
        for (CPListElement elem : fResourcePathList.getElements()) {
            if (elem.getClasspathEntry().getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                IPath path = null;
                if (elem.getLinkTarget() == null) {
                    path = elem.getPath().makeRelativeTo(project.getFullPath());
                } else {
                    path = elem.getLinkTarget();
                }
                resourceFoldersFromEclipseProject.add(path.toString());
            }
        }
        if (sourceFoldersFromEclipseProject.isEmpty()) {
            sourceFoldersFromEclipseProject.add(Constants.DEFAULT_SOURCE_DIR);
        }
        if (resourceFoldersFromEclipseProject.isEmpty()) {
            resourceFoldersFromEclipseProject.add(Constants.DEFAULT_RESOURCE_DIR);
        }
    }

    // -------- UI creation ---------

    public Control createControl(Composite parent) {
        fSWTWidget= parent;

        Composite composite= new Composite(parent, SWT.NONE);
        composite.setFont(parent.getFont());

        GridLayout layout= new GridLayout();
        layout.marginWidth= 0;
        layout.marginHeight= 0;
        layout.numColumns= 1;
        composite.setLayout(layout);

        fNotInSyncText = new Link(composite, SWT.NONE);
        wasInSyncWhenOpening = isInSyncWithCeylonConfig();
        fNotInSyncText.setVisible(! wasInSyncWhenOpening);
        GridData notInSyncLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        notInSyncLayoutData.exclude = wasInSyncWhenOpening;
        fNotInSyncText.setLayoutData(notInSyncLayoutData);
        fNotInSyncText.setText("The Ceylon configuration file (<a>.ceylon/config</a>) is not in sync with the current\n"
                                 + "Ceylon Build Paths. You can :\n"
                                 + "  - simply overwrite the Ceylon configuration with OK\n"
                                 + "  - <a>use</a> the original configuration file settings\n"
                                 + "  - <a>resolve</a> the conflicts yourself.");
        fNotInSyncText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED));
        fNotInSyncText.addSelectionListener(new CeylonConfigNotInSyncListener());
        
        TabFolder folder= new TabFolder(composite, SWT.NONE);
        folder.setLayoutData(new GridData(GridData.FILL_BOTH));
        folder.setFont(composite.getFont());

        TabItem item;
        item= new TabItem(folder, SWT.NONE);
        item.setText(NewWizardMessages.BuildPathsBlock_tab_source);
        item.setImage(JavaPluginImages.get(JavaPluginImages.IMG_OBJS_PACKFRAG_ROOT));

        /*if (fUseNewPage) {
            fSourceContainerPage= new NewSourceContainerWorkbookPage(fClassPathList, fBuildPathDialogField, fRunnableContext, this);
        } else {*/
            fSourceContainerPage= new SourceContainerWorkbookPage(fClassPathList, fJavaBuildPathDialogField);
        //}
        item.setData(fSourceContainerPage);
        item.setControl(fSourceContainerPage.getControl(folder));

        item= new TabItem(folder, SWT.NONE);
        item.setText("Resources");
        item.setImage(CeylonResources.FOLDER);
        fResourceContainerPage = new ResourceContainerWorkbookPage(fResourcePathList, fJavaBuildPathDialogField);
        item.setData(fResourceContainerPage);
        item.setControl(fResourceContainerPage.getControl(folder));

        IWorkbench workbench= JavaPlugin.getDefault().getWorkbench();
        Image projectImage= workbench.getSharedImages().getImage(IDE.SharedImages.IMG_OBJ_PROJECT);

        fProjectsPage= new ProjectsWorkbookPage(fClassPathList, fPageContainer);
        item= new TabItem(folder, SWT.NONE);
        item.setText(NewWizardMessages.BuildPathsBlock_tab_projects);
        item.setImage(projectImage);
        item.setData(fProjectsPage);
        item.setControl(fProjectsPage.getControl(folder));
        
//        fLibrariesPage= new LibrariesWorkbookPage(fClassPathList, fPageContainer);
//        item= new TabItem(folder, SWT.NONE);
//        item.setText(NewWizardMessages.BuildPathsBlock_tab_libraries);
//        item.setImage(JavaPluginImages.get(JavaPluginImages.IMG_OBJS_LIBRARY));
//        item.setData(fLibrariesPage);
//        item.setControl(fLibrariesPage.getControl(folder));

        // a non shared image
        Image cpoImage= JavaPluginImages.DESC_TOOL_CLASSPATH_ORDER.createImage();
        composite.addDisposeListener(new ImageDisposer(cpoImage));

//        ClasspathOrderingWorkbookPage ordpage= new ClasspathOrderingWorkbookPage(fClassPathList);
//        item= new TabItem(folder, SWT.NONE);
//        item.setText(NewWizardMessages.BuildPathsBlock_tab_order);
//        item.setImage(cpoImage);
//        item.setData(ordpage);
//        item.setControl(ordpage.getControl(folder));

        if (fCurrJProject != null) {
            fSourceContainerPage.init(fCurrJProject);
            fResourceContainerPage.init(fCurrJProject);
//            fLibrariesPage.init(fCurrJProject);
            fProjectsPage.init(fCurrJProject);
        }

        folder.setSelection(fPageIndex);
        fCurrPage= (BuildPathBasePage) folder.getItem(fPageIndex).getData();
        folder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tabChanged(e.item);
            }
        });
        fTabFolder= folder;

        Dialog.applyDialogFont(composite);
        return composite;
    }

    private Shell getShell() {
        if (fSWTWidget != null) {
            return fSWTWidget.getShell();
        }
        return JavaPlugin.getActiveWorkbenchShell();
    }

    /**
     * Initializes the classpath for the given project. Multiple calls to init are allowed,
     * but all existing settings will be cleared and replace by the given or default paths.
     * @param jproject The java project to configure. Does not have to exist.
     * @param javaOutputLocation The output location to be set in the page. If <code>null</code>
     * is passed, jdt default settings are used, or - if the project is an existing Java project- the
     * output location of the existing project
     * @param classpathEntries The classpath entries to be set in the page. If <code>null</code>
     * is passed, jdt default settings are used, or - if the project is an existing Java project - the
     * classpath entries of the existing project
     */
    public void init(IJavaProject jproject, IPath javaOutputLocation, 
            IClasspathEntry[] classpathEntries, boolean javaCompilationEnabled) {
        fCurrJProject= jproject;
        boolean projectExists= false;
        someFoldersNeedToBeCreated = false;
        final List<CPListElement> newClassPath;
        IProject project= fCurrJProject.getProject();
        projectExists= (project.exists() && project.getFile(".classpath").exists()); //$NON-NLS-1$

        List<String> configSourceDirectories;
        List<String> configResourceDirectories;
        if (projectExists) {
            CeylonProjectConfig config;
            CeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
            if (ceylonProject != null) {
                config = ceylonProject.getConfiguration();
            } else {
                config = new CeylonProjectConfig(ceylonProject);
            }
            configSourceDirectories = toJavaStringList(config.getProjectSourceDirectories());
            configResourceDirectories = toJavaStringList(config.getProjectResourceDirectories());
        } else {
            File configFile = jproject.getProject().getFile(".ceylon/config").getLocation().toFile();
            CeylonConfig ceylonConfig;
            try {
                ceylonConfig = CeylonConfigFinder.DEFAULT.loadConfigFromFile(configFile);
                configSourceDirectories = toJavaStringList(sourceDirectoriesFromCeylonConfig_.sourceDirectoriesFromCeylonConfig(ceylonConfig));
                configResourceDirectories = toJavaStringList(resourceDirectoriesFromCeylonConfig_.resourceDirectoriesFromCeylonConfig(ceylonConfig));
            } catch (IOException e) {
                configSourceDirectories = Collections.emptyList();
                configResourceDirectories = Collections.emptyList();
            }
        }
        
        IClasspathEntry[] existingEntries= null;
        if  (projectExists) {
            if (javaOutputLocation == null) {
                javaOutputLocation=  fCurrJProject.readOutputLocation();
            }
            existingEntries= fCurrJProject.readRawClasspath();
            if (classpathEntries == null) {
                classpathEntries= existingEntries;
                //TODO: read existing ceylon output location from classpathEntries
            }
        }
        if (javaOutputLocation == null) {
            javaOutputLocation= getDefaultJavaOutputLocation(jproject);
        }

        if (classpathEntries != null) {
            newClassPath= getCPListElements(classpathEntries, existingEntries);
        }
        else {
            newClassPath= getDefaultClassPath(jproject);
        }
        
        Set<String> newFolderNames = new HashSet<>();
        final List<CPListElement> newResourcePath = new ArrayList<CPListElement>();

        if (!configResourceDirectories.isEmpty()) {
            someFoldersNeedToBeCreated = resourcePathsFromStrings(fCurrJProject, configResourceDirectories,
					newFolderNames, newResourcePath);
        }
        else {
            String defaultResourceFolderName = CeylonPlugin.getPreferences().getString(DEFAULT_RESOURCE_FOLDER);
            IFolder defaultResourceFolder = fCurrJProject.getProject().getFolder(defaultResourceFolderName);
            newResourcePath.add(new CPListElement(fCurrJProject, 
                    IClasspathEntry.CPE_SOURCE, 
                    defaultResourceFolder.getFullPath(), 
                    defaultResourceFolder));
        }
        
        List<CPListElement> exportedEntries = new ArrayList<CPListElement>();
        for (int i= 0; i < newClassPath.size(); i++) {
            CPListElement curr= newClassPath.get(i);
            if (curr.isExported() || curr.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                exportedEntries.add(curr);
            }
        }

        fJavaOutputLocationPath = javaOutputLocation.makeRelative();
        
        // inits the dialog field
        fJavaBuildPathDialogField.setText(fJavaOutputLocationPath.toString());
        fJavaBuildPathDialogField.enableButton(project.exists());
        fClassPathList.setElements(newClassPath);
        fClassPathList.setCheckedElements(exportedEntries);

        if (! projectExists) {
            IPreferenceStore store= PreferenceConstants.getPreferenceStore();
            if (!configSourceDirectories.isEmpty() && store.getBoolean(PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ)) {
                updateClassPathsFromConfigFile(configSourceDirectories, newFolderNames);
            }
        }
        fClassPathList.selectFirstElement();

        fResourcePathList.setElements(newResourcePath);
//        fResourcePathList.setCheckedElements(exportedEntries);

        fResourcePathList.selectFirstElement();

        if (fSourceContainerPage != null) {
            fSourceContainerPage.init(fCurrJProject);
            fResourceContainerPage.init(fCurrJProject);
            fProjectsPage.init(fCurrJProject);
//            fLibrariesPage.init(fCurrJProject);
        }

        initializeTimeStamps();
        updateUI();
    }

	public static boolean resourcePathsFromStrings(final IJavaProject javaProject,
			List<String> configResourceDirectories, Set<String> newFolderNames,
			final List<CPListElement> newResourcePath) {
		boolean someFoldersNeedToBeCreated = false;
		IProject project = javaProject.getProject();
		for (final String path: configResourceDirectories) {
		    final IPath iPath = Path.fromOSString(path);
		    if (! iPath.isAbsolute()) {
		        IFolder folder = project.getFolder(iPath);
		        newResourcePath.add(new CPListElement(javaProject, 
		                IClasspathEntry.CPE_SOURCE, 
		                folder.getFullPath(), 
		                folder));
		        if (!folder.exists()) {
		            someFoldersNeedToBeCreated = true;
		        }
		    }
		    else {
		        try {   
		            class CPListElementHolder {
		                public CPListElement value = null;
		            }
		            final CPListElementHolder cpListElement = new CPListElementHolder();
		            project.accept(new IResourceVisitor() {
		                @Override
		                public boolean visit(IResource resource) 
		                        throws CoreException {
		                    if (resource instanceof IFolder &&
		                            resource.isLinked() && 
		                            resource.getLocation() != null &&
		                            resource.getLocation().equals(iPath)) {
		                        cpListElement.value = new CPListElement(null,
		                                javaProject, IClasspathEntry.CPE_SOURCE, 
		                                resource.getFullPath(), 
		                                resource, resource.getLocation());
		                        return false;
		                    }
		                    return resource instanceof IFolder || 
		                            resource instanceof IProject;
		                }
		            });
		            if (cpListElement.value == null) {
		                String newFolderName = iPath.lastSegment();
		                IFolder newFolder = project.getFolder(newFolderName);
		                int counter = 1;
		                while (newFolderNames.contains(newFolderName) || newFolder.exists()) {
		                    newFolderName = iPath.lastSegment() + "_" + counter++;
		                    newFolder = project.getFolder(newFolderName);
		                }
		                newFolderNames.add(newFolderName);
		                cpListElement.value = new CPListElement(null,
		                        javaProject, IClasspathEntry.CPE_SOURCE, 
		                        newFolder.getFullPath(), 
		                        newFolder, iPath);
		                someFoldersNeedToBeCreated = true;
		            }
		            newResourcePath.add(cpListElement.value);
		        }
		        catch (CoreException e) {
		            e.printStackTrace();
		        }
		    }
		}
		return someFoldersNeedToBeCreated;
	}

    protected void updateUI() {
        if (fSWTWidget == null || fSWTWidget.isDisposed()) {
            return;
        }

        if (Display.getCurrent() != null) {
            doUpdateUI();
        } else {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    if (fSWTWidget == null || fSWTWidget.isDisposed()) {
                        return;
                    }
                    doUpdateUI();
                }
            });
        }
    }

    protected void doUpdateUI() {
        boolean isInSync = isInSyncWithCeylonConfig();
        boolean notInSyncWasVisible = fNotInSyncText.isVisible();
        fNotInSyncText.setVisible(! isInSync);
        GridData notInSyncLayoutData = (GridData) fNotInSyncText.getLayoutData();
        notInSyncLayoutData.exclude = isInSync;
        fJavaBuildPathDialogField.refresh();
        fClassPathList.refresh();
        fResourcePathList.refresh();
        
        doStatusLineUpdate();
        if (notInSyncWasVisible && isInSync) {
            fNotInSyncText.getParent().layout(true);
        }
    }
    
    private String getEncodedSettings() {
        StringBuffer buf= new StringBuffer();
        CPListElement.appendEncodePath(fJavaOutputLocationPath, buf).append(';');

        int nElements= fClassPathList.getSize();
        buf.append('[').append(nElements).append(']');
        for (int i= 0; i < nElements; i++) {
            CPListElement elem= fClassPathList.getElement(i);
            elem.appendEncodedSettings(buf);
        }
        nElements= fResourcePathList.getSize();
        buf.append('[').append(nElements).append(']');
        for (int i= 0; i < nElements; i++) {
            CPListElement elem= fResourcePathList.getElement(i);
            elem.appendEncodedSettings(buf);
        }
        return buf.toString();
    }

    public boolean hasChangesInDialog() {
        String currSettings= getEncodedSettings();
        return !currSettings.equals(fUserSettingsTimeStamp) || someFoldersNeedToBeCreated;
    }

    public boolean hasChangesInClasspathFile() {
        IFile file= fCurrJProject.getProject().getFile(".classpath"); //$NON-NLS-1$
        return fFileTimeStamp != file.getModificationStamp();
    }

    public boolean isClassfileMissing() {
        return !fCurrJProject.getProject().getFile(".classpath").exists(); //$NON-NLS-1$
    }

    public void initializeTimeStamps() {
        IFile file= fCurrJProject.getProject().getFile(".classpath"); //$NON-NLS-1$
        fFileTimeStamp= file.getModificationStamp();
        fUserSettingsTimeStamp= getEncodedSettings();
    }

    private ArrayList<CPListElement> getCPListElements(IClasspathEntry[] classpathEntries, 
            IClasspathEntry[] existingEntries) {
        List<IClasspathEntry> existing= existingEntries == null ? 
                Collections.<IClasspathEntry>emptyList() : Arrays.asList(existingEntries);
        ArrayList<CPListElement> newClassPath= new ArrayList<CPListElement>();
        for (int i= 0; i < classpathEntries.length; i++) {
            IClasspathEntry curr= classpathEntries[i];
            newClassPath.add(CPListElement.create(curr, ! existing.contains(curr), fCurrJProject));
        }
        return newClassPath;
    }

    // -------- public api --------

    /**
     * @return Returns the Java project. Can return <code>null<code> if the page has not
     * been initialized.
     */
    public IJavaProject getJavaProject() {
        return fCurrJProject;
    }

    /**
     *  @return Returns the current output location. Note that the path returned must not be valid.
     */
    public IPath getJavaOutputLocation() {
        String text = fJavaBuildPathDialogField.getText();
        if (text.isEmpty()) {
        	return null;
        }
        else {
        	return new Path(text).makeAbsolute();
        }
    }

    /**
     *  @return Returns the current class path (raw). Note that the entries returned must not be valid.
     */
    public IClasspathEntry[] getRawClassPath() {
        List<CPListElement> elements=  fClassPathList.getElements();
        int nElements= elements.size();
        IClasspathEntry[] entries= new IClasspathEntry[elements.size()];

        for (int i= 0; i < nElements; i++) {
            CPListElement currElement= elements.get(i);
            entries[i]= currElement.getClasspathEntry();
        }
        return entries;
    }

    public int getPageIndex() {
        return fPageIndex;
    }


    // -------- evaluate default settings --------

    private List<CPListElement> getDefaultClassPath(IJavaProject jproj) {
        List<CPListElement> list= new ArrayList<CPListElement>();
        IResource srcFolder;
        IPreferenceStore store= PreferenceConstants.getPreferenceStore();
        String sourceFolderName= CeylonPlugin.getPreferences().getString(DEFAULT_SOURCE_FOLDER);
        if (store.getBoolean(PreferenceConstants.SRCBIN_FOLDERS_IN_NEWPROJ) && sourceFolderName.length() > 0) {
            srcFolder= jproj.getProject().getFolder(sourceFolderName);
        } else {
            srcFolder= jproj.getProject();
        }

        list.add(new CPListElement(jproj, IClasspathEntry.CPE_SOURCE, srcFolder.getFullPath(), srcFolder));

        IClasspathEntry[] jreEntries= PreferenceConstants.getDefaultJRELibrary();
        list.addAll(getCPListElements(jreEntries, null));
        return list;
    }

    public static IPath getDefaultJavaOutputLocation(IJavaProject jproj) {
        return jproj.getProject().getFullPath().append("classes");
    }

    private class JavaBuildPathAdapter implements IStringButtonAdapter, 
            IDialogFieldListener, IListAdapter<CPListElement> {

        // -------- IStringButtonAdapter --------
        public void changeControlPressed(DialogField field) {
            buildPathChangeControlPressed(field);
        }

        // ---------- IDialogFieldListener --------
        public void dialogFieldChanged(DialogField field) {
            buildPathDialogFieldChanged(field);
        }

        // ---------- IListAdapter --------
        public void customButtonPressed(ListDialogField<CPListElement> field, int index) {
            buildPathCustomButtonPressed(field, index);
        }

        public void doubleClicked(ListDialogField<CPListElement> field) {
        }

        public void selectionChanged(ListDialogField<CPListElement> field) {
            updateTopButtonEnablement();
        }
    }

    private void buildPathChangeControlPressed(DialogField field) {
        if (field == fJavaBuildPathDialogField) {
            IContainer container= chooseContainer(fJavaOutputLocationPath);
            if (container != null) {
                fJavaBuildPathDialogField.setText(container.getFullPath().makeRelative().toString());
            }
        }
    }

    public void updateTopButtonEnablement() {
        fClassPathList.enableButton(IDX_BOTTOM, fClassPathList.canMoveDown());
        fClassPathList.enableButton(IDX_TOP, fClassPathList.canMoveUp());
    }

    public void buildPathCustomButtonPressed(ListDialogField<CPListElement> field, int index) {
        List<CPListElement> elems= field.getSelectedElements();
        field.removeElements(elems);
        if (index == IDX_BOTTOM) {
            field.addElements(elems);
        } else if (index == IDX_TOP) {
            field.addElements(elems, 0);
        }
    }

    private void buildPathDialogFieldChanged(DialogField field) {
        if (field == fClassPathList) {
            updateClassPathStatus();
            updateTopButtonEnablement();
        } 
        else if (field == fJavaBuildPathDialogField) {
            updateJavaOutputLocationStatus();
        }
        doStatusLineUpdate();
    }



    // -------- verification -------------------------------

    private void doStatusLineUpdate() {
        if (Display.getCurrent() != null) {
            IStatus res= findMostSevereStatus();
            fContext.statusChanged(res);
        }
    }

    private IStatus findMostSevereStatus() {
        return StatusUtil.getMostSevere(new IStatus[] { fClassPathStatus, fOutputFolderStatus, fBuildPathStatus });
    }


    /**
     * Validates the build path.
     */
    public void updateClassPathStatus() {
        fClassPathStatus.setOK();

        List<CPListElement> elements= fClassPathList.getElements();

        CPListElement entryMissing= null;
        CPListElement entryDeprecated= null;
        int nEntriesMissing= 0;
        IClasspathEntry[] entries= new IClasspathEntry[elements.size()];

        for (int i= elements.size()-1 ; i >= 0 ; i--) {
            CPListElement currElement= elements.get(i);
            boolean isChecked= fClassPathList.isChecked(currElement);
            if (currElement.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                if (!isChecked) {
                    fClassPathList.setCheckedWithoutUpdate(currElement, true);
                }
                if (!fClassPathList.isGrayed(currElement)) {
                    fClassPathList.setGrayedWithoutUpdate(currElement, true);
                }
            } else {
                currElement.setExported(isChecked);
            }

            entries[i]= currElement.getClasspathEntry();
            if (currElement.isMissing()) {
                nEntriesMissing++;
                if (entryMissing == null) {
                    entryMissing= currElement;
                }
            }
            if (entryDeprecated == null & currElement.isDeprecated()) {
                entryDeprecated= currElement;
            }
        }

        if (nEntriesMissing > 0) {
            if (nEntriesMissing == 1) {
                fClassPathStatus.setWarning(Messages.format(NewWizardMessages.BuildPathsBlock_warning_EntryMissing, 
                        BasicElementLabels.getPathLabel(entryMissing.getPath(), false)));
            } else {
                fClassPathStatus.setWarning(Messages.format(NewWizardMessages.BuildPathsBlock_warning_EntriesMissing, 
                        String.valueOf(nEntriesMissing)));
            }
        } else if (entryDeprecated != null) {
            fClassPathStatus.setInfo(entryDeprecated.getDeprecationMessage());
        }

/*        if (fCurrJProject.hasClasspathCycle(entries)) {
            fClassPathStatus.setWarning(NewWizardMessages.getString("BuildPathsBlock.warning.CycleInClassPath")); //$NON-NLS-1$
        }
*/
        updateBuildPathStatus();
    }

    /**
     * Validates output location & build path.
     */
    private void updateJavaOutputLocationStatus() {
        fJavaOutputLocationPath= null;

        String text= fJavaBuildPathDialogField.getText();
        if ("".equals(text)) { //$NON-NLS-1$
            fOutputFolderStatus.setError(NewWizardMessages.BuildPathsBlock_error_EnterBuildPath);
            return;
        }
        IPath path= getJavaOutputLocation();
        fJavaOutputLocationPath= path;

        IResource res= fWorkspaceRoot.findMember(path);
        if (res != null) {
            // if exists, must be a folder or project
            if (res.getType() == IResource.FILE) {
                fOutputFolderStatus.setError(NewWizardMessages.BuildPathsBlock_error_InvalidBuildPath);
                return;
            }
        }

        fOutputFolderStatus.setOK();

        String pathStr= fJavaBuildPathDialogField.getText();
        Path outputPath= (new Path(pathStr));
        pathStr= outputPath.lastSegment();
        if (pathStr.equals(".settings") && outputPath.segmentCount() == 2) { //$NON-NLS-1$
            fOutputFolderStatus.setWarning(NewWizardMessages.OutputLocation_SettingsAsLocation);
        }

        if (pathStr.charAt(0) == '.' && pathStr.length() > 1) {
            fOutputFolderStatus.setWarning(Messages.format(NewWizardMessages.OutputLocation_DotAsLocation, 
                    BasicElementLabels.getResourceName(pathStr)));
        }

        updateBuildPathStatus();
    }

    private void updateBuildPathStatus() {
        List<CPListElement> elements= fClassPathList.getElements();
        IClasspathEntry[] entries= new IClasspathEntry[elements.size()];

        for (int i= elements.size()-1 ; i >= 0 ; i--) {
            CPListElement currElement= elements.get(i);
            entries[i]= currElement.getClasspathEntry();
        }

        IJavaModelStatus status= JavaConventions.validateClasspath(fCurrJProject, entries, 
                fJavaOutputLocationPath);
        if (!status.isOK()) {
            fBuildPathStatus.setError(status.getMessage());
            return;
        }
        fBuildPathStatus.setOK();
    }

    // -------- creation -------------------------------

    public static void createProject(IProject project, URI locationURI, IProgressMonitor monitor) 
            throws CoreException {
        if (monitor == null) {
            monitor= new NullProgressMonitor();
        }
        monitor.beginTask(NewWizardMessages.BuildPathsBlock_operationdesc_project, 10);

        // create the project
        try {
            if (!project.exists()) {
                IProjectDescription desc= project.getWorkspace()
                        .newProjectDescription(project.getName());
                if (locationURI != null && ResourcesPlugin.getWorkspace()
                        .getRoot().getLocationURI().equals(locationURI)) {
                    locationURI= null;
                }
                desc.setLocationURI(locationURI);
                project.create(desc, monitor);
                monitor= null;
            }
            if (!project.isOpen()) {
                project.open(monitor);
                monitor= null;
            }
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }

    public static void addJavaNature(IProject project, IProgressMonitor monitor) 
            throws CoreException {
        if (monitor != null && monitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        if (!project.hasNature(JavaCore.NATURE_ID)) {
            IProjectDescription description = project.getDescription();
            String[] prevNatures= description.getNatureIds();
            String[] newNatures= new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length]= JavaCore.NATURE_ID;
            description.setNatureIds(newNatures);
            project.setDescription(description, monitor);
        } else {
            if (monitor != null) {
                monitor.worked(1);
            }
        }
    }

    public void configureJavaProject(IProgressMonitor monitor) 
            throws CoreException, OperationCanceledException {
        configureJavaProject(null, monitor);
    }
    
    public void configureJavaProject(String newProjectCompliance, IProgressMonitor monitor) 
            throws CoreException, OperationCanceledException {
        flush(fClassPathList.getElements(), fResourcePathList.getElements(), 
                getJavaOutputLocation(), getJavaProject(), newProjectCompliance, 
                monitor);
        initializeTimeStamps();

        updateUI();
    }

    /**
     * Sets the configured build path and output location to the given Java project.
     * If the project already exists, only build paths are updated.
     * <p>
     * If the classpath contains an Execution Environment entry, the EE's compiler compliance options
     * are used as project-specific options (unless the classpath already contained the same Execution Environment)
     * 
     * @param classPathEntries the new classpath entries (list of {@link CPListElement})
     * @param javaOutputLocation the output location
     * @param javaProject the Java project
     * @param newProjectCompliance compliance to set for a new project, can be <code>null</code>
     * @param monitor a progress monitor, or <code>null</code>
     * @throws CoreException if flushing failed
     * @throws OperationCanceledException if flushing has been cancelled
     */
    public static void flush(List<CPListElement> classPathEntries, List<CPListElement> resourcePathEntries,
            IPath javaOutputLocation, IJavaProject javaProject, String newProjectCompliance, 
            IProgressMonitor monitor) 
                    throws CoreException, OperationCanceledException {
        if (monitor == null) {
            monitor= new NullProgressMonitor();
        }
        monitor.setTaskName(NewWizardMessages.BuildPathsBlock_operationdesc_java);
        monitor.beginTask("", classPathEntries.size() * 4 + 4); //$NON-NLS-1$
        try {
            IProject project= javaProject.getProject();
            IPath projPath= project.getFullPath();

            IPath oldOutputLocation;
            try {
                oldOutputLocation= javaProject.getOutputLocation();
            } catch (CoreException e) {
                oldOutputLocation= projPath.append(PreferenceConstants.getPreferenceStore()
                        .getString(PreferenceConstants.SRCBIN_BINNAME));
            }

            if (oldOutputLocation.equals(projPath) && !javaOutputLocation.equals(projPath)) {
                if (CeylonBuildPathsBlock.hasClassfiles(project)) {
                    if (CeylonBuildPathsBlock.getRemoveOldBinariesQuery(JavaPlugin.getActiveWorkbenchShell())
                            .doQuery(false, projPath)) {
                        CeylonBuildPathsBlock.removeOldClassfiles(project);
                    }
                }
            } else if (!javaOutputLocation.equals(oldOutputLocation)) {
                IFolder folder= ResourcesPlugin.getWorkspace().getRoot().getFolder(oldOutputLocation);
                if (folder.exists()) {
                    if (folder.members().length==0) {
                        CeylonBuildPathsBlock.removeOldClassfiles(folder);
                    } 
                    else {
                        if (CeylonBuildPathsBlock.getRemoveOldBinariesQuery(JavaPlugin.getActiveWorkbenchShell())
                                .doQuery(folder.isDerived(), oldOutputLocation)) {
                            CeylonBuildPathsBlock.removeOldClassfiles(folder);
                        }
                    }
                }
            }
            
            getCeylonModulesOutputFolder(project).delete(true, monitor);

            monitor.worked(1);

            IWorkspaceRoot fWorkspaceRoot= JavaPlugin.getWorkspace().getRoot();

            //create and set the output path first
            if (!fWorkspaceRoot.exists(javaOutputLocation)) {
                CoreUtility.createDerivedFolder(fWorkspaceRoot.getFolder(javaOutputLocation), 
                        true, true, new SubProgressMonitor(monitor, 1));
            } else {
                monitor.worked(1);
            }
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }

            for (Iterator<CPListElement> iter= resourcePathEntries.iterator(); iter.hasNext();) {
                CPListElement entry= iter.next();
                IResource res= entry.getResource();
                //1 tick
                if (res instanceof IFolder && entry.getLinkTarget() == null && !res.exists()) {
                    CoreUtility.createFolder((IFolder)res, true, true, 
                            new SubProgressMonitor(monitor, 1));
                } else {
                    monitor.worked(1);
                }
                
                //3 ticks
                if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                    IPath folderOutput= (IPath) entry.getAttribute(CPListElement.OUTPUT);
                    if (folderOutput != null && folderOutput.segmentCount() > 1) {
                        IFolder folder= fWorkspaceRoot.getFolder(folderOutput);
                        CoreUtility.createDerivedFolder(folder, true, true, 
                                new SubProgressMonitor(monitor, 1));
                    } else {
                        monitor.worked(1);
                    }

                    IPath path= entry.getPath();
                    if (projPath.equals(path)) {
                        monitor.worked(2);
                        continue;
                    }

                    if (projPath.isPrefixOf(path)) {
                        path= path.removeFirstSegments(projPath.segmentCount());
                    }
                    IFolder folder= project.getFolder(path);
                    IPath orginalPath= entry.getOrginalPath();
                    if (orginalPath == null) {
                        if (!folder.exists()) {
                            //New source folder needs to be created
                            if (entry.getLinkTarget() == null) {
                                CoreUtility.createFolder(folder, true, true, 
                                        new SubProgressMonitor(monitor, 2));
                            } else {
                                folder.createLink(entry.getLinkTarget(), 
                                        IResource.ALLOW_MISSING_LOCAL, 
                                        new SubProgressMonitor(monitor, 2));
                            }
                        }
                    } else {
                        if (projPath.isPrefixOf(orginalPath)) {
                            orginalPath= orginalPath.removeFirstSegments(projPath.segmentCount());
                        }
                        IFolder orginalFolder= project.getFolder(orginalPath);
                        if (entry.getLinkTarget() == null) {
                            if (!folder.exists()) {
                                //Source folder was edited, move to new location
                                IPath parentPath= entry.getPath().removeLastSegments(1);
                                if (projPath.isPrefixOf(parentPath)) {
                                    parentPath= parentPath.removeFirstSegments(projPath.segmentCount());
                                }
                                if (parentPath.segmentCount() > 0) {
                                    IFolder parentFolder= project.getFolder(parentPath);
                                    if (!parentFolder.exists()) {
                                        CoreUtility.createFolder(parentFolder, true, true, 
                                                new SubProgressMonitor(monitor, 1));
                                    } else {
                                        monitor.worked(1);
                                    }
                                } else {
                                    monitor.worked(1);
                                }
                                orginalFolder.move(entry.getPath(), true, true, 
                                        new SubProgressMonitor(monitor, 1));
                            }
                        } else {
                            if (!folder.exists() || !entry.getLinkTarget().equals(entry.getOrginalLinkTarget())) {
                                orginalFolder.delete(true, new SubProgressMonitor(monitor, 1));
                                folder.createLink(entry.getLinkTarget(), IResource.ALLOW_MISSING_LOCAL, 
                                        new SubProgressMonitor(monitor, 1));
                            }
                        }
                    }
                }
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
            }
            
            int nEntries= classPathEntries.size();
            IClasspathEntry[] classpath= new IClasspathEntry[nEntries];
            int i= 0;

            for (Iterator<CPListElement> iter= classPathEntries.iterator(); iter.hasNext();) {
                CPListElement entry= iter.next();
                classpath[i]= entry.getClasspathEntry();
                i++;

                IResource res= entry.getResource();
                //1 tick
                if (res instanceof IFolder && entry.getLinkTarget() == null && !res.exists()) {
                    CoreUtility.createFolder((IFolder)res, true, true, 
                            new SubProgressMonitor(monitor, 1));
                } else {
                    monitor.worked(1);
                }

                //3 ticks
                if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
                    IPath folderOutput= (IPath) entry.getAttribute(CPListElement.OUTPUT);
                    if (folderOutput != null && folderOutput.segmentCount() > 1) {
                        IFolder folder= fWorkspaceRoot.getFolder(folderOutput);
                        CoreUtility.createDerivedFolder(folder, true, true, 
                                new SubProgressMonitor(monitor, 1));
                    } else {
                        monitor.worked(1);
                    }

                    IPath path= entry.getPath();
                    if (projPath.equals(path)) {
                        monitor.worked(2);
                        continue;
                    }

                    if (projPath.isPrefixOf(path)) {
                        path= path.removeFirstSegments(projPath.segmentCount());
                    }
                    IFolder folder= project.getFolder(path);
                    IPath orginalPath= entry.getOrginalPath();
                    if (orginalPath == null) {
                        if (!folder.exists()) {
                            //New source folder needs to be created
                            if (entry.getLinkTarget() == null) {
                                CoreUtility.createFolder(folder, true, true, 
                                        new SubProgressMonitor(monitor, 2));
                            } else {
                                folder.createLink(entry.getLinkTarget(), 
                                        IResource.ALLOW_MISSING_LOCAL, 
                                        new SubProgressMonitor(monitor, 2));
                            }
                        }
                    } else {
                        if (projPath.isPrefixOf(orginalPath)) {
                            orginalPath= orginalPath.removeFirstSegments(projPath.segmentCount());
                        }
                        IFolder orginalFolder= project.getFolder(orginalPath);
                        if (entry.getLinkTarget() == null) {
                            if (!folder.exists()) {
                                //Source folder was edited, move to new location
                                IPath parentPath= entry.getPath().removeLastSegments(1);
                                if (projPath.isPrefixOf(parentPath)) {
                                    parentPath= parentPath.removeFirstSegments(projPath.segmentCount());
                                }
                                if (parentPath.segmentCount() > 0) {
                                    IFolder parentFolder= project.getFolder(parentPath);
                                    if (!parentFolder.exists()) {
                                        CoreUtility.createFolder(parentFolder, true, true, 
                                                new SubProgressMonitor(monitor, 1));
                                    } else {
                                        monitor.worked(1);
                                    }
                                } else {
                                    monitor.worked(1);
                                }
                                orginalFolder.move(entry.getPath(), true, true, 
                                        new SubProgressMonitor(monitor, 1));
                            }
                        } else {
                            if (!folder.exists() || !entry.getLinkTarget().equals(entry.getOrginalLinkTarget())) {
                                orginalFolder.delete(true, new SubProgressMonitor(monitor, 1));
                                folder.createLink(entry.getLinkTarget(), IResource.ALLOW_MISSING_LOCAL, 
                                        new SubProgressMonitor(monitor, 1));
                            }
                        }
                    }
                } else {
                    if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                        IPath path= entry.getPath();
                        if (! path.equals(entry.getOrginalPath())) {
                            String eeID= JavaRuntime.getExecutionEnvironmentId(path);
                            if (eeID != null) {
                                BuildPathSupport.setEEComplianceOptions(javaProject, eeID, newProjectCompliance);
                                newProjectCompliance= null; // don't set it again below
                            }
                        }
                        if (newProjectCompliance != null) {
                            setOptionsFromJavaProject(javaProject,
                                    newProjectCompliance);
                        }
                    }
                    monitor.worked(3);
                }
                if (monitor.isCanceled()) {
                    throw new OperationCanceledException();
                }
            }

            javaProject.setRawClasspath(classpath, javaOutputLocation, 
                    new SubProgressMonitor(monitor, 2));
            
            BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
            CeylonProjectConfig config = ceylonProject.getConfiguration();
            List<String> srcDirs = new ArrayList<String>();
            for (CPListElement cpe: classPathEntries) {
                if (cpe.getEntryKind()==IClasspathEntry.CPE_SOURCE) {
                    srcDirs.add(configFilePath(project, cpe));
                }
            }
            config.setProjectSourceDirectories(new CeylonStringIterable(srcDirs));
            List<String> rsrcDirs = new ArrayList<String>();
            for (CPListElement cpe: resourcePathEntries) {
                rsrcDirs.add(configFilePath(project, cpe));
            }
            config.setProjectResourceDirectories(new CeylonStringIterable(rsrcDirs));
            config.save();
            
        } finally {
            monitor.done();
        }
        
    }

    private static String configFilePath(IProject project, CPListElement cpe) {
        IPath linkTarget = cpe.getLinkTarget();
        if (linkTarget==null) { 
            // It's a relative path
            return cpe.getPath().makeRelativeTo(project.getFullPath()).toString();
        }
        else {
            return cpe.getLinkTarget().toOSString();
        }
    }
    
    private static void setOptionsFromJavaProject(IJavaProject javaProject,
            String newProjectCompliance) {
        @SuppressWarnings("unchecked")
        Map<String, String> options= javaProject.getOptions(false);
        JavaModelUtil.setComplianceOptions(options, newProjectCompliance);
        JavaModelUtil.setDefaultClassfileOptions(options, newProjectCompliance); // complete compliance options
        javaProject.setOptions(options);
    }

    public static boolean hasClassfiles(IResource resource) throws CoreException {
        if (resource.isDerived()) {
            return true;
        }
        if (resource instanceof IContainer) {
            IResource[] members= ((IContainer) resource).members();
            for (int i= 0; i < members.length; i++) {
                if (hasClassfiles(members[i])) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void removeOldClassfiles(IResource resource) throws CoreException {
        if (resource.isDerived()) {
            resource.delete(false, null);
        } else if (resource instanceof IContainer) {
            IResource[] members= ((IContainer) resource).members();
            for (int i= 0; i < members.length; i++) {
                removeOldClassfiles(members[i]);
            }
        }
    }

    public static IRemoveOldBinariesQuery getRemoveOldBinariesQuery(final Shell shell) {
        return new IRemoveOldBinariesQuery() {
            public boolean doQuery(final boolean removeFolder, final IPath oldOutputLocation) 
                    throws OperationCanceledException {
                final int[] res= new int[] { 1 };
                Display.getDefault().syncExec(new Runnable() {
                    public void run() {
                        Shell sh= shell != null ? shell : JavaPlugin.getActiveWorkbenchShell();
                        String title= NewWizardMessages.BuildPathsBlock_RemoveBinariesDialog_title;
                        String message;
                        String pathLabel= BasicElementLabels.getPathLabel(oldOutputLocation, false);
                        if (removeFolder) {
                            message= Messages.format(NewWizardMessages.BuildPathsBlock_RemoveOldOutputFolder_description, pathLabel);
                        } else {
                            message= Messages.format(NewWizardMessages.BuildPathsBlock_RemoveBinariesDialog_description, pathLabel);
                        }
                        MessageDialog dialog= new MessageDialog(sh, title, null, message, MessageDialog.QUESTION, 
                                new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
                        res[0]= dialog.open();
                    }
                });
                if (res[0] == 0) {
                    return true;
                } else if (res[0] == 1) {
                    return false;
                }
                throw new OperationCanceledException();
            }
        };
    }


    // ---------- util method ------------

    private IContainer chooseContainer(IPath outputPath) {
        Class<?>[] acceptedClasses= new Class[] { IProject.class, IFolder.class };
        ISelectionStatusValidator validator= new TypedElementSelectionValidator(acceptedClasses, false);
        IProject[] allProjects= fWorkspaceRoot.getProjects();
        ArrayList<IProject> rejectedElements= new ArrayList<IProject>(allProjects.length);
        IProject currProject= fCurrJProject.getProject();
        for (int i= 0; i < allProjects.length; i++) {
            if (!allProjects[i].equals(currProject)) {
                rejectedElements.add(allProjects[i]);
            }
        }
        ViewerFilter filter= new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());

        ILabelProvider lp= new WorkbenchLabelProvider();
        ITreeContentProvider cp= new WorkbenchContentProvider();

        IResource initSelection= null;
        if (outputPath != null) {
            initSelection= fWorkspaceRoot.findMember(outputPath);
        }

        FolderSelectionDialog dialog= new FolderSelectionDialog(getShell(), lp, cp);
        dialog.setTitle(NewWizardMessages.BuildPathsBlock_ChooseOutputFolderDialog_title);
        dialog.setValidator(validator);
        dialog.setMessage(NewWizardMessages.BuildPathsBlock_ChooseOutputFolderDialog_description);
        dialog.addFilter(filter);
        dialog.setInput(fWorkspaceRoot);
        dialog.setInitialSelection(initSelection);
        dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

        if (dialog.open() == Window.OK) {
            return (IContainer)dialog.getFirstResult();
        }
        return null;
    }

    // -------- tab switching ----------

    private void tabChanged(Widget widget) {
        if (widget instanceof TabItem) {
            TabItem tabItem= (TabItem) widget;
            BuildPathBasePage newPage= (BuildPathBasePage) tabItem.getData();
            if (fCurrPage != null) {
                List<?> selection= fCurrPage.getSelection();
                if (!selection.isEmpty()) {
                    newPage.setSelection(selection, false);
                }
            }
            fCurrPage= newPage;
            fPageIndex= tabItem.getParent().getSelectionIndex();
        }
    }

    private int getPageIndex(int entryKind) {
        switch (entryKind) {
            case IClasspathEntry.CPE_CONTAINER:
            case IClasspathEntry.CPE_LIBRARY:
            case IClasspathEntry.CPE_VARIABLE:
                return 2;
            case IClasspathEntry.CPE_PROJECT:
                return 1;
            case IClasspathEntry.CPE_SOURCE:
                return 0;
        }
        return 0;
    }

    private CPListElement findElement(IClasspathEntry entry) {
        CPListElement prefixMatch= null;
        int entryKind= entry.getEntryKind();
        for (int i= 0, len= fClassPathList.getSize(); i < len; i++) {
            CPListElement curr= fClassPathList.getElement(i);
            if (curr.getEntryKind() == entryKind) {
                IPath entryPath= entry.getPath();
                IPath currPath= curr.getPath();
                if (currPath.equals(entryPath)) {
                    return curr;
                }
                // in case there's no full match, look for a similar container (same ID segment):
                if (prefixMatch == null && entryKind == IClasspathEntry.CPE_CONTAINER) {
                    int n= entryPath.segmentCount();
                    if (n > 0) {
                        IPath genericContainerPath= n == 1 ? 
                                entryPath : entryPath.removeLastSegments(n - 1);
                        if (n > 1 && genericContainerPath.isPrefixOf(currPath)) {
                            prefixMatch= curr;
                        }
                    }
                }
            }
        }
        return prefixMatch;
    }

    public void setElementToReveal(IClasspathEntry entry, String attributeKey) {
        int pageIndex= getPageIndex(entry.getEntryKind());
        if (fTabFolder == null) {
            fPageIndex= pageIndex;
        } else {
            fTabFolder.setSelection(pageIndex);
            CPListElement element= findElement(entry);
            if (element != null) {
                Object elementToSelect= element;

                if (attributeKey != null) {
                    Object attrib= element.findAttributeElement(attributeKey);
                    if (attrib != null) {
                        elementToSelect= attrib;
                    }
                }
                BuildPathBasePage page= (BuildPathBasePage) fTabFolder.getItem(pageIndex).getData();
                List<Object> selection= new ArrayList<Object>(1);
                selection.add(elementToSelect);
                page.setSelection(selection, true);
            }
        }
    }

    public void addElement(IClasspathEntry entry) {
        int pageIndex= getPageIndex(entry.getEntryKind());
        if (fTabFolder == null) {
            fPageIndex= pageIndex;
        } else {
            fTabFolder.setSelection(pageIndex);

//            Object page=  fTabFolder.getItem(pageIndex).getData();
//            if (page instanceof LibrariesWorkbookPage) {
//                CPListElement element= CPListElement.create(entry, true, fCurrJProject);
//                ((LibrariesWorkbookPage) page).addElement(element);
//            }
        }
    }

    public boolean isOKStatus() {
        return findMostSevereStatus().isOK();
    }

    public void setFocus() {
        fSourceContainerPage.setFocus();
    }

    private void refreshSourcePathsFromConfigFile() {
        final IProject project = fCurrJProject.getProject();
        BaseCeylonProject ceylonProject = modelJ2C().ceylonModel().getProject(project);
        if (ceylonProject != null) {
            CeylonProjectConfig config = ceylonProject.getConfiguration();
            List<String> sourceDirectories = toJavaStringList(config.getProjectSourceDirectories());
            updateClassPathsFromConfigFile(sourceDirectories, new HashSet<String>());
        }
    }

    private void updateClassPathsFromConfigFile(List<String> sourceDirectories, final Set<String> newFolderNames) {
        final IProject project = fCurrJProject.getProject();
        final List<CPListElement> newSourcePath = new ArrayList<CPListElement>();
        for (final String path : sourceDirectories) {
            final IPath iPath = Path.fromOSString(path);
            if (!iPath.isAbsolute()) {
                IFolder folder = project.getFolder(iPath);
                newSourcePath.add(new CPListElement(fCurrJProject, 
                        IClasspathEntry.CPE_SOURCE, 
                        folder.getFullPath(), 
                        folder));
                if (!folder.exists()) {
                    someFoldersNeedToBeCreated = true;
                }
            }
            else {
                try {
                    class CPListElementHolder {
                        public CPListElement value = null;
                    }
                    final CPListElementHolder cpListElement = new CPListElementHolder();
                    project.accept(new IResourceVisitor() {
                        @Override
                        public boolean visit(IResource resource) 
                                throws CoreException {
                            if (resource instanceof IFolder &&
                                    resource.isLinked() && 
                                    resource.getLocation() != null &&
                                    resource.getLocation().equals(iPath)) {
                                cpListElement.value = new CPListElement(null,
                                        fCurrJProject, IClasspathEntry.CPE_SOURCE, 
                                        resource.getFullPath(), 
                                        resource, resource.getLocation());
                                return false;
                            }
                            return resource instanceof IFolder || 
                                    resource instanceof IProject;
                        }
                    });
                    if (cpListElement.value == null) {
                        String newFolderName = iPath.lastSegment();
                        IFolder newFolder = project.getFolder(newFolderName);
                        int counter = 1;
                        while (newFolderNames.contains(newFolderName) || newFolder.exists()) {
                            newFolderName = iPath.lastSegment() + "_" + counter++;
                            newFolder = project.getFolder(newFolderName);
                        }
                        newFolderNames.add(newFolderName);
                        cpListElement.value = new CPListElement(null,
                                fCurrJProject, IClasspathEntry.CPE_SOURCE, 
                                newFolder.getFullPath(), 
                                newFolder, iPath);
                        someFoldersNeedToBeCreated = true;
                    }
                    newSourcePath.add(cpListElement.value);
                }
                catch (CoreException ex) {
                    ex.printStackTrace();
                }
            }
        }
        ArrayList<CPListElement> exportedEntries = new ArrayList<>();
        ArrayList<CPListElement> newClassPath = new ArrayList<>();
        // Don't change all the non-source classpath entries
        for (CPListElement elem : fClassPathList.getElements()) {
            if (elem.getClasspathEntry().getEntryKind() != IClasspathEntry.CPE_SOURCE) {
                newClassPath.add(elem);
            }
        }
        for (CPListElement elem : fClassPathList.getCheckedElements()) {
            if (elem.getClasspathEntry().getEntryKind() != IClasspathEntry.CPE_SOURCE) {
                exportedEntries.add(elem);
            }
        }

        // Now all the new source entries
        newClassPath.addAll(newSourcePath);
        
        for (CPListElement elem : fClassPathList.getCheckedElements()) {
            if (newSourcePath.contains(elem)) {
                exportedEntries.add(elem);
            }
        }

        fClassPathList.setElements(newClassPath);
        fClassPathList.setCheckedElements(exportedEntries);
        fClassPathList.selectFirstElement();
    }
}
