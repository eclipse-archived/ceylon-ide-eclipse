package com.redhat.ceylon.eclipse.core.launch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;

/**
 * This class is a stateful helper that groups together
 * - static utility classes
 * - stateful correspondence between Eclipse and Ceylon objects
 * - queries on the correspondence
 * 
 * A GUI component will have its own copy and dispose of it.
 *
 */
public class LaunchHelper {
    
    private List<Declaration> topLevelDeclarations = new LinkedList<Declaration>();
    private List<IFile> correspondingfiles = new LinkedList<IFile>();
    
    private IProject project;
    private Module module;
    private Declaration topLevel;
    private TypeChecker typeChecker;

    //public getters
    public IProject getProject() {
        return this.project;
    }

    public Module getModule() {
        return this.module;
    }

    public Declaration getTopLevel() {
        return this.topLevel;
    }

    public List<Declaration> getTopLevelDeclarations() {
        return this.topLevelDeclarations;
    }  
    
    // Constructors
    public LaunchHelper(String projectName) {
        this.project = getProjectFromName(projectName);
        this.typeChecker = CeylonBuilder.getProjectTypeChecker(this.project);
        this.typeChecker = CeylonBuilder.getProjectTypeChecker(this.project);
        if (!getModules(this.project).isEmpty()) {
            this.module = getModules(this.project).iterator().next(); // just choose first one
        } else {
            this.module = getDefaultModule(); // maybe not required
        }
        // no single declaration
        initializeMapping();
    }
    
    public LaunchHelper(String projectName, String moduleName) {
        this.project = getProjectFromName(projectName);
        this.typeChecker = CeylonBuilder.getProjectTypeChecker(this.project);
        this.module = getModule(this.project, moduleName);
        // no single declaration
        initializeMapping();
    }
    
    public LaunchHelper(IJavaElement context) {
        if (context != null) {
            if (context instanceof IJavaProject) {
                this.project = ((IJavaProject)context).getProject();
                this.typeChecker = CeylonBuilder.getProjectTypeChecker(this.project);
                if (!getModules(this.project).isEmpty()) {
                    this.module = getModules(this.project).iterator().next(); // just choose first one
                } else {
                    this.module = getDefaultModule(); // maybe not required
                }
                initializeMapping();
            }
            else if (context instanceof IPackageFragment) {
                IPackageFragment pkg = (IPackageFragment)context;
                this.project = pkg.getJavaProject().getProject();
                this.typeChecker = CeylonBuilder.getProjectTypeChecker(this.project);
                String pkgName = CeylonBuilder.getPackageName(pkg.getResource());
                for (Module mod : getModules(this.project)) {
                    if (mod.getDirectPackage(pkgName) != null) {
                        this.module = mod;
                    }
                }
                if (this.module == null) {
                    this.module = getDefaultModule();
                }
                initializeMapping();
            }
        }
    }

    private void initializeMapping() { 
        if (this.project != null && this.module != null && typeChecker != null) {
            for (PhasedUnit pu : typeChecker.getPhasedUnits().getPhasedUnits()) {
                if (!module.isDefault()) {
                    if (module.getAllPackages().contains(pu.getPackage())) {
                        addPhasedUnit(CeylonBuilder.getFile(pu), pu);
                    }
                } else {
                    addPhasedUnit(CeylonBuilder.getFile(pu), pu);
                }
            }
        }
    }
 
    //For use in MainTab if needed
    @Deprecated
    public void initializeMapping(List<IFile> files) {  
        for (IFile file : files) {            
            if (typeChecker != null) {
                PhasedUnit phasedUnit = typeChecker.getPhasedUnits()
                        .getPhasedUnit(ResourceVirtualFile.createResourceVirtualFile(file));
                if (phasedUnit!=null) {
                    addPhasedUnit(file, phasedUnit);
                }
            }
        }
    }

    private void addPhasedUnit(IFile file, PhasedUnit phasedUnit) {
        List<Declaration> declarations = phasedUnit.getDeclarations();
        for (Declaration d : declarations) {
            if (isRunnable(d)) {
                topLevelDeclarations.add(d);
                correspondingfiles.add(file);
            }
        }
    }
    
