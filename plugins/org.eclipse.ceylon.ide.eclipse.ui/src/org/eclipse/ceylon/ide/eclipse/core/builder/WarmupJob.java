/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.core.builder;

import static java.lang.Math.max;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.model.TypedDeclaration;

final class WarmupJob extends Job {
    private final IProject project;

    WarmupJob(IProject project) {
        super("Warming up completion processor for " + project.getName());
        this.project = project;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask("Warming up completion processor", 100000);
        Collection<Module> modules = CeylonBuilder.getProjectDeclaredSourceModules(project);
        monitor.worked(10000);
        try {
            for (Module m: modules) {
                List<Package> packages = m.getAllVisiblePackages();
                for (Package p: packages) {
                    if (p.isShared()) {
                        for (Declaration d: p.getMembers()) {
                            if (d.isShared()) {
                                if (d instanceof TypedDeclaration) {
                                    ((TypedDeclaration) d).getType();
                                }
                                //this one really slows it down!
                                /*if (d instanceof Functional) {
                                    ((Functional) d).getParameterLists();
                                }*/
                            }
                        }
                    }
                }
                monitor.worked(90000/max(modules.size(),1));
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }
            }
        }
        catch (ConcurrentModificationException cme) {
            //expected, if a build starts during warmup
        }
        monitor.done();
        return Status.OK_STATUS;
    }
}