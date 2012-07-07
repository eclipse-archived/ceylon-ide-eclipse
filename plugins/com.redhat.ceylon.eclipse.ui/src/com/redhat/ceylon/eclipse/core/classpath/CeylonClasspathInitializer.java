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

import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathContainer.runInitialize;
import static com.redhat.ceylon.eclipse.core.classpath.CeylonClasspathUtil.isCeylonClasspathContainer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IJavaProject;

/**
 * Initializes the Ceylon class path container. It will create 
 * a container from the persisted class path entries (the 
 * .classpath file), and then schedule the refresh of the 
 * container.
 */
public class CeylonClasspathInitializer extends ClasspathContainerInitializer {

    /**
     * Initialize the container with the "persisted" classpath 
     * entries, and then schedule the refresh.
     */
    public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
        if (isCeylonClasspathContainer(containerPath)) {
        	runInitialize(containerPath, project);
        }
    }

    public boolean canUpdateClasspathContainer(IPath containerPath, IJavaProject project) {
        return false;
    }

    public Object getComparisonID(IPath containerPath, IJavaProject project) {
        return project.getProject().getName() + "/" + containerPath;
    }
}
