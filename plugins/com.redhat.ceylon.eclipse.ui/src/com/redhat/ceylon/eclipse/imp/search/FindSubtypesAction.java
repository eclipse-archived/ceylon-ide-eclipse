package com.redhat.ceylon.eclipse.imp.search;

import java.util.Collections;
import java.util.Set;

import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.compiler.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.eclipse.util.FindSubtypesVisitor;

public class FindSubtypesAction extends AbstractFindAction {

    public FindSubtypesAction(IEditorPart editor) {
		super("Find Subtypes", editor);
		setActionDefinitionId("com.redhat.ceylon.eclipse.ui.action.findSubtypes");
	}
    
    @Override
    boolean isValidSelection() {
        return declaration instanceof TypeDeclaration &&
                !(declaration instanceof TypeParameter);
    }

    @Override
    public FindSearchQuery createSearchQuery() {
        return new FindSearchQuery(declaration, project) {
            @Override
            protected Set<Node> getNodes(PhasedUnit pu) {
                FindSubtypesVisitor frv = new FindSubtypesVisitor((TypeDeclaration) declaration);
                pu.getCompilationUnit().visit(frv);
                Set<Tree.Declaration> nodes = frv.getDeclarationNodes();
                return Collections.<Node>unmodifiableSet(nodes);
            }
            @Override
            protected String labelString() {
                return "subtypes of";
            }
        };
    }
}