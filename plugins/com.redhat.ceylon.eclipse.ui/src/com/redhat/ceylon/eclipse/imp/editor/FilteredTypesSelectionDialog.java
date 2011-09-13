package com.redhat.ceylon.eclipse.imp.editor;

import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.imp.editor.UniversalEditor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.contentProposer.CeylonContentProposer;
import com.redhat.ceylon.eclipse.imp.treeModelBuilder.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class FilteredTypesSelectionDialog extends FilteredItemsSelectionDialog {
    private UniversalEditor editor;
    
    private class TypeSelectionHistory extends SelectionHistory {
        protected Object restoreItemFromMemento(IMemento element) {
            String qualifiedName = element.getString("qualifiedName");
            String unitFileName = element.getString("unitFileName");
            String packageName = element.getString("packageName");
            IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
            for (PhasedUnit unit: CeylonBuilder.getUnits(input.getFile().getProject())) {
                if (unit.getUnit().getFilename().equals(unitFileName)
                        && unit.getPackage().getQualifiedNameString().equals(packageName)) {
                    for (Declaration dec: unit.getPackage().getMembers()) {
                        if (dec.getQualifiedNameString().equals(qualifiedName)) {
                            return dec;
                        }
                    }
                }
           }
           return null; 
        }
        protected void storeItemToMemento(Object item, IMemento element) {
            Declaration dec = (Declaration) item;
            element.putString("qualifiedName", dec.getQualifiedNameString());
            element.putString("unitFileName", dec.getUnit().getFilename());
            element.putString("packageName", dec.getUnit().getPackage().getQualifiedNameString());
        }
     }
    
    public FilteredTypesSelectionDialog(Shell shell, UniversalEditor editor) {
        super(shell);
        this.editor = editor;
        setSelectionHistory(new TypeSelectionHistory());
        setListLabelProvider(new ILabelProvider() {
            @Override
            public void addListener(ILabelProviderListener listener) {}
            @Override
            public void dispose() {}
            @Override
            public boolean isLabelProperty(Object element, String property) {
                // TODO Auto-generated method stub
                return false;
            }
            @Override
            public void removeListener(ILabelProviderListener listener) {}
            @Override
            public Image getImage(Object element) {
                return CeylonLabelProvider.getImage((Declaration) element);
            }
            @Override
            public String getText(Object element) {
                return CeylonContentProposer.getDescriptionFor((Declaration) element);
            }            
        });
        setDetailsLabelProvider(new ILabelProvider() {
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
                return "[" + CeylonDocumentationProvider.getPackageLabel((Declaration) element) + "]" +
                        " - " + ((Declaration) element).getUnit().getFilename();
            }
            @Override
            public Image getImage(Object element) {
                return CeylonLabelProvider.PACKAGE;
            }
        });
    }
    
    public FilteredTypesSelectionDialog(Shell shell, boolean multi) {
        super(shell, multi);
    }
    
    @Override
    protected Control createExtendedContentArea(Composite parent) {
        return null;
    }
    
    @Override
    protected IDialogSettings getDialogSettings() {
        return CeylonPlugin.getInstance().getDialogSettings();
    }
    
    @Override
    protected IStatus validateItem(Object item) {
        return Status.OK_STATUS; //(IStatus.OK, CeylonPlugin.kPluginID, 0, "", null); //$NON-NLS-1$
    }
    
    @Override
    protected ItemsFilter createFilter() {
        return new ItemsFilter() {
            @Override
            public boolean matchItem(Object item) {
                return matchesRawNamePattern(item);
            }
            @Override
            public boolean isConsistentItem(Object item) {
                return true;
            }
        };
    }
    
    @Override
    protected Comparator getItemsComparator() {
        return new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Declaration) o1).getName().compareTo(((Declaration) o2).getName());
            }
        };
    }
    
    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException {
        IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
        for (PhasedUnit unit: CeylonBuilder.getUnits(input.getFile().getProject())) {
            for (Declaration dec: unit.getPackage().getMembers()) {
                contentProvider.add(dec, itemsFilter);
            }
        }
    }
    
    @Override
    public String getElementName(Object item) {
        return ((Declaration) item).getName();
    }
    
    @Override
    protected void setResult(List newResult) {
        super.setResult(newResult);
    }
}
