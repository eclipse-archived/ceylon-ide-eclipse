/********************************************************************************
 * Copyright (c) 2011-2017 Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 1.0 which is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.correct;

import static org.eclipse.ceylon.compiler.typechecker.tree.TreeUtil.formatPath;
import static org.eclipse.ceylon.ide.eclipse.java2ceylon.Java2CeylonProxies.importsJ2C;
import static org.eclipse.ceylon.ide.eclipse.ui.CeylonResources.IMPORT;
import static org.eclipse.ceylon.ide.eclipse.util.ModuleQueries.getModuleQuery;

import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import org.eclipse.ceylon.cmr.api.ModuleQuery;
import org.eclipse.ceylon.cmr.api.ModuleSearchResult;
import org.eclipse.ceylon.cmr.api.ModuleSearchResult.ModuleDetails;
import org.eclipse.ceylon.common.Versions;
import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree;
import org.eclipse.ceylon.ide.eclipse.util.Highlights;
import org.eclipse.ceylon.model.cmr.JDKUtils;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Unit;

@Deprecated
class AddModuleImportProposal 
        implements ICompletionProposal, 
                   ICompletionProposalExtension6 {
    
    private final IProject project;
    private final Unit unit; 
    private final String name; 
    private final String version;
    
    AddModuleImportProposal(IProject project, Unit unit, 
            ModuleDetails details) {
        this.project = project;
        this.unit = unit;
        this.name = details.getName();
        this.version = details.getLastVersion().getVersion();
    }
    
    AddModuleImportProposal(IProject project, Unit unit, 
            String name, String version) {
        this.project = project;
        this.unit = unit;
        this.name = name;
        this.version = version;
    }
    
    @Override
    public void apply(IDocument document) {
        importsJ2C().importUtil().addModuleImport(project, 
                unit.getPackage().getModule(), 
                name, version);
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

    @Override
    public String getDisplayString() {
        return "Add 'import " + name + " \"" + version + "\"' to module descriptor";
    }

    @Override
    public Image getImage() {
        return IMPORT;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public StyledString getStyledDisplayString() {
        return Highlights.styleProposal(getDisplayString(), true);
    }
    
    static void addModuleImportProposals(
            final Collection<ICompletionProposal> proposals, 
            final IProject project, 
            final TypeChecker typeChecker, Node node) {
        final Unit unit = node.getUnit();
        if (unit.getPackage().getModule().isDefault()) {
            return;
        }
        if (node instanceof Tree.Import) {
            Tree.Import i = (Tree.Import) node;
            node = i.getImportPath();
        }
        Tree.ImportPath ip = (Tree.ImportPath) node;
        List<Tree.Identifier> ids = ip.getIdentifiers();
        final String pkg = formatPath(ids);
        if (JDKUtils.isJDKAnyPackage(pkg)) {
            TreeSet<String> moduleNames = 
                    new TreeSet<String>
                        (JDKUtils.getJDKModuleNames());
            for (String mod: moduleNames) {
                if (JDKUtils.isJDKPackage(mod, pkg)) {
                    proposals.add(new AddModuleImportProposal(
                            project, unit, mod, 
                            JDKUtils.jdk.version));
                    return;
                }
            }
        }
        Module module = unit.getPackage().getModule();
        ModuleQuery query = 
                getModuleQuery("", module, project);
        query.setMemberName(pkg);
        query.setMemberSearchPackageOnly(true);
        query.setMemberSearchExact(true);
        query.setCount(10l);
        query.setJvmBinaryMajor(Versions.JVM_BINARY_MAJOR_VERSION);
        query.setJvmBinaryMinor(Versions.JVM_BINARY_MINOR_VERSION);
        query.setJsBinaryMajor(Versions.JS_BINARY_MAJOR_VERSION);
        query.setJsBinaryMinor(Versions.JS_BINARY_MINOR_VERSION);
        ModuleSearchResult msr = 
                typeChecker
                    .getContext()
                    .getRepositoryManager()
                    .searchModules(query);
        for (ModuleDetails md: msr.getResults()) {
            proposals.add(new AddModuleImportProposal(
                    project, unit, md));
        }
        
        /*class Runnable implements IRunnableWithProgress {
            @Override
            public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, 
                           InterruptedException {
                monitor.beginTask(
                        "Querying module repositories...", 
                        IProgressMonitor.UNKNOWN);
                ModuleQuery query = 
                        getModuleQuery("", project);
                query.setMemberName(pkg);
                query.setMemberSearchPackageOnly(true);
                query.setMemberSearchExact(true);
                query.setCount(10l);
                query.setBinaryMajor(
                        Versions.JVM_BINARY_MAJOR_VERSION);
                ModuleSearchResult msr = 
                        typeChecker
                            .getContext()
                            .getRepositoryManager()
                            .searchModules(query);
                for (ModuleDetails md: msr.getResults()) {
                    proposals.add(new AddModuleImportProposal(
                            project, unit, md));
                }
                monitor.done();
            }
        }
        try {
            Display.getDefault()
                .syncExec(new java.lang.Runnable() {
                @Override
                public void run() {
                    try {
                        getWorkbench()
                            .getActiveWorkbenchWindow()
                            .run(true, true, new Runnable());
                    }
                    catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}
