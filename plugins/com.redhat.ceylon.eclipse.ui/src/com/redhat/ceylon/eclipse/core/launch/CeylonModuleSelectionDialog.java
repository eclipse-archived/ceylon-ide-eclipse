package com.redhat.ceylon.eclipse.core.launch;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.model.IWorkbenchAdapter;

import com.redhat.ceylon.compiler.typechecker.model.Module;

public class CeylonModuleSelectionDialog extends FilteredItemsSelectionDialog {

    public class ModuleLabelProvider implements ILabelProvider {

        @Override
        public void addListener(ILabelProviderListener arg0) {}

        @Override
        public void dispose() {
            fImageMap.clear();
            fImageMap = null;
        }

        @Override
        public boolean isLabelProperty(Object arg0, String arg1) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener arg0) {}

        HashMap fImageMap = new HashMap();
        
        @Override
        public Image getImage(Object element) {
            if(element instanceof IAdaptable) {
                IWorkbenchAdapter adapter = (IWorkbenchAdapter) ((IAdaptable)element).getAdapter(IWorkbenchAdapter.class);
                if(adapter != null) {
                    ImageDescriptor descriptor = adapter.getImageDescriptor(element);
                    Image image = (Image) fImageMap.get(descriptor);
                    if(image == null) {
                        image = descriptor.createImage();
                        fImageMap.put(descriptor, image);
                    }
                    return image;
                }
            }
            return null;
        }

        @Override
        public String getText(Object mod) {
            if (mod instanceof Map.Entry) {
                Map.Entry<String, Module> entry = (Entry<String, Module>)mod;
                return entry.getKey() + ":" + entry.getValue().getNameAsString() + "/" + entry.getValue().getVersion() ;
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
            return item instanceof Map.Entry;
        }
        public boolean matchItem(Object item) {
            if(!(item instanceof Map.Entry) || !modules.containsKey(((Map.Entry<String, Module>)item).getKey())) {
                return false;
            }
            return matches((((Map.Entry<String, Module>)item).getKey()));
        }
    }
 
    class ModuleSelectionHistory extends SelectionHistory {
        protected Object restoreItemFromMemento(IMemento memento) {
            return memento.getTextData();
        }
        protected void storeItemToMemento(Object item, IMemento memento) {
            if(item instanceof Map.Entry) {
                memento.putTextData(((Map.Entry) item).getKey().toString());
            }
        }
    }
    
    Map<String, Module> modules;

    public CeylonModuleSelectionDialog(Shell shell, Map<String, Module> modules, String title) {
        super(shell, false);
        setTitle(title);
        this.modules = modules;
        setListLabelProvider(new ModuleLabelProvider());
        setMessage(title);
        setInitialPattern("**"); //$NON-NLS-1$
        setDetailsLabelProvider(new ModuleDetailsLabelProvider());
        //setSelectionHistory(new ModuleSelectionHistory());
    }

    @Override
    protected Control createExtendedContentArea(Composite parent) {
        return null;
    }

    @Override
    protected ItemsFilter createFilter() {
        return null; //new ModuleItemsFilter();
    }

    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter filter, IProgressMonitor monitor)
        throws CoreException {
        if (modules!= null) {
            for (Map.Entry<String, Module> entry : this.modules.entrySet()) {
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
        if (mod instanceof Map.Entry) {
            Map.Entry<String, Module> entry = (Entry<String, Module>)mod;
            return entry.getKey() + ":" + entry.getValue().getNameAsString() + "/" + entry.getValue().getVersion() ;
        }
        return null;
    }

    @Override
    protected Comparator getItemsComparator() {
        Comparator comp = new Comparator() {
            public int compare(Object o1, Object o2) {
                if(o1 instanceof Map.Entry && o2 instanceof Map.Entry) {
                    Map.Entry<String, Module> entry1 = (Entry<String, Module>) o1;
                    Map.Entry<String, Module> entry2 = (Entry<String, Module>) o2;
                    return (entry1.getKey().concat(entry1.getValue().toString()).compareTo(
                        (entry2.getKey().concat(entry2.getValue().toString()))));
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
