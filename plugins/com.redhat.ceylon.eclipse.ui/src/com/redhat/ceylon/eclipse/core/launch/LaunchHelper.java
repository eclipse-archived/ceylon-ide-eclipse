package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.model.typechecker.model.Module.DEFAULT_MODULE_NAME;
import static com.redhat.ceylon.model.typechecker.model.Module.LANGUAGE_MODULE_NAME;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackage;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectDeclaredSourceModules;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModules;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_MODULE_NAME;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.ATTR_TOPLEVEL_NAME;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.CAN_LAUNCH_AS_CEYLON_JAVASCIPT_MODULE;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.CAN_LAUNCH_AS_CEYLON_JAVA_MODULE;
import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.DEFAULT_RUN_MARKER;
import static com.redhat.ceylon.eclipse.util.EditorUtil.getShell;
import static java.lang.Character.charCount;
import static java.lang.Character.isUpperCase;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.ide.common.vfs.FileVirtualFile;
import com.redhat.ceylon.model.typechecker.model.Class;
import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Function;
import com.redhat.ceylon.model.typechecker.model.Module;
import com.redhat.ceylon.model.typechecker.model.Modules;
import com.redhat.ceylon.model.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.open.OpenDeclarationDialog;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.vfs.vfsJ2C;
import com.redhat.ceylon.eclipse.ui.CeylonPlugin;
import com.redhat.ceylon.eclipse.util.EditorUtil;

/**
 * This class is a stateless helper that groups together static utility methods
 */
public class LaunchHelper {