    private boolean isRunnable(Declaration d) {
        boolean candidateDeclaration = true;
        if (!d.isToplevel()) {
            candidateDeclaration = false;
        }
        if (d instanceof Method) {
            Method methodDecl = (Method) d;
            if (!methodDecl.getParameterLists().isEmpty() && 
                    !methodDecl.getParameterLists().get(0).getParameters().isEmpty()) {
                candidateDeclaration = false;
            }
        }
        else if (d instanceof Class) {
            Class classDecl = (Class) d;
            if (classDecl.isAbstract() || 
                    classDecl.getParameterList()==null || 
                    !classDecl.getParameterList().getParameters().isEmpty()) {
                candidateDeclaration = false;
            }
        }
        else {
            candidateDeclaration = false;
        }
        return candidateDeclaration;
    }


    
    static Module getModule(IProject project, String fullModuleName) {
        
        if (Module.DEFAULT_MODULE_NAME.equals(fullModuleName)) {
            return getDefaultModule();
        }
        
        if (fullModuleName != null) {
            String[] parts = fullModuleName.split("/");
            
            if (parts != null && parts.length != 2) {
                return null;
            }
            
            for (Module module: CeylonBuilder.getModulesInProject(project)) {
                if (module.getNameAsString().equals(parts[0]) && module.getVersion().equals(parts[1])) {
                    return module;
                }
            }
        }
        return getDefaultModule();
    }

    private static Module getDefaultModule() {
        Module defaultModule = new Module();
        defaultModule.setName(Arrays.asList(new String[]{Module.DEFAULT_MODULE_NAME}));
        defaultModule.setVersion("unversioned");
        defaultModule.setDefault(true);
        return defaultModule;
    }
    
    public Module getModule(Declaration decl) {
        return decl.getUnit().getPackage().getModule();
    }

    public String getModuleFullName(Declaration decl) {
        Module module = getModule(decl);
        if (module.isDefault()) {
            return Module.DEFAULT_MODULE_NAME;
        } else {
            return module.getNameAsString() + "/" + module.getVersion();
        }
    }
 
    public Set<Module> getModules(boolean includeDefault) {
        Set<Module> modules = new HashSet<Module>();

        for(Module module: CeylonBuilder.getModulesInProject(this.project)) {
            if (module.isAvailable() 
                    && !module.getNameAsString().startsWith(Module.LANGUAGE_MODULE_NAME) &&
                    !module.isJava() && module.getPackage(module.getNameAsString()) != null) {
                if ((module.isDefault() && includeDefault) || (!module.isDefault() && !includeDefault)){
                    modules.add(module);
                }
            }
        }
        if (modules.isEmpty()) {
            modules.add(getDefaultModule());
        }
        return modules;
    }
    
    public boolean isProjectContainsModule(IProject project, String fullModuleName) {
        if (fullModuleName.equals(Module.DEFAULT_MODULE_NAME)) {
            return true;
        }
        
        if (project.getName().equals(this.project.getName())
            && this.module != null 
            && fullModuleName.equals(this.module.getNameAsString()+"/"+this.module.getVersion())) {
            return true;
        }
        return false;
    }
    
    public boolean isModuleContainsTopLevel(IProject project, String fullModuleName, String topLevelName) {
        //assuming project and module exist and decls are module-specific
        for (Declaration d : this.topLevelDeclarations) {
            if (d.getQualifiedNameString().replace("::", ".").equals(topLevelName)) {
                return true;
            }
        }
        if (topLevelName.equals("run - default")) {
            for (Declaration d : this.topLevelDeclarations) {
                if (d.getQualifiedNameString().equals(getModule(d).getRootPackage().getNameAsString()+"::run")) {
                    return true;
                }
            }
        }
        return false;
    }  
    
    /**
     * A common place to put this method.  TODO move to Utils in future
     * @param List<Declaration> decls
     * @return Declaration
     */
    public static Declaration chooseDeclaration(final List<Declaration> decls) {
        FilteredItemsSelectionDialog sd = new CeylonTopLevelSelectionDialog(Util.getShell(), false, decls);

        if (sd.open() == Window.OK) {
            return (Declaration)sd.getFirstResult();
        }
        return null;
    }
    
    public static IProject getProjectFromName(String projectName) {
        if (projectName != null && projectName.length() > 0) {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IStatus status = workspace.validateName(projectName, IResource.PROJECT);
            if (status.isOK()) {
                return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
            }
        }
        return null;
    }

    public static Set<Module> getModules(IProject project) {
        Set<Module> returnedModules = new HashSet<Module>();
        returnedModules.addAll(CeylonBuilder.getModulesInProject(project));
        return returnedModules;
    }

    public String getTopLevelDisplayName(Declaration decl) {
        String topLevelName = decl.getQualifiedNameString().replace("::", ".");
        if (getModule(decl) != null) {
            if (getModule(decl).getRootPackage() != null) {
                if (getModule(decl).getRootPackage().equals(decl.getUnit().getPackage()) 
                    && decl.getName().equals("run")) {
                    topLevelName = "run - default"; 
                }
            }
        }
        return "("+topLevelName+")";
    }
}
