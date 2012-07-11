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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.redhat.ceylon.eclipse.ui.CeylonPlugin;

public final class CeylonClasspathUtil {

    private CeylonClasspathUtil() {
        // utility class
    }

    /**
     * Get the Ivy classpath container from the selection in the Java package view
     * 
     * @param selection
     *            the selection
     * @return
     * @throws JavaModelException
     */
    public static CeylonClasspathContainer getCeylonClasspathContainer(IStructuredSelection selection) {
        if (selection == null) {
            return null;
        }
        for (Iterator it = selection.iterator(); it.hasNext();) {
            Object element = it.next();
            CeylonClasspathContainer cp = (CeylonClasspathContainer) CeylonPlugin.adapt(element,
                CeylonClasspathContainer.class);
            if (cp != null) {
                return cp;
            }
            if (element instanceof ClassPathContainer) {
                // FIXME: we shouldn't check against internal JDT API but there are not adaptable to
                // useful class
                return jdt2CeylonCPC((ClassPathContainer) element);
            }
        }
        return null;
    }

    /**
     * Work around the non adaptability of ClassPathContainer
     * 
     * @param cpc
     *            the container to transform into an CeylonClasspathContainer
     * @return the CeylonClasspathContainer is such, null, if not
     */
    public static CeylonClasspathContainer jdt2CeylonCPC(ClassPathContainer cpc) {
        IClasspathEntry entry = cpc.getClasspathEntry();
        try {
            IClasspathContainer icp = JavaCore.getClasspathContainer(entry.getPath(), cpc
                    .getJavaProject());
            if (icp instanceof CeylonClasspathContainer) {
                return (CeylonClasspathContainer) icp;
            }
        } catch (JavaModelException e) {
            // unless there are issues with the JDT, this should never happen
            CeylonPlugin.log(e);
        }
        return null;
    }

    public static boolean isCeylonClasspathContainer(IPath containerPath) {
        return containerPath.segment(0).equals(CeylonClasspathContainer.CONTAINER_ID);
    }

    /**
     * Search the Ceylon classpath containers within the specified Java project
     * 
     * @param javaProject
     *            the project to search into
     * @return the Ivy classpath container if found
     */
    public static List <CeylonClasspathContainer> getCeylonClasspathContainers(
            IJavaProject javaProject) {
        List<CeylonClasspathContainer> containers = new ArrayList<CeylonClasspathContainer>();
        if (FakeProjectManager.isFake(javaProject) || !javaProject.exists()) {
            return containers;
        }
        try {
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                if (entry != null && entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                    IPath path = entry.getPath();
                    if (isCeylonClasspathContainer(path)) {
                        IClasspathContainer cp = JavaCore.getClasspathContainer(path, javaProject);
                        if (cp instanceof CeylonClasspathContainer) {
                            containers.add((CeylonClasspathContainer) cp);
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            // unless there are issues with the JDT, this should never happen
            CeylonPlugin.log(e);
        }
        return containers;
    }


    public static List<String> split(String str) {
        String[] terms = str.split(",");
        List<String> ret = new ArrayList<String>();
        for (int i = 0; i < terms.length; i++) {
            String t = terms[i].trim();
            if (t.length() > 0) {
                ret.add(t);
            }
        }
        return ret;
    }

    public static String concat(Collection<String> list) {
        if (list == null) {
            return "";
        }
        StringBuffer b = new StringBuffer();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            b.append(it.next());
            if (it.hasNext()) {
                b.append(",");
            }
        }
        return b.toString();
    }

    /**
     * Just a verbatim copy of the internal Eclipse function:
     * org.eclipse.jdt.internal.corext.javadoc
     * .JavaDocLocations#getLibraryJavadocLocation(IClasspathEntry)
     * 
     * @param entry
     * @return
     */
    public static URL getLibraryJavadocLocation(IClasspathEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry must not be null"); //$NON-NLS-1$
        }

        int kind = entry.getEntryKind();
        if (kind != IClasspathEntry.CPE_LIBRARY && kind != IClasspathEntry.CPE_VARIABLE) {
            throw new IllegalArgumentException(
                    "Entry must be of kind CPE_LIBRARY or " + "CPE_VARIABLE"); //$NON-NLS-1$
        }

        IClasspathAttribute[] extraAttributes = entry.getExtraAttributes();
        for (int i = 0; i < extraAttributes.length; i++) {
            IClasspathAttribute attrib = extraAttributes[i];
            if (IClasspathAttribute.JAVADOC_LOCATION_ATTRIBUTE_NAME.equals(attrib.getName())) {
                try {
                    return new URL(attrib.getValue());
                } catch (MalformedURLException e) {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * Search the Ivy classpath entry within the specified Java project with the specific path
     * 
     * @param containerPath
     *            the path of the container
     * @param javaProject
     *            the project to search into
     * @return the Ivy classpath container if found, otherwise return <code>null</code>
     */
    public static IClasspathEntry getCeylonClasspathEntry(IPath containerPath,
            IJavaProject javaProject) {
        if (FakeProjectManager.isFake(javaProject) || !javaProject.exists()) {
            return null;
        }
        try {
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                if (entry != null && entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                    if (containerPath.equals(entry.getPath())) {
                        return entry;
                    }
                }
            }
        } catch (JavaModelException e) {
            // unless there are issues with the JDT, this should never happen
            CeylonPlugin.log(e);
        }
        return null;
    }

}