    static void addFiles(List<IFile> files, IResource resource) {
        switch (resource.getType()) {
            case IResource.FILE:
                IFile file = (IFile) resource;
                IPath path = file.getFullPath(); //getProjectRelativePath();
                if (path!=null && 
                        "ceylon".equals(path.getFileExtension()) ) {
                    files.add(file);
                }
                break;
            case IResource.FOLDER:
            case IResource.PROJECT:
                IContainer folder = (IContainer) resource;
                try {
                    for (IResource child: folder.members()) {
                        addFiles(files, child);
                    }
                }
                catch (CoreException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    
    static Object[] findDeclarationFromFiles(List<IFile> files) {
        List<Declaration> topLevelDeclarations = 
                new LinkedList<Declaration>();
        List<IFile> correspondingfiles = 
                new LinkedList<IFile>();
        for (IFile file : files) {
            IProject project = file.getProject();
            TypeChecker typeChecker = 
                    getProjectTypeChecker(project);
            if (typeChecker != null) {
                FileVirtualFile<IResource, IFolder, IFile> virtualFile = 
                        vfsJ2C.createVirtualFile(file);
                PhasedUnit phasedUnit = 
                        typeChecker.getPhasedUnits()
                            .getPhasedUnit(virtualFile);
                if (phasedUnit!=null) {
                    List<Declaration> declarations = 
                            phasedUnit.getDeclarations();
                    for (Declaration d : declarations) {
                        if (isRunnable(d)) {
                            topLevelDeclarations.add(d);
                            correspondingfiles.add(file);
                        }
                    }
                }
            }
        }
        
        Declaration declarationToRun;
        IFile fileToRun;
        if (topLevelDeclarations.isEmpty()) {
            MessageDialog.openError(getShell(), 
                    "Ceylon Launcher", 
                    "No runnable function or class.\n(Only shared toplevel functions and classes with no parameters are runnable.)");
            return null;
        } 
        else if (topLevelDeclarations.size()>1) {
            declarationToRun = 
                    chooseDeclaration(topLevelDeclarations);
            if (declarationToRun!=null) {
                int index = topLevelDeclarations.indexOf(declarationToRun);
                fileToRun = correspondingfiles.get(index);
            }
            else {
                fileToRun = null;
            }
        } 
        else {
            declarationToRun = topLevelDeclarations.get(0);
            fileToRun = correspondingfiles.get(0);
        }
       
        return new Object[] {declarationToRun, fileToRun};
    }
    
    private static boolean isRunnable(Declaration d) {
        boolean candidateDeclaration = true;
        if (!d.isToplevel() || !d.isShared()) {
            candidateDeclaration = false;
        }
        if (d instanceof Function) {
            Function methodDecl = (Function) d;
            if (!methodDecl.getParameterLists().isEmpty() && 
                    !methodDecl.getParameterLists().get(0)
                            .getParameters().isEmpty()) {
                candidateDeclaration = false;
            }
        }
        else if (d instanceof Class) {
            Class classDecl = (Class) d;
            if (classDecl.isAbstract() || 
                    classDecl.getParameterList()==null || 
                    !classDecl.getParameterList()
                            .getParameters().isEmpty()) {
                candidateDeclaration = false;
            }
        }
        else {
            candidateDeclaration = false;
        }
        return candidateDeclaration;
    }
    
    static Module getModule(IProject project, String fullModuleName) {
        
        fullModuleName = 
                normalizeFullModuleName(fullModuleName);
        
        if (fullModuleName != null) {
            String[] parts = fullModuleName.split("/");
            
            if (parts != null && parts.length != 2) {
                return null;
            }
            
            for (Module module: 
                    getProjectDeclaredSourceModules(project)) {
                if (module.getNameAsString().equals(parts[0]) && 
                        module.getVersion().equals(parts[1])) {
                    return module;
                }
            }
            
            if (isDefaultModulePresent(project)) {
                return getDefaultModule(project);
            }
        }
        return null;
    }

    private static String normalizeFullModuleName(String fullModuleName) {
        if (DEFAULT_MODULE_NAME.equals(fullModuleName)) {
            return getFullModuleName(getEmptyDefaultModule());
        }
        else {
            return fullModuleName;
        }
    }

    private static Module getDefaultModule(IProject project) {
        Module defaultModule = 
                getProjectModules(project)
                    .getDefaultModule();
        if (defaultModule == null) {
            defaultModule = getEmptyDefaultModule();
        }
        return defaultModule;
    }
    
    private static Module getEmptyDefaultModule() {
        Module defaultModule = new Module();
        defaultModule.setName(Arrays.asList(DEFAULT_MODULE_NAME));
        defaultModule.setVersion("unversioned");
        defaultModule.setDefault(true);
        return defaultModule;
    }
    
    static Module getModule(Declaration decl) {
        Package pack = decl.getUnit().getPackage();
        if (pack != null) {
            Module mod = pack.getModule();
            if (mod != null) {
                return mod;
            }
        }
        return getEmptyDefaultModule();
    }

    static String getModuleFullName(Declaration decl) {
        Module module = getModule(decl);
        if (module.isDefault()) {
            return DEFAULT_MODULE_NAME;
        } else {
            return getFullModuleName(module);
        }
    }
 
    static Set<Module> getModules(IProject project, 
            boolean includeDefault) {
        Set<Module> modules = new HashSet<Module>();
        for(Module module: 
                getProjectDeclaredSourceModules(project)) {
            if (module.isAvailable() && 
                    !module.getNameAsString()
                            .startsWith(LANGUAGE_MODULE_NAME) && 
                    !module.isJava()) {
                if ((module.isDefault() && includeDefault) // TODO : this is *never* true : the default module is not in the requested list
                        || (!module.isDefault() && 
                                module.getPackage(module.getNameAsString())!=null)){
                    modules.add(module);
                }
            }
        }
        if (modules.isEmpty() || 
                isDefaultModulePresent(project)) {
            modules.add(getDefaultModule(project));
        }
        return modules;
    }
        
    private static boolean isDefaultModulePresent(IProject project) {
        Modules modules = getProjectModules(project);
        if (modules != null) {
            Module defaultModule = 
                    modules.getDefaultModule();
            if (defaultModule != null) {
                List<Declaration> decls = 
                        getDeclarationsForModule(project, 
                                defaultModule);
                if (!decls.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean isModuleInProject(IProject project, 
            String fullModuleName) {
        if (fullModuleName.equals(Module.DEFAULT_MODULE_NAME) && 
                isDefaultModulePresent(project)) {
            return true;
        }
        
        for (Module module: getModules(project, false)) {
                String name = getFullModuleName(module);
                if (fullModuleName!=null &&
                        fullModuleName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    static String getFullModuleName(Module module) {
        return module.getNameAsString() + "/" + 
                module.getVersion();
    }
 
    static List<Declaration> getDeclarationsForModule(IProject project, 
            Module module) {
        List<Declaration> modDecls = 
                new LinkedList<Declaration>();
        if (module != null) {
            List<Package> pkgs = module.getPackages(); // avoid concurrent exception
            for (Package pkg : pkgs) {
                if (pkg.getModule() != null && 
                        isPackageInProject(project, pkg))
                for (Declaration decl : pkg.getMembers()) {
                    if (isRunnable(decl)) {
                        modDecls.add(decl);
                    }                    
                }
            }
        }
        return modDecls;    
    }
    
    private static boolean isPackageInProject(IProject project, 
            Package pkg) {
        TypeChecker typeChecker = 
                getProjectTypeChecker(project);
        List<PhasedUnit> pus = 
                typeChecker.getPhasedUnits().getPhasedUnits();
        for (PhasedUnit phasedUnit : pus) {
            if (pkg.equals(phasedUnit.getPackage())) {
                return true;
            }
        }
        return false;
    }

    static List<Declaration> getDeclarationsForModule(String projectName, 
            String fullModuleName) {
        IProject project = getProjectFromName(projectName);
        Module module = getModule(project, fullModuleName);
        return getDeclarationsForModule(project, module);
    }
    
    /**
     * Does not attempt to get all declarations before it returns true 
     * @param project
     * @param fullModuleName
     * @param topLevelName
     * @return boolean if a top-level is contained in a module
     */
    static boolean isModuleContainsTopLevel(IProject project, 
            String fullModuleName, String topLevelName) {
        
        if (!isModuleInProject(project, fullModuleName)) {
            return false;
        }
        
        if (Module.DEFAULT_MODULE_NAME.equals(fullModuleName)) {
            fullModuleName = 
                    getFullModuleName(getDefaultModule(project));
        }
        
        Module mod = getModule(project, fullModuleName);
        
        if (mod == null) {
            return false;
        }
        
        for (Package pkg : mod.getPackages()) {
            for (Declaration decl : pkg.getMembers()) {
                if (getRunnableName(decl).equals(topLevelName)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    static String getRunnableName(Declaration d) {
        return d.getQualifiedNameString().replace("::", ".");
    }

    static Declaration chooseDeclaration(final List<Declaration> decls) {
        OpenDeclarationDialog sd = 
                new OpenDeclarationDialog(false, false, 
                        EditorUtil.getShell(), 
                        "Ceylon Launcher",
                        "&Type part of a name, with wildcard *, or a camel hump pattern:",
                        "&Select a function to run:") {
            private static final String SETTINGS_ID = 
                    CeylonPlugin.PLUGIN_ID + ".selectRunnableDialog";            
            @Override
            protected String getFilterListAsString(String preference) {
                return "";
            }
            @Override
            public boolean enableDocArea() {
                return false;
            }
            @Override
            protected IDialogSettings getDialogSettings() {
                IDialogSettings settings = 
                        CeylonPlugin.getInstance().getDialogSettings();
                IDialogSettings section = 
                        settings.getSection(SETTINGS_ID);
                if (section == null) {
                    section = settings.addNewSection(SETTINGS_ID);
                }
                return section;
            }
            @Override
            protected IDialogSettings getDialogBoundsSettings() {
                IDialogSettings settings = getDialogSettings();
                IDialogSettings section = 
                        settings.getSection(DIALOG_BOUNDS_SETTINGS);
                if (section == null) {
                    section = settings.addNewSection(DIALOG_BOUNDS_SETTINGS);
                    section.put(DIALOG_HEIGHT, 500);
                    section.put(DIALOG_WIDTH, 400);
                }
                return section;
            }
            @Override
            protected void fillViewMenu(IMenuManager menuManager) {}
            @Override
            protected ItemsFilter createFilter() {
                return new Filter() {
                    @Override
                    public String getPattern() {
                        String pattern = super.getPattern(); 
                        return pattern.isEmpty() ? "**" : pattern;
                    }
                };
            }
            @Override
            protected void fillContentProvider(
                    AbstractContentProvider contentProvider,
                    ItemsFilter itemsFilter,
                    IProgressMonitor monitor)
                    throws CoreException {
                for (int i=0; i<decls.size(); i++) {
                    DeclarationProxy item = 
                            new DeclarationProxy(decls.get(i));
                    contentProvider.add(item, itemsFilter);
                }
            }
        };

        if (sd.open() == Window.OK) {
            return (Declaration) sd.getFirstResult();
        }
        return null;
    }
    
    static Module chooseModule(IProject project, 
            boolean includeDefault) {
        CeylonModuleSelectionDialog cmsd = 
                new CeylonModuleSelectionDialog(getShell(), 
                        getModules(project, true)); 
        if (cmsd.open() == Window.OK) {
            return (Module)cmsd.getFirstResult();
        }
        return null;
    }
    
    static IProject getProjectFromName(String projectName) {
        if (projectName != null && projectName.length() > 0) {
            IWorkspace workspace = getWorkspace();
            IStatus status = 
                    workspace.validateName(projectName, 
                            IResource.PROJECT);
            if (status.isOK()) {
                return workspace.getRoot()
                        .getProject(projectName);
            }
        }
        return null;
    }

    static String getTopLevelNormalName(String moduleFullName, 
            String displayName) {
        if (displayName.contains(DEFAULT_RUN_MARKER) && 
                moduleFullName.indexOf('/') != -1) {
            return moduleFullName.substring(0, 
                    moduleFullName.indexOf('/')) + ".run";
        }
         
        return displayName;
    }
    
    static String getTopLevelDisplayName(Declaration decl) {
        String topLevelName = getRunnableName(decl);
        Module module = getModule(decl);
        if (module!=null && 
                decl.equals(getDefaultRunnableForModule(module))) {
            topLevelName = "run" + DEFAULT_RUN_MARKER; 
        }
        return topLevelName;
    }

    static Module getDefaultOrOnlyModule(IProject project, 
            boolean includeDefault) {
        Set<Module> modules = getModules(project, true);
        
        //if only one real module or just one default module, just send it back
        if (modules.size() == 1) {
            return modules.iterator().next();
        }
        
        if (modules.size() ==2 && !includeDefault) {
            Iterator<Module> modIterator = modules.iterator(); 
            while (modIterator.hasNext()) {
                Module realMod = modIterator.next();
                if (!realMod.isDefault()) {
                    return realMod;
                }
            }
        }
        return null;
    }

    static Declaration getDefaultRunnableForModule(Module mod) {
        Declaration decl = null;
        if (mod.getRootPackage()!=null) {
            decl = mod.getRootPackage()
                    .getDirectMember("run", null, false);
        }
        return decl;
    }

    static Module getModule(IFolder folder) {
        Package pkg = getPackage(folder);
        if (pkg != null) {
            return pkg.getModule();
        }        
        return null;
    }
    
    static boolean isBuilderEnabled(IProject project, String property) {
        if (CAN_LAUNCH_AS_CEYLON_JAVA_MODULE.equals(property)) {
            return CeylonBuilder.compileToJava(project);
        }
        else if (CAN_LAUNCH_AS_CEYLON_JAVASCIPT_MODULE.equals(property)) {
            return CeylonBuilder.compileToJs(project);
        }
        return false;
    }

    public static String getTopLevel(ILaunchConfiguration configuration)
            throws CoreException {
        String topLevelName = 
                configuration.getAttribute(ATTR_TOPLEVEL_NAME,
                        (String) null);
        if (topLevelName == null) {
            String moduleName = 
                    configuration.getAttribute(ATTR_MODULE_NAME,
                            (String) null);
            if (moduleName != null) {
                String packageName = 
                        moduleName.replaceAll("/.*$", "");
                return packageName + ".run";
            }
        }
        return topLevelName;
    }

    public static String getStartLocation(ILaunchConfiguration configuration)
            throws CoreException {
        String location;
        String methodToStopIn = null;
        String toplevel = getTopLevel(configuration);
        if (toplevel != null) {
            int index = toplevel.lastIndexOf(".");
            if (index >= -1 && index < toplevel.length()-1) {
                int typeFirstChar = 
                        toplevel.codePointAt(index + 1);
                if (!isUpperCase(typeFirstChar)) {
                    // It's a top-level method
                    methodToStopIn = 
                            toplevel.substring(index + 
                                    charCount(typeFirstChar));
                    toplevel += "_";
                } else {
                    // It's a top-level class
                    methodToStopIn = "<init>"; // constructor
                }
            }
            location = toplevel + '/' + methodToStopIn;
        }
        else {
            location = null;
        }
        return location;
    }    
}
