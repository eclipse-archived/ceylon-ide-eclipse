package com.redhat.ceylon.eclipse.code.open;

import static com.redhat.ceylon.eclipse.code.complete.CodeCompletions.getQualifiedDescriptionFor;
import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoDeclaration;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getDocumentationFor;
import static com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getLinkedModel;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.addPageEpilog;
import static com.redhat.ceylon.eclipse.code.html.HTMLPrinter.insertPageProlog;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getImageForDeclaration;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getModuleLabel;
import static com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider.getPackageLabel;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAMS_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.PARAM_TYPES_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.RETURN_TYPES_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.code.preferences.CeylonPreferenceInitializer.TYPE_PARAMS_IN_DIALOGS;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.hoverJ2C;
import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.modelJ2C;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.getOpenDialogFont;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_MODULE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CEYLON_PACKAGE;
import static com.redhat.ceylon.eclipse.ui.CeylonResources.CONFIG_LABELS;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getCurrentEditor;
import static com.redhat.ceylon.eclipse.util.Highlights.PACKAGE_STYLER;
import static com.redhat.ceylon.eclipse.util.InteropUtils.toJavaString;
import static com.redhat.ceylon.model.cmr.JDKUtils.isJDKModule;
import static com.redhat.ceylon.model.cmr.JDKUtils.isOracleJDKModule;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isNameMatching;
import static com.redhat.ceylon.model.typechecker.model.ModelUtil.isOverloadedVersion;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
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

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.eclipse.code.editor.CeylonEditor;
import com.redhat.ceylon.eclipse.code.html.HTML;
import com.redhat.ceylon.eclipse.code.preferences.CeylonFiltersPreferencePage;
import com.redhat.ceylon.eclipse.code.preferences.CeylonOpenDialogsPreferencePage;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.ui.CeylonResources;
import com.redhat.ceylon.eclipse.util.DocBrowser;
import com.redhat.ceylon.eclipse.util.Filters;
import com.redhat.ceylon.eclipse.util.ModelProxy;
import com.redhat.ceylon.ide.common.doc.DocGenerator;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.CrossProjectSourceFile;
import com.redhat.ceylon.ide.common.model.EditedSourceFile;
import com.redhat.ceylon.ide.common.model.IResourceAware;
import com.redhat.ceylon.ide.common.model.IdeModule;
import com.redhat.ceylon.ide.common.model.JavaCompilationUnit;
import com.redhat.ceylon.ide.common.model.ProjectSourceFile;
import com.redhat.ceylon.model.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.Unit;

public class OpenDeclarationDialog extends FilteredItemsSelectionDialog {
    
    private static final String SHOW_SELECTION_MODULE = 
            "showSelectionModule";
    private static final String SHOW_SELECTION_PACKAGE = 
            "showSelectionPackage";
    private static final String EXCLUDE_DEPRECATED = 
            "excludeDeprecated";
    private static final String EXCLUDE_JDK = 
            "excludeJDK";
    private static final String EXCLUDE_ORA_JDK = 
            "excludeOracleJDK";

//    private static final Image MEMBERS_IMAGE = 
//            CeylonPlugin.getInstance().getImageRegistry().get(CeylonResources.SHOW_MEMBERS);
    
    private static final String SETTINGS_ID = 
            CeylonPlugin.PLUGIN_ID 
                + ".openDeclarationDialog";
    
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
        includeMembers = 
                getPatternControl().getText()
                    .contains(".");
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

    private class ToggleExcludeDeprecatedAction 
            extends Action {
        ToggleExcludeDeprecatedAction() {
            super("Exclude Deprecated Declarations", 
                    AS_CHECK_BOX);
        }
        @Override
        public void run() {
            excludeDeprecated=!excludeDeprecated;
            filterVersion++;
            applyFilter();
        }
    }

    private class ToggleExcludeJDKAction 
            extends Action {
        ToggleExcludeJDKAction() {
            super("Exclude Java SDK", 
                    AS_CHECK_BOX);
        }
        @Override
        public void run() {
            excludeJDK=!excludeJDK;
            filterVersion++;
            applyFilter();
        }
    }

