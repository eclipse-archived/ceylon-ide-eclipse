package com.redhat.ceylon.eclipse.core.launch;

import static com.redhat.ceylon.eclipse.core.launch.ICeylonLaunchConfigurationConstants.DEFAULT_RUN_MARKER;

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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Class;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Method;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.code.editor.Util;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;

/**
 * This class is a stateless helper that groups together static utility methods
 */
public class LaunchHelper {

	static void addFiles(List<IFile> files, IResource resource) {
        switch (resource.getType()) {
            case IResource.FILE:
                IFile file = (IFile) resource;
                IPath path = file.getFullPath(); //getProjectRelativePath();
                if (path!=null && "ceylon".equals(path.getFileExtension()) ) {
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
        List<Declaration> topLevelDeclarations = new LinkedList<Declaration>();
        List<IFile> correspondingfiles = new LinkedList<IFile>();
        for (IFile file : files) {
            IProject project = file.getProject();
            TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
            if (typeChecker != null) {
                PhasedUnit phasedUnit = typeChecker.getPhasedUnits()
                        .getPhasedUnit(ResourceVirtualFile.createResourceVirtualFile(file));
                if (phasedUnit!=null) {
                    List<Declaration> declarations = phasedUnit.getDeclarations();
                    for (Declaration d : declarations) {
                        if (isRunnable(d)) {
                            topLevelDeclarations.add(d);
                            correspondingfiles.add(file);
                        }
                    }
                }
            }
        }
        
        Declaration declarationToRun = null;
        IFile fileToRun = null; 
        if (topLevelDeclarations.size() == 0) {
            MessageDialog.openError(Util.getShell(), "Ceylon Launcher", 
            		"No ceylon runnable element"); 
        } 
        else if (topLevelDeclarations.size() > 1) {
            declarationToRun = LaunchHelper.chooseDeclaration(topLevelDeclarations);
            if (declarationToRun!=null) {
                fileToRun = correspondingfiles.get(topLevelDeclarations.indexOf(declarationToRun));
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
   
    static Module getAnyModule(IProject project, String fullModuleName) { // mostly for dependent projects
        if (fullModuleName != null) {
            String[] parts = fullModuleName.split("/");
            
            if (parts != null && parts.length != 2) {
                return null;
            }
            Modules modules = CeylonBuilder.getProjectModules(project);
            if (modules != null) {
		        for (Module module: modules.getListOfModules()) {
		            if (module.getNameAsString().equals(parts[0]) && module.getVersion().equals(parts[1])) {
		                return module;
		            }
		        }
            }
        }
        return null;
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
        return null;
    }

    private static Module getDefaultModule() {
        Module defaultModule = new Module();
        defaultModule.setName(Arrays.asList(new String[]{Module.DEFAULT_MODULE_NAME}));
        defaultModule.setVersion("unversioned");
        defaultModule.setDefault(true);
        return defaultModule;
    }
    
    static Module getModule(Declaration decl) {
        return decl.getUnit().getPackage().getModule();
    }

    static String getModuleFullName(Declaration decl) {
        Module module = getModule(decl);
        if (module.isDefault()) {
            return Module.DEFAULT_MODULE_NAME;
        } else {
            return getFullModuleName(module);
        }
    }
 
    static Set<Module> getModules(IProject project, boolean includeDefault) {
        Set<Module> modules = new HashSet<Module>();

        for(Module module: CeylonBuilder.getProjectModules(project).getListOfModules()) {
            if (module.isAvailable() 
                    && !module.getNameAsString().startsWith(Module.LANGUAGE_MODULE_NAME) && !module.isJava() ) {
                if ((module.isDefault() && includeDefault) 
                		|| (!module.isDefault() && module.getPackage(module.getNameAsString()) != null)){
                    modules.add(module);
                }
            }
        }
        if (modules.isEmpty()) {
            modules.add(getDefaultModule());
        }
        return modules;
    }
    
    static boolean isProjectContainsModule(IProject project, String fullModuleName) {
        if (fullModuleName.equals(Module.DEFAULT_MODULE_NAME)) {
            return true;
        }
        
        for (Module module : getModules(project, false)) {
	            if (fullModuleName != null 
	            		&& fullModuleName.equals(getFullModuleName(module))) {
	            return true;
	        }
    	}
        return false;
    }

	static String getFullModuleName(Module module) {
		return module.getNameAsString()+"/"+module.getVersion();
	}
    
	static List<Declaration> getDeclarationsForModule(String projectName, String fullModuleName) {
		IProject project = getProjectFromName(projectName);
    	
    	List<Declaration> modDecls = new LinkedList<Declaration>();
    	
    	if (isProjectContainsModule(project, fullModuleName) && getModule(project, fullModuleName) != null) { // if the module is in the same project
	    	TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
	    	Module module = getModule(project, fullModuleName);
	    	for (PhasedUnit pu : typeChecker.getPhasedUnits().getPhasedUnits()) {
	    		if (isModuleCompatible(module, pu)) {
			    	for (Declaration decl : pu.getDeclarations()) {
			    		if (isRunnable(decl)) {
			    			modDecls.add(decl);
			    		}
			    	}
	    		}
	    	}
    	} else { // depednent module
    		Module module = getAnyModule(project, fullModuleName);
    		for (Package pkg : module.getSharedPackages()) {
    			for (Declaration decl : pkg.getMembers()) {
		    		if (isRunnable(decl)) {
		    			modDecls.add(decl);
		    		}    				
    			}
    		}
    	}
    	
    	return modDecls;	
	}
	
	/**
	 * Does not attempt to get all declarations before it returns true / also for dependent modules
	 * @param project
	 * @param fullModuleName
	 * @param topLevelName
	 * @return boolean if a top-level is contained in a module
	 */
    static boolean isModuleContainsTopLevel(IProject project, String fullModuleName, String topLevelName) {
        
    	if (!isProjectContainsModule(project, fullModuleName)) {
    		return false;
    	}
    	
    	if (Module.DEFAULT_MODULE_NAME.equals(fullModuleName)) {
    		fullModuleName = Module.DEFAULT_MODULE_NAME + "/" + "unversioned"; //compatible with next method.
    	}
    	
    	Module mod = getAnyModule(project, fullModuleName);
    	
    	if (mod == null) {
    		return false;
    	}
    	
        if (mod.isDefault()) {
        	TypeChecker typeChecker = CeylonBuilder.getProjectTypeChecker(project);
        	for (PhasedUnit pu : typeChecker.getPhasedUnits().getPhasedUnits()) {
				for (Declaration decl : pu.getDeclarations()) {
					if (getRunnableName(decl).equals(topLevelName)) {
						return true;
					}
				}
        	}
        } else {
			for (Package pkg : mod.getPackages()) {
				for (Declaration decl : pkg.getMembers()) {
					if (getRunnableName(decl).equals(topLevelName)) {
						return true;
					}
				}
			}
        }
    	
        return false;
    }

	private static boolean isModuleCompatible(Module module,
			PhasedUnit pu) {
		return (!module.isDefault() && module.getAllPackages().contains(pu.getPackage()) )
				|| module.isDefault();
	}

//	/**
//	 * May come in handy for uniting laucnh types
//	 */
//	@Deprecated
//	private static List<Declaration> getRunnableDeclarations(PhasedUnit phasedUnit) {
//    	List<Declaration> puDecls = new LinkedList<Declaration>();
//    	for (Declaration decl : phasedUnit.getDeclarations()) {
//    		if (isRunnable(decl)) {
//    			puDecls.add(decl);
//    		}
//    	}
//    	return puDecls;
//	}

	static String getRunnableName(Declaration d) {
		return d.getQualifiedNameString().replace("::", ".");
	}  
    

    static Declaration chooseDeclaration(final List<Declaration> decls) {
        FilteredItemsSelectionDialog sd = new CeylonTopLevelSelectionDialog(Util.getShell(), false, decls);

        if (sd.open() == Window.OK) {
            return (Declaration)sd.getFirstResult();
        }
        return null;
    }

	static Module chooseModule(String projectName, boolean includeDefault) {
		return chooseModule(getProjectFromName(projectName), includeDefault);
	}
	
    static Module chooseModule(IProject project, boolean includeDefault) {

    	Set<Module> modules = getModules(project, true);
    	if (getDefaultOrOnlyModule(project, includeDefault) != null) {
    		return getDefaultOrOnlyModule(project, includeDefault);
    	}
	    
	    FilteredItemsSelectionDialog cmsd = new CeylonModuleSelectionDialog(Util.getShell(), modules, "Choose Ceylon Module"); 
	    if (cmsd.open() == Window.OK) {
	        return (Module)cmsd.getFirstResult();
	    }
	    return null;
    }
    
    static IProject getProjectFromName(String projectName) {
        if (projectName != null && projectName.length() > 0) {
            IWorkspace workspace = ResourcesPlugin.getWorkspace();
            IStatus status = workspace.validateName(projectName, IResource.PROJECT);
            if (status.isOK()) {
                return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
            }
        }
        return null;
    }

    static Set<Module> getModules(IProject project) {
        Set<Module> returnedModules = new HashSet<Module>();
        returnedModules.addAll(CeylonBuilder.getModulesInProject(project));
        return returnedModules;
    }

    static String getTopLevelNormalName(String moduleFullName, String displayName) {
    	if (displayName.contains(DEFAULT_RUN_MARKER)) {
	        return moduleFullName.substring(0, moduleFullName.indexOf('/')) 
	        		+ ".run";
    	}
	     
        return displayName;
    }
    
    static String getTopLevelDisplayName(Declaration decl) {
        
    	String topLevelName = getRunnableName(decl);
        
        if (getModule(decl) != null && decl.equals(getDefaultRunnableForModule(getModule(decl)))) {
	        topLevelName = "run" + DEFAULT_RUN_MARKER; 
        }
        return topLevelName;
    }

	static Module getDefaultOrOnlyModule(IProject project, boolean includeDefault) {
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
		if (mod.getRootPackage() != null) {
			decl = mod.getRootPackage().getDirectMember("run", null, false);
		}
		return decl;
	}

	static Module getModule(IFolder folder) {
		for (Module mod : getModules(folder.getProject())) {
		if (CeylonBuilder.getPackageName(folder).equals(mod.getRootPackage().getNameAsString()))
			return mod;
		}
		return null;
	}
	
    static ILaunchConfigurationType getConfigurationType() {
        return getLaunchManager().getLaunchConfigurationType(ICeylonLaunchConfigurationConstants.ATTR_CEYLON_MODULE);        
    }
 
    static ILaunchManager getLaunchManager() {
        return DebugPlugin.getDefault().getLaunchManager();
    }
    
    static String getLaunchConfigurationName(String projectName, String moduleName, Declaration declarationToRun) {
        String topLevelDisplayName = getTopLevelDisplayName(declarationToRun);
        
        String configurationName = projectName.trim() + " - " 
        		+ moduleName.trim() + " ("  
        		+ topLevelDisplayName.trim() + ")";
		
        configurationName = configurationName.replaceAll("[\u00c0-\ufffe]", "_");
        
        return getLaunchManager().generateLaunchConfigurationName(configurationName);
    }
}
