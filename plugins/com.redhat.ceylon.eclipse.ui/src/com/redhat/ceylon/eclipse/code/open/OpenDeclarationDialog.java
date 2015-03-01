package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.compiler.typechecker.model.Util.isNameMatching;
import static com.redhat.ceylon.compiler.typechecker.model.Util.isOverloadedVersion;
import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getQualifiedDescriptionFor;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getLinkedModel;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.addPageEpilog;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.insertPageProlog;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getModuleLabel;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.OPEN_FILTERS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.util.Highlights.PACKAGE_STYLER;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.PreferencesUtil;

import com.redhat.ceylon.cmr.api.JDKUtils;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.Referenceable;
import com.redhat.ceylon.compiler.typechecker.model.Unit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.html.HTML;
import com.redhat.ceylon.eclipse.code.preferences.CeylonOpenDialogsPreferencePage;
import com.redhat.ceylon.eclipse.code.search.FindAssignmentsAction;
import com.redhat.ceylon.eclipse.code.search.FindReferencesAction;
import com.redhat.ceylon.eclipse.code.search.FindRefinementsAction;
import com.redhat.ceylon.eclipse.code.search.FindSubtypesAction;
import com.redhat.ceylon.eclipse.core.model.CrossProjectSourceFile;
import com.redhat.ceylon.eclipse.core.model.EditedSourceFile;
import com.redhat.ceylon.eclipse.core.model.IResourceAware;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.core.model.ProjectSourceFile;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.DocBrowser;
import com.redhat.ceylon.eclipse.util.EditorUtil;

public class OpenDeclarationDialog extends FilteredItemsSelectionDialog {
    
    private static final String SHOW_SELECTION_MODULE = "showSelectionModule";
    private static final String SHOW_SELECTION_PACKAGE = "showSelectionPackage";
    private static final String EXCLUDE_DEPRECATED = "excludeDeprecated";
    private static final String EXCLUDE_JDK = "excludeJDK";
    private static final String EXCLUDE_ORA_JDK = "excludeOracleJDK";

//    private static final Image MEMBERS_IMAGE = 
//            CeylonPlugin.getInstance().getImageRegistry().get(CeylonResources.SHOW_MEMBERS);
    
    private static final String SETTINGS_ID = 
            CeylonPlugin.PLUGIN_ID + ".openDeclarationDialog";
    
    private boolean includeMembers;
    private boolean excludeDeprecated;
    private boolean excludeJDK;
    private boolean excludeOracleJDK;
    
    private int filterVersion = 0;
    
    private boolean showSelectionPackage;
    private boolean showSelectionModule;
    
    private TogglePackageAction togglePackageAction;
    private ToggleModuleAction toggleModuleAction;
    private ToggleExcludeDeprecatedAction toggleExcludeDeprecatedAction;
    private ToggleExcludeJDKAction toggleExcludeJDKAction;
    private ToggleExcludeOracleJDKAction toggleExcludeOracleJDKAction;
    
//    private ToolItem toggleMembersToolItem;
//    private ToggleMembersAction toggleMembersAction;
    
    protected String emptyDoc;
    
    @Override
    protected void applyFilter() {
        includeMembers = getPatternControl().getText().contains(".");
//        toggleMembersAction.setChecked(includeMembers);
        super.applyFilter();
    }
    
//    private final class ToggleMembersAction extends Action {
//        private ToggleMembersAction() {
//            super("Include Member Declarations", IAction.AS_CHECK_BOX);
//            setImageDescriptor(CeylonPlugin.getInstance().getImageRegistry().getDescriptor(CeylonResources.SHOW_MEMBERS));
//        }
//
//        @Override
//        public void run() {
//            includeMembers=!includeMembers;
//            applyFilter();
////            if (toggleMembersToolItem!=null) {
////                toggleMembersToolItem.setSelection(includeMembers);
////            }
//        }
//    }

    private class ToggleExcludeDeprecatedAction extends Action {
        ToggleExcludeDeprecatedAction() {
            super("Exclude Deprecated Declarations", AS_CHECK_BOX);
        }
        @Override
        public void run() {
            excludeDeprecated=!excludeDeprecated;
            filterVersion++;
            applyFilter();
        }
    }

