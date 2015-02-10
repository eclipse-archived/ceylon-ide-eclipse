package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getLabelDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getStyledDescriptionFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.PACKAGE;

import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class CeylonTopLevelSelectionDialog extends FilteredItemsSelectionDialog {

    private static final String SETTINGS_ID = 
            CeylonPlugin.PLUGIN_ID + ".TOPLEVEL_DECLARATION_SELECTION_DIALOG";
    
    private List<Declaration> decls;
    
    public CeylonTopLevelSelectionDialog(Shell shell, boolean multi, 
            List<Declaration> decls ) {
        super(shell, multi);
        setTitle("Ceylon Launcher");
        setMessage("Select the toplevel method or class to launch:");
        setListLabelProvider(new LabelProvider());
        setDetailsLabelProvider(new DetailsLabelProvider());
        setListSelectionLabelDecorator(new SelectionLabelDecorator());
        this.decls = decls;
    }

    @Override
    protected Control createExtendedContentArea(Composite parent) {
        return null;
    }

    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = 
                CeylonPlugin.getInstance().getDialogSettings();
        IDialogSettings section = settings.getSection(SETTINGS_ID);
        if (section == null) {
            section = settings.addNewSection(SETTINGS_ID);
        } 
        return section;
    }

    @Override
    protected IStatus validateItem(Object item) {
        return Status.OK_STATUS;
    }

    @Override
    protected ItemsFilter createFilter() {
        return new ItemsFilter() {
            @Override
            public boolean matchItem(Object item) {
                return matches(getElementName(item));
            }
            @Override
            public boolean isConsistentItem(Object item) {
                return true;
            }
            @Override
            public String getPattern() {
                String pattern = super.getPattern(); 
                return pattern.isEmpty() ? "**" : pattern;
            }
        };
    }

    @Override
    protected Comparator<?> getItemsComparator() {
        Comparator<Object> comp = new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                if (o1 instanceof Declaration && 
                        o2 instanceof Declaration) {
                    if (o1 instanceof TypedDeclaration && 
                            o2 instanceof TypeDeclaration) {
                        return -1;
                    }
                    else if (o2 instanceof TypedDeclaration && 
                            o1 instanceof TypeDeclaration) {
                        return 1;
                    }
                    else {
                        return ((Declaration)o1).getName()
                                .compareTo(((Declaration)o2).getName());
                    }
                }
                return 0;
            }
        };
        return comp;
    }

    @Override
    protected void fillContentProvider(
            AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
            throws CoreException {
        if(this.decls != null) {
            for(Declaration d : this.decls) {
                if(itemsFilter.isConsistentItem(d)) {
                    contentProvider.add(d, itemsFilter);
                }
            }
        }
    }

    @Override
    public String getElementName(Object item) {
        return ((Declaration) item).getName();
    }
    
    static class LabelProvider extends StyledCellLabelProvider 
        implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, ILabelProvider {

        @Override
        public void addListener(ILabelProviderListener listener) {}
        
        @Override
        public void dispose() {}
        
        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        
        @Override
        public void removeListener(ILabelProviderListener listener) {}
        
        @Override
        public Image getImage(Object element) {
            Declaration d = (Declaration) element;
            return d==null ? null : getImageForDeclaration(d);
        }
        
        @Override
        public String getText(Object element) {
            Declaration d = (Declaration) element;
            return d==null ? null : getLabelDescriptionFor(d);
        }
        
        @Override
        public StyledString getStyledText(Object element) {
            if (element==null) {
                return new StyledString();
            }
            else {
                Declaration d = (Declaration) element;
                return getStyledDescriptionFor(d);
            }
        }
        
        @Override
        public void update(ViewerCell cell) {
            Object element = cell.getElement();
            if (element!=null) {
                StyledString styledText = getStyledText(element);
                cell.setText(styledText.toString());
                cell.setStyleRanges(styledText.getStyleRanges());
                cell.setImage(getImage(element));
                super.update(cell);
            }
        }
    }

    static class DetailsLabelProvider implements ILabelProvider {
        @Override
        public void removeListener(ILabelProviderListener listener) {}
        
        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        
        @Override
        public void dispose() {}
        
        @Override
        public void addListener(ILabelProviderListener listener) {}
        
        @Override
        public String getText(Object element) {
            return getPackageLabel((Declaration) element);
        }
        
        @Override
        public Image getImage(Object element) {
            return PACKAGE;
        }
    }

    static class SelectionLabelDecorator implements ILabelDecorator {
        @Override
        public void removeListener(ILabelProviderListener listener) {}
        
        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        
        @Override
        public void dispose() {}
        
        @Override
        public void addListener(ILabelProviderListener listener) {}
        
        @Override
        public String decorateText(String text, Object element) {
            return text + " - " + getPackageLabel((Declaration) element);
        }
        
        @Override
        public Image decorateImage(Image image, Object element) {
            return null;
        }
    }  
}
