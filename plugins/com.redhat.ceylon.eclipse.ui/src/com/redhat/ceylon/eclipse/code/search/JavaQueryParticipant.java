package com.redhat.ceylon.eclipse.code.search;

import static org.eclipse.jdt.core.search.IJavaSearchConstants.ALL_OCCURRENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.IMPLEMENTORS;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.READ_ACCESSES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.REFERENCES;
import static org.eclipse.jdt.core.search.IJavaSearchConstants.WRITE_ACCESSES;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
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

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.ClassOrInterface;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationKind;
import com.redhat.ceylon.compiler.typechecker.model.MethodOrValue;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.editor.Navigation;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindAssignmentsVisitor;
import com.redhat.ceylon.eclipse.util.FindReferencesVisitor;
import com.redhat.ceylon.eclipse.util.FindSubtypesVisitor;

public class JavaQueryParticipant implements IQueryParticipant {

    //NOTE: this implementation is horrible, using a fake model
    //      object because we currently have no good way of
    //      obtaining the Declaration object from an IJavaElement
    
    private final class FakeTypeDeclaration extends ClassOrInterface {
        private final String qualifiedName;

        private FakeTypeDeclaration(String qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

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
        public ProducedType getReference() {
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

        @Override
        public void addMember(Declaration arg0) {}

        @Override
        public boolean isAbstract() {
            return false;
        }
    }

    private final class FakeTypedDeclaration extends MethodOrValue {
        private final String qualifiedName;

        private FakeTypedDeclaration(String qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

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
        public ProducedType getReference() {
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
                  element instanceof IField)) {
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
            final String qualifiedName = getQualifiedName((IMember) element);
            Declaration d = element instanceof IType ? 
                    new FakeTypeDeclaration(qualifiedName) : 
                        new FakeTypedDeclaration(qualifiedName);
            for (IProject project: CeylonBuilder.getProjects()) {
                IJavaProject jp = JavaCore.create(project);
                for (IPackageFragmentRoot pfr: jp.getAllPackageFragmentRoots()) {
                    if (querySpecification.getScope().encloses(pfr)) {
                        for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                            CompilationUnit cu = pu.getCompilationUnit();
                            Set<? extends Node> nodes;
                            if (limitTo==WRITE_ACCESSES) {
                                FindAssignmentsVisitor fav = new FindAssignmentsVisitor(d);
                                fav.visit(cu);
                                nodes = fav.getNodes();
                            }
                            else if (limitTo==IMPLEMENTORS) {
                                FindSubtypesVisitor fsv = new FindSubtypesVisitor((TypeDeclaration) d);
                                fsv.visit(cu);
                                nodes = fsv.getDeclarationNodes();
                            }
                            else if (limitTo==REFERENCES || 
                                    limitTo==READ_ACCESSES) {  //TODO: support this properly!!
                                FindReferencesVisitor frv = new FindReferencesVisitor(d);
                                frv.visit(cu);
                                nodes = frv.getNodes();
                            }
                            else {
                                //ALL_OCCURRENCES
                                FindReferencesVisitor frv = new FindReferencesVisitor(d);
                                frv.visit(cu);
                                nodes = frv.getNodes();
                                if (d instanceof TypeDeclaration) {
                                    FindSubtypesVisitor fsv = new FindSubtypesVisitor((TypeDeclaration) d);
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
                                    Tree.StatementOrArgument c;
                                    if (node instanceof Tree.Declaration) {
                                        c = (Tree.StatementOrArgument) node;
                                    }
                                    else {
                                        FindContainerVisitor fcv = new FindContainerVisitor(node);
                                        cu.visit(fcv);
                                        c = fcv.getStatementOrArgument();
                                    }
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
        String name = dec.getElementName();
        if (dec instanceof IMethod) {
            if (name.startsWith("$")) {
                name = name.substring(1);
            }
            else if (name.startsWith("get") ||
                     name.startsWith("set")) {
                name = Character.toLowerCase(name.charAt(3)) + 
                        name.substring(4);
            }
        }
        if (parent instanceof ICompilationUnit || 
                parent instanceof IClassFile) {
            return parent.getParent().getElementName() + "::" + 
                    name;
        }
        else if (dec.getDeclaringType()!=null) {
            return getQualifiedName(dec.getDeclaringType()) + "." + 
                    name;
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
