package com.redhat.ceylon.eclipse.code.navigator;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static com.redhat.ceylon.ide.common.util.toJavaStringList_.toJavaStringList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.JavaPluginImages;
import org.eclipse.jdt.internal.ui.actions.ActionMessages;
import org.eclipse.jdt.internal.ui.actions.JarImportWizardAction;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jdt.internal.ui.preferences.BuildPathsPropertyPage;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.CPListElement;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.AddArchiveToBuildpathAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.AddFolderToBuildpathAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.AddLibraryToBuildpathAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.AddSelectedLibraryToBuildpathAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.ConfigureBuildPathAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.CreateLinkedSourceFolderAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.CreateSourceFolderAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.EditFilterAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.EditOutputFolderAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.ExcludeFromBuildpathAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.IncludeToBuildpathAction;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.RemoveFromBuildpathAction;
import org.eclipse.jdt.ui.IContextMenuConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.texteditor.IUpdate;

import com.redhat.ceylon.eclipse.code.preferences.CeylonBuildPathsBlock;
import com.redhat.ceylon.eclipse.code.preferences.CeylonBuildPathsPropertiesPage;
import com.redhat.ceylon.eclipse.core.external.ExternalSourceArchiveManager;
import com.redhat.ceylon.ide.common.model.BaseCeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjectConfig;

/**
 * Copied from <code>{@link org.eclipse.jdt.internal.ui.wizards.buildpaths.newsourcepage.GenerateBuildPathActionGroup}</code>
 * to override some of the actions.
 */
public class GenerateBuildPathActionGroup extends ActionGroup {
    /**
     * Pop-up menu: id of the source sub menu (value <code>org.eclipse.jdt.ui.buildpath.menu</code>).
     *
     * @since 3.1
     */
    public static final String MENU_ID= "org.eclipse.jdt.ui.buildpath.menu"; //$NON-NLS-1$

    /**
     * Pop-up menu: id of the build path (add /remove) group of the build path sub menu (value
     * <code>buildpathGroup</code>).
     *
     * @since 3.1
     */
    public static final String GROUP_BUILDPATH= "buildpathGroup";  //$NON-NLS-1$

    /**
     * Pop-up menu: id of the filter (include / exclude) group of the build path sub menu (value
     * <code>filterGroup</code>).
     *
     * @since 3.1
     */
    public static final String GROUP_FILTER= "filterGroup";  //$NON-NLS-1$

    /**
     * Pop-up menu: id of the customize (filters / output folder) group of the build path sub menu (value
     * <code>customizeGroup</code>).
     *
     * @since 3.1
     */
    public static final String GROUP_CUSTOMIZE= "customizeGroup";  //$NON-NLS-1$

    private static class NoActionAvailable extends Action {
        public NoActionAvailable() {
            setEnabled(false);
            setText(NewWizardMessages.GenerateBuildPathActionGroup_no_action_available);
        }
    }
    private Action fNoActionAvailable= new NoActionAvailable();

    private class UpdateJarFileAction extends JarImportWizardAction implements IUpdate {

        public UpdateJarFileAction() {
            setText(ActionMessages.GenerateBuildPathActionGroup_update_jar_text);
            setDescription(ActionMessages.GenerateBuildPathActionGroup_update_jar_description);
            setToolTipText(ActionMessages.GenerateBuildPathActionGroup_update_jar_tooltip);
            setImageDescriptor(JavaPluginImages.DESC_OBJS_JAR);
            PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IJavaHelpContextIds.JARIMPORT_WIZARD_PAGE);
        }