    private class ToggleExcludeJDKAction extends Action {
        ToggleExcludeJDKAction() {
            super("Exclude Java SDK", AS_CHECK_BOX);
        }
        @Override
        public void run() {
            excludeJDK=!excludeJDK;
            filterVersion++;
            applyFilter();
        }
    }

    private class ToggleExcludeOracleJDKAction extends Action {
        ToggleExcludeOracleJDKAction() {
            super("Exclude Java SDK Internals", AS_CHECK_BOX);
        }
        @Override
        public void run() {
            excludeOracleJDK=!excludeOracleJDK;
            filterVersion++;
            applyFilter();
        }
    }

    private class TogglePackageAction extends Action {
        private TogglePackageAction() {
            super("Show Selection Package", IAction.AS_CHECK_BOX);
            setImageDescriptor(CeylonPlugin.getInstance()
                    .getImageRegistry().getDescriptor(CeylonResources.CEYLON_PACKAGE));
        }
        @Override
        public void run() {
            showSelectionPackage = !showSelectionPackage;
            refresh();
        }
    }

    private class ToggleModuleAction extends Action {
        private ToggleModuleAction() {
            super("Show Selection Module", IAction.AS_CHECK_BOX);
            setImageDescriptor(CeylonPlugin.getInstance()
                    .getImageRegistry().getDescriptor(CeylonResources.CEYLON_MODULE));
        }
        @Override
        public void run() {
            showSelectionModule = !showSelectionModule;
            refresh();
        }
    }
    
    protected void restoreDialog(IDialogSettings settings) {
        super.restoreDialog(settings);
        
        if (settings.get(SHOW_SELECTION_PACKAGE)!=null) {
            showSelectionPackage = settings.getBoolean(SHOW_SELECTION_PACKAGE);
        }
        if (settings.get(SHOW_SELECTION_MODULE)!=null) {
            showSelectionModule = settings.getBoolean(SHOW_SELECTION_MODULE);
        }
        if (settings.get(EXCLUDE_DEPRECATED)!=null) {
            excludeDeprecated = settings.getBoolean(EXCLUDE_DEPRECATED);
        }
        if (settings.get(EXCLUDE_JDK)!=null) {
            excludeJDK = settings.getBoolean(EXCLUDE_JDK);
        }
        if (settings.get(EXCLUDE_ORA_JDK)!=null) {
            excludeOracleJDK = settings.getBoolean(EXCLUDE_ORA_JDK);
        }
        else {
            excludeOracleJDK = true;
        }
        
        if (togglePackageAction!=null) {
            togglePackageAction.setChecked(showSelectionPackage);
        }
        if (toggleModuleAction!=null) {
            toggleModuleAction.setChecked(showSelectionModule);
        }
        if (toggleExcludeDeprecatedAction!=null) {
            toggleExcludeDeprecatedAction.setChecked(excludeDeprecated);
        }
        if (toggleExcludeJDKAction!=null) {
            toggleExcludeJDKAction.setChecked(excludeJDK);
        }
        if (toggleExcludeOracleJDKAction!=null) {
            toggleExcludeOracleJDKAction.setChecked(excludeOracleJDK);
        }
    }
    
    protected void storeDialog(IDialogSettings settings) {
        super.storeDialog(settings);
        settings.put(SHOW_SELECTION_MODULE, showSelectionModule);
        settings.put(SHOW_SELECTION_PACKAGE, showSelectionPackage);
        settings.put(EXCLUDE_DEPRECATED, excludeDeprecated);
        settings.put(EXCLUDE_JDK, excludeJDK);
        settings.put(EXCLUDE_ORA_JDK, excludeOracleJDK);
    }
    
    private static Declaration toDeclaration(Object object) {
        if (object instanceof DeclarationProxy) {
            return ((DeclarationProxy) object).declaration;
        }
        else {
            return null;
        }
    }
    
    public class Filter extends ItemsFilter {
        boolean members = includeMembers;
        boolean filterDeprecated = excludeDeprecated;
        boolean filterJDK = excludeJDK;
        boolean filterOracleJDK = excludeOracleJDK;
        int version = filterVersion;
        
