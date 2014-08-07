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

package com.redhat.ceylon.eclipse.code.preferences;


import static com.redhat.ceylon.eclipse.code.wizard.NewProjectWizard.DEFAULT_RESOURCE_FOLDER;
import static com.redhat.ceylon.eclipse.code.wizard.NewProjectWizard.DEFAULT_SOURCE_FOLDER;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonModulesOutputFolder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.views.navigator.ResourceComparator;

import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.core.builder.CeylonProjectConfig;

public class CeylonBuildPathsBlock {

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
        item.setImage(CeylonLabelProvider.FOLDER);
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
        final List<CPListElement> newClassPath;
        IProject project= fCurrJProject.getProject();
        projectExists= (project.exists() && project.getFile(".classpath").exists()); //$NON-NLS-1$
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
        
        final List<CPListElement> newResourcePath = new ArrayList<CPListElement>();
        if (projectExists) {
            CeylonProjectConfig config = CeylonProjectConfig.get(project);
            for (final String path: config.getProjectResourceDirectories()) {
                final IPath iPath = Path.fromOSString(path);
                if (! iPath.isAbsolute()) {
                    IFolder folder = fCurrJProject.getProject()
                            .getFolder(iPath);
                    newResourcePath.add(new CPListElement(fCurrJProject, 
                            IClasspathEntry.CPE_SOURCE, 
                            folder.getFullPath(), 
                            folder));
                }
                else {
                    try {   
                        project.accept(new IResourceVisitor() {
                            @Override
                            public boolean visit(IResource resource) 
                                    throws CoreException {
                                if (resource.isLinked() && resource.getLocation() != null &&
                                        resource.getLocation().equals(iPath)) {
                                    newResourcePath.add(new CPListElement(null,
                                            fCurrJProject, IClasspathEntry.CPE_SOURCE, 
                                            resource.getFullPath(), 
                                            resource, resource.getLocation()));
                                }
                                return resource instanceof IFolder || 
                                        resource instanceof IProject;
                            }
                        });
                    }
                    catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else {
            IFolder defaultResourceFolder = fCurrJProject.getProject().getFolder(DEFAULT_RESOURCE_FOLDER);
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
        fJavaBuildPathDialogField.refresh();
        fClassPathList.refresh();
        fResourcePathList.refresh();
        
        doStatusLineUpdate();
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
        return !currSettings.equals(fUserSettingsTimeStamp);
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
        String sourceFolderName= DEFAULT_SOURCE_FOLDER;//store.getString(PreferenceConstants.SRCBIN_SRCNAME);
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
            
            CeylonProjectConfig config = CeylonProjectConfig.get(project);
            List<String> srcDirs = new ArrayList<String>();
            for (CPListElement cpe: classPathEntries) {
                if (cpe.getEntryKind()==IClasspathEntry.CPE_SOURCE) {
                    srcDirs.add(configFilePath(project, cpe));
                }
            }
            config.setProjectSourceDirectories(srcDirs);
            List<String> rsrcDirs = new ArrayList<String>();
            for (CPListElement cpe: resourcePathEntries) {
                rsrcDirs.add(configFilePath(project, cpe));
            }
            config.setProjectResourceDirectories(rsrcDirs);
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
}
