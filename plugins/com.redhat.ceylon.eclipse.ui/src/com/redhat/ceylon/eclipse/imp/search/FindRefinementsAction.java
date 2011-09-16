package com.redhat.ceylon.eclipse.imp.search;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.imp.editor.UniversalEditor;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.Declaration;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.FindRefinementsVisitor;

public class FindRefinementsAction extends AbstractFindAction {

    public FindRefinementsAction(UniversalEditor editor) {
		super("Find Refinements", editor);
		//setAccelerator(SWT.CONTROL | SWT.ALT | 'G');
		setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.findRefinements");
	}
    
    @Override
    boolean isValidSelection(Declaration selectedDeclaration) {
        return selectedDeclaration!=null && 
                selectedDeclaration.isClassOrInterfaceMember();
    }

    @Override
    public FindSearchQuery createSearchQuery(final Declaration declaration, IProject project) {
        return new FindSearchQuery(declaration, project) {
            @Override
            protected Set<Node> getNodes(PhasedUnit pu) {
                FindRefinementsVisitor frv = new FindRefinementsVisitor(declaration);
                pu.getCompilationUnit().visit(frv);
                Set<Tree.Declaration> nodes = frv.getDeclarationNodes();
                return Collections.<Node>unmodifiableSet(nodes);
            }
            @Override
            protected String labelString() {
                return "refinements of";
            }
        };
    }
}