        @Override
        public boolean matchItem(Object item) {
            Declaration declaration = toDeclaration(item);
            Module module = declaration.getUnit().getPackage().getModule();
            if (filterJDK && 
                    module instanceof JDTModule &&
                    JDKUtils.isJDKModule(((JDTModule) module).getNameAsString())) {
                return false;
            }
            if (filterOracleJDK && 
                    module instanceof JDTModule &&
                    JDKUtils.isOracleJDKModule(((JDTModule) module).getNameAsString())) {
                return false;
            }
            if (filterDeprecated && declaration.isDeprecated()) {
                return false;
            }
            String pattern = getPattern();
            int loc = pattern.indexOf('.');
            if (loc<0) {
                String name = declaration.getName();
                if (name==null) {
                    return false;
                }
                else if (pattern.contains("*")) {
                    return isMatchingGlob(pattern, name);
                }
                else {
                    return isNameMatching(pattern, name);
                }
            }
            else {
                if (declaration.isClassOrInterfaceMember()) {
                    String typePattern = pattern.substring(0,loc);
                    String memberPattern = pattern.substring(loc+1);
                    return isNameMatching(memberPattern, declaration) &&
                            isNameMatching(typePattern, 
                                    (Declaration) declaration.getContainer());
                }
                else {
                    return false;
                }
            }
        }

        @Override
        public boolean isConsistentItem(Object item) {
            return true;
        }

        @Override
        public boolean equalsFilter(ItemsFilter filter) {
            if (!(filter instanceof Filter) ||
                    members!=((Filter) filter).members ||
                    version!=((Filter) filter).version) {
                return false;
            }
            else {
                return filter.getPattern().equals(getPattern());
            }
        }
        
        @Override
        public boolean isSubFilter(ItemsFilter filter) {
            if (!(filter instanceof Filter) ||
                    members!=((Filter) filter).members ||
                    version!=((Filter) filter).version) {
                return false;
            }
            else {
                String pattern = getPattern();
                String filterPattern = filter.getPattern();
                int loc = pattern.indexOf('.');
                int filterLoc = filterPattern.indexOf('.');
                if (loc<0) {
                    return filterLoc<0 &&
                            filterPattern.startsWith(pattern);
                }
                else {
                    return filterLoc>=0 &&
                            filterPattern.substring(filterLoc+1)
                            .startsWith(pattern.substring(loc+1)) &&
                            filterPattern.substring(0,filterLoc)
                            .startsWith(pattern.substring(0,loc));
                }
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
            if (showSelectionPackage||showSelectionModule) {
                Declaration dec = toDeclaration(element);
                if (dec!=null) {
                    try {
                        if (!nameOccursMultipleTimes(dec)) {
                            if (showSelectionPackage) {
                                text += " - " + getPackageLabel(dec);
                            }
                            if (showSelectionModule) {
                                text += " - " + getModule(dec);
                            }
                        }
                    }
                    catch (Exception e) {
                        System.err.println(dec.getName());
                        e.printStackTrace();
                    }
                }
            }
            return text;
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
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                try {
                    return getPackageLabel(dec) /*+ " - " + getLocation(dwp)*/;
                }
                catch (Exception e) {
                    System.err.println(dec.getName());
                    e.printStackTrace();
                    return "";
                }
            }
            else if (element instanceof String) {
                return (String) element;
            }
            else {
                return "";
            }
        }

