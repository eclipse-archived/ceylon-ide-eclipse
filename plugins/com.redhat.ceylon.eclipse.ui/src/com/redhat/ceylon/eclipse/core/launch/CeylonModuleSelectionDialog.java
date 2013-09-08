package com.redhat.ceylon.eclipse.core.launch;

import java.util.Comparator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

public class CeylonModuleSelectionDialog extends FilteredItemsSelectionDialog {
    
    public class ModuleLabelProvider implements ILabelProvider {

        @Override
        public void addListener(ILabelProviderListener arg0) {}

        @Override
        public void dispose() {
        }

        @Override
        public boolean isLabelProperty(Object arg0, String arg1) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener arg0) {}
        
        @Override
        public Image getImage(Object element) {
            return JavaUI.getSharedImages().getImage(ISharedImages.IMG_OBJS_JAR_WITH_SOURCE);
        }

        @Override
        public String getText(Object mod) {
            if (mod instanceof CeylonModuleTab.ModuleRepresentation) {
                return mod.toString();
            }
            return null;
        }
        
    }
    
    class ModuleDetailsLabelProvider extends ModuleLabelProvider {
        @Override
        public Image getImage(Object element) {
            return super.getImage(element);
        }

        @Override
        public String getText(Object mod) {
            return super.getText(mod);
        }
        
    }

    class ModuleItemsFilter extends ItemsFilter {
        public boolean isConsistentItem(Object item) {
            return item instanceof CeylonModuleTab.ModuleRepresentation;
        }
        
        public boolean matchItem(Object item) {
            if(!(item instanceof CeylonModuleTab.ModuleRepresentation) || !modules.contains((CeylonModuleTab.ModuleRepresentation)item)) {
                return false;
            }
            return matches(item.toString());
        }
    }
 
    
    Set<CeylonModuleTab.ModuleRepresentation> modules;

    public CeylonModuleSelectionDialog(Shell shell, Set<CeylonModuleTab.ModuleRepresentation> modules, String title) {
        super(shell, false);
        setTitle(title);
        this.modules = modules;
        setListLabelProvider(new ModuleLabelProvider());
        setMessage(title);
        setInitialPattern("**"); //$NON-NLS-1$
        setDetailsLabelProvider(new ModuleDetailsLabelProvider());
    }

    @Override
    protected Control createExtendedContentArea(Composite parent) {
        return null;
    }

    @Override
    protected ItemsFilter createFilter() {
        return new ModuleItemsFilter();
    }

    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter filter, IProgressMonitor monitor)
        throws CoreException {
        if (this.modules!= null) {
            for (CeylonModuleTab.ModuleRepresentation entry : this.modules) {
                contentProvider.add(entry, filter);
            }
        }
    }

    @Override
    protected IDialogSettings getDialogSettings() {
        return JDIDebugUIPlugin.getDefault().getDialogSettings();
    }

    @Override
    public String getElementName(Object mod) {
        if (mod instanceof CeylonModuleTab.ModuleRepresentation) {
            CeylonModuleTab.ModuleRepresentation entry = (CeylonModuleTab.ModuleRepresentation)mod;
            return entry.toString();
        }
        return null;
    }

    @Override
    protected Comparator getItemsComparator() {
        Comparator comp = new Comparator() {
            public int compare(Object o1, Object o2) {
                if(o1 instanceof CeylonModuleTab.ModuleRepresentation 
                    && o2 instanceof CeylonModuleTab.ModuleRepresentation) {
                    return o1.toString().compareTo(
                        (o2.toString()));
                }
                return -1;
            }
        };
        return comp;
    }

    @Override
    protected IStatus validateItem(Object mod) {
        return Status.OK_STATUS;
    }

}
