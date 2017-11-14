/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.launch;

import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getSourceFolders;
import static org.eclipse.ceylon.ide.eclipse.util.Highlights.STRING_STYLER;

import java.util.Comparator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.ceylon.ide.eclipse.code.open.FilteredItemsSelectionDialog;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonPlugin;
import org.eclipse.ceylon.ide.eclipse.ui.CeylonResources;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.ide.common.model.CeylonProject;
import org.eclipse.ceylon.ide.common.model.IdeModule;
import org.eclipse.ceylon.ide.common.model.ProjectSourceFile;
import org.eclipse.ceylon.model.typechecker.model.Module;

public class CeylonModuleSelectionDialog extends FilteredItemsSelectionDialog {
    
    class ModuleLabelProvider 
            extends StyledCellLabelProvider 
            implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, 
                       ILabelProvider {
        
        @Override
        public boolean isLabelProperty(Object arg0, String arg1) {
            return false;
        }
        
        @Override
        public Image getImage(Object element) {
            return CeylonResources.MODULE;
        }
        
        @Override
        public void update(ViewerCell cell) {
            cell.setImage(getImage(cell.getElement()));
            StyledString styledText = getStyledText(cell.getElement());
            cell.setText(styledText.getString());
            cell.setStyleRanges(styledText.getStyleRanges());
            super.update(cell);
        }
        
        @Override
        public StyledString getStyledText(Object element) {
            if (element instanceof Module) {
                Module module = (Module) element;
                if (module.isDefaultModule()) {
                    return new StyledString("(default module)");
                } else {
                    return new StyledString(module.getNameAsString())
                        .append(" \"" + module.getVersion() + "\"", 
                                STRING_STYLER);
                }
            }
            return new StyledString();
        }

        @Override
        public String getText(Object element) {
            return getStyledText(element).getString();
        }
        
    }
    
    class ModuleRepoDetailsLabelProvider extends ModuleLabelProvider {
        @Override
        public Image getImage(Object element) {
            if (element instanceof BaseIdeModule) {
                BaseIdeModule module = (BaseIdeModule) element;
                if (module.getIsProjectModule()) {
                    return CeylonResources.PROJECT;
                }
                else {
                    return CeylonResources.REPO;
                }
            }
            else {
                return null;
            }
        }

        @Override
        public String getText(Object element) {
            if (element instanceof IdeModule) {
                final IdeModule<IProject,IResource,IFolder,IFile> module = (IdeModule<IProject,IResource,IFolder,IFile>) element;
                if (module.getIsProjectModule()) {
//                    ProjectSourceFile unit = (ProjectSourceFile) module.getUnit();
//                    return unit.getProjectResource().getName();
                    CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = module.getCeylonProject();
                    return ceylonProject.getIdeArtifact().getName();
                }
                else {
                    return module.getRepositoryDisplayString();
                }
            }
            return "";
        }
        
    }

    class ModuleSourceFolderLabelProvider extends ModuleLabelProvider {
        @Override
        public Image getImage(Object element) {
            if (element instanceof BaseIdeModule) {
                BaseIdeModule module = (BaseIdeModule) element;
                if (module.getIsProjectModule()) {
                    if (!module.getIsDefaultModule()) {
                        return CeylonResources.SOURCE_FOLDER;
                    }
                }
            }
            return null;
        }

        @Override
        public String getText(Object element) {
            if (element instanceof IdeModule) {
                final IdeModule<IProject,IResource,IFolder,IFile> module = (IdeModule<IProject,IResource,IFolder,IFile>) element;
                if (module.getIsProjectModule()) {
                    if (!module.getIsDefaultModule()) {
                        CeylonProject<IProject,IResource,IFolder,IFile> ceylonProject = module.getCeylonProject();
                        IProject project = ceylonProject.getIdeArtifact();
                        ProjectSourceFile<IProject,IResource,IFolder,IFile> unit = 
                                (ProjectSourceFile<IProject,IResource,IFolder,IFile>) module.getUnit();
                        for (IFolder folder: getSourceFolders(project)) {
                            if (folder.findMember(unit.getRelativePath())!=null) {
                                return folder.getFullPath().toPortableString();
                            }
                        }
                        return null;
                    }
                }
            }
            return "";
        }
        
    }
    class ModuleItemsFilter extends ItemsFilter {
        @Override
        public boolean isConsistentItem(Object item) {
            return item instanceof Module;
        }
        @Override
        public boolean matchItem(Object item) {
            if (item instanceof Module) {
                return matches(((Module) item).getNameAsString());
            }
            return false;
        }
        @Override
        public String getPattern() {
            String pattern = super.getPattern(); 
            return pattern.isEmpty() ? "**" : pattern;
        }
    }
 
    
    Set<Module> modules;

    public CeylonModuleSelectionDialog(Shell shell, Set<Module> modules) {
        super(shell, false, 
                "&Type part of a name with wildcard *:", 
                "&Choose a module to run:");
        setTitle("Ceylon Launcher");
        this.modules = modules;
        initLabelProviders(new ModuleLabelProvider(), null, new ModuleRepoDetailsLabelProvider(), new ModuleSourceFolderLabelProvider(), null);
    }

    @Override
    protected ItemsFilter createFilter() {
        return new ModuleItemsFilter();
    }

    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter filter, IProgressMonitor monitor)
        throws CoreException {
        if (this.modules!= null) {
            for (Module entry : this.modules) {
                contentProvider.add(entry, filter);
            }
        }
    }

    @Override
    public String getElementName(Object mod) {
        if (mod instanceof Module) {
            Module entry = (Module)mod;
            return entry.toString();
        }
        return null;
    }

    @Override
    protected Comparator<Object> getItemsComparator() {
        return new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                if(o1 instanceof Module 
                    && o2 instanceof Module) {
                    return o1.toString().compareTo(
                        (o2.toString()));
                }
                return -1;
            }
        };
    }
    
    private static final String SETTINGS_ID = 
            CeylonPlugin.PLUGIN_ID + ".addDeclarationFilterDialog";            
    @Override
    public boolean enableDocArea() {
        return false;
    }
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = CeylonPlugin.getInstance().getDialogSettings();
        IDialogSettings section = settings.getSection(SETTINGS_ID);
        if (section == null) {
            section = settings.addNewSection(SETTINGS_ID);
        }
        return section;
    }
    @Override
    protected IDialogSettings getDialogBoundsSettings() {
        IDialogSettings settings = getDialogSettings();
        IDialogSettings section = settings.getSection(DIALOG_BOUNDS_SETTINGS);
        if (section == null) {
            section = settings.addNewSection(DIALOG_BOUNDS_SETTINGS);
            section.put(DIALOG_HEIGHT, 500);
            section.put(DIALOG_WIDTH, 400);
        }
        return section;
    }
    @Override
    protected void fillViewMenu(IMenuManager menuManager) {}

    @Override
    protected IStatus validateItem(Object mod) {
        return Status.OK_STATUS;
    }

}