    private class ToggleExcludeOracleJDKAction 
            extends Action {
        ToggleExcludeOracleJDKAction() {
            super("Exclude Java SDK Internals", 
                    AS_CHECK_BOX);
        }
        @Override
        public void run() {
            excludeOracleJDK=!excludeOracleJDK;
            filterVersion++;
            applyFilter();
        }
    }

    private class TogglePackageAction 
            extends Action {
        private TogglePackageAction() {
            super("Show Selection Package", 
                    AS_CHECK_BOX);
            ImageDescriptor desc = 
                    CeylonPlugin.imageRegistry()
                        .getDescriptor(CEYLON_PACKAGE);
            setImageDescriptor(desc);
        }
        @Override
        public void run() {
            showSelectionPackage = !showSelectionPackage;
            refresh();
        }
    }

    private class ToggleModuleAction extends Action {
        private ToggleModuleAction() {
            super("Show Selection Module", AS_CHECK_BOX);
            ImageDescriptor desc = 
                    CeylonPlugin.imageRegistry()
                        .getDescriptor(CEYLON_MODULE);
            setImageDescriptor(desc);
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
            showSelectionPackage = 
                    settings.getBoolean(
                            SHOW_SELECTION_PACKAGE);
        }
        if (settings.get(SHOW_SELECTION_MODULE)!=null) {
            showSelectionModule = 
                    settings.getBoolean(
                            SHOW_SELECTION_MODULE);
        }
        if (settings.get(EXCLUDE_DEPRECATED)!=null) {
            excludeDeprecated = 
                    settings.getBoolean(EXCLUDE_DEPRECATED);
        }
        if (settings.get(EXCLUDE_JDK)!=null) {
            excludeJDK = 
                    settings.getBoolean(EXCLUDE_JDK);
        }
        if (settings.get(EXCLUDE_ORA_JDK)!=null) {
            excludeOracleJDK = 
                    settings.getBoolean(EXCLUDE_ORA_JDK);
        }
        else {
            excludeOracleJDK = true;
        }
        
        if (togglePackageAction!=null) {
            togglePackageAction.setChecked(
                    showSelectionPackage);
        }
        if (toggleModuleAction!=null) {
            toggleModuleAction.setChecked(
                    showSelectionModule);
        }
        if (toggleExcludeDeprecatedAction!=null) {
            toggleExcludeDeprecatedAction.setChecked(
                    excludeDeprecated);
        }
        if (toggleExcludeJDKAction!=null) {
            toggleExcludeJDKAction.setChecked(
                    excludeJDK);
        }
        if (toggleExcludeOracleJDKAction!=null) {
            toggleExcludeOracleJDKAction.setChecked(
                    excludeOracleJDK);
        }
    }
    
    protected void storeDialog(IDialogSettings settings) {
        super.storeDialog(settings);
        settings.put(SHOW_SELECTION_MODULE, 
                showSelectionModule);
        settings.put(SHOW_SELECTION_PACKAGE, 
                showSelectionPackage);
        settings.put(EXCLUDE_DEPRECATED, 
                excludeDeprecated);
        settings.put(EXCLUDE_JDK, 
                excludeJDK);
        settings.put(EXCLUDE_ORA_JDK, 
                excludeOracleJDK);
    }
    
