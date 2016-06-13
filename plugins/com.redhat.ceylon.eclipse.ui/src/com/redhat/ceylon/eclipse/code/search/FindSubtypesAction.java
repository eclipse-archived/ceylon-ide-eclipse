package com.redhat.ceylon.eclipse.code.search;

import static com.redhat.ceylon.eclipse.ui.CeylonPlugin.PLUGIN_ID;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

import com.redhat.ceylon.model.typechecker.model.Declaration;
import com.redhat.ceylon.model.typechecker.model.Referenceable;
import com.redhat.ceylon.model.typechecker.model.TypeDeclaration;
import com.redhat.ceylon.model.typechecker.model.TypeParameter;
import com.redhat.ceylon.compiler.typechecker.tree.Node;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.ide.common.util.FindSubtypesVisitor;

public class FindSubtypesAction extends AbstractFindAction {

    private static final class Query extends FindSearchQuery {
        private Query(TypeDeclaration referencedDeclaration, 
                IProject project) {
            super(referencedDeclaration, project);
        }

        @Override
        protected Set<Node> getNodes(Tree.CompilationUnit cu,
                Referenceable referencedDeclaration) {
            FindSubtypesVisitor frv = 
                    new FindSubtypesVisitor((TypeDeclaration) referencedDeclaration);
            cu.visit(frv);
            @SuppressWarnings("unchecked")
            Set<Node> nodes = frv.getDeclarationNodeSet();
            return Collections.<Node>unmodifiableSet(nodes);
        }

        @Override
        int limitTo() {
            return IJavaSearchConstants.IMPLEMENTORS;
        }

        @Override
        protected String labelString() {
            return "subtypes of";
        }
    }

    public FindSubtypesAction() {
        super("Find Subtypes");
        setActionDefinitionId(PLUGIN_ID + ".action.findSubtypes");
    }
    
    public FindSubtypesAction(CeylonSearchResultPage page, ISelection selection) {
        super("Find Subtypes", page, selection);
        setActionDefinitionId(PLUGIN_ID + ".action.findSubtypes");
    }
    
    public FindSubtypesAction(IEditorPart editor) {
        super("Find Subtypes", editor);
        setActionDefinitionId(PLUGIN_ID + ".action.findSubtypes");
    }
    
    public FindSubtypesAction(IEditorPart editor, Declaration dec) {
        super("Find Subtypes", editor, dec);
        setActionDefinitionId(PLUGIN_ID + ".action.findSubtypes");
    }
    
    @Override
    boolean isValidSelection() {
        return declaration instanceof TypeDeclaration &&
                declaration.getNameAsString()!=null &&
                !(declaration instanceof TypeParameter);
    }

    @Override
    public FindSearchQuery createSearchQuery() {
        return new Query((TypeDeclaration) declaration, project);
    }
}