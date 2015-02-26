package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.util.Highlights.VERSION_STYLER;

import java.util.Comparator;
import java.util.Set;

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

import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.code.open.FilteredItemsSelectionDialog;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

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
            return CeylonLabelProvider.MODULE;
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
                if (module.isDefault()) {
                    return new StyledString("(default module)");
                } else {
                    return new StyledString(module.getNameAsString())
                        .append(" \"" + module.getVersion() + "\"", 
                                VERSION_STYLER);
                }
            }
            return new StyledString();
        }

        @Override
        public String getText(Object element) {
            return getStyledText(element).getString();
        }
        
    }
    
    class ModuleDetailsLabelProvider extends ModuleLabelProvider {
        @Override
        public Image getImage(Object element) {
            if (element instanceof JDTModule) {
                JDTModule module = (JDTModule) element;
                if (module.isProjectModule()) {
                    return CeylonLabelProvider.PROJECT;
                }
                else {
                    return CeylonLabelProvider.REPO;
                }
            }
            else {
                return null;
            }
        }

        @Override
        public String getText(Object element) {
            if (element instanceof JDTModule) {
                final JDTModule module = (JDTModule) element;
                if (module.isProjectModule()) {
//                    final ProjectSourceFile unit = (ProjectSourceFile) module.getUnit();
//                    return unit.getProjectResource().getName();
                    return module.getModuleManager().getJavaProject().getProject().getName();
                }
                else {
                    return module.getRepositoryDisplayString();
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
        initLabelProviders(new ModuleLabelProvider(), null, new ModuleDetailsLabelProvider(), null, null);
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
