/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-1.0
 ********************************************************************************/
package org.eclipse.ceylon.ide.eclipse.code.search;

import static org.eclipse.ceylon.ide.eclipse.code.editor.Navigation.gotoLocation;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getPackage;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectModelLoader;
import static org.eclipse.ceylon.ide.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.getCeylonSimpleName;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.getJavaQualifiedName;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.getProjectsToSearch;
import static org.eclipse.ceylon.ide.eclipse.util.JavaSearch.toCeylonDeclaration;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.IJavaElement.TYPE;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.ALL_OCCURRENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.IMPLEMENTORS;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.READ_ACCESSES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.REFERENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.WRITE_ACCESSES;
import static org.eclipse.search.ui.NewSearchUI.getSearchResultView;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;

import org.eclipse.ceylon.compiler.typechecker.TypeChecker;
import org.eclipse.ceylon.compiler.typechecker.context.PhasedUnit;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;
import org.eclipse.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import org.eclipse.ceylon.ide.eclipse.core.builder.CeylonNature;
import org.eclipse.ceylon.ide.common.model.BaseIdeModule;
import org.eclipse.ceylon.ide.common.util.FindAssignmentsVisitor;
import org.eclipse.ceylon.ide.common.util.FindReferencesVisitor;
import org.eclipse.ceylon.ide.common.util.FindSubtypesVisitor;
import org.eclipse.ceylon.model.loader.ModelLoader;
import org.eclipse.ceylon.model.typechecker.model.Declaration;
import org.eclipse.ceylon.model.typechecker.model.Module;
import org.eclipse.ceylon.model.typechecker.model.Modules;
import org.eclipse.ceylon.model.typechecker.model.Package;
import org.eclipse.ceylon.model.typechecker.model.TypeDeclaration;