        @Override
        public Image getImage(Object element) {
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                return CeylonResources.PACKAGE;
            }
            else {
                return null;
            }
        }
    }

    static class MoreDetailsLabelProvider implements ILabelProvider {
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
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                try {
                    return getModuleLabel(dec)/* + " - " + getLocation(dwp)*/;
                }
                catch (Exception e) {
                    System.err.println(dec.getName());
                    e.printStackTrace();
                    return "";
                }
            }
            else if (element instanceof String) {
                return (String) element;
            }
            else {
                return "";
            }
        }

        @Override
        public Image getImage(Object element) {
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                return CeylonResources.MODULE;
            }
            else {
                return null;
            }
        }
    }

    static class EvenMoreDetailsLabelProvider implements ILabelProvider {
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
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                try {
                    return getLocation(dec);
                }
                catch (Exception e) {
                    System.err.println(dec.getName());
                    e.printStackTrace();
                    return "";
                }
            }
            else if (element instanceof String) {
                return (String) element;
            }
            else {
                return "";
            }
        }

        @Override
        public Image getImage(Object element) {
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                return getLocationImage(dec);
            }
            else {
                return null;
            }
        }
    }

    class LabelProvider 
            extends StyledCellLabelProvider 
            implements DelegatingStyledCellLabelProvider.IStyledLabelProvider, 
                       ILabelProvider {
        
        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }
        
        @Override
        public void removeListener(ILabelProviderListener listener) {}
        
        @Override
        public Image getImage(Object element) {
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                try {
                    return getImageForDeclaration(dec);
                }
                catch (Exception e) {
                    System.err.println(dec.getName());
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
            return getStyledText(element).getString();
        }

        @Override
        public StyledString getStyledText(Object element) {
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                try {
                    IPreferenceStore prefs = EditorUtil.getPreferences();
                    StyledString label = 
                            getQualifiedDescriptionFor(dec,
                                    prefs.getBoolean(TYPE_PARAMS_IN_DIALOGS),
                                    prefs.getBoolean(PARAMS_IN_DIALOGS),
                                    prefs.getBoolean(PARAM_TYPES_IN_DIALOGS),
                                    prefs.getBoolean(RETURN_TYPES_IN_DIALOGS));
                    if (nameOccursMultipleTimes(dec)) {
                        label.append(" - ", PACKAGE_STYLER)
                             .append(getPackageLabel(dec), PACKAGE_STYLER)
                             .append(" - ", COUNTER_STYLER)
                             .append(getModule(dec), COUNTER_STYLER);
                    }
                    return label;
                }
                catch (Exception e) {
                    System.err.println(dec.getName());
                    e.printStackTrace();
                    return new StyledString(dec.getName());
                }
            }
            else {
                return new StyledString();
            }
        }

        @Override
        public void update(ViewerCell cell) {
            Object element = cell.getElement();
            if (element instanceof DeclarationProxy) {
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
        
        protected DeclarationProxy restoreItemFromMemento(IMemento element) {
            String qualifiedName = element.getString("qualifiedName");
            String unitFileName = element.getString("unitFileName");
            String packageName = element.getString("packageName");
            String projectName = element.getString("projectName");
            
            for (IProject project: getProjects()) {
                if (projectName!=null && 
                        project.getName().equals(projectName)) {
                    //search for a source file in the project
                    for (PhasedUnit unit: getUnits(project)) {
                        String filename = unit.getUnit().getFilename();
                        String pname = unit.getPackage().getQualifiedNameString();
                        if (filename.equals(unitFileName) && 
                                pname.equals(packageName)) {
                            for (Declaration dec: unit.getDeclarations()) {
                                try {
                                    if (isPresentable(dec) && 
                                            qualifiedName.equals(dec.getQualifiedNameString())) {
                                        return isFiltered(dec) ? null : new DeclarationProxy(dec);
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                else {
                    //for archives, search all dependent modules
                    //this will find declarations in src archives
                    Modules modules = getProjectTypeChecker(project)
                            .getContext().getModules();
                    for (Module module: modules.getListOfModules()) {
                        if (module.isJava() || //TODO: is this correct
                                packageName.startsWith(module.getNameAsString())) {
                            for (Package pkg: module.getAllPackages()) { 
                                if (pkg.getQualifiedNameString().equals(packageName)) {
                                    for (Declaration dec: pkg.getMembers()) {
                                        if (isPresentable(dec) && 
                                                qualifiedName.equals(dec.getQualifiedNameString())) {
                                            return isFiltered(dec) ? null : new DeclarationProxy(dec);
                                        }
                                        //TODO: members!
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
            Declaration dec = toDeclaration(item);
            Unit unit = dec.getUnit();
            element.putString("qualifiedName", 
                    dec.getQualifiedNameString());
            element.putString("unitFileName", 
                    unit.getFilename());
            element.putString("packageName", 
                    unit.getPackage().getQualifiedNameString());
            if (unit instanceof ProjectSourceFile) {
                ProjectSourceFile projectSourceFile = 
                        (ProjectSourceFile) unit;
                element.putString("projectName", 
                        projectSourceFile.getProjectResource().getName());
            }
        }
        
     }
    
    public OpenDeclarationDialog(boolean multi, boolean history, 
            Shell shell, String title, 
            String filterLabelText, String listLabelText) {
        super(shell, multi, filterLabelText, listLabelText);
        setTitle(title);
        initLabelProviders(new LabelProvider(), new SelectionLabelDecorator(),
                new DetailsLabelProvider(), new MoreDetailsLabelProvider(),
                new EvenMoreDetailsLabelProvider());
        if (history) {
            setSelectionHistory(new TypeSelectionHistory());
        }
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
            public int compare(Object x, Object y) {
                return compareDeclarations(toDeclaration(x), 
                        toDeclaration(y));
            }
            private int compareDeclarations(Declaration p, 
                    Declaration q) {
                int result = p.getName().compareTo(q.getName());
                if (result!=0) {
                    return result;
                }
                else if (p.isClassOrInterfaceMember() && 
                        !q.isClassOrInterfaceMember()) {
                    return 1;
                }
                else if (!p.isClassOrInterfaceMember() && 
                        q.isClassOrInterfaceMember()) {
                    return -1;
                }
                else if (p.isClassOrInterfaceMember() && 
                        q.isClassOrInterfaceMember()) {
                    return compareDeclarations(
                            (Declaration) p.getContainer(), 
                            (Declaration) q.getContainer());
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
            ItemsFilter itemsFilter, IProgressMonitor monitor) 
                    throws CoreException {
        usedNames.clear();
        monitor.beginTask("Filtering", estimateWork(monitor));
        Set<String> searchedArchives = new HashSet<String>();
        Collection<IProject> projects = getProjects();
        for (IProject project: projects) {
            TypeChecker typeChecker = getProjectTypeChecker(project);
            fill(contentProvider, itemsFilter, 
                    typeChecker.getPhasedUnits().getPhasedUnits());
            monitor.worked(1);
            if (monitor.isCanceled()) break;
            Modules modules = typeChecker.getContext().getModules();
            for (Module m: modules.getListOfModules()) {
                if (m instanceof JDTModule) {
                    JDTModule module = (JDTModule) m;
                    if ((!excludeJDK || !JDKUtils.isJDKModule(module.getNameAsString())) &&
                        (!excludeOracleJDK || !JDKUtils.isOracleJDKModule(module.getNameAsString())) &&
                            searchedArchives.add(uniqueIdentifier(module))) {
                        fill(contentProvider, itemsFilter, module, monitor);
                        monitor.worked(1);
                        if (monitor.isCanceled()) break;
                    }
                }
            }
        }
        monitor.done();
    }

    private void fill(AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, JDTModule module, 
            IProgressMonitor monitor) {
        for (Package pack: new ArrayList<Package>(module.getPackages())) {
            if (!isFiltered(pack)) {
                for (Declaration dec: pack.getMembers()) {
                    fillDeclarationAndMembers(contentProvider, 
                            itemsFilter, module, dec);
                }
            }
            monitor.worked(1);
            if (monitor.isCanceled()) break;
        }
    }
    
    protected static class DeclarationProxy {
        private Declaration declaration;
        private String location;
        public DeclarationProxy(Declaration declaration) {
            this.declaration = declaration;
            location = getLocation(declaration);
        }
        @Override
        public boolean equals(Object obj) {
            if (this==obj) {
                return true;
            }
            else if (obj instanceof DeclarationProxy) {
                DeclarationProxy that = (DeclarationProxy) obj;
                if (declaration.equals(that.declaration)) {
                    return location==that.location || 
                            location!=null && that.location!=null &&
                            location.equals(that.location);
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }
        @Override
        public int hashCode() {
            return declaration.hashCode() + location.hashCode();
        }
    }

    private void fillDeclarationAndMembers(
            AbstractContentProvider contentProvider, 
            ItemsFilter itemsFilter,
            JDTModule module, Declaration dec) {
        if (includeDeclaration(module, dec) &&
                //watch out for dupes!
                (!module.isProjectModule() || 
                 !dec.getUnit().getFilename().endsWith("ceylon"))) {
            contentProvider.add(new DeclarationProxy(dec), itemsFilter);
            nameOccurs(dec);
            if (includeMembers && dec instanceof ClassOrInterface) {
                try {
                    for (Declaration member: 
                            new ArrayList<Declaration>(dec.getMembers())) {
                        fillDeclarationAndMembers(contentProvider, 
                                itemsFilter, module, member);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void fill(AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, 
            List<? extends PhasedUnit> units) {
        for (PhasedUnit unit: units) {
            JDTModule jdtModule = 
                    (JDTModule) unit.getPackage().getModule();
            for (Declaration dec: unit.getDeclarations()) {
                if (includeDeclaration(jdtModule, dec)) {
                    contentProvider.add(new DeclarationProxy(dec), itemsFilter);
                    nameOccurs(dec);
                }
            }
            
        }
    }

    protected String getFilterListAsString() {
        return EditorUtil.getPreferences().getString(OPEN_FILTERS);
    }

    private List<Pattern> filters;
    private List<Pattern> packageFilters;
    { 
        filters = new ArrayList<Pattern>();
        packageFilters = new ArrayList<Pattern>();
        String filtersString = getFilterListAsString();
        if (!filtersString.trim().isEmpty()) {
            String[] regexes = filtersString
                    .replaceAll("\\(\\w+\\)", "")
                    .replace(".", "\\.")
                    .replace("*", ".*")
                    .split(",");
            for (String regex: regexes) {
                regex = regex.trim();
                if (!regex.isEmpty()) {
                    filters.add(Pattern.compile(regex));
                    if (regex.endsWith("::*")) {
                        regex = regex.substring(0, regex.length()-3);
                    }
                    if (!regex.contains("::")) {
                        packageFilters.add(Pattern.compile(regex));
                    }
                }
            }
        } 
    }

    private boolean isFiltered(Declaration declaration) {
        if (excludeDeprecated && declaration.isDeprecated()) {
            return true;
        }
        if (declaration.isAnnotation() &&
                declaration.getName().contains("__")) {
            //actually what we should really do is filter
            //out all constructors for Java annotations
            return true;
        }
        if (!filters.isEmpty()) {
            String name = declaration.getQualifiedNameString();
            for (Pattern filter: filters) {
                if (filter.matcher(name).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFiltered(Package pack) {
        if (!packageFilters.isEmpty()) {
            String name = pack.getNameAsString();
            for (Pattern filter: filters) {
                if (filter.matcher(name).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean includeDeclaration(JDTModule module, Declaration dec) {
        try {
            boolean visibleFromSourceModules;
            if (dec.isToplevel()) {
                visibleFromSourceModules = 
                        dec.isShared() || module.isProjectModule();
            }
            else {
                visibleFromSourceModules = 
                        includeMembers && dec.isShared();
            }
            return visibleFromSourceModules && 
                    isPresentable(dec) && 
                    !isFiltered(dec);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private int estimateWork(IProgressMonitor monitor) {
        int work = 0;
        Set<String> searchedArchives = 
                new HashSet<String>();
        for (IProject project: getProjects()) {
            work++;
            Modules modules = 
                    getProjectTypeChecker(project)
                        .getContext().getModules();
            for (Module m: modules.getListOfModules()) {
                if (m instanceof JDTModule) {
                    JDTModule module = (JDTModule) m;
                    if ((!excludeJDK || !JDKUtils.isJDKModule(module.getNameAsString())) &&
                        (!excludeOracleJDK || !JDKUtils.isOracleJDKModule(module.getNameAsString())) &&
                            searchedArchives.add(uniqueIdentifier(module))) {
                        work += 1 + m.getPackages().size();
                    }
                }
            }
        }
        return work;
    }
    
    private String uniqueIdentifier(JDTModule module) {
        return module.getArtifact()==null ?
                module.getNameAsString() + '#' + module.getVersion() :
                module.getArtifact().getAbsolutePath();
    }

    private static String getModule(Declaration dec) {
        Module module = dec.getUnit()
                .getPackage().getModule();
        StringBuilder sb = new StringBuilder();
        sb.append(module.getNameAsString());
        if (module.getVersion()!=null) {
            sb.append(" \"")
              .append(module.getVersion())
              .append("\"");
        }
        return sb.toString();
    }
    
    private static Image getLocationImage(Declaration dwp) {
        Module module = dwp.getUnit().getPackage().getModule();
        if (module instanceof JDTModule) {
            JDTModule m = (JDTModule) module;
            if (m.isProjectModule()) {
//                IProject project = dwp.getProject();
//                if (project.isOpen()) {
                    return CeylonResources.FILE;
//                }
//                else {
//                    return CeylonResources.PROJECT;
//                }
            }
            else {
                return CeylonResources.REPO;
            }
        }
        else {
            return null;
        }
    }

    private static String getLocation(Declaration declaration) {
        Unit unit = declaration.getUnit();
        Module module = unit.getPackage().getModule();
        if (module instanceof JDTModule) {
            JDTModule m = (JDTModule) module;
            if (unit instanceof EditedSourceFile ||
                unit instanceof ProjectSourceFile ||
                unit instanceof CrossProjectSourceFile ) {
                IResourceAware sourceFile = (IResourceAware) unit;
                return sourceFile.getFileResource().getFullPath()
                        .toPortableString();
            }
            else {
                String displayString = m.getRepositoryDisplayString();
                File repository = 
                        CeylonPlugin.getInstance().getCeylonRepository();
                if (repository.getPath().equals(displayString)) {
                    displayString = "IDE System Modules";
                }
                return displayString;
            }
        }
        else {
            return null;
        }
    }
    
    private String toName(Declaration dec) {
        String name = dec.getName();
        if (dec.isClassOrInterfaceMember()) {
            name = ((Declaration) dec.getContainer()).getName() + 
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
        return name!=null && 
                !d.isAnonymous() && 
                !isOverloadedVersion(d);
    }
    
    @Override
    public String getElementName(Object item) {
        return toDeclaration(item).getQualifiedNameString();
    }
    
    @Override
    protected void fillViewMenu(IMenuManager menuManager) {
//        toggleMembersAction = new ToggleMembersAction();
//        toggleMembersAction.setChecked(includeMembers);
//        menuManager.add(toggleMembersAction);
        
        toggleExcludeDeprecatedAction = new ToggleExcludeDeprecatedAction();
        toggleExcludeDeprecatedAction.setChecked(excludeDeprecated);
        menuManager.add(toggleExcludeDeprecatedAction);
        
        toggleExcludeJDKAction = new ToggleExcludeJDKAction();
        toggleExcludeJDKAction.setChecked(excludeJDK);
        menuManager.add(toggleExcludeJDKAction);
        
        toggleExcludeOracleJDKAction = new ToggleExcludeOracleJDKAction();
        toggleExcludeOracleJDKAction.setChecked(excludeOracleJDK);
        menuManager.add(toggleExcludeOracleJDKAction);
        
        menuManager.add(new Separator());
        
        super.fillViewMenu(menuManager);
        
        togglePackageAction = new TogglePackageAction();
        toggleModuleAction = new ToggleModuleAction();
        menuManager.add(togglePackageAction);
        menuManager.add(toggleModuleAction);
        
        menuManager.add(new Separator());
        
        Action configureAction = 
                new Action("Configure Filters and Labels...",
                        CeylonPlugin.getInstance().getImageRegistry()
                        .getDescriptor(CeylonResources.CONFIG_LABELS)) {
            @Override
            public void run() {
                PreferencesUtil.createPreferenceDialogOn(getShell(), 
                        CeylonOpenDialogsPreferencePage.ID, 
                        new String[] {CeylonOpenDialogsPreferencePage.ID}, 
                        null).open();
                filters = new ArrayList<Pattern>();
                packageFilters = new ArrayList<Pattern>();
                String filtersString = getFilterListAsString();
                if (!filtersString.trim().isEmpty()) {
                    String[] regexes = filtersString
                            .replaceAll("\\(\\w+\\)", "")
                            .replace(".", "\\.")
                            .replace("*", ".*")
                            .split(",");
                    for (String regex: regexes) {
                        filters.add(Pattern.compile(regex));
                        if (regex.endsWith("::*")) {
                            String pregex = regex.substring(0, regex.length()-3);
                            packageFilters.add(Pattern.compile(pregex));
                        }
                    }
                }
                filterVersion++;
                applyFilter();
            }
        };
        menuManager.add(configureAction);
    }

    @Override
    protected void refreshBrowserContent(DocBrowser browser,
            Object[] selection) {
        if (browser.isVisible()) {
            try {
                if (selection!=null &&
                        selection.length==1 &&
                        selection[0] instanceof DeclarationProxy) {
                    browser.setText(getDocumentationFor(null, 
                            toDeclaration(selection[0])));
                }
                else {
                    if (emptyDoc==null) {
                        StringBuilder buffer = new StringBuilder();
                        insertPageProlog(buffer, 0, HTML.getStyleSheet());
                        buffer.append("<i>Select a declaration to see its documentation here.</i>");
                        addPageEpilog(buffer);
                        emptyDoc = buffer.toString();
                    }
                    browser.setText(emptyDoc);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isMatchingGlob(String filter, String name) {
        if (name==null) {
            return false;
        }
        int loc = 0;
        boolean first = true;
        for (String subfilter: filter.split("\\*")) {
            int match = name.toLowerCase().indexOf(subfilter.toLowerCase(), loc);
            if (match<0 || first && match>0) {
                return false;
            }
            loc += match + subfilter.length();
            first = false;
        }
        return true;
    }
    
    /*@Override
    protected void createToolBar(ToolBar toolBar) {
        super.createToolBar(toolBar);
        
        toggleMembersToolItem = new ToolItem(toolBar, SWT.CHECK, 0);
        toggleMembersToolItem.setImage(MEMBERS_IMAGE);
        toggleMembersToolItem.setToolTipText("Show/Hide Members");
        toggleMembersToolItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                includeMembers=!includeMembers;
                applyFilter();
                if (toggleMembersAction!=null) {
                    toggleMembersAction.setChecked(includeMembers);
                }
            }
        });
        
    }*/
    
    @Override
    void handleLink(String location, DocBrowser browser) {
        Referenceable target = null;
        CeylonEditor editor = null;
        IEditorPart currentEditor = EditorUtil.getCurrentEditor();
        if (currentEditor instanceof CeylonEditor) {
            editor = (CeylonEditor) currentEditor;
            target = getLinkedModel(editor, location);
            if (location.startsWith("ref:")) {
                new FindReferencesAction(editor, (Declaration) target).run();
                close();
                return;
            }
            else if (location.startsWith("sub:")) {
                new FindSubtypesAction(editor, (Declaration) target).run();
                close();
                return;
            }
            else if (location.startsWith("act:")) {
                new FindRefinementsAction(editor, (Declaration) target).run();
                close();
                return;
            }
            else if (location.startsWith("ass:")) {
                new FindAssignmentsAction(editor, (Declaration) target).run();
                close();
                return;
            }
        }
        if (location.startsWith("doc:")) {
            if (target==null) {
                target = getLinkedModel(location);
            }
            if (target instanceof Declaration) {
                String text = getDocumentationFor(null, (Declaration) target);
                if (text!=null) browser.setText(text);
            }
            if (target instanceof Package) {
                String text = getDocumentationFor(null, (Package) target);
                if (text!=null) browser.setText(text);
            }
            if (target instanceof Module) {
                String text = getDocumentationFor(null, (Module) target);
                if (text!=null) browser.setText(text);
            }
        }
        if (location.startsWith("dec:")) {
            if (target==null) {
                target = getLinkedModel(location);
            }
            if (target!=null) {
                gotoDeclaration(target);
                close();
                return;
            }
        }
    }
    
    @Override
    public Declaration[] getResult() {
        Object[] proxies = super.getResult();
        if (proxies==null) {
            return null;
        }
        else {
            Declaration[] declarations = new Declaration[proxies.length];
            for (int i = 0; i < proxies.length; i++) {
                declarations[i] = toDeclaration(proxies[i]);
            }
            return declarations;
        }
    }

}