    private static Declaration toDeclaration(Object object) {
        if (object instanceof DeclarationProxy) {
            DeclarationProxy proxy = 
                    (DeclarationProxy) object;
            return proxy.get();
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
            Unit unit = declaration.getUnit();
            Module module = unit.getPackage().getModule();
            String moduleName = module.getNameAsString();
            if (filterJDK && 
                    isJDKModule(moduleName) ||
                filterOracleJDK && 
                    isOracleJDKModule(moduleName) ||
                filterDeprecated && 
                    declaration.isDeprecated()) {
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
                    String typePattern = 
                            pattern.substring(0,loc);
                    String memberPattern = 
                            pattern.substring(loc+1);
                    Declaration type = 
                            (Declaration) 
                                declaration.getContainer();
                    return isNameMatching(memberPattern, 
                                          declaration) &&
                            isNameMatching(typePattern, type);
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

        private boolean isCompatibleFilter(
                ItemsFilter itemsFilter) {
            if (itemsFilter instanceof Filter) {
                Filter filter = (Filter) itemsFilter;
                return members==filter.members &&
                        version==filter.version;
            }
            else {
                return false;
            }
        }
        
        @Override
        public boolean equalsFilter(ItemsFilter filter) {
            return isCompatibleFilter(filter) &&
                    filter.getPattern()
                        .equals(getPattern());
        }

        @Override
        public boolean isSubFilter(ItemsFilter filter) {
            if (!isCompatibleFilter(filter)) {
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
                else if (filterLoc>=0) {
                    String memberPattern = 
                            pattern.substring(loc+1);
                    String typePattern = 
                            pattern.substring(0,loc);
                    String filterMemberPattern = 
                            filterPattern.substring(filterLoc+1);
                    String filterTypePattern = 
                            filterPattern.substring(0,filterLoc);
                    return 
                        filterMemberPattern
                            .startsWith(memberPattern) &&
                        filterTypePattern
                            .startsWith(typePattern);
                }
                else {
                    return false;
                }
            }
        }
    }
    
    static abstract class BaseLabelProvider 
            implements IBaseLabelProvider {
        @Override
        public void removeListener(
                ILabelProviderListener listener) {}
        
        @Override
        public boolean isLabelProperty(
                Object element, String property) {
            return false;
        }
        
        @Override
        public void dispose() {}
        
        @Override
        public void addListener(
                ILabelProviderListener listener) {}
    }

    class SelectionLabelDecorator 
            extends BaseLabelProvider
            implements ILabelDecorator {
        
        @Override
        public String decorateText(String text, Object element) {
            if (showSelectionPackage || showSelectionModule) {
                Declaration dec = 
                        toDeclaration(element);
                if (dec!=null && 
                        !nameOccursMultipleTimes(dec)) {
                    try {
                        StringBuilder sb = 
                                new StringBuilder(text);
                        if (showSelectionPackage) {
                            sb.append(" \u2014 ")
                              .append(getPackageLabel(dec));
                        }
                        if (showSelectionModule) {
                            sb.append(" \u2014 ")
                              .append(getModule(dec));
                        }
                        return sb.toString();
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
        public Image decorateImage(Image image, 
                Object element) {
            return null;
        }
    }
    
    static class DetailsLabelProvider 
            extends BaseLabelProvider
            implements ILabelProvider {
        
        @Override
        public String getText(Object element) {
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                try {
                    return getPackageLabel(dec);
                            /*+ " \u2014 " + getLocation(dwp)*/
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

    static class MoreDetailsLabelProvider 
            extends BaseLabelProvider
            implements ILabelProvider {
        @Override
        public String getText(Object element) {
            Declaration dec = toDeclaration(element);
            if (dec!=null) {
                try {
                    return getModuleLabel(dec);
                            /* + " \u2014 " + getLocation(dwp)*/
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

    static class EvenMoreDetailsLabelProvider
            extends BaseLabelProvider
            implements ILabelProvider {
        
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

    private StyledString label(Declaration dec) {
        IPreferenceStore prefs = CeylonPlugin.getPreferences();
        StyledString label = 
                getQualifiedDescriptionFor(dec,
                    prefs.getBoolean(TYPE_PARAMS_IN_DIALOGS),
                    prefs.getBoolean(PARAMS_IN_DIALOGS),
                    prefs.getBoolean(PARAM_TYPES_IN_DIALOGS),
                    prefs.getBoolean(RETURN_TYPES_IN_DIALOGS),
                    getPatternControl().getText(),
                    getOpenDialogFont());
        if (nameOccursMultipleTimes(dec)) {
            label.append(" \u2014 ", PACKAGE_STYLER)
                 .append(getPackageLabel(dec), PACKAGE_STYLER)
                 .append(" \u2014 ", COUNTER_STYLER)
                 .append(getModule(dec), COUNTER_STYLER);
        }
        return label;
    }

    class LabelProvider 
            extends StyledCellLabelProvider 
            implements IStyledLabelProvider, 
                       ILabelProvider {
        
        @Override
        public boolean isLabelProperty(Object element, 
                String property) {
            return false;
        }
        
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
                    return label(dec);
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
                StyledString styledText = 
                        getStyledText(element);
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
        
        protected DeclarationProxy restoreItemFromMemento(
                IMemento element) {
            String qualifiedName = 
                    element.getString("qualifiedName");
            String unitFileName = 
                    element.getString("unitFileName");
            String packageName = 
                    element.getString("packageName");
            String projectName = 
                    element.getString("projectName");
            
            if (projectName!=null) {
                for (IProject project: getProjects()) {
                     if (project.getName()
                             .equals(projectName)) {
                        //search for a source file in the project
                        for (PhasedUnit phasedUnit: 
                                getUnits(project)) {
                            String filename = 
                                    phasedUnit.getUnit()
                                        .getFilename();
                            String pname = 
                                    phasedUnit.getPackage()
                                        .getQualifiedNameString();
                            if (filename.equals(unitFileName) && 
                                    pname.equals(packageName)) {
                                for (Declaration dec: 
                                        phasedUnit.getDeclarations()) {
                                    try {
                                        if (isPresentable(dec)) {
                                            String qname = 
                                                    dec.getQualifiedNameString();
                                            if (qualifiedName.equals(qname)) {
                                                return isFiltered(dec) ? null : 
                                                    new DeclarationProxy(dec);
                                            }
                                        }
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
                
            for (IProject project: getProjects()) {
                //for archives, search all dependent modules
                //this will find declarations in source archives
                Modules modules = 
                        getProjectTypeChecker(project)
                                .getContext()
                                .getModules();
                for (Module module: modules.getListOfModules()) {
                    Package pkg = 
                            module.getDirectPackage(packageName);
                    if (pkg!=null) {
                        for (Unit unit: pkg.getUnits()) {
                            if (unit.getFilename()
                                    .equals(unitFileName)) {
                                for (Declaration dec: 
                                        unit.getDeclarations()) {
                                    if (isPresentable(dec)) {
                                        String qname = 
                                                dec.getQualifiedNameString();
                                        if (qualifiedName.equals(qname)) {
                                            return isFiltered(dec) ? null : 
                                                new DeclarationProxy(dec);
                                        }
                                        else if (qualifiedName.startsWith(qname+ '.')) {
                                            for (Declaration mem: dec.getMembers()) {
                                                if (isPresentable(mem)) {
                                                    String mqname = 
                                                            mem.getQualifiedNameString();
                                                    if (qualifiedName.equals(mqname)) {
                                                        return isFiltered(dec) ? null : 
                                                            new DeclarationProxy(mem);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            return null; 
        }
        
        protected void storeItemToMemento(Object item, 
                IMemento element) {
            Declaration dec = toDeclaration(item);
            Unit unit = dec.getUnit();
            element.putString("qualifiedName", 
                    dec.getQualifiedNameString());
            element.putString("unitFileName", 
                    unit.getFilename());
            element.putString("packageName", 
                    unit.getPackage()
                        .getQualifiedNameString());
            if (unit instanceof EditedSourceFile ||
                unit instanceof ProjectSourceFile ||
                unit instanceof CrossProjectSourceFile ||
                //TODO: is this correct:
                unit instanceof JavaCompilationUnit) {
                IResourceAware projectSourceFile = 
                        (IResourceAware) unit;
                IProject project = (IProject)
                        projectSourceFile.getResourceProject();
                if (project!=null) {
                    element.putString("projectName", 
                            project.getName());
                }
            }
        }
        
     }
    
    public OpenDeclarationDialog(
            boolean multi, boolean history, 
            Shell shell, String title, 
            String filterLabelText, String listLabelText) {
        super(shell, multi, filterLabelText, listLabelText);
        setTitle(title);
        initLabelProviders(new LabelProvider(), 
                new SelectionLabelDecorator(),
                new DetailsLabelProvider(), 
                new MoreDetailsLabelProvider(),
                new EvenMoreDetailsLabelProvider());
        if (history) {
            setSelectionHistory(new TypeSelectionHistory());
        }
    }
    
    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = 
                CeylonPlugin.getInstance()
                    .getDialogSettings();
        IDialogSettings section = 
                settings.getSection(SETTINGS_ID);
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
    protected void fillContentProvider(
            AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, 
            IProgressMonitor monitor) 
                    throws CoreException {
        usedNames.clear();
        monitor.beginTask("Filtering", estimateWork(monitor));
        Set<String> searchedArchives = new HashSet<String>();
    
        /*for (IProject project: getProjects()) {
            TypeChecker typeChecker = 
                    getProjectTypeChecker(project);
            fillUnits(contentProvider, itemsFilter, 
                    typeChecker.getPhasedUnits()
                        .getPhasedUnits(),
                    monitor);
            if (monitor.isCanceled()) break;
            Modules modules = 
                    typeChecker.getContext()
                        .getModules();
            for (Module module: modules.getListOfModules()) {
                fillModule(module, 
                        contentProvider, itemsFilter, 
                        monitor, searchedArchives, 
                        projectsToSearch);
                if (monitor.isCanceled()) break;
            }
        }*/
        for (CeylonProject project: getCeylonProjects()) {
            for (Module module: 
                    project.getModules()
                        .getTypecheckerModules()
                        .getListOfModules()) {
                fillModule(module, 
                        contentProvider, itemsFilter, 
                        monitor, searchedArchives);
                if (monitor.isCanceled()) break;
            }
        }
        monitor.done();
    }
    
    private void fillModule(Module mod, 
            AbstractContentProvider contentProvider, 
            ItemsFilter itemsFilter,
            IProgressMonitor monitor, 
            Set<String> searchedArchives) {
        if (includeModule(mod)) {
            IdeModule module = (IdeModule) mod;
            CeylonProject originalProject = 
                    module.getOriginalProject();
            if (originalProject == null
                   //|| !CeylonNature.isEnabled((IProject) originalProject.getIdeArtifact()) //unnecessary for now!
                && searchedArchives.add(uniqueIdentifier(mod))) {
               fillModulePackages(module, contentProvider, 
                     itemsFilter, monitor);
               monitor.worked(1);
            }
        }
    }

    private void fillModulePackages(
            IdeModule module,
            AbstractContentProvider contentProvider, 
            ItemsFilter itemsFilter, 
            IProgressMonitor monitor) {
        ArrayList<Package> copiedPackages = 
                new ArrayList<Package>(module.getPackages());
        for (Package pack: copiedPackages) {
            fillPackage(pack, module, contentProvider, itemsFilter);
            monitor.worked(1);
            if (monitor.isCanceled()) break;
        }
    }

    private void fillPackage(Package pack, IdeModule module, 
            AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter) {
        if (includePackage(pack, module)) {
            for (Declaration dec: pack.getMembers()) {
                fillDeclarationAndMembers(contentProvider, 
                        itemsFilter, module, dec);
            }
        }
    }

    private boolean includePackage(Package pack, IdeModule module) {
        boolean visibleFromProjectSource
            = pack.isShared() 
            || module.getIsProjectModule();
        return visibleFromProjectSource 
                && !filters.isFiltered(pack);
    }
    
    protected static class DeclarationProxy 
            extends ModelProxy {
        private String location;
        public DeclarationProxy(Declaration declaration) {
            super(declaration);
            location = getLocation(declaration);
        }
        @Override
        public boolean equals(Object obj) {
            if (this==obj) {
                return true;
            }
            else if (obj instanceof DeclarationProxy) {
                DeclarationProxy that = 
                        (DeclarationProxy) obj;
                if (super.equals(that)) {
                    return location==that.location || 
                            location!=null && 
                            that.location!=null &&
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
            return super.hashCode() 
                    + location.hashCode();
        }
    }

    private void fillDeclarationAndMembers(
            AbstractContentProvider contentProvider, 
            ItemsFilter itemsFilter,
            IdeModule module, Declaration dec) {
        if (includeDeclaration(module, dec) /*&&
                //watch out for dupes!
                (!module.getIsProjectModule() || 
                 !dec.getUnit()
                     .getFilename()
                     .endsWith(".ceylon"))*/) {
            contentProvider.add(new DeclarationProxy(dec), 
                    itemsFilter);
            nameOccurs(dec);
            if (includeMembers && 
                    dec instanceof ClassOrInterface) {
                try {
                    ArrayList<Declaration> copiedMembers = 
                            new ArrayList<Declaration>
                                (dec.getMembers());
                    for (Declaration member: copiedMembers) {
                        fillDeclarationAndMembers(
                                contentProvider, 
                                itemsFilter, 
                                module, member);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void fillUnits(
            AbstractContentProvider contentProvider,
            ItemsFilter itemsFilter, 
            List<? extends PhasedUnit> units, 
            IProgressMonitor monitor) {
        for (PhasedUnit unit: units) {
            IdeModule jdtModule = (IdeModule) 
                    unit.getPackage()
                        .getModule();
            for (Declaration dec: unit.getDeclarations()) {
                if (includeDeclaration(jdtModule, dec)) {
                    contentProvider.add(
                            new DeclarationProxy(dec), 
                            itemsFilter);
                    nameOccurs(dec);
                }
            }
            
        }
        monitor.worked(1);
    }

    protected String getFilterListAsString(String preference) {
        return CeylonPlugin.getPreferences()
                .getString(preference);
    }
    
    private Filters filters = new Filters() {
        @Override
        protected String getFilterListAsString(String preference) {
            return OpenDeclarationDialog.this
                    .getFilterListAsString(preference);
        }
    };
    
    private boolean isFiltered(Declaration declaration) {
        if (excludeDeprecated && 
                declaration.isDeprecated()) {
            return true;
        }
        return filters.isFiltered(declaration);
    }

    private boolean includeDeclaration(IdeModule module, 
            Declaration dec) {
        try {
            //only include declarations that could possibly
            //be imported by the project source code
            boolean visibleFromSourceModules;
            if (dec.isToplevel()) {
                visibleFromSourceModules =
                        module.getIsProjectModule()
                        || dec.isShared();
            }
            else {
                visibleFromSourceModules = 
                        includeMembers 
                        && dec.isShared();
            }
            return visibleFromSourceModules 
                    && isPresentable(dec) 
                    && !isFiltered(dec);
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
            CeylonProject ceylonProject = 
                    modelJ2C().ceylonModel()
                        .getProject(project);
            if (ceylonProject != null) {
                for (Module module: 
                        ceylonProject.getModules()
                            .getTypecheckerModules()
                            .getListOfModules()) {
                    if (includeModule(module) &&
                            searchedArchives.add(uniqueIdentifier(module))) {
                        work += 1 + module.getPackages().size();
                    }
                }
            }
        }
        return work;
    }

    private boolean includeModule(Module module) {
        return module instanceof IdeModule &&
                !excluded(module) &&
                !filters.isFiltered(module) && 
                module.isAvailable();
    }

    private boolean excluded(Module module) {
        String moduleName = 
                module.getNameAsString();
        return 
            excludeJDK &&
                isJDKModule(moduleName) ||
            excludeOracleJDK && 
                isOracleJDKModule(moduleName);
    }
    
    private static String uniqueIdentifier(Module m) {
        String nameAndVersion = 
                m.getNameAsString() + 
                '#' + m.getVersion();
        if (m instanceof IdeModule) {
            IdeModule module = (IdeModule) m;
            return module.getArtifact()==null ?
                    nameAndVersion :
                    new File(toJavaString(module.getSourceArchivePath()))
                        .getAbsolutePath();
        }
        else {
            return nameAndVersion;
        }
    }

    private static String getModule(Declaration dec) {
        Module module = 
                dec.getUnit()
                    .getPackage()
                    .getModule();
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
        Module module = 
                dwp.getUnit()
                    .getPackage()
                    .getModule();
        if (module instanceof IdeModule) {
            IdeModule m = (IdeModule) module;
            if (m.getIsProjectModule()) {
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

    private static String repositoryPath = 
            CeylonPlugin.getInstance()
                .getCeylonRepository()
                    .getPath();
    
    private static String getLocation(Declaration declaration) {
        Unit unit = declaration.getUnit();
        Module module = unit.getPackage().getModule();
        if (module instanceof IdeModule) {
            if (unit instanceof EditedSourceFile ||
                unit instanceof ProjectSourceFile ||
                unit instanceof CrossProjectSourceFile ||
                //TODO: is this correct:
                unit instanceof JavaCompilationUnit) {
                IResourceAware sourceFile = 
                        (IResourceAware) unit;
                IFile ra = (IFile) 
                        sourceFile.getResourceFile();
                return ra==null ? null :
                    ra.getFullPath().toPortableString();
            }
            else {
                IdeModule mod = (IdeModule) module;
                String displayString = 
                        mod.getRepositoryDisplayString();
                if (repositoryPath.equals(displayString)) {
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
            Declaration type = 
                    (Declaration) 
                        dec.getContainer();
            name = type.getName() + "." + name; 
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
    
    static boolean isPresentable(Declaration d) {
        return d.getName()!=null && 
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
        
        toggleExcludeDeprecatedAction = 
                new ToggleExcludeDeprecatedAction();
        toggleExcludeDeprecatedAction.setChecked(
                excludeDeprecated);
        menuManager.add(toggleExcludeDeprecatedAction);
        
        toggleExcludeJDKAction = 
                new ToggleExcludeJDKAction();
        toggleExcludeJDKAction.setChecked(excludeJDK);
        menuManager.add(toggleExcludeJDKAction);
        
        toggleExcludeOracleJDKAction = 
                new ToggleExcludeOracleJDKAction();
        toggleExcludeOracleJDKAction.setChecked(
                excludeOracleJDK);
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
                        CeylonPlugin.imageRegistry()
                                .getDescriptor(CONFIG_LABELS)) {
            @Override
            public void run() {
                createPreferenceDialogOn(getShell(), 
                        CeylonOpenDialogsPreferencePage.ID, 
                        new String[] { 
                                CeylonOpenDialogsPreferencePage.ID,
                                CeylonPlugin.COLORS_AND_FONTS_PAGE_ID,
                                CeylonFiltersPreferencePage.ID
                        }, 
                        null).open();
                filters.initFilters();
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
                            toDeclaration(selection[0]), 
                            null));
                }
                else {
                    if (emptyDoc==null) {
                        StringBuilder buffer = 
                                new StringBuilder();
                        insertPageProlog(buffer, 0, 
                                HTML.getStyleSheet());
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

    public static boolean isMatchingGlob(
            String filter, String name) {
        if (name==null) {
            return false;
        }
        int loc = 0;
        boolean first = true;
        for (String subfilter: filter.split("\\*")) {
            int match = name.toLowerCase()
                    .indexOf(subfilter.toLowerCase(), loc);
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
    
    // TODO this is a copy/paste of CeylonLocationListener.handleLink
    @Override
    void handleLink(String location, DocBrowser browser) {
        Referenceable target = null;
        CeylonEditor editor = null;
        IEditorPart currentEditor = getCurrentEditor();
        DocGenerator gen = hoverJ2C().getDocGenerator();
        if (currentEditor instanceof CeylonEditor) {
            editor = (CeylonEditor) currentEditor;
            target = gen.getLinkedModel(
                    new ceylon.language.String(location),
                    editor.getParseController());
            /*if (location.startsWith("ref:")) {
                new FindReferencesAction(editor, 
                        (Declaration) target).run();
                close();
                return;
            }
            else if (location.startsWith("sub:")) {
                new FindSubtypesAction(editor, 
                        (Declaration) target).run();
                close();
                return;
            }
            else if (location.startsWith("act:")) {
                new FindRefinementsAction(editor, 
                        (Declaration) target).run();
                close();
                return;
            }
            else if (location.startsWith("ass:")) {
                new FindAssignmentsAction(editor, 
                        (Declaration) target).run();
                close();
                return;
            }*/
        }
        if (location.startsWith("doc:")) {
            if (target==null) {
                target = getLinkedModel(location);
            }
            if (target instanceof Declaration) {
                String text = 
                        getDocumentationFor(null, 
                                (Declaration) target, 
                                null);
                if (text!=null) browser.setText(text);
            }
            if (target instanceof Package) {
                String text = 
                        getDocumentationFor(null,
                                (Package) target);
                if (text!=null) browser.setText(text);
            }
            if (target instanceof Module) {
                String text = 
                        getDocumentationFor(null, 
                                (Module) target);
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
            Declaration[] declarations = 
                    new Declaration[proxies.length];
            for (int i=0; i<proxies.length; i++) {
                declarations[i] = toDeclaration(proxies[i]);
            }
            return declarations;
        }
    }

}
