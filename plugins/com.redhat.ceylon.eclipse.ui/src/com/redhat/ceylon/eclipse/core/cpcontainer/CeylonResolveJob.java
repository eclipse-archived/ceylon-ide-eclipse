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
package com.redhat.ceylon.eclipse.core.cpcontainer;

import static org.eclipse.core.resources.ResourcesPlugin.getWorkspace;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

/**
 * An asynchronous Job that sets up the classpath entries of
 * the Ceylon Modules classpath container.
 */
public class CeylonResolveJob extends Job {

    private final CeylonClasspathContainer container;

    public CeylonResolveJob(CeylonClasspathContainer container) {
        super("Ceylon dependency resolution for project " + 
                container.getJavaProject().getElementName());
        this.container = container;
    }

    protected IStatus run(IProgressMonitor monitor) {
        //try {
            try {
                getWorkspace().run(new IWorkspaceRunnable() {
					//The following code requires a lock on the workspace to
					//avoid concurrent access to the model
				    @Override
				    public void run(IProgressMonitor monitor) throws CoreException {
				    	container.resolveClasspath(monitor);//, false);
				    }

				}, monitor);
                return Status.OK_STATUS;
            } 
            catch (CoreException e) {
                e.printStackTrace();
                return new Status(IStatus.ERROR, CeylonPlugin.PLUGIN_ID,
                		"could not resolve dependencies", e);
            }            
        /*} 
        finally {
            container.resetJob();
        }*/
    }

}
