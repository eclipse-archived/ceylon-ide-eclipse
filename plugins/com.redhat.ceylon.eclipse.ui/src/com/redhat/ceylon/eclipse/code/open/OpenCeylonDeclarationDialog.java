package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isNameMatching;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getLabelDescriptionFor;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getStyledDescriptionFor;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getFile;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectSourceModules;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Util;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.Highlights;

public class OpenCeylonDeclarationDialog extends FilteredItemsSelectionDialog {
    
    private boolean includeMembers;
    
    private final class Filter extends ItemsFilter {
        boolean members = includeMembers;

        @Override
        public boolean matchItem(Object item) {
            return isNameMatching(getPattern(), 
                    ((DeclarationWithProject) item).getDeclaration());
//            return matches(getElementName(item));
        }

        @Override
        public boolean isConsistentItem(Object item) {
            return true;
        }

        @Override
        public boolean equalsFilter(ItemsFilter filter) {
            if (!(filter instanceof Filter) ||
                    members!=((Filter)filter).members) {
                return false;
            }
            else {
                return super.equalsFilter(filter);
            }
        }
        
        @Override
        public boolean isSubFilter(ItemsFilter filter) {
            if (!(filter instanceof Filter) ||
                    members!=((Filter)filter).members) {
                return false;
            }
            else {
                return super.isSubFilter(filter);
            }
        }
    }

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
            if (element instanceof DeclarationWithProject) {
                DeclarationWithProject dwp = 
                        (DeclarationWithProject) element;
                Declaration d = dwp.getDeclaration();
                try {
                    if (nameOccursMultipleTimes(d)) {
                        return text;
                    }
                    else {
                        String string = text + " - " + 
                                getPackageLabel(d);
                        if (dwp.getVersion()!=null) {
                            string += " \"" + dwp.getVersion() + "\"";
                        }
                        return string;
                    }
                }
                catch (Exception e) {
                    System.err.println(d.getName());
                    e.printStackTrace();
                    return null;
                }
            }
            else {
                return text;
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
            if (element instanceof DeclarationWithProject) {
                DeclarationWithProject dwp = 
                        (DeclarationWithProject) element;
                Declaration d = dwp.getDeclaration();
                try {
                    return getPackageLabel(d) + " - " + getLocation(dwp);
                }
                catch (Exception e) {
                    System.err.println(d.getName());
                    e.printStackTrace();
                    return null;
                }
            }
            else {
                return "";
            }
        }

