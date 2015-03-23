package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.code.editor.Navigation.gotoLocation;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getPackage;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectModelLoader;
import static com.redhat.ceylon.eclipse.core.builder.CeylonBuilder.getProjectTypeChecker;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getProjectsToSearch;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getJavaQualifiedName;
import static com.redhat.ceylon.eclipse.util.JavaSearch.getCeylonSimpleName;
import static com.redhat.ceylon.eclipse.util.JavaSearch.toCeylonDeclaration;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.ALL_OCCURRENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.IMPLEMENTORS;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.READ_ACCESSES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.REFERENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.WRITE_ACCESSES;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.loader.ModelLoader;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.Module;
import com.redhat.ceylon.compiler.typechecker.model.Modules;
import com.redhat.ceylon.compiler.typechecker.model.Package;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.core.builder.CeylonNature;
import com.redhat.ceylon.eclipse.core.model.JDTModule;
import com.redhat.ceylon.eclipse.util.FindAssignmentsVisitor;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.FindSubtypesVisitor;

public class JavaQueryParticipant implements IQueryParticipant, IMatchPresentation {
    
    @Override
    public void showMatch(Match match, int offset, int length,
            boolean activate) throws PartInitException {
        CeylonElement element = (CeylonElement) match.getElement();
        IFile file = element.getFile();
        if (file==null) {
            Path path = new Path(element.getVirtualFile().getPath());
            gotoLocation(path, offset, length);
        }
        else {
            gotoLocation(file, offset, length);
        }
    }

    @Override
    public ILabelProvider createLabelProvider() {
        AbstractTextSearchViewPage activePage = (AbstractTextSearchViewPage) 
                NewSearchUI.getSearchResultView().getActivePage();
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
            
            IFolder folder = (IFolder) packageFragment.getResource();
            Package pack = folder==null ? null : getPackage(folder);
            Declaration declaration;
            if (pack==null) {
                //this is the case for Ceylon decs, since 
                //they sit in the .exploded directory
                declaration = toCeylonDeclaration(elementProject, element);
                if (declaration!=null) {
                    pack = declaration.getUnit().getPackage();
                }
            }
            else {
                //this is the case for Java decs
                IType type = (IType) element.getAncestor(IJavaElement.TYPE);
                String typeQualifiedName = getJavaQualifiedName(type);
                String ceylonMemberName = type!=element ? 
                        getCeylonSimpleName((IMember)element) : null;
                String typeName = type.getElementName();
                if (!Character.isUpperCase(typeName.charAt(0))
                    && typeName.endsWith("_") 
                    && ceylonMemberName == null) {
                    // Ceylon object value ... 
                    // ... without a method call
                    declaration = 
                            getProjectModelLoader(elementProject)
                            .convertToDeclaration(pack.getModule(), 
                                    typeQualifiedName, ModelLoader.DeclarationType.VALUE);
                } else {
                    declaration = 
                            getProjectModelLoader(elementProject)
                            .convertToDeclaration(pack.getModule(), 
                                    typeQualifiedName, ModelLoader.DeclarationType.TYPE);
                    if (declaration!=null 
                            && ceylonMemberName != null 
                            && element instanceof IMember) {
                        declaration = declaration.getMember(ceylonMemberName, null, false);
                    }
                }
            }
            if (declaration==null) return;
            
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
            
            Set<String> searchedArchives = new HashSet<String>();
            IWorkspaceRoot root = elementProject.getWorkspace().getRoot();
            
            for (IPath includedProjectOrJar : querySpecification.getScope().enclosingProjectsAndJars()) {
                IProject project = null;
                if (includedProjectOrJar.segmentCount() == 1) {
                    project = root.getProject(includedProjectOrJar.segment(0));
                    if (! project.exists()) {
                        project = null;
                    }
                }
                if (project == null) {
                    continue;
                }
                for (IProject searchedProject: getProjectsToSearch(project.getProject())) {
                    if (CeylonNature.isEnabled(searchedProject)) {
                        IJavaProject javaProject = JavaCore.create(searchedProject);
                        for (IPackageFragmentRoot sourceFolder: 
                                javaProject.getAllPackageFragmentRoots()) {
                            if (sourceFolder.getKind()==IPackageFragmentRoot.K_SOURCE &&
                                    querySpecification.getScope().encloses(sourceFolder)) {
                                TypeChecker typeChecker = getProjectTypeChecker(searchedProject);
                                searchInUnits(requestor, limitTo, declaration, 
                                        typeChecker.getPhasedUnits().getPhasedUnits());
                                if (monitor.isCanceled()) {
                                    throw new OperationCanceledException();
                                }
                                Modules modules = typeChecker.getContext().getModules();
                                for (Module m: modules.getListOfModules()) {
                                    if (m instanceof JDTModule) {
                                        JDTModule module = (JDTModule) m;
                                        if (module.isCeylonArchive() && module.getArtifact()!=null) {
                                            String archivePath = module.getArtifact().getAbsolutePath();
                                            if (searchedArchives.add(archivePath) && 
                                                    m.getAllReachablePackages().contains(pack)) {
                                                searchInUnits(requestor, limitTo, declaration, 
                                                        module.getPhasedUnits());
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

    private void searchInUnits(ISearchRequestor requestor, int limitTo,
            Declaration declaration, List<? extends PhasedUnit> units) {
        for (PhasedUnit pu: units) {
            CompilationUnit cu = pu.getCompilationUnit();
            for (Node node: findNodes(limitTo, declaration, cu)) {
                if (node.getToken()==null) {
                    //a synthetic node inserted in the tree
                }
                else {
                    CeylonSearchMatch match = 
                            CeylonSearchMatch.create(node, cu, pu.getUnitFile());
                    if (searchResultField!=null && participantsField!=null) {
                        //nasty nasty workaround for stupid 
                        //behavior of reportMatch()
                        try {
                            reportMatchBypassingRubbishApi(requestor, match);
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

    private void reportMatchBypassingRubbishApi(ISearchRequestor requestor,
            CeylonSearchMatch match) throws IllegalAccessException {
        AbstractTextSearchResult searchResult = 
                (AbstractTextSearchResult) searchResultField.get(requestor);
        searchResult.addMatch(match);
        @SuppressWarnings("unchecked")
        Map<Object, IMatchPresentation> participants = 
                (Map<Object, IMatchPresentation>) participantsField.get(searchResult);
        participants.put(match.getElement(), this);
    }

    private Set<? extends Node> findNodes(int limitTo, Declaration declaration,
            CompilationUnit cu) {
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
