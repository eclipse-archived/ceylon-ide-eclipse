package com.redhat.ceylon.eclipse.code.search;

import static org.eclipse.jdt.core.search.IJavaSearchConstants.ALL_OCCURRENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.READ_ACCESSES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.REFERENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.WRITE_ACCESSES;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.search.ElementQuerySpecification;
import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jdt.ui.search.IQueryParticipant;
import org.eclipse.jdt.ui.search.ISearchRequestor;
import org.eclipse.jdt.ui.search.QuerySpecification;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationKind;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindAssignmentsVisitor;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;

public class JavaQueryParticipant implements IQueryParticipant {

    @Override
    public void search(ISearchRequestor requestor, 
            QuerySpecification querySpecification, 
            IProgressMonitor monitor)
            throws CoreException {
        if (querySpecification instanceof ElementQuerySpecification) {
            final IJavaElement element = ((ElementQuerySpecification) querySpecification).getElement();
            if (!(element instanceof IMember)) return;
            int limitTo = querySpecification.getLimitTo();
            if (limitTo!=REFERENCES &&
                limitTo!=ALL_OCCURRENCES &&
                limitTo!=READ_ACCESSES && //TODO: support this properly!!
                limitTo!=WRITE_ACCESSES) {
                return;
            }
            final String qualifiedName = getQualifiedName((IMember) element);
            Declaration d = new Declaration() {
                @Override
                public boolean equals(Object object) {
                    if (object instanceof Declaration) {
                        return ((Declaration) object).getQualifiedNameString()
                                .equals(qualifiedName);
                    }
                    return false;
                }
                @Override
                public ProducedReference getProducedReference(ProducedType pt,
                        List<ProducedType> typeArguments) {
                    return null;
                }
                @Override
                public ProducedReference getReference() {
                    return null;
                }
                @Override
                public DeclarationKind getDeclarationKind() {
                    return null;
                }
                @Override
                protected boolean equalsForCache(Object arg0) {
                    return false;
                }
                @Override
                protected int hashCodeForCache() {
                    return qualifiedName.hashCode();
                }
            };
            for (IProject project: CeylonBuilder.getProjects()) {
                IJavaProject jp = JavaCore.create(project);
                for (IPackageFragmentRoot pfr: jp.getAllPackageFragmentRoots()) {
                    if (querySpecification.getScope().encloses(pfr)) {
                        for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                            CompilationUnit cu = pu.getCompilationUnit();
                            Set<Node> nodes;
                            if (limitTo==WRITE_ACCESSES) {
                                FindAssignmentsVisitor fav = new FindAssignmentsVisitor(d);
                                fav.visit(cu);
                                nodes = fav.getNodes();
                            }
                            else {
                                FindReferencesVisitor frv = new FindReferencesVisitor(d);
                                frv.visit(cu);
                                nodes = frv.getNodes();
                            }
                            for (Node node: nodes) {
                                if (node.getToken()==null) {
                                    //a synthetic node inserted in the tree
                                }
                                else {
                                    FindContainerVisitor fcv = new FindContainerVisitor(node);
                                    cu.visit(fcv);
                                    Tree.StatementOrArgument c = fcv.getStatementOrArgument();
                                    requestor.reportMatch(new CeylonSearchMatch(node, c, pu.getUnitFile()));
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    protected String getQualifiedName(IMember dec) {
        IJavaElement parent = dec.getParent();
        if (parent instanceof ICompilationUnit) {
            return parent.getParent().getElementName() + "::" + 
                    dec.getElementName();
        }
        else if (dec.getDeclaringType()!=null) {
            return getQualifiedName(dec.getDeclaringType()) + "." + 
                    dec.getElementName();
        }
        else {
            return "@";
        }
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
