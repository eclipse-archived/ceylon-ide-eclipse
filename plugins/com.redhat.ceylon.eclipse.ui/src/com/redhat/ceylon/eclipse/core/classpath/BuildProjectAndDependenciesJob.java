package com.redhat.ceylon.eclipse.core.classpath;

import static org.eclipse.core.resources.IncrementalProjectBuilder.INCREMENTAL_BUILD;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IBuildConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class BuildProjectAndDependenciesJob extends Job {
	protected final IProject project;

	public BuildProjectAndDependenciesJob(String name, IProject project) {
		super(name);
		this.project = project;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		if (reallyRun()) {
			try {
				List<IBuildConfiguration> configs = new ArrayList<IBuildConfiguration>();
				configs.add(project.getBuildConfig(IBuildConfiguration.DEFAULT_CONFIG_NAME));
				for (IProject p: project.getReferencingProjects()) {
					if (p.isOpen()) {
						configs.add(p.getBuildConfig(IBuildConfiguration.DEFAULT_CONFIG_NAME));
						//project.getWorkspace().build(FULL_BUILD, monitor);
						//break;
					} 
				}
				project.getWorkspace().build(configs.toArray(new IBuildConfiguration[1]), 
						INCREMENTAL_BUILD, false, monitor);    	            			
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return Status.OK_STATUS;
	}

	protected boolean reallyRun() {
		return true;
	}
}