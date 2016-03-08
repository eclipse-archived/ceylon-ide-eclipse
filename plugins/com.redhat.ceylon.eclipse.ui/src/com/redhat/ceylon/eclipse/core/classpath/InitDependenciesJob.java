package com.redhat.ceylon.eclipse.core.classpath;

import static com.redhat.ceylon.eclipse.java2ceylon.Java2CeylonProxies.utilJ2C;
import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;
import static com.redhat.ceylon.ide.common.util.toCeylonString_.toCeylonString;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.ide.common.util.ProgressMonitor$impl;

public class InitDependenciesJob extends Job {
    
    private final CeylonProjectModulesContainer container;
    private boolean retriedAfterAddingTheLanguageModuleEntry = false; 

    public InitDependenciesJob(String name, CeylonProjectModulesContainer container) {
        super(name);
        this.container = container;
    }

    @Override 
    protected IStatus run(IProgressMonitor monitor) {
        ProgressMonitor$impl<IProgressMonitor>.Progress progress = 
                utilJ2C().wrapProgressMonitor(monitor)
                    .Progress$new$(1000, null);
        try {
            final IJavaProject javaProject = container.getJavaProject();
            final IProject project = javaProject.getProject();
            
            boolean languageModuleContainerFound = false;
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                if (entry != null && entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                    if (CeylonClasspathUtil.isLanguageModuleClasspathContainer(entry.getPath())) {
                        languageModuleContainerFound = true;
                        break;
                    }
                }
            }
            
            if (!languageModuleContainerFound && !retriedAfterAddingTheLanguageModuleEntry) {
                retriedAfterAddingTheLanguageModuleEntry = true;
                new CeylonLanguageModuleContainer(project).install();
                schedule(1000);
                return Status.OK_STATUS;
            }
            
            boolean changed = container.resolveClasspath(progress.newChild(800), true);
            if(changed) {
                container.refreshClasspathContainer(progress.newChild(200));
            }

            // Schedule a build of the project :
            //   - with referenced projects
            //   - with referencing projects (TODO : not sure it's really useful
            //        in the context of the project initialization, 
            //        but let's not change for the moment)
            //   - and don't rebuild if the model is already typechecked

            final Job buildJob = new BuildProjectAfterClasspathChangeJob("Initial build of project " + 
                    project.getName(), project, true, true, false);
            buildJob.setRule(project.getWorkspace().getRoot());
            buildJob.setPriority(Job.BUILD);
            
            // Before scheduling the Build Job, we will wait for the end of the classpath container initialization for 2 minutes 
            final long waitUntil = System.currentTimeMillis() + 120000;
            Job buildWhenAllContainersAreInitialized = new Job("Waiting for all dependencies to be initialized before building project " + project + " ...") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    if (! CeylonBuilder.allClasspathContainersInitialized()) {
                        if (System.currentTimeMillis() < waitUntil) {
                            //System.out.println("Waiting 1 seconde more for classpath container initialization before building project " + project + " ...");
                            schedule(1000);
                            return Status.OK_STATUS;
                        }
                        else {
                            //System.out.println("All the classpath containers are not initialized after 1 minute, so build project " + project + " anyway !");
                        }
                    }

                    boolean shouldSchedule = true;
                    for (Job job : getJobManager().find(buildJob)) {
                        if (job.getState() == Job.WAITING) {
                            //System.out.println("A build of project " + project + " is already scheduled. Finally don't schedule a new one after all classpath containers have been initialized");
                            shouldSchedule = false;
                            break;
                        }
                    }
            
            
                    if (shouldSchedule) {
                        //System.out.println("Scheduling build of project " + project + " after all classpath containers have been initialized");
                        buildJob.schedule();
                    }   
                    return Status.OK_STATUS;
                }
            };
            
            buildWhenAllContainersAreInitialized.setPriority(BUILD);
            buildWhenAllContainersAreInitialized.setSystem(true);
            buildWhenAllContainersAreInitialized.schedule();

            CeylonBuilder.setContainerInitialized(project);
            return Status.OK_STATUS;
            
        } 
        catch (JavaModelException ex) {
            // unless there are issues with the JDT, this should never happen
            return new Status(IStatus.ERROR, PLUGIN_ID,
                    "could not get container", ex);
        }
        finally {
            progress.destroy(null);
        }
    }

    @Override
    public boolean belongsTo(Object family) {
        if (family instanceof InitDependenciesJob) {
            InitDependenciesJob otherJob = (InitDependenciesJob) family;
            return container.getJavaProject().getProject().equals(otherJob.container.getJavaProject().getProject());
        }
        return false;
    }
}