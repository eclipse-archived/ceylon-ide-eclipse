package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getDescriptionFor;
import static com.redhat.ceylon.eclipse.code.propose.CeylonContentProposer.getStyledDescriptionFor;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;
import static org.eclipse.jface.viewers.StyledString.QUALIFIER_STYLER;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public class OpenCeylonDeclarationDialog extends FilteredItemsSelectionDialog {
    //private IEditorPart editor;
    
    class SelectionLabelDecorator implements ILabelDecorator {
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
            if (nameOccursMultipleTimes(dwp.getDeclaration())) {
                return text;
            }
            else {
                return text + " - " + getPackageLabel(dwp.getDeclaration());
            }
        }
        
        @Override
        public Image decorateImage(Image image, Object element) {
            return null;
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
            DeclarationWithProject dwp = (DeclarationWithProject) element;
            return getPackageLabel(dwp.getDeclaration()) + " - " + getLocation(dwp);
        }

        @Override
        public Image getImage(Object element) {
            return CeylonLabelProvider.PACKAGE;
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
            DeclarationWithProject dwp = (DeclarationWithProject) element;
            return dwp==null ? null : CeylonLabelProvider.getImage(dwp.getDeclaration());
        }
        
        @Override
        public String getText(Object element) {
            DeclarationWithProject dwp = (DeclarationWithProject) element;
            return dwp==null ? null : getDescriptionFor(dwp.getDeclaration());
        }

        @Override
        public StyledString getStyledText(Object element) {
            if (element==null) {
                return new StyledString();
            }
            else {
                DeclarationWithProject dwp = (DeclarationWithProject) element;
                StyledString label = getStyledDescriptionFor(dwp.getDeclaration());
                if (nameOccursMultipleTimes(dwp.getDeclaration())) {
                    label.append(" - ", QUALIFIER_STYLER)
                        .append(getPackageLabel(dwp.getDeclaration()), QUALIFIER_STYLER)
                        .append(" - ", COUNTER_STYLER)
                        .append(getLocation(dwp), COUNTER_STYLER);
                }
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

    private class TypeSelectionHistory extends SelectionHistory {
        protected Object restoreItemFromMemento(IMemento element) {
            String qualifiedName = element.getString("qualifiedName");
            String unitFileName = element.getString("unitFileName");
            String packageName = element.getString("packageName");
            String projectName = element.getString("projectName");
            String path = element.getString("path");
            //search for a source file in the project
            //TODO: we can probably remove this loop
            for (PhasedUnit unit: CeylonBuilder.getUnits( new String[] {projectName} )) {
                if (unit.getUnit().getFilename().equals(unitFileName)
                        && unit.getPackage().getQualifiedNameString().equals(packageName)) {
                    for (Declaration dec: unit.getDeclarations()) {
                        if (dec.getQualifiedNameString().equals(qualifiedName)) {
                            return new DeclarationWithProject(dec, 
                                    CeylonBuilder.getFile(unit).getProject(), path);
                        }
                    }
                }
            }
            //if we don't find it, search all dependent modules
            //this will find declarations in src archives
            for (IProject p: CeylonBuilder.getProjects()) {
                if (p.getName().equals(projectName)) {
                    Modules modules = CeylonBuilder.getProjectTypeChecker(p).getContext().getModules();
                    for (Module module: modules.getListOfModules()) {
                        for (Package pkg: module.getAllPackages()) { 
                            if (pkg.getQualifiedNameString().equals(packageName)) {
                                for (Declaration dec: pkg.getMembers()) {
                                    if (dec.getQualifiedNameString().equals(qualifiedName)) {
                                        return new DeclarationWithProject(dec, p, path);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null; 
        }
        protected void storeItemToMemento(Object item, IMemento element) {
            DeclarationWithProject dwp = (DeclarationWithProject) item;
            Declaration dec = dwp.getDeclaration();
            element.putString("qualifiedName", dec.getQualifiedNameString());
            element.putString("unitFileName", dec.getUnit().getFilename());
            element.putString("packageName", dec.getUnit().getPackage().getQualifiedNameString());
            IProject project = dwp.getProject();
            element.putString("projectName", project.getName());
            element.putString("path", dwp.getPath());
        }
     }
    
    public OpenCeylonDeclarationDialog(Shell shell, IEditorPart editor) {
        super(shell);
        //this.editor = editor;
        setSelectionHistory(new TypeSelectionHistory());
        setListLabelProvider(new LabelProvider());
        setDetailsLabelProvider(new DetailsLabelProvider());
        setListSelectionLabelDecorator(new SelectionLabelDecorator());
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
                return matches(getElementName(item));
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
                DeclarationWithProject dwp1 = (DeclarationWithProject) o1;
                DeclarationWithProject dwp2 = (DeclarationWithProject) o2;
                int dc = dwp1.getDeclaration().getName()
                        .compareTo(dwp2.getDeclaration().getName());
                return dc!=0 ? dc : dwp1.getProject().getName()
                        .compareTo(dwp2.getProject().getName());
            }
        };
    }
    
    Map<String,Integer> usedNames = new HashMap<String,Integer>();
    
    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, IProgressMonitor progressMonitor) throws CoreException {
        usedNames.clear();
        Set<DeclarationWithProject> set = new HashSet<DeclarationWithProject>();
        for (PhasedUnit unit: CeylonBuilder.getUnits()) {
            for (Declaration dec: unit.getDeclarations()) {
                if (dec.isToplevel() && isPresentable(dec)) {
                    DeclarationWithProject dwp = new DeclarationWithProject(dec, 
                            CeylonBuilder.getFile(unit).getProject(),
                            unit.getUnitFile().getPath());
                    contentProvider.add(dwp, itemsFilter);
                    set.add(dwp);
                    nameOccurs(dec);
                }
            }
        }
        for (IProject project: CeylonBuilder.getProjects()) {
            TypeChecker tc = CeylonBuilder.getProjectTypeChecker(project);
            for (Module m: tc.getContext().getModules().getListOfModules()) {
                if (!m.isJava()) {
                    for (Package p: m.getPackages()) {
                        for (Declaration dec: p.getMembers()) {
                            if (isPresentable(dec)) {
                                DeclarationWithProject dwp = new DeclarationWithProject(dec, project, null); //TODO: figure out the full path
                                //TODO: eliminate duplicates based on the
                                //      location of the module archive
                                if (!set.contains(dwp)) {
                                    contentProvider.add(dwp, itemsFilter);
                                    nameOccurs(dec);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getLocation(DeclarationWithProject dwp) {
        IProject project = dwp.getProject();
		if (dwp.getPath()!=null) {
        	IResource r = project.isOpen() ? 
        			project.findMember(dwp.getPath()) : null;
            //if the project is closed or for some other reason
    		//findMember() returns null, just abbreviate to the 
    		//project path
            if (r==null) r=project;
            return r.getFullPath().toPortableString();
                    
        }
        else {
            Module module = dwp.getDeclaration().getUnit()
                    .getPackage().getModule();
            return " in module " + module.getNameAsString() +
                  (module.getVersion()==null ? "" : ":" + module.getVersion()) +
                  " imported by project " + project.getName();
        }
    }
    
    private boolean nameOccursMultipleTimes(Declaration dec) {
        Integer n = usedNames.get(dec.getName());
        return n!=null && n>1;
    }

    private void nameOccurs(Declaration dec) {
        Integer i = usedNames.get(dec.getName());
        if (i==null) i=0;
        usedNames.put(dec.getName(), i+1);
    }
    
    private boolean isPresentable(Declaration d) {
        String name = d.getName();
        return name!=null && (!(d instanceof TypeDeclaration) ||
                Character.isUpperCase(name.charAt(0)));
    }
    
    @Override
    public String getElementName(Object item) {
        return ((DeclarationWithProject) item).getDeclaration().getName();
    }
    
}