public class JavaQueryParticipant 
        implements IQueryParticipant, IMatchPresentation {
    
    @Override
    public void showMatch(Match match, 
            int offset, int length,
            boolean activate) 
                    throws PartInitException {
        CeylonElement element = 
                (CeylonElement) match.getElement();
        IFile file = element.getFile();
        if (file==null) {
            String path = element.getVirtualFile().getPath();
            gotoLocation(new Path(path), offset, length);
        }
        else {
            gotoLocation(file, offset, length);
        }
    }

    @Override
    public ILabelProvider createLabelProvider() {
        AbstractTextSearchViewPage activePage = 
                (AbstractTextSearchViewPage) 
                    getSearchResultView()
                        .getActivePage();
        return new MatchCountingLabelProvider(activePage);
    }

    private static Field searchResultField;
    private static Field participantsField;
    
    static {
        try {
            Class<?> clazz = Class.forName("org.eclipse.jdt.internal.ui.search.JavaSearchQuery$SearchRequestor");
            searchResultField = clazz.getDeclaredField("fSearchResult");
            searchResultField.setAccessible(true);
            clazz = Class.forName("org.eclipse.jdt.internal.ui.search.JavaSearchResult");
            participantsField = clazz.getDeclaredField("fElementsToParticipants");
            participantsField.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void search(ISearchRequestor requestor, 
            QuerySpecification querySpecification, 
            IProgressMonitor monitor)
            throws CoreException {
        if (querySpecification 
                instanceof ElementQuerySpecification) {
            ElementQuerySpecification eqs = 
                    (ElementQuerySpecification) 
                        querySpecification;
            final IJavaElement element = 
                    eqs.getElement();
            if (!(element instanceof IType || 
                  element instanceof IMethod || 
                  element instanceof IField) ||
                  element.getJavaProject()==null) {
                return;
            }
            int limitTo = querySpecification.getLimitTo();
            if (limitTo!=REFERENCES &&
                limitTo!=IMPLEMENTORS &&
                limitTo!=ALL_OCCURRENCES &&
                limitTo!=READ_ACCESSES &&
                limitTo!=WRITE_ACCESSES) {
                return;
            }
            IProject elementProject = 
                    element.getJavaProject()
                        .getProject();
            IPackageFragment packageFragment = 
                    (IPackageFragment) 
                        element.getAncestor(PACKAGE_FRAGMENT);
            
            Package pack = 
                    packageFragment==null ? null : 
                        getPackage(packageFragment);
            Declaration declaration;
            if (pack==null) {
                //this is the case for Ceylon decs, since 
                //they sit in the .exploded directory
                declaration = 
                        toCeylonDeclaration(elementProject, 
                                element);
                if (declaration!=null) {
                    pack = declaration.getUnit().getPackage();
                }
            }
            else {
                //this is the case for Java decs
                IType type = 
                        (IType) 
                            element.getAncestor(TYPE);
                String typeQualifiedName = 
                        getJavaQualifiedName(type);
                String ceylonMemberName = 
                        type==element ? null :
                                getCeylonSimpleName(
                                        (IMember) element);
                String typeName = type.getElementName();
                int first = typeName.codePointAt(0);
                if (!Character.isUpperCase(first)
                    && typeName.endsWith("_") 
                    && ceylonMemberName == null) {
                    // Ceylon object value ... 
                    // ... without a method call
                    declaration = 
                            getProjectModelLoader(elementProject)
                                .convertToDeclaration(
                                        pack.getModule(), 
                                        typeQualifiedName, 
                                        ModelLoader.DeclarationType.VALUE);
                } else {
                    declaration = 
                            getProjectModelLoader(elementProject)
                                .convertToDeclaration(
                                        pack.getModule(), 
                                        typeQualifiedName, 
                                        ModelLoader.DeclarationType.TYPE);
                    if (declaration!=null 
                            && ceylonMemberName != null 
                            && element instanceof IMember) {
                        declaration = 
                                declaration.getMember(
                                        ceylonMemberName, 
                                        null, false);
                    }
                }
            }
            if (declaration==null) return;
            
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            
            Set<String> searchedArchives = 
                    new HashSet<String>();
            IWorkspaceRoot root = 
                    elementProject.getWorkspace()
                        .getRoot();
            
            for (IPath includedProjectOrJar: 
                    querySpecification.getScope()
                        .enclosingProjectsAndJars()) {
                IProject project = null;
                if (includedProjectOrJar.segmentCount()==1) {
                    String prefix = 
                            includedProjectOrJar.segment(0);
                    project = root.getProject(prefix);
                    if (!project.exists()) {
                        project = null;
                    }
                }
                if (project == null) {
                    continue;
                }
                for (IProject searchedProject: 
                        getProjectsToSearch(project)) {
                    if (CeylonNature.isEnabled(searchedProject)) {
                        IJavaProject javaProject = 
                                JavaCore.create(searchedProject);
                        for (IPackageFragmentRoot sourceFolder: 
                                javaProject.getAllPackageFragmentRoots()) {
                            if (sourceFolder.getKind()==
                                        IPackageFragmentRoot.K_SOURCE &&
                                    querySpecification.getScope()
                                        .encloses(sourceFolder)) {
                                TypeChecker typeChecker = 
                                        getProjectTypeChecker(
                                                searchedProject);
                                searchInUnits(requestor, 
                                        limitTo, declaration, 
                                        typeChecker.getPhasedUnits()
                                            .getPhasedUnits());
                                if (monitor.isCanceled()) {
                                    throw new OperationCanceledException();
                                }
                                Modules modules = 
                                        typeChecker.getContext()
                                            .getModules();
                                for (Module mod: 
                                        modules.getListOfModules()) {
                                    if (mod instanceof BaseIdeModule) {
                                        BaseIdeModule module = 
                                                (BaseIdeModule) mod;
                                        if (module.getIsCeylonArchive() && 
                                                module.getArtifact()!=null) {
                                            String archivePath = 
                                                    module.getArtifact()
                                                        .getAbsolutePath();
                                            if (searchedArchives.add(archivePath) && 
                                                    mod.getAllReachablePackages()
                                                        .contains(pack)) {
                                                searchInUnits(requestor, 
                                                        limitTo, declaration, 
                                                        module.getPhasedUnitsAsJavaList());
                                                if (monitor.isCanceled()) {
                                                    throw new OperationCanceledException();
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void searchInUnits(ISearchRequestor requestor, 
            int limitTo,
            Declaration declaration, 
            List<? extends PhasedUnit> units) {
        for (PhasedUnit pu: units) {
            CompilationUnit cu = pu.getCompilationUnit();
            for (Node node: 
                    findNodes(limitTo, declaration, cu)) {
                if (node.getToken()==null) {
                    //a synthetic node inserted in the tree
                }
                else {
                    CeylonSearchMatch match = 
                            CeylonSearchMatch.create(node, 
                                    cu, pu.getUnitFile());
                    if (searchResultField!=null && 
                        participantsField!=null) {
                        //nasty nasty workaround for stupid 
                        //behavior of reportMatch()
                        try {
                            reportMatchBypassingRubbishApi(
                                    requestor, match);
                            continue;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    requestor.reportMatch(match);
                }
            }
        }
    }

    private void reportMatchBypassingRubbishApi(
            ISearchRequestor requestor,
            CeylonSearchMatch match) 
                    throws IllegalAccessException {
        AbstractTextSearchResult searchResult = 
                (AbstractTextSearchResult) 
                    searchResultField.get(requestor);
        searchResult.addMatch(match);
        @SuppressWarnings("unchecked")
        Map<Object, IMatchPresentation> participants = 
                (Map<Object, IMatchPresentation>) 
                    participantsField.get(searchResult);
        participants.put(match.getElement(), this);
    }

    private Set<? extends Node> findNodes(int limitTo, 
            Declaration declaration,
            CompilationUnit cu) {
        Set<? extends Node> nodes;
        if (limitTo==WRITE_ACCESSES) {
            FindAssignmentsVisitor fav = 
                    new FindAssignmentsVisitor(declaration);
            fav.visit(cu);
            nodes = fav.getAssignmentNodeSet();
        }
        else if (limitTo==IMPLEMENTORS) {
            FindSubtypesVisitor fsv = 
                    new FindSubtypesVisitor(
                            (TypeDeclaration) 
                                declaration);
            fsv.visit(cu);
            nodes = fsv.getDeclarationNodeSet();
        }
        else if (limitTo==REFERENCES || 
                 limitTo==READ_ACCESSES) {  //TODO: support this properly!!
            FindReferencesVisitor frv = 
                    new FindReferencesVisitor(declaration);
            frv.visit(cu);
            nodes = frv.getReferenceNodeSet();
        }
        else {
            //ALL_OCCURRENCES
            FindReferencesVisitor frv = 
                    new FindReferencesVisitor(declaration);
            frv.visit(cu);
            nodes = frv.getReferenceNodeSet();
            if (declaration instanceof TypeDeclaration) {
                FindSubtypesVisitor fsv = 
                        new FindSubtypesVisitor(
                                (TypeDeclaration) 
                                    declaration);
                fsv.visit(cu);
                HashSet<Node> result = new HashSet<Node>();
                result.addAll(nodes);
                result.addAll(fsv.getDeclarationNodeSet());
                nodes = result;
            }
        }
        return nodes;
    }

    @Override
    public int estimateTicks(QuerySpecification specification) {
        //TODO!
        return 1;
    }

    @Override
    public IMatchPresentation getUIParticipant() {
        return this;
    }

}
