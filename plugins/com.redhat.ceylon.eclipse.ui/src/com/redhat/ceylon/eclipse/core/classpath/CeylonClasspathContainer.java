/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package com.redhat.ceylon.eclipse.core.classpath;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getCeylonClassesOutputFolder;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getJdtClassesEnabled;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getRequiredProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.parseCeylonModel;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.getCeylonClasspathEntry;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static org.eclipse.core.resources.IncrementalProjectBuilder.FULL_BUILD;
import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;
import static org.eclipse.jdt.core.JavaCore.getClasspathContainer;
import static org.eclipse.jdt.core.JavaCore.newLibraryEntry;
import static org.eclipse.jdt.core.JavaCore.setClasspathContainer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnits;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.core.model.loader.JDTModuleManager;

/**
 * Eclipse classpath container that will contain the Ceylon resolved entries.
 */
public class CeylonClasspathContainer implements IClasspathContainer {

    public static final String CONTAINER_ID = PLUGIN_ID + ".cpcontainer.CEYLON_CONTAINER";

    private IClasspathEntry[] classpathEntries;
    private IPath path;
    //private String jdtVersion;
    private IJavaProject javaProject;

    public IJavaProject getJavaProject() {
        return javaProject;
    }

    public IClasspathAttribute[] getAttributes() {
        return attributes;
    }

    /**
     * attributes attached to the container but not Ceylon related (Webtools or AspectJfor instance)
     */
    private IClasspathAttribute[] attributes = new IClasspathAttribute[0];

    public CeylonClasspathContainer(IJavaProject javaProject, IPath path,
            IClasspathEntry[] classpathEntries, IClasspathAttribute[] attributes) {
        this.path = path;
        this.attributes = attributes; 
        this.classpathEntries = classpathEntries;
        this.javaProject = javaProject;
    }

    public CeylonClasspathContainer(IProject project) {
		javaProject = JavaCore.create(project);
		path = new Path(CeylonClasspathContainer.CONTAINER_ID + "/default");
		classpathEntries = new IClasspathEntry[0];
		attributes = new IClasspathAttribute[0];
    }
    
    public CeylonClasspathContainer(CeylonClasspathContainer cp) {
        path = cp.path;
        javaProject = cp.javaProject;        
        classpathEntries = cp.classpathEntries;
        attributes = cp.attributes;
    }

    public String getDescription() {
        return "Ceylon Modules";
    }

    public int getKind() {
        return K_APPLICATION;
    }

    public IPath getPath() {
        return path;
    }

    public IClasspathEntry[] getClasspathEntries() {
        return classpathEntries;
    }

    /*private static final ISchedulingRule RESOLVE_EVENT_RULE = new ISchedulingRule() {
        public boolean contains(ISchedulingRule rule) {
            return rule == this;
        }

        public boolean isConflicting(ISchedulingRule rule) {
            return rule == this;
        }
    };*/

    public static void runInitialize(final IPath containerPath, final IJavaProject project) {
    	Job job = new Job("Initializing Ceylon dependencies for project " + 
    			project.getElementName()) {
    		@Override protected IStatus run(IProgressMonitor monitor) {			
    			try {
    				
        			IClasspathContainer c = getClasspathContainer(containerPath, project);
    				CeylonClasspathContainer container;
    				if (c instanceof CeylonClasspathContainer) {
    					container = (CeylonClasspathContainer) c;
    				} 
    				else {
    					IClasspathEntry entry = getCeylonClasspathEntry(containerPath, project);
    					IClasspathAttribute[] attributes = entry == null ? 
    							new IClasspathAttribute[0] : entry.getExtraAttributes();
						if (c == null) {
							container = new CeylonClasspathContainer(project, containerPath,
									new IClasspathEntry[0], attributes);
						} 
						else {
							// this might be the persisted one: reuse the persisted entries
							container = new CeylonClasspathContainer(project, containerPath, 
									c.getClasspathEntries(), attributes);
						}                    
    				}

    				container.resolveClasspath(monitor, true);
    				
    				setClasspathContainer(containerPath, new IJavaProject[] {project},
    						new IClasspathContainer[] {container}, monitor);

    				return Status.OK_STATUS;
    				
    			} 
    			catch (JavaModelException ex) {
    				// unless there are issues with the JDT, this should never happen
    				return new Status(IStatus.ERROR, PLUGIN_ID,
    						"could not get container", ex);
    			}
    			catch (CoreException e) {
    				e.printStackTrace();
    				return new Status(IStatus.ERROR, PLUGIN_ID,
    						"could not resolve dependencies", e);
    			}            
    		}    		
    	};
    	job.setUser(false);
    	job.setRule(getWorkspace().getRoot());
    	job.schedule();
    }

