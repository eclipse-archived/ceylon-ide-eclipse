package com.redhat.ceylon.eclipse.core.classpath;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaModelException;

import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;

public class InitDependenciesJob extends Job {
    
    private final CeylonProjectModulesContainer container;

    public InitDependenciesJob(String name, CeylonProjectModulesContainer container) {
        super(name);
        this.container = container;
    }

    @Override 
    protected IStatus run(IProgressMonitor monitor) {			
    	try {
    		boolean changed = container.resolveClasspath(monitor, true);

    		if(changed) {
        		container.refreshClasspathContainer(monitor);
        	}

        	// Schedule a build of the project :
            //   - with referenced projects
            //   - with referencing projects (TODO : not sure it's really useful
            //        in the context of the project initialization, 
            //        but let's not change for the moment)
            //   - and don't rebuild if the model is already typechecked

            final IProject p = container.getJavaProject().getProject();
            final Job buildJob = new BuildProjectAfterClasspathChangeJob("Initial build of project " + 
                    p.getName(), p, true, true, false);
            buildJob.setRule(p.getWorkspace().getRoot());
            buildJob.setPriority(Job.BUILD);
            
            // Before scheduling the Build Job, we will wait for the end of the classpath container initialization for 1 minute 
            final long waitUntil = System.currentTimeMillis() + 60000;
            Job buildWhenAllContainersAreInitialized = new Job("Waiting for all dependencies to be initialized before building project " + p + " ...") {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    if (! CeylonBuilder.allClasspathContainersInitialized()) {
                        if (System.currentTimeMillis() < waitUntil) {
                            System.out.println("Waiting 1 seconde more for classpath container initialization before building project " + p + " ...");
                            schedule(1000);
                            return Status.OK_STATUS;
                        }
                        else {
                            System.out.println("All the classpath containers are not initialized after 1 minute, so build project " + p + " anymway !");
                        }
                    }

                    boolean shouldSchedule = true;
                    for (Job job : getJobManager().find(buildJob)) {
                        if (job.getState() == Job.WAITING) {
                            System.out.println("A build of project " + p + " is already scheduled. Finally don't schedule a new one after all classpath containers have been initialized");
                            shouldSchedule = false;
                            break;
                        }
                    }
            
            
                    if (shouldSchedule) {
                        System.out.println("Scheduling build of project " + p + " after all classpath containers have been initialized");
                        buildJob.schedule();
                    }   
                    return Status.OK_STATUS;
                }
            };
            
            buildWhenAllContainersAreInitialized.setPriority(BUILD);
            buildWhenAllContainersAreInitialized.setSystem(true);
            buildWhenAllContainersAreInitialized.schedule(3000);

            CeylonBuilder.setContainerInitialized(p);
    		return Status.OK_STATUS;
    		
    	} 
    	catch (JavaModelException ex) {
    		// unless there are issues with the JDT, this should never happen
    		return new Status(IStatus.ERROR, PLUGIN_ID,
    				"could not get container", ex);
    	}
    }
    
}