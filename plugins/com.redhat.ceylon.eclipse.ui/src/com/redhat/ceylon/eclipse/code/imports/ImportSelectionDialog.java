package com.redhat.ceylon.eclipse.code.imports;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getLabelDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getStyledDescriptionFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;

import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
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
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.Highlights;

final class ImportSelectionDialog extends
        FilteredItemsSelectionDialog {
    private List<Declaration> proposals;

    ImportSelectionDialog(Shell shell, List<Declaration> proposals) {
        super(shell);
        this.proposals = proposals;
        setTitle("Organize Imports");
        setMessage("Select declaration to import:");
        setListLabelProvider(new LabelProvider());
        setDetailsLabelProvider(new DetailsLabelProvider());
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, 
                IDialogConstants.PROCEED_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.SKIP_LABEL, false);
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
    protected Comparator<Object> getItemsComparator() {
        return new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Declaration d1 = (Declaration) o1;
                Declaration d2 = (Declaration) o2;
                return d1.getQualifiedNameString()
                        .compareTo(d2.getQualifiedNameString());
            }
        };
    }

    @Override
    protected void fillContentProvider(
            AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
                    throws CoreException {
        for (Declaration d: proposals) {
            contentProvider.add(d, itemsFilter);
        }
    }

    @Override
    public String getElementName(Object item) {
        return ((Declaration) item).getQualifiedNameString();
    }

}

class DetailsLabelProvider implements ILabelProvider {
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
        Declaration d = (Declaration) element;
        return getPackageLabel(d);
    }

    @Override
    public Image getImage(Object element) {
        return CeylonResources.PACKAGE;
    }
}

class LabelProvider extends StyledCellLabelProvider 
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
            StyledString label = getStyledDescriptionFor(d);
            label.append(" - ", Highlights.PACKAGE_STYLER)
                .append(getPackageLabel(d), Highlights.PACKAGE_STYLER);
            return label;
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
