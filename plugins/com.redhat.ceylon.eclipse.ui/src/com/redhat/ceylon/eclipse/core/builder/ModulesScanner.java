package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.compiler.typechecker.model.Util.formatPath;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.model.JDTModelLoader;
import com.redhat.ceylon.eclipse.core.model.JDTModuleManager;
import com.redhat.ceylon.eclipse.core.vfs.ResourceVirtualFile;

final class ModulesScanner implements IResourceVisitor {
	private final Module defaultModule;
	private final JDTModelLoader modelLoader;
	private final JDTModuleManager moduleManager;
	private final ResourceVirtualFile srcDir;
	private final IPath srcFolderPath;
	private Module module;

	ModulesScanner(Module defaultModule, JDTModelLoader modelLoader,
			JDTModuleManager moduleManager, ResourceVirtualFile srcDir,
			IPath srcFolderPath) {
		this.defaultModule = defaultModule;
		this.modelLoader = modelLoader;
		this.moduleManager = moduleManager;
		this.srcDir = srcDir;
		this.srcFolderPath = srcFolderPath;
	}

	public boolean visit(IResource resource) throws CoreException {
	    Package pkg;
	    if (resource.equals(srcDir.getResource())) {
	        IFile moduleFile = ((IFolder) resource).getFile(ModuleManager.MODULE_FILE);
	        if (moduleFile.exists()) {
	            moduleManager.addTopLevelModuleError();
	        }
	        return true;
	    }

	    if (resource.getParent().equals(srcDir.getResource())) {
	        // We've come back to a source directory child : 
	        //  => reset the current Module to default and set the package to emptyPackage
	        module = defaultModule;
	        pkg = modelLoader.findPackage("");
	        assert(pkg != null);
	    }

	    if (resource instanceof IFolder) {
	        List<String> pkgName = Arrays.asList(resource.getProjectRelativePath()
	        		.makeRelativeTo(srcFolderPath).segments());
	        String pkgNameAsString = formatPath(pkgName);
	        
	        if ( module != defaultModule ) {
	            if (! pkgNameAsString.startsWith(module.getNameAsString() + ".")) {
	                // We've ran above the last module => reset module to default 
	                module = defaultModule;
	            }
	        }
	        
	        IFile moduleFile = ((IFolder) resource).getFile(ModuleManager.MODULE_FILE);
	        if (moduleFile.exists()) {
	            if ( module != defaultModule ) {
	                moduleManager.addTwoModulesInHierarchyError(module.getName(), pkgName);
	            } else {
	                final List<String> moduleName = pkgName;
	                //we don't know the version at this stage, will be filled later
	                module = moduleManager.getOrCreateModule(moduleName, null);
	                assert(module != null);
	            }
	        }
	        
	        if (module != defaultModule) {
	            pkg = modelLoader.findOrCreatePackage(module, pkgNameAsString);
	        }
	        return true;
	    }
	    return false;
	}
}