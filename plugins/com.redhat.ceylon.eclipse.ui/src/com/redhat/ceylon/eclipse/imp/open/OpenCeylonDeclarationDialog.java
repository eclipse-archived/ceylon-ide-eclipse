package com.redhat.ceylon.eclipse.imp.open;

import static com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.imp.proposals.CeylonContentProposer.getDescriptionFor;

import java.util.Comparator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.imp.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.imp.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class OpenCeylonDeclarationDialog extends FilteredItemsSelectionDialog {
    //private IEditorPart editor;
    
    private class TypeSelectionHistory extends SelectionHistory {
        protected Object restoreItemFromMemento(IMemento element) {
            String qualifiedName = element.getString("qualifiedName");
            String unitFileName = element.getString("unitFileName");
            String packageName = element.getString("packageName");
            String projectName = element.getString("projectName");
            //for (PhasedUnit unit: CeylonBuilder.getUnits(Util.getProject(editor.getEditorInput()))) {
            for (PhasedUnit unit: CeylonBuilder.getUnits( new String[] {projectName} )) {
                if (unit.getUnit().getFilename().equals(unitFileName)
                        && unit.getPackage().getQualifiedNameString().equals(packageName)) {
                    for (Declaration dec: unit.getPackage().getMembers()) {
                        if (dec.getQualifiedNameString().equals(qualifiedName)) {
                            return new DeclarationWithProject(dec, 
                                    CeylonBuilder.getFile(unit).getProject());
                        }
                    }
                }
           }
           return null; 
        }
        protected void storeItemToMemento(Object item, IMemento element) {
            Declaration dec = ((DeclarationWithProject) item).getDeclaration();
            element.putString("qualifiedName", dec.getQualifiedNameString());
            element.putString("unitFileName", dec.getUnit().getFilename());
            element.putString("packageName", dec.getUnit().getPackage().getQualifiedNameString());
            element.putString("projectName", ((DeclarationWithProject) item).getProject().getName());
        }
     }
    
    public OpenCeylonDeclarationDialog(Shell shell, IEditorPart editor) {
        super(shell);
        //this.editor = editor;
        setSelectionHistory(new TypeSelectionHistory());
        setListLabelProvider(new ILabelProvider() {
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
                DeclarationWithProject dwp = (DeclarationWithProject) element;
                return dwp==null ? null : CeylonLabelProvider.getImage(dwp.getDeclaration());
            }
            @Override
            public String getText(Object element) {
                DeclarationWithProject dwp = (DeclarationWithProject) element;
                return dwp==null ? null : getDescriptionFor(dwp.getDeclaration());
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
                DeclarationWithProject dwp = (DeclarationWithProject) element;
                return "[" + getPackageLabel(dwp.getDeclaration()) + "] - " + 
                        dwp.getDeclaration().getUnit().getFilename() +
                        " in project " + dwp.getProject().getName();
            }
            @Override
            public Image getImage(Object element) {
                return CeylonLabelProvider.PACKAGE;
            }
        });
        setListSelectionLabelDecorator(new ILabelDecorator() {         
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
                DeclarationWithProject dwp = (DeclarationWithProject) element;
                return text + " [" + getPackageLabel(dwp.getDeclaration()) + "]";
            }
            @Override
            public Image decorateImage(Image image, Object element) {
                return null;
            }
        });
    }
    
    public OpenCeylonDeclarationDialog(Shell shell, boolean multi) {
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
    protected Comparator<Object> getItemsComparator() {
        return new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                //TODO: also sort by project
                DeclarationWithProject dwp1 = (DeclarationWithProject) o1;
                DeclarationWithProject dwp2 = (DeclarationWithProject) o2;
                int dc = dwp1.getDeclaration().getName()
                        .compareTo(dwp2.getDeclaration().getName());
                return dc!=0 ? dc : dwp1.getProject().getName()
                        .compareTo(dwp2.getProject().getName());
            }
        };
    }
    
    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException {
        for (PhasedUnit unit: CeylonBuilder.getUnits()) {
            for (Declaration dec: unit.getPackage().getMembers()) {
                contentProvider.add(new DeclarationWithProject(dec, 
                        CeylonBuilder.getFile(unit).getProject()),
                        itemsFilter);
            }
        }
        //TODO: this hacks in special support for the language 
        //      module, when it should be support for any src
        //      archive in the repo!
        for (IProject project: CeylonBuilder.getProjects()) {
            TypeChecker tc = CeylonBuilder.getProjectTypeChecker(project);
            for (Package p: tc.getContext().getModules().getLanguageModule().getPackages()) {
                for (Declaration dec: p.getMembers()) {
                    contentProvider.add(new DeclarationWithProject(dec, project), 
                            itemsFilter);
                }
            }
        }
    }
    
    @Override
    public String getElementName(Object item) {
        return ((DeclarationWithProject) item).getDeclaration().getName();
    }
    
}