        /**
         * {@inheritDoc}
         */
        public void update() {
            final IWorkbenchPart part= fSite.getPage().getActivePart();
            if (part != null)
                setActivePart(this, part);
            selectionChanged(this, fSelectionProvider.getSelection());
        }
    }

    private final IWorkbenchSite fSite;
    private final ISelectionProvider fSelectionProvider;
    private final List<Action> fActions;

    private String fGroupName= IContextMenuConstants.GROUP_REORGANIZE;


    /**
     * Creates a new <code>GenerateActionGroup</code>. The group
     * requires that the selection provided by the page's selection provider
     * is of type <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
     *
     * @param page the page that owns this action group
     */
    public GenerateBuildPathActionGroup(Page page) {
        this(page.getSite(), page.getSite().getSelectionProvider());
    }

    /**
     * Creates a new <code>GenerateActionGroup</code>. The group
     * requires that the selection provided by the part's selection provider
     * is of type <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
     *
     * @param part the view part that owns this action group
     */
    public GenerateBuildPathActionGroup(IViewPart part) {
        this(part.getSite(), part.getSite().getSelectionProvider());
    }
    /**
     * Creates a new <code>GenerateActionGroup</code>. The group requires
     * that the selection provided by the given selection provider is of type
     * {@link IStructuredSelection}.
     *
     * @param site the site that will own the action group.
     * @param selectionProvider the selection provider used instead of the
     *  page selection provider.
     *
     * @since 3.4
     */
    public GenerateBuildPathActionGroup(IWorkbenchSite site, ISelectionProvider selectionProvider) {
        fSite= site;
        fSelectionProvider= selectionProvider;
        fActions= new ArrayList<Action>();

        final CreateLinkedSourceFolderAction addLinkedSourceFolderAction= new CreateLinkedSourceFolderAction(site) {
            private void doBuildpathAction() {
                super.run();
            }
            @Override
            public void run() {
                alsoManageCeylonConfigFile(new Runnable() {
                    @Override
                    public void run() {
                        doBuildpathAction();
                    }
                }, getShell(), getSelectedElements());
            }
        };
        fActions.add(addLinkedSourceFolderAction);

        final CreateSourceFolderAction addSourceFolderAction= new CreateSourceFolderAction(site) {
            private void doBuildpathAction() {
                super.run();
            }
            @Override
            public void run() {
                alsoManageCeylonConfigFile(new Runnable() {
                    @Override
                    public void run() {
                        doBuildpathAction();
                    }
                }, getShell(), getSelectedElements());
            }
        };
        fActions.add(addSourceFolderAction);

        final AddFolderToBuildpathAction addFolder= new AddFolderToBuildpathAction(site) {
            private void doBuildpathAction() {
                super.run();
            }
            @Override
            public void run() {
                alsoManageCeylonConfigFile(new Runnable() {
                    @Override
                    public void run() {
                        doBuildpathAction();
                    }
                }, getShell(), getSelectedElements());
            }
        };
        fActions.add(addFolder);

        final AddSelectedLibraryToBuildpathAction addSelectedLibrary= new AddSelectedLibraryToBuildpathAction(site);
        fActions.add(addSelectedLibrary);

        final RemoveFromBuildpathAction remove = new RemoveFromBuildpathAction(site) {
            private void doBuildpathAction() {
                super.run();
            }
            @Override
            public void run() {
                alsoManageCeylonConfigFile(new Runnable() {
                    @Override
                    public void run() {
                        doBuildpathAction();
                    }
                }, getShell(), getSelectedElements());
            }
        };
        fActions.add(remove);

        final AddArchiveToBuildpathAction addArchive= new AddArchiveToBuildpathAction(site);
        fActions.add(addArchive);

        final AddLibraryToBuildpathAction addLibrary= new AddLibraryToBuildpathAction(site);
        fActions.add(addLibrary);

        final UpdateJarFileAction updateAction= new UpdateJarFileAction();
        fActions.add(updateAction);

        final ExcludeFromBuildpathAction exclude= new ExcludeFromBuildpathAction(site);
        fActions.add(exclude);

        final IncludeToBuildpathAction include= new IncludeToBuildpathAction(site);
        fActions.add(include);

        final EditFilterAction editFilterAction= new EditFilterAction(site);
        fActions.add(editFilterAction);

        final EditOutputFolderAction editOutput= new EditOutputFolderAction(site);
        fActions.add(editOutput);

        final ConfigureBuildPathAction configure= new ConfigureBuildPathAction(site) {
            @Override
            public void run() {
                IProject project = null;
                Object firstElement = getSelectedElements().get(0);
                HashMap<Object, IClasspathEntry> data= new HashMap<Object, IClasspathEntry>();

                if (firstElement instanceof IJavaElement) {
                    IJavaElement element= (IJavaElement) firstElement;
                    IPackageFragmentRoot root= (IPackageFragmentRoot) element.getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
                    if (root != null) {
                        try {
                            data.put(BuildPathsPropertyPage.DATA_REVEAL_ENTRY, root.getRawClasspathEntry());
                        } catch (JavaModelException e) {
                            // ignore
                        }
                    }
                    project = element.getJavaProject().getProject();
                }
                else if (firstElement instanceof PackageFragmentRootContainer) {
                    PackageFragmentRootContainer container= (PackageFragmentRootContainer) firstElement;
                    project= container.getJavaProject().getProject();
                    IClasspathEntry entry = container instanceof ClassPathContainer ? ((ClassPathContainer) container).getClasspathEntry() : JavaCore.newLibraryEntry(new Path("/x/y"), null, null); //$NON-NLS-1$
                    data.put(BuildPathsPropertyPage.DATA_REVEAL_ENTRY, entry);
                }
                else {
                    project= ((IResource) ((IAdaptable) firstElement).getAdapter(IResource.class)).getProject();
                }
                PreferencesUtil.createPropertyDialogOn(getShell(), project, CeylonBuildPathsPropertiesPage.ID, null, data).open();
            }
        };
        fActions.add(configure);

        for (Iterator<Action> iter= fActions.iterator(); iter.hasNext();) {
            Action action= iter.next();
            if (action instanceof ISelectionChangedListener) {
                ISelectionChangedListener listener = (ISelectionChangedListener) action;
                selectionProvider.addSelectionChangedListener(listener);
                listener.selectionChanged(new SelectionChangedEvent(selectionProvider, selectionProvider.getSelection()));
            }
        }

    }

    public static void alsoManageCeylonConfigFile(Runnable runnable, Shell shell, List<?> selectedElements) {
        // First check the the config is synchronized to authorize the action
        IProject project = null;
        IJavaProject javaProject = null;
        Object object= selectedElements.get(0);
        if (object instanceof IJavaElement) {
            IJavaElement javaElement = (IJavaElement) object;
            javaProject = javaElement.getJavaProject();
        } else if (object instanceof IResource) {
            IResource resource = (IResource) object;
            if (!ExternalSourceArchiveManager.isInSourceArchive(resource)) {
                javaProject= JavaCore.create(resource.getProject());
            }
        }
        
        if (javaProject != null) {
            project = javaProject.getProject();
        }

        if (project == null) {
            return;
        }
        
        BaseCeylonProject ceylonProject = 
                modelJ2C()
                    .ceylonModel()
                    .getProject(project);
        
        if (ceylonProject != null
                && !ceylonProject.getSynchronizedWithConfiguration()) {
            MessageDialog.openError(shell, 
                    NewWizardMessages.NewSourceContainerWorkbookPage_ToolBar_RemoveFromCP_tooltip, 
                    "The Ceylon configuration file (.ceylon/config) is not synchronized with the current build path settings.\n" +
                    "You should fix this before adding changing the Ceylon build path (a QuickFix is available).");
            return;
        }
        
        // Then run the modification action
        runnable.run();
        
        if (ceylonProject != null) {
            CeylonProjectConfig config = ceylonProject.getConfiguration();
    
            // And finally update the Ceylon config file
            try {
                CPListElement[] classPathEntries = 
                        CPListElement.createFromExisting(javaProject);
                List<CPListElement> resourcePathEntries = new ArrayList<>();
                CeylonBuildPathsBlock.resourcePathsFromStrings(javaProject, 
                        toJavaStringList(config.getProjectResourceDirectories()), 
                        new HashSet<String>(), resourcePathEntries);
                CeylonBuildPathsBlock.flush(Arrays.asList(classPathEntries), 
                        resourcePathEntries, javaProject.readOutputLocation(), 
                        javaProject, null, null);
            }
            catch (OperationCanceledException|CoreException e) {}
        }
    }
    
    /* (non-Javadoc)
     * Function declared in ActionGroup
     */
    @Override
    public void fillActionBars(IActionBars actionBar) {
        super.fillActionBars(actionBar);
        setGlobalActionHandlers(actionBar);
    }

    /* (non-Javadoc)
     * Function declared in ActionGroup
     */
    @Override
    public void fillContextMenu(IMenuManager menu) {
        super.fillContextMenu(menu);
        if (!canOperateOnSelection())
            return;
        String menuText= ActionMessages.BuildPath_label;
        IMenuManager subMenu= new MenuManager(menuText, MENU_ID);
        subMenu.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager) {
                fillViewSubMenu(manager);
            }
        });
        subMenu.setRemoveAllWhenShown(true);
        subMenu.add(new ConfigureBuildPathAction(fSite));
        menu.appendToGroup(fGroupName, subMenu);
    }

    private void fillViewSubMenu(IMenuManager source) {
        int added= 0;
        int i=0;
        for (Iterator<Action> iter= fActions.iterator(); iter.hasNext();) {
            Action action= iter.next();
            if (action instanceof IUpdate)
                ((IUpdate) action).update();

            if (i == 2)
                source.add(new Separator(GROUP_BUILDPATH));
            else if (i == 8)
                source.add(new Separator(GROUP_FILTER));
            else if (i == 10)
                source.add(new Separator(GROUP_CUSTOMIZE));
            added+= addAction(source, action);
            i++;
        }

        if (added == 0) {
            source.add(fNoActionAvailable);
        }
    }

    /**
     * @param actionBar the action bars to set the handler for
     */
    private void setGlobalActionHandlers(IActionBars actionBar) {
        // TODO implement
    }

    private int addAction(IMenuManager menu, IAction action) {
        if (action != null && action.isEnabled()) {
            menu.add(action);
            return 1;
        }
        return 0;
    }

    private boolean canOperateOnSelection() {
        ISelection sel= fSelectionProvider.getSelection();
        if (!(sel instanceof IStructuredSelection))
            return false;
        IStructuredSelection selection= (IStructuredSelection)sel;
        if (selection.isEmpty())
            return false;
        for (Iterator<?> iter= selection.iterator(); iter.hasNext();) {
            Object element= iter.next();
            if (element instanceof IWorkingSet)
                return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (fActions != null) {
            for (Iterator<Action> iter= fActions.iterator(); iter.hasNext();) {
                Action action= iter.next();
                if (action instanceof ISelectionChangedListener)
                    fSelectionProvider.removeSelectionChangedListener((ISelectionChangedListener) action);
            }
            fActions.clear();
        }
        super.dispose();
    }
}
