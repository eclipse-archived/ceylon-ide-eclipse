package com.redhat.ceylon.eclipse.core.builder;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isCeylon;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.isJava;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.retrievePackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import com.redhat.ceylon.compiler.typechecker.analyzer.ModuleManager;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.BooleanHolder;

final class DeltaScanner implements IResourceDeltaVisitor {
	private final BooleanHolder mustDoFullBuild;
	private final IProject project;
	private final BooleanHolder sourceModified;
	private final BooleanHolder mustResolveClasspathContainer;

	DeltaScanner(BooleanHolder mustDoFullBuild, IProject project,
			BooleanHolder sourceModified,
			BooleanHolder mustResolveClasspathContainer) {
		this.mustDoFullBuild = mustDoFullBuild;
		this.project = project;
		this.sourceModified = sourceModified;
		this.mustResolveClasspathContainer = mustResolveClasspathContainer;
	}

	@Override
	public boolean visit(IResourceDelta resourceDelta) 
			throws CoreException {
	    IResource resource = resourceDelta.getResource();
	    
	    if (resource instanceof IFolder) {
	    	if (resourceDelta.getKind()==IResourceDelta.REMOVED) {
	    		IFolder folder = (IFolder) resource; 
	    		Package pkg = retrievePackage(folder);
	    		if (pkg!=null) {
	    			//a package has been removed
	    			mustDoFullBuild.value = true;
	    		}
	    	}
	    }
	    
	    else if (resource instanceof IFile) {
	        if (resource.getName().equals(ModuleManager.PACKAGE_FILE)) {
                //a package descriptor has been added, removed, or changed
	            if (resourceDelta.getKind()!=IResourceDelta.CHANGED) {
	                mustDoFullBuild.value = true;
	            }
	            else {
	                sourceModified.value = true;
	            }
	        }
	        else if (resource.getName().equals(ModuleManager.MODULE_FILE)) {
	            //a module descriptor has been added, removed, or changed
	            mustResolveClasspathContainer.value = true;
                if (resourceDelta.getKind()!=IResourceDelta.CHANGED) {
                    mustDoFullBuild.value = true;
                }
                else {
                    sourceModified.value = true;
                }
	        }
	        else if (resource.getName().equals(".classpath")) {
	            //the classpath changed
	        	mustDoFullBuild.value = true;
	        	mustResolveClasspathContainer.value = true;
	        }
	        else if (isCeylon((IFile) resource)) {
	        	//a Ceylon source file was modified, we can
	        	//compile incrementally
	            sourceModified.value = true;
	        }
	        else if (isJava((IFile) resource)) {
	        	if (!resource.getProject().equals(project)) {
	        		//a Java source file in a project we depend
	        		//on was modified - we must do a full build, 
	        		//'cos we don't know what Ceylon units in 
	        		//this project depend on it
	        		//TODO: fix that by tracking cross-project 
	        		//      dependencies to Java!
	                mustDoFullBuild.value = true;
	            }
	            sourceModified.value = true;
	        }
	    }
	    
	    else if (resource instanceof IProject) { 
	    	if ((resourceDelta.getFlags() & IResourceDelta.DESCRIPTION)!=0) {
	    		//some project setting changed
	        	mustDoFullBuild.value = true;
	        	mustResolveClasspathContainer.value = true;
	    	}
	    	else if (!resource.equals(project)) {
	    		//this is some kind of multi-project build,
	    		//indicating a change in a project we
	    		//depend upon
	    		/*mustDoFullBuild.value = true;
	    		mustResolveClasspathContainer.value = true;*/
	    	}
	    }
	    
	    return true;
	}
}