        @Override
        public Image getImage(Object element) {
            return CeylonLabelProvider.PACKAGE;
        }
    }

    class LabelProvider extends StyledCellLabelProvider 
            implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, 
                       ILabelProvider {
        
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
            if (element instanceof DeclarationWithProject) {
                DeclarationWithProject dwp = 
                        (DeclarationWithProject) element;
                Declaration d = dwp.getDeclaration();
                try {
                    return getImageForDeclaration(d);
                }
                catch (Exception e) {
                    System.err.println(d.getName());
                    e.printStackTrace();
                    return null;
                }
            }
            else {
                return null;
            }
        }

        @Override
        public String getText(Object element) {
            if (element instanceof DeclarationWithProject) {
                DeclarationWithProject dwp = 
                        (DeclarationWithProject) element;
                Declaration d = dwp.getDeclaration();
                try {
                    return getLabelDescriptionFor(d);
                }
                catch (Exception e) {
                    System.err.println(d.getName());
                    e.printStackTrace();
                    return d.getName();
                }
            }
            else {
                return "";
            }
        }

        @Override
        public StyledString getStyledText(Object element) {
            if (element instanceof DeclarationWithProject) {
                DeclarationWithProject dwp = 
                        (DeclarationWithProject) element;
                Declaration d = dwp.getDeclaration();
                try {
                    StyledString label = getStyledDescriptionFor(d);
                    if (d.isClassOrInterfaceMember()) {
                        Declaration ci = (Declaration) d.getContainer();
                        label.append(" of ")
                        .append(ci.getName(), Highlights.TYPE_ID_STYLER);
                    }
                    if (nameOccursMultipleTimes(d)) {
                        label.append(" - ", Highlights.PACKAGE_STYLER)
                        .append(getPackageLabel(d), Highlights.PACKAGE_STYLER)
                        .append(" - ", COUNTER_STYLER)
                        .append(getLocation(dwp), COUNTER_STYLER);
                    }
                    return label;
                }
                catch (Exception e) {
                    System.err.println(d.getName());
                    e.printStackTrace();
                    return new StyledString(d.getName());
                }
            }
            else {
                return new StyledString();
            }
        }

        @Override
        public void update(ViewerCell cell) {
            Object element = cell.getElement();
            if (element instanceof DeclarationWithProject) {
                StyledString styledText = getStyledText(element);
                cell.setText(styledText.toString());
                cell.setStyleRanges(styledText.getStyleRanges());
                cell.setImage(getImage(element));
            }
            else {
                cell.setStyleRanges(new StyleRange[0]);
            }
            super.update(cell);
        }
        
    }

    private class TypeSelectionHistory extends SelectionHistory {
        protected Object restoreItemFromMemento(IMemento element) {
            String qualifiedName = element.getString("qualifiedName");
            String unitFileName = element.getString("unitFileName");
            String packageName = element.getString("packageName");
            String projectName = element.getString("projectName");
            String version = element.getString("version");
            String path = element.getString("path");
            //search for a source file in the project
            //TODO: we can probably remove this loop
            if (projectName!=null) {
                for (PhasedUnit unit: getUnits( new String[] {projectName} )) {
                    if (unit.getUnit().getFilename().equals(unitFileName)
                            && unit.getPackage().getQualifiedNameString().equals(packageName)) {
                        for (Declaration dec: unit.getDeclarations()) {
                            if (dec.getQualifiedNameString().equals(qualifiedName)) {
                                return new DeclarationWithProject(dec, 
                                        getFile(unit).getProject(), 
                                        version, path);
                            }
                        }
                    }
                }
            }
            //if we don't find it, search all dependent modules
            //this will find declarations in src archives
            for (IProject p: getProjects()) {
                if (p.getName().equals(projectName)) {
                    Modules modules = getProjectTypeChecker(p).getContext().getModules();
                    for (Module module: modules.getListOfModules()) {
                        for (Package pkg: module.getAllPackages()) { 
                            if (pkg.getQualifiedNameString().equals(packageName)) {
                                for (Declaration dec: pkg.getMembers()) {
                                    if (dec.getQualifiedNameString().equals(qualifiedName)) {
                                        return new DeclarationWithProject(dec, p, version, path);
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
            element.putString("qualifiedName", 
                    dec.getQualifiedNameString());
            element.putString("unitFileName", 
                    dec.getUnit().getFilename());
            element.putString("packageName", 
                    dec.getUnit().getPackage().getQualifiedNameString());
            IProject project = dwp.getProject();
            element.putString("projectName", 
                    project==null ? null : project.getName());
            element.putString("path", dwp.getPath());
            element.putString("version", dwp.getVersion());
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
        return new Filter();
    }
    
    @Override
    protected Comparator<Object> getItemsComparator() {
        return new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                DeclarationWithProject dwp1 = 
                        (DeclarationWithProject) o1;
                DeclarationWithProject dwp2 = 
                        (DeclarationWithProject) o2;
                int dc = compareDeclarations(
                        dwp1.getDeclaration(), 
                        dwp2.getDeclaration());
                if (dc!=0) {
                    return dc;
                }
                else if (dwp1.getProject()==dwp2.getProject()) {
                    return 0;
                }
                else if (dwp1.getProject()==null) {
                    return 1;
                }
                else if (dwp2.getProject()==null) {
                    return -1;
                }
                else {
                    return dwp1.getProject().getName()
                            .compareTo(dwp2.getProject().getName());
                }
            }
            private int compareDeclarations(Declaration dec1, 
                    Declaration dec2) {
                int dc = dec1.getName()
                        .compareTo(dec2.getName());
                if (dc!=0) {
                    return dc;
                }
                else if (dec1.isClassOrInterfaceMember() && 
                        !dec2.isClassOrInterfaceMember()) {
                    return 1;
                }
                else if (!dec1.isClassOrInterfaceMember() && 
                        dec2.isClassOrInterfaceMember()) {
                    return -1;
                }
                else if (dec1.isClassOrInterfaceMember() && 
                        dec2.isClassOrInterfaceMember()) {
                    return compareDeclarations(
                            (Declaration) dec1.getContainer(), 
                            (Declaration) dec2.getContainer());
                }
                else {
                    return 0;
                }
            }
        };
    }
    
    Map<String,Integer> usedNames = 
            new HashMap<String,Integer>();
    
    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, IProgressMonitor progressMonitor) 
                    throws CoreException {
        usedNames.clear();
        progressMonitor.beginTask("filtering", 10000);
        Set<DeclarationWithProject> set = 
                new HashSet<DeclarationWithProject>();
        List<PhasedUnit> units = getUnits();
        for (PhasedUnit unit: units) {
            for (Declaration dec: unit.getDeclarations()) {
                if (includeMembers ? dec.isShared() : dec.isToplevel() && 
                        isPresentable(dec)) {
                    String version = unit.getPackage().getModule().getVersion();
                    DeclarationWithProject dwp = 
                            new DeclarationWithProject(dec, 
                                    getFile(unit).getProject(),
                                    version,
                                    unit.getUnitFile().getPath());
                    contentProvider.add(dwp, itemsFilter);
                    set.add(dwp);
                    nameOccurs(dec);
                }
            }
            progressMonitor.worked(1000/units.size());
        }
        Collection<IProject> projects = getProjects();
        for (IProject project: projects) {
            List<Package> packages = new LinkedList<Package>();
            
            for (Module compiledModule: 
                    getProjectSourceModules(project)) {
                for (Package p: compiledModule.getAllPackages()) {
                    Module m = p.getModule();
                    if (!m.isJava() || includeJava()) {
                        packages.add(p);
                    }
                }
            }
            for (Package p: packages) {
                for (Declaration dec: p.getMembers()) {
                    if (isPresentable(dec)) {
                        String version = p.getModule().getVersion();
                        DeclarationWithProject dwp =
                                new DeclarationWithProject(dec,
                                        project, version, null); //TODO: figure out the full path
                        //TODO: eliminate duplicates based on the
                        //      location of the module archive
                        if (set.add(dwp)) {
                            contentProvider.add(dwp, itemsFilter);
                            nameOccurs(dec);
                        }
                        if (includeMembers &&
                                dec instanceof ClassOrInterface) {
                            for (Declaration mem: dec.getMembers()) {
                                if (mem.isShared() && isPresentable(mem)) {
                                    DeclarationWithProject mwp =
                                            new DeclarationWithProject(mem,
                                                    project, version, null); //TODO: figure out the full path
                                    if (set.add(mwp)) {
                                        contentProvider.add(mwp, itemsFilter);
                                        nameOccurs(mem);
                                    }
                                }
                            }
                        }
                    }
                }
                progressMonitor.worked(9000/projects.size()/packages.size());
            }
        }
    }
    
    boolean includeJava() {
        return true;
    }

    private static String getLocation(DeclarationWithProject dwp) {
        IProject project = dwp.getProject();
        if (project!=null && dwp.getPath()!=null) {
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
            StringBuilder sb = new StringBuilder();
            sb.append("in module ")
              .append(module.getNameAsString());
            if (module.getVersion()!=null) {
                sb.append(" \"")
                  .append(module.getVersion())
                  .append("\"");
            }
            if (project!=null) {
                sb.append(" imported by project ")
                  .append(project.getName());
            }
            return sb.toString();
        }
    }
    
    private String toName(Declaration dec) {
        String name = dec.getName();
        if (dec.isClassOrInterfaceMember()) {
            name = ((Declaration)dec.getContainer()).getName() + 
                    "." + name; 
        }
        return name;
    }

    private boolean nameOccursMultipleTimes(Declaration dec) {
        Integer n = usedNames.get(toName(dec));
        return n!=null && n>1;
    }

    private void nameOccurs(Declaration dec) {
        String name = toName(dec);
        Integer i = usedNames.get(name);
        if (i==null) i=0;
        usedNames.put(name, i+1);
    }
    
    boolean isPresentable(Declaration d) {
        String name = d.getName();
        return name!=null && !d.isAnonymous() && 
                !Util.isOverloadedVersion(d);
    }
    
    @Override
    public String getElementName(Object item) {
        return ((DeclarationWithProject) item).getDeclaration().getName();
    }
    
    @Override
    protected void fillViewMenu(IMenuManager menuManager) {
        Action action = 
                new Action("Include Member Declarations", 
                        IAction.AS_CHECK_BOX) {
            @Override
            public void run() {
                includeMembers=!includeMembers;
                applyFilter();
            }
        };
        action.setChecked(includeMembers);
        menuManager.add(action);
        super.fillViewMenu(menuManager);
    }
    
}
