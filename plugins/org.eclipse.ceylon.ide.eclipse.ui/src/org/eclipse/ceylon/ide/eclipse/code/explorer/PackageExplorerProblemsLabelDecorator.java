/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.explorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerProblemsDecorator;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;

public class PackageExplorerProblemsLabelDecorator extends PackageExplorerProblemsDecorator {
    
//    private boolean flatPackageMode;
//    
//    public ProbsLabelDecorator(boolean flatPackageMode) {
//        this.flatPackageMode = flatPackageMode;
//    }
    
    @Override
    protected int computeAdornmentFlags(Object obj) {
        //if (!flatPackageMode && !fragment.isDefaultPackage()) {
        if (obj instanceof IJavaElement) {
            IJavaElement element= (IJavaElement) obj;
            int type= element.getElementType();
            if (type==IJavaElement.PACKAGE_FRAGMENT) {
                IPackageFragment fragment = (IPackageFragment) obj;
                try {
                    for (Object o: fragment.getNonJavaResources()) {
                        if (o instanceof IFile) {
                            IFile file = (IFile) o;
                            String ext = file.getFileExtension();
                            if (ext!=null && ext.equals("ceylon")) {
                                if (file.getName().equals("module.ceylon")) {
                                    int flags= getErrorTicksFromMarkers(element.getResource());
                                    //TODO: see ProblemsLabelDecorator
//                                    if (flags != ERRORTICK_ERROR)
//                                            && isIgnoringOptionalProblems(root.getRawClasspathEntry())) {
//                                        flags= ERRORTICK_IGNORE_OPTIONAL_PROBLEMS;
//                                    }
                                    return flags;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.computeAdornmentFlags(obj);
    }

    private int getErrorTicksFromMarkers(IResource res) throws CoreException {
        if (res == null || !res.isAccessible()) {
            return 0;
        }
        int severity= 0;
//        if (sourceElement == null) {
//            if (res instanceof IProject) {
//                severity= res.findMaxProblemSeverity(IJavaModelMarker.BUILDPATH_PROBLEM_MARKER, true, IResource.DEPTH_ZERO);
//                if (severity == IMarker.SEVERITY_ERROR) {
//                    return ERRORTICK_BUILDPATH_ERROR;
//                }
//                severity= res.findMaxProblemSeverity(JavaRuntime.JRE_CONTAINER_MARKER, true, IResource.DEPTH_ZERO);
//                if (severity == IMarker.SEVERITY_ERROR) {
//                    return ERRORTICK_BUILDPATH_ERROR;
//                }
//            }
            severity= res.findMaxProblemSeverity(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
//        } else {
//            IMarker[] markers= res.findMarkers(IMarker.PROBLEM, true, depth);
//            if (markers != null && markers.length > 0) {
//                for (int i= 0; i < markers.length && (severity != IMarker.SEVERITY_ERROR); i++) {
//                    IMarker curr= markers[i];
//                    if (isMarkerInRange(curr, sourceElement)) {
//                        int val= curr.getAttribute(IMarker.SEVERITY, -1);
//                        if (val == IMarker.SEVERITY_WARNING || val == IMarker.SEVERITY_ERROR) {
//                            severity= val;
//                        }
//                    }
//                }
//            }
//        }
        if (severity == IMarker.SEVERITY_ERROR) {
            return JavaElementImageDescriptor.ERROR;
        } else if (severity == IMarker.SEVERITY_WARNING) {
            return JavaElementImageDescriptor.WARNING;
        }
        return 0;
    }

}
