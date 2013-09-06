package com.redhat.ceylon.eclipse.code;

import static com.redhat.ceylon.eclipse.code.editor.Util.gotoLocation;
import static com.redhat.ceylon.eclipse.code.parse.CeylonSourcePositionLocator.getIdentifyingNode;

import java.util.List;

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
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.model.DeclarationKind;
import com.redhat.ceylon.compiler.typechecker.model.ProducedReference;
import com.redhat.ceylon.compiler.typechecker.model.ProducedType;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.eclipse.code.outline.CeylonLabelProvider;
import com.redhat.ceylon.eclipse.code.search.CeylonElement;
import com.redhat.ceylon.eclipse.code.search.CeylonSearchMatch;
import com.redhat.ceylon.eclipse.code.search.FindContainerVisitor;
import com.redhat.ceylon.eclipse.code.search.WithProject;
import com.redhat.ceylon.eclipse.core.builder.CeylonBuilder;
import com.redhat.ceylon.eclipse.util.FindReferenceVisitor;

public class JavaQueryParticipant implements IQueryParticipant {

    @Override
    public void search(ISearchRequestor requestor, 
            QuerySpecification querySpecification, 
            IProgressMonitor monitor)
            throws CoreException {
        if (querySpecification instanceof ElementQuerySpecification) {
            final IJavaElement element = ((ElementQuerySpecification) querySpecification).getElement();
            if (!(element instanceof IMember)) return;
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
                public DeclarationKind getDeclarationKind() {
                    return null;
                }
            };
            for (IProject project: CeylonBuilder.getProjects()) {
                IJavaProject jp = JavaCore.create(project);
                for (IPackageFragmentRoot pfr: jp.getAllPackageFragmentRoots()) {
                    if (querySpecification.getScope().encloses(pfr)) {
                        for (PhasedUnit pu: CeylonBuilder.getUnits(project)) {
                            FindReferenceVisitor frv = new FindReferenceVisitor(d);
                            CompilationUnit cu = pu.getCompilationUnit();
                            frv.visit(cu);
                            for (Node node: frv.getNodes()) {
                                FindContainerVisitor fcv = new FindContainerVisitor(node);
                                cu.visit(fcv);
                                if (node.getToken()==null) {
                                    //a synthetic node inserted in the tree
                                }
                                else {
                                    node = getIdentifyingNode(node);
                                }
                                requestor.reportMatch(new CeylonSearchMatch(fcv.getStatementOrArgument(), 
                                        pu.getUnitFile(), 
                                        node.getStartIndex(), 
                                        node.getStopIndex()-node.getStartIndex()+1,
                                        node.getToken()));
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
                gotoLocation(file, offset, length);
            }
            @Override
            public ILabelProvider createLabelProvider() {
                return new CeylonLabelProvider() {
                    Object unwrap(Object element) {
                        if (element instanceof WithProject) {
                            return ((WithProject) element).element;
                        }
                        else {
                            return element;
                        }
                    }
                    @Override
                    public Image getImage(Object element) {
                        return super.getImage(unwrap(element));
                    }
                    @Override
                    public String getText(Object element) {
                        return super.getText(unwrap(element));
                    }
                    @Override
                    public StyledString getStyledText(Object element) {
                        return super.getStyledText(unwrap(element));
                    }
                };
            }
        };
    }

}