    public void runReconfigure() {
    	Job job = new Job("Resolving Ceylon dependencies for project " + 
                getJavaProject().getElementName()) {
    	    @Override protected IStatus run(IProgressMonitor monitor) {
    	    	final IProject project = javaProject.getProject();
    			IFolder jdtClassesDir = getCeylonClassesOutputFolder(javaProject);
	    		try {
	    			
	    			if (getJdtClassesEnabled(project)) {
	    				if (!jdtClassesDir.exists()) {
	    					jdtClassesDir.create(0, true, monitor);
	    				}
	    			}
	    			else {
	    				if (jdtClassesDir.exists()) {
	    					jdtClassesDir.delete(true, monitor);
	    				}
	    			}
    	    		
        			final IClasspathEntry[] classpath = constructModifiedClasspath(javaProject, path);        			
    	            javaProject.setRawClasspath(classpath, monitor);
    	            
    	    		resolveClasspath(monitor, false);
    	    		
    	            setClasspathContainer(path, new IJavaProject[] {javaProject},
    	                    new IClasspathContainer[] {CeylonClasspathContainer.this}, 
    	                    monitor);

    	            Job job = new Job("Rebuild dependencies of project " + project.getName()) {
    	            	@Override
    	            	protected IStatus run(IProgressMonitor monitor) {
    	            		try {
    	            			//Note: I would love to be able to just build the projects that
    	            			//      depend on this one, but that just doesn't work out right
    	            			for (IProject p: project.getWorkspace().getRoot().getProjects()) {
    	            				if (p.isOpen() && !p.equals(project) &&
    	            						getRequiredProjects(p).contains(project)) {
    	            					project.getWorkspace().build(FULL_BUILD, monitor);
    	            					break;
    	            				} 
    	            			}
    	            			
    	            		}
    	            		catch (CoreException e) {
    	            			e.printStackTrace();
    	            		}
    	            		return Status.OK_STATUS;
    	            	}
    	            };
    	            job.setRule(project.getWorkspace().getRoot());
    	            job.schedule();
    				return Status.OK_STATUS;
    				
    	    	} 
    	    	catch (CoreException e) {
    	    		e.printStackTrace();
    	    		return new Status(IStatus.ERROR, PLUGIN_ID,
    	    				"could not resolve dependencies", e);
    	    	}
    	    }    		
    	};
    	job.setUser(true);
    	job.setRule(getWorkspace().getRoot());
        job.schedule();
    }
    
	private IClasspathEntry[] constructModifiedClasspath(IJavaProject javaProject, IPath path) 
			throws JavaModelException {
		IClasspathEntry newEntry = JavaCore.newContainerEntry(path, null, 
				new IClasspathAttribute[0], false);
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		List<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>(Arrays.asList(entries));
		int index = 0;
		boolean mustReplace = false;
		for (IClasspathEntry entry: newEntries) {
		    if (entry.getPath().equals(newEntry.getPath()) ) {
		        mustReplace = true;
		        break;
		    }
		    index++;
		}
		if (mustReplace) {
		    newEntries.set(index, newEntry);
		}
		else {
		    newEntries.add(newEntry);
		}
		return (IClasspathEntry[]) newEntries.toArray(new IClasspathEntry[newEntries.size()]);
	}
	
    public boolean resolve(IProgressMonitor monitor) {
        try {
			return resolveClasspath(monitor, true);
		} 
        catch (CoreException e) {
			e.printStackTrace();
		}
        return false;
    }

