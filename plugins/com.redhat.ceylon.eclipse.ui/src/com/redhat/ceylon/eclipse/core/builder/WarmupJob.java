package com.redhat.ceylon.eclipse.core.builder;

import static java.lang.Math.max;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypedDeclaration;

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
                List<Package> packages = m.getAllPackages();
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