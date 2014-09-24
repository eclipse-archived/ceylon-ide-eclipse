package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackage;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjects;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getUnits;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getProjectAndReferencedProjects;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getProjectAndReferencingProjects;
import static com.redhat.ceylon.eclipse.util.JavaSearch.isDeclarationOfLinkedElement;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.ALL_OCCURRENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.IMPLEMENTORS;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.READ_ACCESSES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.REFERENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.WRITE_ACCESSES;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
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
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.loader.ModelLoader;
import com.redhat.ceylon.compiler.loader.ModelLoader.DeclarationType;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.eclipse.util.FindAssignmentsVisitor;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.FindSubtypesVisitor;
import com.redhat.ceylon.eclipse.util.JavaSearch;

public class JavaQueryParticipant implements IQueryParticipant {
    
    @Override
    public void search(ISearchRequestor requestor, 
            QuerySpecification querySpecification, 
            IProgressMonitor monitor)
            throws CoreException {
        if (querySpecification instanceof ElementQuerySpecification) {
            final IJavaElement element = 
                    ((ElementQuerySpecification) querySpecification).getElement();
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
            IProject elementProject = element.getJavaProject().getProject();
            IPackageFragment packageFragment = (IPackageFragment) 
                    element.getAncestor(PACKAGE_FRAGMENT);
            DeclarationType declarationType = 
                    element instanceof IType ? 
                        ModelLoader.DeclarationType.TYPE : 
                        ModelLoader.DeclarationType.VALUE;
            
            IFolder folder = (IFolder) packageFragment.getResource();
            Package pack = folder==null ? null : getPackage(folder);
            Declaration declaration;
            if (pack==null) {
                //this is the case for Ceylon decs, since 
                //they sit in the .exploded directory
                declaration = getCeylonDeclaration(elementProject, element);
            }
            else {
                //this is the case for Java decs
                IType type = (IType) element.getAncestor(IJavaElement.TYPE);
                String qualifiedName = JavaSearch.getQualifiedName(type);
                declaration = 
                        getProjectModelLoader(elementProject)
                                .convertToDeclaration(pack.getModule(), 
                                        qualifiedName, declarationType);
                if (type!=element && declaration!=null) {
                    declaration = declaration.getMember(element.getElementName(), null, false);
                }
                
            }
            if (declaration==null) return;

            Collection<IProject> ceylonProjects = getProjects();
            for (IProject project: getProjectAndReferencingProjects(elementProject)) {
                if (ceylonProjects.contains(project)) {
                    IJavaProject javaProject = JavaCore.create(project);
                    for (IPackageFragmentRoot sourceFolder: 
                            javaProject.getAllPackageFragmentRoots()) {
                        if (querySpecification.getScope().encloses(sourceFolder)) {
                            for (PhasedUnit pu: getUnits(project)) {
                                CompilationUnit cu = pu.getCompilationUnit();
                                Set<? extends Node> nodes;
                                if (limitTo==WRITE_ACCESSES) {
                                    FindAssignmentsVisitor fav = 
                                            new FindAssignmentsVisitor(declaration);
                                    fav.visit(cu);
                                    nodes = fav.getNodes();
                                }
                                else if (limitTo==IMPLEMENTORS) {
                                    FindSubtypesVisitor fsv = 
                                            new FindSubtypesVisitor((TypeDeclaration) declaration);
                                    fsv.visit(cu);
                                    nodes = fsv.getDeclarationNodes();
                                }
                                else if (limitTo==REFERENCES || 
                                         limitTo==READ_ACCESSES) {  //TODO: support this properly!!
                                    FindReferencesVisitor frv = 
                                            new FindReferencesVisitor(declaration);
                                    frv.visit(cu);
                                    nodes = frv.getNodes();
                                }
                                else {
                                    //ALL_OCCURRENCES
                                    FindReferencesVisitor frv = 
                                            new FindReferencesVisitor(declaration);
                                    frv.visit(cu);
                                    nodes = frv.getNodes();
                                    if (declaration instanceof TypeDeclaration) {
                                        FindSubtypesVisitor fsv = 
                                                new FindSubtypesVisitor((TypeDeclaration) declaration);
                                        fsv.visit(cu);
                                        HashSet<Node> result = new HashSet<Node>();
                                        result.addAll(nodes);
                                        result.addAll(fsv.getDeclarationNodes());
                                        nodes = result;
                                    }
                                }
                                for (Node node: nodes) {
                                    if (node.getToken()==null) {
                                        //a synthetic node inserted in the tree
                                    }
                                    else {
                                        Tree.StatementOrArgument container;
                                        if (node instanceof Tree.Declaration) {
                                            container = (Tree.StatementOrArgument) node;
                                        }
                                        else {
                                            FindContainerVisitor fcv = 
                                                    new FindContainerVisitor(node);
                                            cu.visit(fcv);
                                            container = fcv.getStatementOrArgument();
                                        }
                                        CeylonSearchMatch match = 
                                                new CeylonSearchMatch(node, container, 
                                                        pu.getUnitFile());
                                        requestor.reportMatch(match);
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

    private static Declaration getCeylonDeclaration(IProject project, IJavaElement javaElement) {
        Collection<IProject> projects = getProjects();
        for (IProject referencedProject: getProjectAndReferencedProjects(project)) {
            if (projects.contains(referencedProject)) {
                TypeChecker typeChecker = getProjectTypeChecker(referencedProject);
                if (typeChecker!=null) {
                    for (PhasedUnit pu: typeChecker.getPhasedUnits().getPhasedUnits()) {
                        for (Declaration declaration: pu.getDeclarations()) {
                            if (isDeclarationOfLinkedElement(declaration, javaElement)) {
                                return declaration;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int estimateTicks(QuerySpecification specification) {
        return 1;
    }

    @Override
    public IMatchPresentation getUIParticipant() {
        return new IMatchPresentation() {
            @Override
            public void showMatch(Match match, int offset, int length,
                    boolean activate) throws PartInitException {
                CeylonElement element = (CeylonElement) match.getElement();
                IFile file = element.getFile();
                Navigation.gotoLocation(file, offset, length);
            }
            @Override
            public ILabelProvider createLabelProvider() {
                return new SearchResultsLabelProvider();
            }
        };
    }

}