    /*void notifyUpdateClasspathEntries() {
        try {
            setClasspathContainer(path, new IJavaProject[] {javaProject},
                new IClasspathContainer[] {new CeylonClasspathContainer(CeylonClasspathContainer.this)},
                null);

            // the following code was imported from:
            // http://svn.codehaus.org/m2eclipse/trunk/org.maven.ide.eclipse/src/org/maven/ide
            // /eclipse/embedder/BuildPathManager.java
            // revision: 370; function setClasspathContainer; line 215

            // XXX In Eclipse 3.3, changes to resolved classpath are not announced by JDT Core
            // and PackageExplorer does not properly refresh when we update Ivy
            // classpath container.
            // As a temporary workaround, send F_CLASSPATH_CHANGED notifications
            // to all PackageExplorerContentProvider instances listening to
            // java ElementChangedEvent.
            // Note that even with this hack, build clean is sometimes necessary to
            // reconcile PackageExplorer with actual classpath
            // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=154071
            if (getJDTVersion().startsWith("3.3")) {
                DeltaProcessingState s = JavaModelManager.getJavaModelManager().deltaState;
                synchronized (s) {
                    IElementChangedListener[] listeners = s.elementChangedListeners;
                    for (int i = 0; i < listeners.length; i++) {
                        if (listeners[i] instanceof PackageExplorerContentProvider) {
                            JavaElementDelta delta = new JavaElementDelta(javaProject);
                            delta.changed(IJavaElementDelta.F_CLASSPATH_CHANGED);
                            listeners[i].elementChanged(new ElementChangedEvent(delta,
                                    ElementChangedEvent.POST_CHANGE));
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            // unless there are some issues with the JDT, this should never happen
            CeylonPlugin.getInstance().logException("", e);
        }
    }*/

    /*private synchronized String getJDTVersion() {
        if (jdtVersion == null) {
            Bundle[] bundles = CeylonPlugin.getInstance().getBundleContext().getBundles();
            for (int i = 0; i < bundles.length; i++) {
                if (JavaCore.PLUGIN_ID.equals(bundles[i].getSymbolicName())) {
                    jdtVersion = (String) bundles[i].getHeaders().get(Constants.BUNDLE_VERSION);
                    break;
                }
            }
        }
        return jdtVersion;
    }*/

	boolean resolveClasspath(IProgressMonitor monitor, boolean reparse)
			throws CoreException {
		IJavaProject javaProject = getJavaProject();
		IProject project = javaProject.getProject();
		
		//TODO: the following is terrible for two reasons:
		//      - we don't really need to parse all the 
		//        source of the whole project (just the
		//        module descriptors)
		//      - as a side effect we throw away the whole
		//        model, forcing us to have to do a full 
		//        build even if nothing interesting changed!
		if (reparse) parseCeylonModel(project, monitor);
		
		TypeChecker typeChecker = getProjectTypeChecker(project);
		if (typeChecker!=null) {
			final Collection<IClasspathEntry> paths = new LinkedHashSet<IClasspathEntry>();
			
		    PhasedUnits phasedUnits = typeChecker.getPhasedUnits();
		    JDTModuleManager moduleManager = (JDTModuleManager) phasedUnits.getModuleManager();
		    for (File archive: moduleManager.getClasspath()) {
		        if (archive.exists()) {
					try {
						Path classpathArtifact = new Path(archive.getCanonicalPath());
			            IPath srcArtifact = classpathArtifact.removeFileExtension()
			            		.addFileExtension("src");
			            paths.add(newLibraryEntry(classpathArtifact, srcArtifact, null));
					}
					catch (IOException e) {
						e.printStackTrace();
					}
		        }
		    }
		    List<IJavaProject> javaProjects = new ArrayList<IJavaProject>();
	        for (IProject requiredProject: getRequiredProjects(project)) {
	            javaProjects.add(JavaCore.create(requiredProject));
	        }

	        for (final IJavaProject javaProj : javaProjects) {
	        	IFolder ceylonModulesOutputFolder = CeylonBuilder.getCeylonModulesOutputFolder(javaProj);
	        	ceylonModulesOutputFolder.refreshLocal(IResource.DEPTH_INFINITE, monitor);
	        	ceylonModulesOutputFolder.accept(new IResourceVisitor() {
					@Override
					public boolean visit(IResource resource) throws CoreException {
						String ext = resource.getFileExtension();
						if (ext!=null && ext.equals("car")) {
				            IPath classpathArtifact = resource.getFullPath();
							IPath srcArtifact = javaProj.getPath();
							        /*classpathArtifact.removeFileExtension().addFileExtension("src");*/
							paths.add(newLibraryEntry(classpathArtifact, srcArtifact, null));
						}
						return true;
					}
				}, 
				IResource.DEPTH_INFINITE, 
				IContainer.INCLUDE_HIDDEN);
	        }
	        
		    if (getJdtClassesEnabled(project)) {
		    	paths.add(newLibraryEntry(getCeylonClassesOutputFolder(javaProject).getFullPath(), 
		    			javaProject.getPath(), null, true));
		    }
		    
		    classpathEntries = paths.toArray(new IClasspathEntry[paths.size()]);
		    return true;
		    
		}
		return false;
	}
}
