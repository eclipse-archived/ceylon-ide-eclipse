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
package org.eclipse.ceylon.ide.eclipse.core.classpath;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * This class seems to be a hack, but it seems to work a lot better with non-null project. So a
 * Ceylon container will always be attached to a project. But some will be fake, the ones for the
 * launch configurations.
 * 
 * see also:
 * org.eclipse.jdt.internal.ui.preferencesUserLibraryPreferencePage#createPlaceholderProject()
 */
public final class FakeProjectManager {

    private FakeProjectManager() {
        // utility class
    }

    public static boolean isFake(IJavaProject project) {
        // a fake project doesn't have real path
        return project.getProject().getLocation() == null;
    }

    public static IJavaProject createPlaceholderProject() {
        String name = "####ceylon-launch"; //$NON-NLS-1$
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        while (true) {
            IProject project = root.getProject(name);
            if (!project.exists()) {
                return JavaCore.create(project);
            }
            name += '1';
        }
    }

}
